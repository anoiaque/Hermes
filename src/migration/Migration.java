package migration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import adapters.Adapter;

import core.Attribute;
import core.Hermes;
import core.Inflector;
import core.Introspector;
import core.Jointure;
import core.ManyToMany;

public class Migration {

	private ArrayList<Table>	tables	= new ArrayList<Table>();
	private String						packageName;
	private String						fileName;

	public Migration(String packageName, String fileName) {
		this.packageName = packageName;
		this.fileName = fileName;
		loadModels();
		schematize();
	}

	// Private methods

	private void schematize() {
		columnsDefinitions();
		jointureDefinitions();
		handleSTI();
		createSqlSchemaFile();
	}

	private void createSqlSchemaFile() {
		Iterator<Table> tablesIt = tables.iterator();
		File file = new File(fileName);

		try {
			FileOutputStream migrationFile = new FileOutputStream(file);
			PrintStream printStream = new PrintStream(migrationFile);
			while (tablesIt.hasNext())
				printStream.println(tablesIt.next().sql());
			printStream.close();
		}
		catch (Exception e) {
			System.err.println("Error writing to file");
		}
	}

	private void jointureDefinitions() {
		ArrayList<Table> tablesClone = (ArrayList<Table>) tables.clone();
		Hermes klass;
		HashMap<String, ManyToMany> associations;

		for (Table table : tablesClone) {
			klass = Introspector.instanciate(table.getKlass());
			associations = klass.getAssociations().getManyToManyAsociations();
			for (String attribute : associations.keySet()) {
				Jointure jointure = associations.get(attribute).getJointure();
				if (!Table.exists(jointure.getTableName(), tables)) {
					tables.add(new Table(jointure.getTableName()));
				}
			}
		}
	}

	private void handleSTI() {
		Table parent = null;
		ArrayList<Table> tablesClone = (ArrayList<Table>) tables.clone();

		for (Table table : tablesClone) {
			if (table.isSingleTableInheritence()) {
				parent = Table.withKlass(table.getParent(), tables);
				parent.getColumns().putAll(table.getColumns());
				parent.addSTIKlassColumn();
				parent.getForeignKeys().putAll(table.getForeignKeys());
				tables.remove(table);
			}
		}
	}

	private void columnsDefinitions() {
		Hermes klass = null;

		for (Table table : tables) {
			klass = Introspector.instanciate(table.getKlass());
			table.getColumns().put("id", Adapter.get().idDefinition());
			for (Attribute attribute : klass.getAttributes())
				table.getColumns().put(attribute.getName(), attribute.getSqlType());
			for (String attribute : klass.getAssociations().getHasOneAssociations().keySet())
				addForeignKeyToTable(attribute, klass);
			for (String attribute : klass.getAssociations().getHasManyAssociations().keySet())
				addForeignKeyToTable(attribute, klass);
		}
	}

	private void addForeignKeyToTable(String attribute, Hermes klass) {
		Table table = Table.withKlass(Introspector.klass(attribute, klass), tables);
		table.getForeignKeys().put(Inflector.foreignKey(klass), "integer");
	}

	private void loadModels() {
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

	// Getters & Setters

	public ArrayList<Table> getTables() {
		return tables;
	}
}
