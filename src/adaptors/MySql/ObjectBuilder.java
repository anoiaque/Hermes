package adaptors.MySql;

import core.Attribute;
import core.Hermes;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ObjectBuilder {

  // Transform a resultset into an Hermes object with its relationnal datas
  public static Hermes toObject(ResultSet rs, Class<? extends Hermes> model) {
    try {
      if (rs == null || !rs.next()) return null;
      Hermes object = model.newInstance();
      object.loadAttributes();
      List<Attribute> attributes = (List<Attribute>) object.getAttributes();
      for (Attribute attribute : attributes) {
        Field field = object.getClass().getDeclaredField(attribute.getName());
        field.setAccessible(true);
        field.set(object, rs.getObject(field.getName()));
        try {
          object.setId((Integer) rs.getObject("id"));
        } catch (SQLException e) {
          // pas d'id dans la table
        }
      }
      object.getAssociations().loadRelationalFields(object, rs);
      return object;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static Set<Hermes> toObjects(ResultSet rs, Class<? extends Hermes> model) {
    Set<Hermes> result = new HashSet<Hermes>();
    Hermes obj;
    try {
      do {
        obj = toObject(rs, model);
        if (obj != null) result.add(obj);
      } while (obj != null);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result;
  }
}
