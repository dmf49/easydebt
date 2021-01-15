package com.dsm.test.example.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.dsm.test.example.model.Debt;
import com.dsm.test.example.model.Payment;
import com.dsm.test.example.model.PaymentPlan;
import com.dsm.test.example.rest.client.DebtPaymentDataClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(MockitoJUnitRunner.class)
public class DebtPaymentServiceTests {

	@Mock
	DebtPaymentDataClient mockDebtPaymentDataClient;
	
	List<Debt> debts = new ArrayList<>();
	List<PaymentPlan> paymentPlans = new ArrayList<>();
	List<Payment> payments = new ArrayList<>();
	List<Payment> payments2 = new ArrayList<>();
	String debtsJson = "[{\r\n" + 
			"  \"id\": 0,\r\n" + 
			"  \"amount\": 123.46\r\n" + 
			"}]";
	
	String paymentPlansJson = "[{\r\n" + 
			"  \"id\": 0,\r\n" + 
			"  \"debt_id\": 0,\r\n" + 
			"  \"amount_to_pay\": 102.50,\r\n" + 
			"  \"installment_frequency\": \"WEEKLY\",\r\n" + 
			"  \"installment_amount\": 51.25,\r\n" + 
			"  \"start_date\": \"2020-09-28T16:18:30Z\"\r\n" + 
			"}]";
	
    String paymentsJson = "[{\r\n" + 
    		"  \"payment_plan_id\": 0,\r\n" + 
    		"  \"amount\": 51.25,\r\n" + 
    		"  \"date\": \"2020-09-29T17:19:31Z\"\r\n" + 
    		"}]";
    String paymentsJson2 = "[\r\n" + 
    		"  {\r\n" + 
    		"    \"amount\": 51.25,\r\n" + 
    		"    \"date\": \"2020-09-29\",\r\n" + 
    		"    \"payment_plan_id\": 0\r\n" + 
    		"  },\r\n" + 
    		"  {\r\n" + 
    		"    \"amount\": 51.25,\r\n" + 
    		"    \"date\": \"2020-10-29\",\r\n" + 
    		"    \"payment_plan_id\": 0\r\n" + 
    		"  }]";
    
    String expectedResult = "[ {\r\n" + 
    		"  \"id\" : 0,\r\n" + 
    		"  \"amount\" : 123.46,\r\n" + 
    		"  \"is_in_payment_plan\" : true,\r\n" + 
    		"  \"remaining_amount\" : 51.25,\r\n" + 
    		"  \"next_payment_due_date\" : \"2020-10-06T00:00:00.000+00:00\"\r\n" + 
    		"} ]";
    String expectedResult2 = "[ {\r\n" + 
    		"  \"id\" : 0,\r\n" + 
    		"  \"amount\" : 123.46,\r\n" + 
    		"  \"is_in_payment_plan\" : true,\r\n" + 
    		"  \"remaining_amount\" : 0.0,\r\n" + 
    		"  \"next_payment_due_date\" : null\r\n" + 
    		"} ]";
	
	@Before
	public void setUp() throws Exception {
		
		ObjectMapper objectMapper = new ObjectMapper();
		debts = objectMapper.readValue(debtsJson, new TypeReference<List<Debt>>(){});
		paymentPlans = objectMapper.readValue(paymentPlansJson, new TypeReference<List<PaymentPlan>>(){});
		payments = objectMapper.readValue(paymentsJson, new TypeReference<List<Payment>>(){});
		payments2 = objectMapper.readValue(paymentsJson2, new TypeReference<List<Payment>>(){});
		
		
	}

	@Test
	public void executeTestHappyPath() {
		DebtPaymentService service = new DebtPaymentService();
		String result = service.execute(debts, paymentPlans, payments);
		
		assertEquals(expectedResult, result );
		
	}
	
	@Test
	public void executeTestMultiplePayments() {
		DebtPaymentService service = new DebtPaymentService();
		String result = service.execute(debts, paymentPlans, payments2);
		
		assertEquals(expectedResult2, result );
		
	}

}
