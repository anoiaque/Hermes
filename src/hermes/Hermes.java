package hermes;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;

import com.mysql.jdbc.Statement;

public class Hermes {
    private String                             table_name;
    private HashMap<String, String>            fieldsType           = null;
    private HashMap<String, Object>            fieldsValue          = null;
    private HashMap<String, RelationnalConfig> hasOneRelationsShip  = new HashMap<String, RelationnalConfig>();
    private HashMap<String, RelationnalConfig> hasManyRelationsShip = new HashMap<String, RelationnalConfig>();
    private int                                id                   = 0;

    // Public methods
    public boolean save() {
        Connection connexion = null;
        ConnectionPool pool = ConnectionPool.getInstance();
        setFieldsType();
        setFieldsValue();
        saveHasOneRelations();
        String fields = fieldsType.keySet().toString().replace("[", "").replace("]", "");
        String values = getSQLValues();
        fields += foreignKeys();
        values += foreignKeysValues();
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

    private String foreignKeysValues() {
        String values = "";
        Iterator<String> attributs = hasOneRelationsShip.keySet().iterator();
        while (attributs.hasNext()) {
            values += ",";
            int fkvalue = hasOneRelationsShip.get(attributs.next()).getForeignKeyValue();
            values += (fkvalue == -1) ? null : fkvalue;
        }
        return values;
    }

    private String foreignKeys() {
        String fKeys = "";
        Iterator<String> attributs = hasOneRelationsShip.keySet().iterator();
        while (attributs.hasNext()) {
            fKeys += ",";
            fKeys += hasOneRelationsShip.get(attributs.next()).getForeignKeyName();
        }
        return fKeys;
    }

    private void saveHasOneRelations() {
        for (String attribute : hasOneRelationsShip.keySet()) {
            try {
                Field field = this.getClass().getDeclaredField(attribute);
                field.setAccessible(true);
                Hermes obj = (Hermes) field.get(this);
                if (obj != null) {
                    obj.save();
                    hasOneRelationsShip.get(attribute).setForeignKeyValue(obj.getId());
                } else
                    hasOneRelationsShip.get(attribute).setForeignKeyValue(-1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
        deleteHasOneRelations();
        return deleted;
    }

    private void deleteHasOneRelations() {
        for (String attribute : hasOneRelationsShip.keySet())
            if (hasOneRelationsShip.get(attribute).isCascadeDelete()) {
                try {
                    Field field = this.getClass().getDeclaredField(attribute);
                    field.setAccessible(true);
                    Hermes obj = (Hermes) field.get(this);
                    if (obj != null)
                        obj.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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
                for (String attribute : this.fieldsType.keySet()) {
                    Field field = this.getClass().getDeclaredField(attribute);
                    field.setAccessible(true);
                    field.set(this, rs.getObject(field.getName()));
                }
                this.id = id;
                setRelationnalFields(rs);
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

    private void setRelationnalFields(ResultSet rs) {
        for (String attr : hasOneRelationsShip.keySet()) {
            try {
                Field field = this.getClass().getDeclaredField(attr);
                Hermes obj = (Hermes) field.getType().newInstance();
                obj.find(hasOneRelationsShip.get(attr).getForeignKeyValue());
                field.setAccessible(true);
                field.set(this, obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void hasOne(String attribute, RelationnalConfig rc) {
        rc.setForeignKeyName(attribute + "_id");
        hasOneRelationsShip.put(attribute, rc);
        setFieldsType();
        setFieldsValue();
    }

    public void hasOne(String attribute) {
        RelationnalConfig rc = new RelationnalConfig();
        rc.setForeignKeyName(attribute + "_id");
        hasOneRelationsShip.put(attribute, rc);
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

    private boolean isBasicField(String attributeName) {
        return !(hasOneRelationsShip.containsKey(attributeName) || hasManyRelationsShip.containsKey(attributeName));
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

    public HashMap<String, RelationnalConfig> getHasOneRelationsShip() {
        return hasOneRelationsShip;
    }
}
