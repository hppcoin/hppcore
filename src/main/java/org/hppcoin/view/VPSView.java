package org.hppcoin.view;

import org.hppcoin.model.OS;
import org.hppcoin.model.PowerStatus;
import org.hppcoin.model.VPSRentalStatus;

import javafx.scene.control.Button;

public class VPSView {
	private String xenHost;
	private String ip;
	private String user;
	private int cpu;
	private long memory;
	private long storage;
	private double setupPrice;
	private double costPerMinute;
	// in minutes : the user may choose among 60 (hourly),360,720,1440 (daily),
	// 44640 (monthly)
	private String payementInterval;
	private OS os;
	private VPSRentalStatus rentalStatus;
	private PowerStatus powerStatus;
	private String creationTime;
	private String lastAccessTime;
	private Button manage;
	public int getCpu() {
		return cpu;
	}

	public void setCpu(int cpu) {
		this.cpu = cpu;
	}
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public long getMemory() {
		return memory;
	}

	public void setMemory(long memory) {
		this.memory = memory;
	}

	public long getStorage() {
		return storage;
	}

	public void setStorage(long storage) {
		this.storage = storage;
	}

	public OS getOs() {
		return os;
	}

	public void setOs(OS os) {
		this.os = os;
	}

	public VPSRentalStatus getRentalStatus() {
		return rentalStatus;
	}

	public void setRentalStatus(VPSRentalStatus rentalStatus) {
		this.rentalStatus = rentalStatus;
	}

	public PowerStatus getPowerStatus() {
		return powerStatus;
	}

	public void setPowerStatus(PowerStatus powerStatus) {
		this.powerStatus = powerStatus;
	}

	public String getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(String creationTime) {
		this.creationTime = creationTime;
	}

	public String getLastAccessTime() {
		return lastAccessTime;
	}

	public void setLastAccessTime(String lastAccessTime) {
		this.lastAccessTime = lastAccessTime;
	}

	public Button getManage() {
		return manage;
	}

	public void setManage(Button manage) {
		this.manage = manage;
	}

	public String getXenHost() {
		return xenHost;
	}

	public void setXenHost(String xenHost) {
		this.xenHost = xenHost;
	}

	public double getSetupPrice() {
		return setupPrice;
	}

	public void setSetupPrice(double setupPrice) {
		this.setupPrice = setupPrice;
	}

	public double getCostPerMinute() {
		return costPerMinute;
	}

	public void setCostPerMinute(double costPerMinute) {
		this.costPerMinute = costPerMinute;
	}

	public String getPayementInterval() {
		return payementInterval;
	}

	public void setPayementInterval(String payementInterval) {
		this.payementInterval = payementInterval;
	}

}
