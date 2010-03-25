package unit.mysqladaptor;

import java.util.HashMap;

import adapters.MySql.Analyzer;

import junit.framework.TestCase;
import sample.Person;
import core.Inflector;

public class SqlBuilderTest extends TestCase {

	Person	person	= new Person();

	public void testJoinTablesNames() {
		String where_clause = "nom=' to.to' and adresse.numero= 10 and age=10 and adresse.rue='hh' and pets.nom='Medor'";
		HashMap<String, String> tables = Analyzer.tables(where_clause, person);
		
		assertEquals(2, tables.size());
		assertTrue(tables.containsKey("adresse"));
		assertTrue(tables.containsKey("pets"));
		assertTrue(tables.get("adresse").equals(Inflector.pluralize("address").toUpperCase()));
	}
}
