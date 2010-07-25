package unit.hermes;

import helpers.Database;

import java.util.Set;

import junit.framework.TestCase;
import sample.Address;
import sample.Car;
import sample.Man;
import sample.Person;
import sample.Pet;
import core.Hermes;
import factory.Factory;

public class InheritanceTest extends TestCase {

	public static Man	man;

	public void setUp() {
		man = new Man("Man", 23, new Address(9, "Avenue de Quimper"));
	}

	public void tearDown() {
		Database.clear();
	}

	public void testChildShouldHaveParentTableName() {
		assertEquals("PEOPLE", man.getTableName());
	}

	public void testSaveAndReloadAttributes() {
		assertTrue(man.saveWithoutValidation());
		man = (Man) man.reload();
		assertEquals("Man", man.getName());
		assertEquals("Avenue de Quimper", man.getAdress().getStreet());
	}

	public void testReloadHasOneAssociations() {
		man.saveWithoutValidation();
		man = (Man) man.reload();
		assertEquals("Man", man.getName());
		assertEquals("Avenue de Quimper", man.getAdress().getStreet());
	}

	public void testReloadHasManyAssociations() {
		man.setCars((Set<Car>) Factory.get("cars"));
		assertTrue(man.saveWithoutValidation());
		man = (Man) man.reload();
		assertEquals(2, man.getCars().size());
	}

	public void testReloadManyToManyAssociations() {
		man.setPets((Set<Pet>) Factory.get("pets"));
		assertTrue(man.saveWithoutValidation());
		man = (Man) man.reload();
		assertEquals(2, man.getPets().size());
	}

	public void testFind() {
		assertTrue(man.save());
		Set<Man> humans = (Set<Man>) Hermes.find("age = 23 and name='Man'", Man.class);
		assertEquals(1, humans.size());
		assertEquals("Avenue de Quimper", humans.iterator().next().getAdress().getStreet());
		humans = (Set<Man>) Hermes.find("adress.street='Avenue de Quimper'", Man.class);
		assertEquals(1, humans.size());
	}

	public void testFindWithManyToManyConditions() {
		man.setPets((Set<Pet>) Factory.get("pets"));
		assertTrue(man.save());
		Set<Man> humans = (Set<Man>) Hermes.find("pets.name='Medor'", Man.class);
		assertEquals(1, humans.size());
	}
	
	public void testFindersShouldRetrieveOnlyInstancesOfSubclass(){
		Person human = new Person("Human", 23, new Address(9, "Avenue de Quimper"));
		human.save();
		man.save();
		Set<Man> men = (Set<Man>) Hermes.all(Man.class);
		assertEquals(1,men.size());
	}
	
	public void testDelete(){
		int id = man.getId();
		assertTrue(man.save());
		assertTrue(man.delete());
		assertNull(Hermes.find(id, Man.class));
	}

}
