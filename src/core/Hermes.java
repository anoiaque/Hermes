package core;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import adaptors.MySqlAdaptor;

@SuppressWarnings("unchecked")
public class Hermes {

    private String tableName;
    private HashMap<String, String> fieldsType = null;
    private HashMap<String, Object> fieldsValue = null;
    private HashMap<String, Relationnal> hasOneRelationsShip = new HashMap<String, Relationnal>();
    private HashMap<String, Relationnal> hasManyRelationsShip = new HashMap<String, Relationnal>();
    private int id = 0;

    // Constructeurs
    public Hermes() {
        setTableName(Pluralizer.getPlurial(this.getClass().getSimpleName()));
        setFields();
    }

    // Public methods
    public boolean save() {
        setFieldsValue();
        saveHasOneRelations();
        HashMap<String, Object> attributes_values = (HashMap<String, Object>) fieldsValue.clone();
        attributes_values.putAll(foreignKeys());
        this.id = MySqlAdaptor.save(this.tableName, attributes_values);
        saveHasManyRelations();
        return this.id != -1;
    }

    public boolean delete() {
        cascadeDelete();
        return MySqlAdaptor.delete(this.tableName, this.id);
    }

    public boolean delete(String whereClause) {
        cascadeDelete();
        return MySqlAdaptor.delete(this.tableName, whereClause);
    }

    public boolean find(int id) {
        Hermes object = MySqlAdaptor.find(tableName, id, this);
        if (object == null)
            return false;
        findRelationnalFields(object);
        return true;
    }

    public Set<?> find(String select_clause, String where_clause) {
        Set<Hermes> objects = MySqlAdaptor.find(this.tableName, select_clause, where_clause, this);
        for (Hermes object : objects)
            findRelationnalFields(object);
        return objects;
    }

    public void hasOne(String attribute, Relationnal rc) {
        rc.setForeignKeyName(foreignKeyName(attribute));
        hasOneRelationsShip.put(attribute, rc);
        setFields();
    }

    public void hasOne(String attribute) {
        Relationnal rc = new Relationnal();
        rc.setForeignKeyName(foreignKeyName(attribute));
        hasOneRelationsShip.put(attribute, rc);
        setFields();
    }

    public void hasMany(String attribute, Relationnal rc) {
        try {
            ParameterizedType setField = (ParameterizedType) this.getClass().getDeclaredField(attribute).getGenericType();
            String joinTableName = (this.tableName + "_" + ((Class<?>) setField.getActualTypeArguments()[0]).getSimpleName()).toUpperCase();
            Jointure jointure = new Jointure(joinTableName);
            rc.setJointure(jointure);
            hasManyRelationsShip.put(attribute, rc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setFields();
    }

    public void hasMany(String attribute) {
        Relationnal rc = new Relationnal();
        try {
            ParameterizedType setField = (ParameterizedType) this.getClass().getDeclaredField(attribute).getGenericType();
            String joinTableName = (this.tableName + "_" + ((Class<?>) setField.getActualTypeArguments()[0]).getSimpleName()).toUpperCase();
            Jointure jointure = new Jointure(joinTableName);
            rc.setJointure(jointure);
            hasManyRelationsShip.put(attribute, rc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setFields();
    }

    // Private methods
    private void saveHasManyRelations() {
        for (String attribute : hasManyRelationsShip.keySet()) {
            try {
                Jointure jointure = hasManyRelationsShip.get(attribute).getJointure();
                Field field = this.getClass().getDeclaredField(attribute);
                field.setAccessible(true);
                Set<Hermes> fieldSet = (Set<Hermes>) field.get(this);
                if (fieldSet != null)
                    for (Hermes occurence : fieldSet) {
                        occurence.save();
                        jointure.setLeftId(this.getId());
                        jointure.setRightId(occurence.getId());
                        jointure.save();
                    }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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

    private HashMap<String, Object> foreignKeys() {
        HashMap<String, Object> fkHash = new HashMap<String, Object>();
        Iterator<String> attributes = hasOneRelationsShip.keySet().iterator();
        while (attributes.hasNext()) {
            Relationnal rel = hasOneRelationsShip.get(attributes.next());
            fkHash.put(rel.getForeignKeyName(), (rel.getForeignKeyValue() == -1) ? null : rel.getForeignKeyValue());
        }
        return fkHash;
    }

    private void setFieldsType() {
        fieldsType = new HashMap<String, String>();
        for (Field field : this.getClass().getDeclaredFields())
            if (isBasicField(field.getName()))
                fieldsType.put(field.getName(), MySqlAdaptor.javaToSql(field.getType().getSimpleName()));
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

    private void cascadeDelete() {
        deleteHasOneRelations();
        deleteHasManyRelations();
    }

    private void deleteHasManyRelations() {
        for (String attribute : hasManyRelationsShip.keySet())
            if (hasManyRelationsShip.get(attribute).isCascadeDelete()) {
                try {
                    Jointure jointure = hasManyRelationsShip.get(attribute).getJointure();
                    jointure.delete("leftId=" + this.getId());
                    Field field = this.getClass().getDeclaredField(attribute);
                    field.setAccessible(true);
                    Set<Hermes> objects = (Set<Hermes>) field.get(this);
                    if (objects != null)
                        for (Hermes obj : objects) {
                            obj.delete();
                        }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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

    private void findRelationnalFields(Hermes object) {
        findHasOneRelationFields(object);
        findHasManyRelationFields(object);
    }

    private void findHasManyRelationFields(Hermes object) {
        for (String attr : hasManyRelationsShip.keySet()) {
            try {
                Set<Hermes> objects = new HashSet<Hermes>();
                Field field = this.getClass().getDeclaredField(attr);
                Jointure jointure = hasManyRelationsShip.get(attr).getJointure();
                Set<Jointure> jointures = (Set<Jointure>) jointure.find("*", "leftId = " + this.getId());
                ParameterizedType type = (ParameterizedType) this.getClass().getDeclaredField(attr).getGenericType();
                Class<?> classe = (Class<?>) type.getActualTypeArguments()[0];
                for (Jointure join : jointures) {
                    Hermes obj = (Hermes) classe.newInstance();
                    obj.find(join.getRightId());
                    objects.add(obj);
                }
                field.setAccessible(true);
                field.set(this, objects);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void findHasOneRelationFields(Hermes object) {
        for (String attr : hasOneRelationsShip.keySet()) {
            try {
                Field field = object.getClass().getDeclaredField(attr);
                Hermes obj = (Hermes) field.getType().newInstance();
                obj.find(hasOneRelationsShip.get(attr).getForeignKeyValue());
                field.setAccessible(true);
                field.set(object, obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String foreignKeyName(String attribute) {
        return attribute + "_id";
    }

    private void setFields() {
        setFieldsType();
        setFieldsValue();
    }

    private boolean isBasicField(String attributeName) {
        return !(hasOneRelationsShip.containsKey(attributeName) || hasManyRelationsShip.containsKey(attributeName));
    }

    // Getters & Setters
    public HashMap<String, Relationnal> getHasManyRelationsShip() {
        return hasManyRelationsShip;
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

    public HashMap<String, Relationnal> getHasOneRelationsShip() {
        return hasOneRelationsShip;
    }

    public void setFieldsType(HashMap<String, String> fieldsType) {
        this.fieldsType = fieldsType;
    }

    public HashMap<String, String> getFieldsType() {
        return fieldsType;
    }
}
