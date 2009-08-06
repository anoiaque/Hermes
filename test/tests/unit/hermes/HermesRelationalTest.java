package tests.unit.hermes;

import factory.Factory;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;
import sample.Adress;
import sample.Person;
import sample.Pet;
import helpers.Database;
import helpers.TestHelper;

public class HermesRelationalTest extends TestCase {

  public static Person marc;

  @Override
  public void setUp() {
    Database.clear();
    marc = (Person) Factory.get("marc");
  }

  // Test on relationnals fields and basics fields hashes .
  // Relational fields must only contain relationnal fields (has_one and many_to_many relations attributes)
  // Basics fields must only contain String,Integer ...
  public void testFieldsHashes() {
    HashMap<String, String> fields = marc.getDatabaseFields();
    assertTrue(fields.containsKey("age"));
    assertTrue(fields.containsKey("nom"));
    assertEquals(2, fields.size());
    assertTrue(marc.getHasOneRelationsShip().containsKey("adresse"));
    assertTrue(marc.getManyToManyRelationsShip().containsKey("pets"));
    assertEquals(1, marc.getHasOneRelationsShip().size());
    assertEquals(1, marc.getManyToManyRelationsShip().size());
  }

  // Test on the name of the jointure table in many_to_many relations
  // name must be <parent_table_name>_<child_table_name>
  public void testHasManyJointureInit() {
    assertEquals("PERSON_PET", marc.getManyToManyRelationsShip().get("pets").getJointure().getTableName());
  }

  // Test delete cascading , if person is deleted , his adress is erased too
  // with has_one and Cascade.DELETE = true
  public void testCascadeDeleteWithRelationHasOne() {
    marc.delete();
    assertNull(Adress.find(marc.getAdresse().getId(), Adress.class));
  }

  // Test delete cascading , if person is deleted , his adress is not erased
  // with has_one and Cascade.DELETE = false
  public void testNoCascadeDeleteWithRelationHasOne() {
    marc.getHasOneRelationsShip().get("adresse").setCascadeDelete(false);
    marc.delete();
    assertNotNull(Adress.find(marc.getAdresse().getId(), Adress.class));
  }

  // Test , if a new person has the same adress , the adress must not be duplicated in the adress table
  // Only the foreign key id for person must be updated
  public void testTwoPeopleWithSameAdress() {
    Person p = (Person) Factory.get("jean");
    p.setAdresse(marc.getAdresse());
    p.save();
    assertEquals(1, Adress.find("*", "rue='rue Tabarly'", Adress.class).size());

    p.setAdresse(null);
    p = (Person) Person.find(p.getId(), Person.class);
    assertEquals("rue Tabarly", p.getAdresse().getRue());
  }

  // Test on update in a many_to_many relation when the many_to_many field reference existed before (not null)
  // Must so not add or change a pair of key in the join_table , but just update the child table
  public void testUpdateManyToManyRelationWhenExistBefore() {
    assertEquals(2, Pet.findAll(Pet.class).size());
    marc.getPets().iterator().next().setName("Idefix");
    marc.save();
    assertEquals(2, Pet.findAll(Pet.class).size());
    assertEquals(2, TestHelper.jointureSizeFor(marc, "pets"));
  }

  // Test on update in a many_to_many relation when the many_to_many field reference not exist before (null)
  // Must so add pair (s)of key in the join_table , and add the object(s) in the child table
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

  // Test on update in a many_to_many relation when the many_to_many field reference not exist before (null)
  // And assign an occurence (here pets) that already exist (here assign a pet of marc)
  // Must so  add pair (s)of key in the join_table , and not add the object(s) in the child table
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
  // If person is deleted , his pets must also be erased with Cascase.DELETE=true
  public void testCascadeDeleteWithRelationManyToMany() {
    marc.getManyToManyRelationsShip().get("pets").setCascadeDelete(true);
    marc.delete();
    assertNull(Pet.find(marc.getPets().iterator().next().getId(), Pet.class));
  }

  // Test on delete cascading with many_to_many relations .
  // If person is deleted , his pets must not be erased with Cascase.DELETE=false
  // Pairs of keys in join table must  be deleted
  public void testNoCascadeDeleteWithRelationManyToMany() {
    Iterator pets = marc.getPets().iterator();
    Pet pet1 = (Pet) pets.next();
    Pet pet2 = (Pet) pets.next();
    marc.getManyToManyRelationsShip().get("pets").setCascadeDelete(false);
    marc.delete();
    assertNotNull(Pet.find(pet1.getId(), Pet.class));
    assertNotNull(Pet.find(pet2.getId(), Pet.class));
    assertEquals(0, TestHelper.jointureSizeFor(marc, "pets"));
  }

  // Test if add an occurence in the set , just one is added in table rows , other are just updated.
  // Only one more link in join table.
  public void testAddOccurenceInManyToManyRelationSet() {
    marc.getPets().add(new Pet("Hamster", "Scorpio"));
    marc.save();
    assertEquals(3, Pet.findAll(Pet.class).size());
    marc = (Person) Person.find(marc.getId(), Person.class);
    assertEquals(3, marc.getPets().size());
    assertEquals(3, TestHelper.jointureSizeFor(marc, "pets"));
  }

  // Test if an occurence is deleted in the set , the occurence in table is deleted when save has been called.
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
