package org.hppcoin.mas;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.hppcoin.crons.WalletListener;
import org.hppcoin.dao.VPSDao;
import org.hppcoin.dao.impl.VPSDaoImpl;
import org.hppcoin.model.Contract;
import org.hppcoin.model.ContractType;
import org.hppcoin.model.HPPTransaction;
import org.hppcoin.model.Settings;
import org.hppcoin.model.VPS;
import org.hppcoin.model.VPSRentalStatus;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class SetupFeesVerificationAgent extends Agent {
	private static final long serialVersionUID = 1L;
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	@Override
	protected void setup() {
		DFAgentDescription template = new DFAgentDescription();
		template.setName(getAID());
		ServiceDescription agentDescriptor = new ServiceDescription();
		agentDescriptor.setType("SetupFeesVerificationAgent");
		agentDescriptor.setName(getLocalName() + "SetupFeesVerificationAgent");
		Settings.SetupFeesVerificationGID=getAID().getName();
		template.addServices(agentDescriptor);
		try {
			DFService.register(this, template);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
       final long timer=System.currentTimeMillis();
		/**
		 * handle Contract Request Messages
		 */
		Behaviour handleCredentialsRequestMessages = new CyclicBehaviour() {

			private static final long serialVersionUID = 1L;

			@Override
			public void action() {
				System.out.println("SetupFeesVerificationAgent started and will block for incoming Credentials Requests !");
				VPSDao vpsDao = new VPSDaoImpl();
				ACLMessage message = receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
				if (message != null) {
					ACLMessage response = new ACLMessage(ACLMessage.INFORM);
					String requestContent = message.getContent();
					if (requestContent != null) {
						String txid = null;
						String uuid = null;
						String contractId = null;
						String replyTo = null;
						int durationHours=24*31*12;
						//block 15 seconds for transaction propagation
						try {
							Thread.sleep(15000);
						}catch (Exception e) {
							e.printStackTrace();
						}
						HPPTransaction setupFeesTx = null;
						try {
							String parts[] = requestContent.split(";");
							txid = parts[0];
							uuid = parts[1];
							contractId = parts[2];
							replyTo = parts[3];

							List<org.hppcoin.model.HPPTransaction> transactions = new WalletListener(false)
									.listRecentTransactions();
							if (transactions != null && transactions.size() > 0)
								for (HPPTransaction trx : transactions)
									if (trx.getTxid().equals(txid)) {
										setupFeesTx = trx;
										break;
									}
						durationHours=Integer.parseInt(parts[4]);
						} catch (Exception e) {
							LOGGER.severe(e.getMessage());
							e.printStackTrace();
						}
						VPS vps = vpsDao.find(uuid);
						if (setupFeesTx != null && vps != null && setupFeesTx.getAmount() >= vps.getSetupPrice()
								&& vps.getRentalSattus().equals(VPSRentalStatus.RESERVED)) {
							vps.setRentalSattus(VPSRentalStatus.RENTED);
							try {
							vps.setRecievingAddress(new WalletListener(true).getNewAddress());
							} catch (Exception e) {
								LOGGER.severe(e.getMessage());
								e.printStackTrace();
							}
							vpsDao.update(vps);
							Contract contract=new Contract(true, ContractType.SELL);
							contract.setVps(vps);
							contract.setStartDate(new Date().getTime());
							contract.setDurationHours(durationHours);
							
							//password may contain ;
							response.setContent(vps.getIp() + ";" + vps.getSshPort() + ";" + vps.getUser() + ";" + uuid
									+ ";" + contractId + ";" + vps.getPassword());
							response.addReceiver(new AID(replyTo));
							send(response);
						}
					}

				}
				else block();
			}
		};

		addBehaviour(handleCredentialsRequestMessages);
	}

}
