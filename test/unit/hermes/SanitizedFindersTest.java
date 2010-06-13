package unit.hermes;

import helpers.Database;

import java.util.Set;

import junit.framework.TestCase;
import sample.Car;
import sample.Person;
import core.Hermes;
import factory.Factory;

public class SanitizedFindersTest extends TestCase {

	public static Person	citizen, human;

	public void setUp() {
		citizen = (Person) Factory.get("human");
		human = (Person) Factory.get("human");
		human.setAge(31);
		human.save();
	}

	public void tearDown() {
		Database.clear();
	}

	public void testFindWithConditions() {
		Object[] values = { 30 };
		Person person = (Person) Person.first("age = ?", values, Person.class);
		assertEquals(citizen.getId(), person.getId());
		citizen.setName("George");
		citizen.save();
		values[0] = "George";
		person = (Person) Person.first("name =?", values, Person.class);
		assertEquals(citizen.getId(), person.getId());

	}

	public void testFindWithConditionsOnHasOneAssociation() {
		Set<Person> people;
		Object[] values = { 13 };
		people = (Set<Person>) Person.find("adress.number = ?", values, Person.class);
		assertEquals(2, people.size());
	}

	public void testFindWithMultipleLevelConditions() {
		Set<Person> people;
		Object[] values = { 13, 30 };
		people = (Set<Person>) Person.find("adress.number =? and age = ?", values, Person.class);
		assertEquals(1, people.size());
		assertEquals(citizen.getName(), people.iterator().next().getName());
	}

	public void testFindWithConditionOnHasManyAssociation() {
		Set<Person> people;
		Object[] values = { "BMW" };
		citizen.setCars((Set<Car>) Factory.get("cars"));
		citizen.save();
		people = (Set<Person>) Person.find("cars.brand = ?", values, Person.class);
		assertEquals(1, people.size());
	}

	public void testFindWithConditionsOnHasManyAssociation() {
		Set<Person> people;
		Set<Car> cars = (Set<Car>) Factory.get("cars");
		cars.iterator().next().setBrand("Audi");
		human.setCars(cars);
		human.save();
		citizen.setCars((Set<Car>) Factory.get("cars"));
		citizen.save();
		Object[] values = { "BMW", "Audi" };
		people = (Set<Person>) Person.find("cars.brand = ? or cars.brand = ?", values, Person.class);
		assertEquals(2, people.size());
	}

	public void testDoubleQuote() {
		human.setName("o'clock");
		assertTrue(human.save());
		Object[] values = { "o'clock" };
		Set<Person> humans = (Set<Person>) Hermes.find("name=?", values, Person.class);
		assertEquals(1, humans.size());
	}

}
