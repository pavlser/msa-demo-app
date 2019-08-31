# Homework Bank Information System Microservices Demo Application

## Task

1. Components to set up:
   - config server
   - auth server
   - api gateway
   - discovery service
   - cloud monitoring

2. Services to implement:
   - Account Service
   - Bank Information Service
   
3. Implement service to manage accounts.

4. API endpoints:
   - GET /accounts/ - list of all accounts;
   - POST /account/{id} -  Create an account with specified ID (authenticated);
   - GET /account/{id} - Get specific account information (authenticated);
   - GET /bank/{id} - Get bank information for specified account id authenticated);

5. Implement service to get bank data of account:
   - for requested account id fetch account information from account service:
     - make sure that authentication checked automatically;
     - make sure that endpoint is resolved dynamically.
   - return account id as last digits of IBAN:
     - take "DE89370400440532013087" as a base value;
     - replace last N digits of IBAN wit account id.

6. Make sure that both services are properly registered in discovery and monitoring services.


## Solution Description

1. Main features:
   - Spring Boot 2.1.4
   - Spring Cloud Greenwich.SR1
   - Spring Cloud Config
   - Netflix Eureka
   - Spring Cloud Gateway
   - Spring Cloud OpenFeign
   - Codecentric Spring Boot Admin UI
   - JWT auth

2. Config server
   - port: 9000
   - library: spring-cloud-config-server/Greenwich.SR1
   - properties location: https://github.com/pavlser/msa-bank-demo-app-config.git
   - check url: http://localhost:9000/discovery-service/development
	
3. Discovery Service
   - port: 9001
   - library: spring-cloud-starter-netflix-eureka-server/Greenwich.SR1
   - check url: http://localhost:9001/
	
4. Gateway Server
   - port: 9002
   - library: spring-cloud-starter-gateway/Greenwich.SR1
   - WebFlux Security with JWT
   - check url (login:user, pass:user): 
     - http://localhost:9002/api/bank/1
     - http://localhost:9002/api/accounts
     - http://localhost:9002/api/account/1

5. Account Service
   - port: 9020
   - mapped path:
     - GET /accounts
     - GET /account/{id}
     - POST /account/{id}
	
6. Bank Service
   - port: 9010
   - mapped path:
     - GET /bank/{id}
   - communication between account service: 
     - spring-cloud-starter-openfeign/2.1.1.RELEASE
	
7. Admin Server
   - port: 9003
   - library: spring-boot-admin-server-ui/2.1.4
   - url: http://localhost:9003/#/applications

8. Commons 
   - general library between Account and Bank service

9. Auth Server - not finished Oauth2 Project.
   - Currently not calling from gateway server. TBD.

10. Testing Aplication 
    - JUnit5 test for rest integration testing.


## Build and run

### Checkout code and run bat file:

```
git clone https://pavlser/msa-demo-app.git

cd Sergiy-Pavlenko

start-all.bat
```

### This will do the following:
- build all maven modules
- consequentially launch all apps
- launch testing app which do:
  - authenticate on gateway with login/password and obtain token
  - call GET /api/bank/1 without JWT token and expect error 401
  - call from gateway GET /api/bank/1 and ensure this is 'Max Mustermann'
  - call from gateway GET /api/bank/2 and ensure this is 'Darth Vader'
  - POST to gateway /api/account/3 {firstName:Luke, lastName:Skywalker}
  - call from gateway GET /api/bank/3 and ensure this is 'Luke Skywalker'
  - call from gateway GET /api/accounts to list all accounts in the repository
 

### Output example:

```
======================================================
Build all maven artifacts
======================================================

[INFO] Scanning for projects...
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Build Order:
...............................................................................
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 27.835 s
[INFO] Finished at: 2019-04-22T14:33:58+03:00
[INFO] ------------------------------------------------------------------------


Start applications
======================================================

1 - Start config server
2 - Start Eureka discovery server
3 - Start Auth server
4 - Start Account service
5 - Start Bank service
6 - Start gateway server
7 - Start admin monitoring server
8 - Run integration tests

Press any key to continue . . .
```

## Testing

Tests will run automatically at the final step of bat file.

### Tests output

```
Start tests...

Perform login with basic auth
> jwtToken: eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwicm9sZXMiOiJST0xFX0FETUlOLFJPTEVfVVNFUiIsImlzcyI6ImdhdGV3YXkiLCJleHAiOjE1NTYxMDczMzd9.Ca_dxVJLAyb71SdBtV1N7x6D-sfMgHw84N2vdi0t72A

>>> Try request without jwt auth '/bank/1' ----------------------------------------------
> statusCode should be 401 (UNAUTHORIZED): 401

>>> Read account 1 ----------------------------------------------
Bank Data Info 1: BankAccountInfo [id=1, firstName=Max, lastName=Mustermann, IBAN=DE8937040044053201301]

>>> Read account 2 ----------------------------------------------
Bank Data Info 2: BankAccountInfo [id=2, firstName=Darth, lastName=Vader, IBAN=DE8937040044053201302]

>>> Create new account ----------------------------------------------
{"id":3,"firstName":"Luke","lastName":"Skywalker"}

>>> Read account 3 ----------------------------------------------
Bank Data Info 3: BankAccountInfo [id=3, firstName=Luke, lastName=Skywalker, IBAN=DE8937040044053201303]

>>> Read all accounts ----------------------------------------------
Accounts: [AccountInfo [id=1, firstName=Max, lastName=Mustermann], AccountInfo [id=2, firstName=Darth, lastName=Vader], AccountInfo [id=3, firstName=Luke, lastName=Skywalker]]

Tests are OK
```

### Running tests manually

Execute:

```
cd testing-application

run-tests.bat
```

## Monitoring

- Open http://localhost:9003/#/applications
- Login/pass: admin/admin

