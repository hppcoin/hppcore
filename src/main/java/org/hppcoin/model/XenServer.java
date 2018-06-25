package org.hppcoin.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

@Entity
@NamedQueries({ @NamedQuery(name = "XenServer.selectAll", query = "SELECT xs FROM XenServer xs ") })
public class XenServer {
	@Id
	private String ip;
	private String hostName, status;
	private long creationTime, lastAccessTime;
	@OneToMany(mappedBy = "xenServer")
	private List<VPS> vpsList;
	private boolean defaultServer;
	private String username, password;

	public int getTotalVps() {
		if (vpsList == null || vpsList.size() < 1)
			return 0;
		else
			return vpsList.size();
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public List<VPS> getVpsList() {
		return vpsList;
	}

	public void setVpsList(List<VPS> vpsList) {
		this.vpsList = vpsList;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public boolean isDefaultServer() {
		return defaultServer;
	}

	public void setDefaultServer(boolean defaultServer) {
		this.defaultServer = defaultServer;
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

}
