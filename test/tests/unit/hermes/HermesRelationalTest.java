package tests.unit.hermes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;
import sample.Adress;
import sample.Person;
import sample.Pet;

public class HermesRelationalTest extends TestCase {

    public static Person person;

    @Override
    public void setUp() {
        person = new Person();
        person.setAge(30);
        person.setNom("toto");
        person.setAdresse(new Adress(13, "rue Tabarly"));
        Set<Pet> pets = new HashSet<Pet>();
        pets.add(new Pet("Chien", "Medor"));
        pets.add(new Pet("Chat", "Felix"));
        person.setPets(pets);
        person.save();
    }

    @Override
    public void tearDown() {
        person.delete();
    }

    // Test on relationnals fields and basics fields hashes .
    // Relational fields must only contain relationnal fields (has_one and many_to_many relations attributes)
    // Basics fields must only contain String,Integer ...
    public void testFieldsHashes() {
        HashMap<String, String> fields = person.getDatabaseFields();
        assertTrue(fields.containsKey("age"));
        assertTrue(fields.containsKey("nom"));
        assertEquals(2, fields.size());
        assertTrue(person.getHasOneRelationsShip().containsKey("adresse"));
        assertTrue(person.getManyToManyRelationsShip().containsKey("pets"));
        assertEquals(1, person.getHasOneRelationsShip().size());
        assertEquals(1, person.getManyToManyRelationsShip().size());
    }

    // Test on relation Has-One : retrieve data with find(id) after saving it.
    public void testRetrieveDataById() {
        person.setAdresse(new Adress(12, "rue des hibous"));
        person.find(person.getId());
        assertEquals(13, person.getAdresse().getNumero());
        assertEquals("rue Tabarly", person.getAdresse().getRue());
    }

    // Test on relation Has-One : retrieve data with find(where_clause) after saving it.
    public void testRetrieveDataByWhereClause() {
        person.setAdresse(new Adress(12, "rue des hibous"));
        person = (Person) person.find("*", "age=30").iterator().next();
        assertEquals(13, person.getAdresse().getNumero());
        assertEquals("rue Tabarly", person.getAdresse().getRue());
    }

    // Test delete cascading , if person is deleted , his adress is erased too
    // with has_one and Cascade.DELETE = true
    public void testCascadeDeleteWithRelationHasOne() {
        person.delete();
        assertFalse(new Adress().find(person.getAdresse().getId()));
    }

    // Test delete cascading , if person is deleted , his adress is not erased
    // with has_one and Cascade.DELETE = false
    public void testNoCascadeDeleteWithRelationHasOne() {
        person.getHasOneRelationsShip().get("adresse").setCascadeDelete(false);
        person.delete();
        assertTrue(new Adress().find(person.getAdresse().getId()));
        person.getHasOneRelationsShip().get("adresse").setCascadeDelete(true);
    }

    // Test , if a new person has the same adress , the adress must not be duplicated in the adress table
    // Only the foreign key id must be updated
    public void testTwoPersonsWithSameAdress() {
        Person p = new Person();
        p.setAdresse(person.getAdresse());
        p.save();
        assertEquals(1, (new Adress()).find("*", "rue='rue Tabarly'").size());
        p.setAdresse(null);
        p.find(p.getId());
        assertEquals("rue Tabarly", p.getAdresse().getRue());
        p.delete();
    }

    // Test on the name of the jointure table in many_to_many relations
    // name must be <parent_table_name>_<child_table_name>
    public void testHasManyJointureInit() {
        assertEquals("PERSON_PET", person.getManyToManyRelationsShip().get("pets").getJointure().getTableName());
    }

    // Test on retrieve data with finf(id) after saving many_to_many relation
    public void testRetrieveDataByIdWithHasMany() {
        person.setPets(new HashSet<Pet>());
        person.find(person.getId());
        Iterator<Pet> iterator = person.getPets().iterator();
        Pet pet1 = iterator.next();
        Pet pet2 = iterator.next();
        assertTrue((pet1.getType().equals("Chien") && pet2.getType().equals("Chat")) || (pet2.getType().equals("Chien") && pet1.getType().equals("Chat")));
        assertTrue((pet1.getName().equals("Medor") && pet2.getName().equals("Felix")) || (pet2.getName().equals("Medor") && pet1.getName().equals("Felix")));
    }

    // Test on retrieve data with finf(where_clause) after saving many_to_many relation
    public void testRetrieveDataByWhereClauseWithHasManyRelations() {
        person = (Person) person.find("*", "age=30").iterator().next();
        Iterator<Pet> iterator = person.getPets().iterator();
        Pet pet1 = iterator.next();
        Pet pet2 = iterator.next();
        assertTrue((pet1.getType().equals("Chien") && pet2.getType().equals("Chat")) || (pet2.getType().equals("Chien") && pet1.getType().equals("Chat")));
        assertTrue((pet1.getName().equals("Medor") && pet2.getName().equals("Felix")) || (pet2.getName().equals("Medor") && pet1.getName().equals("Felix")));
    }

    // Test on update in a many_to_many relation when the many_to_many field reference existed before (not null)
    // Must so not add or change a pair of key in the join_table , but just update the child table
    public void testUpdateHasManyWhenExistBefore() {
        assertEquals(2, (new Pet().find("*", "id>0")).size());
        for (Pet pet : person.getPets())
            if (pet.getType().equals("Chien"))
                pet.setName("Idefix");
        person.save();
        assertEquals(2, (new Pet().find("*", "id>0")).size());
        person = (Person) person.find("*", "age=30").iterator().next();
        Iterator<Pet> iterator = person.getPets().iterator();
        Pet pet1 = iterator.next();
        Pet pet2 = iterator.next();
        assertTrue((pet1.getType().equals("Chien") && pet2.getType().equals("Chat")) || (pet2.getType().equals("Chien") && pet1.getType().equals("Chat")));
        assertTrue((pet1.getName().equals("Idefix") && pet2.getName().equals("Felix")) || (pet2.getName().equals("Idefix") && pet1.getName().equals("Felix")));
    }

    // Test on update in a many_to_many relation when the many_to_many field reference not exist before (null)
    // Must so add pair (s)of key in the join_table , and add the object(s) in the child table
    public void testUpdateHasManyWhenNullBefore() {
        Person p = new Person();
        p.save();
        Set<Pet> pets = new HashSet<Pet>();
        pets.add(new Pet("Chien", "Idefix"));
        pets.add(new Pet("Chat", "Merlin"));
        p.setPets(pets);
        p.save();
        p.find(p.getId());
        Iterator<Pet> iterator = p.getPets().iterator();
        Pet pet1 = iterator.next();
        Pet pet2 = iterator.next();
        assertTrue((pet1.getType().equals("Chien") && pet2.getType().equals("Chat")) || (pet2.getType().equals("Chien") && pet1.getType().equals("Chat")));
        assertTrue((pet1.getName().equals("Idefix") && pet2.getName().equals("Merlin")) || (pet2.getName().equals("Idefix") && pet1.getName().equals("Merlin")));
        p.delete();
    }

    // Test pairs of keys are deleted in join table when person is deleted
    public void testRefreshJoinTable() {
        person.delete();
        assertEquals(0, person.getManyToManyRelationsShip().get("pets").getJointure().findAll().size());
    }

    // Test on delete cascading with many_to_many relations .
    // If person is deleted , his pets must also be erased with Cascase.DELETE=true
    public void testCascadeDeleteWithRelationHasMany() {
        person.delete();
        assertFalse(new Pet().find(person.getPets().iterator().next().getId()));
    }

    // Test on delete cascading with many_to_many relations .
    // If person is deleted , his pets must not be erased with Cascase.DELETE=false
    // pairs of keys in join table must be deleted
    public void testNoCascadeDeleteWithRelationHasMany() {
        person.getManyToManyRelationsShip().get("pets").setCascadeDelete(false);
        person.delete();
        assertTrue(new Pet().find(person.getPets().iterator().next().getId()));
        person.getManyToManyRelationsShip().get("pets").setCascadeDelete(true);
        assertEquals(0, person.getManyToManyRelationsShip().get("pets").getJointure().findAll().size());
    }

    // Test if a new person has a same pet , the pet must not be duplicated in the pets table, only a new pair of keys
    // must be created in the join table
    public void testTwoPersonWithSamePet() {
        Person p = new Person();
        Set<Pet> pets = new HashSet<Pet>();
        pets.add(person.getPets().iterator().next());
        p.setPets(pets);
        p.save();
        assertEquals(2, (new Pet()).findAll().size());
        p.setPets(null);
        p.find(p.getId());
        assertEquals(1, p.getPets().size());
        p.delete();
    }

    // Test if add an occurence in the set , just one is added in table rows , other are just updated
    public void testAddOccurenceInHasManyRelationSet() {
        person.getPets().add(new Pet("Hamster", "Scorpio"));
        person.save();
        assertEquals(3, (new Pet()).find("*", "id>0").size());
        person.find(person.getId());
        assertEquals(3, person.getPets().size());
        assertEquals(3, person.getManyToManyRelationsShip().get("pets").getJointure().findAll().size());
    }

    // Test if an occurence is deleted in the set , the occurence in table is not deleted , but the pair keys is deleted in the join table
    public void testDeleteOccurenceInHasManyRelationSet() {
        Pet pet = person.getPets().iterator().next();
        person.getPets().remove(pet);
        assertEquals(1, person.getPets().size());
        person.save();
        assertEquals(1, person.getManyToManyRelationsShip().get("pets").getJointure().findAll().size());
        pet.delete();
    }
}
