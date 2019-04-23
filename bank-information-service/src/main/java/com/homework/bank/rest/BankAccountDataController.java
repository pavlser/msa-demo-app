package com.homework.bank.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.homework.common.rest.AccountInfo;

@RestController
public class BankAccountDataController {
	
	@Autowired
	AccountServiceClient accountClient;
	
	private final static String IbanBaseValue = "DE89370400440532013087";
	
	@GetMapping("/bank/{id}")
	public BankAccountData getOne(@PathVariable Long id) {
		AccountInfo acc = accountClient.findById(id);
		return new BankAccountData(id, acc.firstName, acc.lastName, getIbanCode(id));
	}
	
	/**
	 * Replace last N chars of IBAN base value with given ID
	 * @param id - provided id
	 * @return String
	 */
	String getIbanCode(Long id) {
		String idStr = String.valueOf(id);
		return IbanBaseValue.substring(0, IbanBaseValue.length() - idStr.length() - 1) + idStr;
	}

}
