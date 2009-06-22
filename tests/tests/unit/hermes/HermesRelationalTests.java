package tests.unit.hermes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;
import sample.Adress;
import sample.Person;
import sample.Pet;

public class HermesRelationalTests extends TestCase {

    public static Person person;

    public void setUp() {
        person = new Person();
        person.setAge(30);
        person.setNom("toto");
    }

    public void tearDown() {
        person.delete();
    }

    public void testRelationFieldsNotInBasicFields() {
        HashMap<String, String> fields = person.getDatabaseFields();
        assertFalse(fields.containsKey("adresse"));
        assertFalse(fields.containsKey("pets"));
    }

    public void testRelationConfig() {
        assertTrue(person.getHasOneRelationsShip().containsKey("adresse"));
        assertTrue(person.getHasManyRelationsShip().containsKey("pets"));
    }

    public void testSaveRelationHasOne() {
        person.setAdresse(new Adress(13, "rue Tabarly"));
        person.save();
        person.setAdresse(new Adress(12, "rue des hibous"));
        person.find(person.getId());
        assertEquals(13, person.getAdresse().getNumero());
        assertEquals("rue Tabarly", person.getAdresse().getRue());
    }

    public void testCascadeDeleteWithRelationHasOne() {
        person.setAdresse(new Adress(11, "rue Tabarly"));
        person.save();
        person.delete();
        assertFalse(new Adress().find(person.getAdresse().getId()));
    }

    public void testHasManyJointureInit() {
        assertEquals("PERSON_PET", person.getHasManyRelationsShip().get("pets").getJointure().getTableName());
        person.delete();
    }

    public void testSaveRelationHasMany() {
        person.setAge(10);
        person.setNom("Toto");
        Set<Pet> pets = new HashSet<Pet>();
        pets.add(new Pet("Chien", "Medor"));
        pets.add(new Pet("Chat", "Felix"));
        person.setPets(pets);
        person.save();
        person.setPets(new HashSet<Pet>());
        person.find(person.getId());
        Iterator<Pet> iterator = person.getPets().iterator();
        Pet pet1 = iterator.next();
        Pet pet2 = iterator.next();
        assertTrue((pet1.getType().equals("Chien") && pet2.getType().equals("Chat")) || (pet2.getType().equals("Chien") && pet1.getType().equals("Chat")));
        assertTrue((pet1.getName().equals("Medor") && pet2.getName().equals("Felix")) || (pet2.getName().equals("Medor") && pet1.getName().equals("Felix")));
        person.delete();
    }

    public void testCascadeDeleteWithRelationHasMany() {
        Set<Pet> pets = new HashSet<Pet>();
        pets.add(new Pet("Chien", "Medor"));
        pets.add(new Pet("Chat", "Felix"));
        person.setPets(pets);
        person.save();
        person.delete();
        assertFalse(new Pet().find(person.getPets().iterator().next().getId()));
    }
}
