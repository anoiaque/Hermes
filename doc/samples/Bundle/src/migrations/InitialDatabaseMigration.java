package migrations;

import migration.Migration;

public class InitialDatabaseMigration {

	public static void main(String[] args) {

		new Migration("models", "src/migrations/bundle_initial_migration.sql");

	}

}
