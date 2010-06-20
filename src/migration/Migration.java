package migration;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

import core.Attribute;
import core.Hermes;
import core.Inflector;
import core.Introspector;

public class Migration {

	private ArrayList<Table>	tables	= new ArrayList<Table>();

	public Migration(String packageName) {
		loadModels(packageName);
		addIdDefinition();
		addColumnsDefinitions();
		addForeignKeysDefinitions();
	}

	private void addForeignKeysDefinitions() {
		for (Table table : tables) {
			Hermes klass = Introspector.instanciate(table.getKlass());
			for (String attribute : klass.getAssociations().getHasOneAssociations().keySet()) {
				Table associatedTable = associatedTable(attribute, klass);
				String foreignKeyName = Inflector.foreignKey(klass);
				associatedTable.getForeignKeys().put(foreignKeyName, "integer");
			}
		}
	}

	private Table associatedTable(String attribute, Hermes klass) {
		Class<Hermes> associateKlass = Introspector.hermesClass(attribute, klass);
		for (Table table : tables) {
			System.out.println(table.getKlass());
			System.out.println(associateKlass);

			if (table.getKlass().equals(associateKlass)) return table;
		}
		return null;
	}

	private void addColumnsDefinitions() {
		for (Table table : tables) {
			Hermes klass = Introspector.instanciate(table.getKlass());
			for (Attribute attribute : klass.getAttributes())
				table.getColumns().put(attribute.getName(), columnDefinition(attribute));
		}
	}

	private void addIdDefinition() {
		for (Table table : tables) {
			table.getColumns().put("id", idDefinition());
		}

	}

	// Private Methods
	private void loadModels(String packageName) {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL resource = loader.getResource(packageName.replace('.', '/'));
		File directory = new File(resource.getFile());

		for (String file : directory.list())
			try {
				String klassName = packageName + "." + file.replace(".class", "");
				Class<? extends Hermes> klass = (Class<? extends Hermes>) Class.forName(klassName);
				tables.add(new Table(klass));
			}
			catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
	}

	public static String idDefinition() {
		return "int primary key auto_increment";
	}

	public static String columnDefinition(Attribute attribute) {
		return attribute.getSqlType();
	}

	// Getters & Setters

	public ArrayList<Table> getTables() {
		return tables;
	}
}
