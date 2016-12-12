package info;
import java.util.Vector;

import main.ClientManager;

public class RoomInfo {
	private Vector<ClientManager> users;

	public RoomInfo() {
		users = new Vector<ClientManager>();
	}

	/** user�� Room�� ���� */
	public void addUser(ClientManager user) {
		users.add(user);
		user.setRoom(this);
		user.updateAllClientManager(users);
	}

	/** Room�� �����ϰ� �ִ� User���� Vector */
	public Vector<ClientManager> getUsers() {
		return users;
	}
}