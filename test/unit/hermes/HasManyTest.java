package unit.hermes;

import factory.Factory;
import helpers.Database;
import java.util.Set;
import junit.framework.TestCase;
import sample.Car;
import sample.Person;

public class HasManyTest extends TestCase {

  public static Person marc;

  @Override
  public void setUp() {
    Database.clear();
    marc = (Person) Factory.get("marc");

  }

  public void testHasManyAssociationContent() {
    assertTrue(marc.getHasManyRelationsShip().containsKey("cars"));
    assertEquals(1, marc.getHasManyRelationsShip().size());
  }


public void testSave() {
    Set<Car> cars = (Set<Car>) Factory.get("cars");
    marc.setCars(cars);
    marc.save();

  }
}