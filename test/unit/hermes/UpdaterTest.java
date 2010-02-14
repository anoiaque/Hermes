package unit.hermes;

import factory.Factory;
import helpers.Database;
import sample.Address;
import sample.Person;
import junit.framework.TestCase;

public class UpdaterTest extends TestCase {

	public static Person	marc;

	public void setUp() {
		Database.clear();
		marc = (Person) Factory.get("marc");
	}

	public void tearDown() {
		Database.clear();
	}

	public void testDelete() {
		marc.delete();
		assertNull(Person.find(marc.getId(), Person.class));
	}

	public void testUpdate() {
		marc.setNom("titi");
		marc.save();
		marc.setNom("tata");
		marc = (Person) marc.reload();
		assertEquals("titi", marc.getNom());
	}
}
