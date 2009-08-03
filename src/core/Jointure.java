package core;

import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import pool.Pool;

public class Jointure extends Hermes {

    private int parentId;
    private int childId;

    // Constructeurs
    public Jointure() {
    }

    public Jointure(Hermes object, String attribute) {
        this.setTableName(createJoinTableName(object, attribute));
        createJoinTable();
    }

    // Public methods
    private static String createJoinTableName(Hermes object, String attribute) {
        ParameterizedType setField;
        String parentName = object.getTableName().toUpperCase();
        String childName = "";
        try {
            setField = (ParameterizedType) object.getClass().getDeclaredField(attribute).getGenericType();
            childName = (((Class<?>) setField.getActualTypeArguments()[0]).getSimpleName()).toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return parentName + "_" + childName;
    }

    // Private methods
    private void createJoinTable() {
        Connection connexion = null;
        Pool pool = Pool.getInstance();
        String sql = "create  table if not exists " + this.getTableName() + "(" + "parentId int default null,childId int default null); ";
        ResultSet rs = null;
        try {
            connexion = pool.getConnexion();
            PreparedStatement statement = connexion.prepareStatement(sql);
            statement.execute();
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
    }

    // Getters & setters
    public int getParentId() {
        return parentId;
    }

    public void setParentId(int leftId) {
        this.parentId = leftId;
    }

    public int getChildId() {
        return childId;
    }

    public void setChildId(int rightId) {
        this.childId = rightId;
    }
}
