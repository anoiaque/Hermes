package adaptors;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import pool.Pool;

import com.mysql.jdbc.Statement;

import configuration.Configuration;
import core.Hermes;

public class MySqlAdaptor {

    private static HashMap<String, String> typesMapping = null;

    // Public methods
    public static int save(String tableName, HashMap<String, Object> attributes_values) {
        Connection connexion = null;
        Integer id = 0;
        Pool pool = Pool.getInstance();
        ResultSet rs = null;
        try {
            connexion = pool.getConnexion();
            PreparedStatement statement = connexion.prepareStatement(sqlInsert(tableName, attributes_values), Statement.RETURN_GENERATED_KEYS);
            statement.execute();
            rs = statement.getGeneratedKeys();
            if (rs.next())
                id = rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        } finally {
            pool.release(connexion);
            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return id;
    }

    public static boolean delete(String tableName, int id) {
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

    public static boolean delete(String tableName, String whereClause) {
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

    public static Hermes find(String tableName, int id, Hermes object) {
        Connection connexion = null;
        Pool pool = Pool.getInstance();
        String sql = "select * from " + tableName + " where id =" + id;
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

    public static Set<Hermes> find(String tableName, String select_clause, String where_clause, Hermes object) {
        Set<Hermes> result = new HashSet<Hermes>();
        Connection connexion = null;
        Pool pool = Pool.getInstance();
        String sql = "select " + select_clause + " from " + tableName + " where " + where_clause;
        ResultSet rs = null;
        Class<?> classe = object.getClass();
        try {
            connexion = pool.getConnexion();
            PreparedStatement statement = connexion.prepareStatement(sql);
            rs = statement.executeQuery();
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
        } finally {
            try {
                if (rs != null)
                    rs.close();
                pool.release(connexion);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static String javaToSql(String javaType) {
        if (typesMapping == null)
            setTypesMapping();
        return typesMapping.get(javaType);
    }

    // Private methods
    private static String sqlInsert(String tableName, HashMap<String, Object> attributes_values) {
        String fields = attributes_values.keySet().toString().replace("[", "").replace("]", "");
        String values = attributes_values.values().toString().replace("[", "'").replace("]", "'").replace(", ", "','").replace("'null'", "null");
        return "insert into  " + tableName + "(" + fields + ")" + "values (" + values + ")";
    }

    private static void setTypesMapping() {
        typesMapping = new HashMap<String, String>();
        typesMapping.put("int", "int");
        typesMapping.put("Integer", "int");
        typesMapping.put("String", "varchar(" + Configuration.SqlConverterConfig.varcharLength + ")");
    }
}
