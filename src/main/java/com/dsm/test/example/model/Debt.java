package com.dsm.test.example.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Debt class
 * @author Debsankar Mukhopadhyay
 *
 */
public class Debt {

	private int id;
	private double amount;
	
	@JsonProperty("id")
	public int getId() {
		return id;
	}
	@JsonProperty("amount")
	public double getAmount() {
		return amount;
	}
	
	@Override
	public String toString() {
		return "Debt [id=" + id + ", amount=" + amount + "]";
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}

}
