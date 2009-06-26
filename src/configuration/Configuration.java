package configuration;

import java.io.FileInputStream;
import java.util.Properties;

public class Configuration {

    public static class SqlConverterConfig {

        public static int varcharLength = 50;

    }

    public static class DBMSConfig {

        private int poolSize;
        private String DBMSName;
        private String URL;
        private String user;
        private String password;

        public static DBMSConfig get() {
            Properties props = new Properties();
            DBMSConfig config = new DBMSConfig();
            FileInputStream propfile;
            try {
                propfile = new FileInputStream("hermes.properties");
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

        public int getPoolSize() {
            return poolSize;
        }

        public void setPoolSize(int poolSize) {
            this.poolSize = poolSize;
        }

        public String getDBMSName() {
            return DBMSName;
        }

        public void setDBMSName(String name) {
            DBMSName = name;
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
    }
}
