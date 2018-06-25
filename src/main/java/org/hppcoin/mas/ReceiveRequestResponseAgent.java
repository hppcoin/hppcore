package org.hppcoin.mas;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.hppcoin.crons.WalletListener;
import org.hppcoin.dao.ContractDao;
import org.hppcoin.dao.impl.ContractDaoImpl;
import org.hppcoin.dao.impl.TransactionDaoImpl;
import org.hppcoin.dao.impl.VPSDaoImpl;
import org.hppcoin.model.Contract;
import org.hppcoin.model.ContractStatus;
import org.hppcoin.model.HPPTransaction;
import org.hppcoin.model.Settings;
import org.hppcoin.model.TransactionType;
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

public class ReceiveRequestResponseAgent  extends Agent {

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
	agentDescriptor.setType("ReceiveRequestResponseAgent");
	agentDescriptor.setName(getLocalName() + "ReceiveRequestResponseAgent");
	//update Settings
	Settings.ReceiveRequestResponseAgentGID=getAID().getName();
	template.addServices(agentDescriptor);
	
	try {
		DFService.register(this, template);
	} catch (FIPAException fe) {
		fe.printStackTrace();
	}
   final long timer=System.currentTimeMillis();
   
	Behaviour receiveRequestResponseBehaviour = new CyclicBehaviour() {

		@Override
		public void action() {
			ACLMessage reservatioResponse = receive();
			System.out.println("ReceiveRequestResponseAgent Started  and  block waiting for Request Responses  !"+((System.currentTimeMillis()-timer)/1000)+" seconds");
			if (reservatioResponse != null&&reservatioResponse.getContent()!=null) {
				String uuid=null,contractId=null,replyTo=null;
				try {
					String[] parts = reservatioResponse.getContent().split(";");
					uuid = parts[1];
					contractId = parts[2];
			        if(parts.length>3)		replyTo = parts[3];
				}catch (Exception e) {
					e.printStackTrace();
				}
				if(uuid!=null&&contractId!=null) {
				ContractDao contractDao=new ContractDaoImpl();
				
				Contract contract=contractDao.find(contractId);
				VPS vps=contract.getVps();
				String txt = reservatioResponse.getContent();
				if (txt.startsWith("OK")) {
					System.out.println("OK recieved ");
					contract.setContractStatus(ContractStatus.RESERVED);
					contractDao.update(contract);
					String txid = new WalletListener(false).sendToAddress(contract.getRecievingAddress(),
							contract.getSetupPrice());
					System.out.println("Setup Fees Paid txid = "+txid);
					if (txid != null && txid.length() > 20) {
						ACLMessage setupFeesMsg = new ACLMessage(ACLMessage.INFORM);
					HPPTransaction	newTransaction =new HPPTransaction(txid);
					newTransaction.setAddress(contract.getRecievingAddress());
					newTransaction.setAmount(contract.getSetupPrice());
					newTransaction.setTime(new Date().getTime());
					newTransaction.setType(TransactionType.SEND);
					newTransaction.setContract(contract);
					newTransaction = new TransactionDaoImpl().save(newTransaction);
					new ContractDaoImpl().update(contract, newTransaction);
						setupFeesMsg.setContent( txid + ";" + uuid + ";" +contractId + ";" +Settings.ReceiveCredentialsAgentGID + ";" +contract.getDurationHours());
						setupFeesMsg.addReceiver(new AID(replyTo));
						send(setupFeesMsg);
						System.out.println("Paiement sent and msg  broadcasted ");
					}

				}
				if (txt.startsWith("KO")) {
					// UNKNOWN STATUS
					vps.setRentalSattus(VPSRentalStatus.UNKNOWN);
					new VPSDaoImpl().update(vps);
					contract.setContractStatus(ContractStatus.INCOMPLETE);
					contractDao.update(contract);
				}
			
			} 
			} 
			//Block waiting for new messages
			else block();
		}
	};
	addBehaviour(receiveRequestResponseBehaviour);

}
}
