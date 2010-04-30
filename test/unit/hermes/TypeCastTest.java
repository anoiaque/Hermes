package unit.hermes;

import adapters.MySql.TypeCast;
import helpers.Database;
import junit.framework.TestCase;
import sample.Address;
import sample.Man;
import core.Attribute;
import core.Introspector;

public class TypeCastTest extends TestCase {

	public static Man	man;

	public void setUp() {
		man = new Man("Man", 23, new Address(9, "Avenue de Quimper"));
	}

	public void tearDown() {
		Database.clear();
	}

	public void testBooleanTypeCast() {
		man.setMarried(true);
		assertTrue(man.save());
		man.reload();
		assertTrue(man.isMarried());
		man.setMarried(false);
		assertTrue(man.save());
		man.reload();
		assertFalse(man.isMarried());
	}

	public void testTypeCastInvokingByIntropsector() {
		man.setMarried(true);
		Attribute attribute = man.getAttribute("married");
		Object value = Introspector.invokeMethod("booleanToSql", attribute, TypeCast.class);
		assertEquals(1, value);
	}

}
