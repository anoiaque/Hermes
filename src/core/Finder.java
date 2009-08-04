package core;

import adaptors.Adaptor;
import gather.Row;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class Finder {

  public static Hermes find(int id, Hermes object) {
    return Row.toObject(Adaptor.get().find(id, object.getTableName()), object);
  }

  public static Set<?> find(String whereClause, Hermes object) {
    Set<Hermes> objects = Row.resultSetToObjects(Adaptor.get().find("*", whereClause, object), object);
    return (Set<Hermes>) loadRelationals(objects, object);

  }

  public static Set<?> find(String selectClause, String whereClause, Hermes object) {
    Set<Hermes> objects = Row.resultSetToObjects(Adaptor.get().find(selectClause, whereClause, object), object);
    return (Set<Hermes>) loadRelationals(objects, object);
  }

  

  private static Set<Hermes> loadRelationals(Set<Hermes> objects, Hermes obj) {
    for (Hermes object : objects) {
      obj.getRelations().getRelationalFields(object);
    }
    return objects;
  }
}
