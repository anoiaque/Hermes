package helpers;

import adaptors.Adaptor;

public class Database {

	public static void clear() {
		String[] tables = { "addresses","cars","personnel", "people", "pets", "person_pet" };

		for (String tableName : tables) {
			String sql = "delete from " + tableName;
			Adaptor.get().execute(sql, null);
		}
	}
}
