package unit.hermes;

import helpers.Database;

import java.util.Calendar;

import junit.framework.TestCase;
import sample.Address;
import sample.Man;
import adapters.MySql.TypeCast;
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
	
	public void testDateTypeCast(){
		Calendar birthday = Calendar.getInstance();
		birthday.set(1975, 10, 12);
		man.setBirthday(birthday);
		assertTrue(man.save());
		man.reload();
		assertEquals(1975,man.getBirthday().get(Calendar.YEAR));
		assertEquals(10,man.getBirthday().get(Calendar.MONTH));
		assertEquals(12,man.getBirthday().get(Calendar.DAY_OF_MONTH));
	}

}
