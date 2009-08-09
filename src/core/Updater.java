package core;

import adaptors.Adaptor;

public class Updater {

  public static boolean save(Hermes object) {
    if (object.getId() != 0) return update(object);

    Fields.setFieldsValue(object);
    object.getRelations().saveHasOneRelations();
    boolean saved = Adaptor.get().save(object);
    object.getRelations().saveManyToManyRelations();

    return saved;
  }

  public static boolean update(Hermes object) {
    Fields.setFieldsValue(object);

    object.getRelations().updateHasOneRelations();
    boolean updated = Adaptor.get().update(object);
    object.getRelations().updateManyToManyRelations();

    return updated;
  }

  public static boolean delete(Hermes object) {
    object.getRelations().cascadeDelete();
    boolean deleted = Adaptor.get().delete(object);
    if (deleted) object.setId(0);
    return deleted;
  }

  public static boolean delete(Hermes object, String conditions) {
    object.getRelations().cascadeDelete();
    boolean deleted = Adaptor.get().delete(object, conditions);
    if (deleted) object.setId(0);
    return deleted;
  }
}
