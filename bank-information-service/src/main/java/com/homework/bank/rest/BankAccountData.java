package com.homework.bank.rest;

public class BankAccountData {
	
	public Long id;
	public String firstName;
	public String lastName;
	public String IBAN;
	
	public BankAccountData() { }
	
	public BankAccountData(Long id, String firstName, String lastName, String IBAN) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.IBAN = IBAN;
	}
	
}
