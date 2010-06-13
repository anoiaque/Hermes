package unit.hermes;

import helpers.Database;

import java.util.Calendar;
import java.util.Set;

import junit.framework.TestCase;
import sample.Address;
import sample.Man;
import sample.Person;
import sample.Pet;
import factory.Factory;

public class InjectionsSqlTest extends TestCase {

	public static Person	human, man;

	public void setUp() {
		human = (Person) Factory.get("human");
		Man man = new Man();
		man.setName("Peter");
		man.setAge(30);
		man.setMarried(true);
		man.setAdress(new Address());
		man.save();
	}

	public void tearDown() {
		Database.clear();
	}

	public void testAllColumnValuesAreQuoted() {
		Calendar today = Calendar.getInstance();

		Object[] values = { "Peter", "30 or 1=1", true, today };
		Set<Man> men = null;
		men = (Set<Man>) Man.find("name=? and age=? and married=? and birthday=?", values, Man.class);
		assertEquals(0, men.size());
	}

	// Example : update people set name='George';drop table pets-- where id=12
	// Seems to be handled by jdbc or mysql server ???
	// No sql with update in logs ...
	public void testInjectionInUpdateStatement() {
		human.setName("George;drop table pets--");
		human.save();
		human = (Person) human.reload();
		assertNotNull(Pet.first(Pet.class));
	}

	// TODO
	public void testValuesOfLimitAndOffsetAreQuoted() {

	}

}
