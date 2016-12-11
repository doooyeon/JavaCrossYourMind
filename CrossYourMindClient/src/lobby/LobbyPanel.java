package lobby;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import home.HomePanel;
import main.CYMFrame;
import network.CYMNet;
import superPanel.ReceiveJPanel;

public class LobbyPanel extends ReceiveJPanel  {
	
	private CYMNet cymNet;
	private CYMFrame cymFrame;

	// For inner panels
	private JPanel northPanel, centerPanel;
	private JLabel titleImage;
	private JLabel gameListLabel, userListLabel, myinfoLabel, lobbyChatLabel;

	private JPanel gameListPanel, userListPanel, lobbyChatPanel, infoAndButton;
	private JScrollPane gameListScroll, userListScroll, chattingScroll;
	private JList<String> gameList, userList;

	// private JTextArea showChat;
	private JTextPane ChattingPane;
	private JTextField lobbyChatTextField;
	
	private ImageIcon icon;
	private JLabel iconLabel;
	
	private JButton ImageOpenBtn; // 이미지첨부버튼
	private JButton FileOpenBtn; // 파일첨부버튼
	
	private String receiveFilePath = "C:/Program Files/CrossYourMindClient";

	private JPanel myInfo, buttonPanel;
	private JLabel myChar;
	private JLabel[] idLabel;
	private JLabel[] charNameLabel;
	private JLabel[] levelLabel;
	private JButton createButton, backButton;
	private JDialog createDialog;
	
	private String[] gamesLobby, usersLobby;

	public LobbyPanel(CYMFrame cymFrame) {
		setLayout(null);
		this.cymFrame = cymFrame;
		cymNet = cymFrame.getCYMNet();
		
		initCreateDialog();
		setPanel();
		setEvent();
	}
	
	public void initCreateDialog() {
//		createDialog = new JDialog();
//		CreateDialog cd = new CreateDialog(this);
//		createDialog.setContentPane(cd);
//		createDialog.setBounds(400, 300, 350, 150);
//		createDialog.setResizable(false);
//		createDialog.setVisible(false);
	}

	/** INPUT: null, OUTPUT: null, Objective: Initialize the panels */
	private void setPanel() {
		this.setLayout(null);

		/* For north panel */
		northPanel = new JPanel();
		northPanel.setLayout(null);
		northPanel.setBounds(0, 0, 800, 110);
		northPanel.setBackground(new Color(64, 64, 64));
		titleImage = new JLabel();
		titleImage.setIcon(new ImageIcon(CYMFrame.ImagePath + "titlePanel.png"));
		titleImage.setBounds(22, 5, 750, 100);
		northPanel.add(titleImage);
		this.add(northPanel);

		/* For center panel */
		centerPanel = new JPanel(null);
		centerPanel.setBounds(0, 110, 800, 410);
		centerPanel.setBackground(new Color(64, 64, 64));
		centerPanel.setOpaque(true);

		gameListPanel = new JPanel(null);
		gameListPanel.setBounds(11, 0, 200, 400);
		gameListLabel = new JLabel(new ImageIcon(CYMFrame.ImagePath + "gameListLabel.png"));
		gameListLabel.setBounds(0, 0, 200, 30);
		gameList = new JList<String>();
		gameList.setBackground(new Color(255, 230, 153));
		gameList.setBorder(new LineBorder(new Color(255, 206, 5), 4));
		gameList.setFont(new Font(CYMFrame.FONT, Font.PLAIN, 20));
		gameList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		gameListScroll = new JScrollPane(gameList);
		gameListScroll.setBounds(0, 30, 200, 370);
		gameListPanel.add(gameListLabel);
		gameListPanel.add(gameListScroll);

		userListPanel = new JPanel(null);
		userListPanel.setBounds(216, 0, 250, 200);
		userListLabel = new JLabel(new ImageIcon(CYMFrame.ImagePath + "userListLabel.png"));
		userListLabel.setBounds(0, 0, 250, 30);
		userList = new JList<String>();
		userList.setBackground(new Color(255, 230, 153));
		userList.setBorder(new LineBorder(new Color(255, 206, 5), 4));
		userList.setFont(new Font(CYMFrame.FONT, Font.PLAIN, 20));
		userListScroll = new JScrollPane(userList);
		userListScroll.setBounds(0, 30, 250, 170);
		userListPanel.add(userListLabel);
		userListPanel.add(userListScroll);

		// Right of the south panel: Information of this client & buttons
		infoAndButton = new JPanel(null);
		infoAndButton.setBounds(216, 205, 250, 210);
		infoAndButton.setBackground(new Color(64, 64, 64));
		myinfoLabel = new JLabel(new ImageIcon(CYMFrame.ImagePath + "myInfoLabel.png"));
		myinfoLabel.setBounds(0, 0, 250, 30);

		myInfo = new JPanel(null);
		myInfo.setBounds(0, 30, 250, 130);
		myInfo.setBackground(new Color(255, 230, 153));
		myInfo.setOpaque(true);
		myInfo.setBorder(new LineBorder(new Color(255, 206, 5), 4));

		buttonPanel = new JPanel(null);
		buttonPanel.setBounds(0, 162, 250, 38);
		buttonPanel.setBackground(new Color(64, 64, 64));
		buttonPanel.setOpaque(true);
		createButton = new JButton(new ImageIcon(CYMFrame.ImagePath + "createUp.png"));
		createButton.setBounds(20, -2, 100, 37);
		createButton.setBackground(new Color(64, 64, 64));
		createButton.setOpaque(true);
		backButton = new JButton(new ImageIcon(CYMFrame.ImagePath + "backUp.png"));
		backButton.setBounds(130, -2, 100, 37);
		backButton.setBackground(new Color(64, 64, 64));
		backButton.setOpaque(true);
		buttonPanel.add(createButton);
		buttonPanel.add(backButton);

		infoAndButton.add(myinfoLabel);
		infoAndButton.add(myInfo);
		infoAndButton.add(buttonPanel);

		// Left of the south panel: chats in lobby
		lobbyChatPanel = new JPanel(null);
		lobbyChatPanel.setBounds(471, 0, 310, 400);
		lobbyChatLabel = new JLabel(new ImageIcon(CYMFrame.ImagePath + "lobbyChatLabel.png"));
		lobbyChatLabel.setBounds(0, 0, 310, 30);

		chattingScroll = new JScrollPane();
		chattingScroll.setBounds(0, 30, 310, 335);
		ChattingPane = new JTextPane();
		chattingScroll.setViewportView(ChattingPane);
		ChattingPane.setDisabledTextColor(new Color(0, 0, 0));
		ChattingPane.setBackground(new Color(255, 230, 153));
		ChattingPane.setFont(new Font(CYMFrame.FONT, Font.BOLD, 15));
		ChattingPane.setBorder(new LineBorder(new Color(255, 206, 5), 4));
		ChattingPane.setEditable(false);

		lobbyChatTextField = new JTextField();
		lobbyChatTextField.setBounds(1, 365, 240, 35);
		lobbyChatTextField.setFont(new Font(CYMFrame.FONT, Font.BOLD, 20));
		lobbyChatTextField.setBorder(new LineBorder(new Color(255, 206, 5), 4));
		ImageOpenBtn = new JButton(new ImageIcon(CYMFrame.ImagePath + "imageOpen.png"));
		ImageOpenBtn.setBorderPainted(false);
		ImageOpenBtn.setFocusPainted(false);
		ImageOpenBtn.setBounds(242, 367, 32, 32);
		FileOpenBtn = new JButton(new ImageIcon(CYMFrame.ImagePath + "fileOpen.png"));
		FileOpenBtn.setBorderPainted(false);
		FileOpenBtn.setFocusPainted(false);
		FileOpenBtn.setBounds(276, 367, 32, 32);
		
		lobbyChatPanel.add(lobbyChatLabel);
		lobbyChatPanel.add(chattingScroll);
		lobbyChatPanel.add(lobbyChatTextField);
		lobbyChatPanel.add(ImageOpenBtn);
		lobbyChatPanel.add(FileOpenBtn);
		

		centerPanel.add(gameListPanel);
		centerPanel.add(userListPanel);
		this.add(centerPanel);
		centerPanel.add(lobbyChatPanel);
		centerPanel.add(infoAndButton);

		repaint();
		invalidate();
	}

	private void setEvent() {
		lobbyChatTextField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (lobbyChatTextField.getText().length() != 0) {
					String msg = lobbyChatTextField.getText();
					cymNet.sendMSG("/MSG;" + cymFrame.getMyChatImagePath() + ";" + cymFrame.getMyProfileImagePath() + ";"
							+ cymFrame.getMyNickname() + ";" + cymFrame.getMyCharName() + ";" + cymFrame.getMyLevel()
							+ ";" + msg);
				}
				lobbyChatTextField.setText("");
				lobbyChatTextField.requestFocus();
			}
		});
		
		ImageOpenBtn.addActionListener(new ActionListener() {
			JFileChooser chooser = new JFileChooser();

			@Override
			public void actionPerformed(ActionEvent arg0) {
				FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG & PNG Images", "jpg", "png");
				chooser.setFileFilter(filter);
				int ret = chooser.showOpenDialog(null);
				if (ret != JFileChooser.APPROVE_OPTION) {
					JOptionPane.showMessageDialog(null, "파일을 선택하지 않았습니다", "경고", JOptionPane.WARNING_MESSAGE);
					lobbyChatTextField.requestFocus(); // 메세지를 보내고 커서를 다시 텍스트 필드로 위치시킨다
					return;
				}

				String filePath = chooser.getSelectedFile().getPath();
				fileSend(filePath, "/IMAGE;");
			}
		});
		
		FileOpenBtn.addActionListener(new ActionListener() {
			JFileChooser chooser = new JFileChooser();

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int ret = chooser.showOpenDialog(null);
				if (ret != JFileChooser.APPROVE_OPTION) {
					JOptionPane.showMessageDialog(null, "파일을 선택하지 않았습니다", "경고", JOptionPane.WARNING_MESSAGE);
					lobbyChatTextField.requestFocus(); // 메세지를 보내고 커서를 다시 텍스트 필드로 위치시킨다
					return;
				}

				String filePath = chooser.getSelectedFile().getPath();
				fileSend(filePath, "/FILE;");
			}
		});
		
		//
		// // Click create button to create a new game
//		createButton.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				showCreateDialog();
//			}
//		});

		// Click back button to go the entry panel
		backButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ChattingPane.setText("");
				cymFrame.sequenceControl("homePanel", 0);
				cymNet.sendMSG("/LOGOUT");
			}
		});

//		// Double click the game to join
//		gameList.addMouseListener(new MouseAdapter() {
//			public void mouseClicked(MouseEvent e) {
//				if (e.getClickCount() == 2) {
//					if (gameList.getSelectedIndex() == -1) {
//						JOptionPane.showMessageDialog(LobbyPanel.this.mainFrame.getContentPane(), "Select the room.");
//					} else {
//						System.out.println("I'm here!");
//						ProgressInfo pi = new ProgressInfo();
//						pi.set_status(ProgressInfo.JOIN_GAME_TRY);
//						pi.set_chat(gameList.getSelectedValue().trim());
//						LobbyPanel.this.mainFrame.sendProtocol(pi);
//					}
//				}
//			}
//		});
	}
	
	/* 선택한 이미지나 파일을 서버로 보냄 */
	public void fileSend(String filePath, String cmd) {
		File sendFile = new File(filePath); // 파일 생성
		String sendFileName = sendFile.getName(); // 이미지 파일 이름 받아오기
		long sendFileSize = sendFile.length(); // 이미지 파일 크기 받아오기

		cymNet.sendMSG(cmd + cymFrame.getMyChatImagePath() + ";" + cymFrame.getMyProfileImagePath() + ";"
				+ cymFrame.getMyNickname() + ";" + cymFrame.getMyCharName() + ";" + cymFrame.getMyLevel() + ";"
				+ sendFileName + ";" + sendFileSize);

		cymNet.sendFile(sendFile, sendFileSize);
		
		lobbyChatTextField.requestFocus(); // 메세지를 보내고 커서를 다시 텍스트 필드로 위치시킨다
	}

	public void myInfoUpdate() {
		myInfo.removeAll();
		myChar = new JLabel(new ImageIcon(this.cymFrame.getMyLobbyImagePath()));
		myChar.setBounds(5, 5, 100, 120);
		myChar.setBackground(Color.red);
		myChar.setOpaque(true);
		myInfo.add(myChar);

		idLabel = new JLabel[2];
		for (int i = 0; i < idLabel.length; i++) {
			idLabel[i] = new JLabel("Id:");
		}

		idLabel[0].setFont(new Font(CYMFrame.FONT, Font.BOLD, 16));
		idLabel[0].setBounds(110, 5, 50, 20);
		myInfo.add(idLabel[0]);

		idLabel[1].setFont(new Font(CYMFrame.FONT, Font.PLAIN, 14));
		idLabel[1].setText(this.cymFrame.getMyNickname());
		idLabel[1].setBounds(110, 20, 135, 20);
		myInfo.add(idLabel[1]);

		charNameLabel = new JLabel[2];
		for (int i = 0; i < charNameLabel.length; i++) {
			charNameLabel[i] = new JLabel("Char:");
		}

		charNameLabel[0].setFont(new Font(CYMFrame.FONT, Font.BOLD, 16));
		charNameLabel[0].setBounds(110, 45, 50, 20);
		myInfo.add(charNameLabel[0]);

		charNameLabel[1].setFont(new Font(CYMFrame.FONT, Font.PLAIN, 14));
		charNameLabel[1].setText(this.cymFrame.getMyCharName());
		charNameLabel[1].setBounds(110, 60, 135, 20);
		myInfo.add(charNameLabel[1]);

		levelLabel = new JLabel[2];
		for (int i = 0; i < levelLabel.length; i++) {
			levelLabel[i] = new JLabel("Level:");
		}

		levelLabel[0].setFont(new Font(CYMFrame.FONT, Font.BOLD, 16));
		levelLabel[0].setBounds(110, 85, 50, 20);
		myInfo.add(levelLabel[0]);

		levelLabel[1].setFont(new Font(CYMFrame.FONT, Font.PLAIN, 14));
		levelLabel[1].setText(Integer.toString(this.cymFrame.getMyLevel()));
		levelLabel[1].setBounds(110, 100, 135, 20);
		myInfo.add(levelLabel[1]);

		myInfo.repaint();
	}
	
	public void updateLobbyUser(ArrayList<String> updated) {
		usersLobby = new String[updated.size()];
		for (int i = 0; i < updated.size(); i++) {
			usersLobby[i] = "  " + updated.get(i);
		}
		userList.setListData(usersLobby);
	}

	public void updateLobbyGame(ArrayList<String> updated) {
		gamesLobby = new String[updated.size()];
		for (int i = 0; i < updated.size(); i++) {
			gamesLobby[i] = "  " + updated.get(i);
		}
		gameList.setListData(gamesLobby);
	}
	
	public void addRoom() {
		
	}
	
	/* String 붙임 */
	public void appendString(String str) {
		StyledDocument doc = ChattingPane.getStyledDocument();
		SimpleAttributeSet style = getChatFontStyle();
		
		try {
			doc.insertString(doc.getLength(), str, style);
		} catch (Exception e1) {
			System.out.println(e1);
		}
	}

	/* Component 붙임 */
	public void appendComponent(Component c) {
		int len = ChattingPane.getDocument().getLength();
		ChattingPane.setCaretPosition(len);
		ChattingPane.insertComponent(c);
	}
	
	/* 자신의 메세지 오른쪽에 붙임 */
	public void appendRightMyMsg(String nickname, int offset) {
		StyledDocument doc = ChattingPane.getStyledDocument();
		SimpleAttributeSet style = getChatFontStyle();
		
		if (nickname.equals("[" + this.cymFrame.getMyNickname() + "]")) {
			StyleConstants.setAlignment(style, StyleConstants.ALIGN_RIGHT);
			doc.setParagraphAttributes(offset, doc.getLength(), style, false);
			chattingScroll.getVerticalScrollBar().setValue(chattingScroll.getVerticalScrollBar().getMaximum());
		}
		else {
			StyleConstants.setAlignment(style, StyleConstants.ALIGN_LEFT);
			doc.setParagraphAttributes(offset, doc.getLength(), style, false);
			chattingScroll.getVerticalScrollBar().setValue(chattingScroll.getVerticalScrollBar().getMaximum());
		}
	}

	/* 채팅방 폰트 스타일 설정해 return */
	public SimpleAttributeSet getChatFontStyle() {
		SimpleAttributeSet style = new SimpleAttributeSet();
		StyleConstants.setFontSize(style, 17);
		StyleConstants.setBold(style, false);
		StyleConstants.setFontFamily(style, CYMFrame.FONT);
		StyleConstants.setForeground(style, Color.BLACK);
		
		return style;
	}
	
	/* MSG 붙임 */
	public void appendMessage(String ChatImgPath, String ProfileImgPath, String id, String charName, int level, String msg) {
		StyledDocument doc = ChattingPane.getStyledDocument();
		
		setIconListener(ChatImgPath, ProfileImgPath, id, charName, level);
		
		int offset = doc.getLength();
		String nickname = "[" + id + "]";
		appendString(nickname + " " + msg + "\n");
		
		appendRightMyMsg(nickname, offset);
		
	}
	
	/* IMAGE 붙임 */
	public void appendImage(String ChatImgPath, String ProfileImgPath, String id, String charName, int level, String fileName) {
		ImageIcon [] receiveImage = cymNet.receiveImageIcon();
		ImageIcon receiveOriginalImage = receiveImage[0];
		ImageIcon receiveResizingImage = receiveImage[1];

		StyledDocument doc = ChattingPane.getStyledDocument();

		int offset = doc.getLength();
		
		setIconListener(ChatImgPath, ProfileImgPath, id, charName, level);
		String nickname = "[" + id + "]";
		appendString(nickname + " ");
		
		JLabel resizingImageLabel = new JLabel(receiveResizingImage);
		resizingImageLabel.addMouseListener(new MyImageClickListener(receiveOriginalImage)); // 리사이징이미지 리스너 설정
		appendComponent(resizingImageLabel);
		setSaveBtnListener(fileName);
		
		appendString("\n");
		
		appendRightMyMsg(nickname, offset);
	}
	
	/* FILE 붙임 */
	public void appendFileInfo(String ChatImgPath, String ProfileImgPath, String id, String charName, int level, String fileName) {
		StyledDocument doc = ChattingPane.getStyledDocument();
		
		int offset = doc.getLength();
		
		setIconListener(ChatImgPath, ProfileImgPath, id, charName, level);
		String nickname = "[" + id + "]";
		appendString(nickname + " " + fileName);
		
		setSaveBtnListener(fileName);
		
		appendString("\n");
		
		appendRightMyMsg(nickname, offset);
	}
	
	/* 아이콘 리스너 설정 */
	public void setIconListener(String charImagPath, String ProfileImgPath, String id, String charName, int level) {
		icon = new ImageIcon(charImagPath);
		iconLabel = new JLabel(icon);
		iconLabel.addMouseListener(new MyProfileClickListener(ProfileImgPath, id, charName, level));
		appendComponent(iconLabel);
	}
	
	/* 저장 버튼 리스너 설정 */
	public void setSaveBtnListener(String fileName) {
		JButton fileSaveBtn = new JButton(new ImageIcon(CYMFrame.ImagePath + "fileSave.png"));
		fileSaveBtn.setBorderPainted(false);
		fileSaveBtn.setContentAreaFilled(false);
		fileSaveBtn.addMouseListener(new MyFileSavaBtnClickListener(cymNet, fileName));
		appendComponent(fileSaveBtn);
	}

	@Override
	public void receiveMSG(String msg) {
		String splitMsg[];
		splitMsg = msg.split(";");
		if (splitMsg[0].equals("/MSG")) {
			appendMessage(splitMsg[1], splitMsg[2], splitMsg[3], splitMsg[4], Integer.parseInt(splitMsg[5]), splitMsg[6]);
		} else if(splitMsg[0].equals("/IMAGE")) {
			appendImage(splitMsg[1], splitMsg[2], splitMsg[3], splitMsg[4], Integer.parseInt(splitMsg[5]), splitMsg[6]);
		} else if(splitMsg[0].equals("/FILE")) {
			appendFileInfo(splitMsg[1], splitMsg[2], splitMsg[3], splitMsg[4], Integer.parseInt(splitMsg[5]), splitMsg[6]);
		} else if(splitMsg[0].equals("/FILESEND")) {
			String saveFolder = receiveFilePath; // 경로
			File targetDir = new File(saveFolder);

			if (!targetDir.exists()) { // 디렉토리 없으면 생성.
				targetDir.mkdirs();
			}
			
			cymNet.receiveFile(receiveFilePath, splitMsg[1], Integer.parseInt(splitMsg[2]));
		}
	}
}

/* 프로필 클릭 리스너 */
class MyProfileClickListener extends MouseAdapter {
	private ImageIcon profileImg;
	private String id;
	private String charName;
	private int level;

	public MyProfileClickListener(String imagPath, String id, String charName, int level) {
		this.profileImg = new ImageIcon(imagPath);
		this.id = id;
		this.charName = charName;
		this.level = level;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		new ProfileDialog(profileImg, id, charName, level);
	}
}

/* 이미지 클릭 리스너 */
class MyImageClickListener extends MouseAdapter {
	private ImageIcon imageIcon;

	public MyImageClickListener(ImageIcon imageIcon) {
		this.imageIcon = imageIcon;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		new OriginalImageDialog(imageIcon);
	}
}

/* 저장버튼 클릭 리스너 */
class MyFileSavaBtnClickListener extends MouseAdapter {
	private CYMNet cymNet;
	private String fileName;

	public MyFileSavaBtnClickListener(CYMNet cymNet, String fileName) {
		this.cymNet = cymNet;
		this.fileName = fileName;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		cymNet.sendMSG("/FILESAVE;" + fileName);
	}
}
