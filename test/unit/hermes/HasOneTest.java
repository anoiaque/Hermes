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
		human.getAdress().setStreet("");
		human = (Person) human.reload();
		assertEquals("rue Tabarly", human.getAdress().getStreet());
	}

	public void testUpdate() {
		human.getAdress().setStreet("rue de Brest");
		human.save();
		human = (Person) human.reload();
		assertEquals("rue de Brest", human.getAdress().getStreet());
	}

	public void testDelete() {
		human.getAdress().delete();
		human = (Person) human.reload();
		assertEquals(null, human.getAdress());
	}

	public void testUpdateWithNewObject() {
		human.getAdress().delete();
		human.setAdress(new Address(25, "rue de Brest"));
		human.save();
		human = (Person) human.reload();
		assertEquals(25, human.getAdress().getNumber());
		assertEquals("rue de Brest", human.getAdress().getStreet());
	}

	public void testCascadeDelete() {
		human.delete();
		assertNull(Address.find(human.getAdress().getId(), Address.class));
	}

	public void testNoCascadeDelete() {
		human.getHasOneAssociations().get("adress").setCascadeDelete(false);
		human.delete();
		assertNotNull(Address.find(human.getAdress().getId(), Address.class));
	}
}
