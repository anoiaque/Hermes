package unit.hermes;

import factory.Factory;
import helpers.Database;

import java.util.List;

import junit.framework.TestCase;
import sample.Address;
import sample.Person;
import sample.Personne;
import sample.Pet;
import configuration.Configuration;
import core.Attribute;
import core.Table;

public class BasicTest extends TestCase {

	public static Person marc;

	public void setUp() {
		Database.clear();
		marc = (Person) Factory.get("marc");
	}

	public void testDefaultTableName() {
		assertEquals("people", marc.getTableName());
		assertEquals("people", Table.nameFor(Person.class));
		assertEquals("addresses", Table.nameFor(Address.class));
		assertEquals("pets", Table.nameFor(Pet.class));
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

	// private
	private boolean containsAttribute(List<Attribute> list, String name, String type, Object value) {
		for (Attribute attr : list) {
			String aname = attr.getName();
			String atype = attr.getSqlType();
			Object avalue = attr.getValue();
			
			if (aname.equals(name) && atype.equals(type) && avalue.equals(value))
				return true;
		}
		return false;
	}
}
