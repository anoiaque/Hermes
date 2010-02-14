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

	public static Person	marc;

	public void setUp() {
		marc = (Person) Factory.get("marc");
	}

	public void tearDown() {
		Database.clear();
	}

	public void testDefaultTableName() {
		assertEquals("PEOPLE", marc.getTableName());
		assertEquals("PEOPLE", Table.nameFor(Person.class));
		assertEquals("ADDRESSES", Table.nameFor(Address.class));
		assertEquals("PETS", Table.nameFor(Pet.class));
		assertEquals("CARS", Table.nameFor(Car.class));
	}

	public void testRedefinedTableName() {
		assertEquals("personnel", Table.nameFor(Personne.class));
	}

	public void testId() {
		Person joe = new Person();
		joe.save();
		assertEquals(marc.getId() + 1, joe.getId());
	}

	public void testBasicsFields() {
		Integer varcharLength = Configuration.SqlConverterConfig.varcharLength;
		String nameType = "varchar(" + varcharLength + ")";
		List<Attribute> attributes = marc.getAttributes();
		assertTrue(containsAttribute(attributes, "age", "int", 30));
		assertTrue(containsAttribute(attributes, "nom", nameType, "Marc"));
	}

	public void testReloading() {
		marc.setAge(100);
		marc.save();
		marc = (Person) marc.reload();
		assertEquals(100, marc.getAge());
	}

	// private
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
