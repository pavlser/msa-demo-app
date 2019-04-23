package com.homework.account.rest;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.homework.common.rest.AccountInfo;

@RestController
public class AccountDataController {
	
	Map<Long, AccountData> repository;
	
	@PostConstruct
	void initRepository() {
		repository = new ConcurrentHashMap<>();
		repository.put(1L, new AccountData(1L, "Max", "Mustermann"));
		repository.put(2L, new AccountData(2L, "Darth", "Vader"));
	}

	@GetMapping("/accounts")
	public Collection<AccountData> getAll() {
		return repository.values();
	}

	@GetMapping("/account/{id}")
	public AccountInfo getById(@PathVariable Long id) {
		// find entity in repository
		AccountData accData = repository.get(id);
		// convert to DTO and return
		return new AccountInfo(accData.id, accData.firstName, accData.lastName);
	}
	
	@PostMapping("/account/{id}")
	public ResponseEntity<AccountInfo> createOrUpdate(
			@PathVariable Long id, 
			@RequestBody AccountInfo account) {
		// create new entity
		AccountData accData = new AccountData(id, account.firstName, account.lastName);
		// save entity to repository
		repository.put(id, accData);
		// create HAL-like response with DTO
		return ResponseEntity
			.created(UriComponentsBuilder.fromPath("/account/" + id).build().toUri())
			.body(account);
	}

}
