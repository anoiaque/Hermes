package unit.hermes;

import helpers.Database;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

import junit.framework.TestCase;
import sample.Address;
import sample.Man;

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

	public void testCalendarTypeCast() {
		Calendar birthday = Calendar.getInstance();
		birthday.set(1975, 10, 12);
		man.setBirthday(birthday);
		assertTrue(man.save());
		man = (Man) man.reload();
		assertEquals(1975, man.getBirthday().get(Calendar.YEAR));
		assertEquals(10, man.getBirthday().get(Calendar.MONTH));
		assertEquals(12, man.getBirthday().get(Calendar.DAY_OF_MONTH));
	}

	public void testTimeStampTypeCast() {
		Calendar ts = Calendar.getInstance();
		ts.set(2010, 3, 10, 10, 10, 10);
		Timestamp createdAt = new Timestamp(ts.getTimeInMillis());
		man.setCreatedAt(createdAt);
		assertTrue(man.save());

		man = (Man) man.reload();
		ts.setTime(man.getCreatedAt());
		assertEquals(2010, ts.get(Calendar.YEAR));
		assertEquals(3, ts.get(Calendar.MONTH));
		assertEquals(10, ts.get(Calendar.DAY_OF_MONTH));
		assertEquals(10, ts.get(Calendar.HOUR_OF_DAY));
		assertEquals(10, ts.get(Calendar.MINUTE));
		assertEquals(10, ts.get(Calendar.SECOND));
	}

	public void testTimeTypeCast() {
		Calendar time = Calendar.getInstance();
		time.set(Calendar.HOUR_OF_DAY, 22);
		time.set(Calendar.MINUTE, 23);
		time.set(Calendar.SECOND, 21);
		man.setWake(new Time(time.getTimeInMillis()));
		assertTrue(man.save());
		man = (Man) man.reload();
		time.setTime(man.getWake());
		assertEquals(22, time.get(Calendar.HOUR_OF_DAY));
		assertEquals(23, time.get(Calendar.MINUTE));
		assertEquals(21, time.get(Calendar.SECOND));

	}

}
