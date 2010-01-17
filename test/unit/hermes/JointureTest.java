package unit.hermes;

import core.Jointure;
import factory.Factory;
import helpers.Database;
import sample.Person;
import junit.framework.TestCase;

public class JointureTest extends TestCase {
    public static Person marc;

    public void setUp() {
	Database.clear();
	marc = (Person) Factory.get("marc");
    }

    public void testJointureTableName() {
	Jointure jointure = marc.getManyToManyAssociations().get("pets").getJointure();

	assertEquals("PERSON_PET", jointure.getTableName());
    }
}
