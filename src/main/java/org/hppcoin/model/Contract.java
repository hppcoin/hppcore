/*
 * HPPCOIN License
 * 
 * Copyright (c) 2017-2018, HPPCOIN Developers.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the 
 * Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject
 * to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR
 * ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.hppcoin.model;

import java.util.List;
import java.util.Random;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

@Entity
@NamedQueries({

		@NamedQuery(name = "Contract.selectAll", query = "SELECT c FROM Contract c "),
		@NamedQuery(name = "Contract.selectByAddress", query = "SELECT c FROM Contract c WHERE c.vps.recievingAddress= :address1"),
		@NamedQuery(name = "Contract.selectByContractStatus", query = "SELECT c FROM Contract c WHERE c.contractStatus= :status1"),
		@NamedQuery(name = "Contract.selectByContractType", query = "SELECT c FROM Contract c WHERE c.type= :type1"),

		@NamedQuery(name = "Contract.selectByContractTypeAndStatus", query = "SELECT c FROM Contract c WHERE c.type= :type1 AND c.contractStatus= :status1")
})
public class Contract {

	public Contract() {
		super();
	}

	public Contract(boolean generate,ContractType type) {
		super();
		if (generate) {
			String str = new Random().nextLong() + "@" + new Random().nextLong();
			this.id = str;
		}
		this.type=type;
	}

	@Id
	private String id;

	@ManyToOne
	private VPS vps;
	int cores;
	private long memorySize;
	private long storageSize;
	private long startDate;
	private int durationHours;
	private int payDelay = 3;
	private double totalAmount;
	@Enumerated(EnumType.STRING)
	private ContractType type;
	@Enumerated(EnumType.STRING)
	private ContractStatus contractStatus=ContractStatus.PENDING;
	// recored from vps data at contract creation time : user may change the cost
	// before the contract end date but it does not affect the ongoing contracts
	private String recievingAddress;
	private String ip;
	private String username;
	private String password;
	private int sshPort;
	private double costPerMinute;
	private double setupPrice;
	private int payementInterval;


	@OneToMany(mappedBy = "contract")
	List<HPPTransaction> transactions;

	public VPS getVps() {
		return vps;
	}

	public void setVps(VPS vps) {
		this.vps = vps;
		this.recievingAddress = vps.getRecievingAddress();
		this.ip=vps.getIp();
		this.memorySize=vps.getMemory().getMemorySize();
		this.storageSize=vps.getStorage().getStorageSize();
		this.cores=vps.getCpu().getCores();
		this.costPerMinute=vps.getCostPerMinute();
		this.setupPrice=vps.getSetupPrice();
		this.payementInterval=vps.getPayementInterval();
		this.payDelay=vps.getPayDelay();
		this.sshPort=vps.getSshPort();
		this.username=vps.getUser();
		this.password=vps.getPassword();
	}

	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	

	
	public long getStartDate() {
		return startDate;
	}

	public void setStartDate(long startDate) {
		this.startDate = startDate;
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

	public ContractStatus getContractStatus() {
		return contractStatus;
	}

	public void setContractStatus(ContractStatus contractStatus) {
		this.contractStatus = contractStatus;
	}

	public List<HPPTransaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<HPPTransaction> transactions) {
		this.transactions = transactions;
	}

	public ContractType getType() {
		return type;
	}

	public void setType(ContractType type) {
		this.type = type;
	}

	public String getRecievingAddress() {
		return recievingAddress;
	}

	
	public double getCostPerMinute() {
		return costPerMinute;
	}

	
	public double getSetupPrice() {
		return setupPrice;
	}

	

	public int getPayementInterval() {
		return payementInterval;
	}

	public void setRecievingAddress(String recievingAddress) {
		this.recievingAddress = recievingAddress;
	}

	public void setCostPerMinute(double costPerMinute) {
		this.costPerMinute = costPerMinute;
	}

	public void setSetupPrice(double setupPrice) {
		this.setupPrice = setupPrice;
	}

	public void setPayementInterval(int payementInterval) {
		this.payementInterval = payementInterval;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getSshPort() {
		return sshPort;
	}

	public void setSshPort(int sshPort) {
		this.sshPort = sshPort;
	}

	public int getCores() {
		return cores;
	}

	public void setCores(int cores) {
		this.cores = cores;
	}

	public long getMemorySize() {
		return memorySize;
	}

	public void setMemorySize(long memorySize) {
		this.memorySize = memorySize;
	}

	public long getStorageSize() {
		return storageSize;
	}

	public void setStorageSize(long storageSize) {
		this.storageSize = storageSize;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPayDelay() {
		return payDelay;
	}

	public void setPayDelay(int payDelay) {
		this.payDelay = payDelay;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Contract)) return false;
		Contract ct=(Contract)obj;
		return ct.getId().equals(this.id);
	}

}
