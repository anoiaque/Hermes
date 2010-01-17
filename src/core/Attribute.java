package core;

import adaptors.Adaptor;
import java.lang.reflect.Field;

import java.util.ArrayList;

public class Attribute {

  private String name;
  private String sqlType;
  private Object value;

  public Attribute(String name, String type, Object value) {
    this.name = name;
    this.sqlType = type;
    this.value = value;
  }

  public static ArrayList<Attribute> load(Hermes model) {
    ArrayList<Attribute> list = new ArrayList<Attribute>();
    try {
      for (Field field : model.getClass().getDeclaredFields()) {
        if (isBasic(field.getName(), model)) {
          field.setAccessible(true);
          String name = field.getName();
          String sqlType = Adaptor.get().javaToSql(field.getType().getSimpleName());
          Object value = field.get(model);
          list.add(new Attribute(name, sqlType, value));
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
   
    return list;
  }

  public static boolean isBasic(String name, Hermes object) {
    Associations relations = object.getAssociations();
    return !(relations.getHasOneAssociations().containsKey(name)
            || relations.getManyToManyAsociations().containsKey(name)
            || relations.getHasManyRelationsShip().containsKey(name));
  }

  // Getters & Setters
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  public String getSqlType() {
    return sqlType;
  }
}
