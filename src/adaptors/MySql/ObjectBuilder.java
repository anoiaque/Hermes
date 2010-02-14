package adaptors.MySql;

import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

import core.Hermes;
import core.Loader;

public class ObjectBuilder {

	public static Hermes toObject(ResultSet rs, Class<? extends Hermes> model) {
		try {
			if (rs == null || !rs.next()) return null;
			Hermes object = model.newInstance();
			return Loader.loadObject(object, rs);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Set<Hermes> toObjects(ResultSet rs, Class<? extends Hermes> model) {
		Set<Hermes> result = new HashSet<Hermes>();
		Hermes obj = null;
		do {
			obj = toObject(rs, model);
			if (obj != null) result.add(obj);
		} while (obj != null);
		return result;
	}
}
