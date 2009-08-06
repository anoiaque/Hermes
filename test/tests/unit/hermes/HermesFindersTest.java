package tests.unit.hermes;

import factory.Factory;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;
import sample.Adress;
import sample.Person;
import sample.Pet;
import helpers.Database;
import helpers.TestHelper;

public class HermesFindersTest extends TestCase {

  public static Person marc, jean;

  @Override
  public void setUp() {
    Database.clear();
    marc = (Person) Factory.get("marc");
    jean = (Person) Factory.get("jean");
  }

  // Test Find by id
  public void testFindById() {
    marc.setAge(0);
    marc.setNom("");
    marc = (Person) Person.find(marc.getId(), Person.class);
    assertEquals(30, marc.getAge());
    assertEquals("Marc", marc.getNom());
  }

  // Test Find with conditions
  public void testFindWithConditions() {
    int id = marc.getId();
    marc.setId(-1);
    marc = (Person) Person.find("age = 30", Person.class).iterator().next();
    assertEquals(id, marc.getId());
  }

  // Test find object(s) with conditions on child's attributes
  // For child of has_one relationship
  public void testFindWithConditionsOnChildAttributes() {
    Set<Person> people = (Set<Person>) Person.find("adresse.numero = 13", Person.class);
    assertEquals(1, people.size());
    jean.getAdresse().setNumero(13);
    jean.save();
    people = (Set<Person>) Person.find("adresse.numero = 13", Person.class);
    assertEquals(2, people.size());
    people = (Set<Person>) Person.find("adresse.rue = 'rue Kervegan'", Person.class);
    assertEquals(1, people.size());
  }

  // Test find with many to many relations
  public void testFindWithManyToManyRelation() {
    Person newMarc = (Person) Person.find(marc.getId(), Person.class);
    (new TestHelper()).assertMarcRetrieveHisPets(newMarc);

  }

  // Test find with conditions with many to many relations
  public void testFindWithConditionsOnManyToManyRelation() {
    Person newMarc = (Person) Person.find("age = 30", Person.class).iterator().next();
    (new TestHelper()).assertMarcRetrieveHisPets(newMarc);
  }

  // Test find with static method (for external use) and on an instance(used in the model class)
  public void testStaticAndOnInstanceFindersSameResult() {
    Person clone1 = (Person) marc.find(marc.getId());
    Person clone2 = (Person) Person.find(marc.getId(), Person.class);
    assertEquals(clone1.getId(), clone2.getId());
  }
}
