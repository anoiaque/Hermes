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

	public void delete(Hermes parent) {
		if (!cascadeDelete) return;
		Set<Hermes> objects = (Set<Hermes>) Introspector.getObject(attributeName, parent);

		if (objects == null) return;
		jointure.delete("parentId=" + parent.getId());
		for (Hermes obj : objects)
			obj.delete();
	}

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
