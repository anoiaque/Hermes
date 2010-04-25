package unit.hermes;

import helpers.Database;
import junit.framework.TestCase;
import sample.Address;
import sample.Person;
import core.Hermes;
import factory.Factory;

public class UpdaterTest extends TestCase {

	public static Person	human;

	public void setUp() {
		human = (Person) Factory.get("human");
	}

	public void tearDown() {
		Database.clear();
	}

	public void testSave() {
		Person person = new Person("citizen kane",90);
		person.setAdress(new Address(10, "rue de Lannion"));
		person.setName("Anne");
		person.setAge(30);
		person.save();
		person = (Person) person.reload();
		assertEquals("Anne", person.getName());
		assertEquals(30, person.getAge());
	}

	public void testUpdate() {
		human.setName("joe");
		human.save();
		human = (Person) human.reload();
		assertEquals("joe", human.getName());
	}

	public void testDelete() {
		human.delete();
		assertNull(Person.find(human.getId(), Person.class));
	}
	
	public void testExecuteSql(){
		Hermes.execute("update people set name = 'joe' where age = 30");
		human = (Person) human.reload();
		assertEquals("joe", human.getName());
		Hermes.execute("insert into people(age) values(34)");
		assertEquals(2, Hermes.findAll(Person.class).size());
		
	}

}
