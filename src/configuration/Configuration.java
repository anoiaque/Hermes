package configuration;

import java.io.FileInputStream;
import java.util.Properties;

public class Configuration {

  public static class SqlConverterConfig {

    public static int varcharLength = 50;
  }

  public static class DBMSConfig {

    private int poolSize;
    private String adapter;
    private String port;
    private String host;
    private String database;
    private String URL;
    private String user;
    private String password;

    public static DBMSConfig get() {
      Properties props = properties();
      DBMSConfig config = new DBMSConfig();
      String urlProperty = props.getProperty("URL");

      config.setAdapter(props.getProperty("ADAPTER"));
      config.setPort(props.getProperty("PORT"));
      config.setHost(props.getProperty("HOST"));
      config.setDatabase(props.getProperty("DATABASE"));
      config.setPoolSize(Integer.valueOf(props.getProperty("POOLSIZE", "5")));
      config.setURL((urlProperty == null) ? url(config) : urlProperty);
      config.setUser(props.getProperty("USER"));
      config.setPassword(props.getProperty("PASSWORD"));
      
      return config;
    }

    // Private Methods
    private static Properties properties() {
      Properties props = new Properties();
      FileInputStream propfile;
      try {
        propfile = new FileInputStream("hermes.properties");
        props.load(propfile);
      } catch (Exception e) {
        e.printStackTrace();
      }
      return props;
    }

    private static String url(DBMSConfig config) {
      String protocol = "jdbc";
      String subprotocol = config.getAdapter().toLowerCase();

      return protocol + ":" + subprotocol + "://" + config.getHost() + ":" + config.getPort() + "/" + config.getDatabase();
    }

    // Getters end Setters
    public int getPoolSize() {
      return poolSize;
    }

    public void setPoolSize(int poolSize) {
      this.poolSize = poolSize;
    }

    public String getAdapter() {
      return adapter;
    }

    public void setAdapter(String name) {
      adapter = name;
    }

    public String getURL() {
      return URL;
    }

    public void setURL(String URL) {
      this.URL = URL;
    }

    public String getUser() {
      return user;
    }

    public void setUser(String user) {
      this.user = user;
    }

    public String getPassword() {
      return password;
    }

    public void setPassword(String password) {
      this.password = password;
    }

    public String getDatabase() {
      return database;
    }

    public void setDatabase(String database) {
      this.database = database;
    }

    public String getHost() {
      return host;
    }

    public void setHost(String host) {
      this.host = host;
    }

    public String getPort() {
      return port;
    }

    public void setPort(String port) {
      this.port = port;
    }
  }
}
