package org.hppcoin.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Balance {
	@Id
	final long id = 1;
	double available; 
	double pending;

	public double getAvailable() {
		return available;
	}

	public void setAvailable(double available) {
		this.available = available;
	}

	public double getPending() {
		return pending;
	}

	public void setPending(double pending) {
		this.pending = pending;
	}

	public long getId() {
		return id;
	}

}
