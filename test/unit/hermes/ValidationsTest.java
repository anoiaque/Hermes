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
		assertTrue(hasError(new Error(Error.Symbol.PRESENCE), "name", citizen));
		citizen.setName("Job");
		assertTrue(citizen.isValid());
	}

	public void testValidateWithOwnMessage() {
		Personne citizen = new Personne();
		Error error = new Error(Error.Symbol.PRESENCE, "name must be present yes!");
		assertFalse(citizen.isValid());
		assertTrue(hasError(error, "name", citizen));
		citizen.setName("Job");
		assertTrue(citizen.isValid());
	}

	public void testValidateSize() {
		citizen.setName("veryverylongname");
		assertFalse(citizen.isValid());
		assertTrue(hasError(new Error(Error.Symbol.SIZE), "name", citizen));
		citizen.setName("Job");
		assertTrue(citizen.isValid());
	}

	public void testValidationOnAssociation() {
		citizen.setAdress(null);
		assertFalse(citizen.isValid());
		assertTrue(hasError(new Error(Error.Symbol.PRESENCE), "adress", citizen));
	}

	public void testValidateShouldNotAddErrorIfNullAllowed() {
		citizen.setPhone(null);
		assertFalse(hasError(new Error(Error.Symbol.SIZE), "phone", citizen));
	}

	public void testShouldSaveIfAndOnlyIfObjectIsValid() {
		citizen.setName(null);

		assertFalse(citizen.save());
		citizen.setName("Job");
		assertTrue(citizen.save());
	}

	public void testUniquenessValidation() {
		Person human = new Person("Job", 90);
		human.setAdress(new Adress());
		human.setPhone("0678453676");
		citizen.setPhone("0678453676");
		citizen.save();
		assertFalse(human.isValid());
		assertTrue(hasError(new Error(Error.Symbol.UNIQUENESS), "phone", human));
		assertFalse(human.save());
		citizen.setPhone("0678453675");
		citizen.save();
		assertTrue(human.isValid());
	}

	public void testFormatValidation() {
		citizen.setPhone("0678AZER");
		assertFalse(citizen.isValid());
		assertTrue(hasError(new Error(Error.Symbol.FORMAT), "phone", citizen));
		citizen.setPhone("0678453676");
		assertTrue(citizen.isValid());
	}

	private static boolean hasError(Error error, String attribute, Hermes object) {
		if (object.getErrors().isEmpty()) return false;
		if (object.getErrors().get(attribute) == null) return false;
		for (Error erratum : object.getErrors().get(attribute)) {
			if (erratum.getSymbol().equals(error.getSymbol())
					&& erratum.getMessage().equals(error.getMessage())) return true;
		}
		return false;
	}
}
