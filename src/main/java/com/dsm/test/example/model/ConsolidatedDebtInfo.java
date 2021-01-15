package com.dsm.test.example.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * ConsolidatedDebtInfo class
 * <P>
 * Contains:
 * <P>
 * All of the debt's fields returned by the API
 * <P>
 * An additional boolean field, "is_in_payment_plan" set to true, if the debt is associated with a payment plan
 * <P>
 * Add a new field to the debts in the output: remaining_amount, containing the calculated amount remaining to be 
 * paid on the debt. Output the value as a JSON number.
 * <P>
 * Add a new field to the output: "next_payment_due_date", containing the ISO 8601 UTC date of when the next payment 
 * is due or null if there is no payment plan or if the debt has been paid off
 * <P>
 * @author Debsankar Mukhopadhyay
 *
 */
public class ConsolidatedDebtInfo {

	private int id;
	private double amount;
	private Boolean inPaymentPlan;
	private double remainingAmount;
	private Date nextPaymentDate;
	
    @JsonProperty("id")
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
    @JsonProperty("amount")
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	@JsonProperty("is_in_payment_plan")
	public Boolean getInPaymentPlan() {
		return inPaymentPlan;
	}
	public void setInPaymentPlan(Boolean inPaymentPlan) {
		this.inPaymentPlan = inPaymentPlan;
	}
	
	@JsonProperty("remaining_amount")
	public double getRemainingAmount() {
		return remainingAmount;
	}
	public void setRemainingAmount(double remainingAmount) {
		this.remainingAmount = remainingAmount;
	}
	
	@JsonProperty("next_payment_due_date")
	public Date getNextPaymentDate() {
		return nextPaymentDate;
	}
	public void setNextPaymentDate(Date nextPaymentDate) {
		this.nextPaymentDate = nextPaymentDate;
	}
	
	
}
