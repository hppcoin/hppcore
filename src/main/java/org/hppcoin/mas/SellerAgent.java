package org.hppcoin.mas;

import java.util.List;
import java.util.logging.Logger;

import org.hppcoin.dao.impl.VPSDaoImpl;
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

public class SellerAgent extends Agent {
	/**
	 * A SellerAgent manage all available Hosts. Tasks : Broadcast info about Hosts
	 * in json format
	 */
	private static final long serialVersionUID = 1L;
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	@Override
	protected void setup() {
		LOGGER.info("SellerAgent.setup()");
		DFAgentDescription template = new DFAgentDescription();
		template.setName(getAID());
		ServiceDescription agentDescriptor = new ServiceDescription();
		agentDescriptor.setType("SellerAgent");
		agentDescriptor.setName(getLocalName() + "-SellerAgent");
		template.addServices(agentDescriptor);
		try {
			DFService.register(this, template);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
		/**
		 * Broadcast new resources
		 */
		Behaviour broadcastResources = new CyclicBehaviour() {

			private static final long serialVersionUID = 1L;

			@Override
			public void action() {
				List<VPS> hosts = new VPSDaoImpl().selectMine();
				if (null != hosts && hosts.size() > 0) {
					boolean isOneAvailable = false;
					for (VPS vps : hosts)
						if (vps.getRentalSattus().equals(VPSRentalStatus.AVAILABLE)) {
							isOneAvailable = true;
							break; 
						}
					if (isOneAvailable)
						try {
							// update hosts Seller Agent ID
							for (VPS hos : hosts)
								hos.setReceiveRequestAgentGID(Settings.ReceiveRequestAgentGID);

							String content = "";
							for (VPS vps : hosts)
								if (vps.getRentalSattus().equals(VPSRentalStatus.AVAILABLE))
									content += Settings.ReceiveRequestAgentGID + " " + vps.getUuid() + " "
											+ vps.getCpu().getCores() + " " + vps.getMemory().getMemorySize() + " "
											+ vps.getStorage().getStorageSize() + " " + vps.getCostPerMinute() + " "
											+ vps.getSetupPrice() + " " + vps.getPayDelay() + " "
											+ vps.getPayementInterval() + " " + vps.getIp() + " "
											+ vps.getRecievingAddress() + ";";
							ACLMessage message = new ACLMessage(ACLMessage.INFORM);
							message.setContent(content);
							DFAgentDescription template = new DFAgentDescription();
							ServiceDescription sd = new ServiceDescription();
							sd.setType("BuyerAgent");
							template.addServices(sd);

							DFAgentDescription[] dfads = DFService.search(myAgent, template);
							if (dfads != null && dfads.length > 0) {
								for (DFAgentDescription dfAgentDesc : dfads) {
									message.addReceiver(dfAgentDesc.getName());
								}
								// do not send to yourself !
								message.removeReceiver(new AID(Settings.buyerAgentGID));
							}
							send(message);
						} catch (Exception fe) {
							fe.printStackTrace();
							LOGGER.severe(fe.getMessage());
						}
					
					// block for 1.5 min
					try {
						Thread.sleep(90000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				// No VPS available block for 3 min
				else try {
					Thread.sleep(180000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		};

		addBehaviour(broadcastResources);
	}
}
