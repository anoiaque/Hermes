package unit.mysqladaptor;

import java.util.HashMap;

import adapters.MySql.Analyzer;

import junit.framework.TestCase;
import sample.Person;
import core.Inflector;

public class SqlBuilderTest extends TestCase {

	Person	person	= new Person("citizen kane",90);

	public void testJoinTablesNames() {
		String where_clause = "name=' to.to' and adress.number= 10 and age=10 and adress.street='hh' and pets.nom='Medor'";
		HashMap<String, String> tables = Analyzer.tables(where_clause, person);
		
		assertEquals(2, tables.size());
		assertTrue(tables.containsKey("adress"));
		assertTrue(tables.containsKey("pets"));
		assertTrue(tables.get("adress").equals(Inflector.pluralize("adress").toUpperCase()));
	}
}
