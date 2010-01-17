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

public class HermesRelationalTest extends TestCase {

	public static Person marc;

	public void setUp() {
		Database.clear();
		marc = (Person) Factory.get("marc");
	}

	public void testAssociationsBuilding() {
		assertTrue(marc.getHasOneAssociations().containsKey("adresse"));
		assertTrue(marc.getManyToManyAssociations().containsKey("pets"));
		assertTrue(marc.getHasManyRelationsShip().containsKey("cars"));

		assertEquals(1, marc.getHasOneAssociations().size());
		assertEquals(1, marc.getManyToManyAssociations().size());
		assertEquals(1, marc.getHasManyRelationsShip().size());
	}


	// Test on update in a many_to_many relation when the many_to_many field
	// reference existed before (not null)
	// Must so not add or change a pair of key in the join_table , but just
	// update the child table
	public void testUpdateManyToManyRelationWhenExistBefore() {
		assertEquals(2, Pet.findAll(Pet.class).size());
		marc.getPets().iterator().next().setName("Idefix");
		marc.save();
		assertEquals(2, Pet.findAll(Pet.class).size());
		assertEquals(2, TestHelper.jointureSizeFor(marc, "pets"));
	}

	// Test on update in a many_to_many relation when the many_to_many field
	// reference not exist before (null)
	// Must so add pair (s)of key in the join_table , and add the object(s) in
	// the child table
	public void testUpdateManyToManyRelationWhenNullBefore() {
		assertEquals(2, Pet.findAll(Pet.class).size());
		Person p = new Person();
		p.save();
		assertEquals(2, Pet.findAll(Pet.class).size());

		p.setPets((Set<Pet>) Factory.get("pets"));
		p.save();
		assertEquals(4, Pet.findAll(Pet.class).size());
		assertEquals(2, TestHelper.jointureSizeFor(p, "pets"));
	}

	// Test on update in a many_to_many relation when the many_to_many field
	// reference not exist before (null)
	// And assign an occurence (here pets) that already exist (here assign a pet
	// of marc)
	// Must so add pair (s)of key in the join_table , and not add the object(s)
	// in the child table
	// Just make a new reference in the join table for the new person
	public void testUpdateManyToManyRelationWhenNullBeforeAndAssignAnExistingOccurence() {
		assertEquals(2, Pet.findAll(Pet.class).size());
		Person p = new Person();
		Set<Pet> pets = new HashSet<Pet>();
		pets.add(marc.getPets().iterator().next());
		p.setPets(pets);
		p.save();
		assertEquals(2, Pet.findAll(Pet.class).size());
		assertEquals(1, TestHelper.jointureSizeFor(p, "pets"));
	}

	// Test pairs of keys are deleted in join table when person is deleted
	public void testRefreshJoinTable() {
		marc.delete();
		assertEquals(0, TestHelper.jointureSizeFor(marc, "pets"));
	}

	// Test on delete cascading with many_to_many relations .
	// If person is deleted , his pets must also be erased with
	// Cascase.DELETE=true
	public void testCascadeDeleteWithRelationManyToMany() {
		marc.getManyToManyAssociations().get("pets").setCascadeDelete(true);
		marc.delete();
		assertNull(Pet
				.find(marc.getPets().iterator().next().getId(), Pet.class));
	}

	// Test on delete cascading with many_to_many relations .
	// If person is deleted , his pets must not be erased with
	// Cascase.DELETE=false
	// Pairs of keys in join table must be deleted
	public void testNoCascadeDeleteWithRelationManyToMany() {
		Iterator<Pet> pets = marc.getPets().iterator();
		Pet pet1 = (Pet) pets.next();
		Pet pet2 = (Pet) pets.next();
		marc.getManyToManyAssociations().get("pets").setCascadeDelete(false);
		marc.delete();
		assertNotNull(Pet.find(pet1.getId(), Pet.class));
		assertNotNull(Pet.find(pet2.getId(), Pet.class));
		assertEquals(0, TestHelper.jointureSizeFor(marc, "pets"));
	}

	// Test if add an occurence in the set , just one is added in table rows ,
	// other are just updated.
	// Only one more link in join table.
	public void testAddOccurenceInManyToManyRelationSet() {
		marc.getPets().add(new Pet("Hamster", "Scorpio"));
		marc.save();
		assertEquals(3, Pet.findAll(Pet.class).size());
		marc = (Person) Person.find(marc.getId(), Person.class);
		assertEquals(3, marc.getPets().size());
		assertEquals(3, TestHelper.jointureSizeFor(marc, "pets"));
	}

	// Test if an occurence is deleted in the set , the occurence in table is
	// deleted when save has been called.
	// The pair keys is deleted in the join table
	public void testDeleteOccurenceInManyToManyRelationSet() {
		Pet pet = marc.getPets().iterator().next();
		marc.getPets().remove(pet);
		assertEquals(1, marc.getPets().size());
		marc.save();
		marc = (Person) Person.find(marc.getId(), Person.class);
		assertEquals(1, TestHelper.jointureSizeFor(marc, "pets"));

	}
}
