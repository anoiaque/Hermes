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

	public void save() {
		saveHasManyAssociations();
		saveHasOneAssociations();
		saveManyToManyAssociations();
	}

	public void cascadeDelete() {
		deleteHasOneRelations();
		deleteManyToManyRelations();
	}

	// Private methods
	private void saveHasManyAssociations() {
		for (String attribute : hasManyAssociations.keySet()) {
			Set<Hermes> set = (Set<Hermes>) Introspector.getObject(attribute, parent);
			if (set == null) continue;
			for (Hermes occurence : set) {
				occurence.belongsTo(parent);
				occurence.save();
			}
		}
	}

	private void saveHasOneAssociations() {
		for (String attribute : hasOneAssociations.keySet()) {
			Hermes object = (Hermes) Introspector.getObject(attribute, parent);
			if (object == null) continue;
			object.belongsTo(parent);
			object.save();
		}
	}

	private void saveManyToManyAssociations() {
		for (String attribute : manyToManyAssociations.keySet()) {
			Set<Hermes> set = (Set<Hermes>) Introspector.getObject(attribute, parent);
			if (set == null) continue;
			Jointure jointure = manyToManyAssociations.get(attribute).getJointure();
			jointure.clear(parent);
			for (Hermes occurence : set) {
				occurence.save();
				jointure.save(parent.getId(), occurence.getId());
			}
		}
	}

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
