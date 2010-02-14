package sample;

import core.Hermes;

public class Car extends Hermes {

	String	brand;
	int		category;

	public Car() {

	}

	public Car(String brand) {
		this.brand = brand;
	}

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String name) {
		this.brand = name;
	}
}
