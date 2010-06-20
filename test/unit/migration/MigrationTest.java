package unit.migration;

import java.util.Iterator;

import junit.framework.TestCase;
import migration.Migration;
import migration.Table;

public class MigrationTest extends TestCase {

	public void testLoadModels() {
		Migration migration = new Migration("sample");
		assertEquals(7, migration.getTables().size());
	}

	@SuppressWarnings("unchecked")
	public void testSql() {
		Migration migration = new Migration("sample");
		Iterator tables = migration.getTables().iterator();

		while (tables.hasNext()) {
			Table table = (Table) tables.next();
			System.out.println(table.sql());
		}
	}

}
