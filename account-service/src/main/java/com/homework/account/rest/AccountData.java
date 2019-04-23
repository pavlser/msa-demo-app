package com.homework.account.rest;

public class AccountData {
	
	public Long id;
	public String firstName;
	public String lastName;
	
	public AccountData() { }
	
	public AccountData(Long id, String firstName, String lastName) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	@Override
	public String toString() {
		return "AccountData [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + "]";
	}

}
