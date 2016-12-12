package info;
import java.util.Vector;

import main.ClientManager;

public class RoomInfo {
	private Vector<ClientManager> users;

	public RoomInfo() {
		users = new Vector<ClientManager>();
	}

	/** user가 Room에 참여 */
	public void addUser(ClientManager user) {
		users.add(user);
		user.setRoom(this);
		user.updateAllClientManager(users);
	}

	/** Room에 참여하고 있는 User들의 Vector */
	public Vector<ClientManager> getUsers() {
		return users;
	}
}