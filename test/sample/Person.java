package sample;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import core.Hermes;

public class Person extends Hermes {

	private int				age;
	private String		name;
	private String		phone;
	private Address		adress;
	private Set<Pet>	pets;
	private Set<Car>	cars;
	private Calendar	birthday;
	private Timestamp	createdAt;
	private Time			wake;

	public Person() {}

	public Person(String name, int age) {
		this.name = name;
		this.age = age;
	}

	public Person(String name, int age, Address adress) {
		this.name = name;
		this.age = age;
		this.adress = adress;
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

	protected void beforeValidate() {
		if (pets == null) pets = new HashSet<Pet>();
	}

	private void validate() {
		if (age > 500) addError("age", "You are not Master Yoda");
	}

	protected void afterCreate() {
		if (name == "Aurelia") this.setAge(35);
	}

	public static Set<Person> allYoungs() {
		return (Set<Person>) find("age < 30", Person.class);
	}

	// Getters & Setters
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

	public Address getAdress() {
		return adress;
	}

	public void setAdress(Address adress) {
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

	public void setBirthday(Calendar birthday) {
		this.birthday = birthday;
	}

	public Calendar getBirthday() {
		return birthday;
	}

	public void setWake(Time wake) {
		this.wake = wake;
	}

	public Time getWake() {
		return wake;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

}
