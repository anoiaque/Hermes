package unit.migration;

import java.util.List;

import configuration.Configuration;

import core.Attribute;
import sample.Type;
import migration.Migration;
import junit.framework.TestCase;

public class MigrationTest extends TestCase {

	private static Type	types	= new Type();

	public void testIdToBigInt() {
		String sql = "id int primary key auto_increment";
		assertEquals(sql, Migration.idColumnDefinition());
	}

	public void testAttributesSqlDefinition() {
		int varcharLength = Configuration.SqlConverterConfig.varcharLength;
		assertDefinition("car", "char(1)");
		assertDefinition("octet", "tinyint");
		assertDefinition("court", "smallint");
		assertDefinition("entier", "integer");
		assertDefinition("longue", "bigint");
		assertDefinition("reel", "float");
		assertDefinition("bigreal", "float");
		assertDefinition("str", "varchar(" + varcharLength + ")");
	}

	public void testTableDefinition() {
		assertEquals(tableDefinition(), Migration.tableDefinition(Type.class));
	}
	
	public void testForeignKeysDefinition(){
		assertEquals("person_id integer", Migration.foreignKeyDefinition("person_id"));
	}

	// Private methods

	private void assertDefinition(String attr, String sqlType) {
		Attribute attribute = getAttribute(attr, types.getAttributes());
		assertEquals(attr + " " + sqlType, Migration.columnDefinition(attribute));
		attribute = getAttribute("k" + attr, types.getAttributes());
		if (attribute == null) return;
		assertEquals("k" + attr + " " + sqlType, Migration.columnDefinition(attribute));
	}

	private Attribute getAttribute(String name, List<Attribute> attributes) {
		for (Attribute attribute : attributes) {
			if (attribute.getName().equals(name)) return attribute;
		}
		return null;
	}

	private String tableDefinition() {
		return "create table TYPES(id int primary key auto_increment,\n" + "car char(1),\n"
				+ "kcar char(1),\n" + "octet tinyint,\n" + "koctet tinyint,\n" + "court smallint,\n"
				+ "kcourt smallint,\n" + "entier integer,\n" + "kentier integer,\n" + "longue bigint,\n"
				+ "klongue bigint,\n" + "reel float,\n" + "kreel float,\n" + "bigreal float,\n" + "kbigreal float,\n"
				+ "str varchar(50));";
	}

}
