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
package org.hppcoin.crons;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Base64;
import org.apache.mina.filter.buffer.BufferedWriteFilter;
import org.apache.xmlrpc.XmlRpcException;
import org.hppcoin.dao.ContractDao;
import org.hppcoin.dao.TransactionDao;
import org.hppcoin.dao.impl.ContractDaoImpl;
import org.hppcoin.dao.impl.LMNodeDaoImpl;
import org.hppcoin.dao.impl.TransactionDaoImpl;
import org.hppcoin.model.Contract;
import org.hppcoin.model.ContractStatus;
import org.hppcoin.model.ContractType;
import org.hppcoin.model.HPPTransaction;
import org.hppcoin.model.LMNode;
import org.hppcoin.model.TransactionType;
import org.hppcoin.service.XenServerService;
import org.hppcoin.util.OsCheck.OSType;

import com.xensource.xenapi.Types.XenAPIException;

/**
 * @author E. Ramalin
 * @Copyright 2018 HPPCOIN Team Licence GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 */
public class WalletCron {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	public static void updateTransactionsAndBalance() {
		new Thread() {
			public void run() {
				while (true) {
					try {
						double available = new WalletListener(false).getBalance();
						double pending = new WalletListener(false).getUnconfirmedBalance();
						new TransactionDaoImpl().updateBalance(available, pending);
						List<org.hppcoin.model.HPPTransaction> transactions = new WalletListener(false)
								.listRecentTransactions();
						new TransactionDaoImpl().update(transactions);
					} catch (Exception e) {
						LOGGER.severe(e.getMessage());
						LOGGER.severe(e.getMessage());
					}
					try {
						TimeUnit.SECONDS.sleep(5);
					} catch (InterruptedException e) {
						LOGGER.severe(e.getMessage());
					}
				}
			}
		}.start();

	}

	public static void updateLMNList() {
		new Thread() {
			public void run() {
				while (true) {
					try {
						List<LMNode> nodes = new WalletListener(false).LMNodes();
						new LMNodeDaoImpl().update(nodes);
						LOGGER.info("LMNode List updated with " + nodes.size() + " nodes");
					} catch (Exception e) {
						LOGGER.severe(e.getMessage());
						LOGGER.severe(e.getMessage());
					}
					try {
						TimeUnit.MINUTES.sleep(30);
					} catch (InterruptedException e) {
						LOGGER.severe(e.getMessage());
					}
				}
			}
		}.start();

	}

	public static void payContract(Contract contract) {
		new Thread() {
			public void run() {
				ContractDao contractDao = new ContractDaoImpl();
				Contract contractUpdated = null;
				int cycles = contract.getDurationHours() / contract.getPayementInterval();
				double amountPerCycle = contract.getCostPerMinute() * contract.getPayementInterval() * 60;
				LOGGER.info("Start paying contract " + contract.getId());
				for (int i = 0; i < cycles; i++) {
					contractUpdated = contractDao.find(contract.getId());
					if (contractUpdated.getContractStatus() == ContractStatus.ACTIVE) {
						try {
							String txid = new WalletListener(false).sendToAddress(contract.getRecievingAddress(),
									amountPerCycle);
							LOGGER.info("TX sent : " + txid);
							org.hppcoin.model.HPPTransaction newTransaction = new org.hppcoin.model.HPPTransaction(
									txid);
							newTransaction.setAddress(contract.getRecievingAddress());
							newTransaction.setAmount(amountPerCycle);
							newTransaction.setTime(new Date().getTime());
							newTransaction.setType(TransactionType.SEND);
							newTransaction.setContract(contract);
							newTransaction = new TransactionDaoImpl().save(newTransaction);
							new ContractDaoImpl().update(contract, newTransaction);
						} catch (Exception e) {
							LOGGER.severe(e.getMessage());
						}
					}

					try {
						TimeUnit.HOURS.sleep(contract.getPayementInterval());
					} catch (InterruptedException e) {
						LOGGER.severe(e.getMessage());
					}
				}
				// Ensure the user is paid to the last minute.
				if ((contract.getDurationHours()) % contract.getPayementInterval() != 0) {
					int remainingImpaidHours = (contract.getDurationHours()) % contract.getPayementInterval();
					double remainingAmount = contract.getCostPerMinute() * remainingImpaidHours * 60;
					if (contract.getContractStatus() == ContractStatus.ACTIVE) {
						try {
							String txid = new WalletListener(false).sendToAddress(contract.getRecievingAddress(),
									remainingAmount);
							org.hppcoin.model.HPPTransaction newTransaction = new org.hppcoin.model.HPPTransaction(
									txid);
							newTransaction.setAddress(contract.getRecievingAddress());
							newTransaction.setAmount(remainingAmount);
							newTransaction.setTime(new Date().getTime());
							newTransaction.setType(TransactionType.SEND);
							newTransaction.setContract(contract);
							newTransaction = new TransactionDaoImpl().save(newTransaction);
							new ContractDaoImpl().update(contract, newTransaction);
						} catch (Exception e) {
							LOGGER.severe(e.getMessage());
						}
					}
				}
				contractUpdated = contractDao.find(contract.getId());
				if (contractUpdated.getContractStatus() == ContractStatus.ACTIVE) {
					contract.setContractStatus(ContractStatus.COMPLETE);
					contractDao.update(contract);
				}
			}
		}.start();

	}

	public static void suspendAllImpaidContracts() {
		new Thread() {
			public void run() {
				while (true) {
					ContractDao contractDao = new ContractDaoImpl();
					List<Contract> sellContracts = contractDao.selectByContractType(ContractType.SELL);
					if (sellContracts != null && sellContracts.size() > 0)
						for (Contract contract : sellContracts)
							if (contract.getContractStatus() != null
									&& contract.getContractStatus().equals(ContractStatus.ACTIVE))
								inspect(contract);
					try {
						Thread.sleep(60L * 60L * 1000L);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	private static void inspect(Contract contract) {
		int delayedPayments = 0;
		double supposedPaidAmount = 0;
		double reelPaidAmount = 0;
		double delayedAmount = 0;
		ContractDao contractDao = new ContractDaoImpl();
		TransactionDaoImpl txDao = new TransactionDaoImpl();
		List<HPPTransaction> transactions = txDao.selectAll();
		int pastCycles = (int) ((new Date().getTime() - contract.getStartDate())
				/ (contract.getPayementInterval() * 60L * 60L * 1000L));
		if (transactions != null && transactions.size() > 0)
			for (HPPTransaction trx : transactions)
				if (trx.getAddress().equals(contract.getRecievingAddress())
						&& trx.getType().equals(TransactionType.RECEIVE)
						&& trx.getTime() > (contract.getStartDate() - 20L * 60L * 1000L))
					reelPaidAmount += trx.getAmount();
		supposedPaidAmount = pastCycles * contract.getCostPerMinute() * 60 * contract.getPayementInterval()
				+ contract.getSetupPrice();
		delayedAmount = supposedPaidAmount - reelPaidAmount;
		delayedPayments = (int) (delayedAmount / (contract.getCostPerMinute() * 60 * contract.getPayementInterval()));
		if (delayedPayments > contract.getPayDelay()) {
			contract.setContractStatus(ContractStatus.SUSPENDED);
			contractDao.update(contract);
			try {
				XenServerService service = new XenServerService(contract.getVps().getXenServer().getIp(),
						contract.getVps().getXenServer().getUsername(), contract.getVps().getXenServer().getPassword());
				service.shutDownVM(contract.getVps().getUuid());
				LOGGER.severe(contract.getId() + " Suspended ! at " + new Date());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void continuePayingActiveContracts() {
		ContractDao contractDao = new ContractDaoImpl();
		List<Contract> buyContracts = contractDao.selectByContractType(ContractType.BUY);
		if (buyContracts != null && buyContracts.size() > 0)
			for (Contract contract : buyContracts)
				if (contract.getContractStatus() != null && contract.getContractStatus().equals(ContractStatus.ACTIVE))
					continuePayingContract(contract);

	}

	private static void continuePayingContract(Contract contract) {
		new Thread() {
			public void run() {
				int delayedPayments = 0;
				double supposedPaidAmount = 0;
				double reelPaidAmount = 0;
				int remainingCycles = 0;
				double delayedAmount = 0;
				long timeToToNextPayment = 0;
				ContractDao contractDao = new ContractDaoImpl();
				TransactionDaoImpl txDao = new TransactionDaoImpl();
				List<HPPTransaction> transactions = txDao.selectAll();
				int pastCycles = (int) ((new Date().getTime() - contract.getStartDate())
						/ (contract.getPayementInterval() * 60L * 60L * 1000L));
				if (transactions != null && transactions.size() > 0)
					for (HPPTransaction trx : transactions)
						if (trx.getAddress().equals(contract.getRecievingAddress())
								&& trx.getType().equals(TransactionType.SEND)
								&& trx.getTime() > (contract.getStartDate() - 20L * 60L * 1000L))
							reelPaidAmount += trx.getAmount();
				supposedPaidAmount = pastCycles * contract.getCostPerMinute() * 60 * contract.getPayementInterval()
						+ contract.getSetupPrice();
				delayedAmount = supposedPaidAmount - reelPaidAmount;
				delayedPayments = (int) (delayedAmount
						/ (contract.getCostPerMinute() * 60 * contract.getPayementInterval()));
				if (delayedPayments > contract.getPayDelay()) {
					contract.setContractStatus(ContractStatus.SUSPENDED);
					contractDao.update(contract);
					return;
				}
				if (delayedPayments > 0) {
					try {
						String txid = new WalletListener(false).sendToAddress(contract.getRecievingAddress(),
								delayedAmount);
						LOGGER.info("TX sent : " + txid);
						org.hppcoin.model.HPPTransaction newTransaction = new org.hppcoin.model.HPPTransaction(txid);
						newTransaction.setAddress(contract.getRecievingAddress());
						newTransaction.setAmount(delayedAmount);
						newTransaction.setTime(new Date().getTime());
						newTransaction.setType(TransactionType.SEND);
						newTransaction.setContract(contract);
						newTransaction = new TransactionDaoImpl().save(newTransaction);
						new ContractDaoImpl().update(contract, newTransaction);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

				remainingCycles = contract.getDurationHours() / contract.getPayementInterval() - pastCycles;

				timeToToNextPayment = ((pastCycles + 1) * (contract.getPayementInterval() * 60L * 60L * 1000L))
						- new Date().getTime();
				if (timeToToNextPayment > 0)
					try {
						Thread.sleep(timeToToNextPayment);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				Contract contractUpdated = null;
				double amountPerCycle = contract.getCostPerMinute() * contract.getPayementInterval() * 60;
				LOGGER.info("Start paying contract " + contract.getId());
				for (int i = 0; i < remainingCycles; i++) {
					contractUpdated = contractDao.find(contract.getId());
					if (contractUpdated.getContractStatus() == ContractStatus.ACTIVE) {
						try {
							String txid = new WalletListener(false).sendToAddress(contract.getRecievingAddress(),
									amountPerCycle);
							LOGGER.info("TX sent : " + txid);
							org.hppcoin.model.HPPTransaction newTransaction = new org.hppcoin.model.HPPTransaction(
									txid);
							newTransaction.setAddress(contract.getRecievingAddress());
							newTransaction.setAmount(amountPerCycle);
							newTransaction.setTime(new Date().getTime());
							newTransaction.setType(TransactionType.SEND);
							newTransaction.setContract(contract);
							newTransaction = new TransactionDaoImpl().save(newTransaction);
							new ContractDaoImpl().update(contract, newTransaction);
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}

					try {
						TimeUnit.HOURS.sleep(contract.getPayementInterval());
					} catch (InterruptedException e) {
						LOGGER.severe(e.getMessage());
					}
				}
				// Ensure the user is paid to the last minute.
				if ((contract.getDurationHours()) % contract.getPayementInterval() != 0) {
					int remainingImpaidHours = (contract.getDurationHours()) % contract.getPayementInterval();
					double remaininder = contract.getCostPerMinute() * remainingImpaidHours * 60;
					if (contract.getContractStatus() == ContractStatus.ACTIVE) {
						try {
							String txid = new WalletListener(false).sendToAddress(contract.getRecievingAddress(),
									remaininder);
							org.hppcoin.model.HPPTransaction newTransaction = new org.hppcoin.model.HPPTransaction(
									txid);
							newTransaction.setAddress(contract.getRecievingAddress());
							newTransaction.setAmount(remaininder);
							newTransaction.setTime(new Date().getTime());
							newTransaction.setType(TransactionType.SEND);
							newTransaction.setContract(contract);
							newTransaction = new TransactionDaoImpl().save(newTransaction);
							new ContractDaoImpl().update(contract, newTransaction);
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
				contractUpdated = contractDao.find(contract.getId());
				if (contractUpdated.getContractStatus() == ContractStatus.ACTIVE) {
					contract.setContractStatus(ContractStatus.COMPLETE);
					contractDao.update(contract);
				}
			}
		}.start();

	}

	public static void createConfigFileIfNotExist(OSType ostype) {
		try {
			File f = null;
			File home = new File(System.getProperty("user.home"));
			boolean exist = false;
			switch (ostype) {
			case Windows:
				if ((f = new File(home, "AppData" + File.separatorChar + "Roaming" + File.separatorChar + "Hppcoin"
						+ File.separatorChar + "hppcoin.conf")).exists()) {
					System.out.println("Windows");
				}
				break;
			case MacOS:
				if ((f = new File(home, "Library/Application Support/hppcoin" + File.separatorChar + "hppcoin.conf"))
						.exists()) {
					exist = true;
					System.out.println("MacOsx");
				}
				break;
			case Linux:
				if ((f = new File(home, ".hppcoin" + File.separatorChar + "hppcoin.conf")).exists()) {
					System.out.println("Linux");
					exist = true;
				}
				break;
			case Other:
				break;
			}
			if (!exist) {
				BufferedWriter fw = new BufferedWriter(new FileWriter(f));
				long userLong = new Random().nextLong();
				Base64 binaryBase64 = new Base64();

				String userPart1 = new String(binaryBase64.encodeBase64(new BigInteger(String.valueOf(userLong), 10).toByteArray()));
				String userPart2 = new String(binaryBase64.encodeBase64(new BigInteger(String.valueOf(userLong), 10).toByteArray()));
				long passLong = new Random().nextLong();
				String passPart1 =  new String(binaryBase64.encodeBase64(new BigInteger(String.valueOf(passLong), 10).toByteArray()));
				String passPart2 =  new String(binaryBase64.encodeBase64(new BigInteger(String.valueOf(passLong), 10).toByteArray()));
				fw.write("rpcuser=" + userPart1+userPart2);
				fw.newLine();
				fw.write("rpcpassword=" + passPart1+passPart2);
				fw.flush();
				fw.close();
				System.out.println("hppcoin.conf Created !");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
