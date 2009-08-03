package adaptors;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pool.Pool;

import com.mysql.jdbc.Statement;

import configuration.Configuration;
import core.Hermes;
import core.Pluralizer;

public class MySqlAdaptor extends Adaptor {

  private static HashMap<String, String> typesMapping = null;

  // Public methods
  public int save(String tableName, HashMap<String, Object> attributes_values) {
    Connection connexion = null;
    Integer id = 0;
    Pool pool = Pool.getInstance();
    ResultSet rs = null;
    try {
      connexion = pool.getConnexion();
      PreparedStatement statement = connexion.prepareStatement(sqlInsert(tableName, attributes_values), Statement.RETURN_GENERATED_KEYS);
      statement.execute();
      rs = statement.getGeneratedKeys();
      if (rs.next()) {
        id = rs.getInt(1);
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return -1;
    } finally {
      pool.release(connexion);
      try {
        if (rs != null) {
          rs.close();
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return id;
  }

  public boolean update(String tableName, HashMap<String, Object> attributes_values, int id) {
    Connection connexion = null;
    Pool pool = Pool.getInstance();
    ResultSet rs = null;
    try {
      connexion = pool.getConnexion();
      PreparedStatement statement = connexion.prepareStatement(sqlUpdate(tableName, attributes_values, id));
      statement.execute();
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    } finally {
      pool.release(connexion);
      try {
        if (rs != null) {
          rs.close();
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return true;
  }

  public boolean delete(String tableName, int id) {
    Connection connexion = null;
    Pool pool = Pool.getInstance();
    boolean deleted = false;
    String sql = "delete from " + tableName + " where id =" + id;
    try {
      connexion = pool.getConnexion();
      PreparedStatement statement = connexion.prepareStatement(sql);
      statement.execute();
      deleted = true;
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      pool.release(connexion);
    }
    return deleted;
  }

  public boolean delete(String tableName, String whereClause) {
    Connection connexion = null;
    Pool pool = Pool.getInstance();
    boolean deleted = false;
    String sql = "delete from " + tableName + " where " + whereClause;
    try {
      connexion = pool.getConnexion();
      PreparedStatement statement = connexion.prepareStatement(sql);
      statement.execute();
      deleted = true;
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      pool.release(connexion);
    }
    return deleted;
  }

  public Hermes find(int id, Hermes object) {
    Connection connexion = null;
    Pool pool = Pool.getInstance();
    String sql = "select * from " + object.getTableName() + " where id =" + id;
    ResultSet rs = null;
    boolean found = false;
    try {
      connexion = pool.getConnexion();
      rs = connexion.prepareStatement(sql).executeQuery();
      if (found = rs.next()) {
        for (String attribute : object.getFieldsType().keySet()) {
          Field field = object.getClass().getDeclaredField(attribute);
          field.setAccessible(true);
          field.set(object, rs.getObject(field.getName()));
        }
        object.setId(id);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      pool.release(connexion);
      try {
        rs.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return found ? object : null;
  }

  public Set<Hermes> find(String select_clause, String where_clause, Hermes object) {
    Connection connexion = null;
    Pool pool = Pool.getInstance();
    ResultSet rs = null;
    try {
      connexion = pool.getConnexion();
      PreparedStatement statement = connexion.prepareStatement(sqlSelect(select_clause, where_clause, object));
      rs = statement.executeQuery();
      return resultSetToObjects(rs, object);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    } finally {
      try {
        if (rs != null) {
          rs.close();
        }
        pool.release(connexion);
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  public String javaToSql(String javaType) {
    if (typesMapping == null) {
      setTypesMapping();
    }
    return typesMapping.get(javaType);
  }

  // Private methods
  private static String sqlFrom(HashMap<String, String> joinedTables, Hermes object) {
    String sqlFrom = object.getTableName();
    for (String table : joinedTables.values()) {
      sqlFrom += "," + table;
    }
    return sqlFrom;
  }

  private static String attributeNameToTableName(HashMap<String, String> joinedTables, String where_clause, Hermes object) {
    String sqlWhere = where_clause;
    Pattern pattern;
    for (String attribute : joinedTables.keySet()) {
      pattern = Pattern.compile(attribute + ".");
      sqlWhere = pattern.matcher(sqlWhere).replaceAll(joinedTables.get(attribute) + ".");
      sqlWhere += " and " + object.getHasOneRelationsShip().get(attribute).getForeignKeyName() + "=" + joinedTables.get(attribute) + ".id";
    }
    return sqlWhere;
  }

  public static HashMap<String, String> joinedTables(String where_clause, Hermes object) {
    Pattern pattern = Pattern.compile("'(.)*?'");
    String cleaned = pattern.matcher(where_clause).replaceAll("");
    pattern = Pattern.compile("([\\w]*\\.)");
    Matcher matcher = pattern.matcher(cleaned);
    ArrayList<String> relationAtrributes = new ArrayList<String>();
    while (matcher.find()) {
      String attr = matcher.group().replace(".", "");
      if (!relationAtrributes.contains(attr)) {
        relationAtrributes.add(attr);
      }
    }
    return tablesNamesFor(relationAtrributes, object);
  }

  private static HashMap<String, String> tablesNamesFor(ArrayList<String> relationAtrributes, Hermes object) {
    HashMap<String, String> tablesNames = new HashMap<String, String>();
    for (String attr : relationAtrributes) {
      try {
        Field field = object.getClass().getDeclaredField(attr);
        Class<?> type = field.getType();
        if (!type.equals(Set.class)) {
          tablesNames.put(attr, Pluralizer.getPlurial(type.getSimpleName()).toUpperCase());
        } else {
          ParameterizedType set = (ParameterizedType) object.getClass().getDeclaredField(attr).getGenericType();
          String setType = (((Class<?>) set.getActualTypeArguments()[0]).getSimpleName().toUpperCase());
          tablesNames.put(attr, Pluralizer.getPlurial(setType));
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return tablesNames;
  }

  private static Set<Hermes> resultSetToObjects(ResultSet rs, Hermes object) {
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

  private static String sqlSelect(String select_clause, String where_clause, Hermes object) {
    HashMap<String, String> joinedTables = new HashMap<String, String>();
    if (where_clause != null) {
      joinedTables = joinedTables(where_clause, object);
      where_clause = attributeNameToTableName(joinedTables, where_clause, object);
    }
    String from_clause = sqlFrom(joinedTables, object);
    String sqlSelect = "select " + select_clause + " from " + from_clause;
    return (where_clause == null) ? sqlSelect : sqlSelect + " where " + where_clause;
  }

  private static String sqlInsert(String tableName, HashMap<String, Object> attributes_values) {
    String fields = attributes_values.keySet().toString().replace("[", "").replace("]", "");
    String values = attributes_values.values().toString().replace("[", "'").replace("]", "'").replace(", ", "','").replace("'null'", "null");
    return "insert into  " + tableName + "(" + fields + ")" + "values (" + values + ")";
  }

  private static String sqlUpdate(String tableName, HashMap<String, Object> attributes_values, int id) {
    String setClause = "";
    Iterator<String> attrs = attributes_values.keySet().iterator();
    while (attrs.hasNext()) {
      String attr = attrs.next();
      setClause += attr + "='" + attributes_values.get(attr) + "'";
      if (attrs.hasNext()) {
        setClause += ",";
      }
    }
    return "update " + tableName + " set " + setClause.replace("'null'", "null") + " where id=" + id;
  }

  private static void setTypesMapping() {
    typesMapping = new HashMap<String, String>();
    typesMapping.put("int", "int");
    typesMapping.put("Integer", "int");
    typesMapping.put("String", "varchar(" + Configuration.SqlConverterConfig.varcharLength + ")");
  }
}
