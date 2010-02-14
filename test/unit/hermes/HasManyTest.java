package unit.hermes;

import factory.Factory;
import helpers.Database;

import java.util.Set;

import junit.framework.TestCase;
import sample.Car;
import sample.Person;

public class HasManyTest extends TestCase {

	public static Person	marc;

	public void setUp() {
		Database.clear();
		marc = (Person) Factory.get("marc");
	}

	public void tearDown() {
		Database.clear();
	}

	public void testHasManyAssociationContent() {
		assertTrue(marc.getHasManyAssociations().containsKey("cars"));
		assertEquals(1, marc.getHasManyAssociations().size());
	}

	public void testSaveAndLoad() {
		Set<Car> cars = (Set<Car>) Factory.get("cars");
		marc.setCars(cars);
		marc.save();
		marc = (Person) marc.reload();
		assertRetrieveCars(cars);
	}

	// Private method
	private void assertRetrieveCars(Set<Car> cars) {
		assertEquals(2, marc.getCars().size());
		for (Car car : marc.getCars())
			assertTrue(contains(car, cars));
	}

	private boolean contains(Car car, Set<Car> cars) {
		for (Car c : cars) {
			if (c.getBrand().equals(car.getBrand())) return true;
		}
		return false;
	}
}
