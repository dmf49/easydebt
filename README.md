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

 
