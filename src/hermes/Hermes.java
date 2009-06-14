package hermes;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import com.mysql.jdbc.Statement;

public class Hermes {
    private String                             table_name;
    private HashMap<String, String>            fieldsType       = null;
    private HashMap<String, Object>            fieldsValue      = null;
    private HashMap<String, RelationnalConfig> hasOneRelations  = new HashMap<String, RelationnalConfig>();
    private HashMap<String, RelationnalConfig> hasManyRelations = new HashMap<String, RelationnalConfig>();
    private int                                id               = 0;

    // Public methods
    public boolean save() {
        Connection connexion = null;
        ConnectionPool pool = ConnectionPool.getInstance();
        setFieldsType();
        setFieldsValue();
        String fields = fieldsType.keySet().toString().replace("[", "").replace("]", "");
        String values = getSQLValues();
        String sql = "insert into  " + table_name + "(" + fields + ")" + "values (" + values + ")";
        boolean saved = false;
        ResultSet rs = null;
        try {
            connexion = pool.getConnexion();
            PreparedStatement statement = connexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            saved = statement.execute();
            rs = statement.getGeneratedKeys();
            if (rs.next())
                this.id = rs.getInt(1);
        } catch (SQLException e) {
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
        return saved;
    }

    public boolean delete() {
        Connection connexion = null;
        ConnectionPool pool = ConnectionPool.getInstance();
        boolean deleted = false;
        String sql = "delete from " + table_name + " where id =" + this.getId();
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

    public boolean find(int id) {
        Connection connexion = null;
        ConnectionPool pool = ConnectionPool.getInstance();
        String sql = "select * from " + table_name + " where id =" + id;
        boolean found = false;
        ResultSet rs = null;
        try {
            connexion = pool.getConnexion();
            PreparedStatement statement = connexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            rs = statement.executeQuery();
            if (rs.next()) {
                found = true;
                for (Field field : this.getClass().getDeclaredFields()) {
                    field.setAccessible(true);
                    field.set(this, rs.getObject(field.getName()));
                }
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
        return found;
    }

    public void hasOne(String attributeName, RelationnalConfig rc) {
        Class<?> classe = getClassOf(attributeName);
        rc.setClasse(classe);
        hasOneRelations.put(attributeName, rc);
        setFieldsType();
        setFieldsValue();
    }

    public void hasOne(String attributeName) {
        RelationnalConfig rc = new RelationnalConfig();
        Class<?> classe = getClassOf(attributeName);
        rc.setClasse(classe);
        hasOneRelations.put(attributeName, rc);
        setFieldsType();
        setFieldsValue();
    }

    // Constructeurs
    public Hermes() {
        setTable_name(this.getClass().getSimpleName());
        setFieldsType();
        setFieldsValue();
    }

    // Private methods
    private String SQLType(String simplename) {
        if (simplename.equals("int"))
            return "int";
        else if (simplename.equals("String"))
            return "varchar(250)";
        else
            return null;
    }

    private String getSQLValues() {
        Object[] values = fieldsValue.values().toArray();
        String valuesForSQL = "";
        for (int i = 0; i < values.length; i++) {
            valuesForSQL += "'" + values[i] + "'";
            if (i < values.length - 1)
                valuesForSQL += ",";
        }
        return valuesForSQL;
    }

    // Getters & Setters
    // Génrère la liste de tous les attributs basiques (ni relation has_one ni
    // relation has_many)
    private void setFieldsType() {
        fieldsType = new HashMap<String, String>();
        for (Field field : this.getClass().getDeclaredFields())
            if (isBasicField(field.getName()))
                fieldsType.put(field.getName(), SQLType(field.getType().getSimpleName()));
    }

    private void setFieldsValue() {
        fieldsValue = new HashMap<String, Object>();
        for (Field field : this.getClass().getDeclaredFields())
            if (isBasicField(field.getName())) {
                field.setAccessible(true);
                try {
                    fieldsValue.put(field.getName(), field.get(this));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
    }

    private boolean isBasicField(String fieldName) {
        boolean res = !(hasOneRelations.containsKey(fieldName) || hasManyRelations.containsKey(fieldName));
        return res;
    }

    private Class<?> getClassOf(String attributeName) {
        try {
            Field field = this.getClass().getDeclaredField(attributeName);
            return field.getType();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return Object.class;
    }

    public void setTable_name(String table_name) {
        this.table_name = table_name;
    }

    public HashMap<String, String> getDatabaseFields() {
        return fieldsType;
    }

    public String getTable_name() {
        return table_name;
    }

    public int getId() {
        return id;
    }

    public HashMap<String, RelationnalConfig> getHasOneRelations() {
        return hasOneRelations;
    }

    public void setHasOneRelations(HashMap<String, RelationnalConfig> hasOneRelations) {
        this.hasOneRelations = hasOneRelations;
    }

    public HashMap<String, RelationnalConfig> getHasManyRelations() {
        return hasManyRelations;
    }

    public void setHasManyRelations(HashMap<String, RelationnalConfig> hasManyRelations) {
        this.hasManyRelations = hasManyRelations;
    }
}
