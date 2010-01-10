package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Hermes {

  private int id = 0;
  private String tableName;
  private ArrayList<Attribute> attributes = new ArrayList<Attribute>();
  private Associations associations = new Associations(this);

  // Constructeurs
  public Hermes() {
    tableName = (Inflector.pluralize(this.getClass().getSimpleName()));
  }

  // Public methods
  public boolean save() {
    return Updater.save(this);
  }

  public boolean delete() {
    return Updater.delete(this);
  }

  public boolean delete(String conditions) {
    return Updater.delete(this, conditions);
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

  public boolean isNewRecord() {
    return (id == 0);
  }

  public void hasMany(String attribute, Relation rc) {
    associations.hasMany(attribute, rc);
  }

  public void hasMany(String attribute) {
    hasMany(attribute, new Relation());
  }

  public void hasOne(String attribute, String dependency) {
    associations.hasOne(attribute, dependency);
  }

  public void hasOne(String attribute) {
    hasOne(attribute);
  }

  public void manyToMany(String attribute, Relation rc) {
    associations.manyToMany(attribute, rc);
  }

  public void manyToMany(String attribute) {
    manyToMany(attribute, new Relation());
  }

   public void loadAttributes() {
    this.attributes = (ArrayList<Attribute>) Attribute.setAttributesFor(this);

  }

// Getters & Setters
  public HashMap<String, Relation> getManyToManyRelationsShip() {
    return associations.getManyToManyRelationsShip();
  }

  public HashMap<String, HasOne> getHasOneAssociations() {
    return associations.getHasOneAssociations();
  }

  public HashMap<String, Relation> getHasManyRelationsShip() {
    return associations.getHasManyRelationsShip();
  }

  public void setTableName(String table_name) {
    this.tableName = table_name;
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

  public Associations getAssociations() {
    return associations;
  }

  public void setAssociations(Associations relations) {
    this.associations = relations;
  }

  public List<Attribute> getAttributes() {
    return attributes;
  }

  
 
}
