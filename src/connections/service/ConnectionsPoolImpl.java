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
			mapConnections.remove(list.tail.connection.getId());
			list.tail.prev.next = newConnection;
			newConnection.prev = list.tail.prev;
			list.tail = newConnection;
		}else {
			addAsTail(newConnection);
		}
		mapConnections.put(connection.getId(), newConnection);
		return true;
	}

	private void addAsTail(Node newConnection) {
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
					list.head = nodeCalled.next;
					list.head.next.prev = null;
				}else {
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
