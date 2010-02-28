package unit.hermes;

import helpers.Database;

import java.util.Set;

import junit.framework.TestCase;
import sample.Car;
import sample.Person;
import factory.Factory;

public class HasManyTest extends TestCase {

	public static Person		marc;
	public static Set<Car>	cars;

	public void setUp() {
		marc = (Person) Factory.get("marc");
		cars = (Set<Car>) Factory.get("cars");
		marc.setCars(cars);
		marc.save();
	}

	public void tearDown() {
		Database.clear();
	}

	public void testHasManyAssociationContent() {
		assertTrue(marc.getHasManyAssociations().containsKey("cars"));
		assertEquals(1, marc.getHasManyAssociations().size());
	}

	public void testSaveAndLoad() {
		marc = (Person) marc.reload();
		for (Car car : marc.getCars())
			assertTrue(contains(car, cars));
	}

	public void testCascadeDelete() {
		marc.getAssociations().getHasManyAssociations().get("cars").setCascadeDelete(true);
		assertEquals(2, Car.findAll(Car.class).size());
		marc.delete();
		assertEquals(0, Car.findAll(Car.class).size());
		marc.getAssociations().getHasManyAssociations().get("cars").setCascadeDelete(false);
	}

	public void testNoCascadeDelete() {
		marc.getAssociations().getHasManyAssociations().get("cars").setCascadeDelete(false);
		assertEquals(2, Car.findAll(Car.class).size());
		marc.delete();
		assertEquals(2, Car.findAll(Car.class).size());
	}

	// Private method

	private boolean contains(Car car, Set<Car> cars) {
		for (Car c : cars)
			if (c.getBrand().equals(car.getBrand())) return true;
		return false;
	}
}
