package core;

public class HasMany {

	private String	attributeName;
	private String	fkName;
	private Integer	fkValue			= null;
	private boolean	cascadeDelete	= false;

	// Constuctors
	public HasMany(String attribute, String dependency) {
		attributeName = attribute;
		fkName = Inflector.foreignKeyName(attribute);
		cascadeDelete = dependency.equals("dependent:destroy");
	}

	// Getters & Setters

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public String getFkName() {
		return fkName;
	}

	public void setFkName(String fkName) {
		this.fkName = fkName;
	}

	public Integer getFkValue() {
		return fkValue;
	}

	public void setFkValue(Integer fkValue) {
		this.fkValue = fkValue;
	}

	public boolean isCascadeDelete() {
		return cascadeDelete;
	}

	public void setCascadeDelete(boolean cascadeDelete) {
		this.cascadeDelete = cascadeDelete;
	}

}
