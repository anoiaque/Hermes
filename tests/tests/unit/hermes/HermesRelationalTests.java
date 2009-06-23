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

    public void testRelationalFields() {
        assertTrue(person.getHasOneRelationsShip().containsKey("adresse"));
        assertTrue(person.getHasManyRelationsShip().containsKey("pets"));
    }

    public void testSaveAndFindRelationHasOne() {
        person.setAdresse(new Adress(13, "rue Tabarly"));
        person.save();
        person.setAdresse(new Adress(12, "rue des hibous"));
        person.find(person.getId());
        assertEquals(13, person.getAdresse().getNumero());
        assertEquals("rue Tabarly", person.getAdresse().getRue());
    }

    public void testSaveAndFindWithWhereClauseRelationHasOne() {
        person.setAdresse(new Adress(13, "rue Tabarly"));
        person.save();
        person.setAdresse(new Adress(12, "rue des hibous"));
        person = (Person) person.find("*", "age=30").iterator().next();
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
    }

    public void testSaveAndFindRelationHasMany() {
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
    }

    public void testSaveAndFindWithWhereClauseRelationHasMany() {
        person.setAge(10);
        person.setNom("Toto");
        Set<Pet> pets = new HashSet<Pet>();
        pets.add(new Pet("Chien", "Medor"));
        pets.add(new Pet("Chat", "Felix"));
        person.setPets(pets);
        person.save();
        person.setPets(new HashSet<Pet>());
        person = (Person) person.find("*", "age=10").iterator().next();
        Iterator<Pet> iterator = person.getPets().iterator();
        Pet pet1 = iterator.next();
        Pet pet2 = iterator.next();
        assertTrue((pet1.getType().equals("Chien") && pet2.getType().equals("Chat")) || (pet2.getType().equals("Chien") && pet1.getType().equals("Chat")));
        assertTrue((pet1.getName().equals("Medor") && pet2.getName().equals("Felix")) || (pet2.getName().equals("Medor") && pet1.getName().equals("Felix")));
    }

    public void testUpdateRelationsHasManyExistBefore() {
        Set<Pet> pets = new HashSet<Pet>();
        pets.add(new Pet("Chien", "Medor"));
        person.setPets(pets);
        person.save();
        person.getPets().iterator().next().setName("Idefix");
        person.save();
        person = (Person) person.find("*", "age=30").iterator().next();
        Pet pet = person.getPets().iterator().next();
        assertEquals("Idefix", pet.getName());
    }

    public void testUpdateRelationsHasManyNullBefore() {
        Set<Pet> pets = new HashSet<Pet>();
        person.save();
        pets.add(new Pet("Chien", "Idefix"));
        pets.add(new Pet("Chat", "Merlin"));
        person.setPets(pets);
        person.save();
        person = (Person) person.find("*", "age=30").iterator().next();
        Iterator<Pet> iterator = person.getPets().iterator();
        Pet pet1 = iterator.next();
        Pet pet2 = iterator.next();
        assertTrue((pet1.getType().equals("Chien") && pet2.getType().equals("Chat")) || (pet2.getType().equals("Chien") && pet1.getType().equals("Chat")));
        assertTrue((pet1.getName().equals("Idefix") && pet2.getName().equals("Merlin")) || (pet2.getName().equals("Idefix") && pet1.getName().equals("Merlin")));
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
