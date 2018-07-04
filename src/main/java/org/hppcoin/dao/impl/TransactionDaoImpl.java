/*
 * HPPCOIN License
 * 
 * Copyright (c) 2017-2018, HPPCOIN Developers.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the 
 * Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject
 * to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR
 * ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.hppcoin.dao.impl;

import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.hppcoin.dao.TransactionDao;
import org.hppcoin.model.Balance;
import org.hppcoin.model.Contract;
import org.hppcoin.model.HPPTransaction;
import org.hppcoin.model.Settings;
import org.hppcoin.model.TransactionType;

public class TransactionDaoImpl implements TransactionDao {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	@Override
	public boolean exit(String txid) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hppcoin");
		EntityManager em = emf.createEntityManager();
		HPPTransaction tx = null;
		try {
			synchronized (Settings.monitor) {
			em.getTransaction().begin();
			tx = em.find(HPPTransaction.class, txid);
			em.getTransaction().commit();
			em.close();
			emf.close();
			} 
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());

		}
		return (tx != null);
	}

	@Override
	public HPPTransaction save(HPPTransaction transaction) {
		int confirmations=transaction.getConfirmations();
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hppcoin");
		EntityManager em = emf.createEntityManager();
		boolean isContracted=false;
		Contract contract=null;
		try {
			synchronized (Settings.monitor) {
			em.getTransaction().begin();
			contract=new ContractDaoImpl().findByAddress(transaction.getAddress());
				
				if(null!=contract)  {	 
					em.merge(contract);
					transaction.setContract(contract);
                    isContracted=true;
				 }
			 
			if(exit(transaction.getTxid()))
			{
			transaction=em.find(HPPTransaction.class, transaction.getTxid());
			transaction.setConfirmations(confirmations);
			} 
			em.merge(transaction);
			em.getTransaction().commit();
			em.close();
			emf.close();
			if(isContracted) new ContractDaoImpl().update(contract, transaction);
			} 
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());

		}
		return transaction;
	}

	@Override
	public List<HPPTransaction> selectAll() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hppcoin");
		EntityManager em = emf.createEntityManager();
		List<HPPTransaction> transactions = null;
		try {
			Query query = em.createNamedQuery("HPPTransaction.selectAll");
			synchronized (Settings.monitor) {
			em.getTransaction().begin();
			transactions = query.getResultList();
			em.getTransaction().commit();
			em.close();
			emf.close();
			} 
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
		}
		return transactions;
	}

	@Override
	public int update(List<HPPTransaction> txs) {
		int updateCount = 0;
		if (txs != null && txs.size() > 0)
			for (HPPTransaction tx : txs)
		 {
					save(tx);
					updateCount++;
				}
		return updateCount;
	}

	@Override
	public void updateBalance(double available, double pending) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hppcoin");
		EntityManager em = emf.createEntityManager();
		Balance balance = null;
		try {
			synchronized (Settings.monitor) {
			em.getTransaction().begin();
			balance = em.find(Balance.class, new Long(1L));
			balance.setAvailable(available);
			balance.setPending(pending);
			em.getTransaction().commit();
			em.close();
			emf.close();
			}
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());

		}

	}

	@Override
	public Balance loadBalance() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hppcoin");
		EntityManager em = emf.createEntityManager();
		Balance balance = null;
		try {
			synchronized (Settings.monitor) {
			em.getTransaction().begin();
			balance = em.find(Balance.class, new Long(1L));
			em.getTransaction().commit();
			em.close();
			emf.close();
			}
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
		}
		return balance;
	}

	@Override
	public Balance saveBalance(Balance balance) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hppcoin");
		EntityManager em = emf.createEntityManager();
		try {
			synchronized (Settings.monitor) {
			em.getTransaction().begin();
			em.persist(balance);
			em.getTransaction().commit();
			em.close();
			emf.close();
			} 
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());

		}
		return balance;
	}

	@Override
	public double getReceivedAmount(String recievingAddress) {
		double amount = 0;
		List<HPPTransaction> txs = selectAll();
		if (txs == null || txs.size() < 1)
			return 0;
		for (HPPTransaction tx : txs)
			if (tx.getAddress().equals(recievingAddress) && tx.getType().equals(TransactionType.RECEIVE))
				amount += tx.getAmount();
		return amount;
	}

	public void updateAll(Contract contract) {
		List<HPPTransaction> txs = selectAll();
		if (txs == null || txs.size() < 1)
			return ;
		for (HPPTransaction tx : txs)
			if (tx.getAddress().equals(contract.getRecievingAddress()))
				try {
					EntityManagerFactory emf = Persistence.createEntityManagerFactory("hppcoin");
					EntityManager em = emf.createEntityManager();
					synchronized (Settings.monitor) {
					em.getTransaction().begin();
					em.merge(tx);
					contract=em.find(Contract.class, contract.getId());
					tx.setContract(contract);
					em.getTransaction().commit();
					em.close();
					emf.close();
					new ContractDaoImpl().update(contract, tx);
					} 
				} catch (Exception e) {
					e.printStackTrace();
					LOGGER.severe(e.getMessage());

				}
		
	}

}
