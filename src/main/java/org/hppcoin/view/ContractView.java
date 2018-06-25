package org.hppcoin.view;

import org.hppcoin.model.ContractStatus;
import org.hppcoin.model.ContractType;

import javafx.scene.control.Button;

public class ContractView {
	private int durationHours;
	private double totalAmount;
	private ContractType type;
	private ContractStatus contractStatus;
	private String recievingAddress;
	private double costPerMinute;
	private double setupPrice;
	private String endDate;
	private String startDate;
	private double paidAmount;
	private double hourlyPrice;
	private String payementInterval;
	private String status;
	private Button manage;
	private int cpu;
	private int sshPort;
	private String ip;
	private String password;
	private String username;
	private long memory;
	private long storage;

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public double getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(double paidAmount) {
		this.paidAmount = paidAmount;
	}

	public double getHourlyPrice() {
		return hourlyPrice;
	}

	public void setHourlyPrice(double hourlyPrice) {
		this.hourlyPrice = hourlyPrice;
	}

	public String getPayementInterval() {
		return payementInterval;
	}

	public void setPayementInterval(String payementInterval) {
		this.payementInterval = payementInterval;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Button getManage() {
		return manage;
	}

	public void setManage(Button manage) {
		this.manage = manage;
	}

	public int getCpu() {
		return cpu;
	}

	public void setCpu(int cpu) {
		this.cpu = cpu;
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

	public int getDurationHours() {
		return durationHours;
	}

	public void setDurationHours(int durationHours) {
		this.durationHours = durationHours;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public ContractType getType() {
		return type;
	}

	public void setType(ContractType type) {
		this.type = type;
	}

	public ContractStatus getContractStatus() {
		return contractStatus;
	}

	public void setContractStatus(ContractStatus contractStatus) {
		this.contractStatus = contractStatus;
	}

	public String getRecievingAddress() {
		return recievingAddress;
	}

	public void setRecievingAddress(String recievingAddress) {
		this.recievingAddress = recievingAddress;
	}

	public double getCostPerMinute() {
		return costPerMinute;
	}

	public void setCostPerMinute(double costPerMinute) {
		this.costPerMinute = costPerMinute;
	}

	public double getSetupPrice() {
		return setupPrice;
	}

	public void setSetupPrice(double setupPrice) {
		this.setupPrice = setupPrice;
	}

	public int getSshPort() {
		return sshPort;
	}

	public void setSshPort(int sshPort) {
		this.sshPort = sshPort;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	
	
}
