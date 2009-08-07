package adaptors.MySql;

import adaptors.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import pool.Pool;

import com.mysql.jdbc.Statement;

import configuration.Configuration;
import core.Hermes;

public class MySql extends Adaptor {

  private static HashMap<String, String> typesMapping = null;

  public int save(HashMap<String, Object> attributes_values, Object model) {
    Connection connexion = null;
    Integer id = 0;
    Pool pool = Pool.getInstance();
    ResultSet rs = null;
    PreparedStatement statement;
    String sql = getSqlInsert(attributes_values, model);
    try {
      connexion = pool.getConnexion();
      statement = connexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      statement.execute();
      rs = statement.getGeneratedKeys();
      if (rs.next()) id = rs.getInt(1);
    }
    catch (SQLException e) {
      e.printStackTrace();
      return -1;
    }
    finally {
      pool.release(connexion);
      try {
        if (rs != null) rs.close();
      }
      catch (SQLException e) {
        e.printStackTrace();
      }
      return id;
    }
  }

  public boolean update(HashMap<String, Object> attributes_values, int id, Object model) {
    Connection connexion = null;
    Pool pool = Pool.getInstance();
    ResultSet rs = null;
    String sql = getSqlUpdate(attributes_values, model, id);
    try {
      connexion = pool.getConnexion();
      PreparedStatement statement = connexion.prepareStatement(sql);
      statement.execute();
    }
    catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
    finally {
      pool.release(connexion);
      try {
        if (rs != null) {
          rs.close();
        }
      }
      catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return true;
  }

  public boolean delete(int id, Object model) {
    Connection connexion = null;
    Pool pool = Pool.getInstance();
    boolean deleted = false;
    String sql = getSqlDelete(model, id);
    try {
      connexion = pool.getConnexion();
      PreparedStatement statement = connexion.prepareStatement(sql);
      statement.execute();
      deleted = true;
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    finally {
      pool.release(connexion);
    }
    return deleted;
  }

  public boolean delete(Object model, String conditions) {
    Connection connexion = null;
    Pool pool = Pool.getInstance();
    boolean deleted = false;
    String sql = getSqlDelete(model, conditions);
    try {
      connexion = pool.getConnexion();
      PreparedStatement statement = connexion.prepareStatement(sql);
      statement.execute();
      deleted = true;
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    finally {
      pool.release(connexion);
    }
    return deleted;
  }

  public Hermes find(int id, Class<? extends Hermes> model) {
    Connection connexion = null;
    Pool pool = Pool.getInstance();
    ResultSet rs = null;
    try {
      String sql = SqlBuilder.select("*", "id = " + id, model.newInstance());
      connexion = pool.getConnexion();
      rs = connexion.prepareStatement(sql).executeQuery();
      return Record.toObject(rs, model);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    finally {
      pool.release(connexion);
      if (rs != null) try {
          rs.close();
        }
        catch (SQLException e) {
          e.printStackTrace();
        }
    }
    return null;
  }

  public ResultSet find(String select_clause, String where_clause, Hermes model) {
    Connection connexion = null;
    Pool pool = Pool.getInstance();
    try {
      connexion = pool.getConnexion();
      PreparedStatement statement = connexion.prepareStatement(SqlBuilder.select(select_clause, where_clause, model));
      return statement.executeQuery();

    }
    catch (Exception e) {
      e.printStackTrace();
      return null;
    }
    finally {
      pool.release(connexion);
    }
  }

  public String javaToSql(String javaType) {
    if (typesMapping == null) {
      setTypesMapping();
    }
    return typesMapping.get(javaType);
  }

  // Private methods
  private static void setTypesMapping() {
    typesMapping = new HashMap<String, String>();
    typesMapping.put("int", "int");
    typesMapping.put("Integer", "int");
    typesMapping.put("String", "varchar(" + Configuration.SqlConverterConfig.varcharLength + ")");
  }

  private String getSqlInsert(HashMap<String, Object> attributes_values, Object model) {
    if (model.getClass().equals(String.class))
      return SqlBuilder.insert(attributes_values, (String) model);
    else if (model.getClass().equals(Class.class))
      return SqlBuilder.insert(attributes_values, (Class<? extends Hermes>) model);
    else return "";
  }

  private String getSqlUpdate(HashMap<String, Object> attributes_values, Object model, int id) {
    if (model.getClass().equals(String.class))
      return SqlBuilder.update(attributes_values, id, (String) model);
    else if (model.getClass().equals(Class.class))
      return SqlBuilder.update(attributes_values, id, (Class<? extends Hermes>) model);
    else return "";

  }

  private String getSqlDelete(Object model, int id) {
    if (model.getClass().equals(String.class))
      return SqlBuilder.delete(id, (String) model);
    else if (model.getClass().equals(Class.class))
      return SqlBuilder.delete(id, (Class<? extends Hermes>) model);
    else return "";

  }
  private String getSqlDelete(Object model, String conditions) {
    if (model.getClass().equals(String.class))
      return SqlBuilder.delete((String) model,conditions);
    else if (model.getClass().equals(Class.class))
      return SqlBuilder.delete((Class<? extends Hermes>) model,conditions);
    else return "";

  }
}
