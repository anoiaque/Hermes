package core;

import java.lang.reflect.Field;

import java.lang.reflect.ParameterizedType;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Associations {

	Hermes															parent;
	private HashMap<String, HasOne>			hasOneAssociations			= new HashMap<String, HasOne>();
	private HashMap<String, ManyToMany>	manyToManyAssociations	= new HashMap<String, ManyToMany>();
	private HashMap<String, HasMany>		hasManyAssociations			= new HashMap<String, HasMany>();
	private List<BelongsTo>							belongsToAssociations		= new ArrayList<BelongsTo>();

	public Associations(Hermes object) {
		this.parent = object;
	}

	public void hasOne(String attribute, String dependency) {
		hasOneAssociations.put(attribute, new HasOne(attribute, dependency));
	}

	public void hasMany(String attribute, String dependency) {
		hasManyAssociations.put(attribute, new HasMany(attribute, dependency));
	}

	public void manyToMany(String attribute, String dependency) {
		manyToManyAssociations.put(attribute, new ManyToMany(attribute, dependency, parent));
	}

	public void belongsTo(Hermes object) {
		belongsToAssociations.add(new BelongsTo(object));
	}

	public void saveHasManyAssociations() {
		for (String attribute : hasManyAssociations.keySet()) {
			Set<Hermes> setAttribute = (Set<Hermes>) Introspector.getObject(attribute, parent);
			if (setAttribute == null) continue;
			for (Hermes occurence : setAttribute) {
				occurence.belongsTo(parent);
				occurence.save();
			}
		}
	}

	public void saveHasOneAssociations() {
		for (String attribute : hasOneAssociations.keySet()) {
			Hermes object = (Hermes) Introspector.getObject(attribute, parent);
			if (object == null) continue;
			object.save();
			hasOneAssociations.get(attribute).setFkValue(object.getId());
		}
	}

	public void saveManyToManyAssociations() {
		clearKeysPairs();
		for (String attribute : manyToManyAssociations.keySet()) {
			Jointure jointure = manyToManyAssociations.get(attribute).getJointure();
			Set<Hermes> setAttribute = (Set<Hermes>) Introspector.getObject(attribute, parent);
			if (setAttribute != null) {
				for (Hermes occurence : setAttribute) {
					occurence.save();
					jointure.setParentId(parent.getId());
					jointure.setChildId(occurence.getId());
					jointure.save();
				}
			}
		}
	}

	public void cascadeDelete() {
		deleteHasOneRelations();
		deleteManyToManyRelations();
	}

	public void loadRelationalFields(Hermes object, ResultSet rs) {
		loasHasOneAssociations(object, rs);
		loadManyToManyRelationFields(object);
		loadHasManyAssociations(object, rs);
	}

	// Private methods
	private void deleteHasOneRelations() {
		for (String attribute : hasOneAssociations.keySet()) {
			if (hasOneAssociations.get(attribute).isCascadeDelete()) {
				Hermes obj = (Hermes) Introspector.getObject(attribute, parent);
				if (obj != null) {
					obj.delete();
				}
			}
		}
	}

	private void deleteManyToManyRelations() {
		for (String attribute : manyToManyAssociations.keySet()) {
			Jointure jointure = manyToManyAssociations.get(attribute).getJointure();
			jointure.delete("parentId=" + parent.getId());
			if (manyToManyAssociations.get(attribute).isCascadeDelete()) {
				Set<Hermes> objects = (Set<Hermes>) Introspector.getObject(attribute, parent);
				if (objects != null) {
					for (Hermes obj : objects) {
						obj.delete();
					}
				}
			}
		}
	}

	private void clearKeysPairs() {
		for (String attribute : manyToManyAssociations.keySet()) {
			Jointure jointure = manyToManyAssociations.get(attribute).getJointure();
			jointure.delete("parentId=" + parent.getId());
		}
	}

	private void loadManyToManyRelationFields(Hermes object) {
		for (String attr : manyToManyAssociations.keySet()) {
			try {
				Set<Hermes> objects = new HashSet<Hermes>();
				Field field = object.getClass().getDeclaredField(attr);
				Jointure jointure = manyToManyAssociations.get(attr).getJointure();
				Set<Jointure> jointures = (Set<Jointure>) Finder.joinFind(object.getId(), jointure);
				ParameterizedType type = (ParameterizedType) object.getClass().getDeclaredField(attr)
						.getGenericType();
				Class<?> classe = (Class<?>) type.getActualTypeArguments()[0];
				jointures.remove(null);
				for (Jointure join : jointures) {
					Hermes obj = (Hermes) classe.newInstance();
					obj = Finder.find(join.getChildId(), obj.getClass());
					objects.add(obj);
				}
				field.setAccessible(true);
				field.set(object, objects);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void loadHasManyAssociations(Hermes object, ResultSet rs) {
		for (String attribute : hasManyAssociations.keySet()) {
			String fk = Inflector.foreignKeyName(Introspector.className(object));
			Class<Hermes> klass = Introspector.collectionTypeClass(object, attribute);
			Set<?> set = Finder.find(fk + "=" + object.getId(), klass);
			for (Object child : set) {
				Hermes obj = (Hermes) child;
				obj.belongsTo(object);
			}
			try {
				Field field = object.getClass().getDeclaredField(attribute);
				field.setAccessible(true);
				field.set(object, set);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void loasHasOneAssociations(Hermes object, ResultSet rs) {
		for (String attr : hasOneAssociations.keySet()) {
			try {
				object.getAssociations().getHasOneAssociations().get(attr).setFkValue(
						rs.getInt(attr + "_id"));
				Field field = object.getClass().getDeclaredField(attr);
				Hermes obj = (Hermes) field.getType().newInstance();
				obj = Finder.find(hasOneAssociations.get(attr).getFkValue(), obj.getClass());
				field.setAccessible(true);
				field.set(object, obj);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// Getters & Setters
	public HashMap<String, HasOne> getHasOneAssociations() {
		return hasOneAssociations;
	}

	public HashMap<String, ManyToMany> getManyToManyAsociations() {
		return manyToManyAssociations;
	}

	public HashMap<String, HasMany> getHasManyAssociations() {
		return hasManyAssociations;
	}

	public List<BelongsTo> getBelongsToAssociations() {
		return belongsToAssociations;
	}

	public void setHasManyAssociations(HashMap<String, HasMany> hasManyAssociations) {
		this.hasManyAssociations = hasManyAssociations;
	}
}
