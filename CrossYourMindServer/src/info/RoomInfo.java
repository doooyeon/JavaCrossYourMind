package info;

import java.util.Vector;

import main.ClientManager;

public class RoomInfo {
	private Vector<ClientManager> users; // 같은 방에 있는 사용자들
	private Vector<UserInfo> usersInfo; // 같은 방에 있는 사용자들
	private String roomName;

	public RoomInfo(String roomName) {
		users = new Vector<ClientManager>();
		usersInfo = new Vector<UserInfo>();
		this.roomName = roomName;
	}

	/** user가 Room에 참여 */
	public void addUser(ClientManager user) {
		users.add(user);
		usersInfo.add(user.getUserInfo());
		user.setRoom(this);
		// update users in same room
		user.updateAllClientManager(users);
	}

	/* getter */
	/** Room에 참여하고 있는 User들의 ClientManager Vector */
//	public Vector<ClientManager> getUsers() {
//		return users;
//	}
	
	/** Room에 참여하고 있는 User들의 UserInfo Vector */
	public Vector<UserInfo> getUsersInfo() {
		return usersInfo;
	}

	
	public String getRoomName() {
		return roomName;
	}
}