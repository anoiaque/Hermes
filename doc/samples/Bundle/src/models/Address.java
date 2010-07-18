package models;

import core.Hermes;

public class Address extends Hermes {

	private int			number;
	private String	street;

	public Address() {

	}

	public Address(int num, String rue) {
		this.number = num;
		this.street = rue;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

}
