package info;

import java.io.Serializable;

public class UserInfo implements Serializable{
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
	}

	/* setter for userInfo */
	public void setMyNickname(String item) {
		this.myNickname = item;
	}

	public void setMyLevel(int item) {
		this.myLevel = item;
	}

	public void setMyCharName(int index) {
		this.myCharName = charName[index];
	}

	public String seperateImagePath(String imagePath) {
		String frontImagePath = imagePath.substring(0, imagePath.length() - 4);
		System.out.println("<UserInfo> frontImagePath " + frontImagePath);
		return frontImagePath;
	}

	public void setImagePath(String item) {
		System.out.println("<UserInfo> setImagePath imagePath: " + item);
		myImagePath = item;
		String frontImagePath = seperateImagePath(item);
		myLobbyImagePath = frontImagePath + "L.png";
		myChatImagePath = frontImagePath + "T.png";
		myProfileImagePath = frontImagePath + "F.png";
		myGameCharImagePath = frontImagePath + "H.png";

	}

	/* getter for userInfo */
	public String getMyNickname() {
		return myNickname;
	}

	public int getMyLevel() {
		return myLevel;
	}

	public String getMyCharName() {
		return myCharName;
	}

	public String getMyImagePath() {
		return myImagePath;
	}

	public String getMyLobbyImagePath() {
		return myLobbyImagePath;
	}

	public String getMyChatImagePath() {
		return myChatImagePath;
	}

	public String getMyProfileImagePath() {
		return myProfileImagePath;
	}

	public String getMyGameCharImagePath() {
		return myGameCharImagePath;
	}

}
