package connections.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import connections.dto.Connection;

class ConnectionsPoolTests {
	
	ConnectionsPool connectionsPool;
	int limit = 3;
	Connection co1 = new Connection(1234, "gh34t5ty123a", 123);
	Connection co2 = new Connection(2345, "gh34t5ty123b", 234);
	Connection co3 = new Connection(3456, "gh34t5ty123c", 345);
	Connection co4 = new Connection(4567, "gh34t5ty123d", 456);

	@BeforeEach
	void setUp() throws Exception {
		connectionsPool= new ConnectionsPoolImpl(limit);
	}

	@Test
	void testAddConnection() {
		assertTrue(connectionsPool.addConnection(co1));
		assertTrue(connectionsPool.addConnection(co2));
		assertTrue(connectionsPool.addConnection(co3));
		assertTrue(connectionsPool.addConnection(co4));
		assertTrue(connectionsPool.addConnection(co3));
		assertFalse(connectionsPool.addConnection(co1));
	}

	@Test
	void testGetConnection() {
		connectionsPool.addConnection(co1);
		connectionsPool.addConnection(co2);
		connectionsPool.addConnection(co3);
		assertEquals(co2, connectionsPool.getConnection(co2.getId()));
		assertEquals(co1, connectionsPool.getConnection(co1.getId()));
		connectionsPool.addConnection(co4);
		assertEquals(null, connectionsPool.getConnection(co1.getId()));
	}

}
