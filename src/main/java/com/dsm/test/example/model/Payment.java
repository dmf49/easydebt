package com.dsm.test.example.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Payment class
 * @author Debsankar Mukhopadhyay
 *
 */
public class Payment {

	private int paymentPlanId;
	private double amount;
	private Date date;
	
	@JsonProperty("payment_plan_id")
	public int getPaymentPlanId() {
		return paymentPlanId;
	}
	
	@JsonProperty("amount")
	public double getAmount() {
		return amount;
	}
	
	@JsonProperty("date")
	public Date getDate() {
		return date;
	}
	
	public void setPaymentPlanId(int paymentPlanId) {
		this.paymentPlanId = paymentPlanId;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "Payment [paymentPlanId=" + paymentPlanId + ", amount=" + amount + ", date=" + date + "]";
	}
	
	
}
