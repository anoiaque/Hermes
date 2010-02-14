package core;

import adaptors.Adaptor;
import adaptors.MySql.ObjectBuilder;
import java.sql.ResultSet;
import java.util.Set;

public class Finder {

    private static Adaptor adaptor = Adaptor.get();

    public static Hermes find(int id, Class<? extends Hermes> model) {
        try {
            ResultSet rs = adaptor.find("*", "id = " + id, model.newInstance());
			return ObjectBuilder.toObject(rs, model);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static Set<?> find(String whereClause, Class<? extends Hermes> model) {
        return find("*", whereClause, model);
    }

    public static Set<?> find(String selectClause, String whereClause, Class<? extends Hermes> model) {
        try {
            ResultSet rs = adaptor.find(selectClause, whereClause, model.newInstance());
            return ObjectBuilder.toObjects(rs, model);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

// Finder for Jointure models.
// The reason for this finder is that instances of this model have not the same table name.
    public static Set<?> joinFind(int parentId, Jointure join) {
        return ObjectBuilder.toObjects(adaptor.find("*", "parentId = " + parentId, join), Jointure.class);
    }
}
