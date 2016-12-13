package room;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.LineBorder;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import info.UserInfo;
import main.CYMFrame;
import network.CYMNet;
import network.Protocol;
import superPanel.ReceiveJPanel;

public class RoomPanel extends ReceiveJPanel {
	public static final int ROUND_TIME = 20;

	// for network
	private CYMNet cymNet;
	private CYMFrame cymFrame;

	// for inner panels
	private JPanel northPanel, centerPanel, drawingPanel, westPanel, eastPanel, southPanel;
	private JLabel titleImage;
	private JPanel centerToolPanel, centerCanvasPanel;
	private JPanel[] userPanel = new JPanel[4];
	private JTextPane[] userChat = new JTextPane[4];
	private JLabel[] userChar = new JLabel[4];
	private JLabel[] userNickname = new JLabel[4];
	private JLabel[] userScoreLabel = new JLabel[4];
	private JLabel[] userScore = new JLabel[4];
	private JLabel[] userLevelLabel = new JLabel[4];
	private JLabel[] userLevel = new JLabel[4];
	private JTextField gameChatField;
	private JTextPane answer, timer;
	private JButton clearAll, eraser, color[];
	private JButton startButton, backButton;

	// for drawing
	private Canvas canvas;
	public Point oldPoint;
	public Point newPoint;
	private ArrayList<Point> pointList;
	private Color drawColor;
	private int drawThick;

	// for game operation
	private Vector<UserInfo> usersList; // 같은 방에 있는 사용자 정보
	private boolean gameStarted; // 게임이 시작되었는지
	private boolean isQuestioner; // 질문자
	private long gameTime; // 게임진행시간

	private Thread thread;
	// private int k = 0;

	/** RoomPanel Construction */
	public RoomPanel(CYMFrame cymFrame) {
		this.cymFrame = cymFrame;
		this.cymNet = cymFrame.getCYMNet();

		drawColor = Color.black;
		drawThick = 10;

		gameStarted = false;
		isQuestioner = false;

		setPanel();
		setEvent();
	}

	/** for GUI */
	private void setPanel() {
		this.setLayout(null);
		pointList = new ArrayList<Point>();
		usersList = new Vector<UserInfo>();

		// For north panel
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
		centerPanel.setBounds(0, 110, 800, 360);
		centerPanel.setBackground(new Color(64, 64, 64));
		centerPanel.setOpaque(true);

		drawingPanel = new JPanel(null);
		drawingPanel.setBounds(145, 7, 501, 340);
		drawingPanel.setBackground(new Color(64, 64, 64));
		drawingPanel.setOpaque(true);

		/* For drawing tools */
		centerToolPanel = new JPanel(null);
		centerToolPanel.setBounds(0, 0, 501, 33);
		centerToolPanel.setBorder(new LineBorder(new Color(219, 219, 219), 2));
		StyleContext contextAnswer = new StyleContext();
		StyledDocument documentAnswer = new DefaultStyledDocument(contextAnswer);
		Style styleAnswer = contextAnswer.getStyle(StyleContext.DEFAULT_STYLE);
		StyleConstants.setAlignment(styleAnswer, StyleConstants.ALIGN_CENTER);
		answer = new JTextPane(documentAnswer);
		answer.setBounds(0, 0, 150, 35);
		answer.setFont(new Font(CYMFrame.FONT, Font.BOLD, 20));
		answer.setText("ANSWER");
		answer.setBorder(new LineBorder(new Color(64, 64, 64), 2));
		answer.setEditable(false);
		clearAll = new JButton(new ImageIcon(CYMFrame.ImagePath + "canvas.png"));
		clearAll.setBounds(150, 0, 37, 35);
		clearAll.setContentAreaFilled(false);
		eraser = new JButton(new ImageIcon(CYMFrame.ImagePath + "eraser.png"));
		eraser.setBounds(187, 0, 35, 35);
		eraser.setContentAreaFilled(false);
		color = new JButton[6];
		for (int i = 0; i < color.length; i++) {
			color[i] = new JButton();
			color[i].setBounds(221 + i * 31, 0, 30, 35);
		}
		color[0].setBackground(Color.black);
		color[1].setBackground(Color.red);
		color[2].setBackground(Color.yellow);
		color[3].setBackground(Color.green);
		color[4].setBackground(Color.blue);
		color[5].setBackground(new Color(128, 0, 128));
		StyleContext contextTimer = new StyleContext();
		StyledDocument documentTimer = new DefaultStyledDocument(contextTimer);
		Style styleTimer = contextTimer.getStyle(StyleContext.DEFAULT_STYLE);
		StyleConstants.setAlignment(styleTimer, StyleConstants.ALIGN_CENTER);
		timer = new JTextPane(documentTimer);
		timer.setBounds(440, 0, 60, 35);
		timer.setFont(new Font(CYMFrame.FONT, Font.BOLD, 17));
		timer.setText("TIMER");
		timer.setBorder(new LineBorder(Color.black, 2));
		timer.setEditable(false);
		centerToolPanel.add(answer);
		centerToolPanel.add(clearAll);
		centerToolPanel.add(eraser);
		for (int i = 0; i < 6; i++) {
			centerToolPanel.add(color[i]);
		}
		centerToolPanel.add(timer);
		// For drawing canvas
		centerCanvasPanel = new JPanel(null);
		centerCanvasPanel.setBounds(0, 35, 501, 305);
		centerCanvasPanel.setBorder(new LineBorder(new Color(255, 206, 5), 2));
		centerCanvasPanel.add(canvas = new Canvas());
		canvas.setBackground(Color.white);
		canvas.setBounds(0, 0, 500, 305);
		canvas.setEnabled(true);
		drawingPanel.add(centerToolPanel);
		drawingPanel.add(centerCanvasPanel);

		centerPanel.add(drawingPanel);

		// For west panel: 2 users
		westPanel = new JPanel(null);
		westPanel.setBounds(15, 10, 130, 336);
		westPanel.setBorder(new LineBorder(new Color(255, 206, 5), 3));
		westPanel.setBackground(new Color(255, 230, 156));
		westPanel.setOpaque(true);
		for (int i = 0; i < 2; i++) {
			userPanel[i] = new JPanel();
			userChat[i] = new JTextPane();
			userChar[i] = new JLabel();
			userNickname[i] = new JLabel("");
			userScoreLabel[i] = new JLabel("");
			userScore[i] = new JLabel("");
			userLevel[i] = new JLabel("");
			userLevelLabel[i] = new JLabel("");
			userPanel[i].setBounds(0, i * 180, 140, 180);
			westPanel.add(userPanel[i]);
		}
		centerPanel.add(westPanel);

		// For east panel: 2 users
		eastPanel = new JPanel(null);
		eastPanel.setBounds(646, 10, 130, 336);
		eastPanel.setBorder(new LineBorder(new Color(255, 206, 5), 3));
		eastPanel.setBackground(new Color(255, 230, 156));
		for (int i = 2; i < 4; i++) {
			userPanel[i] = new JPanel();
			userChat[i] = new JTextPane();
			userChar[i] = new JLabel();
			userNickname[i] = new JLabel("");
			userScore[i] = new JLabel("");
			userLevel[i] = new JLabel("");
			userPanel[i].setBounds(0, i * 180, 140, 180);
			userPanel[i].setBackground(new Color(255, 230, 156));
			userPanel[i].setOpaque(true);
			eastPanel.add(userPanel[i]);
		}
		centerPanel.add(eastPanel);

		// For south panel: chat and buttons
		southPanel = new JPanel(null);
		southPanel.setBounds(0, 470, 800, 50);
		southPanel.setBackground(new Color(64, 64, 64));
		gameChatField = new JTextField();
		gameChatField.setBounds(240, 0, 250, 40);
		gameChatField.setFont(new Font(CYMFrame.FONT, Font.BOLD, 30));
		gameChatField.setBorder(new LineBorder(new Color(255, 206, 5), 4));
		startButton = new JButton(new ImageIcon(CYMFrame.ImagePath + "startUp.png"));
		startButton.setBounds(530, 2, 100, 37);
		backButton = new JButton(new ImageIcon(CYMFrame.ImagePath + "backUp.png"));
		backButton.setBounds(635, 2, 100, 37);
		southPanel.add(gameChatField);
		southPanel.add(startButton);
		southPanel.add(backButton);

		this.add(centerPanel);
		this.add(southPanel);
	}

	/** for Event */
	private void setEvent() {

		// 게임 채팅창 TextField 이벤트
		gameChatField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!isQuestioner) {
					// 프로토콜 전송
					Protocol pt = new Protocol();
					pt.setStatus(Protocol.GAME_CHAT_MSG);
					pt.setChatSentence(gameChatField.getText());
					cymNet.sendProtocol(pt);
					gameChatField.setText("");
				}
			}
		});

		// start button 이벤트
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!gameStarted) {
					// 프로토콜 전송
					Protocol pt = new Protocol();
					pt.setStatus(Protocol.GAME_START);
					cymNet.sendProtocol(pt);
				}
			}
		});

		// back button 이벤트 (to go the entry panel)
		backButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!gameStarted) {
					cymFrame.sequenceControl("lobbyPanel", 0);

					// 프로토콜 전송
					Protocol pt = new Protocol();
					pt.setStatus(Protocol.GAME_LOGOUT);
					cymNet.sendProtocol(pt);
				}
			}
		});

		// Mouse drag 이벤트
		canvas.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e) && isQuestioner) {
					System.out.println("<RoomPanel> canvasEvent mouseDragged 들어옴");
					pointList = new ArrayList<Point>();
					pointList.add(new Point(e.getX(), e.getY()));

					// 프로토콜 전송
					Protocol pt = new Protocol();
					pt.setStatus(Protocol.GAME_DRAW);
					pt.setPointList(pointList);
					cymNet.sendProtocol(pt);
				}
			}
		});

		// Mouse Pressed, Released 이벤트
		canvas.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e) && isQuestioner) {
					System.out.println("<RoomPanel> canvasEvent mousePressed 들어옴");
					pointList = new ArrayList<Point>();
					pointList.add(new Point(e.getX(), e.getY()));

					// 프로토콜 전송
					Protocol pt = new Protocol();
					pt.setStatus(Protocol.GAME_DRAW);
					pt.setPointList(pointList);
					cymNet.sendProtocol(pt);
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e) && isQuestioner) {
					System.out.println("<GamePanel> canvasEvent mouseRelease 들어옴");
					pointList = new ArrayList<Point>();
					pointList.add(null);

					// 프로토콜 전송
					Protocol pt = new Protocol();
					pt.setStatus(Protocol.GAME_DRAW);
					pt.setPointList(pointList);
					cymNet.sendProtocol(pt);
				}
			}

		});

		// clear button 이벤트
		clearAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (isQuestioner) {
					// 프로토콜 전송
					Protocol pt = new Protocol();
					pt.setStatus(Protocol.GAME_DRAW_ALLCLEAR);
					cymNet.sendProtocol(pt);
				}
			}
		});

		// eraser button 이벤트
		eraser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (isQuestioner) {
					// 프로토콜 전송
					Protocol pt = new Protocol();
					pt.setStatus(Protocol.GAME_DRAW_ERASER);
					cymNet.sendProtocol(pt);
				}
			}
		});

		// color button 이벤트
		for (int i = 0; i < color.length; i++) {
			color[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JButton color = (JButton) e.getSource();
					if (isQuestioner) {
						// 프로토콜 전송
						Protocol pt = new Protocol();
						pt.setStatus(Protocol.GAME_DRAW_SELECT_COLOR);
						pt.setDrawColor(colorToInt(color.getBackground()));
						cymNet.sendProtocol(pt);
					}
				}
			});
		}
	}

	/** 서버로부터 Protocol을 수신받는 오버라이딩 메서드 */
	@Override
	public void receiveProtocol(Protocol pt) {
		int status = pt.getStatus();
		System.out.println("<RoomPanel> receiveProtocol status: " + status);

		switch (status) {
		case Protocol.GAME_CREATED:
			System.out.println("<RoomPanel> GAME_CREATED");
			usersList = pt.getUsersInRoom();
			updateRoomPanel(usersList);
			break;
		case Protocol.GAME_JOIN_PARTIPANT:
			System.out.println("<RoomPanel> GAME_JOIN_PARTIPANT");
			usersList = pt.getUsersInRoom();
			updateRoomPanel(usersList);
			break;
		case Protocol.GAME_CORRECT_ANSWER:
			System.out.println("<RoomPanel> GAME_CORRECT_ANSWER");
			gameChatUpdate(pt);
			correctAnswer(pt);
			scoreUpdate(pt);
			break;
		case Protocol.GAME_CHAT_UPDATE:
			System.out.println("<RoomPanel> GAME_CHAT_UPDATE");
			gameChatUpdate(pt);
			break;
//		case Protocol.GAME_START_SUCCESS_QUESTIONER:
//			System.out.println("<RoomPanel> GAME_START_SUCCESS_QUESTIONER");
//			canvas.repaint();
//			gameStarted(pt);
//			quetionerBorder(pt);
//			break;
//		case Protocol.GAME_START_SUCCESS_ANSWER:
//			System.out.println("<RoomPanel> GAME_START_SUCCESS_ANSWER");
//			canvas.repaint();
//			gameStarted(pt);
//			quetionerBorder(pt);
//			break;
		case Protocol.GAME_START_SUCCESS:
			System.out.println("<RoomPanel> GAME_START_SUCCESS");
			canvas.repaint();
			gameStarted(pt);
			quetionerBorder(pt);
			break;
		case Protocol.GAME_START_FAIL_LACK_USER:
			System.out.println("<RoomPanel> GAME_START_FAIL_LACK_USER");
			JOptionPane.showMessageDialog(cymFrame.getContentPane(), "You need at least two players!");
			break;
		case Protocol.GAME_START_FAIL_NOT_MASTER:
			System.out.println("<RoomPanel> GAME_START_FAIL_NOT_MASTER");
			JOptionPane.showMessageDialog(cymFrame.getContentPane(), "You are not the game master!");
			break;
		case Protocol.GAME_TIMER_BROADCAST:
			System.out.println("<RoomPanel> GAME_TIMER_BROADCAST");
			timerBroadCasting();
			break;
		}
	}

	/** color의 값을 내가 정한 int로 바꿔주는 메소드 */
	public int colorToInt(Color color) {
		if (color == Color.BLACK) {
			return 0;
		} else if (color == Color.red) {
			return 1;
		} else if (color == Color.yellow) {
			return 2;
		} else if (color == Color.green) {
			return 3;
		} else if (color == Color.blue) {
			return 4;
		} else if (color == new Color(128, 0, 128)) {
			return 5;
		} else {
			return 6;
		}
	}

	/** 인자로 주어진 사용자들로 EW 패널을 새로 업데이트하는 메소드 */
	public void updateRoomPanel(Vector<UserInfo> usersList) {
		this.usersList = usersList;
		updateGameEWPanel();
	}

	/** 새로운 사용자가 들어왔을 때 EW 패널 업데이트 */
	private void updateGameEWPanel() {
		westPanel.removeAll();
		eastPanel.removeAll();
		int usersListSize = usersList.size();

		// Initialize score of each player
		for (UserInfo ui : usersList) {
			ui.setMyScore(0);
		}
		answer.setText("ANSWER");
		timer.setText("TIMER");

		// Re-draw userNpanel according the number of users currently in game
		switch (usersListSize) {
		case 4: {
			updateGameUserPanel(4);
			userPanel[3].setLocation(3, 168);
			eastPanel.add(userPanel[3]);
		}
		case 3: {
			updateGameUserPanel(3);
			userPanel[2].setLocation(3, 168);
			westPanel.add(userPanel[2]);
		}
		case 2: {
			updateGameUserPanel(2);
			userPanel[1].setLocation(3, 3);
			eastPanel.add(userPanel[1]);
		}
		case 1: {
			updateGameUserPanel(1);
			userPanel[0].setLocation(3, 3);
			westPanel.add(userPanel[0]);
		}
		}
		eastPanel.revalidate();
		eastPanel.repaint();
		westPanel.revalidate();
		westPanel.repaint();
	}

	/** EW Panel에 들어갈 사용자 패널을 업데이트하는 메소드 */
	private void updateGameUserPanel(int i) {
		int index = i - 1;

		userPanel[index] = new JPanel(null);
		userPanel[index].setSize(123, 164);
		userPanel[index].setBackground(new Color(255, 230, 156));
		userPanel[index].setOpaque(true);

		userChar[index] = new JLabel(new ImageIcon(usersList.get(index).getMyGameCharImagePath()));
		userChar[index].setBounds(0, 0, 100, 100);
		StyleContext contextUser = new StyleContext();
		StyledDocument documentUser = new DefaultStyledDocument(contextUser);
		Style styleUser = contextUser.getStyle(StyleContext.DEFAULT_STYLE);
		StyleConstants.setAlignment(styleUser, StyleConstants.ALIGN_CENTER);
		userChat[index] = new JTextPane(documentUser);
		userChat[index].setBounds(0, 100, 123, 30);
		userChat[index].setFont(new Font(CYMFrame.FONT, Font.BOLD, 15));
		userChat[index].setText("");
		userChat[index].setBorder(new LineBorder(Color.black, 2));
		userChat[index].setEditable(false);

		userNickname[index] = new JLabel();
		userNickname[index].setText(usersList.get(index).getMyNickname());
		userNickname[index].setBounds(5, 132, 115, 15);
		userNickname[index].setFont(new Font(CYMFrame.FONT, Font.BOLD, 15));
		userScoreLabel[index] = new JLabel();
		userScoreLabel[index].setText("SCORE:");
		userScoreLabel[index].setBounds(3, 148, 40, 13);
		userScoreLabel[index].setFont(new Font(CYMFrame.FONT, Font.PLAIN, 11));
		userScore[index] = new JLabel();
		userScore[index].setText(Integer.toString(usersList.get(index).getMyScore()));
		userScore[index].setBounds(45, 148, 15, 13);
		userScore[index].setFont(new Font(CYMFrame.FONT, Font.PLAIN, 11));
		userLevelLabel[index].setText("LEVEL:");
		userLevelLabel[index].setBounds(65, 148, 40, 13);
		userLevelLabel[index].setFont(new Font(CYMFrame.FONT, Font.PLAIN, 11));
		userLevel[index] = new JLabel();
		userLevel[index].setText(Integer.toString(usersList.get(index).getMyLevel()));
		userLevel[index].setBounds(107, 148, 15, 13);
		userLevel[index].setFont(new Font(CYMFrame.FONT, Font.PLAIN, 11));

		userPanel[index].add(userChat[index]);
		userPanel[index].add(userChar[index]);
		userPanel[index].add(userNickname[index]);
		userPanel[index].add(userScoreLabel[index]);
		userPanel[index].add(userScore[index]);
		userPanel[index].add(userLevelLabel[index]);
		userPanel[index].add(userLevel[index]);
	}

	/** 접속자들의 chat fields를 업데이트하는 메소드 */
	public void gameChatUpdate(Protocol pt) {
		String nickName = pt.getUserInfo().getMyNickname();
		String chattingSentence = pt.getChatSentence();

		for (int i = 0; i < userNickname.length; i++) {
			if (!(userNickname[i].getText().equals(""))) {
				if (userNickname[i].getText().equals(nickName))
					userChat[i].setText(chattingSentence);
			}
		}
	}

	/** 같은 방 접속자들에게 정답을 맞춘 것을 다이얼로그로 알리는 메소드 */
	public void correctAnswer(Protocol pt) {
		String nickName = pt.getUserInfo().getMyNickname();
		String answer = pt.getChatSentence();

		System.out.println("<GamePanel> correctAnswer");
		System.out.println("nickName: " + nickName + ", answer: " + answer);

		gameStarted = false;
		String message = "";

		for (int i = 0; i < userNickname.length; i++) {
			if (userNickname[i].getText().equals(nickName))
				message = userNickname[i].getText() + " got correct!\n" + "ANSWER: " + answer;
		}

		final JOptionPane optionPane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE,
				JOptionPane.DEFAULT_OPTION, null, new Object[] {}, null);
		final JDialog dialog = new JDialog();
		dialog.setTitle("");
		dialog.setModal(true);
		dialog.setContentPane(optionPane);
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialog.pack();
		ActionListener action = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
				if (isQuestioner) {
					// 프로토콜 전송 (서버에 게임이 끝났음을 알림)
					pt.setStatus(Protocol.GAME_DRAW_TIMER_EXPIRE);
					cymNet.sendProtocol(pt);
				}
			}
		};
		Dimension frameSize = getSize();
		Dimension windowSize = Toolkit.getDefaultToolkit().getScreenSize();
		dialog.setLocation((windowSize.width - frameSize.width) / 2, (windowSize.height - frameSize.height) / 2);
		Timer timer = new Timer(2000, action);
		timer.setRepeats(false);
		timer.start();
		dialog.setVisible(true);
	}

	/** 정답을 맞췄을 때 맞춘 사용자의 점수를 업데이트하는 메소드 */
	public void scoreUpdate(Protocol pt) {
		String nickName = pt.getUserInfo().getMyNickname();
		int score = 0;
		// Update the score of target user
		for (UserInfo ui : usersList) {
			if (ui.getMyNickname().equals(nickName)) {
				ui.increaseScore();
				score = ui.getMyScore();
			}
		}

		// Update display of score for target user
		for (int i = 0; i < usersList.size(); i++) {
			if (!(userNickname[i].getText().equals(""))) {
				if (userNickname[i].getText().equals(nickName))
					userScore[i].setText(String.valueOf(score));
			}
		}
	}

	/** 게임 시작 메소드. 제한 시간 설정, 정답 Label 설정 */
	public void gameStarted(Protocol pt) {
		System.out.println("<RoomPanel> call gameStarted");

		String roundAnswer = pt.getRoundAnswer();
		String questioner = pt.getUserInfo().getMyNickname();

		// 질문자이면
		if (cymFrame.getUserInfo().getMyNickname().equals(questioner)) {
			isQuestioner = true;
			answer.setText(roundAnswer);
		}
		// 질문자가 아니면
		else {
			isQuestioner = false;
			answer.setText("ANSWER");
		}
		gameTime = ROUND_TIME;
		timer.setText(String.valueOf(gameTime));
		gameStarted = true;

		final JOptionPane optionPane = new JOptionPane("ROUND STARTS!", JOptionPane.INFORMATION_MESSAGE,
				JOptionPane.DEFAULT_OPTION, null, new Object[] {}, null);
		final JDialog dialog = new JDialog();
		dialog.setTitle("");
		dialog.setModal(true);
		dialog.setContentPane(optionPane);
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialog.pack();
		ActionListener action = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		};

		Timer timer = new Timer(1000, action);
		timer.setRepeats(false);
		timer.start();
		dialog.setVisible(true);
	}

	/** 질문자를 검은색 border로 표시하는 메소드 */
	public void quetionerBorder(Protocol pt) {
		System.out.println("<RoomPanel> call quetionerBorder");
		
		String questioner = pt.getUserInfo().getMyNickname();
		int size = usersList.size();

		for (int i = 0; i < userPanel.length; i++) {
			userPanel[i].setBorder(null);
		}

		for (int i = 1; i <= size; i++) {
			System.out.println("<GamePanel> quetionerBorder i : " + i);
			if (usersList.get(size - i).getMyNickname().equals(questioner)) {
				userPanel[size - i].setBorder(new LineBorder(Color.black, 4));
			}
		}
	}
	
	/**
	 * When the server notifies that 1 second elapsed If game is playing,
	 * decrement the in-game timer If in-game timer becomes zero, notice the
	 * server that the round ended
	 */
	public void timerBroadCasting() {
		if (gameStarted) {
			gameTime--;
			timer.setText(String.valueOf(gameTime));
			if (gameTime == 0) {
				gameStarted = false;
				if (isQuestioner) {
					Protocol pt = new Protocol();
					pt.setStatus(Protocol.GAME_DRAW_TIMER_EXPIRE);
					cymNet.sendProtocol(pt);
				}
			}
		}
	}
	//
	// /**
	// * Inform user that unable to start game because the user is not game
	// master
	// */
	// public void startDeniedMaster() {
	// JOptionPane.showMessageDialog(GamePanel.this.cymFrame.getContentPane(),
	// "You are not the game master!");
	// }
	//
	// /**
	// * Inform user that unable to start game because there is not enough
	// player
	// */
	// public void startDeniedNum() {
	// JOptionPane.showMessageDialog(GamePanel.this.cymFrame.getContentPane(),
	// "You need at least two players!");
	// }
	//

	// /**
	// * Draw the canvas with point list, selected color
	// *
	// * @param list
	// * of points drawn by questioner
	// */
	// public void drawBroadcasted(ArrayList<UserPoint> pList) {
	// Graphics g = canvas.getGraphics();
	// Graphics2D g2 = (Graphics2D) g;
	//
	// for (UserPoint p : pList) {
	// System.out.println("<GamePanel> drawColor: " + drawColor);
	// g2.setColor(drawColor);
	// g2.setStroke(new BasicStroke(5));
	// // g.setColor(drawColor);
	//
	// newPoint = p;
	//
	// if (newPoint == null) {
	// newPoint = oldPoint;
	// }
	// if (oldPoint == null) {
	// oldPoint = p;
	// }
	//
	// // g.drawLine(p1.get_pointX(), p1.get_pointY(), p2.get_pointX(),
	// // p2.get_pointY());
	//
	// System.out.println(newPoint.get_pointX() + ", " + newPoint.get_pointY());
	// System.out.println(oldPoint.get_pointX() + ", " + oldPoint.get_pointY());
	// g2.draw(new Line2D.Float(oldPoint.get_pointX(), oldPoint.get_pointY(),
	// newPoint.get_pointX(),
	// newPoint.get_pointY()));
	//
	// oldPoint = p;
	// }
	// }
	//
	// /** Clear the canvas */
	// public void clearBroadcasted() {
	// canvas.repaint();
	// }
	//
	// /**
	// * Questioner selected eraser. Set the color as white and set the eraser
	// * thickness
	// */
	// public void eraserBroadcasted() {
	// set_drawColor(6);
	// set_drawThick(25);
	// }
	//
	// /**
	// * Questioner selected color. Set the color as selected
	// *
	// * @param index
	// * of selected color
	// */
	// public void colorBroadcasted(int drawingColor) {
	// set_drawColor(drawingColor);
	// set_drawThick(10);
	// }
	//
	

	// /**
	// * Redraw all the panel as waiting state (not started)
	// *
	// * @param message
	// * noticing that the game ended
	// */
	// public void roundTerminated(String message) {
	// gameStarted = false;
	// final JOptionPane optionPane = new JOptionPane(message,
	// JOptionPane.INFORMATION_MESSAGE,
	// JOptionPane.DEFAULT_OPTION, null, new Object[] {}, null);
	// final JDialog dialog = new JDialog();
	// dialog.setTitle("");
	// dialog.setModal(true);
	// dialog.setContentPane(optionPane);
	// dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
	// dialog.pack();
	// ActionListener action = new ActionListener() {
	// public void actionPerformed(ActionEvent e) {
	// dialog.dispose();
	// updatePanel();
	// }
	// };
	// Timer timer = new Timer(4000, action);
	// timer.setRepeats(false);
	// timer.start();
	// dialog.setVisible(true);
	// }
	//

	//
	// /* Get methods */
	// public Color get_drawColor() {
	// return drawColor;
	// }
	//
	// public int get_drawThick() {
	// return drawThick;
	// }
	//
	// /* Set methods */
	// public void set_drawThick(int item) {
	// drawThick = item;
	// }
	//
	// public void set_drawColor(int option) {
	// switch (option) {
	// case 0: {
	// drawColor = Color.black;
	// break;
	// }
	// case 1: {
	// drawColor = Color.red;
	// break;
	// }
	// case 2: {
	// drawColor = Color.yellow;
	// break;
	// }
	// case 3: {
	// drawColor = Color.green;
	// break;
	// }
	// case 4: {
	// drawColor = Color.blue;
	// break;
	// }
	// case 5: {
	// drawColor = new Color(128, 0, 128);
	// break;
	// }
	// case 6: {
	// drawColor = Color.white;
	// break;
	// }
	// }
	// }
	//
	//

}
