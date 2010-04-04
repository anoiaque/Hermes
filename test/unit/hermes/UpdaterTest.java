package unit.hermes;

import core.Hermes;
import factory.Factory;
import helpers.Database;
import junit.framework.TestCase;
import sample.Person;

public class UpdaterTest extends TestCase {

	public static Person	human;

	public void setUp() {
		human = (Person) Factory.get("human");
	}

	public void tearDown() {
		Database.clear();
	}

	public void testSave() {
		Person person = new Person();
		
		person.setNom("Anne");
		person.setAge(30);
		person.save();
		person = (Person) person.reload();
		assertEquals("Anne", person.getNom());
		assertEquals(30, person.getAge());
	}

	public void testUpdate() {
		human.setNom("joe");
		human.save();
		human = (Person) human.reload();
		assertEquals("joe", human.getNom());
	}

	public void testDelete() {
		human.delete();
		assertNull(Person.find(human.getId(), Person.class));
	}
	
	public void testExecuteSql(){
		Hermes.execute("update people set nom = 'joe' where age = 30");
		human = (Person) human.reload();
		assertEquals("joe", human.getNom());
		Hermes.execute("insert into people(age) values(34)");
		assertEquals(2, Hermes.findAll(Person.class).size());
		
	}

}
