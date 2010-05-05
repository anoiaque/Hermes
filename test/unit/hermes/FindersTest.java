package unit.hermes;

import helpers.Database;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;
import sample.Address;
import sample.Car;
import sample.Person;
import sample.Personne;
import sample.Pet;
import core.Hermes;
import factory.Factory;

public class FindersTest extends TestCase {

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

	public void testFindById() {
		Person person = (Person) Hermes.find(citizen.getId(), Person.class);
		assertEquals(citizen.getAge(), person.getAge());
		assertEquals(citizen.getName(), person.getName());
	}

	public void testFindByIds() {
		List<Integer> ids = new ArrayList<Integer>();
		ids.add(citizen.getId());
		Set<Person> people = (Set<Person>) Hermes.find(ids, Person.class);
		assertEquals(1, people.size());
		ids.add(human.getId());
		people = (Set<Person>) Hermes.find(ids, Person.class);
		assertEquals(2, people.size());
	}

	public void testFindWithConditions() {
		Person person = (Person) Person.findFirst("age = 30", Person.class);
		assertEquals(citizen.getId(), person.getId());
		citizen.setName("George");
		citizen.save();
		person = (Person) Person.findFirst("name = 'George'", Person.class);
		assertEquals(citizen.getId(), person.getId());

	}

	public void testFindWithConditionsOnHasOneAssociation() {
		Set<Person> people;

		people = (Set<Person>) Person.find("adress.number = 13", Person.class);
		assertEquals(2, people.size());

		human.getAdress().setNumber(10);
		human.save();
		people = (Set<Person>) Person.find("adress.number= 13", Person.class);
		assertEquals(1, people.size());
	}

	public void testFindWithMultipleLevelConditions() {
		Set<Person> people;
		people = (Set<Person>) Person.find("adress.number = 13 and age = 30", Person.class);
		assertEquals(1, people.size());
		assertEquals(citizen.getName(), people.iterator().next().getName());
	}

	public void testFindRetrieveManyToManyAssociations() {
		Person person = (Person) Person.find(citizen.getId(), Person.class);
		assertEquals(2, person.getPets().size());
		Iterator<Pet> pets = citizen.getPets().iterator();
		assertTrue(containPet(person.getPets(), pets.next()));
		assertTrue(containPet(person.getPets(), pets.next()));

	}

	public void testStaticAndOnInstanceFinders() {
		Person clone1 = (Person) citizen.find(citizen.getId());
		Person clone2 = (Person) Person.find(citizen.getId(), Person.class);
		assertEquals(clone1.getId(), clone2.getId());
	}

	public void testFindWhithTableNameChangedInModel() {
		Personne person = new Personne();
		person.setName("Pierre");
		person.save();
		person = (Personne) Personne.find(person.getId(), Personne.class);
		assertEquals("Pierre", person.getName());
	}

	public void testFindWithConditionOnHasManyAssociation() {
		Set<Person> people;
		citizen.setCars((Set<Car>) Factory.get("cars"));
		citizen.save();
		people = (Set<Person>) Person.find("cars.brand = 'BMW'", Person.class);
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
		people = (Set<Person>) Person.find("cars.brand = 'BMW' or cars.brand = 'Audi'", Person.class);
		assertEquals(2, people.size());
	}

	public void testFindWithConditionsOnMultipleKindOfAttributes() {
		Set<Person> people;
		citizen.setCars((Set<Car>) Factory.get("cars"));
		citizen.save();
		people = (Set<Person>) Person.find("age = 30 and cars.brand = 'BMW'", Person.class);
		assertEquals(citizen.getName(), people.iterator().next().getName());
	}

	public void testFindWithConditionsOnManyToManyAssociation() {
		Set<Person> people;
		givePetAndCarsToCitizen();

		people = (Set<Person>) Person.find("pets.name = 'Toutou toutou'", Person.class);
		assertEquals(1, people.size());

		givePetToHuman();
		people = (Set<Person>) Person.find("pets.name = 'Toutou toutou'", Person.class);
		assertEquals(2, people.size());

		people = (Set<Person>) Person.find("pets.name = 'Toutou toutou' and age=30", Person.class);
		assertEquals(1, people.size());

		people = (Set<Person>) Person.find("pets.name = 'Toutou toutou' and cars.brand = 'BMW'",
				Person.class);
		assertEquals(1, people.size());
	}

	public void testFindBySql() {
		Set<Person> people;

		people = (Set<Person>) Hermes.findBySql("select * from people where age=30", Person.class);
		assertEquals(1, people.size());
	}

	public void testInstanceFinder() {
		Person david = new Person("David", 30, new Address());
		Person eve = new Person("Eve", 29, new Address());
		Person anne = new Person("Anne", 20, new Address());

		david.save();
		eve.save();
		anne.save();
		assertEquals(2, Person.allYoungs().size());
	}

	public void testWithLimitAndOffset() {
		Person.deleteAll(Person.class);
		Person david = new Person("David", 30, new Address());
		Person eve = new Person("Eve", 29, new Address());
		Person anne = new Person("Anne", 20, new Address());
		david.save();
		eve.save();
		anne.save();

		assertEquals(2, Hermes.findAll(Person.class, "limit => 2").size());

		Set<Person> people = (Set<Person>) Hermes.findAll(Person.class, "limit => 2, offset => 1");
		assertEquals(2, people.size());
		assertTrue(containPerson(people, eve));
		assertTrue(containPerson(people, anne));

		people = (Set<Person>) Hermes.find("name", "age>=20", Person.class, "limit=>2, offset=> 1");
		assertEquals(2, people.size());

		people = (Set<Person>) Hermes.find("name", "age>=20", Person.class, "limit=>5, offset=> 2");
		assertEquals(1, people.size());
	}

	// Private methods

	private void givePetToHuman() {
		Set<Pet> pets = new HashSet<Pet>();
		pets.add(new Pet("Chien", "Toutou toutou"));
		human.setPets(pets);
		human.save();

	}

	private void givePetAndCarsToCitizen() {
		Set<Pet> pets = new HashSet<Pet>();
		pets.add(new Pet("Chien", "Toutou toutou"));
		citizen.setCars((Set<Car>) Factory.get("cars"));
		citizen.setPets(pets);
		citizen.save();
	}

	private boolean containPet(Set<Pet> pets, Pet pet) {
		for (Pet p : pets) {
			if (p.getType().equals(pet.getType()) && p.getName().equals(pet.getName())) return true;
		}
		return false;
	}

	private boolean containPerson(Set<Person> people, Person person) {
		for (Person p : people) {
			if (p.getName().equals(person.getName())) return true;
		}
		return false;
	}
}
