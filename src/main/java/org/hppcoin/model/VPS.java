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

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

@Entity
@NamedQueries({ @NamedQuery(name = "VPS.selectMyVps", query = "SELECT v FROM VPS v where v.mine=1"),
		@NamedQuery(name = "VPS.selectAll", query = "SELECT v FROM VPS v "),
		@NamedQuery(name = "VPS.selectAllOthers", query = "SELECT v FROM VPS v where v.mine=0"), })
// availability interval and end Date will be implemented in V1.2.0 (for
// companies and
// Universities use cases)

public class VPS {
	@Id
	//TODO prevent duplicate id
	private String uuid;
	private String ReceiveRequestAgentGID;
	private String ip;
	private int sshPort = 22;
	// The contract will be suspended after (payDelay) delayed payments
	private int payDelay = 3;
	@ElementCollection
	List<String> additionalIP;
	private String user;
	private String password;
	@ManyToOne
	XenServer xenServer;
	private String RSAFile;
	@Embedded
	private CPU cpu;
	@Embedded
	private Memory memory;
	@Embedded
	private Storage storage;
	private byte mine = 0;
	@Embedded
	private NetworkConnectivity networkConnectivity;
	private OS os;
	// must be generqted using OpenSSL to ensure recievingAddress is unique
	@Column(unique = true)
	private String recievingAddress;
	private double costPerMinute;
	private double setupPrice;
	// in minutes : the user may choose among 60 (hourly),360,720,1440 (daily),
	// 44640 (monthly)
	private int payementInterval;
	private long endDate;
	private long creationTime;
	private long lastAccessTime;
	private int spammer=0;
	
	private VPSRentalStatus rentalSattus;
	private PowerStatus powerStatus;
	@OneToMany(mappedBy = "vps")
	List<Contract> contracts;

	@Enumerated(EnumType.STRING)
	private VPSAccesType type;

	private boolean xenType;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRSAFile() {
		return RSAFile;
	}

	public void setRSAFile(String rSAFile) {
		RSAFile = rSAFile;
	}

	public VPSAccesType getType() {
		return type;
	}

	public void setType(VPSAccesType type) {
		this.type = type;
	}

	public OS getOs() {
		return os;
	}

	public void setOs(OS os) {
		this.os = os;
	}

	public byte getMine() {
		return mine;
	}

	public void setMine(byte mine) {
		this.mine = mine;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof VPS))
			return false;
		VPS vps = (VPS) obj;
		return (this.ip.equals(vps.ip) && (this.cpu.equals(vps.cpu)) && (this.memory.equals(vps.memory))
				&& (this.storage.equals(vps.storage)));
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

	public List<Contract> getContracts() {
		return contracts;
	}

	public void setContracts(List<Contract> contracts) {
		this.contracts = contracts;
	}

	public int getPayementInterval() {
		return payementInterval;
	}

	public void setPayementInterval(int payementInterval) {
		this.payementInterval = payementInterval;
	}

	public VPSRentalStatus getRentalSattus() {
		return rentalSattus;
	}

	public void setRentalSattus(VPSRentalStatus rentalSattus) {
		this.rentalSattus = rentalSattus;
	}

	public PowerStatus getPowerStatus() {
		return powerStatus;
	}

	public void setPowerStatus(PowerStatus powerStatus) {
		this.powerStatus = powerStatus;
	}

	public List<String> getAdditionalIP() {
		return additionalIP;
	}

	public void setAdditionalIP(List<String> additionalIP) {
		this.additionalIP = additionalIP;
	}

	public CPU getCpu() {
		return cpu;
	}

	public void setCpu(CPU cpu) {
		this.cpu = cpu;
	}

	public Memory getMemory() {
		return memory;
	}

	public void setMemory(Memory memory) {
		this.memory = memory;
	}

	public Storage getStorage() {
		return storage;
	}

	public void setStorage(Storage storage) {
		this.storage = storage;
	}

	public NetworkConnectivity getNetworkConnectivity() {
		return networkConnectivity;
	}

	public void setNetworkConnectivity(NetworkConnectivity networkConnectivity) {
		this.networkConnectivity = networkConnectivity;
	}

	public boolean isXenType() {
		return xenType;
	}

	public void setXenType(boolean xenType) {
		this.xenType = xenType;
	}

	public XenServer getXenServer() {
		return xenServer;
	}

	public void setXenServer(XenServer xenServer) {
		this.xenServer = xenServer;
	}

	public long getEndDate() {
		return endDate;
	}

	public void setEndDate(long endDate) {
		this.endDate = endDate;
	}

	public long getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}

	public long getLastAccessTime() {
		return lastAccessTime;
	}

	public void setLastAccessTime(long lastAccessTime) {
		this.lastAccessTime = lastAccessTime;
	}

	public int getSshPort() {
		return sshPort;
	}

	public void setSshPort(int sshPort) {
		this.sshPort = sshPort;
	}

	public int getPayDelay() {
		return payDelay;
	}

	public void setPayDelay(int payDelay) {
		this.payDelay = payDelay;
	}

	public int getSpammer() {
		return spammer;
	}

	public void setSpammer(int spammer) {
		this.spammer = spammer;
	}

	public String getReceiveRequestAgentGID() {
		return ReceiveRequestAgentGID;
	}

	public void setReceiveRequestAgentGID(String receiveRequestAgentGID) {
		ReceiveRequestAgentGID = receiveRequestAgentGID;
	}

	
	

}
