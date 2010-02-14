package unit.hermes;

import factory.Factory;
import helpers.Database;
import sample.Address;
import sample.Person;
import junit.framework.TestCase;

public class HasOneTest extends TestCase {

	public static Person	marc;

	public void setUp() {
		Database.clear();
		marc = (Person) Factory.get("marc");
	}

	public void tearDown() {
		Database.clear();
	}

	public void testSaveAndLoad() {
		marc.reload();
		assertEquals("rue Tabarly", marc.getAdresse().getRue());
		marc.getAdresse().setRue("rue de Brest");
		marc.save();
		marc.reload();
		assertEquals("rue de Brest", marc.getAdresse().getRue());
	}

	public void testUpdate() {
		marc.getAdresse().delete();
		marc.setAdresse(new Address(25, "rue de Brest"));
		marc.save();
		marc = (Person) marc.reload();
		assertEquals(25, marc.getAdresse().getNumero());
		assertEquals("rue de Brest", marc.getAdresse().getRue());
	}

	public void testCascadeDeleteWithRelationHasOne() {
		marc.delete();
		assertNull(Address.find(marc.getAdresse().getId(), Address.class));
	}

	public void testNoCascadeDeleteWithRelationHasOne() {
		marc.getHasOneAssociations().get("adresse").setCascadeDelete(false);
		marc.delete();
		assertNotNull(Address.find(marc.getAdresse().getId(), Address.class));
	}
}
