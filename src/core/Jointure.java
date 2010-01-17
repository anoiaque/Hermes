package core;

import java.lang.reflect.ParameterizedType;

import adaptors.Adaptor;

public class Jointure extends Hermes {

    private int parentId;
    private int childId;

    // Constructors
    public Jointure() {
    }

    public Jointure(Hermes object, String attribute) {
	createJoinTable(object, attribute);
    }

    // Private Methods
    private void createJoinTable(Hermes model, String attribute) {
	setTableName(joinTableNameFor(model, attribute));
	String sql = "create  table if not exists " + this.getTableName() + "("
		+ "parentId int default null,childId int default null); ";
	Adaptor.get().execute(sql, this);
    }

    private String joinTableNameFor(Hermes model, String attribute) {
	ParameterizedType setAttribute;
	String parentName = model.getClass().getSimpleName().toUpperCase();
	String childName = "";
	try {
	    setAttribute = (ParameterizedType) model.getClass()
		    .getDeclaredField(attribute).getGenericType();
	    childName = (((Class<?>) setAttribute.getActualTypeArguments()[0])
		    .getSimpleName()).toUpperCase();
	} catch (Exception e) {
	    e.printStackTrace();
	    return null;
	}
	return parentName + "_" + childName;
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
