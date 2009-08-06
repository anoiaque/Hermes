package adaptors;

import adaptors.MySql.MySql;
import configuration.Configuration;
import configuration.Configuration.DBMSConfig;
import core.Hermes;
import java.sql.ResultSet;
import java.util.HashMap;

public abstract class Adaptor {

  private static Adaptor adaptor = null;

  public static Adaptor get() {
    if (adaptor == null) {
      synchronized (Adaptor.class) {
        DBMSConfig config = Configuration.DBMSConfig.get();
        adaptor = adaptor(config.getDBMSName());
      }
    }
    return adaptor;
  }

  // Private Methods
  private static Adaptor adaptor(String DBMSName) {
    if (DBMSName.equalsIgnoreCase("MySql")) {
      return new MySql();
    }

    return null;
  }

  // Abstract methods TODO : Change for independency with singular Adaptor
  public abstract int save(HashMap<String, Object> attributes_values, String tableName);

  public abstract int save(HashMap<String, Object> attributes_values, Class<? extends Hermes> model);

  public abstract boolean update(String tableName, HashMap<String, Object> attributes_values, int id);

  public abstract boolean delete(String tableName, int id);

  public abstract boolean delete(String tableName, String whereClause);

  public abstract Hermes find(int id, Class<? extends Hermes> model);

  public abstract ResultSet find(String select_clause, String where_clause, Hermes object);

  public abstract String javaToSql(String javaType);
}
