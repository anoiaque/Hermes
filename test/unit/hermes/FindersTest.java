package unit.hermes;

import factory.Factory;
import helpers.Database;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;
import sample.Car;
import sample.Person;
import sample.Personne;
import sample.Pet;
import core.Hermes;

public class FindersTest extends TestCase {

	public static Person	citizen, human;

	public void setUp() {
		citizen = (Person) Factory.get("human");
		human = (Person) Factory.get("human");
		human.setAge(25);
		human.save();
	}

	public void tearDown() {
		Database.clear();
	}

	public void testFindById() {
		Person person = (Person) Hermes.find(citizen.getId(), Person.class);
		assertEquals(citizen.getAge(), person.getAge());
		assertEquals(citizen.getNom(), person.getNom());
	}

	public void testFindWithConditions() {
		Person person = (Person) Person.findFirst("age = 30", Person.class);
		assertEquals(citizen.getId(), person.getId());
	}

	public void testFindWithConditionsOnHasOneAssociation() {
		Set<Person> people;

		people = (Set<Person>) Person.find("adresse.numero = 13", Person.class);
		assertEquals(2, people.size());

		human.getAdresse().setNumero(10);
		human.save();
		people = (Set<Person>) Person.find("adresse.numero = 13", Person.class);
		assertEquals(1, people.size());
	}

	public void testFindWithMultipleLevelConditions() {
		Set<Person> people;
		people = (Set<Person>) Person.find("adresse.numero = 13 and age = 30", Person.class);
		assertEquals(1, people.size());
		assertEquals(citizen.getNom(), people.iterator().next().getNom());
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
		person.setNom("Pierre");
		person.save();
		person = (Personne) Personne.find(person.getId(), Personne.class);
		assertEquals("Pierre", person.getNom());
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
		assertEquals(citizen.getNom(), people.iterator().next().getNom());
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
}
