package com.homework.testing;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import com.homework.testing.rest.BankApiClient;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class BankIntegrationTestingApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(BankIntegrationTestingApplication.class, args);
	}
	
	@Autowired
	BankApiClient api;
	
	@PostConstruct
	void runTests() {
		trace("\nRunning test calls...\n\n");
		dash();
		trace("Getting bank accounts\n");
		dash();
		trace("\n1: " + api.getBankAccountInfoById(1L));
		trace("\n2: " + api.getBankAccountInfoById(2L));
	}
	
	void dash() {
		for (int i=0; i<30; i++) {
			trace("-");
		}
		trace("\n");
	}
	
	void trace(Object o) {
		System.out.print(o);
	}
	
}
