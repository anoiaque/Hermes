package core;

public class ManyToMany {

	Hermes			parent;
	Hermes			child;
	Jointure		jointure;
	private boolean	cascadeDelete	= false;

	public ManyToMany(String attribute, String dependency, Hermes parent) {
		this.parent = parent;
		jointure = new Jointure(parent, attribute);
		cascadeDelete = dependency.equals("dependent:destroy");
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
