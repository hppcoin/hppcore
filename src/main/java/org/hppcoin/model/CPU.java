package org.hppcoin.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class CPU {
	public CPU() {

	}

	public CPU(int cores) {
		this.cores = cores;
	}

	private String architecture;
	private String cpuOpModes;
	private String byteOrder;
	private int cores;
	private String onlineCPUslist;
	private int threadsPerCore;
	private int coresPersocket;
	private int sockets;
	private int nUMANodes;
	private String vendorID;
	private int cPUFamily;
	private int model;
	private String ModelName;
	private int stepping;
	private double cPUMHz;
	private double cPUMaxMHz;
	private double cPUMinMHz;
	private double bogoMIPS;
	private String virtualization;
	private String l1dCache;
	private String cache21i;
	private String l2cache;
	private String l3cache;
	@Column(length = 4000)
	private String flags;

	public String getArchitecture() {
		return architecture;
	}

	public void setArchitecture(String architecture) {
		this.architecture = architecture;
	}

	public String getCpuOpModes() {
		return cpuOpModes;
	}

	public void setCpuOpModes(String cpuOpModes) {
		this.cpuOpModes = cpuOpModes;
	}

	public String getByteOrder() {
		return byteOrder;
	}

	public void setByteOrder(String byteOrder) {
		this.byteOrder = byteOrder;
	}

	public int getCores() {
		return cores;
	}

	public void setCores(int cores) {
		this.cores = cores;
	}

	public String getOnlineCPUslist() {
		return onlineCPUslist;
	}

	public void setOnlineCPUslist(String onlineCPUslist) {
		this.onlineCPUslist = onlineCPUslist;
	}

	public int getThreadsPerCore() {
		return threadsPerCore;
	}

	public void setThreadsPerCore(int threadsPerCore) {
		this.threadsPerCore = threadsPerCore;
	}

	public int getCoresPersocket() {
		return coresPersocket;
	}

	public void setCoresPersocket(int coresPersocket) {
		this.coresPersocket = coresPersocket;
	}

	public int getSockets() {
		return sockets;
	}

	public void setSockets(int sockets) {
		this.sockets = sockets;
	}

	public int getnUMANodes() {
		return nUMANodes;
	}

	public void setnUMANodes(int nUMANodes) {
		this.nUMANodes = nUMANodes;
	}

	public String getVendorID() {
		return vendorID;
	}

	public void setVendorID(String vendorID) {
		this.vendorID = vendorID;
	}

	public int getcPUFamily() {
		return cPUFamily;
	}

	public void setcPUFamily(int cPUFamily) {
		this.cPUFamily = cPUFamily;
	}

	public int getModel() {
		return model;
	}

	public void setModel(int model) {
		this.model = model;
	}

	public String getModelName() {
		return ModelName;
	}

	public void setModelName(String modelName) {
		ModelName = modelName;
	}

	public int getStepping() {
		return stepping;
	}

	public void setStepping(int stepping) {
		this.stepping = stepping;
	}

	public double getcPUMHz() {
		return cPUMHz;
	}

	public void setcPUMHz(double cPUMHz) {
		this.cPUMHz = cPUMHz;
	}

	public double getcPUMaxMHz() {
		return cPUMaxMHz;
	}

	public void setcPUMaxMHz(double cPUMaxMHz) {
		this.cPUMaxMHz = cPUMaxMHz;
	}

	public double getcPUMinMHz() {
		return cPUMinMHz;
	}

	public void setcPUMinMHz(double cPUMinMHz) {
		this.cPUMinMHz = cPUMinMHz;
	}

	public double getBogoMIPS() {
		return bogoMIPS;
	}

	public void setBogoMIPS(double bogoMIPS) {
		this.bogoMIPS = bogoMIPS;
	}

	public String getVirtualization() {
		return virtualization;
	}

	public void setVirtualization(String virtualization) {
		this.virtualization = virtualization;
	}

	public String getL1dCache() {
		return l1dCache;
	}

	public void setL1dCache(String l1dCache) {
		this.l1dCache = l1dCache;
	}

	public String getCache21i() {
		return cache21i;
	}

	public void setCache21i(String cache21i) {
		this.cache21i = cache21i;
	}

	public String getL2cache() {
		return l2cache;
	}

	public void setL2cache(String l2cache) {
		this.l2cache = l2cache;
	}

	public String getL3cache() {
		return l3cache;
	}

	public void setL3cache(String l3cache) {
		this.l3cache = l3cache;
	}

	public String getFlags() {
		return flags;
	}

	public void setFlags(String flags) {
		this.flags = flags;
	}

}
