package org.stringtemplate.v4.benchmark.oliver;

public class Customer {
	public final String firstName;
	public final String lastName;
	public final String address;

	public Customer(String firstName, String lastName, String address) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.address = address;
	}

	// all getters created for freemarker as it can not access the fields directly (JMTE and ST can)
	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getAddress() {
		return address;
	}

}
