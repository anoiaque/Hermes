package unit.hermes;

import factory.Factory;
import helpers.Database;
import sample.Address;
import sample.Person;
import junit.framework.TestCase;

public class HasOneTest extends TestCase {

	public static Person	human;

	public void setUp() {
		human = (Person) Factory.get("human");
	}

	public void tearDown() {
		Database.clear();
	}

	public void testSave() {
		human.getAdresse().setRue("");
		human = (Person) human.reload();
		assertEquals("rue Tabarly", human.getAdresse().getRue());
	}

	public void testUpdate() {
		human.getAdresse().setRue("rue de Brest");
		human.save();
		human = (Person) human.reload();
		assertEquals("rue de Brest", human.getAdresse().getRue());
	}

	public void testDelete() {
		human.getAdresse().delete();
		human = (Person) human.reload();
		assertEquals(null, human.getAdresse());
	}

	public void testUpdateWithNewObject() {
		human.getAdresse().delete();
		human.setAdresse(new Address(25, "rue de Brest"));
		human.save();
		human = (Person) human.reload();
		assertEquals(25, human.getAdresse().getNumero());
		assertEquals("rue de Brest", human.getAdresse().getRue());
	}

	public void testCascadeDelete() {
		human.delete();
		assertNull(Address.find(human.getAdresse().getId(), Address.class));
	}

	public void testNoCascadeDelete() {
		human.getHasOneAssociations().get("adresse").setCascadeDelete(false);
		human.delete();
		assertNotNull(Address.find(human.getAdresse().getId(), Address.class));
	}
}
