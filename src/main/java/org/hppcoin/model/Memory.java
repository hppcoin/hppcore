package org.hppcoin.model;

import javax.persistence.Embeddable;

@Embeddable
public class Memory {
	public Memory() {
		super();
	}

	public Memory(long memorySize) {
		super();
		this.memorySize = memorySize;
	}

	private long memorySize;
	private String memoryConstructor;
	private String memoryType;

	public long getMemorySize() {
		return memorySize;
	}

	public void setMemorySize(long memorySize) {
		this.memorySize = memorySize;
	}

	public String getMemoryConstructor() {
		return memoryConstructor;
	}

	public void setMemoryConstructor(String memoryConstructor) {
		this.memoryConstructor = memoryConstructor;
	}

	public String getMemoryType() {
		return memoryType;
	}

	public void setMemoryType(String memoryType) {
		this.memoryType = memoryType;
	}

}
