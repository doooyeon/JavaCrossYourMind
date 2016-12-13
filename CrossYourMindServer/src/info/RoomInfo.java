package info;

import java.util.Vector;

import main.ClientManager;

public class RoomInfo {
	public static final int ROOM_INIT = 600;
	public static final int ROOM_WAITNG = 601;
	public static final int ROOM_PLAYING = 602;
	public static final int ROOM_ROUND_NUM = 4; // �� ���� ����

	private Vector<ClientManager> users; // ���� �濡 �ִ� ����ڵ�
	private Vector<UserInfo> usersInfo; // ���� �濡 �ִ� ����ڵ�
	private String roomName; // ���� �̸�
	private int roomStatus; // ���� ����
	private String roundAnswer; // �ش� ������ ����
	private int roundNum; // ���� ���� ���� ��ȣ

	public RoomInfo(String roomName) {
		users = new Vector<ClientManager>();
		usersInfo = new Vector<UserInfo>();
		this.roomName = roomName;
		this.roomStatus = ROOM_INIT;
		this.roundAnswer = "";
		this.roundNum = ROOM_ROUND_NUM;
	}

	/** user�� Room�� ���� */
	public void addUser(ClientManager user) {
		users.add(user);
		usersInfo.add(user.getUserInfo());
		user.setRoom(this);

		// update users in same room
		user.updateAllClientManager(users);
	}
	
	/** user�� Room���� ���� */
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

	// /** Room�� �����ϰ� �ִ� User���� ClientManager Vector */
	// public Vector<ClientManager> getUsers() {
	// return users;
	// }
}