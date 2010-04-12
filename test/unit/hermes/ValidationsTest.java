package unit.hermes;

import helpers.Database;
import junit.framework.TestCase;
import sample.Adress;
import sample.Person;
import sample.Personne;
import core.Error;
import core.Hermes;

public class ValidationsTest extends TestCase {

	public static Person	citizen;

	public void setUp() {
		citizen = new Person();
		citizen.setAdress(new Adress());
		citizen.setName("Job");
	}

	public void tearDown() {
		Database.clear();
	}

	public void testValidatePresence() {
		citizen.setName(null);
		assertFalse(citizen.isValid());
		assertError(new Error(Error.Symbol.PRESENCE), "name", citizen);
		citizen.setName("Job");
		assertTrue(citizen.isValid());
	}

	public void testValidateWithOwnMessage() {
		Personne citizen = new Personne();
		assertFalse(citizen.isValid());
		assertError(new Error(Error.Symbol.PRESENCE, "name must be present yes!"), "name", citizen);
		citizen.setName("Job");
		assertTrue(citizen.isValid());
	}

	public void testValidateSize() {
		citizen.setName("veryverylongname");
		assertFalse(citizen.isValid());
		assertError(new Error(Error.Symbol.SIZE), "name", citizen);
		citizen.setName("Job");
		assertTrue(citizen.isValid());
	}

	public void testValidationOnAssociation() {
		citizen.setAdress(null);
		assertFalse(citizen.isValid());
		assertError(new Error(Error.Symbol.PRESENCE), "adress", citizen);
	}

	public void testValidateShouldNotAddErrorIfNullAllowed() {}

	public void testShouldSaveIfAndOnlyIfObjectIsValid() {
		citizen.setName(null);
		assertFalse(citizen.save());
		citizen.setName("Job");
		assertTrue(citizen.save());
	}

	private static boolean assertError(Error error, String attribute, Hermes object) {
		for (Error erratum : object.getErrors().get(attribute)) {
			if (erratum.getSymbol().equals(error.getSymbol())
					&& erratum.getMessage().equals(error.getMessage())) return true;
		}
		return false;
	}
}
