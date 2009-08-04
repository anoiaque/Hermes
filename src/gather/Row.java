package gather;

import core.Hermes;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class Row {

  // Transform a resultset into an Hermes object with its relationnal datas
  public static Hermes toObject(ResultSet rs, Hermes object) {
    try {
      if (rs == null || !rs.next()) return null;
      for (String attribute : object.getFieldsType().keySet()) {
        Field field = object.getClass().getDeclaredField(attribute);
        field.setAccessible(true);
        field.set(object, rs.getObject(field.getName()));
        try {
          object.setId((Integer) rs.getObject("id"));
        } catch (SQLException e) {
          // pas d'id dans la table
        }
      }
     // rs.close();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
    object.getRelations().getRelationalFields(object);
    return object;
  }
  public static Set<Hermes> resultSetToObjects(ResultSet rs, Hermes object) {
    Set<Hermes> result = new HashSet<Hermes>();
    Class<?> classe = object.getClass();
    try {
      while (rs.next()) {
        Hermes obj = (Hermes) classe.newInstance();
        for (String attribute : obj.getFieldsType().keySet()) {
          Field field = obj.getClass().getDeclaredField(attribute);
          field.setAccessible(true);
          field.set(obj, rs.getObject(field.getName()));
        }
        try {
          obj.setId((Integer) rs.getObject("id"));
        } catch (SQLException e) {
          // pas d'id dans la table
        }
        result.add(obj);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result;
  }
}
