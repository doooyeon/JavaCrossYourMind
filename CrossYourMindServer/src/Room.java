import java.util.Vector;

public class Room {
	private Vector<ClientManager> users;

	public Room() {
		users = new Vector<ClientManager>();
	}

	/** user�� Room�� ���� */
	void addUser(ClientManager user) {
		users.add(user);
		user.setRoom(this);
		user.changeUsers(users);
	}

	/** Room�� �����ϰ� �ִ� User���� Vector */
	public Vector<ClientManager> getUsers() {
		return users;
	}
}