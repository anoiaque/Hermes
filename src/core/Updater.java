package core;

import adaptors.Adaptor;

public class Updater {

  public static boolean save(Hermes object) {
    if (!object.isNewRecord()) return update(object);

    object.loadAttributes();
    object.getAssociations().saveHasOneRelations();
    boolean saved = Adaptor.get().save(object);
    object.getAssociations().saveHasManyRelations();
    object.getAssociations().saveManyToManyRelations();
    return saved;
  }

  public static boolean update(Hermes object) {
    object.loadAttributes();
    object.getAssociations().saveHasOneRelations();
    boolean updated = Adaptor.get().update(object);
    object.getAssociations().saveHasManyRelations();
    object.getAssociations().saveManyToManyRelations();
    return updated;
  }

  public static boolean delete(Hermes object) {
    object.getAssociations().cascadeDelete();
    boolean deleted = Adaptor.get().delete(object);
    if (deleted) object.setId(0);
    return deleted;
  }

  public static boolean delete(Hermes object, String conditions) {
    object.getAssociations().cascadeDelete();
    boolean deleted = Adaptor.get().delete(object, conditions);
    if (deleted) object.setId(0);
    return deleted;
  }
}
