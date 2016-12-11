package main;

import java.awt.CardLayout;
import java.awt.Container;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.JFrame;

import home.HomePanel;
import lobby.LobbyPanel;
import main.CYMFrame.ExitListener;
import network.CYMNet;
import room.RoomPanel;

public class CYMFrame extends JFrame {
	public static final int SCREEN_WIDTH = 800, SCREEN_HEIGHT = 560;
	public static final String ImagePath = "res/";
	public static final String FONT = "12롯데마트드림Light";

	private CYMNet cymNet;
	private CardLayout layoutManager;
	private Container contentPane;

	private HomePanel homePanel;
	public LobbyPanel lobbyPanel;
	public RoomPanel roomPanel;
	
	private ArrayList<String> entrycharImageList;
	private ArrayList<String> entryEnteredcharImageList;
	private ArrayList<String> lobbyImageList;
	private ArrayList<String> talkcharImageList;
	private ArrayList<String> profileImageList;
	private ArrayList<String> gamecharImageList;
	private String [] charName = {"Spiderman", "Batman", "Captain America", "Shrek", "Ironman"};
	
	private String myNickname;
	private String myLobbyImagePath; // seleced charNum
	private int myLevel;
	private String myCharName;
	
	private String imagePath;
	private String lobbyImagePath;
	private String chatImagePath;
	private String profileImagePath;
	private String gameCharImagePath;

	public CYMFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		layoutManager = new CardLayout();
		setLayout(layoutManager);
		setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		setResizable(false);
		initCharImageList();
		addWindowListener(new ExitListener());
		
		cymNet = new CYMNet();
		homePanel = new HomePanel(this);
		lobbyPanel = new LobbyPanel(this);
		//roomPanel = new RoomPanel(this);
		cymNet.setHomePanel(homePanel);
		cymNet.setLobbyPanel(lobbyPanel);
		cymNet.setRoomPanel(roomPanel);
		cymNet.toHomePanel();
		cymNet.network();

		contentPane = this.getContentPane();
		contentPane.add("homePanel", homePanel);
		contentPane.add("lobbyPanel", lobbyPanel);
		//contentPane.add("roomPanel", roomPanel);

		setVisible(true);
		sequenceControl("homePanel", 0);
	}

	public void sequenceControl(String panelName, int arg0) {
		// arg0는 패널마다 의미하는 바가 다름
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

	public void changePanel(String panelName) {
		layoutManager.show(contentPane, panelName);
		switch (panelName) {
		case "homePanel":
			homePanel.requestFocus();
			break;
		case "lobbyPanel":
			lobbyPanel.requestFocus();
			break;
		case "roomPanel":
			roomPanel.requestFocus();
			break;
		}
	}
	
	public void setMyLobbyImagePath(String item) {
		myLobbyImagePath = item;
	}

	public void setMyNickname(String item) {
		this.myNickname = item;
	}

	public void setMyLevel(int item) {
		this.myLevel = item;
	}

	public void setMyCharName(int index) {
		this.myCharName = charName[index];
	}
	
	public void setImagePath(String item) {
		System.out.println("<ProgressInfo> set_imagePath imagePath: " + item);
		imagePath = item;
		String frontImagePath = seperateImagePath(item);
		lobbyImagePath = frontImagePath + "L.png";
		chatImagePath = frontImagePath + "T.png";
		profileImagePath = frontImagePath + "F.png";
		gameCharImagePath = frontImagePath + "H.png";

	}
	
	public String seperateImagePath(String imagePath) {
		System.out.println("<ProgressInfo> imageFilename " + imagePath);
		String frontImagePath = imagePath.substring(0, imagePath.length() - 4);
		System.out.println("<ProgressInfo> frontImagePath " + frontImagePath);
		return frontImagePath;
	}

	public CYMNet getCYMNet() {
		return this.cymNet;
	}
	
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
	
	public String getMyNickname() {
		return myNickname;
	}

	public String getMyLobbyImagePath() {
		return myLobbyImagePath;
	}

	public int getMyLevel() {
		return myLevel;
	}

	public String getMyCharName() {
		return myCharName;
	}
	
	public String getImagePath() {
		return imagePath;
	}

	public String getLobbyImagePath() {
		return lobbyImagePath;
	}

	public String getChatImagePath() {
		return chatImagePath;
	}
	
	public String getProfileImagePath() {
		return profileImagePath;
	}

	public String getGameCharImagePath() {
		return gameCharImagePath;
	}
	
	class ExitListener implements WindowListener {
		@Override
		public void windowClosing(WindowEvent e) {
			cymNet.sendMSG("/EXIT");
			System.out.println("프로그램 종료");
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