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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.hppcoin.dao.ContractDao;
import org.hppcoin.model.Contract;
import org.hppcoin.model.ContractStatus;
import org.hppcoin.model.ContractType;
import org.hppcoin.model.HPPTransaction;
import org.hppcoin.model.Settings;

public class ContractDaoImpl implements ContractDao {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	@Override
	public Contract save(Contract contract) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hppcoin");
		EntityManager em = emf.createEntityManager();

		try {
			synchronized (Settings.monitor) {
			em.getTransaction().begin();
			System.out.println("ContractDaoImpl -> save getSetupPrice = "+contract.getSetupPrice());
			em.persist(contract);
			em.getTransaction().commit();
			em.close();
			emf.close();
			}
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
		}

		return contract;
	}

	@Override
	public Contract findByAddress(String address) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hppcoin");
		EntityManager em = emf.createEntityManager();
		Contract contract = null;
		try {
			synchronized (Settings.monitor) {
			Query query = em.createNamedQuery("Contract.selectByAddress");

			em.getTransaction().begin();
			query.setParameter("address1", address);
			contract = (Contract) query.getSingleResult();
			em.getTransaction().commit();
			em.close();
			emf.close();
			}
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
		}
		return contract;
	}

	public List<Contract> selectByContractStaus(ContractStatus status) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hppcoin");
		EntityManager em = emf.createEntityManager();
		List<Contract> contracts = null;
		try {
			synchronized (Settings.monitor) {
			Query query = em.createNamedQuery("Contract.selectByContractStatus");
			query.setParameter("status1", status);
			em.getTransaction().begin();
			contracts = query.getResultList();
			em.getTransaction().commit();
			em.close();
			emf.close();
			}
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
		}
		return contracts;
	}

	public List<Contract> selectAll() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hppcoin");
		EntityManager em = emf.createEntityManager();
		List<Contract> contracts = null;
		try {
			synchronized (Settings.monitor) {
			Query query = em.createNamedQuery("Contract.selectAll");
			em.getTransaction().begin();
			contracts = query.getResultList();
			em.getTransaction().commit();
			em.close();
			emf.close();
			} 
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.severe(e.getMessage());
		}
		return contracts;
	}

	@Override
	public void update(Contract contract, HPPTransaction transaction) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hppcoin");
		EntityManager em = emf.createEntityManager();
		try {
			synchronized (Settings.monitor) {
			em.getTransaction().begin();
			contract = em.find(Contract.class, contract.getId());
			List<HPPTransaction> transactions = contract.getTransactions();
			if (transactions == null)
				transactions = new ArrayList<>();
			// prevent duplicate Transactions
			if (!transactions.contains(transaction))
				transactions.add(transaction);
			contract.setTransactions(transactions);
			em.getTransaction().commit();
			em.close();
			emf.close();
			} 
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
		}
	}

	public int countActive() {
		List<Contract> contracts = selectByContractStaus(ContractStatus.ACTIVE);
		if (contracts != null)
			return contracts.size();
		return 0;
	}

	public List<Contract> selectByContractType(ContractType type) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hppcoin");
		EntityManager em = emf.createEntityManager();
		List<Contract> contracts = null;
		try {
			synchronized (Settings.monitor) {
			Query query = em.createNamedQuery("Contract.selectByContractType");
			query.setParameter("type1", type);
			em.getTransaction().begin();
			contracts = query.getResultList();
			em.getTransaction().commit();
			em.close();
			emf.close();
			} 
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
		}
		return contracts;
	}

	public Contract find(String currentContractID) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hppcoin");
		EntityManager em = emf.createEntityManager();
		Contract contract = null;
		try {
			synchronized (Settings.monitor) {
			em.getTransaction().begin();
			contract = em.find(Contract.class, currentContractID);
			em.getTransaction().commit();
			em.close();
			emf.close();
			}
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
		}

		return contract;
	}

	public int update(Contract contract) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hppcoin");
		EntityManager em = emf.createEntityManager();
		try {
			synchronized (Settings.monitor) {
			em.getTransaction().begin();
			em.merge(contract);
			em.getTransaction().commit();
			em.close();
			emf.close();
			} 
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
			;
			return 0;
		}

		return 1;

	}

	public List<Contract> selectByContractTypeAndStatus(ContractType type, ContractStatus status) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hppcoin");
		EntityManager em = emf.createEntityManager();
		List<Contract> contracts = null;
		try {
			synchronized (Settings.monitor) {
			Query query = em.createNamedQuery("Contract.selectByContractTypeAndStatus");
			query.setParameter("type1", type);
			query.setParameter("status1", status);
			em.getTransaction().begin();
			contracts = query.getResultList();
			em.getTransaction().commit();
			em.close();
			emf.close();	} 
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
		}
		return contracts;
	}

}
