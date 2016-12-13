package info;

import java.util.Vector;

import main.ClientManager;

public class RoomInfo {
	public static final int ROOM_INIT = 600;
	public static final int ROOM_WAITNG = 601;
	public static final int ROOM_PLAYING = 602;
	public static final int ROOM_ROUND_NUM = 4; // 총 진행 라운드

	private Vector<ClientManager> users; // 같은 방에 있는 사용자들
	private Vector<UserInfo> usersInfo; // 같은 방에 있는 사용자들
	private String roomName; // 방의 이름
	private int roomStatus; // 방의 상태
	private String roundAnswer; // 해당 라운드의 정답
	private int roundNum; // 진행 중인 라운드 번호

	public RoomInfo(String roomName) {
		users = new Vector<ClientManager>();
		usersInfo = new Vector<UserInfo>();
		this.roomName = roomName;
		this.roomStatus = ROOM_INIT;
		this.roundAnswer = "";
		this.roundNum = ROOM_ROUND_NUM;
	}

	/** user가 Room에 참여 */
	public void addUser(ClientManager user) {
		users.add(user);
		usersInfo.add(user.getUserInfo());
		user.setRoom(this);

		// update users in same room
		user.updateAllClientManager(users);
	}
	
	/** user가 Room에서 나감 */
	public void removeUser(ClientManager user) {
		users.remove(user);
		usersInfo.remove(user.getUserInfo());
		user.setRoom(null);

		// update users in same room
		user.updateAllClientManager(users);
	}

	/* getter */
	public Vector<ClientManager> getUsersClientManager() {
		System.out.println("<RoomInfo> getUsersClientManager: ~~");
		return users;
	}

	public Vector<UserInfo> getUsersInfo() {
		System.out.println("<RoomInfo> getUsersInfo: ~~");
		return usersInfo;
	}

	public int getUsersCount() {
		return users.size();
	}

	public String getRoomName() {
		System.out.println("<RoomInfo> getRoomName: " + roomName);
		return roomName;
	}

	public int getRoomStatus() {
		System.out.println("<RoomInfo> getRoomStatus: " + roomStatus);
		return roomStatus;
	}

	public String getRoundAnswer() {
		System.out.println("<RoomInfo> getRoundAnswer: " + roundAnswer);
		return roundAnswer;
	}

	public int getRoundNum() {
		System.out.println("<RoomInfo> getRoundNum: " + roundNum);
		return roundNum;
	}

	/* setter */
	public void setRoomStatus(int roomStatus) {
		System.out.println("<RoomInfo> setRoomStatus: " + roomStatus);
		this.roomStatus = roomStatus;
	}

	public void setRoundAnswer(String roundAnswer) {
		System.out.println("<RoomInfo> setRoundAnswer: " + roundAnswer);
		this.roundAnswer = roundAnswer;
	}

	public void setRoundNum(int roundNum) {
		System.out.println("<RoomInfo> setRoundNum: " + roundNum);
		this.roundNum = roundNum;
	}

	// /** Room에 참여하고 있는 User들의 ClientManager Vector */
	// public Vector<ClientManager> getUsers() {
	// return users;
	// }
}