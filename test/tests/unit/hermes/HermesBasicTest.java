package tests.unit.hermes;

import java.util.HashMap;

import junit.framework.TestCase;
import sample.Adress;
import sample.Person;
import configuration.Configuration;
import core.Pluralizer;
import util.Database;

public class HermesBasicTest extends TestCase {

  public static Person person;

  @Override
  public void setUp() {
    Database.clear();
    person = new Person();
    person.setAge(10);
    person.setNom("toto");
    person.save();
  }

  @Override
  public void tearDown() {
    person.delete();
  }

  // Test the name of table , function of the class name pluralized
  public void testNomTable() {
    assertEquals(Pluralizer.getPlurial("Person"), person.getTableName());
  }

  // Test id obtain by get_generated_keys from database
  public void testId() {
    Person person2 = new Person();
    person2.save();
    assertEquals(person.getId() + 1, person2.getId());
    person2.delete();
  }

  // Test basics fields , type and name are right
  public void testBasicsFields() {
    HashMap<String, String> fields = person.getDatabaseFields();
    assertTrue(fields.containsKey("age"));
    assertEquals("int", fields.get("age"));
    assertTrue(fields.containsKey("nom"));
    assertEquals("varchar(" + Configuration.SqlConverterConfig.varcharLength + ")", fields.get("nom"));
  }

  // Test retrieve data with find(id) after save
  public void testRetrieveDataWithFind() {
    person.setAge(0);
    person.setNom("");
    person.find(person.getId());
    assertEquals(10, person.getAge());
    assertEquals("toto", person.getNom());
  }

  // Test retrieve data with find(where_clause) after save
  public void testRetrieveIdWithFindWhereClause() {
    int id = person.getId();
    person.setId(-1);
    person = (Person) person.find("*", "age=10").iterator().next();
    assertEquals(id, person.getId());
  }

  // Test raw is deleted in database
  public void testDelete() {
    person.delete();
    assertFalse(person.find(person.getId()));
  }

  // Test retrieve all occurences with find(where_clause)
  public void testGetAllWithFindWherClause() {
    Person person2 = new Person();
    person2.setAge(10);
    person2.setNom("titi");
    person2.save();
    assertEquals(2, person.find("*", "age=10").size());
    person2.delete();
  }

  // Test update . Ensure no new record created but the one is updated
  public void testUpdate() {
    assertEquals(1, person.find("*", "id>0").size());
    person.setAge(30);
    person.setNom("titi");
    person.setAdresse(new Adress(25, "rue de Brest"));
    person.save();
    assertEquals(1, person.find("*", "id>0").size());
    person.setAge(0);
    person.find(person.getId());
    assertEquals(30, person.getAge());
    assertEquals("titi", person.getNom());
    assertEquals(25, person.getAdresse().getNumero());
    assertEquals("rue de Brest", person.getAdresse().getRue());
  }
}
