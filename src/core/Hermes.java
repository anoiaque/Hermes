package core;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Set;

import adaptors.MySqlAdaptor;

public class Hermes {

    private String tableName;
    private HashMap<String, String> fieldsType = null;
    private HashMap<String, Object> fieldsValue = null;
    private Relational relations = new Relational(this);
    private int id = 0;

    // Constructeur
    public Hermes() {
        setTableName(Pluralizer.getPlurial(this.getClass().getSimpleName()));
        setFields();
    }

    // Public methods
    @SuppressWarnings("unchecked")
    public boolean save() {
        if (this.id == 0) {
            setFieldsValue();
            relations.saveHasOneRelations();
            HashMap<String, Object> attributes_values = ((HashMap<String, Object>) fieldsValue.clone());
            attributes_values.putAll(relations.foreignKeys());
            this.id = MySqlAdaptor.save(this.tableName, attributes_values);
            relations.saveHasManyRelations();
            return this.id != -1;
        }
        else return update();
    }

    @SuppressWarnings("unchecked")
    private boolean update() {
        setFieldsValue();
        relations.updateHasOneRelations();
        HashMap<String, Object> attributes_values = (HashMap<String, Object>) fieldsValue.clone();
        attributes_values.putAll(relations.foreignKeys());
        boolean updated = MySqlAdaptor.update(this.tableName, attributes_values, this.id);
        relations.updateHasManyRelations();
        return updated;
    }

    public boolean delete() {
        relations.cascadeDelete();
        boolean deleted = MySqlAdaptor.delete(this.tableName, this.id);
        this.id = 0;
        return deleted;
    }

    public boolean delete(String whereClause) {
        relations.cascadeDelete();
        boolean deleted = MySqlAdaptor.delete(this.tableName, whereClause);
        this.id = 0;
        return deleted;
    }

    
    public boolean find(int id) {
        Hermes object = MySqlAdaptor.find(id, this);
        if (object == null) return false;
        relations.getRelationalFields(object);
        return true;
    }

   
    public Set<?> find(String where_clause) {
        Set<Hermes> objects = MySqlAdaptor.find("*", where_clause, this);
        return (Set<Hermes>) loadRelationals(objects);
    }
    public Set<?> find(String select_clause, String where_clause) {
        Set<Hermes> objects = MySqlAdaptor.find(select_clause, where_clause, this);
        return (Set<Hermes>) loadRelationals(objects);
    }

    public Set<?> findAll() {
        Set<Hermes> objects = MySqlAdaptor.find("*", null, this);
        return (Set<Hermes>) loadRelationals(objects);
    }
    public Set<?> findBySql(String sqlRequest){
        return null;
    }
    
    public void hasOne(String attribute, Relation rc) {
        relations.hasOne(attribute, rc);
        setFields();
    }

    public void hasOne(String attribute) {
        relations.hasOne(attribute);
        setFields();
    }

    public void hasMany(String attribute, Relation rc) {
        relations.hasMany(attribute, rc);
        setFields();
    }

    public void hasMany(String attribute) {
        relations.hasMany(attribute);
        setFields();
    }

    // Private methods
    private Set<Hermes> loadRelationals(Set<Hermes> objects) {
        for (Hermes object : objects)
            relations.getRelationalFields(object);
        return objects;
    }

    private void setFieldsType() {
        fieldsType = new HashMap<String, String>();
        for (Field field : this.getClass().getDeclaredFields())
            if (isBasicField(field.getName())) fieldsType.put(field.getName(), MySqlAdaptor.javaToSql(field.getType().getSimpleName()));
    }

    private void setFieldsValue() {
        fieldsValue = new HashMap<String, Object>();
        for (Field field : this.getClass().getDeclaredFields())
            if (isBasicField(field.getName())) {
                field.setAccessible(true);
                try {
                    fieldsValue.put(field.getName(), field.get(this));
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
    }

    private void setFields() {
        setFieldsType();
        setFieldsValue();
    }

    private boolean isBasicField(String attributeName) {
        return !(relations.getHasOneRelationsShip().containsKey(attributeName) || relations.getHasManyRelationsShip().containsKey(attributeName));
    }

    // Getters & Setters
    public HashMap<String, Relation> getHasManyRelationsShip() {
        return relations.getHasManyRelationsShip();
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
}
