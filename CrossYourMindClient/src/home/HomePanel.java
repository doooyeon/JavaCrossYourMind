package home;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import info.UserInfo;
import main.CYMFrame;
import network.CYMNet;
import network.Protocol;
import superPanel.ReceiveJPanel;

public class HomePanel extends ReceiveJPanel  {
	
	private CYMNet cymNet;
	private CYMFrame cymFrame;
	private UserInfo userInfo;
	
	// For inner panels
	private JPanel northPanel, centerPanel, southPanel;
	private JLabel titleImage;
	private JButton[] CH;
	private JButton enterButton;
	private ArrayList<Icon> charImages = new ArrayList<Icon>();
	private ArrayList<Icon> charPressedImages = new ArrayList<Icon>();
	private JTextField nickNameTextField;
	
	ConnectAction connectActionListener = new ConnectAction();
	
	private String selectedCharImgPath;
	private int selectedCharNum;
	
	/** HomePanel construction */
	public HomePanel(CYMFrame cymFrame) {
		setLayout(null);
		this.cymFrame = cymFrame;
		userInfo = cymFrame.getUserInfo();
		cymNet = cymFrame.getCYMNet();
		selectedCharImgPath = "";
		initCharStrArray();
		setPanel();
		setEvent();
	}

	/** for GUI */
	private void setPanel() {
		this.setLayout(new BorderLayout());

		// For north panel
		northPanel = new JPanel(new FlowLayout());
		northPanel.setPreferredSize(new Dimension(800, 110));
		northPanel.setBackground(new Color(64, 64, 64));
		titleImage = new JLabel();
		titleImage.setIcon(new ImageIcon(CYMFrame.ImagePath + "titlePanel.png"));
		titleImage.setPreferredSize(new Dimension(750, 100));
		northPanel.add(titleImage);
		this.add(BorderLayout.NORTH, northPanel);

		// For center panel
		centerPanel = new JPanel(new FlowLayout());
		centerPanel.setPreferredSize(new Dimension(800, 400));
		centerPanel.setBackground(Color.gray);
		initCharBtn();
		this.add(BorderLayout.CENTER, centerPanel);

		// For south panel
		southPanel = new JPanel(new FlowLayout());
		southPanel.setPreferredSize(new Dimension(800, 50));
		southPanel.setBackground(new Color(64, 64, 64));
		nickNameTextField = new JTextField();
		nickNameTextField.setPreferredSize(new Dimension(250, 40));
		nickNameTextField.setBackground(new Color(255, 230, 153));
		nickNameTextField.setFont(new Font(null, Font.BOLD, 25));
		nickNameTextField.setBorder(new LineBorder(new Color(255, 206, 5), 4));
		enterButton = new JButton(new ImageIcon(CYMFrame.ImagePath + "enterUp.png"));
		enterButton.setBackground(new Color(64, 64, 64));
		enterButton.setOpaque(true);
		enterButton.setPreferredSize(new Dimension(100, 37));
		southPanel.add(nickNameTextField);
		southPanel.add(enterButton);
		this.add(BorderLayout.SOUTH, southPanel);
	}

	/** for Charter Button Event */
	private void setEvent() {
		CH[0].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectMaster(0);
			}
		});
		CH[1].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectMaster(1);
			}
		});
		CH[2].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectMaster(2);
			}
		});
		CH[3].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectMaster(3);
			}
		});
		CH[4].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectMaster(4);
			}
		});

		nickNameTextField.addActionListener(connectActionListener);
		enterButton.addActionListener(connectActionListener);
	}

	/** 캐릭터 버튼 이미지 초기화 */
	private void initCharBtn() {
		CH = new JButton[5];
		for (int i = 0; i < 5; i++) {
			CH[i] = new JButton(charImages.get(i));
			CH[i].setPreferredSize(new Dimension(135, 350));
			CH[i].setBorder(new LineBorder(Color.black, 6));
			centerPanel.add(CH[i]);
		}
	}

	/** 캐릭터 String Array 초기화 */
	private void initCharStrArray() {
		ArrayList<String> imagePath = new ArrayList<String>();
		ArrayList<String> imagePathBtnPressed = new ArrayList<String>();

		imagePath = this.cymFrame.getCharImageList();
		imagePathBtnPressed = this.cymFrame.getCharEnteredImageList();

		int length = imagePath.size();
		for (int i = 0; i < length; i++) {
			charImages.add(new ImageIcon(imagePath.get(i)));
			charPressedImages.add(new ImageIcon(imagePathBtnPressed.get(i)));
		}
	}

	/** 선택된 이미지 번호와 경로 설정 */
	private void selectMaster(int selected) {
		for (int i = 0; i < 5; i++) {
			if (i == selected) {
				CH[i].setIcon(charPressedImages.get(i));
				selectedCharImgPath = cymFrame.getCharImageList().get(i);
				selectedCharNum = i;
			} else
				CH[i].setIcon(charImages.get(i));
		}
	}

	/** Mouse Entered, Exited -> have to modify */
	// private void selectExited(int selected) {
	// for (int i = 0; i < 5; i++) {
	// if (i == selected) {
	// CH[i].setIcon(charImages.get(i));
	// // CH[i].setBorder(new LineBorder(new Color(91, 155, 213), 8));
	// CH[i].setBorder(new LineBorder(Color.black, 4));
	// }
	// }
	// }

	
	/** 로그인 이벤트에 대한 내부 클래스  -> 서버로 프로토콜 전송  */
	class ConnectAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			//TextField에서 닉네임 세팅
			String id = nickNameTextField.getText().trim(); // 공백이 있지 모르니 공백 제거 trim() 사용
			
			//레벨을 랜덤으로 세팅
			int level = (int) (Math.random() * 30 + 1);
			
			//입력 내용 확인
			if(selectedCharImgPath == "") {
				JOptionPane.showMessageDialog(HomePanel.this.cymFrame.getContentPane(), "Please select a character.");
				return;
			} else if (id.equals("")) {
				JOptionPane.showMessageDialog(HomePanel.this.cymFrame.getContentPane(), "Please enter nickname.");
				return;
			}
			
			//userInfo 세팅
			userInfo.setMyNickname(id);
			userInfo.setMyLevel(level);
			userInfo.setMyCharName(selectedCharNum);
			userInfo.setImagePath(selectedCharImgPath);
			
			//프로토콜 전송
			Protocol pt = new Protocol();
			pt.setStatus(Protocol.LOGIN);
			pt.setUserInfo(userInfo);
			//logIn(id, selectedCharNum, level);
			logIn(pt);
			
			nickNameTextField.setText("");
		}
	}
	
	/** LOGIN 프로토콜 전송 */
//	public void logIn(String id, int charNum, int level) {
//		cymNet.sendMSG("/LOGIN;" + id + ";" + charNum + ";" + level);
//	}
	public void logIn(Protocol pt) {
		System.out.println("<HomePanel> logIn: " + pt.getUserInfo().getMyNickname());
		cymNet.sendProtocol(pt);
	}
	
	@Override
	public void receiveMSG(String msg) {
		System.out.println("<HomePanel> receiveMSG: 내용 없음");
//		String splitMsg[];
//		splitMsg = msg.split(";");
//		if (splitMsg[0].equals("/SUCCESSLOGIN")) {
//			userInfo.setMyNickname(splitMsg[1]);
//			userInfo.setMyCharName(Integer.parseInt(splitMsg[2]));
//			userInfo.setMyLevel(Integer.parseInt(splitMsg[3]));
//			cymFrame.sequenceControl("lobbyPanel", Integer.parseInt(splitMsg[4]));
//		}
	}
	
	@Override
	public void receiveProtocol(Protocol pt){
		System.out.println("<HomePanel> in receiveProtocol");
		
		//String status = Integer.toString(pt.getStatus());
		int status = pt.getStatus();
		System.out.println("<HomePanel> status: " + status);
		if(status == Protocol.SUCCESSLOGIN){
			userInfo = pt.getUserInfo();
			//cymFrame.sequenceControl("lobbyPanel", arg0);
			System.out.println("<HomePanel> ???");
			cymFrame.sequenceControl("lobbyPanel", 0);
			System.out.println("<HomePanel> !!!");
		}
	}
}
