package com.homework.testing.rest;

public class BankAccountInfo {
	
	public Long id;
	public String firstName;
	public String lastName;
	public String IBAN;
	
	@Override
	public String toString() {
		return "BankAccountInfo [id=" + id + ", firstName=" + firstName 
			+ ", lastName=" + lastName + ", IBAN=" + IBAN + "]";
	}
	
}
