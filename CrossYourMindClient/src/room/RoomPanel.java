package room;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Line2D;
import java.util.ArrayList;

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

import info.PointInfo;
import info.UserInfo;
import main.CYMFrame;
import network.Protocol;
import superPanel.ReceiveJPanel;

public class RoomPanel extends ReceiveJPanel  {
	
	  // ** DEFINE **
	   public static final int ROUND_TIME = 60;
	   public PointInfo oldPoint;
	   public PointInfo newPoint;

	   // ** VARIABLE **
	   // Connect its parent frame
	   CYMFrame cymFrame;

	   // For inner panels
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

	   // For drawing
	   private Canvas canvas;
	   int pointX, pointY;
	   private ArrayList<PointInfo> pList;
	   private Color drawColor;
	   private int drawThick;

	   // For game operation
	   private ArrayList<UserInfo> usersGame;
	   private boolean gameStarted;
	   private boolean isQuestioner;
	   private long gameTime;
	   int answerCount;

	   Thread thread;
	   private int k = 0;

	   // ** CONSTRUCTOR **
	   public RoomPanel(CYMFrame cymFrame) {
	      this.cymFrame = cymFrame;

	      drawColor = Color.black;
	      drawThick = 10;

	      gameStarted = false;
	      isQuestioner = false;
	      answerCount = 0;

	      setPanel();
	      //setEvent();
	   }

	   // ** METHOD **
	   private void setPanel() {
	      // Initialize data
	      this.setLayout(null);
	      pList = new ArrayList<PointInfo>();
	      usersGame = new ArrayList<UserInfo>();

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
	      canvas.setBounds(0, 0, 500, 305); // heee?
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

	   /** Initialize reactions in this panel */
//	   private void setEvent() {
//	      // Press enter key to finish typing chat
//	      gameChatField.addActionListener(new ActionListener() {
//	         public void actionPerformed(ActionEvent e) {
//	            if (!isQuestioner) {
//	               ProgressInfo pi = new ProgressInfo();
//	               pi.set_status(ProgressInfo.CHAT_GAME);
//	               // heee
//	               pi.set_chattingSentence(gameChatField.getText());
//	               // pi.set_chat(gameChatField.getText());// ????필요한가????
//	               GamePanel.this.cymFrame.sendProtocol(pi);
//	               gameChatField.setText("");
//	            }
//	         }
//	      });
//
//	      // Click start button to start game
//	      startButton.addActionListener(new ActionListener() {
//	         public void actionPerformed(ActionEvent e) {
//	            if (!gameStarted) {
//	               ProgressInfo pi = new ProgressInfo();
//	               pi.set_status(ProgressInfo.START_TRY);
//	               GamePanel.this.cymFrame.sendProtocol(pi);
//	            }
//	         }
//	      });
//
//	      // Click back button to go to the lobby panel
//	      backButton.addActionListener(new ActionListener() {
//	         public void actionPerformed(ActionEvent e) {
//	            if (!gameStarted) {
//	               ProgressInfo pi = new ProgressInfo();
//	               pi.set_status(ProgressInfo.EXIT_GAME);
//	               GamePanel.this.cymFrame.sendProtocol(pi);
//	               GamePanel.this.cymFrame.setSize(MainFrame.lobbyPwidth, MainFrame.lobbyPheight);
//	               GamePanel.this.cymFrame.set_currentCard(MainFrame.lobbyPcard);
//	               GamePanel.this.cymFrame.get_card().show(GamePanel.this.cymFrame.getContentPane(),
//	                     MainFrame.lobbyPcard);
//	            }
//	         }
//	      });
//
//	      // Mouse drag to draw
//	      canvas.addMouseMotionListener(new MouseMotionAdapter() {
//	         @Override
//	         public void mouseDragged(MouseEvent e) {
//	            if (SwingUtilities.isLeftMouseButton(e) && isQuestioner) {
//	               System.out.println("<GamePanel> canvasEvent mouseDragged 들어옴");
//	               pList = new ArrayList<UserPoint>();
//	               pList.add(new UserPoint(e.getX(), e.getY()));
//
//	               ProgressInfo pi = new ProgressInfo();
//	               pi.set_status(ProgressInfo.DRAW);
//	               pi.set_pList(pList);
//	               GamePanel.this.cymFrame.sendProtocol(pi);
//	            }
//	         }
//	      });
//
//	      canvas.addMouseListener(new MouseAdapter() {
//	         @Override
//	         public void mousePressed(MouseEvent e) {
//	            if (SwingUtilities.isLeftMouseButton(e) && isQuestioner) {
//	               System.out.println("<GamePanel> canvasEvent mousePressed 들어옴");
//	               pList = new ArrayList<UserPoint>();
//	               pList.add(new UserPoint(e.getX(), e.getY()));
//
//	               ProgressInfo pi = new ProgressInfo();
//	               pi.set_status(ProgressInfo.DRAW);
//	               pi.set_pList(pList);
//	               GamePanel.this.cymFrame.sendProtocol(pi);
//	            }
//	         }
//
//	         @Override
//	         public void mouseReleased(MouseEvent e) {
//	            if (SwingUtilities.isLeftMouseButton(e) && isQuestioner) {
//	               System.out.println("<GamePanel> canvasEvent mouseRelease 들어옴");
//	               pList = new ArrayList<UserPoint>();
//	               pList.add(null);
//
//	               ProgressInfo pi = new ProgressInfo();
//	               pi.set_status(ProgressInfo.DRAW);
//	               pi.set_pList(pList);
//	               GamePanel.this.cymFrame.sendProtocol(pi);
//	            }
//	         }
//
//	      });
//
//	      // Click clear button to erase the whole canvas
//	      clearAll.addActionListener(new ActionListener() {
//	         public void actionPerformed(ActionEvent e) {
//	            if (isQuestioner) {
//	               ProgressInfo pi = new ProgressInfo();
//	               pi.set_status(ProgressInfo.SELECT_CLEAR);
//	               GamePanel.this.cymFrame.sendProtocol(pi);
//	            }
//	         }
//	      });
//
//	      // Click eraser button to select eraser
//	      eraser.addActionListener(new ActionListener() {
//	         public void actionPerformed(ActionEvent e) {
//	            if (isQuestioner) {
//	               ProgressInfo pi = new ProgressInfo();
//	               pi.set_status(ProgressInfo.SELECT_ERASER);
//	               GamePanel.this.cymFrame.sendProtocol(pi);
//	            }
//	         }
//	      });
//
//	      // Click color buttons to change select drawing color
//	      color[0].addActionListener(new ActionListener() {
//	         public void actionPerformed(ActionEvent e) {
//	            if (isQuestioner) {
//	               ProgressInfo pi = new ProgressInfo();
//	               pi.set_status(ProgressInfo.SELECT_COLOR);
//	               pi.set_drawColor(0);
//	               GamePanel.this.cymFrame.sendProtocol(pi);
//	            }
//	         }
//	      });
//	      color[1].addActionListener(new ActionListener() {
//	         public void actionPerformed(ActionEvent e) {
//	            if (isQuestioner) {
//	               ProgressInfo pi = new ProgressInfo();
//	               pi.set_status(ProgressInfo.SELECT_COLOR);
//	               pi.set_drawColor(1);
//	               GamePanel.this.cymFrame.sendProtocol(pi);
//	            }
//	         }
//	      });
//	      color[2].addActionListener(new ActionListener() {
//	         public void actionPerformed(ActionEvent e) {
//	            if (isQuestioner) {
//	               ProgressInfo pi = new ProgressInfo();
//	               pi.set_status(ProgressInfo.SELECT_COLOR);
//	               pi.set_drawColor(2);
//	               GamePanel.this.cymFrame.sendProtocol(pi);
//	            }
//	         }
//	      });
//	      color[3].addActionListener(new ActionListener() {
//	         public void actionPerformed(ActionEvent e) {
//	            if (isQuestioner) {
//	               ProgressInfo pi = new ProgressInfo();
//	               pi.set_status(ProgressInfo.SELECT_COLOR);
//	               pi.set_drawColor(3);
//	               GamePanel.this.cymFrame.sendProtocol(pi);
//	            }
//	         }
//	      });
//	      color[4].addActionListener(new ActionListener() {
//	         public void actionPerformed(ActionEvent e) {
//	            if (isQuestioner) {
//	               ProgressInfo pi = new ProgressInfo();
//	               pi.set_status(ProgressInfo.SELECT_COLOR);
//	               pi.set_drawColor(4);
//	               GamePanel.this.cymFrame.sendProtocol(pi);
//	            }
//	         }
//	      });
////	      color[5].addActionListener(new ActionListener() {
////	         public void actionPerformed(ActionEvent e) {
////	            if (isQuestioner) {
////	               ProgressInfo pi = new ProgressInfo();
////	               pi.set_status(ProgressInfo.SELECT_COLOR);
////	               pi.set_drawColor(5);
////	               GamePanel.this.mainFrame.sendProtocol(pi);
////	            }
////	         }
////	      });
//	   }

//	   /**
//	    * When a new user enters game, updates the west&east panel with updated
//	    * user list in game
//	    */
//	   private void updatePanel() {
//	      westPanel.removeAll();
//	      eastPanel.removeAll();
//	      int size = usersGame.size();
//
//	      // Initialize score of each player
//	      for (UserInfo ui : usersGame) {
//	         ui.set_score(0);
//	      }
//	      answer.setText("ANSWER");
//	      timer.setText("TIMER");
//
//	      // Re-draw userNpanel according the number of users currently in game
//	      switch (size) {
//
//	      case 4: {
//	         updateMethodPanel(4);
//	         userPanel[3].setLocation(3, 168);
//	         eastPanel.add(userPanel[3]);
//	      }
//	      case 3: {
//	         updateMethodPanel(3);
//	         userPanel[2].setLocation(3, 168);
//	         westPanel.add(userPanel[2]);
//	      }
//	      case 2: {
//	         updateMethodPanel(2);
//	         userPanel[1].setLocation(3, 3);
//	         eastPanel.add(userPanel[1]);
//
//	      }
//	      case 1: {
//	         updateMethodPanel(1);
//	         userPanel[0].setLocation(3, 3);
//	         westPanel.add(userPanel[0]);
//	      }
//	      }
//	      eastPanel.revalidate();
//	      eastPanel.repaint();
//	      westPanel.revalidate();
//	      westPanel.repaint();
//	   }
//
//	   private void updateMethodPanel(int i) {
//	      int index = i - 1;
//
//	      userPanel[index] = new JPanel(null);
//	      userPanel[index].setSize(123, 164);
//	      userPanel[index].setBackground(new Color(255, 230, 156));
//	      userPanel[index].setOpaque(true);
//
//	      userChar[index] = new JLabel(new ImageIcon(usersGame.get(index).get_gamecharImagePath()));
//	      userChar[index].setBounds(0, 0, 100, 100);
//	      StyleContext contextUser = new StyleContext();
//	      StyledDocument documentUser = new DefaultStyledDocument(contextUser);
//	      Style styleUser = contextUser.getStyle(StyleContext.DEFAULT_STYLE);
//	      StyleConstants.setAlignment(styleUser, StyleConstants.ALIGN_CENTER);
//	      userChat[index] = new JTextPane(documentUser);
//	      userChat[index].setBounds(0, 100, 123, 30);
//	      userChat[index].setFont(new Font(ProgressInfo.FONT, Font.BOLD, 15));
//	      userChat[index].setText("");
//	      userChat[index].setBorder(new LineBorder(Color.black, 2));
//	      userChat[index].setEditable(false);
//
//	      userNickname[index] = new JLabel();
//	      userNickname[index].setText(usersGame.get(index).get_nickName());
//	      userNickname[index].setBounds(5, 132, 115, 15);
//	      userNickname[index].setFont(new Font(ProgressInfo.FONT, Font.BOLD, 15));
//	      userScoreLabel[index] = new JLabel();
//	      userScoreLabel[index].setText("SCORE:");
//	      userScoreLabel[index].setBounds(3, 148, 40, 13);
//	      userScoreLabel[index].setFont(new Font(ProgressInfo.FONT, Font.PLAIN, 13));
//	      userScore[index] = new JLabel();
//	      userScore[index].setText(Integer.toString(usersGame.get(index).get_score()));
//	      userScore[index].setBounds(45, 148, 15, 13);
//	      userScore[index].setFont(new Font(ProgressInfo.FONT, Font.PLAIN, 13));
//	      userLevelLabel[index].setText("LEVEL:");
//	      userLevelLabel[index].setBounds(65, 148, 40, 13);
//	      userLevelLabel[index].setFont(new Font(ProgressInfo.FONT, Font.PLAIN, 13));
//	      userLevel[index] = new JLabel();
//	      userLevel[index].setText(Integer.toString(usersGame.get(index).get_level()));
//	      userLevel[index].setBounds(107, 148, 15, 13);
//	      userLevel[index].setFont(new Font(ProgressInfo.FONT, Font.PLAIN, 13));
//
//	      userPanel[index].add(userChat[index]);
//	      userPanel[index].add(userChar[index]);
//	      userPanel[index].add(userNickname[index]);
//	      userPanel[index].add(userScoreLabel[index]);
//	      userPanel[index].add(userScore[index]);
//	      userPanel[index].add(userLevelLabel[index]);
//	      userPanel[index].add(userLevel[index]);
//	   }
//
//	   /**
//	    * Update score of user who got correct answer
//	    * 
//	    * @param nickname
//	    *            of target user to update score
//	    */
//	   public void scoreUpdate(String nickName) {
//	      int score = 0;
//	      // Update the score of target user
//	      for (UserInfo ui : usersGame) {
//	         if (ui.get_nickName().equals(nickName)) {
//	            ui.inc_score();
//	            score = ui.get_score();
//	         }
//	      }
//
//	      // Update display of score for target user
//	      if (!(userNickname[3].getText().equals(""))) {
//	         if (userNickname[3].getText().equals(nickName))
//	            userScore[3].setText(String.valueOf(score));
//	      }
//	      if (!(userNickname[2].getText().equals(""))) {
//	         if (userNickname[2].getText().equals(nickName))
//	            userScore[2].setText(String.valueOf(score));
//	      }
//	      if (!(userNickname[1].getText().equals(""))) {
//	         if (userNickname[1].getText().equals(nickName))
//	            userScore[1].setText(String.valueOf(score));
//	      }
//	      if (!(userNickname[0].getText().equals(""))) {
//	         if (userNickname[0].getText().equals(nickName))
//	            userScore[0].setText(String.valueOf(score));
//	      }
//	   }
//
//	   /**
//	    * Invoke updatePanel function to redraw west&east panel
//	    * 
//	    * @param list
//	    *            of the users in this game
//	    */
//	   public void joinApproved(ArrayList<UserInfo> usersGame) {
//	      this.usersGame = usersGame;
//	      System.out.println("f : " + usersGame);
//	      updatePanel();
//	   }
//
//	   /**
//	    * Invoke updatePanel function to draw west&east panel at first
//	    * 
//	    * @param list
//	    *            of the users in this game
//	    */
//	   public void createApproved(ArrayList<UserInfo> usersGame) {
//	      this.usersGame = usersGame;
//	      updatePanel();
//	   }
//
//	   /**
//	    * Inform user that unable to start game because the user is not game master
//	    */
//	   public void startDeniedMaster() {
//	      JOptionPane.showMessageDialog(GamePanel.this.cymFrame.getContentPane(), "You are not the game master!");
//	   }
//
//	   /**
//	    * Inform user that unable to start game because there is not enough player
//	    */
//	   public void startDeniedNum() {
//	      JOptionPane.showMessageDialog(GamePanel.this.cymFrame.getContentPane(), "You need at least two players!");
//	   }
//
//	   /**
//	    * Start the game. If the client is questioner, set the answer panel with
//	    * the round answer For all client, set the timer with game time
//	    * 
//	    * @param answer
//	    *            of this round, nickname of questioner
//	    */
//	   public void gameStarted(String roundAnswer, String questioner) {
//	      System.out.println("<GamePanel_gameStarted> roundAnswer: " + roundAnswer);
//	      System.out.println("<GamePanel_gameStarted> questioner Nickname: " + questioner);
//	      if (GamePanel.this.cymFrame.get_myNickname().equals(questioner)) {
//	         isQuestioner = true;
//	         answer.setText(roundAnswer);
//	      } else {
//	         isQuestioner = false;
//	         answer.setText("ANSWER");
//	      }
//	      gameTime = ROUND_TIME;
//	      timer.setText(String.valueOf(gameTime));
//	      gameStarted = true;
//
//	      final JOptionPane optionPane = new JOptionPane("ROUND STARTS!", JOptionPane.INFORMATION_MESSAGE,
//	            JOptionPane.DEFAULT_OPTION, null, new Object[] {}, null);
//	      final JDialog dialog = new JDialog();
//	      dialog.setTitle("");
//	      dialog.setModal(true);
//	      dialog.setContentPane(optionPane);
//	      dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
//	      dialog.pack();
//	      ActionListener action = new ActionListener() {
//	         public void actionPerformed(ActionEvent e) {
//	            dialog.dispose();
//	         }
//	      };
//	      Timer timer = new Timer(1000, action);
//	      timer.setRepeats(false);
//	      timer.start();
//	      dialog.setVisible(true);
//	   }
//
//	   /**
//	    * Draw the canvas with point list, selected color
//	    * 
//	    * @param list
//	    *            of points drawn by questioner
//	    */
//	   public void drawBroadcasted(ArrayList<UserPoint> pList) {
//	      Graphics g = canvas.getGraphics();
//	      Graphics2D g2 = (Graphics2D) g;
//
//	      for (UserPoint p : pList) {
//	         System.out.println("<GamePanel> drawColor: " + drawColor);
//	         g2.setColor(drawColor);
//	         g2.setStroke(new BasicStroke(5));
//	         // g.setColor(drawColor);
//
//	         newPoint = p;
//
//	         if (newPoint == null) {
//	            newPoint = oldPoint;
//	         }
//	         if (oldPoint == null) {
//	            oldPoint = p;
//	         }
//
//	         // g.drawLine(p1.get_pointX(), p1.get_pointY(), p2.get_pointX(),
//	         // p2.get_pointY());
//
//	         System.out.println(newPoint.get_pointX() + ", " + newPoint.get_pointY());
//	         System.out.println(oldPoint.get_pointX() + ", " + oldPoint.get_pointY());
//	         g2.draw(new Line2D.Float(oldPoint.get_pointX(), oldPoint.get_pointY(), newPoint.get_pointX(),
//	               newPoint.get_pointY()));
//
//	         oldPoint = p;
//	      }
//	   }
//
//	   /** Clear the canvas */
//	   public void clearBroadcasted() {
//	      canvas.repaint();
//	   }
//
//	   /**
//	    * Questioner selected eraser. Set the color as white and set the eraser
//	    * thickness
//	    */
//	   public void eraserBroadcasted() {
//	      set_drawColor(6);
//	      set_drawThick(25);
//	   }
//
//	   /**
//	    * Questioner selected color. Set the color as selected
//	    * 
//	    * @param index
//	    *            of selected color
//	    */
//	   public void colorBroadcasted(int drawingColor) {
//	      set_drawColor(drawingColor);
//	      set_drawThick(10);
//	   }
//
//	   /**
//	    * Set border of the questioner with blue, set with black for others
//	    * 
//	    * @param nickname
//	    *            of questioner
//	    */
//	   public void quetionerBorder(String questioner) {
//	      int size = usersGame.size();
//	      switch (size) {
//	      case 4: {
//	         if (usersGame.get(size - 4).get_nickName().equals(questioner)) {
//	            userPanel[0].setBorder(new LineBorder(new Color(189, 215, 238), 4));
//	            userPanel[1].setBorder(new LineBorder(new Color(189, 215, 238), 4));
//	            userPanel[2].setBorder(new LineBorder(new Color(189, 215, 238), 4));
//	            userPanel[3].setBorder(new LineBorder(Color.black, 4));
//	         }
//	      }
//	      case 3: {
//	         if (usersGame.get(size - 3).get_nickName().equals(questioner)) {
//	            userPanel[0].setBorder(new LineBorder(new Color(189, 215, 238), 4));
//	            userPanel[1].setBorder(new LineBorder(new Color(189, 215, 238), 4));
//	            userPanel[2].setBorder(new LineBorder(Color.black, 4));
//	            userPanel[3].setBorder(new LineBorder(new Color(189, 215, 238), 4));
//	         }
//	      }
//	      case 2: {
//	         if (usersGame.get(size - 2).get_nickName().equals(questioner)) {
//	            userPanel[0].setBorder(new LineBorder(new Color(189, 215, 238), 4));
//	            userPanel[1].setBorder(new LineBorder(Color.black, 4));
//	            userPanel[2].setBorder(new LineBorder(new Color(189, 215, 238), 4));
//	            userPanel[3].setBorder(new LineBorder(new Color(189, 215, 238), 4));
//	         }
//	      }
//	      case 1: {
//	         if (usersGame.get(size - 1).get_nickName().equals(questioner)) {
//	            userPanel[0].setBorder(new LineBorder(Color.black, 4));
//	            userPanel[1].setBorder(new LineBorder(new Color(189, 215, 238), 4));
//	            userPanel[2].setBorder(new LineBorder(new Color(189, 215, 238), 4));
//	            userPanel[3].setBorder(new LineBorder(new Color(189, 215, 238), 4));
//	         }
//	      }
//	      }
//	   }
//
//	   /**
//	    * Update the chat fields for the players in game
//	    * 
//	    * @param nickname
//	    *            of the chat's owner, contents of chat
//	    */
//	   public void gameChatUpdate(String nickName, String chattingSentence) {
//	      if (!(userNickname[3].getText().equals(""))) {
//	         if (userNickname[3].getText().equals(nickName))
//	            userChat[3].setText(chattingSentence);
//	      }
//	      if (!(userNickname[2].getText().equals(""))) {
//	         if (userNickname[2].getText().equals(nickName))
//	            userChat[2].setText(chattingSentence);
//	      }
//	      if (!(userNickname[1].getText().equals(""))) {
//	         if (userNickname[1].getText().equals(nickName))
//	            userChat[1].setText(chattingSentence);
//	      }
//	      if (!(userNickname[0].getText().equals(""))) {
//	         if (userNickname[0].getText().equals(nickName))
//	            userChat[0].setText(chattingSentence);
//	      }
//	   }
//
//	   /**
//	    * Inform all the clients the answer and that the chat's owner got correct
//	    * 
//	    * @param nickname
//	    *            of the correct chat's owner, contents of answer
//	    */
//	   public void correctAnswer(String nickName, String answer) {
//	      System.out.println("<GamePanel> correctAnswer");
//	      System.out.println("nickName: " + nickName + ", answer: " + answer);
//	      
//	      gameStarted = false;
//	      String message = "";
//	      if (!(userNickname[3].getText().equals(""))) {
//	         if (userNickname[3].equals(nickName))
//	            message = new String(userNickname[3].getText() + " got correct!\n" + "ANSWER: " + answer);
//	      }
//	      if (!(userNickname[2].getText().equals(""))) {
//	         if (userNickname[2].equals(nickName))
//	            message = new String(userNickname[2].getText() + " got correct!\n" + "ANSWER: " + answer);
//	      }
//	      if (!(userNickname[1].getText().equals(""))) {
//	         if (userNickname[1].equals(nickName))
//	            message = new String(userNickname[1].getText() + " got correct!\n" + "ANSWER: " + answer);
//	      }
//	      if (!(userNickname[0].getText().equals(""))) {
//	         if (userNickname[0].getText().equals(nickName))
//	            message = new String(userNickname[0].getText() + " got correct!\n" + "ANSWER: " + answer);
//	      }
//	      final JOptionPane optionPane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE,
//	            JOptionPane.DEFAULT_OPTION, null, new Object[] {}, null);
//	      final JDialog dialog = new JDialog();
//	      dialog.setTitle("");
//	      dialog.setModal(true);
//	      dialog.setContentPane(optionPane);
//	      dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
//	      dialog.pack();
//	      ActionListener action = new ActionListener() {
//	         public void actionPerformed(ActionEvent e) {
//	            dialog.dispose();
//	            if (isQuestioner) {
//	               // Questioner sends the server to notify that the round
//	               // ended
//	               ProgressInfo pi = new ProgressInfo();
//	               pi.set_status(ProgressInfo.TIMER_EXPIRE);
//	               GamePanel.this.cymFrame.sendProtocol(pi);
//	            }
//	         }
//	      };
//	      Timer timer = new Timer(2000, action);
//	      timer.setRepeats(false);
//	      timer.start();
//	      dialog.setVisible(true);
//	   }
//
//	   /**
//	    * Redraw all the panel as waiting state (not started)
//	    * 
//	    * @param message
//	    *            noticing that the game ended
//	    */
//	   public void roundTerminated(String message) {
//	      gameStarted = false;
//	      final JOptionPane optionPane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE,
//	            JOptionPane.DEFAULT_OPTION, null, new Object[] {}, null);
//	      final JDialog dialog = new JDialog();
//	      dialog.setTitle("");
//	      dialog.setModal(true);
//	      dialog.setContentPane(optionPane);
//	      dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
//	      dialog.pack();
//	      ActionListener action = new ActionListener() {
//	         public void actionPerformed(ActionEvent e) {
//	            dialog.dispose();
//	            updatePanel();
//	         }
//	      };
//	      Timer timer = new Timer(4000, action);
//	      timer.setRepeats(false);
//	      timer.start();
//	      dialog.setVisible(true);
//	   }
//
//	   /**
//	    * When the server notifies that 1 second elapsed If game is playing,
//	    * decrement the in-game timer If in-game timer becomes zero, notice the
//	    * server that the round ended
//	    */
//	   public void timerBroadcasted() {
//	      if (gameStarted) {
//	         gameTime--;
//	         timer.setText(String.valueOf(gameTime));
//	         if (gameTime == 0) {
//	            gameStarted = false;
//	            if (isQuestioner) {
//	               ProgressInfo pi = new ProgressInfo();
//	               pi.set_status(ProgressInfo.TIMER_EXPIRE);
//	               GamePanel.this.cymFrame.sendProtocol(pi);
//	            }
//	         }
//	      }
//	   }
//
//	   /* Get methods */
//	   public Color get_drawColor() {
//	      return drawColor;
//	   }
//
//	   public int get_drawThick() {
//	      return drawThick;
//	   }
//
//	   /* Set methods */
//	   public void set_drawThick(int item) {
//	      drawThick = item;
//	   }
//
//	   public void set_drawColor(int option) {
//	      switch (option) {
//	      case 0: {
//	         drawColor = Color.black;
//	         break;
//	      }
//	      case 1: {
//	         drawColor = Color.red;
//	         break;
//	      }
//	      case 2: {
//	         drawColor = Color.yellow;
//	         break;
//	      }
//	      case 3: {
//	         drawColor = Color.green;
//	         break;
//	      }
//	      case 4: {
//	         drawColor = Color.blue;
//	         break;
//	      }
//	      case 5: {
//	         drawColor = new Color(128, 0, 128);
//	         break;
//	      }
//	      case 6: {
//	         drawColor = Color.white;
//	         break;
//	      }
//	      }
//	   }
//
//	
	
	
	/** 서버로부터 Protocol을 수신받는 오버라이딩 메서드 */
	@Override
	public void receiveProtocol(Protocol pt){
		
	}

}
