package core;

import adaptors.Adaptor;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.HashMap;
import pool.Pool;

public class Jointure extends Hermes {

  private int parentId;
  private int childId;

  public Jointure() {
  }

  // Constructeurs
  public Jointure(Hermes object, String attribute) {
    setTableName(createJoinTableName(object, attribute));
    createJoinTable();
  }

  // Public methods
  @Override
  public boolean save() {
    Fields.setFieldsValue(this);
    HashMap<String, Object> attributes_values = ((HashMap<String, Object>) getFieldsValue().clone());
    Adaptor.get().save(attributes_values, getTableName());
    return true;
  }

  private String createJoinTableName(Hermes object, String attribute) {
    ParameterizedType setField;
    String parentName = object.getTableName().toUpperCase();
    String childName = "";
    try {
      setField = (ParameterizedType) object.getClass().getDeclaredField(attribute).getGenericType();
      childName = (((Class<?>) setField.getActualTypeArguments()[0]).getSimpleName()).toUpperCase();
    }
    catch (Exception e) {
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
    }
    catch (SQLException e) {
      e.printStackTrace();
    }
    finally {
      try {
        if (rs != null)
          rs.close();
        pool.release(connexion);
      }
      catch (SQLException e) {
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
