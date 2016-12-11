import java.util.Vector;

public class Room {
	private Vector<ClientManager> users;

	public Room() {
		users = new Vector<ClientManager>();
	}

	/** user가 Room에 참여 */
	void addUser(ClientManager user) {
		users.add(user);
		user.setRoom(this);
		user.changeUsers(users);
	}

	/** Room에 참여하고 있는 User들의 Vector */
	public Vector<ClientManager> getUsers() {
		return users;
	}
}