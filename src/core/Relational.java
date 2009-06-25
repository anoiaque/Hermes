package core;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@SuppressWarnings("unchecked")
public class Relational {

    Hermes object;
    private HashMap<String, Relation> hasOneRelationsShip = new HashMap<String, Relation>();
    private HashMap<String, Relation> hasManyRelationsShip = new HashMap<String, Relation>();

    // Constructeur
    public Relational(Hermes object) {
        this.object = object;
    }

    // Public methods
    public void hasOne(String attribute, Relation rc) {
        rc.setForeignKeyName(foreignKeyName(attribute));
        hasOneRelationsShip.put(attribute, rc);
    }

    public void hasOne(String attribute) {
        Relation rc = new Relation();
        rc.setForeignKeyName(foreignKeyName(attribute));
        hasOneRelationsShip.put(attribute, rc);
    }

    public void hasMany(String attribute, Relation rc) {
        Jointure jointure = new Jointure(object, attribute);
        rc.setJointure(jointure);
        hasManyRelationsShip.put(attribute, rc);
    }

    public void hasMany(String attribute) {
        Relation rc = new Relation();
        Jointure jointure = new Jointure(object, attribute);
        rc.setJointure(jointure);
        hasManyRelationsShip.put(attribute, rc);
    }

    public void saveHasManyRelations() {
        for (String attribute : hasManyRelationsShip.keySet()) {
            Jointure jointure = hasManyRelationsShip.get(attribute).getJointure();
            Set<Hermes> fieldSet = (Set<Hermes>) getObject(attribute);
            if (fieldSet != null)
                for (Hermes occurence : fieldSet) {
                    occurence.save();
                    jointure.setLeftId(object.getId());
                    jointure.setRightId(occurence.getId());
                    jointure.save();
                }
        }
    }

    public void updateHasManyRelations() {
        for (String attribute : hasManyRelationsShip.keySet()) {
            Jointure jointure = hasManyRelationsShip.get(attribute).getJointure();
            Set<Hermes> fieldSet = (Set<Hermes>) getObject(attribute);
            clearKeysPairsFor(object, attribute);
            if (fieldSet != null)
                for (Hermes occurence : fieldSet) {
                    occurence.save();
                    jointure.setLeftId(object.getId());
                    jointure.setRightId(occurence.getId());
                    jointure.save();
                }
        }
    }

    public void saveHasOneRelations() {
        for (String attribute : hasOneRelationsShip.keySet()) {
            Hermes obj = (Hermes) getObject(attribute);
            if (obj != null) {
                obj.save();
                hasOneRelationsShip.get(attribute).setForeignKeyValue(obj.getId());
            } else
                hasOneRelationsShip.get(attribute).setForeignKeyValue(-1);
        }
    }

    public void updateHasOneRelations() {
        for (String attribute : hasOneRelationsShip.keySet()) {
            Hermes obj = (Hermes) getObject(attribute);
            if (obj != null) {
                obj.save();
                hasOneRelationsShip.get(attribute).setForeignKeyValue(obj.getId());
            }
        }
    }

    public void cascadeDelete() {
        deleteHasOneRelations();
        deleteHasManyRelations();
    }

    public void getRelationalFields(Hermes object) {
        getHasOneRelationFields(object);
        getHasManyRelationFields(object);
    }

    public HashMap<String, Object> foreignKeys() {
        HashMap<String, Object> fkHash = new HashMap<String, Object>();
        Iterator<String> attributes = hasOneRelationsShip.keySet().iterator();
        while (attributes.hasNext()) {
            Relation rel = hasOneRelationsShip.get(attributes.next());
            fkHash.put(rel.getForeignKeyName(), (rel.getForeignKeyValue() == -1) ? null : rel.getForeignKeyValue());
        }
        return fkHash;
    }

    // Private methods
    private void deleteHasOneRelations() {
        for (String attribute : hasOneRelationsShip.keySet())
            if (hasOneRelationsShip.get(attribute).isCascadeDelete()) {
                Hermes obj = (Hermes) getObject(attribute);
                if (obj != null)
                    obj.delete();
            }
    }

    private void deleteHasManyRelations() {
        for (String attribute : hasManyRelationsShip.keySet()) {
            Jointure jointure = hasManyRelationsShip.get(attribute).getJointure();
            jointure.delete("leftId=" + object.getId());
            if (hasManyRelationsShip.get(attribute).isCascadeDelete()) {
                Set<Hermes> objects = (Set<Hermes>) getObject(attribute);
                if (objects != null)
                    for (Hermes obj : objects) {
                        obj.delete();
                    }
            }
        }
    }

    private void clearKeysPairsFor(Hermes object, String attribute) {
        Jointure jointure = hasManyRelationsShip.get(attribute).getJointure();
        jointure.delete("leftId=" + object.getId());
    }

    private void getHasManyRelationFields(Hermes object) {
        for (String attr : hasManyRelationsShip.keySet()) {
            try {
                Set<Hermes> objects = new HashSet<Hermes>();
                Field field = object.getClass().getDeclaredField(attr);
                Jointure jointure = hasManyRelationsShip.get(attr).getJointure();
                Set<Jointure> jointures = (Set<Jointure>) jointure.find("*", "leftId = " + object.getId());
                ParameterizedType type = (ParameterizedType) object.getClass().getDeclaredField(attr).getGenericType();
                Class<?> classe = (Class<?>) type.getActualTypeArguments()[0];
                for (Jointure join : jointures) {
                    Hermes obj = (Hermes) classe.newInstance();
                    obj.find(((Jointure) join).getRightId());
                    objects.add(obj);
                }
                field.setAccessible(true);
                field.set(object, objects);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getHasOneRelationFields(Hermes object) {
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

    private Object getObject(String attribute) {
        Field field;
        try {
            field = object.getClass().getDeclaredField(attribute);
            field.setAccessible(true);
            return field.get(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Getters & Setters
    public HashMap<String, Relation> getHasOneRelationsShip() {
        return hasOneRelationsShip;
    }

    public HashMap<String, Relation> getHasManyRelationsShip() {
        return hasManyRelationsShip;
    }
}
