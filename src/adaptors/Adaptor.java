package adaptors;

import adaptors.MySql.MySql;
import configuration.Configuration;
import configuration.Configuration.DBMSConfig;
import core.Hermes;
import java.sql.ResultSet;

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

  private static Adaptor adaptor(String DBMSName) {
    if (DBMSName.equalsIgnoreCase("MySql")) {
      return new MySql();
    }
    return null;
  }

  public abstract boolean save(Hermes object);

  public abstract boolean update(Hermes object);

  public abstract boolean delete(Hermes object);

  public abstract boolean delete(Hermes object, String conditions);

  public abstract Hermes find(int id, Class<? extends Hermes> model);

  public abstract ResultSet find(String select_clause, String where_clause, Hermes object);

  public abstract String javaToSql(String javaType);
}
