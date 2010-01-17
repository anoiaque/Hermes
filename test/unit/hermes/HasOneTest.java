package unit.hermes;

import factory.Factory;
import helpers.Database;
import sample.Address;
import sample.Person;
import junit.framework.TestCase;

public class HasOneTest extends TestCase {

	public static Person marc;

	public void setUp() {
		Database.clear();
		marc = (Person) Factory.get("marc");
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

	// Only the foreign key id for person must be updated
	public void testNoSameChildDuplication() {
		Person jean = (Person) Factory.get("jean");
		jean.setAdresse(marc.getAdresse());
		jean.save();
		assertEquals(1, Address.find("rue='rue Tabarly'", Address.class).size());
		jean.setAdresse(null);
		jean = (Person) Person.find(jean.getId(), Person.class);
		assertEquals("rue Tabarly", jean.getAdresse().getRue());
	}

}
