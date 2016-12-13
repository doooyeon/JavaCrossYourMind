package lobby;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Vector;

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

import info.UserInfo;
import main.CYMFrame;
import network.CYMNet;
import network.Protocol;
import superPanel.ReceiveJPanel;

public class LobbyPanel extends ReceiveJPanel {

	// for network
	private CYMNet cymNet;
	private CYMFrame cymFrame;
	private UserInfo userInfo;

	private String receiveFilePath = "C:/Program Files/CrossYourMindClient";
	private String[] gameListNamesLobby, userListNamesLobby;

	// For inner panels
	private JPanel northPanel, centerPanel;
	private JLabel titleImage;
	private JLabel gameListLabel, userListLabel, myinfoLabel, lobbyChatLabel;
	private JPanel gameListPanel, userListPanel, lobbyChatPanel, infoAndButton;
	private JScrollPane gameListScroll, userListScroll, chattingScroll;
	private JList<String> gameList, userList;

	private JTextPane ChattingPane;
	private JTextField lobbyChatTextField;

	private ImageIcon icon;
	private JLabel iconLabel;

	private JButton ImageOpenBtn; // 이미지첨부버튼
	private JButton FileOpenBtn; // 파일첨부버튼

	private JPanel myInfo, buttonPanel;
	private JLabel myChar;
	private JLabel[] idLabel;
	private JLabel[] charNameLabel;
	private JLabel[] levelLabel;
	private JButton createButton, backButton;
	private JDialog createDialog;

	/** LobbyPanel construction */
	public LobbyPanel(CYMFrame cymFrame) {
		setLayout(null);
		this.cymFrame = cymFrame;
		userInfo = cymFrame.getUserInfo();
		cymNet = cymFrame.getCYMNet();

		initCreateDialog();
		setPanel();
		setEvent();
	}

	/** 방 생성에 필요한 다이얼로그 초기화 메소드 */
	public void initCreateDialog() {
		createDialog = new JDialog();
		CreateDialog cd = new CreateDialog(this);
		createDialog.setContentPane(cd);
		createDialog.setBounds(400, 300, 350, 150);
		createDialog.setResizable(false);
		createDialog.setVisible(false);
	}

	/** for GUI */
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

	/** for Event */
	private void setEvent() {
		// 채팅창 TextField 이벤트
		lobbyChatTextField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (lobbyChatTextField.getText().length() != 0) {
					String lobbyChatSentence = lobbyChatTextField.getText();
					System.out.println("<LobbyPanel> lobbyChatSentence: " + lobbyChatSentence);

					// 프로토콜 전송
					Protocol pt = new Protocol();
					pt.setStatus(Protocol.LOBBY_CHAT_MSG);
					pt.setUserInfo(userInfo);
					pt.setChatSentence(lobbyChatSentence);
					cymNet.sendProtocol(pt);
				}
				lobbyChatTextField.setText("");
				lobbyChatTextField.requestFocus();
			}
		});

		// Image 보내기 버튼 이벤트
		ImageOpenBtn.addActionListener(new ActionListener() {
			JFileChooser chooser = new JFileChooser();

			@Override
			public void actionPerformed(ActionEvent arg0) {
				FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG & PNG Images", "jpg", "png");
				chooser.setFileFilter(filter);
				int ret = chooser.showOpenDialog(null);
				if (ret != JFileChooser.APPROVE_OPTION) {
					JOptionPane.showMessageDialog(null, "파일을 선택하지 않았습니다", "경고", JOptionPane.WARNING_MESSAGE);
					lobbyChatTextField.requestFocus(); // 메세지를 보내고 커서를 다시 텍스트
														// 필드로 위치시킨다
					return;
				}
				String filePath = chooser.getSelectedFile().getPath();
				// 프로토콜 전송
				fileSend(filePath, Protocol.LOBBY_CHAT_IMAGE);
			}
		});

		// File 보내기 버튼 이벤트
		FileOpenBtn.addActionListener(new ActionListener() {
			JFileChooser chooser = new JFileChooser();

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int ret = chooser.showOpenDialog(null);
				if (ret != JFileChooser.APPROVE_OPTION) {
					JOptionPane.showMessageDialog(null, "파일을 선택하지 않았습니다", "경고", JOptionPane.WARNING_MESSAGE);
					lobbyChatTextField.requestFocus(); // 메세지를 보내고 커서를 다시 텍스트
														// 필드로 위치시킨다
					return;
				}
				String filePath = chooser.getSelectedFile().getPath();
				// 프로토콜 전송
				fileSend(filePath, Protocol.LOBBY_CHAT_FILE);
			}
		});

		// create button 이벤트 (to create a new game)
		createButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createDialog.setVisible(true);
			}
		});

		// back button 이벤트 (to go the entry panel)
		backButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ChattingPane.setText("");
				cymFrame.sequenceControl("homePanel", 0);
				// 프로토콜 전송
				Protocol pt = new Protocol();
				pt.setStatus(Protocol.LOBBY_LOGOUT);
				cymNet.sendProtocol(pt);
			}
		});

		// room list double click 이벤트
		gameList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					if (gameList.getSelectedIndex() == -1) {
						JOptionPane.showMessageDialog(cymFrame.getContentPane(), "Select the room.");
					} else {
						System.out.println("<LobbyPanel> want to enter the room!");

						// 프로토콜 전송
						Protocol pt = new Protocol();
						pt.setStatus(Protocol.LOBBY_JOIN_ROOM);
						pt.setRoomName(gameList.getSelectedValue().trim());
						System.out.println("TEST <LobbyPanel> enter room name: " + gameList.getSelectedValue().trim());
						pt.setUserInfo(userInfo);
						cymNet.sendProtocol(pt);
					}
				}
			}
		});
	}

	/** 선택한 이미지나 파일을 서버로 보내는 메서드 */
	public void fileSend(String filePath, int cmd) {
		File sendFile = new File(filePath); // 파일 생성
		String sendFileName = sendFile.getName(); // 이미지 파일 이름 받아오기
		long sendFileSize = sendFile.length(); // 이미지 파일 크기 받아오기

		// 프로토콜 전송
		Protocol pt = new Protocol();
		pt.setStatus(cmd);
		pt.setUserInfo(userInfo);
		pt.setSendFileName(sendFileName);
		pt.setSendFileSize(sendFileSize);

		cymNet.sendProtocol(pt);
		cymNet.sendFile(sendFile, sendFileSize);

		lobbyChatTextField.requestFocus(); // 메세지를 보내고 커서를 다시 텍스트 필드로 위치시킨다
	}

	/** Lobby에 표시되는 내 정보를 업데이트하는 메서드 */
	public void myInfoUpdate() {
		myInfo.removeAll();
		myChar = new JLabel(new ImageIcon(this.userInfo.getMyLobbyImagePath()));
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
		idLabel[1].setText(this.userInfo.getMyNickname());
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
		charNameLabel[1].setText(this.userInfo.getMyCharName());
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
		levelLabel[1].setText(Integer.toString(this.userInfo.getMyLevel()));
		levelLabel[1].setBounds(110, 100, 135, 20);
		myInfo.add(levelLabel[1]);

		myInfo.repaint();
	}

	/** Lobby에 표시되는 접속자들을 업데이트하는 메서드 */
	public void updateLobbyUser(Vector<String> updated) {
		userListNamesLobby = new String[updated.size()];
		for (int i = 0; i < updated.size(); i++) {
			userListNamesLobby[i] = "  " + updated.get(i);
		}
		userList.setListData(userListNamesLobby);
	}

	/** Lobby에 표시되는 게임방 이름들을 업데이트하는 메서드 */
	public void updateLobbyGame(Vector<String> updated) {
		gameListNamesLobby = new String[updated.size()];
		for (int i = 0; i < updated.size(); i++) {
			gameListNamesLobby[i] = "  " + updated.get(i);
		}
		gameList.setListData(gameListNamesLobby);
	}

	/** 생성한 방을 추가하는 메서드 */
	public void addRoom() {

	}

	/** 채팅에 String 붙이는 메서드 */
	public void appendString(String str) {
		StyledDocument doc = ChattingPane.getStyledDocument();
		SimpleAttributeSet style = getChatFontStyle();

		try {
			doc.insertString(doc.getLength(), str, style);
		} catch (Exception e1) {
			System.out.println(e1);
		}
	}

	/** 채팅에 Component 붙이는 메서드 */
	public void appendComponent(Component c) {
		int len = ChattingPane.getDocument().getLength();
		ChattingPane.setCaretPosition(len);
		ChattingPane.insertComponent(c);
	}

	/** 채팅에서 자신의 메세지 오른쪽에 붙이는 메서드 */
	public void appendRightMyMsg(String nickname, int offset) {
		StyledDocument doc = ChattingPane.getStyledDocument();
		SimpleAttributeSet style = getChatFontStyle();

		if (nickname.equals("[" + this.userInfo.getMyNickname() + "]")) {
			StyleConstants.setAlignment(style, StyleConstants.ALIGN_RIGHT);
			doc.setParagraphAttributes(offset, doc.getLength(), style, false);
			chattingScroll.getVerticalScrollBar().setValue(chattingScroll.getVerticalScrollBar().getMaximum());
		} else {
			StyleConstants.setAlignment(style, StyleConstants.ALIGN_LEFT);
			doc.setParagraphAttributes(offset, doc.getLength(), style, false);
			chattingScroll.getVerticalScrollBar().setValue(chattingScroll.getVerticalScrollBar().getMaximum());
		}
	}

	/** 채팅 폰트 스타일을 설정하여 리턴 */
	public SimpleAttributeSet getChatFontStyle() {
		SimpleAttributeSet style = new SimpleAttributeSet();
		StyleConstants.setFontSize(style, 17);
		StyleConstants.setBold(style, false);
		StyleConstants.setFontFamily(style, CYMFrame.FONT);
		StyleConstants.setForeground(style, Color.BLACK);

		return style;
	}

	/** 채팅에 MSG 붙이는 메서드 */
	public void appendMessage(Protocol pt) {
		StyledDocument doc = ChattingPane.getStyledDocument();

		setIconListener(pt);

		int offset = doc.getLength();
		String nickname = "[" + pt.getUserInfo().getMyNickname() + "]";
		appendString(nickname + " " + pt.getChatSentence() + "\n");

		appendRightMyMsg(nickname, offset);
	}

	/** 채팅에 IMAGE 붙이는 메서드 */
	public void appendImage(Protocol pt) {
		ImageIcon[] receiveImage = cymNet.receiveImageIcon();
		ImageIcon receiveOriginalImage = receiveImage[0];
		ImageIcon receiveResizingImage = receiveImage[1];

		StyledDocument doc = ChattingPane.getStyledDocument();

		int offset = doc.getLength();

		setIconListener(pt);
		String nickname = "[" + pt.getUserInfo().getMyNickname() + "]";
		appendString(nickname + " ");

		JLabel resizingImageLabel = new JLabel(receiveResizingImage);
		resizingImageLabel.addMouseListener(new MyImageClickListener(receiveOriginalImage)); // 리사이징이미지
																								// 리스너
		appendComponent(resizingImageLabel);
		setSaveBtnListener(pt.getSendFileName());

		appendString("\n");

		appendRightMyMsg(nickname, offset);
	}

	/** 채팅에 FILE 붙이는 메서드 */
	public void appendFileInfo(Protocol pt) {
		StyledDocument doc = ChattingPane.getStyledDocument();

		int offset = doc.getLength();

		setIconListener(pt);
		String nickname = "[" + pt.getUserInfo().getMyNickname() + "]";
		String fileName = pt.getSendFileName();
		appendString(nickname + " " + fileName);

		setSaveBtnListener(fileName);
		appendString("\n");
		appendRightMyMsg(nickname, offset);
	}

	/** Profile Icon 이벤트 리스너 */
	public void setIconListener(Protocol pt) {
		icon = new ImageIcon(pt.getUserInfo().getMyChatImagePath());
		iconLabel = new JLabel(icon);
		iconLabel.addMouseListener(new MyProfileClickListener(pt));
		appendComponent(iconLabel);
	}

	/** Save button 이벤트 리스너 */
	public void setSaveBtnListener(String fileName) {
		JButton fileSaveBtn = new JButton(new ImageIcon(CYMFrame.ImagePath + "fileSave.png"));
		fileSaveBtn.setBorderPainted(false);
		fileSaveBtn.setContentAreaFilled(false);
		fileSaveBtn.addMouseListener(new MyFileSavaBtnClickListener(cymNet, fileName));
		appendComponent(fileSaveBtn);
	}

	/** 서버로부터 Protocol을 수신받는 오버라이딩 메서드 */
	@Override
	public void receiveProtocol(Protocol pt) {
		System.out.println("<LobbyPanel> receiveProtocol");
		switch (pt.getStatus()) {
		case Protocol.LOBBY_CHAT_MSG:
			System.out.println("<LobbyPanel> Protocol.MSG");
			appendMessage(pt);
			break;
		case Protocol.LOBBY_CHAT_IMAGE:
			System.out.println("<LobbyPanel> Protocol.IMAGE");
			appendImage(pt);
			break;
		case Protocol.LOBBY_CHAT_FILE:
			System.out.println("<LobbyPanel> Protocol.FILE");
			appendFileInfo(pt);
			break;
		case Protocol.LOBBY_CHAT_FILESEND:
			System.out.println("<LobbyPanel> Protocol.FILESEND");
			String saveFolder = receiveFilePath; // 경로
			File targetDir = new File(saveFolder);

			if (!targetDir.exists()) { // 디렉토리 없으면 생성.
				targetDir.mkdirs();
			}
			cymNet.receiveFile(receiveFilePath, pt.getSendFileName(), pt.getSendFileSize());

			break;
		case Protocol.LOBBY_UPDATE_GAME_LIST:
			System.out.println("<LobbyPanel> Protocol.UPDATE_GAME_LIST");
			updateLobbyGame(pt.getGameList());
			break;
		case Protocol.LOBBY_UPDATE_USER_LIST:
			System.out.println("<LobbyPanel> Protocol.UPDATE_USER_LIST");
			updateLobbyUser(pt.getUserList());
			break;
		case Protocol.LOBBY_CREATE_ROOM_FAIL:
			System.out.println("<LobbyPanel> Protocol.CREATE_ROOM_FAIL");
			JOptionPane.showMessageDialog(cymFrame.getContentPane(), "Game name duplicated.\n Try another one!");
			break;
		case Protocol.LOBBY_CREATE_ROOM_SUCCESS:
			System.out.println("<LobbyPanel> Protocol.CREATE_ROOM_SUCCESS");
			ChattingPane.setText("");
			cymFrame.sequenceControl("roomPanel", 0);
			closeCreateDialog();

			// 프로토콜 전송
			pt.setStatus(Protocol.GAME_IN);
			cymNet.sendProtocol(pt);
			System.out.println("<LobbyPanel> send GAME_IN");
			break;
		case Protocol.LOBBY_JOIN_ROOM_FAIL:
			System.out.println("<LobbyPanel> Protocol.LOBBY_JOIN_ROOM_FAIL");
			JOptionPane.showMessageDialog(cymFrame.getContentPane(), "The game is full or already started!");
			break;
		case Protocol.LOBBY_JOIN_ROOM_SUCCESS:
			System.out.println("<LobbyPanel> Protocol.LOBBY_JOIN_ROOM_SUCCESS");
			ChattingPane.setText("");
			cymFrame.sequenceControl("roomPanel", 0);
			closeCreateDialog();

			// 프로토콜 전송
			pt.setStatus(Protocol.GAME_JOIN_IN);
			cymNet.sendProtocol(pt);
			System.out.println("<LobbyPanel> send GAME_JOIN_IN");
			break;
		}
	}

	/** 게임 방 생성 다이얼로그를 숨긴다. */
	public void closeCreateDialog() {
		createDialog.setVisible(false);
	}

	/** 게임 방 생성 버튼 클릭 이벤트에 대한 내부 클래스 */
	class CreateDialog extends JPanel {
		// for Connect to its parent panel
		LobbyPanel lp;

		// for inner panels
		JPanel southPanel;
		JLabel message;
		JTextField roomNameTextField;
		JButton createButton, exitButton;

		/** CreateDialog Construction */
		public CreateDialog(LobbyPanel lp) {
			this.lp = lp;
			setPanel();
			setEvent();
		}

		/** for GUI */
		private void setPanel() {
			this.setLayout(new BorderLayout());
			// Inform user to enter the game name
			message = new JLabel("Enter the name of game room.");
			message.setPreferredSize(new Dimension(200, 30));
			message.setBackground(new Color(242, 242, 242));
			message.setOpaque(true);
			message.setFont(new Font(CYMFrame.FONT, Font.BOLD, 20));
			message.setHorizontalAlignment(JLabel.CENTER);
			message.setVerticalAlignment(JLabel.CENTER);
			this.add(BorderLayout.NORTH, message);

			// Textfield for user to type game name
			roomNameTextField = new JTextField();
			roomNameTextField.setPreferredSize(new Dimension(200, 60));
			roomNameTextField.setFont(new Font(null, Font.BOLD, 30));
			roomNameTextField.setBorder(new LineBorder(new Color(255, 206, 5), 4));
			this.add(BorderLayout.CENTER, roomNameTextField);

			// South panel for buttons
			southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
			southPanel.setPreferredSize(new Dimension(150, 50));
			southPanel.setBackground(new Color(242, 242, 242));
			createButton = new JButton(new ImageIcon(CYMFrame.ImagePath + "createUp.png"));
			createButton.setPreferredSize(new Dimension(100, 37));
			exitButton = new JButton(new ImageIcon(CYMFrame.ImagePath + "backUp.png"));
			exitButton.setPreferredSize(new Dimension(100, 37));
			southPanel.add(createButton);
			southPanel.add(exitButton);
			this.add(BorderLayout.SOUTH, southPanel);
		}

		/** for Event */
		private void setEvent() {
			// room name TextField 이벤트
			roomNameTextField.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (roomNameTextField.getText().equals(""))
						JOptionPane.showMessageDialog(CreateDialog.this.lp.cymFrame.getContentPane(),
								"Please enter room name.");
					else {
						Protocol pt = new Protocol();
						pt.setStatus(Protocol.LOBBY_CREATE_ROOM);
						pt.setRoomName(roomNameTextField.getText()); // room 이름
																		// 설정
						CreateDialog.this.lp.cymNet.sendProtocol(pt);
						roomNameTextField.setText("");
					}
				}
			});

			// create button 이벤트
			createButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (roomNameTextField.getText().equals(""))
						JOptionPane.showMessageDialog(CreateDialog.this.lp.cymFrame.getContentPane(),
								"Please enter room name.");
					else {
						Protocol pt = new Protocol();
						pt.setStatus(Protocol.LOBBY_CREATE_ROOM);
						pt.setRoomName(roomNameTextField.getText()); // room이름설정
						CreateDialog.this.lp.cymNet.sendProtocol(pt);
						roomNameTextField.setText("");
					}
				}
			});

			// exit button 이벤트
			exitButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					roomNameTextField.setText("");
					CreateDialog.this.lp.closeCreateDialog();
				}
			});
		}
	}

	/** 프로필 클릭 이벤트에 대한 내부 클래스 */
	class MyProfileClickListener extends MouseAdapter {
		private ImageIcon profileImg;
		private String id;
		private String charName;
		private int level;

		public MyProfileClickListener(Protocol pt) {
			UserInfo userInfo = pt.getUserInfo();

			this.profileImg = new ImageIcon(userInfo.getMyProfileImagePath());
			this.id = userInfo.getMyNickname();
			this.charName = userInfo.getMyCharName();
			this.level = userInfo.getMyLevel();
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			new ProfileDialog(profileImg, id, charName, level);
		}
	}

	/** 이미지 클릭 이벤트에 대한 내부 클래스 */
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

	/** 저장 버튼 클릭 이벤트에 대한 내부 클래스 */
	class MyFileSavaBtnClickListener extends MouseAdapter {
		private CYMNet cymNet;
		private String fileName;

		public MyFileSavaBtnClickListener(CYMNet cymNet, String fileName) {
			this.cymNet = cymNet;
			this.fileName = fileName;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			// 프로토콜 전송
			Protocol pt = new Protocol();
			pt.setStatus(Protocol.LOBBY_CHAT_FILESAVE);
			pt.setSendFileName(fileName);
			cymNet.sendProtocol(pt);
		}
	}

}
