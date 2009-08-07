package core;

import adaptors.Adaptor;
import java.util.HashMap;

public class Updater {

  public static boolean save(Hermes object) {
    if (object.getId() != 0) return update(object);

    Relational relations = object.getRelations();
    HashMap<String, Object> attributes_values;
    Fields.setFieldsValue(object);
    relations.saveHasOneRelations();
    attributes_values = ((HashMap<String, Object>) object.getFieldsValue().clone());
    attributes_values.putAll(relations.foreignKeys());
    object.setId(Adaptor.get().save(attributes_values, (Object) object.getClass()));
    relations.saveManyToManyRelations();
    return object.getId() != -1;

  }

  public static boolean delete(Hermes object) {
    Relational relations = object.getRelations();
    relations.cascadeDelete();
    boolean deleted = Adaptor.get().delete(object.getTableName(), object.getId());
    object.setId(0);
    return deleted;
  }

  public static boolean delete(String conditions, Hermes object) {
    Relational relations = object.getRelations();
    relations.cascadeDelete();
    boolean deleted = Adaptor.get().delete(object.getTableName(), conditions);
    object.setId(0);
    return deleted;
  }

  private static boolean update(Hermes object) {
    HashMap<String, Object> attributes_values;
    Relational relations = object.getRelations();
    Fields.setFieldsValue(object);
    relations.updateHasOneRelations();
    attributes_values = (HashMap<String, Object>) object.getFieldsValue().clone();
    attributes_values.putAll(relations.foreignKeys());
    boolean updated = Adaptor.get().update(object.getTableName(), attributes_values, object.getId());
    relations.updateManyToManyRelations();
    return updated;
  }
}
