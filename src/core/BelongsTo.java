package core;

import java.util.Set;

public class BelongsTo {

	private String	fkName;
	private Long		fkValue	= null;

	public BelongsTo(Hermes object) {
		fkName = Inflector.foreignKey(Introspector.name(object));
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

	public Long getFkValue() {
		return fkValue;
	}

	public void setFkValue(Long fkValue) {
		this.fkValue = fkValue;
	}
}
