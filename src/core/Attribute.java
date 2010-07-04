package core;

import java.lang.reflect.Field;
import java.util.ArrayList;

import adapters.Adapter;

public class Attribute {

	private String	name;
	private String	sqlType;
	private Object	value;

	public Attribute(String name, String type, Object value) {
		this.name = name;
		this.sqlType = type;
		this.value = value;
	}

	public static ArrayList<Attribute> load(Hermes model) {
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		for (Field field : Introspector.fields(model))
			if (basic(field, model)) attributes.add(attributize(field, model));
		return attributes;
	}

	// Getters & Setters
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getSqlType() {
		return sqlType;
	}

	// Private methods
	private static Attribute attributize(Field field, Hermes model) {
		try {
			field.setAccessible(true);
			String name = field.getName();
			String sqlType = Adapter.get().javaToSql(field.getType().getSimpleName());
			Object value = field.get(model);
			return new Attribute(name, sqlType, value);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static boolean basic(Field field, Hermes object) {
		Associations relations = object.getAssociations();
		
		String attribute = field.getName();
		if (relations.getHasOneAssociations().containsKey(attribute)) return false;
		if (relations.getManyToManyAsociations().containsKey(attribute)) return false;
		if (relations.getHasManyAssociations().containsKey(attribute)) return false;
		return true;
	}

}
