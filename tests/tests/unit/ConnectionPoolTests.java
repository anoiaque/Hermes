package tests.unit;

import hermes.ConnectionPool;

import java.sql.Connection;

import junit.framework.TestCase;

public class ConnectionPoolTests extends TestCase {
    private final int defaultPoolSize = 5;
    ConnectionPool    pool            = ConnectionPool.getInstance();

    public void testReleaseConnection() {
        assertEquals(5, pool.availableConnections());
        Connection conn = pool.getConnexion();
        assertEquals(defaultPoolSize - 1, pool.availableConnections());
        pool.release(conn);
        assertEquals(defaultPoolSize, pool.availableConnections());
    }

    public void testgetInstance() {
        assertNotNull(pool);
        assertEquals(pool, ConnectionPool.getInstance());
    }

    public void testGetConnexionDefaultPoolSize() {
        for (int i = 0; i < defaultPoolSize; i++) {
            assertNotNull(pool.getConnexion());
            assertEquals(defaultPoolSize - (i + 1), pool.availableConnections());
        }
        assertEquals(0, pool.availableConnections());
        assertNull(pool.getConnexion());
    }
}
