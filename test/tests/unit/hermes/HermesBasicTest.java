package tests.unit.hermes;

import java.util.HashMap;

import junit.framework.TestCase;
import sample.Adress;
import sample.Person;
import configuration.Configuration;
import core.Hermes;
import core.Pluralizer;
import factory.Factory;
import helpers.Database;

public class HermesBasicTest extends TestCase {

  public static Person marc;

  @Override
  public void setUp() {
    Database.clear();
    marc = (Person) Factory.get("marc");
  }

  // Test the name of table , function of the class name pluralized
  public void testNomTable() {
    assertEquals(Pluralizer.getPlurial("Person"), marc.getTableName());
    assertEquals(Pluralizer.getPlurial("Person"), Hermes.tableName(Person.class));
  }

  // Test id obtain by get_generated_keys from database
  public void testId() {
    Person person2 = new Person();
    person2.save();
    assertEquals(marc.getId() + 1, person2.getId());
  }

  // Test basics fields , type and name are right
  public void testBasicsFields() {
    HashMap<String, String> fields = marc.getDatabaseFields();
    assertTrue(fields.containsKey("age"));
    assertEquals("int", fields.get("age"));
    assertTrue(fields.containsKey("nom"));
    assertEquals("varchar(" + Configuration.SqlConverterConfig.varcharLength + ")", fields.get("nom"));
  }

  // Test raw is deleted in database
  public void testDelete() {
    marc.delete();
    assertNull(Person.find(marc.getId(), Person.class));
  }

  // Test update . Ensure no new record created but the one is updated
  public void testUpdate() {
    assertEquals(1, Person.findAll(Person.class).size());
    marc.setNom("titi");
    marc.setAdresse(new Adress(25, "rue de Brest"));
    marc.save();
    assertEquals(1, Person.findAll(Person.class).size());
    marc = (Person) Person.find(marc.getId(),Person.class);
    assertEquals(30, marc.getAge());
    assertEquals("titi", marc.getNom());
    assertEquals(25, marc.getAdresse().getNumero());
    assertEquals("rue de Brest", marc.getAdresse().getRue());
  }
}
