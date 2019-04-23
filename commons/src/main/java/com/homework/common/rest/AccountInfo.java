package com.homework.common.rest;

/**
 * Data Transfer Object between Account and Bank services
 */
public class AccountInfo {
	
	public Long id;
	public String firstName;
	public String lastName;
	
	public AccountInfo() { }
	
	public AccountInfo(Long id, String firstName, String lastName) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	@Override
	public String toString() {
		return "AccountInfo [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + "]";
	}
	
}
