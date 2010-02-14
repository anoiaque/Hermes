package core;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class Loader {

	public static Hermes loadObject(Hermes object, ResultSet rs) {
		object.loadAttributes();
		loadAttributesValue(object, rs);
		loadAssociations(object, rs);
		return object;
	}

	public static void loadManyToManyAssociations(Hermes object) {
		for (String attr : object.getManyToManyAssociations().keySet()) {
			try {
				Set<Hermes> objects = new HashSet<Hermes>();
				Field field = object.getClass().getDeclaredField(attr);
				Jointure jointure = object.getManyToManyAssociations().get(attr).getJointure();
				Set<Jointure> jointures = (Set<Jointure>) Finder.joinFind(object.getId(), jointure);
				ParameterizedType type = (ParameterizedType) object.getClass().getDeclaredField(attr)
						.getGenericType();
				Class<?> classe = (Class<?>) type.getActualTypeArguments()[0];
				jointures.remove(null);
				for (Jointure join : jointures) {
					Hermes obj = (Hermes) classe.newInstance();
					obj = Finder.find(join.getChildId(), obj.getClass());
					objects.add(obj);
				}
				field.setAccessible(true);
				field.set(object, objects);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void loadHasManyAssociations(Hermes object, ResultSet rs) {
		for (String attribute : object.getHasManyAssociations().keySet()) {
			String fk = Inflector.foreignKeyName(Introspector.className(object));
			Class<Hermes> klass = Introspector.collectionTypeClass(object, attribute);
			Set<?> set = Finder.find(fk + "=" + object.getId(), klass);
			for (Object child : set) {
				Hermes obj = (Hermes) child;
				obj.belongsTo(object);
			}
			try {
				Field field = object.getClass().getDeclaredField(attribute);
				field.setAccessible(true);
				field.set(object, set);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void loadHasOneAssociations(Hermes object, ResultSet rs) {
		for (String attr : object.getHasOneAssociations().keySet()) {
			try {
				String fk = Inflector.foreignKeyName(Introspector.className(object));
				Field field = object.getClass().getDeclaredField(attr);
				Hermes obj = (Hermes) field.getType().newInstance();
				obj = (Hermes) Finder.findFirst(fk + "=" + object.getId(), obj.getClass());
				field.setAccessible(true);
				field.set(object, obj);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}



	// Private methods
	private static void loadAssociations(Hermes object, ResultSet rs) {
		loadHasOneAssociations(object, rs);
		loadManyToManyAssociations(object);
		loadHasManyAssociations(object, rs);
	}
	private static void loadAttributeValue(Hermes object, ResultSet rs, Attribute attribute) {
		try {
			Field field = Introspector.fieldFor(object, attribute);
			field.setAccessible(true);
			field.set(object, rs.getObject(field.getName()));
		}
		catch (Exception e) {}
	}

	private static void loadAttributesValue(Hermes object, ResultSet rs) {
		try {
			for (Attribute attribute : object.getAttributes())
				loadAttributeValue(object, rs, attribute);
			object.setId((Integer) rs.getObject("id"));
		}
		catch (SQLException e) {
			// No id column in table
		}
	}
}
