package core;

import adaptors.Adaptor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Set;

public class Hermes {

  private String tableName;
  private int id = 0;
  private HashMap<String, String> fieldsType = null;
  private HashMap<String, Object> fieldsValue = null;
  private Relational relations = new Relational(this);
  private Adaptor adaptor = Adaptor.get();

  // Constructeurs
  public Hermes() {
    setTableName(Pluralizer.getPlurial(this.getClass().getSimpleName()));
    setFields();
  }

  // Public methods
  public boolean save() {
    if (this.id == 0) {
      setFieldsValue();
      relations.saveHasOneRelations();
      HashMap<String, Object> attributes_values = ((HashMap<String, Object>) fieldsValue.clone());
      attributes_values.putAll(relations.foreignKeys());
      this.id = adaptor.save(this.tableName, attributes_values);
      relations.saveManyToManyRelations();
      return this.id != -1;
    } else {
      return update();
    }
  }

  public boolean delete() {
    relations.cascadeDelete();
    boolean deleted = adaptor.delete(this.tableName, this.id);
    this.id = 0;
    return deleted;
  }

  public boolean delete(String whereClause) {
    relations.cascadeDelete();
    boolean deleted = adaptor.delete(this.tableName, whereClause);
    this.id = 0;
    return deleted;
  }

  public static Hermes find(int id, Class<? extends Hermes> model) {
    return Finder.find(id, model);
  }

  public Hermes find(int id) {
    return Finder.find(id, this.getClass());
  }

  public static Set<?> find(String conditions, Class<? extends Hermes> model) {
    return Finder.find(conditions, model);
  }

  public Set<?> find(String conditions) {
    return Finder.find(conditions, this.getClass());
  }

  public static Set<?> find(String select, String conditions, Class<? extends Hermes> model) {
    return Finder.find(select, conditions, model);
  }

  public Set<?> find(String select, String conditions) {
    return Finder.find(select, conditions, this.getClass());
  }

  public static Set<?> findAll(Class<? extends Hermes> model) {
    return Finder.find("*", null, model);
  }

  public Set<?> findAll() {
    return Finder.find("*", null, this.getClass());
  }

  public Set<?> findBySql(String sqlRequest) {
    return null;
  }

  public void hasOne(String attribute, Relation rc) {
    relations.hasOne(attribute, rc);
    setFields();
  }

  public void hasOne(String attribute) {
    hasOne(attribute, new Relation());
  }

  public void manyToMany(String attribute, Relation rc) {
    relations.manyToMany(attribute, rc);
    setFields();
  }

  public void manyToMany(String attribute) {
    relations.manyToMany(attribute);
    setFields();
  }

  public static String tableName(Class<? extends Hermes> model) {
    try {
      return (String) model.getMethod("getTableName").invoke(model.newInstance());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
  // Private methods

  private boolean update() {
    setFieldsValue();
    relations.updateHasOneRelations();
    HashMap<String, Object> attributes_values = (HashMap<String, Object>) fieldsValue.clone();
    attributes_values.putAll(relations.foreignKeys());
    boolean updated = adaptor.update(this.tableName, attributes_values, this.id);
    relations.updateManyToManyRelations();
    return updated;
  }

  private void setFieldsType() {
    fieldsType = new HashMap<String, String>();
    for (Field field : this.getClass().getDeclaredFields()) {
      if (isBasicField(field.getName())) {
        fieldsType.put(field.getName(), adaptor.javaToSql(field.getType().getSimpleName()));
      }
    }
  }

  private void setFieldsValue() {
    fieldsValue = new HashMap<String, Object>();
    for (Field field : this.getClass().getDeclaredFields()) {
      if (isBasicField(field.getName())) {
        field.setAccessible(true);
        try {
          fieldsValue.put(field.getName(), field.get(this));
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }

  private void setFields() {
    setFieldsType();
    setFieldsValue();
  }

  private boolean isBasicField(String attributeName) {
    return !(relations.getHasOneRelationsShip().containsKey(attributeName) || relations.getManyToManyRelationsShip().containsKey(attributeName));
  }

  // Getters & Setters
  public HashMap<String, Relation> getManyToManyRelationsShip() {
    return relations.getManyToManyRelationsShip();
  }

  public void setTableName(String table_name) {
    this.tableName = table_name;
  }

  public HashMap<String, String> getDatabaseFields() {
    return getFieldsType();
  }

  public String getTableName() {
    return tableName;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public HashMap<String, Relation> getHasOneRelationsShip() {
    return relations.getHasOneRelationsShip();
  }

  public void setFieldsType(HashMap<String, String> fieldsType) {
    this.fieldsType = fieldsType;
  }

  public HashMap<String, String> getFieldsType() {
    return fieldsType;
  }

  public Relational getRelations() {
    return relations;
  }
}
