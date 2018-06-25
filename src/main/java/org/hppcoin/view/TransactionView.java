package org.hppcoin.view;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.scene.control.Button;

public class TransactionView {
	private FontAwesomeIcon type;
	private double amount;
	private String txDate;
	private int confirmations;
	private Button explore;

	public FontAwesomeIcon getType() {
		return type;
	}

	public void setType(FontAwesomeIcon type) {
		this.type = type;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getTxDate() {
		return txDate;
	}

	public void setTxDate(String txDate) {
		this.txDate = txDate;
	}

	public int getConfirmations() {
		return confirmations;
	}

	public void setConfirmations(int confirmations) {
		this.confirmations = confirmations;
	}

	public Button getExplore() {
		return explore;
	}

	public void setExplore(Button explore) {
		this.explore = explore;
	}

}
