package tests.unit.hermes;

import java.util.HashMap;

import configuration.Configuration;

import junit.framework.TestCase;
import sample.Person;

public class HermesBasicTests extends TestCase {

    public static Person person;

    public void setUp() {
        person = new Person();
        person.setAge(10);
        person.setNom("toto");
    }

    public void tearDown() {
      person.delete();
    }

    public void testNomTable() {
        assertEquals("Person", person.getTableName());
    }

    public void testFields() {
        HashMap<String, String> fields = person.getDatabaseFields();
        assertTrue(fields.containsKey("age"));
        assertEquals("int", fields.get("age"));
        assertTrue(fields.containsKey("nom"));
        assertEquals("varchar("+Configuration.SqlConverterConfig.varcharLength+")", fields.get("nom"));
    }

    public void testSaveAndFind() {
        person.save();
        person.setAge(0);
        person.setNom("");
        person.find(person.getId());
        assertEquals(10, person.getAge());
        assertEquals("toto", person.getNom());
    }

    public void testDelete() {
        person.save();
        person.delete();
        assertFalse(person.find(person.getId()));
    }

    public void testId() {
        person.save();
        Person person2 = new Person();
        person2.save();
        assertEquals(person.getId() + 1, person2.getId());
        person2.delete();
    }

    public void testRetrieveIdWithFindWhereClause() {
        person.save();
        int id = person.getId();
        person.setId(-1);
        person = (Person) person.find("*", "age=10").iterator().next();
        assertEquals(id, person.getId());
    }

   
}
