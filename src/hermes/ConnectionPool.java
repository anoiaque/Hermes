package hermes;

import hermes.Config.DBMSConfig;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;


public class ConnectionPool {

	private static ConnectionPool instance = null;
	private DBMSConfig config = null;
	private String driverName = "";
	private HashMap<Connection, Boolean> pool = null;

	// constructors
	private ConnectionPool() {
		config = readConfigFile();
		this.driverName = driverNameFor(config.getDBMSName());
		pool = new HashMap<Connection, Boolean>();
		initPool();
	}

	public void finalize() {
		for (Connection conn : pool.keySet())
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}

	// public methods
	public static ConnectionPool getInstance() {
		if (instance == null) {
			synchronized (ConnectionPool.class) {
				instance = new ConnectionPool();
			}
		}
		return instance;
	}

	public Connection getConnexion() {
		for (Connection conn : pool.keySet())
			if (pool.get(conn)) {
				pool.put(conn, false);
				return conn;
			}
		return null;
	}

	public void release(Connection connection) {
		for (Connection conn : pool.keySet())
			if (connection.equals(conn)) {
		        pool.put(connection, true);
			}
	}

	public int availableConnections() {
		int nbFree = 0;
		for (Boolean free : pool.values()) {
			if (free)
				nbFree++;
		}
		return nbFree;
	}
	

	// private methods
	private String driverNameFor(String dbmsName) {
		if (dbmsName.equalsIgnoreCase("MySql"))
			return "com.mysql.jdbc.Driver";
		else
			return "";
	}

	private void initPool() {
		try {
			Class.forName(driverName);
			for (int i = 0; i < config.getPoolSize(); i++)
				pool.put(DriverManager.getConnection(config.getURL(), config.getUser(), config.getPassword()), true);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private DBMSConfig readConfigFile() {
		Properties props = new Properties();
		DBMSConfig config = (new Config()).new DBMSConfig();
		FileInputStream propfile;

		try {
			propfile = new FileInputStream("dbms.properties");
			props.load(propfile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		config.setDBMSName(props.getProperty("ADAPTER"));
		config.setPoolSize(Integer.valueOf(props.getProperty("POOLSIZE", "5")));
		config.setURL(props.getProperty("URL"));
		config.setUser(props.getProperty("USER"));
		config.setPassword(props.getProperty("PASSWORD"));
		return config;

	}

}
