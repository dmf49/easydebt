package com.dsm.test.example.rest.client;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.dsm.test.example.model.ConsolidatedDebtInfo;
import com.dsm.test.example.model.Debt;
import com.dsm.test.example.model.Payment;
import com.dsm.test.example.model.PaymentPlan;
import com.dsm.test.example.service.DebtPaymentService;

/**
 * A rest client to fetch data from trueAccord services
 * @author Debsankar Mukhopadhyay
 *
 */
@Service
public class DebtPaymentDataClient {

	private static final String DEBTS_URL = "https://my-json-server.typicode.com/druska/trueaccord-mock-payments-api/debts";
	private static final String PAYMENT_PLANS_URL = "https://my-json-server.typicode.com/druska/trueaccord-mock-payments-api/payment_plans";
	private static final String PAYMENTS_URL = "https://my-json-server.typicode.com/druska/trueaccord-mock-payments-api/payments";
	

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    DebtPaymentService service;
	
	public List<Debt> getDebts(){
        ResponseEntity<Debt[]> resp = restTemplate.getForEntity(DEBTS_URL,Debt[].class);
        Debt[] debts = resp.getBody();
        assert debts != null;
        return asList(debts);
	}
	
	public List<PaymentPlan> getPaymentPlans(){
        ResponseEntity<PaymentPlan[]> resp = restTemplate.getForEntity(PAYMENT_PLANS_URL,PaymentPlan[].class);
        PaymentPlan[] paymentPlans = resp.getBody();
        assert paymentPlans != null;
        return asList(paymentPlans);
	}
	public List<Payment> getPayments(){
        ResponseEntity<Payment[]> resp = restTemplate.getForEntity(PAYMENTS_URL,Payment[].class);
        Payment[] payments = resp.getBody();
        assert payments != null;
        return asList(payments);
	}
	
	
	
}
