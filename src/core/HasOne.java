package core;

public class HasOne {

	private String	attributeName;
	private boolean	cascadeDelete	= false;

	public HasOne(String attribute, String dependency) {
		attributeName = attribute;
		cascadeDelete = dependency.equals("dependent:destroy");
	}

	public void delete(Hermes parent) {
		if (!cascadeDelete) return;
		Hermes object = (Hermes) Introspector.getObject(attributeName, parent);
		if (object != null) object.delete();
	}

	// Getters & Setters
	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public boolean isCascadeDelete() {
		return cascadeDelete;
	}

	public void setCascadeDelete(boolean cascadeDelete) {
		this.cascadeDelete = cascadeDelete;
	}
}
