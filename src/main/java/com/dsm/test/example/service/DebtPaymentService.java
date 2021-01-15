package com.dsm.test.example.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dsm.test.example.model.ConsolidatedDebtInfo;
import com.dsm.test.example.model.Debt;
import com.dsm.test.example.model.Payment;
import com.dsm.test.example.model.PaymentPlan;
import com.dsm.test.example.rest.client.DebtPaymentDataClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;

/**
 * DebtPaymentService class
 * <p> 
 * Services for processing Debt, PaymentPlan and Payment data for the specific business requirements
 * <p>
 * @author Debsankar Mukhopadhyay
 *
 */

@Service
public class DebtPaymentService {

	
	@Autowired
	private DebtPaymentDataClient debtPaymentDataClient;
	
	/**
	 * Determines if the Debt is associated with a Payment Plan
	 * @param debt
	 * @param paymentPlans
	 * @return boolean (true/false)
	 */
	public boolean isDebtInPaymentPlan(Debt debt, List<PaymentPlan> paymentPlans) {
		return paymentPlans.stream().filter(p -> p.getDebtId() == debt.getId()).findFirst().isPresent();	
	}
	
	/**
	 * Get the payment plan with which a Debt is associated
	 * @param debt
	 * @param paymentPlans
	 * @return Optional<PaymentPlan>
	 */
	public Optional<PaymentPlan> getPaymentPlanForDebt(Debt debt, List<PaymentPlan> paymentPlans) {
		return paymentPlans.stream().filter(p -> p.getDebtId() == debt.getId()).findAny();	
	}
	
	/**
	 * Remaining debt amount
	 * @param paymentPlan
	 * @param payments
	 * @return remaining amount on the debt 
	 *
	 */
	public double getRemainingAmount(PaymentPlan paymentPlan, List<Payment> payments)
	{
		double totalAmountPaid = 0;
		for ( Payment payment: payments )
		{
			if ( payment.getPaymentPlanId() == paymentPlan.getId() )
			{
				totalAmountPaid += payment.getAmount();
			}
		}
		return paymentPlan.getAmountToPay() - totalAmountPaid;
		
	}
	
	/**
	 * Get the Latest Payment Date for a payment Plan
	 * @param paymentPlan
	 * @param payments
	 * @return Optional<Date>
	 */
	public Optional<Date> getLatestPaymentDate(PaymentPlan paymentPlan, List<Payment> payments)
	{
		return payments.stream().filter(p -> p.getPaymentPlanId() == paymentPlan.getId())
		.map(pd -> pd.getDate()).max(Date::compareTo);
	}
	
	/**
	 * Calculate next payment date
	 * @param startDate
	 * @param installmentFrequency
	 * @param amountRemainingToBePaid
	 * @param debtInPaymentPlan
	 * @param lastPaymentDate
	 * @return next payment date
	 */
	public Date getNextPaymentDueDate(Date startDate, String installmentFrequency, double amountRemainingToBePaid, boolean debtInPaymentPlan, Date lastPaymentDate)
	{
		if ( amountRemainingToBePaid == 0 || !debtInPaymentPlan)
		{
			return null;
		}
		else
		{
			LocalDate startDateLocalDate = lastPaymentDate.toInstant().atZone(ZoneId.of("UTC")).toLocalDate();
			DayOfWeek dw = startDateLocalDate.getDayOfWeek();
			LocalDate ld = lastPaymentDate.toInstant().atZone(ZoneId.of("UTC")).toLocalDate();
			ld = ld.with(TemporalAdjusters.next(dw));
			return java.util.Date.from(ld.atStartOfDay()
				      .atZone(ZoneId.of("UTC"))
				      .toInstant());
		}
	}
	
	/**
	 * Construct the Json Debt Object for output
	 * @param debtInfo
	 * @return
	 * @throws JsonProcessingException
	 */
	public String getJsonDebtObject(List<ConsolidatedDebtInfo> debtInfo) throws JsonProcessingException
	{
		ObjectMapper mapper = new ObjectMapper();
	    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	    mapper.setDateFormat(new StdDateFormat().withColonInTimeZone(true));

		return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(debtInfo);
	}
	
	/**
	 * Execute the code 
	 * @return output Json object
	 */
	public String  execute()
	{
		List<Debt> debtList = getDebts();
		List<PaymentPlan> paymentPlanList = getPaymentPlans();
		List<Payment> paymentList = getPayments();

		return execute(debtList, paymentPlanList, paymentList);
	}
	
	/**
	 * Execute code with parameters
	 * @param debtList
	 * @param paymentPlanList
	 * @param paymentList
	 * @return output json object
	 */
	public String execute(List<Debt> debtList, List<PaymentPlan> paymentPlanList, List<Payment> paymentList) {
		List<ConsolidatedDebtInfo> consolidatedDebtInfo = new ArrayList<>();
		for ( Debt debt: debtList)
		{
			Optional<PaymentPlan>  paymentPlan = getPaymentPlanForDebt(debt, paymentPlanList);
			if ( paymentPlan.isPresent()) {
				PaymentPlan payPlan = paymentPlan.get();
				boolean debtInPayPlan = isDebtInPaymentPlan(debt, paymentPlanList);
				Optional<Date> latestPaymentDate = getLatestPaymentDate(payPlan, paymentList);
				Date nextPaymentDate = null;
				double amountRemaining = getRemainingAmount(payPlan, paymentList);
				if ( latestPaymentDate.isPresent() )
				{
					nextPaymentDate = 
							getNextPaymentDueDate(payPlan.getStartDate(), payPlan.getInstallmentFrequency(), 
									amountRemaining, debtInPayPlan, latestPaymentDate.get());
				}
				ConsolidatedDebtInfo debtInfo = new ConsolidatedDebtInfo();
				debtInfo.setId(debt.getId());
				debtInfo.setAmount(debt.getAmount());
				debtInfo.setInPaymentPlan(debtInPayPlan);
				debtInfo.setNextPaymentDate(nextPaymentDate);
				debtInfo.setRemainingAmount(amountRemaining);
				consolidatedDebtInfo.add(debtInfo);
			}
			
		}
		try {
			return getJsonDebtObject(consolidatedDebtInfo);
		}
		catch(Exception ex)
		{
			return ex.getMessage();
		}

	}

	public List<Debt> getDebts() {
		return debtPaymentDataClient.getDebts();
	}


	public List<PaymentPlan> getPaymentPlans() {
		return debtPaymentDataClient.getPaymentPlans();
	}


	public List<Payment> getPayments() {
		return debtPaymentDataClient.getPayments();
	}

	

}
