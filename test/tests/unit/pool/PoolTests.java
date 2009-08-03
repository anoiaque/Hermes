package tests.unit.pool;

import java.sql.Connection;

import junit.framework.TestCase;
import pool.Pool;

public class PoolTests extends TestCase {

    private final int defaultPoolSize = 5;
    Pool pool = Pool.getInstance();

    // Test singleton pool
    public void testgetInstance() {
        assertNotNull(pool);
        assertEquals(pool, Pool.getInstance());
    }

    // Test release connection . Available connections increment if one connection is released.
    public void testReleaseConnection() {
        assertEquals(5, pool.availableConnections());
        Connection conn = pool.getConnexion();
        assertEquals(defaultPoolSize - 1, pool.availableConnections());
        pool.release(conn);
        assertEquals(defaultPoolSize, pool.availableConnections());
    }
    // Test available connexions is decrement when one is used and not realeased
    // getConnexion() must return null if no available connexion
    public void testGetConnexionDefaultPoolSize() {
        for (int i = 0; i < defaultPoolSize; i++) {
            assertNotNull(pool.getConnexion());
            assertEquals(defaultPoolSize - (i + 1), pool.availableConnections());
        }
        assertEquals(0, pool.availableConnections());
        assertNull(pool.getConnexion());
    }

   
}
