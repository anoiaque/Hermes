package unit.hermes;

import factory.Factory;
import helpers.Database;

import java.util.List;

import junit.framework.TestCase;
import sample.Address;
import sample.Car;
import sample.Person;
import sample.Personne;
import sample.Pet;
import configuration.Configuration;
import core.Attribute;
import core.Table;

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
		assertEquals("PEOPLE", Table.nameFor(Person.class));
		assertEquals("ADDRESSES", Table.nameFor(Address.class));
		assertEquals("PETS", Table.nameFor(Pet.class));
		assertEquals("CARS", Table.nameFor(Car.class));
	}

	public void testRedefinedTableName() {
		assertEquals("personnel", Table.nameFor(Personne.class));
	}

	public void testIdIncrementation() {
		Person joe = new Person();
		joe.save();
		assertEquals(citizen.getId() + 1, joe.getId());
	}

	public void testBasicsFields() {
		Integer varcharLength = Configuration.SqlConverterConfig.varcharLength;
		String nameSqlType = "varchar(" + varcharLength + ")";
		List<Attribute> attributes = citizen.getAttributes();
		
		assertTrue(containsAttribute(attributes, "age", "integer", 30));
		assertTrue(containsAttribute(attributes, "nom", nameSqlType, "Marc"));
	}

	public void testReloading() {
		citizen.setAge(100);
		citizen.save();
		citizen = (Person) citizen.reload();
		assertEquals(100, citizen.getAge());
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
