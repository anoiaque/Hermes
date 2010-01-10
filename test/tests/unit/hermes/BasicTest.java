package tests.unit.hermes;


import junit.framework.TestCase;
import sample.Adress;
import sample.Person;
import configuration.Configuration;
import core.Attribute;
import core.Inflector;
import core.Table;
import factory.Factory;
import helpers.Database;
import java.lang.reflect.Field;
import java.util.List;
import sample.Personne;

public class BasicTest extends TestCase {

    public static Person marc;

    @Override
    public void setUp() {
        Database.clear();
        marc = (Person) Factory.get("marc");
    }

    // Test the default name of table , function of the class name pluralized
    public void testDefaultTableName() {
        assertEquals(Inflector.pluralize("Person"), marc.getTableName());
        assertEquals(Inflector.pluralize("Person"), Table.nameFor(Person.class));
    }

    // Test the  name of table when redefined in the model
    public void testRedefinedTableName() {
        Personne p = new Personne();
        assertEquals("personnel", p.getTableName());
        assertEquals("personnel", Table.nameFor(Personne.class));
    }

    // Test id obtain by get_generated_keys from database
    public void testId() {
        Person person2 = new Person();
        person2.save();
        assertEquals(marc.getId() + 1, person2.getId());
    }

    // Test basics fields , type and name are right
    public void testBasicsFields() {
     
        List<Attribute> attributes = marc.getAttributes();

        assertTrue(containsAttribute(attributes,"age","int",30));
        String type = "varchar(" + Configuration.SqlConverterConfig.varcharLength + ")";
        assertTrue(containsAttribute(attributes,"nom",type,"Marc"));
       
    }

    // Test raw is deleted in database
    public void testDelete() {
        marc.delete();
        assertNull(Person.find(marc.getId(), Person.class));
    }

    // Test update . Ensure no new record created but the one is updated
    public void testUpdate() {
        assertEquals(1, Person.findAll(Person.class).size());
        marc.setNom("titi");
        marc.setAdresse(new Adress(25, "rue de Brest"));
        marc.save();
        assertEquals(1, Person.findAll(Person.class).size());
        marc = (Person) Person.find(marc.getId(), Person.class);
        assertEquals(30, marc.getAge());
        assertEquals("titi", marc.getNom());
        assertEquals(25, marc.getAdresse().getNumero());
        assertEquals("rue de Brest", marc.getAdresse().getRue());
    }

    private boolean containsAttribute(List<Attribute> list,String name, String type,Object value){
      for (Attribute attr : list){
        if (attr.getName().equals(name) && 
            attr.getSqlType().equals(type) &&
            attr.getValue().equals(value)) return true;
      }
      return false;
    }
}
