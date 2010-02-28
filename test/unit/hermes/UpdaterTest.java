package unit.hermes;

import factory.Factory;
import helpers.Database;
import junit.framework.TestCase;
import sample.Person;

public class UpdaterTest extends TestCase {

	public static Person	marc;

	public void setUp() {
		marc = (Person) Factory.get("marc");
	}

	public void tearDown() {
		Database.clear();
	}

	public void testSave() {
		Person person = new Person();
		person.setNom("Anne");
		person.setAge(30);
		person.save();
		person = (Person) person.reload();
		assertEquals("Anne", person.getNom());
		assertEquals(30, person.getAge());
	}

	public void testUpdate() {
		marc.setNom("joe");
		marc.save();
		marc.setNom("");
		marc = (Person) marc.reload();
		assertEquals("joe", marc.getNom());
	}

	public void testDelete() {
		marc.delete();
		assertNull(Person.find(marc.getId(), Person.class));
	}

}
