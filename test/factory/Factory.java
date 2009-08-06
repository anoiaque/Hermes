package factory;

import core.Hermes;
import java.util.HashSet;
import java.util.Set;
import sample.Adress;
import sample.Person;
import sample.Pet;

public class Factory {

  public static Object get(String feature) {
    if (feature.equals("marc")) return marc();
    if (feature.equals("jean")) return jean();
    if (feature.equals("pets")) return pets();
    return null;
  }

  private static Set<Pet> pets() {
    Set<Pet> pets = new HashSet<Pet>();
    pets.add(new Pet("Chien", "Medor"));
    pets.add(new Pet("Chat", "Felix"));
    return pets;
  }

  private static Person marc() {
    Person person = new Person();
    person.setAge(30);
    person.setNom("Marc");
    person.setAdresse(new Adress(13, "rue Tabarly"));
    person.setPets(pets());
    person.save();
    return person;
  }

  private static Person jean() {
    Person person = new Person();
    person.setAge(25);
    person.setNom("Jean");
    person.setAdresse(new Adress(28, "rue Kervegan"));
    Set<Pet> pets = pets();
    pets.add(new Pet("Hamster", "Leon"));
    person.setPets(pets);
    person.save();
    return person;
  }
}
