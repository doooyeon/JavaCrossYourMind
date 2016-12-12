package info;

import java.util.Vector;

import main.ClientManager;

public class RoomInfo {
	private Vector<ClientManager> users;
	private String roomName;

	public RoomInfo() {
		users = new Vector<ClientManager>();
	}

	/** user가 Room에 참여 */
	public void addUser(ClientManager user) {
		users.add(user);
		user.setRoom(this);
		// 같은 방에 있는 사용자들
		user.updateAllClientManager(users);
	}

	/** Room에 참여하고 있는 User들의 Vector */
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