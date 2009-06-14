package tests.unit;

import java.util.HashMap;

import junit.framework.TestCase;
import classfortests.Adress;
import classfortests.Sample;

public class HermesTests extends TestCase {
    public static Sample sample;

    public void setUp() {
        sample = new Sample();
        sample.setNumber(1000);
        sample.setChaine("toto");
    }

    public void tearDown() {
        sample.delete();
    }

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

    public void testRelationFieldsNotInBasicFields() {
        HashMap<String, String> fields = sample.getDatabaseFields();
        assertFalse(fields.containsKey("adresse"));
    }

    public void testRelationConfig() {
        assertEquals(Adress.class, sample.getHasOneRelations().get("adresse").getClasse());
    }

    public void testSaveAndFind() {
        sample.save();
        sample.setNumber(0);
        sample.setChaine("");
        sample.find(sample.getId());
        assertEquals(1000, sample.getNumber());
        assertEquals("toto", sample.getChaine());
    }

    public void testDelete() {
        sample.save();
        sample.delete();
        assertFalse(sample.find(sample.getId()));
    }

    public void testId() {
        sample.save();
        Sample sample2 = new Sample();
        sample2.save();
        assertEquals(sample.getId() + 1, sample2.getId());
    }
}
