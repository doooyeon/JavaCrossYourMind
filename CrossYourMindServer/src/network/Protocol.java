package network;

import java.io.Serializable;
import java.util.Vector;

import info.UserInfo;

public class Protocol implements Serializable {
	// in homePanel
	public static final int HOME_LOGIN = 101;
	public static final int HOME_SUCCESSLOGIN = 102;

	// in LobbyPanel
	public static final int LOBBY_CHAT_MSG = 201;
	public static final int LOBBY_CHAT_IMAGE = 202;
	public static final int LOBBY_CHAT_FILE = 203;
	public static final int LOBBY_CHAT_FILESEND = 204;
	public static final int LOBBY_CHAT_FILESAVE = 205;
	public static final int LOBBY_UPDATE_GAME_LIST = 206;
	public static final int LOBBY_UPDATE_USER_LIST = 207;
	public static final int LOBBY_CREATE_ROOM = 208;
	public static final int LOBBY_CREATE_ROOM_FAIL = 209;
	public static final int LOBBY_CREATE_ROOM_SUCCESS = 210;

	// in gamePanel
	public static final int GAME_IN = 301;
	public static final int GAME_CREATED = 302;

	public static final int START = 401;
	public static final int EXIT = 402;
	public static final int LOGOUT = 403;

	/* FIELD */
	private int status = 0;
	private UserInfo userInfo;

	// for file send
	private String filePath;
	private String sendFileName;
	private long sendFileSize;

	// for chatting
	private String lobbyChatSentence;
	private Vector<String> gameList;
	private Vector<String> userList;

	// for roominfo
	private int roomSize;
	private String roomName;
	private Vector<UserInfo> usersInRoom;

	/** Protocol construction */
	public Protocol() {
		System.out.println("<Protocol> 새로운 프로토콜 생성!!");
		userInfo = new UserInfo();
	}

	/* getter */
	public int getStatus() {
		System.out.println("<Protocol> getStatus: " + status);
		return status;
	}

	public UserInfo getUserInfo() {
		System.out.println("<Protocol> getUserInfo: " + userInfo);
		return userInfo;
	}

	public String getFilePath() {
		System.out.println("<Protocol> getFilePath: " + filePath);
		return filePath;
	}

	public String getSendFileName() {
		System.out.println("<Protocol> getSendFileName: " + sendFileName);
		return sendFileName;
	}

	public long getSendFileSize() {
		System.out.println("<Protocol> getSendFileSize: " + sendFileSize);
		return sendFileSize;
	}

	public String getLobbyCharSentence() {
		System.out.println("<Protocol> getLobbyCharSentence: " + lobbyChatSentence);
		return lobbyChatSentence;
	}

	public int getRoomSize() {
		System.out.println("<Protocol> getRoomSize: " + roomSize);
		return roomSize;
	}

	public Vector<String> getGameList() {
		System.out.print("<Protocol> getGameList: ");
		for (int i = 0; i < gameList.size(); i++) {
			System.out.print(gameList.get(i) + " | ");
		}
		System.out.println();

		return gameList;
	}

	public Vector<String> getUserList() {
		System.out.print("<Protocol> getUserList: ");
		for (int i = 0; i < userList.size(); i++) {
			System.out.print(userList.get(i) + " | ");
		}
		System.out.println();

		return userList;
	}

	public String getRoomName() {
		System.out.println("<Protocol> getRoomName: " + roomName);
		return roomName;
	}

	public Vector<UserInfo> getUsersInRoom() {
		System.out.print("<Protocol> getUsersInRoom: ");
		for (int i = 0; i < usersInRoom.size(); i++) {
			System.out.print(usersInRoom.get(i).getMyNickname() + " | ");
		}
		System.out.println();

		return usersInRoom;
	}

	/* setter */
	public void setStatus(int status) {
		System.out.println("<Protocol> setStatus: " + status);
		this.status = status;
	}

	public void setUserInfo(UserInfo userInfo) {
		System.out.println("<Protocol> setUserInfo: " + userInfo);
		this.userInfo = userInfo;
	}

	public void setSendFileName(String sendFileName) {
		System.out.println("<Protocol> setSendFileName: " + sendFileName);
		this.sendFileName = sendFileName;
	}

	public void setSendFileSize(long sendFileSize) {
		System.out.println("<Protocol> setSendFileSize: " + sendFileSize);
		this.sendFileSize = sendFileSize;
	}

	public void setLobbyChatSentence(String lobbyChatSentence) {
		System.out.println("<Protocol> setLobbyChatSentence: " + lobbyChatSentence);
		this.lobbyChatSentence = lobbyChatSentence;
	}

	public void setRoomSize(int roomSize) {
		System.out.println("<Protocol> setRoomSize: " + roomSize);
		this.roomSize = roomSize;
	}

	public void setGameList(Vector<String> gameList) {
		System.out.print("<Protocol> setGameList: ");
		for (int i = 0; i < gameList.size(); i++) {
			System.out.print(gameList.get(i) + " | ");
		}
		System.out.println();
		this.gameList = gameList;
	}

	public void setUserList(Vector<String> userList) {
		System.out.print("<Protocol> setUserList: ");
		for (int i = 0; i < userList.size(); i++) {
			System.out.print(userList.get(i) + " | ");
		}
		System.out.println();
		this.userList = userList;
	}

	public void setRoomName(String roomName) {
		System.out.print("<Protocol> setRoomName: " + roomName);
		this.roomName = roomName;
	}

	public void setUsersInRoom(Vector<UserInfo> usersInRoom) {
		System.out.print("<Protocol> setUsersInRoom: ");
		this.usersInRoom = usersInRoom;
	}
}
