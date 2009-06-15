package hermes;

public class Config {
    public class DBMSConfig {
        private int    poolSize;
        private String DBMSName;
        private String URL;
        private String user;
        private String password;

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
