package com.dsm.test.example.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Payment plan class
 * @author Debsankar Mukhopadhyay
 *
 */
public class PaymentPlan {

	private int id;
	private int debtId;
	private double amountToPay;
	private String installmentFrequency;
	private double installmentAmount;
	private Date startDate;
	
	
	@JsonProperty("id")
	public int getId() {
		return id;
	}


	@JsonProperty("debt_id")

	public int getDebtId() {
		return debtId;
	}


	@JsonProperty("amount_to_pay")
	public double getAmountToPay() {
		return amountToPay;
	}

	@JsonProperty("installment_frequency")
	public String getInstallmentFrequency() {
		return installmentFrequency;
	}


	@JsonProperty("installment_amount")
	public double getInstallmentAmount() {
		return installmentAmount;
	}


	@JsonProperty("start_date")
	public Date getStartDate() {
		return startDate;
	}



	public void setId(int id) {
		this.id = id;
	}


	public void setDebtId(int debtId) {
		this.debtId = debtId;
	}


	public void setAmountToPay(double amountToPay) {
		this.amountToPay = amountToPay;
	}


	public void setInstallmentFrequency(String installmentFrequency) {
		this.installmentFrequency = installmentFrequency;
	}


	public void setInstallmentAmount(double installmentAmount) {
		this.installmentAmount = installmentAmount;
	}


	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}


	@Override
	public String toString() {
		return "PaymentPlan [id=" + id + ", debtId=" + debtId + ", amountToPay=" + amountToPay
				+ ", installmentFrequency=" + installmentFrequency + ", installmentAmount=" + installmentAmount
				+ ", startDate=" + startDate + "]";
	}
	
	
	
	
	
}
