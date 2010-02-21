package unit.mysqladaptor;

import java.util.HashMap;

import junit.framework.TestCase;
import sample.Person;
import adaptors.MySql.Analyser;
import core.Inflector;

public class SqlBuilderTest extends TestCase {

	Person	person	= new Person();

	// Test on retrieve tables names for from_clause from where_clause when
	// conditions on jointures
	public void testJoinTablesNames() {
		String where_clause = "nom=' to.to' and adresse.numero= 10 and age=10 and adresse.rue='hh' and pets.nom='Medor'";
		HashMap<String, String> tablesNames = Analyser.joinedTables(where_clause, person);
		assertEquals(2, tablesNames.size());
		assertTrue(tablesNames.containsKey("adresse"));
		assertTrue(tablesNames.containsKey("pets"));
		assertTrue(tablesNames.get("adresse").equals(Inflector.pluralize("address").toUpperCase()));
	}
}
