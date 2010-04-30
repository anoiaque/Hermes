package unit.hermes;

import core.Inflector;
import junit.framework.TestCase;

public class InflectorTest extends TestCase {

	public void testPlurals() {
		assertEquals("cars", Inflector.pluralize("car"));
		assertEquals("addresses", Inflector.pluralize("address"));
		assertEquals("axes", Inflector.pluralize("axis"));
		assertEquals("testes", Inflector.pluralize("testis"));
		assertEquals("viri", Inflector.pluralize("virus"));		
		assertEquals("statuses", Inflector.pluralize("status"));		
		assertEquals("buses", Inflector.pluralize("bus"));		
		assertEquals("tomatoes", Inflector.pluralize("tomato"));	
		assertEquals("fishes", Inflector.pluralize("fish"));	
		assertEquals("men", Inflector.pluralize("man"));	

		assertEquals("millenia", Inflector.pluralize("millenium"));		
		assertEquals("theses", Inflector.pluralize("thesis"));		
		assertEquals("thesisses", Inflector.pluralize("thesissis"));	
		assertEquals("elves", Inflector.pluralize("elf"));
		assertEquals("chives", Inflector.pluralize("chive"));
		assertEquals("delays", Inflector.pluralize("delay"));	
				
		assertEquals("oquies", Inflector.pluralize("oquy"));	
		assertEquals("hashes", Inflector.pluralize("hash"));	
		assertEquals("cortexes", Inflector.pluralize("cortex"));	
		assertEquals("matrices", Inflector.pluralize("matrix"));	
		assertEquals("vertices", Inflector.pluralize("vertex"));	
		assertEquals("mices", Inflector.pluralize("mouse"));	
		assertEquals("oxygens", Inflector.pluralize("oxygen"));	
		assertEquals("oxen", Inflector.pluralize("ox"));	
		assertEquals("quizzes", Inflector.pluralize("quiz"));	

		assertEquals("people", Inflector.pluralize("person"));	
		assertEquals("children", Inflector.pluralize("child"));	
		assertEquals("sexes", Inflector.pluralize("sex"));	
		assertEquals("moves", Inflector.pluralize("move"));	
		assertEquals("cows", Inflector.pluralize("cow"));	
		
		assertEquals("equipment", Inflector.pluralize("equipment"));	
		assertEquals("rice", Inflector.pluralize("rice"));	
		assertEquals("money", Inflector.pluralize("money"));	
	}
	
}
