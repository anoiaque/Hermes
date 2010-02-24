package core;

import java.util.ArrayList;
import java.util.HashMap;
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

	public boolean save() {
		return saveHasManyAssociations() && saveHasOneAssociations() && saveManyToManyAssociations();
	}

	public void cascadeDelete() {
		deleteHasOneRelations();
		deleteManyToManyRelations();
	}

	// Private methods
	private boolean saveHasManyAssociations() {
		Set<Hermes> set;

		for (String attribute : hasManyAssociations.keySet()) {
			set = (Set<Hermes>) Introspector.getObject(attribute, parent);
			if (set == null) continue;
			for (Hermes occurence : set) {
				occurence.belongsTo(parent);
				if (!occurence.save()) return false;
			}
		}
		return true;
	}

	private boolean saveHasOneAssociations() {
		Hermes object;

		for (String attribute : hasOneAssociations.keySet()) {
			object = (Hermes) Introspector.getObject(attribute, parent);
			if (object == null) continue;
			object.belongsTo(parent);
			if (!object.save()) return false;
		}
		return true;
	}

	private boolean saveManyToManyAssociations() {
		Set<Hermes> set;
		Jointure jointure;

		for (String attribute : manyToManyAssociations.keySet()) {
			set = (Set<Hermes>) Introspector.getObject(attribute, parent);
			if (set == null) continue;
			jointure = manyToManyAssociations.get(attribute).getJointure();
			jointure.clear(parent);
			for (Hermes occurence : set) {
				if (!occurence.save()) return false;
				if (!jointure.save(parent.getId(), occurence.getId())) return false;
			}
		}
		return true;
	}

	private void deleteHasOneRelations() {
		Hermes obj;

		for (String attribute : hasOneAssociations.keySet()) {
			if (!hasOneAssociations.get(attribute).isCascadeDelete()) continue;
			obj = (Hermes) Introspector.getObject(attribute, parent);
			if (obj != null) obj.delete();
		}
	}

	private void deleteManyToManyRelations() {
		Jointure jointure;
		Set<Hermes> objects;

		for (String attribute : manyToManyAssociations.keySet()) {
			jointure = manyToManyAssociations.get(attribute).getJointure();
			jointure.delete("parentId=" + parent.getId());
			if (!manyToManyAssociations.get(attribute).isCascadeDelete()) continue;
			objects = (Set<Hermes>) Introspector.getObject(attribute, parent);
			if (objects == null) continue;
			for (Hermes obj : objects)
				obj.delete();
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

}
