package helpers;

import adapters.Adapter;

public class Database {

	public static void clear() {
		String[] tables = { "addresses","cars","personnel", "people", "pets", "people_pets" };

		for (String tableName : tables) {
			String sql = "delete from " + tableName;
			Adapter.get().execute(sql, null);
		}
	}
}
