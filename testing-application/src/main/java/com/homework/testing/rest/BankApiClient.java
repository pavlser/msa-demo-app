package com.homework.testing.rest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="gateway-server")
public interface BankApiClient {
	
	@GetMapping("/api/bank/{id}")
	BankAccountInfo getBankAccountInfoById(@PathVariable("id") Long id);
	
}
