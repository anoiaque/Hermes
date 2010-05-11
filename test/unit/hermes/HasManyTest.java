package unit.hermes;

import helpers.Database;

import java.util.Set;

import junit.framework.TestCase;
import sample.Car;
import sample.Person;
import factory.Factory;

public class HasManyTest extends TestCase {

	public static Person		citizen;
	public static Set<Car>	cars;

	public void setUp() {
		citizen = (Person) Factory.get("human");
		cars = (Set<Car>) Factory.get("cars");
		citizen.setCars(cars);
		citizen.save();
	}

	public void tearDown() {
		Database.clear();
	}

	public void testHasManyAssociation() {
		assertTrue(citizen.getHasManyAssociations().containsKey("cars"));
		assertEquals(1, citizen.getHasManyAssociations().size());
	}

	public void testSaveAndLoadRetrieveAttribute() {
		citizen = (Person) citizen.reload();
		for (Car car : citizen.getCars())
			assertTrue(include(car, cars));
	}

	public void testCascadeDelete() {
		citizen.getAssociations().getHasManyAssociations().get("cars").setCascadeDelete(true);
		assertEquals(2, Car.all(Car.class).size());
		citizen.delete();
		assertEquals(0, Car.all(Car.class).size());
		citizen.getAssociations().getHasManyAssociations().get("cars").setCascadeDelete(false);
	}

	public void testNoCascadeDelete() {
		citizen.getAssociations().getHasManyAssociations().get("cars").setCascadeDelete(false);
		assertEquals(2, Car.all(Car.class).size());
		citizen.delete();
		assertEquals(2, Car.all(Car.class).size());
	}

	// Private method

	private boolean include(Car car, Set<Car> cars) {
		for (Car c : cars)
			if (c.getBrand().equals(car.getBrand())) return true;
		return false;
	}
}
