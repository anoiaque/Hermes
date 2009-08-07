package core;

import adaptors.Adaptor;
import java.lang.reflect.Field;
import java.util.HashMap;

public class Fields {

  public static void setFields(Hermes object) {
    setFieldsType(object);
    setFieldsValue(object);
  }

  public static void setFieldsValue(Hermes object) {
    object.setFieldsValue(new HashMap<String, Object>());
    try {
      for (Field field : object.getClass().getDeclaredFields()) {
        if (isBasicField(field.getName(), object)) {
          field.setAccessible(true);
          object.getFieldsValue().put(field.getName(), field.get(object));
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void setFieldsType(Hermes object) {
    object.setFieldsType(new HashMap<String, String>());
    for (Field field : object.getClass().getDeclaredFields()) {
      if (isBasicField(field.getName(), object))
        object.getFieldsType().put(field.getName(), Adaptor.get().javaToSql(field.getType().getSimpleName()));
    }
  }

  private static boolean isBasicField(String attributeName, Hermes object) {
    Relational relations = object.getRelations();
    return !(relations.getHasOneRelationsShip().containsKey(attributeName) || relations.getManyToManyRelationsShip().containsKey(attributeName));
  }
}
