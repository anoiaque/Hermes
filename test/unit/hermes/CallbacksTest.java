package unit.hermes;

import junit.framework.TestCase;
import sample.Address;
import sample.Person;

public class CallbacksTest extends TestCase {

	private static Person	aurelia;

	public void setUp() {
		aurelia = new Person("Aurelia", 30, new Address());
	}

	public void testBeforeValidationCallback() {
		assertNotNull(aurelia.getPets());
	}

	public void testAfterCreateCallBack() {
		assertEquals(30, aurelia.getAge());
		assertTrue(aurelia.save());
		assertEquals(35, aurelia.getAge());
	}
}
