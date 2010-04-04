package unit.hermes;

import helpers.Database;
import junit.framework.TestCase;
import sample.Person;
import core.Transaction;
import factory.Factory;

public class TransactionTest extends TestCase {

	public static Person	human;

	public void setUp() {
		human = (Person) Factory.get("human");
	}

	public void tearDown() {
		Database.clear();
	}

	public void testRollbackIfOneStatementFail() {
		testRollbackWith("insert into people(unknown_field) value('value')");
	}

	public void testRollbackWithFailureOnDelete() {
		testRollbackWith("delete from people where addresse = 10");
	}

	private void testRollbackWith(String sql) {
		int age = human.getAge();
		Transaction transaction = new Transaction();

		transaction.begin();
		human.setAge(12);
		transaction.save(human);
		transaction.execute(sql);
		transaction.end();

		human = (Person) human.reload();
		assertEquals(age, human.getAge());

	}

}
