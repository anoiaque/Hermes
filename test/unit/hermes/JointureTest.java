package unit.hermes;

import core.Jointure;
import factory.Factory;
import helpers.Database;
import sample.Person;
import junit.framework.TestCase;

public class JointureTest extends TestCase {

	public static Person	human;

	public void setUp() {
		human = (Person) Factory.get("human");
	}

	public void tearDown() {
		Database.clear();
	}

	public void testJointureTableName() {
		Jointure jointure = human.getManyToManyAssociations().get("pets").getJointure();
		assertEquals("PEOPLE_PETS", jointure.getTableName());
	}
}
