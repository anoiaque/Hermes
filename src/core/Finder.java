package core;

import adaptors.Adaptor;
import adaptors.MySql.Record;
import java.util.Set;

public class Finder {

  public static Hermes find(int id, Class<? extends Hermes> model) {
    return Adaptor.get().find(id,model);
  }

  public static Set<?> find(String whereClause, Class<? extends Hermes> model) {
    try {
      return Record.toObjects(Adaptor.get().find("*", whereClause, model.newInstance()), model);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public static Set<?> find(String selectClause, String whereClause, Class<? extends Hermes> model) {
    try {
      return Record.toObjects(Adaptor.get().find(selectClause, whereClause, model.newInstance()), model);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  // Finder for Jointure models. The reason for this finder is that instances of this model have not
  // the same table name.
  public static Set<?> joinFind(int parentId, Jointure join) {
    try {
      return Record.toObjects(Adaptor.get().find("*", "parentId = " + parentId, join), Jointure.class);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

 
}
