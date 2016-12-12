package info;

import java.util.Vector;

import main.ClientManager;

public class RoomInfo {
	private Vector<ClientManager> users;
	private String roomName;

	public RoomInfo() {
		users = new Vector<ClientManager>();
	}

	/** user�� Room�� ���� */
	public void addUser(ClientManager user) {
		users.add(user);
		user.setRoom(this);
		// ���� �濡 �ִ� ����ڵ�
		user.updateAllClientManager(users);
	}

	/** Room�� �����ϰ� �ִ� User���� Vector */
	public Vector<ClientManager> getUsers() {
		return users;
	}

	/* getter */
	public String getRoomName() {
		return roomName;
	}

	/* setter */
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
}