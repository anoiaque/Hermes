package pool;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

import configuration.Configuration;
import configuration.Configuration.DBMSConfig;

public class Pool {
    private static Pool                  instance   = null;
    private DBMSConfig                   config     = null;
    private String                       driverName = "";
    private HashMap<Connection, Boolean> pool       = null;

    // constructors
    private Pool() {
        config = Configuration.DBMSConfig.get();
        this.driverName = driverNameFor(config.getDBMSName());
        pool = new HashMap<Connection, Boolean>();
        initPool();
    }

    @Override
    public void finalize() {
        for (Connection conn : pool.keySet())
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }

    // public methods
    public static Pool getInstance() {
        if (instance == null) {
            synchronized (Pool.class) {
                instance = new Pool();
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
}
