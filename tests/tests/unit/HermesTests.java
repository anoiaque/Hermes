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
        sample2.delete();
    }

    public void testRelationConfig() {
        assertTrue(sample.getHasOneRelationsShip().containsKey("adresse"));
    }

    public void testSaveRelationHasOne() {
        sample.setAdresse(new Adress(13, "rue Tabarly"));
        sample.save();
        sample.setAdresse(new Adress(122222, "rue des hibous"));
        sample.find(sample.getId());
        assertEquals(13, sample.getAdresse().getNumero());
        assertEquals("rue Tabarly", sample.getAdresse().getRue());
    }

    public void testCascadeDeleteWithRelationHasOne() {
        Sample sample = new Sample();
        sample.setAdresse(new Adress(11, "rue Tabarly"));
        sample.save();
        sample.delete();
        assertFalse(new Adress().find(sample.getAdresse().getId()));
    }
}
