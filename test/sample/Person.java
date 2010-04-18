package sample;

import java.util.Set;
import java.util.regex.Pattern;

import core.Hermes;

public class Person extends Hermes {

	private int				age;
	private String		name;
	private String		phone;
	private Adress		adress;
	private Set<Pet>	pets;
	private Set<Car>	cars;

	public Person() {}

	public Person(String name, int age) {
		this.name = name;
		this.age = age;
	}

	protected void Associations() {
		hasOne("adress", "dependent:destroy");
		manyToMany("pets");
		hasMany("cars");
	}

	protected void Validations() {
		validatePresenceOf("name");
		validatePresenceOf("age");
		validatePresenceOf("adress");
		validateSizeOf("name", 1, 10, true);
		validateSizeOf("phone", 8, 10, true);
		validateUniquenessOf("phone");
		validateFormatOf("phone", Pattern.compile("\\d{8,10}"), true);
		validate();
	}

	private void validate() {
		if (age > 100) addError("age", "age must be <= 100");
	}

	public Set<Car> getCars() {
		return cars;
	}

	public void setCars(Set<Car> cars) {
		this.cars = cars;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Adress getAdress() {
		return adress;
	}

	public void setAdress(Adress adress) {
		this.adress = adress;
	}

	public Set<Pet> getPets() {
		return pets;
	}

	public void setPets(Set<Pet> pets) {
		this.pets = pets;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPhone() {
		return phone;
	}
}
