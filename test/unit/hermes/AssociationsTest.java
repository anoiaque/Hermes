package unit.hermes;

import factory.Factory;
import helpers.Database;
import helpers.TestHelper;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;
import sample.Person;
import sample.Pet;

public class AssociationsTest extends TestCase {

	public static Person	citizen;

	public void setUp() {
		citizen = (Person) Factory.get("human");
	}

	public void tearDown() {
		Database.clear();
	}

	public void testAssociationsBuilding() {
		assertTrue(citizen.getHasOneAssociations().containsKey("adresse"));
		assertTrue(citizen.getManyToManyAssociations().containsKey("pets"));
		assertTrue(citizen.getHasManyAssociations().containsKey("cars"));
		assertEquals(1, citizen.getHasOneAssociations().size());
		assertEquals(1, citizen.getManyToManyAssociations().size());
		assertEquals(1, citizen.getHasManyAssociations().size());
	}

	// Must not add or change a pair of key in the join_table , but just
	// update the child table
	public void testUpdateManyToManyAttributeWhenExistBefore() {
		assertEquals(2, Pet.findAll(Pet.class).size());
		citizen.getPets().iterator().next().setName("Idefix");
		citizen.save();
		assertEquals(2, Pet.findAll(Pet.class).size());
		assertEquals(2, TestHelper.jointureSizeFor(citizen, "pets"));
	}

	// Must add pair(s)of key in the join_table , and add the object(s) in
	// the child table
	public void testUpdateManyToManyAttributeWhenNullBefore() {
		assertEquals(2, Pet.findAll(Pet.class).size());
		Person p = new Person();
		p.save();
		p.setPets((Set<Pet>) Factory.get("pets"));
		p.save();
		assertEquals(4, Pet.findAll(Pet.class).size());
		assertEquals(2, TestHelper.jointureSizeFor(p, "pets"));
	}

	// Must add pair(s) of key in the join_table , and not add the object(s)
	// in the child table
	// Just make a new reference in the join table for the new person
	public void testUpdateManyToManyAttributeWhenNullBeforeAndAssignAnExistingOccurence() {
		assertEquals(2, Pet.findAll(Pet.class).size());
		Person p = new Person();
		Set<Pet> pets = new HashSet<Pet>();
		pets.add(citizen.getPets().iterator().next());
		p.setPets(pets);
		p.save();
		assertEquals(2, Pet.findAll(Pet.class).size());
		assertEquals(1, TestHelper.jointureSizeFor(p, "pets"));
	}

	// Test pairs of keys are deleted in join table when person is deleted
	public void testRefreshJoinTable() {
		citizen.delete();
		assertEquals(0, TestHelper.jointureSizeFor(citizen, "pets"));
	}

	public void testCascadeDeleteWithManyToManyAttribute() {
		citizen.getManyToManyAssociations().get("pets").setCascadeDelete(true);
		citizen.delete();
		assertNull(Pet.find(citizen.getPets().iterator().next().getId(), Pet.class));
	}

	public void testNoCascadeDeleteWithManyToManyAttribute() {
		Iterator<Pet> pets = citizen.getPets().iterator();
		Pet pet1 = (Pet) pets.next();
		Pet pet2 = (Pet) pets.next();
		citizen.getManyToManyAssociations().get("pets").setCascadeDelete(false);
		citizen.delete();
		assertNotNull(Pet.find(pet1.getId(), Pet.class));
		assertNotNull(Pet.find(pet2.getId(), Pet.class));
		assertEquals(0, TestHelper.jointureSizeFor(citizen, "pets"));
	}

	// Test if add an occurence in the set , just one is added in table rows ,
	// Other are just updated.
	// Only one more link in join table.
	public void testAddOccurenceInManyToManyAttribute() {
		citizen.getPets().add(new Pet("Hamster", "Scorpio"));
		citizen.save();
		assertEquals(3, Pet.findAll(Pet.class).size());
		citizen = (Person) Person.find(citizen.getId(), Person.class);
		assertEquals(3, citizen.getPets().size());
		assertEquals(3, TestHelper.jointureSizeFor(citizen, "pets"));
	}

	// Test if an occurence is deleted in the set , the occurence in table is
	// deleted when save has been called.
	// The pair keys is deleted in the join table
	public void testDeleteOccurenceInManyToManyAttribute() {
		Pet pet = citizen.getPets().iterator().next();
		citizen.getPets().remove(pet);
		assertEquals(1, citizen.getPets().size());
		citizen.save();
		citizen = (Person) Person.find(citizen.getId(), Person.class);
		assertEquals(1, TestHelper.jointureSizeFor(citizen, "pets"));
	}
}
