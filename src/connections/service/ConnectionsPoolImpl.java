package connections.service;

import java.util.HashMap;

import connections.dto.Connection;

public class ConnectionsPoolImpl implements ConnectionsPool {
	
	private static class Node{
		Connection connection;
		Node prev;
		Node next;
		Node(Connection connection) {
			this.connection = connection;
		}
	}
	private static class ConnectionsList{
		Node head;
		Node tail;
		/* V.R.  It is good idea to encapsulate functionality like
		 * addAsHead(), moveToHead() and so on in this class
		 */
	}
	ConnectionsList list = new ConnectionsList();
	HashMap<Integer, Node> mapConnections = new HashMap<>();
	int connectionsPoolLimit;
	public ConnectionsPoolImpl(int limit) {
		this.connectionsPoolLimit = limit;
	}

	@Override
	public boolean addConnection(Connection connection) {
		if(mapConnections.containsKey(connection.getId())) {
			return false;
		}
		Node newConnection = new Node(connection);
		if(list.head == null) {
			list.head = list.tail = newConnection;
		}else if(mapConnections.size() == connectionsPoolLimit) {
			/* V.R. In this case it is enough to remove the tail from the list
			 *  and to remove suitable record from the map.
			 *  Adding new connection to the list will be executed by addAsTail()
			 */
			mapConnections.remove(list.tail.connection.getId());
			list.tail.prev.next = newConnection;
			newConnection.prev = list.tail.prev;
			list.tail = newConnection;
		}else {
			// V.R. It will be simpler without 'else'.
			addAsTail(newConnection);
		}
		mapConnections.put(connection.getId(), newConnection);
		return true;
	}

	private void addAsTail(Node newConnection) {
		/* V.R. This method has to named as addAsHead. It also have
		 *  to execute adding to the head of the list.
		 *  This is the way to use head for newest connection and to use the tail
		 *  for oldest connection. 
		 */
		list.tail.next = newConnection;
		newConnection.prev = list.tail;
		list.tail = newConnection;
	}

	@Override
	public Connection getConnection(int id) {
		Node nodeCalled = mapConnections.getOrDefault(id, null);
		if(nodeCalled != null) {
			if(nodeCalled != list.tail) {
				if(nodeCalled == list.head) {
					// V.R. Do nothing, itg is already the head
					list.head = nodeCalled.next;
					list.head.next.prev = null;
				}else {
					/* V.R. It is necessary to remove nodeCalled from the list
					 *  and to add it to the head
					 */
					nodeCalled.prev.next = nodeCalled.next;
					nodeCalled.next.prev = nodeCalled.prev;
				}
				addAsTail(nodeCalled);
			}
			return nodeCalled.connection;
			
		}
		return null;
	}

}
