package adaptors.MySql;

import adaptors.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import pool.Pool;

import com.mysql.jdbc.Statement;

import core.Hermes;

public class MySql extends Adaptor {

  public boolean save(Hermes object) {
    return execute(SqlBuilder.getSqlInsertFor(object), object);
  }

  public boolean update(Hermes object) {
    return execute(SqlBuilder.getSqlUpdateFor(object), object);
  }

  public boolean delete(Hermes object) {
    return execute(SqlBuilder.getSqlDeleteFor(object), object);
  }

  public boolean delete(Hermes object, String conditions) {
    return execute(SqlBuilder.getSqlDeleteFor(object, conditions), object);
  }

  public Hermes find(int id, Class<? extends Hermes> model) {
    Connection connexion = null;
    Pool pool = Pool.getInstance();
    ResultSet rs = null;
    try {
      String sql = SqlBuilder.select("*", "id = " + id, model.newInstance());
      connexion = pool.getConnexion();
      rs = connexion.prepareStatement(sql).executeQuery();
      return ObjectBuilder.toObject(rs, model);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      pool.release(connexion);
      if (rs != null) {
        try {
          rs.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
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

    } catch (Exception e) {
      e.printStackTrace();
      return null;
    } finally {
      pool.release(connexion);
    }
  }

  public String javaToSql(String javaType) {
    return Mapping.javaToSql(javaType);
  }

  public boolean execute(String sql, Hermes object) {
    Connection connexion = null;
    Pool pool = Pool.getInstance();
    ResultSet rs = null;
    PreparedStatement statement;
    try {
      connexion = pool.getConnexion();
      statement = connexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      statement.execute();
      rs = statement.getGeneratedKeys();
      if (rs.next()) object.setId(rs.getInt(1));

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    } finally {
      pool.release(connexion);
      try {
        if (rs != null) rs.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return true;
  }

  
}
