package network;

import java.io.Serializable;

import info.UserInfo;

public class Protocol implements Serializable {
	// in homePanel
	public static final int LOGIN = 101;
	public static final int SUCCESSLOGIN = 102;

	// in LobbyPanel
	public static final int MSG = 201;
	public static final int IMAGE = 202;
	public static final int FILE = 203;
	public static final int FILESEND = 204;
	public static final int FILESAVE = 205;

	// in gamePanel

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

	// for roominfo
	private int roomSize;

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

}
