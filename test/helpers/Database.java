package helpers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import pool.Pool;

public class Database {

  public static void clear() {
    Connection connexion = null;
    Pool pool = Pool.getInstance();
    String[] tables = {"adress", "person", "pet", "person_pet"};
    String sql = "delete from ";

    try {
      connexion = pool.getConnexion();
      for (String tableName : tables) {
        sql = "delete from " + tableName;
        PreparedStatement statement = connexion.prepareStatement(sql);
        statement.execute();
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      pool.release(connexion);
    }

  }
}
