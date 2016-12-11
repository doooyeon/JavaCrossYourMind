package main;

import java.awt.CardLayout;
import java.awt.Container;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.JFrame;

import home.HomePanel;
import lobby.LobbyPanel;
import network.CYMNet;
import room.RoomPanel;

public class CYMFrame extends JFrame {
	public static final int SCREEN_WIDTH = 800, SCREEN_HEIGHT = 560;
	public static final String ImagePath = "res/";
	public static final String FONT = "12�Ե���Ʈ�帲Light";

	//for connection
	private CYMNet cymNet;
	
	//for panel
	private CardLayout layoutManager;
	private Container contentPane;

	private HomePanel homePanel;
	public LobbyPanel lobbyPanel;
	public RoomPanel roomPanel;
	
	//for images
	private String [] charName = {"Spiderman", "Batman", "Captain America", "Shrek", "Ironman"};
	private ArrayList<String> entrycharImageList;
	private ArrayList<String> entryEnteredcharImageList;
	private ArrayList<String> lobbyImageList;
	private ArrayList<String> talkcharImageList;
	private ArrayList<String> profileImageList;
	private ArrayList<String> gamecharImageList;
	
	//for userInfo
	private String myNickname;
	private int myLevel;
	private String myCharName;
	private String myImagePath;
	private String myLobbyImagePath;
	private String myChatImagePath;
	private String myProfileImagePath;
	private String myGameCharImagePath;

	public CYMFrame() {
		setTitle("Cross Your Mind");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		layoutManager = new CardLayout();
		setLayout(layoutManager);
		setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		setResizable(false);
		initCharImageList();
		addWindowListener(new ExitListener());
		
		cymNet = new CYMNet(); //�������� ����� ������ �ۼ����� ���� ��ü
		
		homePanel = new HomePanel(this);
		lobbyPanel = new LobbyPanel(this);
		//roomPanel = new RoomPanel(this);
		cymNet.setHomePanel(homePanel);
		cymNet.setLobbyPanel(lobbyPanel);
		cymNet.setRoomPanel(roomPanel);
		cymNet.toHomePanel(); //������ HomePanel
		cymNet.network(); //������ ����

		contentPane = this.getContentPane();
		contentPane.add("homePanel", homePanel);
		contentPane.add("lobbyPanel", lobbyPanel);
		//contentPane.add("roomPanel", roomPanel);

		setVisible(true);
		sequenceControl("homePanel", 0);
	}
	
	/** ����� �̹��� ����Ʈ�� ���� �ʱ�ȭ �޼ҵ� */
	private void initCharImageList() {
		entrycharImageList = new ArrayList<String>();
		entryEnteredcharImageList = new ArrayList<String>();
		lobbyImageList = new ArrayList<String>();
		talkcharImageList = new ArrayList<String>();
		profileImageList  = new ArrayList<String>();
		gamecharImageList = new ArrayList<String>();
		
		for (int i = 0; i < 5; i++) {
			entrycharImageList.add(ImagePath + "Char" + i + ".png");
			entryEnteredcharImageList.add(ImagePath + "Char" + i + "E.png");
			lobbyImageList.add(ImagePath + "Char" + i + "L.png");
			talkcharImageList.add(ImagePath + "Char" + i + "T.png");
			profileImageList.add(ImagePath + "Char" + i + "F.png");
			gamecharImageList.add(ImagePath + "Char" + i + "H.png");
		}
	}

	/** �г��� �ٲٴ� �޼ҵ� */
	public void sequenceControl(String panelName, int arg0) {
		// arg0�� �гθ��� �ǹ��ϴ� �ٰ� �ٸ�
		switch (panelName) {
		case "homePanel":
			cymNet.toHomePanel();
			cymNet.setStateToHome();
			changePanel(panelName);
			break;
		case "lobbyPanel":
			cymNet.toLobbyPanel();
			cymNet.setStateToLobby();
			for (int i = 0; i < arg0; i++)
				lobbyPanel.addRoom();
			changePanel(panelName);
			lobbyPanel.myInfoUpdate();
			repaint();
			break;
		case "roomPanel":
			cymNet.toRoomPanel();
			cymNet.setStateToRoom();
			//roomPanel.setRoomNum(arg0);
			changePanel(panelName);
			break;
		}
	}

	/** cardLayout���� �г� ��ȯ �� keyFocus �������� */
	public void changePanel(String panelName) {
		layoutManager.show(contentPane, panelName);
		switch (panelName) {
		case "homePanel":
			homePanel.requestFocus();
			break;
		case "lobbyPanel":
			lobbyPanel.requestFocus();
			break;
		case "gamePanel":
			roomPanel.requestFocus();
			break;
		}
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
		System.out.println("<CYMFrame> frontImagePath " + frontImagePath);
		return frontImagePath;
	}
	public void setImagePath(String item) {
		System.out.println("<ProgressInfo> set_imagePath imagePath: " + item);
		myImagePath = item;
		String frontImagePath = seperateImagePath(item);
		myLobbyImagePath = frontImagePath + "L.png";
		myChatImagePath = frontImagePath + "T.png";
		myProfileImagePath = frontImagePath + "F.png";
		myGameCharImagePath = frontImagePath + "H.png";

	}
	
	public CYMNet getCYMNet() {
		return this.cymNet;
	}
	
	/* getter for ImageList */
	public ArrayList<String> getCharImageList() {
		return entrycharImageList;
	}
	public ArrayList<String> getCharEnteredImageList() {
		return entryEnteredcharImageList;
	}
	public ArrayList<String> getLobbyImageList() {
		return lobbyImageList;
	}
	public ArrayList<String> getTalkcharImageList() {
		return talkcharImageList;
	}
	public ArrayList<String> getProfileImageList() {
		return profileImageList;
	}
	public ArrayList<String> getGamecharImageList() {
		return gamecharImageList;
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
	
	/** ������ â �ݱ⿡ ���� �̺�Ʈ ó�� Ŭ���� */
	class ExitListener implements WindowListener {
		@Override
		public void windowClosing(WindowEvent e) {
			cymNet.sendMSG("/EXIT");
			System.out.println("���α׷� ����");
			System.exit(0);
		}

		@Override
		public void windowActivated(WindowEvent arg0) {
		}

		@Override
		public void windowClosed(WindowEvent arg0) {
		}

		@Override
		public void windowDeactivated(WindowEvent arg0) {
		}

		@Override
		public void windowDeiconified(WindowEvent arg0) {
		}

		@Override
		public void windowIconified(WindowEvent arg0) {
		}

		@Override
		public void windowOpened(WindowEvent arg0) {
		}
	}
}