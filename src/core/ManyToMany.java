package core;

import java.util.Set;

public class ManyToMany {

	private String		attributeName;
	private Jointure	jointure;
	private boolean		cascadeDelete	= false;

	public ManyToMany(String attribute, String dependency, Hermes parent) {
		this.attributeName = attribute;
		jointure = new Jointure(parent, attribute);
		cascadeDelete = dependency.equals("dependent:destroy");
	}

	public boolean save(Hermes parent) {
		Set<Hermes> set = (Set<Hermes>) Introspector.getObject(attributeName, parent);

		if (set == null) return true;
		jointure.clear(parent);
		
		for (Hermes occurence : set) {
			if (!occurence.save()) return false;
			if (!jointure.save(parent.getId(), occurence.getId())) return false;
		}
		return true;
	}

	public void delete(Hermes parent) {
		if (!cascadeDelete) return;
		Set<Hermes> objects = (Set<Hermes>) Introspector.getObject(attributeName, parent);

		if (objects == null) return;
		jointure.delete("parentId=" + parent.getId());
		for (Hermes obj : objects)
			obj.delete();
	}

	// Getters & Setters
	public Jointure getJointure() {
		return jointure;
	}

	public boolean isCascadeDelete() {
		return cascadeDelete;
	}

	public void setCascadeDelete(boolean cascadeDelete) {
		this.cascadeDelete = cascadeDelete;
	}

}
