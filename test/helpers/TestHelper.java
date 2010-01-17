package helpers;

import core.Finder;
import core.Hermes;
import core.Jointure;
import factory.Factory;
import java.util.Iterator;
import java.util.Set;
import junit.framework.TestCase;
import sample.Person;
import sample.Pet;

public class TestHelper extends TestCase {

  public static int jointureSizeFor(Hermes object, String attribute) {
    Jointure jointure = object.getManyToManyAssociations().get(attribute).getJointure();
    return Finder.joinFind(object.getId(), jointure).size();
  }

  public void assertMarcRetrieveHisPets(Person newMarc) {
    Person marc = (Person) Factory.get("marc");
    assertEquals(2, newMarc.getPets().size());

    Iterator<Pet> pets = marc.getPets().iterator();
    assertTrue(containPet(newMarc.getPets(), pets.next()));
    assertTrue(containPet(newMarc.getPets(), pets.next()));
  }

  private boolean containPet(Set<Pet> pets, Pet pet) {
    for (Pet p : pets) {
      if (p.getType().equals(pet.getType()) && p.getName().equals(pet.getName())) return true;
    }
    return false;
  }
}
