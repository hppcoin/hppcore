package org.hppcoin.view;

import javafx.scene.control.Button;

public class XenServerView {
	private String ip, host, status;
	private String creationTime, lastAccessTime;
	private int totalVMs;
	private Button manage;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public int getTotalVMs() {
		return totalVMs;
	}

	public void setTotalVMs(int totalVMs) {
		this.totalVMs = totalVMs;
	}

	public Button getManage() {
		return manage;
	}

	public void setManage(Button manage) {
		this.manage = manage;
	}

}
