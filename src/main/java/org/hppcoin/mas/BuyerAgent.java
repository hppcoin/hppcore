package org.hppcoin.mas;

import java.util.List;
import java.util.logging.Logger;

import org.hppcoin.dao.impl.VPSDaoImpl;
import org.hppcoin.model.Settings;
import org.hppcoin.model.VPS;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class BuyerAgent extends Agent {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void setup() {
		DFAgentDescription template = new DFAgentDescription();
		template.setName(getAID());
		ServiceDescription agentDescriptor = new ServiceDescription();
		agentDescriptor.setType("BuyerAgent");
		agentDescriptor.setName(getLocalName() + "BuyerAgent");
		Settings.buyerAgentGID=getAID().getName();
		template.addServices(agentDescriptor);
		try {
			DFService.register(this, template);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
       final long timer=System.currentTimeMillis();
		Behaviour updateResourcesBehaviour = new CyclicBehaviour() {

			@Override
			public void action() {
				MessageTemplate temp = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
				ACLMessage aclMeessage = receive(temp);
				if (aclMeessage != null) {
					String hostsJson = aclMeessage.getContent();
					List<VPS> vpsList = Parser.fromJson(hostsJson);
					if (vpsList != null && vpsList.size() > 0) {
						new VPSDaoImpl().updateVPS(vpsList);
					}
				}
				//block waiting for new resources
				block();
			}
		};
		addBehaviour(updateResourcesBehaviour);

	}
}
