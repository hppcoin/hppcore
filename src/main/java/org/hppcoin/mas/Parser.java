package org.hppcoin.mas;

import java.util.ArrayList;
import java.util.List;

import org.hppcoin.model.CPU;
import org.hppcoin.model.Memory;
import org.hppcoin.model.PowerStatus;
import org.hppcoin.model.Storage;
import org.hppcoin.model.VPS;
import org.hppcoin.model.VPSRentalStatus;

public class Parser {

	public static List<VPS> fromJson(String hostsJson) {
		 List<VPS> vpsList=new ArrayList<VPS>();
		 if(hostsJson!=null) {
			 String hosts[]=hostsJson.split(";");
			 if(hosts!=null&&hosts.length>0) 
				 for(String host:hosts)
			 {   try { 
				 String parts[]=host.split(" ");
				 VPS vps=new VPS();
				 vps.setMine((byte)0);
		//		 vps.getReceiveRequestAgentGID()+" "+vps.getUuid()+" "+vps.getCpu().getCores()+" "+vps.getMemory().getMemorySize()+" "+vps.getStorage().getStorageSize()+" "+vps.getCostPerMinute()+" "+vps.getSetupPrice()+" "+vps.getPayDelay()+" "+vps.getPayementInterval()+" "+";";
				 vps.setReceiveRequestAgentGID(parts[0]);
			     vps.setUuid(parts[1]);
				 vps.setCpu(new CPU(Integer.parseInt(parts[2])));
				 vps.setMemory(new Memory(Long.parseLong(parts[3])));
				 vps.setStorage(new Storage(Long.parseLong(parts[4])));
				
				 vps.setCostPerMinute(Double.parseDouble(parts[5]));
				 vps.setSetupPrice(Double.parseDouble(parts[6]));
				 vps.setPayDelay(Integer.parseInt(parts[7]));
				 vps.setPayementInterval(Integer.parseInt(parts[8]));
				 vps.setIp(parts[9]);
				 vps.setRecievingAddress(parts[10]);
				 
				 vps.setRentalSattus(VPSRentalStatus.AVAILABLE);
				 vps.setPowerStatus(PowerStatus.RUNNING);
				 vpsList.add(vps);
				 
				 
			 }catch (Exception e) {
				e.printStackTrace();
			}
			 }
		 }
		return vpsList;
	}
	public static void main(String[] args) {
		
	}

}
