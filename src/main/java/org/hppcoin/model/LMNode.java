package org.hppcoin.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({ @NamedQuery(name = "LMNode.selectAll", query = "SELECT l FROM LMNode l "),
		@NamedQuery(name = "LMNode.deleteAll", query = "DELETE FROM LMNode l ") })
public class LMNode {
	@Id
	private String address;
	private String protocol;
	private String ip;
	private double latitude;
	private double longitude;
	private String status;
	private String city;
	private String region;
	private String country;
	private String countryIso;
	private long blockNumber;

	public String getAddress() {
		return address;
	}

	public LMNode() {
		super();
	}

	public LMNode(String status, String protocol, String address) {
		super();
		this.status = status;
		this.protocol = protocol;
		this.address = address;
		this.ip = ip;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public long getBlockNumber() {
		return blockNumber;
	}

	public void setBlockNumber(long blockNumber) {
		this.blockNumber = blockNumber;
	}

	@Override
	public String toString() {
		String str = ip + " " + address + " " + blockNumber;
		if (country != null)
			str += " " + country;
		if (countryIso != null)
			str += " " + countryIso;
		if (region != null)
			str += " " + region;
		if (city != null)
			str += " " + city;
		return str;
	}

	public String getCountryIso() {
		return countryIso;
	}

	public void setCountryIso(String countryIso) {
		this.countryIso = countryIso;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

}
