package network;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Vector;

import info.UserInfo;

public class Protocol implements Serializable {
	// in homePanel
	public static final int HOME_LOGIN = 101;
	public static final int HOME_SUCCESSLOGIN = 102;

	public static final int HOME_LOGOUT = 199;

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
	public static final int LOBBY_JOIN_ROOM = 211;
	public static final int LOBBY_JOIN_ROOM_FAIL = 212;
	public static final int LOBBY_JOIN_ROOM_SUCCESS = 213;

	public static final int LOBBY_LOGOUT = 299;

	// in gamePanel
	public static final int GAME_IN = 301;
	public static final int GAME_CREATED = 302;
	public static final int GAME_JOIN_IN = 303;
	public static final int GAME_JOIN_PARTIPANT = 304;
	public static final int GAME_CHAT_MSG = 305;
	public static final int GAME_CORRECT_ANSWER = 306;
	public static final int GAME_CHAT_UPDATE = 307;
	public static final int GAME_START = 308;
	public static final int GAME_START_FAIL_LACK_USER = 309;
	public static final int GAME_START_FAIL_NOT_MASTER = 310;
	public static final int GAME_START_SUCCESS = 311;
	public static final int GAME_START_SUCCESS_QUESTIONER = 312;
	public static final int GAME_START_SUCCESS_ANSWER = 313;
	public static final int GAME_TIMER_BROADCAST = 314;
	public static final int GAME_ROUND_TERMINATE = 315;

	// in gamePanel for drawing
	public static final int GAME_DRAW = 401;
	public static final int GAME_DRAW_BROADCAST = 402;
	public static final int GAME_DRAW_ALLCLEAR = 403;
	public static final int GAME_DRAW_ALLCLEAR_BROADCAST = 404;
	public static final int GAME_DRAW_ERASER = 405;
	public static final int GAME_DRAW_ERASER_BROADCAST = 406;
	public static final int GAME_DRAW_SELECT_COLOR = 407;
	public static final int GAME_DRAW_SELECT_COLOR_BROADCAST = 408;
	public static final int GAME_DRAW_TIMER_EXPIRE = 409;
	public static final int GAME_DRAW_TIMER_EXPIRE_BROADCAST = 410;

	public static final int GAME_LOGOUT = 499;

	public static final int EXIT = 599; // 윈도우창닫기

	/* RoomInfo 600~ */
	/* UserInfo 700~ */
	
	/* FIELD */
	private int status = 0;
	private UserInfo userInfo;

	// for file send
	private String filePath;
	private String sendFileName;
	private long sendFileSize;

	// for chatting
	private String chatSentence;
	private Vector<String> gameList;
	private Vector<String> userList;

	// for roominfo
	private int roomSize;
	private String roomName;
	private Vector<UserInfo> usersInRoom;

	// for game
	private String roundAnswer;

	// for drawing
	private int drawColor;
	private ArrayList<Point> pointList;

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
		System.out.println("<Protocol> getUserInfo(name): " + userInfo.getMyNickname());
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

	public String getChatSentence() {
		System.out.println("<Protocol> getChatSentence: " + chatSentence);
		return chatSentence;
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

	public String getRoundAnswer() {
		System.out.print("<Protocol> getRoundAnswer: " + roundAnswer);
		return roundAnswer;
	}

	public int getDrawColor() {
		System.out.println("<Protocol> getDrawColor: " + drawColor);
		return drawColor;
	}

	public ArrayList<Point> getPointList() {
		System.out.println("<Protocol> getPointList");
		return pointList;
	}

	/* setter */
	public void setStatus(int status) {
		System.out.println("<Protocol> setStatus: " + status);
		this.status = status;
	}

	public void setUserInfo(UserInfo userInfo) {
		System.out.println("<Protocol> setUserInfo(name): " + userInfo.getMyNickname());
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

	public void setChatSentence(String chatSentence) {
		System.out.println("<Protocol> setLobbyChatSentence: " + chatSentence);
		this.chatSentence = chatSentence;
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
		System.out.println("<Protocol> setRoomName: " + roomName);
		this.roomName = roomName;
	}

	public void setUsersInRoom(Vector<UserInfo> usersInRoom) {
		System.out.println("<Protocol> setUsersInRoom");
		this.usersInRoom = usersInRoom;
	}

	public void setRoundAnswer(String roundAnswer) {
		System.out.println("<Protocol> setRoundAnswer: " + roundAnswer);
		this.roundAnswer = roundAnswer;
	}

	public void setDrawColor(int drawColor) {
		System.out.println("<Protocol> setDrawColor: " + drawColor);
		this.drawColor = drawColor;
	}

	public void setPointList(ArrayList<Point> pointList) {
		System.out.println("<Protocol> setPointList");
		this.pointList = pointList;
	}
}
