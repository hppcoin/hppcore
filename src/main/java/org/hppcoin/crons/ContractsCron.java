package org.hppcoin.crons;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.hppcoin.dao.ContractDao;
import org.hppcoin.dao.TransactionDao;
import org.hppcoin.dao.impl.ContractDaoImpl;
import org.hppcoin.dao.impl.TransactionDaoImpl;
import org.hppcoin.model.Contract;
import org.hppcoin.model.ContractStatus;
import org.hppcoin.model.ContractType;
import org.hppcoin.model.TransactionType;

public class ContractsCron {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	//select all sold vps contracts and update payments 
	public static void updateSoldeVPSPayements() {
		new Thread() {
			public void run() {
				while (true) {
					try {
						// Enhance Performance : select just Active Contracts
						List<Contract> contractList = new ContractDaoImpl().selectByContractTypeAndStatus(ContractType.SELL,ContractStatus.ACTIVE);
						List<org.hppcoin.model.HPPTransaction> transactions = new WalletListener(false)
								.listRecentTransactions();
						ContractDao contractDao = new ContractDaoImpl();
						TransactionDao txDao = new TransactionDaoImpl();
						if (contractList != null && contractList.size() > 0)
							for (Contract contract : contractList) {
								String address = contract.getRecievingAddress();
								for (org.hppcoin.model.HPPTransaction tx : transactions) {

									if (tx.getAddress().equals(address)&&tx.getType().equals(TransactionType.RECEIVE)) {
										if (!txDao.exit(tx.getTxid())) {
											tx.setContract(contract);
											txDao.save(tx);
										}

										tx.setContract(contract);
										contractDao.update(contract, tx);

									}

								}
							}
						try {
							TimeUnit.SECONDS.sleep(30);
						} catch (InterruptedException e) {
							LOGGER.severe(e.getMessage());
							LOGGER.severe(e.getMessage());
						}
					} catch (Exception e) {
						LOGGER.severe(e.getMessage());
						LOGGER.severe(e.getMessage());
					}
				}
			}
		}.start();
	}
	
	
}
