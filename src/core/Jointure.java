package core;

import adaptors.Adaptor;
import adaptors.MySql.SqlBuilder;

public class Jointure extends Hermes {

	private int	parentId;
	private int	childId;

	// Constructors
	public Jointure() {}

	public Jointure(Hermes model, String attribute) {
		this.tableName = Table.joinTableNameFor(model, attribute);
		Adaptor.get().execute(SqlBuilder.build("jointure", tableName), this);
	}

	// Getters & setters
	public int getParentId() {
		return parentId;
	}

	public void setParentId(int leftId) {
		this.parentId = leftId;
	}

	public int getChildId() {
		return childId;
	}

	public void setChildId(int rightId) {
		this.childId = rightId;
	}
}
