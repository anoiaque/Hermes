package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Associations {

	Hermes															parent;
	private HashMap<String, HasOne>			hasOne			= new HashMap<String, HasOne>();
	private HashMap<String, ManyToMany>	manyToMany	= new HashMap<String, ManyToMany>();
	private HashMap<String, HasMany>		hasMany			= new HashMap<String, HasMany>();
	private List<BelongsTo>							belongsTo		= new ArrayList<BelongsTo>();

	public Associations(Hermes object) {
		this.parent = object;
	}

	public void hasOne(String attribute, String dependency) {
		hasOne.put(attribute, new HasOne(attribute, dependency));
	}

	public void hasMany(String attribute, String dependency) {
		hasMany.put(attribute, new HasMany(attribute, dependency));
	}

	public void manyToMany(String attribute, String dependency) {
		manyToMany.put(attribute, new ManyToMany(attribute, dependency, parent));
	}

	public void belongsTo(Hermes object) {
		belongsTo.add(new BelongsTo(object));
	}

	public boolean save() {
		return saveHasMany() && saveHasOne() && saveManyToMany();
	}

	public void delete() {
		deleteHasOne();
		deleteManyToMany();
		deleteHasMany();
	}

	// Private methods
	private boolean saveHasMany() {
		for (HasMany relation : hasMany.values())
			if (!relation.save(parent)) return false;
		return true;
	}

	private boolean saveHasOne() {
		for (HasOne relation : hasOne.values())
			if (!relation.save(parent)) return false;
		return true;
	}

	private boolean saveManyToMany() {
		for (ManyToMany relation : manyToMany.values())
			if (!relation.save(parent)) return false;
		return true;
	}

	private void deleteHasOne() {
		for (HasOne relation : hasOne.values())
			relation.delete(parent);
	}

	private void deleteHasMany() {
		for (HasMany relation : hasMany.values())
			relation.delete(parent);
	}

	private void deleteManyToMany() {
		for (ManyToMany relation : manyToMany.values())
			relation.delete(parent);
	}

	// Getters & Setters
	public HashMap<String, HasOne> getHasOneAssociations() {
		return hasOne;
	}

	public HashMap<String, ManyToMany> getManyToManyAsociations() {
		return manyToMany;
	}

	public HashMap<String, HasMany> getHasManyAssociations() {
		return hasMany;
	}

	public List<BelongsTo> getBelongsToAssociations() {
		return belongsTo;
	}

}
