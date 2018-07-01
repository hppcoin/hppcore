package org.hppcoin.mas;

import java.util.List;
import java.util.logging.Logger;

import org.hppcoin.crons.WalletListener;
import org.hppcoin.dao.ContractDao;
import org.hppcoin.dao.impl.ContractDaoImpl;
import org.hppcoin.dao.impl.VPSDaoImpl;
import org.hppcoin.model.Contract;
import org.hppcoin.model.ContractStatus;
import org.hppcoin.model.ContractType;
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

public class SendContractRequestAgent extends Agent {
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
		agentDescriptor.setType("SendContractRequestAgent");
		agentDescriptor.setName(getLocalName() + "SendContractRequestAgent");
		template.addServices(agentDescriptor);
		try {
			DFService.register(this, template);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

		final long timer = System.currentTimeMillis();

		/**
		 * Send new Contract Requests
		 */
		Behaviour sendContractRequestBehaviour = new CyclicBehaviour() {

			@Override
			public void action() {
				ContractDao contractDao = new ContractDaoImpl();
				List<Contract> pendingContracts = contractDao.selectByContractTypeAndStatus(ContractType.BUY,
						ContractStatus.PENDING);
				ACLMessage requestMessage = new ACLMessage(ACLMessage.REQUEST);

				if (pendingContracts != null && pendingContracts.size() > 0) {
					Contract contract = pendingContracts.get(0);
					VPS vps = contract.getVps();
					requestMessage.setContent(contract.getVps().getUuid() + ";" + contract.getId() + ";"
							+ Settings.ReceiveRequestResponseAgentGID);
					requestMessage.addReceiver(new AID(contract.getVps().getReceiveRequestAgentGID()));
					send(requestMessage);
					contract.setContractStatus(ContractStatus.PRERESERVED);
					contractDao.update(contract);
				}
				block(60000);

			}
		};
		addBehaviour(sendContractRequestBehaviour);

	}
}