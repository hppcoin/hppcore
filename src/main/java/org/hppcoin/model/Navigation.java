package org.hppcoin.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Navigation {
	@Id
	private long id = 1;

	private String currentXenServerIP;
	private String currentTransactionID;
	private String currentVPSUID;
	private String currentContractID;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCurrentXenServerIP() {
		return currentXenServerIP;
	}

	public void setCurrentXenServerIP(String currentXenServerIP) {
		this.currentXenServerIP = currentXenServerIP;
	}

	public String getCurrentTransactionID() {
		return currentTransactionID;
	}

	public void setCurrentTransactionID(String currentTransactionID) {
		this.currentTransactionID = currentTransactionID;
	}

	public String getCurrentVPSUID() {
		return currentVPSUID;
	}

	public void setCurrentVPSUID(String currentVPSUID) {
		this.currentVPSUID = currentVPSUID;
	}

	public String getCurrentContractID() {
		return currentContractID;
	}

	public void setCurrentContractID(String currentContractID) {
		this.currentContractID = currentContractID;
	}

}
