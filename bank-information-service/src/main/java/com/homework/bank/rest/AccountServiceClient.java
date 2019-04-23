package com.homework.bank.rest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.homework.common.rest.AccountInfo;

@FeignClient(name="account-service")
public interface AccountServiceClient {
	
	@GetMapping("/account/{id}")
	AccountInfo findById(@PathVariable("id") Long id);
	
}
