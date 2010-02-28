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

	public void delete() {
		deleteHasOneAssociations();
		deleteManyToManyAssociations();
		deleteHasManyAssociations();
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

	private void deleteHasOneAssociations() {
		for (HasOne relation : hasOneAssociations.values())
			relation.delete(parent);
	}

	private void deleteHasManyAssociations() {
		for (HasMany relation : hasManyAssociations.values())
			relation.delete(parent);
	}

	private void deleteManyToManyAssociations() {
		for (ManyToMany relation : manyToManyAssociations.values())
			relation.delete(parent);
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
