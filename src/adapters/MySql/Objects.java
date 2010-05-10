package adapters.MySql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;

import core.Attribute;
import core.Hermes;
import core.Introspector;

public class Objects {

	public static Set<Hermes> get(ResultSet rs, Class<?> model) {
		Set<Hermes> result = new LinkedHashSet<Hermes>();

		Hermes obj = null;
		do {
			obj = toObject(rs, model);
			if (obj != null) result.add(obj);
		} while (obj != null);

		return result;
	}

	// Private methods

	private static Hermes toObject(ResultSet rs, Class<?> model) {
		try {
			if (rs == null || !rs.next()) return null;
			Hermes object = (Hermes) model.newInstance();
			loadObject(object, rs);
			return object;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static void loadAttributeValue(Hermes object, ResultSet rs, Attribute attribute) {
		try {
			Object value = TypeCast.toJava(rs.getObject(attribute.getName()));
			Introspector.set(attribute, value, object);
		}
		catch (SQLException e) {
			// if selected columns , some columns can not be found inf resultset
		}
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

	private static Hermes loadObject(Hermes object, ResultSet rs) {
		object.loadAttributes();
		loadAttributesValue(object, rs);
		return object;
	}
}
