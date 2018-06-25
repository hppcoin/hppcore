package org.hppcoin.model;

import java.util.TimeZone;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
public class Settings {
	@Id
	private final long id = 1;
	// used for to check VPS availability based on timeZones
	private TimeZone timeZone = TimeZone.getDefault();
	private String defaultReceivingAddress;
	// Login Password
	private String password;
	private String hint;
	@Transient
	public static String ReceiveRequestAgentGID="";
	@Transient
	public static String ReceiveRequestResponseAgentGID="";
	@Transient
	public static String SetupFeesVerificationGID="";
	@Transient
	public static String ReceiveCredentialsAgentGID="";
	@Transient
	public static String buyerAgentGID="";
	@Transient
	public static Object monitor=new Object();
	// close Dialog
	private int doNotShowAgain = 0;

	// New VPS Default values
	private int paymentInterval;
	private double setupFees;
	private int maxDuration;

	public long getId() {
		return id;
	}

	public int getDoNotShowAgain() {
		return doNotShowAgain;
	}

	public void setDoNotShowAgain(int doNotShowAgain) {
		this.doNotShowAgain = doNotShowAgain;
	}

	public int getPaymentInterval() {
		return paymentInterval;
	}

	public void setPaymentInterval(int paymentInterval) {
		this.paymentInterval = paymentInterval;
	}

	public double getSetupFees() {
		return setupFees;
	}

	public void setSetupFees(double setupFees) {
		this.setupFees = setupFees;
	}

	public int getMaxDuration() {
		return maxDuration;
	}

	public void setMaxDuration(int maxDuration) {
		this.maxDuration = maxDuration;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getHint() {
		return hint;
	}

	public void setHint(String hint) {
		this.hint = hint;
	}

	public String getDefaultReceivingAddress() {
		return defaultReceivingAddress;
	}

	public void setDefaultReceivingAddress(String defaultReceivingAddress) {
		this.defaultReceivingAddress = defaultReceivingAddress;
	}

	public TimeZone getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}

	//

}
