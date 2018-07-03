package org.hppcoin.mas;

import java.util.Date;
import java.util.logging.Logger;

import org.hppcoin.crons.WalletCron;
import org.hppcoin.dao.impl.ContractDaoImpl;
import org.hppcoin.dao.impl.VPSDaoImpl;
import org.hppcoin.model.Contract;
import org.hppcoin.model.ContractStatus;
import org.hppcoin.model.Settings;
import org.hppcoin.model.VPS;
import org.hppcoin.model.VPSRentalStatus;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class ReceiveCredentialsAgent extends Agent {

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
		agentDescriptor.setType("ReceiveCredentialsAgent");
		agentDescriptor.setName(getLocalName() + "ReceiveCredentialsAgent");
		Settings.ReceiveCredentialsAgentGID = getAID().getName();
		template.addServices(agentDescriptor);
		try {
			DFService.register(this, template);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
		final long timer = System.currentTimeMillis();
		Behaviour receiveCredentialsBehaviour = new CyclicBehaviour() {

			@Override
			public void action() {
				ACLMessage credentialsResponse = receive();
				if (credentialsResponse != null && credentialsResponse.getContent() != null) {
					String credentials = credentialsResponse.getContent();
					try {
						String parts[] = credentials.split(";");
						String ip = parts[0];
						String port = parts[1];
						String user = parts[2];
						String uuid = parts[3];
						String contractId = parts[4];
						String pass = credentials.substring(
								ip.length() + port.length() + user.length() + uuid.length() + contractId.length() + 5);
						VPS vps = new VPSDaoImpl().find(uuid);
						Contract contract = new ContractDaoImpl().find(contractId);
						if (vps != null && contract != null) {
							vps.setUser(user);
							vps.setPassword(pass);
							// if success change status to rented
							vps.setRentalSattus(VPSRentalStatus.RENTED);
							vps.setIp(ip);
							vps.setSshPort(Integer.parseInt(port));
							contract.setIp(ip);
							long startTime=new Date().getTime();
							contract.setStartDate(startTime);
							contract.setUsername(user);
							contract.setPassword(pass);
							new VPSDaoImpl().update(vps);
							contract.setContractStatus(ContractStatus.ACTIVE);
							new ContractDaoImpl().update(contract);
							WalletCron.payContract(contract);
						}
					} catch (Exception e) {
						e.printStackTrace();
						LOGGER.severe(e.getMessage());
					}
				} else
					block();

			}
		};
		addBehaviour(receiveCredentialsBehaviour);
	}
}
