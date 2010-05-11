package factory;

import helpers.Database;

import java.util.HashSet;
import java.util.Set;

import sample.Address;
import sample.Car;
import sample.Person;
import sample.Pet;

public class Factory {

	public static void create(String feature, Integer number) {
		if (feature.equals("friends")) createFriends();
		else if (feature.equals("humanoids")) createHumanoids();
		else for (int i = 1; i <= number; i++)
			get(feature);
	}

	public static Object get(String feature) {
		if (feature.equals("human")) return human();
		if (feature.equals("pets")) return pets();
		if (feature.equals("cars")) return cars();

		return null;
	}

	private static void createHumanoids() {
		Database.clear();
		Person humanoid = (Person) Factory.get("human");
		Person jupiteroid = (Person) Factory.get("human");
		humanoid.setName("humanoid");
		jupiteroid.setName("jupiteroid");
		humanoid.saveWithoutValidation();
		jupiteroid.saveWithoutValidation();

	}

	private static void createFriends() {
		new Person("David", 30, new Address()).save();
		new Person("Eve", 29, new Address()).save();
		new Person("Anne", 20, new Address()).save();
	}

	private static Set<Car> cars() {
		Set<Car> cars = new HashSet<Car>();
		cars.add(new Car("Ferrari"));
		cars.add(new Car("BMW"));
		return cars;
	}

	private static Set<Pet> pets() {
		Set<Pet> pets = new HashSet<Pet>();
		pets.add(new Pet("Chien", "Medor"));
		pets.add(new Pet("Chat", "Felix"));
		return pets;
	}

	private static Person human() {
		Person person = new Person();
		person.setAge(30);
		person.setName("Marc");
		person.setAdress(new Address(13, "rue Tabarly"));
		person.setPets(pets());
		person.save();
		return person;
	}

}
