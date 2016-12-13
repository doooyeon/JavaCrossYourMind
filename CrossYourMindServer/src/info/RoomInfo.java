package info;

import java.util.Vector;

import main.ClientManager;

public class RoomInfo {
	private Vector<ClientManager> users; // ���� �濡 �ִ� ����ڵ�
	private Vector<UserInfo> usersInfo; // ���� �濡 �ִ� ����ڵ�
	private String roomName;

	public RoomInfo(String roomName) {
		users = new Vector<ClientManager>();
		usersInfo = new Vector<UserInfo>();
		this.roomName = roomName;
	}

	/** user�� Room�� ���� */
	public void addUser(ClientManager user) {
		users.add(user);
		usersInfo.add(user.getUserInfo());
		user.setRoom(this);
		// update users in same room
		user.updateAllClientManager(users);
	}

	/* getter */
	/** Room�� �����ϰ� �ִ� User���� ClientManager Vector */
//	public Vector<ClientManager> getUsers() {
//		return users;
//	}
	
	/** Room�� �����ϰ� �ִ� User���� UserInfo Vector */
	public Vector<UserInfo> getUsersInfo() {
		return usersInfo;
	}

	
	public String getRoomName() {
		return roomName;
	}
}