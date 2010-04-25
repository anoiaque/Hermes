package core;

import java.util.Set;

public class HasMany {

	private String	attributeName;
	private String	fkName;
	private int			fkValue				= 0;
	private boolean	cascadeDelete	= false;

	public HasMany(String attribute, String dependency) {
		attributeName = attribute;
		fkName = Inflector.foreignKey(attribute);
		cascadeDelete = dependency.equals("dependent:destroy");
	}

	public boolean save(Hermes parent) {
		Set<Hermes> objects = (Set<Hermes>) Introspector.getObject(attributeName, parent);

		if (objects == null) return true;
		for (Hermes occurence : objects) {
			occurence.belongsTo(parent);
			if (!occurence.save()) return false;
		}
		return true;
	}

	public void delete(Hermes parent) {
		if (!cascadeDelete) return;
		Set<Hermes> objects = (Set<Hermes>) Introspector.getObject(attributeName, parent);
		if (objects == null) return;
		for (Hermes object : objects)
			if (object != null) object.delete();
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

	public int getFkValue() {
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
