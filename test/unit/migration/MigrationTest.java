package unit.migration;

import junit.framework.TestCase;
import migration.Migration;
import migration.Table;
import sample.Man;
import sample.Person;

public class MigrationTest extends TestCase {

	// Note: "sample" is the package of models under test/
	private static String	file	= "test/unit/migration/migration_test.sql";

	public void testLoadModels() {
		Migration migration = new Migration("sample", file);
		assertEquals(7, migration.getTables().size());
		// 7 models ; 1 inherit ; 1 Jointure = 7 - 1 + 1
	}

	public void testSingleTabeInheritenceFlag() {
		Migration migration = new Migration("sample", file);

		for (Table table : migration.getTables()) {
			if (table.getKlass().equals(Man.class)) assertTrue(table.isSingleTableInheritence());
			else assertFalse(table.isSingleTableInheritence());
		}
	}

	public void testAttributesFromChildClassAreAddedToParentClass() {
		Migration migration = new Migration("sample", file);
		Table person = Table.withKlass(Person.class, migration.getTables());

		assertEquals(7 + 2, person.getColumns().size());
	}
	
}
