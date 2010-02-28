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
		return saveHasMany() && saveHasOne() && saveManyToMany();
	}

	public void delete() {
		deleteHasOne();
		deleteManyToMany();
		deleteHasMany();
	}

	// Private methods
	private boolean saveHasMany() {
		for (HasMany relation : hasManyAssociations.values())
			if (!relation.save(parent)) return false;
		return true;
	}

	private boolean saveHasOne() {
		for (HasOne relation : hasOneAssociations.values())
			if (!relation.save(parent)) return false;
		return true;
	}

	private boolean saveManyToMany() {
		for (ManyToMany relation : manyToManyAssociations.values())
			if (!relation.save(parent)) return false;
		return true;
	}

	private void deleteHasOne() {
		for (HasOne relation : hasOneAssociations.values())
			relation.delete(parent);
	}

	private void deleteHasMany() {
		for (HasMany relation : hasManyAssociations.values())
			relation.delete(parent);
	}

	private void deleteManyToMany() {
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
