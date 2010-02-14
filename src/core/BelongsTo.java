package core;

public class BelongsTo {

	Hermes					parent;
	private String	fkName;
	private Integer	fkValue	= null;

	public BelongsTo(Hermes object) {
		parent = object;
		fkName = Inflector.foreignKeyName(Introspector.className(object));
		fkValue = object.getId();
	}

	// Getters & Setters
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
}
