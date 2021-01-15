# easydebt: A simple project to solve Debt, Payments, and PaymentPlan management problem

The project is built with a service oriented architecture.

The initial design is based on three entities: Debt, PaymentPlan, and Payment

The data is fetched from three mock trueAccord rest services reflecting Debts, PaymentPlan, and Payments. 

There are two servcives in the system

a) A service to fetch data from the rest service. This is a rest client.

b) A service to process business requirements on those three schemas. 

We created a consolidated Debt object with attributes based on the business requirements. The putput of the application is a Json List each containing a ConsolidatedDebtPayment json object.

The application is built using Spring Boot.

A simple and bare minimum set of Junit tests were performed on the DebtService module. 

# Walking through the development

 The entities for input data:
 
```
public class Debt {

	private int id;
	private double amount;
	
	.....

}
```

```


public class Payment {

	private int paymentPlanId;
	private double amount;
	private Date date;
 ......
 }
 ```
 
 ```
 public class PaymentPlan {

	private int id;
	private int debtId;
	private double amountToPay;
	private String installmentFrequency;
	private double installmentAmount;
	private Date startDate;
 .....
 }
 ```
 
 The output object:
 ```
 
 
 
 public class ConsolidatedDebtInfo {

	private int id;
	private double amount;
	private Boolean inPaymentPlan;
	private double remainingAmount;
	private Date nextPaymentDate;
 ....
 }
 ```
 
 How do we retrieve the data?
 I am using RestTemplate to design the bare minimum rest client.
 
 ```
 
 
 
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
 ```
 
 The business services: I designed following methods.
 
 ```
	public boolean isDebtInPaymentPlan(Debt debt, List<PaymentPlan> paymentPlans) {
		return paymentPlans.stream().filter(p -> p.getDebtId() == debt.getId()).findFirst().isPresent();	
	}
 
 ```
 ```
 	public Optional<PaymentPlan> getPaymentPlanForDebt(Debt debt, List<PaymentPlan> paymentPlans) {
		return paymentPlans.stream().filter(p -> p.getDebtId() == debt.getId()).findAny();	
	}
```
```
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
 ```
 
 ```
 
 	public Optional<Date> getLatestPaymentDate(PaymentPlan paymentPlan, List<Payment> payments)
	{
		return payments.stream().filter(p -> p.getPaymentPlanId() == paymentPlan.getId())
		.map(pd -> pd.getDate()).max(Date::compareTo);
	}
```
```

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
			System.out.println(Date.from(ld.atStartOfDay(ZoneId.of("UTC")).toInstant()));
			return java.util.Date.from(ld.atStartOfDay()
				      .atZone(ZoneId.of("UTC"))
				      .toInstant());
		}
	}
	
```
```
	public String getJsonDebtObject(List<ConsolidatedDebtInfo> debtInfo) throws JsonProcessingException
	{
		ObjectMapper mapper = new ObjectMapper();
	    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	    mapper.setDateFormat(new StdDateFormat().withColonInTimeZone(true));

		return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(debtInfo);
	}
```
```
	public String  execute()
	{
		List<Debt> debtList = getDebts();
		List<PaymentPlan> paymentPlanList = getPaymentPlans();
		List<Payment> paymentList = getPayments();

		return execute(debtList, paymentPlanList, paymentList);
	}
```
```
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
```

Everything is orchestrated with Spring boot.

```
public class DebtApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(DebtApplication.class, args);
	}

	@Autowired 
	DebtPaymentService debtPaymentService;
	
	@Bean
	public RestTemplate restTemplate() {
	    return new RestTemplate();
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("---- Debt Services ----------");
		System.out.println(debtPaymentService.execute());
	}

}
```

Execution:
Run as a SpringBoot application:


```

2021-01-15 10:48:49.531  INFO 23976 --- [           main] com.dsm.test.example.DebtApplication     : Starting DebtApplication on L633394 with PID 23976 (C:\appDev\usr\springsource\workspace-e4.7-imot\easydebt\target\classes started by DYM in C:\appDev\usr\springsource\workspace-e4.7-imot\easydebt)
2021-01-15 10:48:49.534  INFO 23976 --- [           main] com.dsm.test.example.DebtApplication     : No active profile set, falling back to default profiles: default
2021-01-15 10:48:50.390  INFO 23976 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port(s): 9000 (http)
2021-01-15 10:48:50.414  INFO 23976 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2021-01-15 10:48:50.415  INFO 23976 --- [           main] org.apache.catalina.core.StandardEngine  : Starting Servlet engine: [Apache Tomcat/9.0.17]
2021-01-15 10:48:50.586  INFO 23976 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/api]    : Initializing Spring embedded WebApplicationContext
2021-01-15 10:48:50.586  INFO 23976 --- [           main] o.s.web.context.ContextLoader            : Root WebApplicationContext: initialization completed in 1014 ms
2021-01-15 10:48:50.903  INFO 23976 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 9000 (http) with context path '/api'
2021-01-15 10:48:50.907  INFO 23976 --- [           main] com.dsm.test.example.DebtApplication     : Started DebtApplication in 11.89 seconds (JVM running for 13.039)
---- Debt Services ----------
[ {
  "id" : 0,
  "amount" : 123.46,
  "is_in_payment_plan" : true,
  "remaining_amount" : 0.0,
  "next_payment_due_date" : null
}, {
  "id" : 1,
  "amount" : 100.0,
  "is_in_payment_plan" : true,
  "remaining_amount" : 50.0,
  "next_payment_due_date" : "2020-08-15T00:00:00.000+00:00"
}, {
  "id" : 2,
  "amount" : 4920.34,
  "is_in_payment_plan" : true,
  "remaining_amount" : 607.6700000000001,
  "next_payment_due_date" : "2020-08-15T00:00:00.000+00:00"
}, {
  "id" : 3,
  "amount" : 12938.0,
  "is_in_payment_plan" : true,
  "remaining_amount" : 622.415,
  "next_payment_due_date" : "2020-08-22T00:00:00.000+00:00"
} ]

```

 

 # Unit Tests
 I have designed only two test cases to meet a bare minimum requirement for the tests.
 
 ```
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
```


 
 
 
 
 
 
