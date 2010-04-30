package unit.hermes;

import helpers.Database;

import java.util.List;

import junit.framework.TestCase;
import sample.Address;
import sample.Car;
import sample.Man;
import sample.Person;
import sample.Personne;
import sample.Pet;
import configuration.Configuration;
import core.Attribute;
import core.Hermes;
import core.Inflector;
import factory.Factory;

public class BasicTest extends TestCase {

	public static Person	citizen;

	public void setUp() {
		citizen = (Person) Factory.get("human");
	}

	public void tearDown() {
		Database.clear();
	}

	public void testDefaultTableName() {
		assertEquals("PEOPLE", citizen.getTableName());
		assertEquals("PEOPLE", Inflector.tableize(Person.class));
		assertEquals("ADDRESSES", Inflector.tableize(Address.class));
		assertEquals("PETS", Inflector.tableize(Pet.class));
		assertEquals("CARS", Inflector.tableize(Car.class));
	}

	public void testCount() {
		assertEquals(1, Hermes.count(Person.class));
	}

	public void testRedefinedTableName() {
		assertEquals("personnel", Inflector.tableize(Personne.class));
	}

	public void testIdIncrementation() {
		Person joe = new Person();
		joe.saveWithoutValidation();
		assertEquals(citizen.getId() + 1, joe.getId());
	}

	public void testBasicsFields() {
		Integer varcharLength = Configuration.SqlConverterConfig.varcharLength;
		String nameSqlType = "varchar(" + varcharLength + ")";
		List<Attribute> attributes = citizen.getAttributes();

		assertTrue(containsAttribute(attributes, "age", "integer", 30));
		assertTrue(containsAttribute(attributes, "name", nameSqlType, "Marc"));
	}

	public void testReloading() {
		citizen.setAge(100);
		citizen.save();
		citizen = (Person) citizen.reload();
		assertEquals(100, citizen.getAge());
	}

	public void testIsChanged() {
		citizen.setAge(100);
		citizen.save();
		assertFalse(citizen.isChanged());
		citizen.setAge(10);
		assertTrue(citizen.isChanged());
	}

	public void testExists() {
		assertTrue(citizen.exists());
		assertTrue(citizen.exists("name = 'Marc'"));
	}

	public void testToggle() {
		Man man = new Man();
		assertFalse(man.isMarried());
		man.toggle("married");
		assertTrue(man.isMarried());
		man.toggle("married");
		assertFalse(man.isMarried());
	}

	// Private methods
	private boolean containsAttribute(List<Attribute> list, String name, String type, Object value) {
		for (Attribute attr : list) {
			String aname = attr.getName();
			String atype = attr.getSqlType();
			Object avalue = attr.getValue();
			if (aname.equals(name) && atype.equals(type) && avalue.equals(value)) return true;
		}
		return false;
	}
}
