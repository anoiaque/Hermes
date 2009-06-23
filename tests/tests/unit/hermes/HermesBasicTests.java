package tests.unit.hermes;

import java.util.HashMap;

import junit.framework.TestCase;
import sample.Adress;
import sample.Person;
import configuration.Configuration;

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

    public void testId() {
        person.save();
        Person person2 = new Person();
        person2.save();
        assertEquals(person.getId() + 1, person2.getId());
        person2.delete();
    }

    public void testFields() {
        HashMap<String, String> fields = person.getDatabaseFields();
        assertTrue(fields.containsKey("age"));
        assertEquals("int", fields.get("age"));
        assertTrue(fields.containsKey("nom"));
        assertEquals("varchar(" + Configuration.SqlConverterConfig.varcharLength + ")", fields.get("nom"));
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

    public void testRetrieveIdWithFindWhereClause() {
        person.save();
        int id = person.getId();
        person.setId(-1);
        person = (Person) person.find("*", "age=10").iterator().next();
        assertEquals(id, person.getId());
    }

    public void testGetAllWithFindWherClause() {
        Person person2 = new Person();
        person2.setAge(10);
        person2.setNom("titi");
        person2.save();
        person.save();
        assertEquals(2, person.find("*", "age=10").size());
        person2.delete();
    }

    public void testUpdate() {
        person.save();
        person.setAge(30);
        person.setNom("titi");
        person.setAdresse(new Adress(25, "rue de Brest"));
        person.save();
        person.setAge(0);
        person.find(person.getId());
        assertEquals(30, person.getAge());
        assertEquals("titi", person.getNom());
        assertEquals(25, person.getAdresse().getNumero());
        assertEquals("rue de Brest", person.getAdresse().getRue());
    }
}
