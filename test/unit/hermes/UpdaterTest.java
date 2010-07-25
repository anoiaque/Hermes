package unit.hermes;

import java.util.HashMap;

import helpers.Database;
import junit.framework.TestCase;
import sample.Address;
import sample.Man;
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
		Person person = new Person("citizen kane", 90);
		person.setAdress(new Address(10, "rue de Lannion"));
		person.setName("Anne");
		person.setAge(30);
		person.save();
		person = (Person) person.reload();
		assertEquals("Anne", person.getName());
		assertEquals(30, person.getAge());
	}

	public void testCreate() {
		HashMap<String, Object> attributes = new HashMap<String, Object>();
		attributes.put("name", "Paul");
		attributes.put("adress", new Address());
		attributes.put("age", 30);
		Person paul = (Person) Hermes.create(attributes, Person.class);

		assertNotNull(paul);
		assertEquals("Paul", paul.getName());
		assertEquals(30, paul.getAge());
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

	public void testDeleteAll() {
		Factory.get("human");
		assertEquals(2, Hermes.count(Person.class));
		Hermes.deleteAll(Person.class);
		assertEquals(0, Hermes.count(Person.class));
	}

	public void testExecuteSql() {
		Hermes.execute("update people set name = 'joe' where age = 30");
		human = (Person) human.reload();
		assertEquals("joe", human.getName());
		Hermes.execute("insert into people(age) values(34)");
		assertEquals(2, Hermes.all(Person.class).size());
	}

	public void testShouldAddKlassAttributeForSTIModels() {
		Man man = new Man();
		assertEquals(null, human.getAttribute("klass"));
		assertEquals("klass", man.getAttribute("klass").getName());
		assertEquals("Man", man.getAttribute("klass").getValue());
	}

	public void testShouldSaveKlassIfSTIInstance() {
		Man man = new Man("Man", 23, new Address());

		assertTrue(man.save());
		man = (Man) man.reload();
		assertEquals("Man", man.getAttribute("klass").getValue());
	}

}
