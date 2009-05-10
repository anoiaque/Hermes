package tests;

import java.util.HashMap;

import classfortests.Sample;
import junit.framework.TestCase;

public class DataRecordTester extends TestCase {

	Sample sample = new Sample();

	public void testNomTable() {
		assertEquals("Sample", sample.getTable_name());
	}

	public void testFields() {
		HashMap<String, String> fields = sample.getDatabaseFields();
		assertTrue(fields.containsKey("number"));
		assertEquals("int", fields.get("number"));
		assertTrue(fields.containsKey("chaine"));
		assertEquals("varchar(250)", fields.get("chaine"));
	}

	public void testSaveAndFind() {
		sample.setNumber(1000);
		sample.setChaine("toto");
		sample.save();
		sample.setNumber(0);
		sample.setChaine("");
		sample.find(sample.getId());
		assertEquals(1000, sample.getNumber());
		assertEquals("toto", sample.getChaine());
	}
	public void testDelete() {
		sample.setNumber(1);
		sample.setChaine("titi");
		sample.save();
		sample.delete(sample.getId());
        assertFalse(sample.find(sample.getId()));
	}

}
