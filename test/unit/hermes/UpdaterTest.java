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

	public void testDelete() {
		marc.delete();
		assertNull(Person.find(marc.getId(), Person.class));
	}

	public void testUpdate() {
		updateMarc();
		assertMarcIsUpdated();
	}

	// Private

	private void updateMarc() {
		marc.setNom("titi");
		marc.setAdresse(new Address(25, "rue de Brest"));
		marc.save();
	}

	private void assertMarcIsUpdated() {
		marc = (Person) marc.reload();
		assertEquals(30, marc.getAge());
		assertEquals("titi", marc.getNom());
		assertEquals(25, marc.getAdresse().getNumero());
		assertEquals("rue de Brest", marc.getAdresse().getRue());
		assertEquals(1, Person.findAll(Person.class).size());
	}

}
