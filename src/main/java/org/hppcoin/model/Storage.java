package org.hppcoin.model;

import javax.persistence.Embeddable;

@Embeddable
public class Storage {
	private String storageType;
	private long storageSize;

	public Storage() {
		super();
	}

	public Storage(long storageSize) {
		this.storageSize = storageSize;
	}

	public long getStorageSize() {
		return storageSize;
	}

	public void setStorageSize(long storageSize) {
		this.storageSize = storageSize;
	}

	public String getStorageType() {
		return storageType;
	}

	public void setStorageType(String storageType) {
		this.storageType = storageType;
	}

}
