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

import main.CYMFrame;
import network.CYMNet;
import superPanel.ReceiveJPanel;

public class HomePanel extends ReceiveJPanel  {
	private CYMNet cymNet;
	private CYMFrame cymFrame;
	
	private JPanel northPanel, centerPanel, southPanel;
	private JLabel titleImage;
	private JButton[] CH;
	private JButton enterButton;
	private ArrayList<Icon> charImages = new ArrayList<Icon>();
	private ArrayList<Icon> charPressedImages = new ArrayList<Icon>();
	private JTextField nickNameTextField;
	
	ConnectAction connectActionListener = new ConnectAction();
	
	private String CharcterImgPath;
	private int characterNum;
	
	public HomePanel(CYMFrame cymFrame) {
		setLayout(null);
		this.cymFrame = cymFrame;
		cymNet = cymFrame.getCYMNet();
		CharcterImgPath = "";
		initCharImages();
		setPanel();
		setEvent();
	}

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
		initCH();
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

	private void initCH() {
		CH = new JButton[5];
		for (int i = 0; i < 5; i++) {
			CH[i] = new JButton(charImages.get(i));
			CH[i].setPreferredSize(new Dimension(135, 350));
			CH[i].setBorder(new LineBorder(Color.black, 6));
			centerPanel.add(CH[i]);
		}
	}

	private void initCharImages() {
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

	private void selectMaster(int selected) {
		for (int i = 0; i < 5; i++) {
			if (i == selected) {
				CH[i].setIcon(charPressedImages.get(i));
				characterNum = i;
				CharcterImgPath = cymFrame.getCharImageList().get(i);

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

	
	class ConnectAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			String id = nickNameTextField.getText().trim(); // 공백이 있지 모르니 공백 제거 trim() 사용
			
			if(CharcterImgPath == "") {
				JOptionPane.showMessageDialog(HomePanel.this.cymFrame.getContentPane(), "Please select a character.");
				return;
			} else if (id.equals("")) {
				JOptionPane.showMessageDialog(HomePanel.this.cymFrame.getContentPane(), "Please enter nickname.");
				return;
			}
			
			int level = (int) (Math.random() * 30 + 1);
			
			cymFrame.setImagePath(CharcterImgPath);
			
			logIn(id, characterNum, level);
			
			nickNameTextField.setText("");
		}
	}
	
	public void logIn(String id, int charNum, int level) {
		cymNet.sendMSG("/LOGIN;" + id + ";" + charNum + ";" + level);
	}
	
	@Override
	public void receiveMSG(String msg) {
		String splitMsg[];
		splitMsg = msg.split(";");
		if (splitMsg[0].equals("/SUCCESSLOGIN")) {
			cymFrame.setMyNickname(splitMsg[1]);
			cymFrame.setMyCharName(Integer.parseInt(splitMsg[2]));
			cymFrame.setMyLevel(Integer.parseInt(splitMsg[3]));
			cymFrame.sequenceControl("lobbyPanel", Integer.parseInt(splitMsg[4]));
		}
	}
}
