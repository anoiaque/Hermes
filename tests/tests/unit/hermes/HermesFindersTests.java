package tests.unit.hermes;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;
import sample.Adress;
import sample.Person;
import sample.Pet;

public class HermesFindersTests extends TestCase {

    public static Person person = new Person();
    public static Person person1;
    public static Person person2;

    public void setUp() {
        setPerson1();
        setPerson2();
    }

    public void tearDown() {
        person1.delete();
        person2.delete();
    }

    // Test find object(s) with conditions on attributes wich are has_one relation
    @SuppressWarnings("unchecked")
    public void testFindWithConditionsOnHasOneRelation() {
        Set<Person> people = (Set<Person>) person.find("adresse.numero=13");
        assertEquals(1, people.size());
    }

    // Private methods
    private void setPerson1() {
        person1 = new Person();
        person1.setAge(30);
        person1.setNom("Marc");
        person1.setAdresse(new Adress(13, "rue Tabarly"));
        Set<Pet> pets = new HashSet<Pet>();
        pets.add(new Pet("Chien", "Medor"));
        pets.add(new Pet("Chat", "Felix"));
        person1.setPets(pets);
        person1.save();
    }

    private void setPerson2() {
        person2 = new Person();
        person2.setAge(25);
        person2.setNom("Jean");
        person2.setAdresse(new Adress(28, "rue Kervegan"));
        Set<Pet> pets = new HashSet<Pet>();
        pets.add(new Pet("Chien", "Toutou"));
        pets.add(new Pet("Chat", "Miel"));
        pets.add(new Pet("Hamster", "Leon"));
        person2.setPets(pets);
        person2.save();
    }
}
