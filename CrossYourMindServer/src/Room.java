import java.util.Vector;

public class Room {
   private Vector<ClientManager> users;
   Room() {
      users = new Vector<ClientManager>();
   }
   void addUser(ClientManager user) {
      users.add(user);
      user.setRoom(this);
      user.changeUsers(users);
   }
   public Vector<ClientManager> getUsers() {
      return users;
   }
}