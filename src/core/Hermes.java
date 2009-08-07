package core;

import java.util.HashMap;
import java.util.Set;

public class Hermes {

  private String tableName;
  private int id = 0;
  private HashMap<String, String> fieldsType = null;
  private HashMap<String, Object> fieldsValue = null;
  private Relational relations = new Relational(this);

  // Constructeurs
  public Hermes() {
    setTableName(Pluralizer.getPlurial(this.getClass().getSimpleName()));
    Fields.setFields(this);
  }

  // Public methods
  public boolean save() {
    return Updater.save(this);
  }

  public boolean delete() {
    return Updater.delete(this);
  }

  public boolean delete(String conditions) {
    return Updater.delete(conditions, this);
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
    Fields.setFields(this);
  }

  public void hasOne(String attribute) {
    hasOne(attribute, new Relation());
  }

  public void manyToMany(String attribute, Relation rc) {
    relations.manyToMany(attribute, rc);
    Fields.setFields(this);
  }

  public void manyToMany(String attribute) {
    manyToMany(attribute, new Relation());
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
    return this.tableName;
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

  public HashMap<String, String> getFieldsType() {
    return fieldsType;
  }

  public Relational getRelations() {
    return relations;
  }

  public HashMap<String, Object> getFieldsValue() {
    return fieldsValue;
  }

  public void setFieldsType(HashMap<String, String> fieldsType) {
    this.fieldsType = fieldsType;
  }

  public void setFieldsValue(HashMap<String, Object> fieldsValue) {
    this.fieldsValue = fieldsValue;
  }

  public void setRelations(Relational relations) {
    this.relations = relations;
  }
}
