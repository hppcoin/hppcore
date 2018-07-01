package org.hppcoin.mas;

import java.util.logging.Logger;

import org.hppcoin.dao.VPSDao;
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
import jade.lang.acl.MessageTemplate;

public class ReceiveRequestAgent extends Agent {

	private static final long serialVersionUID = 1L;
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	@Override
	protected void setup() {
		DFAgentDescription template = new DFAgentDescription();
		template.setName(getAID());
		ServiceDescription agentDescriptor = new ServiceDescription();
		agentDescriptor.setType("ReceiveRequestAgent");
		agentDescriptor.setName(getLocalName() + "ReceiveRequestAgent");
		Settings.ReceiveRequestAgentGID = getAID().getName();
		template.addServices(agentDescriptor);
		try {
			DFService.register(this, template);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

		final long timer = System.currentTimeMillis();

		/**
		 * Receive and handle Contract Request Messages
		 */
		Behaviour handleContractRequestMessages = new CyclicBehaviour() {

			private static final long serialVersionUID = 1L;

			@Override
			public void action() {
				VPSDao vpsDao = new VPSDaoImpl();
				ACLMessage message = receive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
				if (message != null) {
					String uuid = null;
					String contractId = null;
					String replyTo = null;
					ACLMessage response = new ACLMessage(ACLMessage.INFORM);
					String requestContent = message.getContent();
					if (requestContent != null) {
						try {
							String[] parts = requestContent.split(";");
							uuid = parts[0];
							contractId = parts[1];
							replyTo = parts[2];
						} catch (Exception e) {
							LOGGER.severe(e.getMessage());
							e.printStackTrace();
						}
						VPS vps = vpsDao.find(uuid);
						if (vps != null && vps.getRentalSattus().equals(VPSRentalStatus.AVAILABLE)) {
							vps.setRentalSattus(VPSRentalStatus.RESERVED);
							vpsDao.update(vps);
							response.setContent("OK;" + uuid + ";" + contractId + ";" + Settings.SetupFeesVerificationGID);
						} else {
							response.setContent("KO;" + uuid + ";" + contractId);
						}
					}
					response.addReceiver(new AID(replyTo));
					send(response);
				}
				else block();
			}
		};

		addBehaviour(handleContractRequestMessages);
	}

}
