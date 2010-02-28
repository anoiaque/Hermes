package unit.hermes;

import helpers.Database;

import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;
import sample.Person;
import sample.Personne;
import sample.Pet;
import core.Hermes;
import factory.Factory;

public class FindersTest extends TestCase {

	public static Person	marc, jean;

	public void setUp() {
		marc = (Person) Factory.get("marc");
		jean = (Person) Factory.get("jean");
	}

	public void tearDown() {
		Database.clear();
	}

	public void testFindById() {
		marc.setAge(0);
		marc.setNom("");
		marc = (Person) Hermes.find(marc.getId(), Person.class);
		assertEquals(30, marc.getAge());
		assertEquals("Marc", marc.getNom());
	}

	public void testFindWithConditions() {
		int id = marc.getId();
		marc.setId(-1);
		marc = (Person) Person.find("age = 30", Person.class).iterator().next();
		assertEquals(id, marc.getId());
	}

	public void testFindWithConditionsOnHasOneAssociation() {
		Set<Person> people = (Set<Person>) Person.find("adresse.numero = 13", Person.class);
		assertEquals(1, people.size());
		jean.getAdresse().setNumero(13);
		jean.save();
		people = (Set<Person>) Person.find("adresse.numero = 13", Person.class);
		assertEquals(2, people.size());
		people = (Set<Person>) Person.find("adresse.rue = 'rue Kervegan'", Person.class);
		assertEquals(1, people.size());
	}

	public void testFindWithMultipleLevelConditions() {
		Set<Person> people;
		people = (Set<Person>) Person.find("adresse.numero = 13 and age = 30", Person.class);
		assertEquals(1, people.size());
	}

	public void testFindRetrieveManyToManyAssociations() {
		Person newMarc = (Person) Person.find(marc.getId(), Person.class);
		assertMarcRetrieveHisPets(newMarc);
		newMarc = (Person) Person.find("age = 30", Person.class).iterator().next();
		assertMarcRetrieveHisPets(newMarc);
	}

	// Test find with static method (for external use) and on an instance(used in
	// the model class)
	public void testStaticAndOnInstanceFindersSameResult() {
		Person clone1 = (Person) marc.find(marc.getId());
		Person clone2 = (Person) Person.find(marc.getId(), Person.class);
		assertEquals(clone1.getId(), clone2.getId());
	}

	public void testFindWhithTableNameChangedInModel() {
		Personne p = new Personne();
		p.setNom("Pierre");
		p.save();
		p = (Personne) Personne.find(p.getId(), Personne.class);
		assertEquals("Pierre", p.getNom());
	}

	// Private methods
	private void assertMarcRetrieveHisPets(Person newMarc) {
		Person marc = (Person) Factory.get("marc");
		assertEquals(2, newMarc.getPets().size());

		Iterator<Pet> pets = marc.getPets().iterator();
		assertTrue(containPet(newMarc.getPets(), pets.next()));
		assertTrue(containPet(newMarc.getPets(), pets.next()));
	}

	private boolean containPet(Set<Pet> pets, Pet pet) {
		for (Pet p : pets) {
			if (p.getType().equals(pet.getType()) && p.getName().equals(pet.getName())) return true;
		}
		return false;
	}
}
