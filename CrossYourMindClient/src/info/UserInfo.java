package info;

import java.io.Serializable;

public class UserInfo implements Serializable {
	// for userInfo
	private String[] charName = { "Spiderman", "Batman", "Captain America", "Shrek", "Ironman" };
	private String myNickname;
	private int myLevel;
	private String myCharName;
	private String myImagePath;
	private String myLobbyImagePath;
	private String myChatImagePath;
	private String myProfileImagePath;
	private String myGameCharImagePath;

	// for game
	private String myRoomName;
	private int myScore;
	private boolean isMaster; // 방의 주인장

	public UserInfo() {
		initField();
	}

	/** 필드의 값을 모두 초기화 */
	public void initField() {
		myNickname = null;
		myLevel = 0;
		myCharName = null;
		myImagePath = null;
		myLobbyImagePath = null;
		myChatImagePath = null;
		myProfileImagePath = null;
		myGameCharImagePath = null;

		myRoomName = null;
		myScore = 0;
	}

	/** 사용자의 게임 점수를 증가시키는 메소드 */
	public void increaseScore() {
		myScore++;
	}

	/** 사용자의 게임 점수를 감소시키는 메소드 */
	public void decreaseScore() {
		myScore--;
	}

	/* getter */
	public String getMyNickname() {
		System.out.println("<UserInfo> getMyNickname: " + myNickname);
		return myNickname;
	}

	public int getMyLevel() {
		System.out.println("<UserInfo> getMyLevel: " + myLevel);
		return myLevel;
	}

	public String getMyCharName() {
		System.out.println("<UserInfo> getMyCharName: " + myCharName);
		return myCharName;
	}

	public String getMyImagePath() {
		System.out.println("<UserInfo> getMyImagePath: " + myImagePath);
		return myImagePath;
	}

	public String getMyLobbyImagePath() {
		System.out.println("<UserInfo> getMyLobbyImagePath: " + myLobbyImagePath);
		return myLobbyImagePath;
	}

	public String getMyChatImagePath() {
		System.out.println("<UserInfo> getMyChatImagePath: " + myChatImagePath);
		return myChatImagePath;
	}

	public String getMyProfileImagePath() {
		System.out.println("<UserInfo> getMyProfileImagePath: " + myProfileImagePath);
		return myProfileImagePath;
	}

	public String getMyGameCharImagePath() {
		System.out.println("<UserInfo> getMyGameCharImagePath: " + myGameCharImagePath);
		return myGameCharImagePath;
	}

	public String getMyRoomName() {
		System.out.println("<UserInfo> getMyRoomName: " + myRoomName);
		return myRoomName;
	}

	public int getMyScore() {
		System.out.println("<UserInfo> getMyScore: " + myScore);
		return myScore;
	}

	public boolean getIsMaster() {
		System.out.println("<UserInfo> getIsMaster: " + isMaster);
		return isMaster;
	}

	/* setter */
	public void setMyNickname(String item) {
		System.out.println("<UserInfo> setMyNickname: " + item);
		this.myNickname = item;
	}

	public void setMyLevel(int item) {
		System.out.println("<UserInfo> setMyLevel: " + item);
		this.myLevel = item;
	}

	public void setMyCharName(int index) {
		this.myCharName = charName[index];
		System.out.println("<UserInfo> setMyCharName: " + myCharName);
	}

	public String seperateImagePath(String imagePath) {
		String frontImagePath = imagePath.substring(0, imagePath.length() - 4);
		System.out.println("<UserInfo> seperateImagePath: " + frontImagePath);
		return frontImagePath;
	}

	public void setImagePath(String item) {
		System.out.println("<UserInfo> setImagePath: " + item);
		myImagePath = item;
		String frontImagePath = seperateImagePath(item);
		myLobbyImagePath = frontImagePath + "L.png";
		myChatImagePath = frontImagePath + "T.png";
		myProfileImagePath = frontImagePath + "F.png";
		myGameCharImagePath = frontImagePath + "H.png";

	}

	public void setMyRoomName(String myRoomName) {
		System.out.println("<UserInfo> setMyRoomName: " + myRoomName);
		this.myRoomName = myRoomName;
	}

	public void setMyScore(int myScore) {
		System.out.println("<UserInfo> setMyScore: " + myScore);
		this.myScore = myScore;
	}

	public void setIsMaster(boolean isMaster) {
		System.out.println("<UserInfo> setIsMaster: " + isMaster);
		this.isMaster = isMaster;
	}

}
