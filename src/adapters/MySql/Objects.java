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
			object.loadAttributes();
			loadAttributes(object, rs);
			return object;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static void loadAttributes(Hermes object, ResultSet rs) {
		Object value = null;
		try {
			for (Attribute attribute : object.getAttributes()) {
				if (attribute.getName().equals("klass")) continue;
				value = TypeCast.toJava(rs.getObject(attribute.getName()));
				Introspector.set(attribute, value, object);
			}
			object.setId((Integer) rs.getObject("id"));
		}
		catch (SQLException e) {
			// No id column in table
			// Or if selected columns , some columns can not be found in resultset

		}
	}

}
