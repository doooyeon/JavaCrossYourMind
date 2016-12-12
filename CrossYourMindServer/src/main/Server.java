package main;
// Java Chatting Server

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import info.RoomInfo;

public class Server extends JFrame {
	private static final int PORT = 30000;
	
	// for GUI
	private JPanel contentPane;
	private JButton startBtn; // 서버를 실행시킨 버튼
	JTextArea textArea; // 클라이언트 및 서버 메시지 출력

	// for connection
	private ServerSocket socket; // 서버소켓
	private Socket soc; // 연결소켓
	
	// for manage users, rooms -> default
	int playerCnt = 0;
	Vector<RoomInfo> rooms  = new Vector<RoomInfo>(); // 생성된 방들을 저장할 벡터
	Vector<ClientManager> users = new Vector<ClientManager>(); // 접속한 사용자들을 저장할 벡터

	/** SERVR construction */
	public Server() {
		init();
	}

	/** GUI를 구성하는 메소드 */
	private void init() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 280, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane js = new JScrollPane();

		textArea = new JTextArea();
		textArea.setColumns(20);
		textArea.setRows(5);
		js.setBounds(0, 0, 264, 320);
		contentPane.add(js);
		js.setViewportView(textArea);

		startBtn = new JButton("서버 실행");
		Myaction action = new Myaction();
		startBtn.addActionListener(action); // 내부클래스로 액션 리스너를 상속받은 클래스로
		startBtn.setBounds(0, 325, 264, 37);
		contentPane.add(startBtn);
		textArea.setEditable(false); // textArea를 사용자가 수정 못하게끔 막는다.
	}
	
	/** SERVER socket을 여는 액션 이벤트 처리하는 내부 클래스 */
	class Myaction implements ActionListener { 
		@Override
		public void actionPerformed(ActionEvent e) {
			// 액션 이벤트가 sendBtn일때 또는 textField 에세 Enter key 치면
			if (e.getSource() == startBtn)
				server_start();
		}
	}
		
	/** 서버가 소켓을 여는 메소드 */
	private void server_start() {
		try {
			socket = new ServerSocket(PORT); // 서버가 포트 여는부분
			startBtn.setText("서버실행중");
			startBtn.setEnabled(false); // 서버를 더이상 실행시키지 못 하게 막는다

			if (socket != null) { // socket 이 정상적으로 열렸을때
				Connection();
			}
		} catch (IOException e) {
			textArea.append("소켓이 이미 사용중입니다...\n");
		}
	}

	/** 사용자 접속을 받도록하는 메소드 */
	private void Connection() {
		Thread th = new Thread(new Runnable() { // 사용자 접속을 받을 스레드
			@Override
			public void run() {
				while (true) { // 사용자 접속을 계속해서 받기 위해 while문
					try {
						textArea.append("사용자 접속 대기중...\n");
						soc = socket.accept(); // accept가 일어나기 전까지는 무한 대기중
						playerCnt++; //접속자 수를 증가시킨다.
						
						textArea.append("사용자 접속!!\n");
						ClientManager user = new ClientManager(soc, users, playerCnt, Server.this);
						users.add(user); // 해당 벡터에 사용자 객체를 추가
						user.start(); // 만든 객체의 스레드 실행
					} catch (IOException e) {
						textArea.append("!!!! accept 에러 발생... !!!!\n");
					}
				}
			}
		});
		th.start();
	}
	
	/** Room 생성한 방을 벡터에 추가하는 메소드 */
	void addRoom(RoomInfo room) {
		this.rooms.add(room);
	}
	
	/** Room Client를 방에 추가하는 메소드 */
	void addUserToRoom(ClientManager clientManager, int roomIdx) {	
		rooms.get(roomIdx).addUser(clientManager);
	}

	public static void main(String[] args) {
		Server frame = new Server();
		frame.setVisible(true);
	}
}
