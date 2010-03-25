package core;

import java.util.Set;

public class BelongsTo {

	private String	fkName;
	private Integer	fkValue	= null;

	public BelongsTo(Hermes object) {
		fkName = Inflector.foreignKeyName(Introspector.className(object));
		fkValue = object.getId();
	}

	public static void belongsTo(Set<Hermes> set, Hermes object) {
		for (Object child : set)
			((Hermes) child).belongsTo(object);
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
