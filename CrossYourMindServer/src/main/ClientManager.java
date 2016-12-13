package main;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.Timer;

import info.RoomInfo;
import info.UserInfo;
import network.Protocol;

public class ClientManager extends Thread {
	private static enum NETSTATE {
		Home, Lobby, Room
	};

	private String receiveFilePath = "C:/Program Files/CrossYourMindServer";
	private static final int ROUND_NUM = 4; // 총 진행 라운드
	private static final ArrayList<String> wordList = new ArrayList<String>();
	
	// for connection
	private Server server;
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;
	private FileInputStream fis;
	private FileOutputStream fos;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private Socket user_socket;

	// for
	private UserInfo userInfo; //나의 정보
	private RoomInfo room = null; //내가 속해있는 방의 정보
	private Vector<ClientManager> users; //같은 방에 있는 사용자의 정보
	private int playerNo; //접속자수
	private int turn = 0; //라운드를 계속 돌도록하는 int값

	// for
	private NETSTATE netState = NETSTATE.Home;
	private Object readObject;
	

	/** ClientManager Construction */
	public ClientManager(Socket soc, Vector<ClientManager> vc, int playerNo, Server server) { // 생성자메소드
		// 매개변수로 넘어온 자료 저장
		this.server = server;
		this.user_socket = soc;
		this.users = vc;
		this.playerNo = playerNo;

		this.userInfo = new UserInfo();
		
		//게임을 실행하는 데 필요한 timerBroadcast
		ActionListener action = new ActionListener() {
	         public void actionPerformed(ActionEvent e) {
	            timerBroadcast();
	         }
	      };
	      try {
	         Timer gametimer = new Timer(2000, action);
	         gametimer.setRepeats(true);
	         gametimer.start();
	      } catch (Exception e) {
	         e.printStackTrace();
	      }

		UserNetwork();
		initWordList();
	}

	/** 모든 클라이언트에게 1초가 지났음을 알리는 메소드 */
	public void timerBroadcast() {
		Protocol pt = new Protocol();
		pt.setStatus(Protocol.GAME_TIMER_BROADCAST);

//		for (ClientManager users : server.users) {
//			try {
//				users.sendProtocol(pt);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
		for (ClientManager users : users) {
			try {
				users.sendProtocol(pt);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/** 스트림 생성 메소드 */
	public void UserNetwork() {
		try {
			is = user_socket.getInputStream();
			dis = new DataInputStream(is);
			os = user_socket.getOutputStream();
			dos = new DataOutputStream(os);
			ois = new ObjectInputStream(is);

			server.textArea.append("Player NO. " + playerNo + "접속\n");
			server.textArea.setCaretPosition(server.textArea.getText().length());
		} catch (Exception e) {
			server.textArea.append("스트림 셋팅 에러\n");
			server.textArea.setCaretPosition(server.textArea.getText().length());
		}
	}

	/** 단어 리스트 초기화 메소드 */
	public void initWordList(){
		wordList.add("엑소");
		wordList.add("귓속말");
		wordList.add("세차장");
		wordList.add("포크레인");
	}
	
	/** 단어 리스트에서 랜덤으로 선택하는 메소드 */
	public String getRandomWord(){
		Random random = new Random();
		return wordList.get(random.nextInt(wordList.size()));
	}
	
	/** Protocol 클라이언트로 Protocol을 송신하는 메소드 */
	public void sendProtocol(Protocol pt) {
		try {
			oos = new ObjectOutputStream(os);

			oos.writeObject(pt);
			oos.flush();
		} catch (IOException e) {
			server.textArea.append("메시지 송신 에러 발생\n");
			server.textArea.setCaretPosition(server.textArea.getText().length());
		}
	}

	/** IMAGE 클라이언트로 원본과 리사이징 이미지를 송신하는 메소드 */
	public void sendImage(String fineName) {
		Image originalImage = Toolkit.getDefaultToolkit().getImage(receiveFilePath + "/" + fineName); // ImageIcon
		ImageIcon originalImageIcon = new ImageIcon(originalImage);
		Image resizingImage = originalImage.getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH); // resize
		ImageIcon resizingImageIcon = new ImageIcon(resizingImage);

		try {
			oos = new ObjectOutputStream(os);
			oos.writeObject(originalImageIcon);
			System.out.println("OriginalImageIcon 전송!");
			oos.writeObject(resizingImageIcon);
			System.out.println("ResizingImageIcon 전송!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** FILE 클라이언트로부터 파일을 수신하는 메소드 */
	public void receiveFile(String fileName, long fileSize) {
		int byteSize = 10000;
		byte[] ReceiveByteArrayToFile = new byte[byteSize];

		String saveFolder = receiveFilePath; // 경로
		File targetDir = new File(saveFolder);

		if (!targetDir.exists()) { // 디렉토리 없으면 생성.
			targetDir.mkdirs();
		}

		try {
			fos = new FileOutputStream(receiveFilePath + "/" + fileName);
			int n = 0;
			int count = 0;
			while (count < fileSize) {
				n = dis.read(ReceiveByteArrayToFile);
				fos.write(ReceiveByteArrayToFile, 0, n);
				count += n;
			}
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** FILE 클라이언트로 파일을 송신하는 메소드 */
	public void sendFile(File sendFile, long fileSize) {
		int byteSize = 10000;
		byte[] sendFileTobyteArray = new byte[byteSize]; // 바이트 배열 생성
		try {
			fis = new FileInputStream(sendFile); // 파일에서 읽어오기 위한 스트림 생성
			int n = 0;
			int count = 0;
			while (count < fileSize) {
				n = fis.read(sendFileTobyteArray);
				dos.write(sendFileTobyteArray, 0, n);
				count += n;
			}
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** Protocol을 수신하여 처리하는 메소드 -> Thread */
	@Override
	public void run() {
		while (true) {
			try {
				readObject = ois.readObject();
				Protocol pt = (Protocol) readObject;

				server.textArea.append("프로토콜 수신 번호 : " + pt.getStatus() + "\n");
				switch (pt.getStatus()) {

				case Protocol.HOME_LOGIN:
					System.out.println("<ClientManager> Protocol.LOGIN");
					pt.setStatus(Protocol.HOME_SUCCESSLOGIN);
					pt.setRoomSize(server.rooms.size()); // 만들어져 있는 방의 개수 세팅
					sendProtocol(pt);
					System.out.println("<ClientManager> send SUCCESSLOGIN");
					netState = NETSTATE.Lobby; // 로비로 이동

					userInfo = pt.getUserInfo(); // 프로토콜로 받은 사용자 정보 세팅
					updateGameListInLobby(); // GameList업데이트
					updateUserListInLobby(); // UserList업데이트
					break;
				case Protocol.LOBBY_CHAT_MSG:
					System.out.println("<ClientManager> Protocol.MSG");
					broadCastProtocol(pt);
					break;
				case Protocol.LOBBY_CHAT_IMAGE:
					System.out.println("<ClientManager> Protocol.IMAGE");
					receiveFile(pt.getSendFileName(), pt.getSendFileSize());
					broadCastProtocol(pt);
					broadCastImage(pt.getSendFileName());
					break;
				case Protocol.LOBBY_CHAT_FILE:
					System.out.println("<ClientManager> Protocol.FILE");
					receiveFile(pt.getSendFileName(), pt.getSendFileSize());
					broadCastProtocol(pt);
					break;
				case Protocol.LOBBY_CHAT_FILESAVE:
					System.out.println("<ClientManager> Protocol.FILESAVE");
					File sendFile = new File(receiveFilePath + "/" + pt.getSendFileName()); // 파일
																							// 생성
					long fileSize = (int) sendFile.length(); // 파일 크기 받아오기
					pt.setStatus(Protocol.LOBBY_CHAT_FILESEND);
					pt.setSendFileSize(fileSize);
					sendProtocol(pt);
					System.out.println("<ClientManager> send FILESEND");

					sendFile(sendFile, fileSize);
					break;
				case Protocol.LOBBY_CREATE_ROOM:
					System.out.println("<ClientManager> Protocol.CREATE_ROOM");
					String roomName = pt.getRoomName(); // 사용자가 입력한 방이름

					if (server.checkDuplicateRoomName(roomName)) {
						pt.setStatus(Protocol.LOBBY_CREATE_ROOM_FAIL);
						sendProtocol(pt);
						System.out.println("<ClientManager> send CREATE_ROOM_DENIED");
					} else {
						netState = NETSTATE.Room;
						room = new RoomInfo(roomName); // 새로운 방 생성
						userInfo.setIsMaster(true); // 주인장으로 표시

						server.addRoom(room); // 방을 벡터에 추가
						// roomName = null?
						server.addUserToRoom(this, roomName); // Client를 방에 추가

						pt.setStatus(Protocol.LOBBY_CREATE_ROOM_SUCCESS);
						sendProtocol(pt);
						System.out.println("<ClientManager> send CREATE_ROOM_SUCCESS");

						updateGameListInLobby(); // GameList업데이트
						updateUserListInLobby(); // UserList업데이트
					}
					break;
				case Protocol.LOBBY_LOGOUT:
					System.out.println("<ClientManager> Protocol.LOBBY_LOGOUT");
					netState = NETSTATE.Home;
					
					//처리!!
					break;
				case Protocol.GAME_IN:
					System.out.println("<ClientManager> Protocol.GAME_IN");
					pt.setStatus(Protocol.GAME_CREATED);
					// 방에 있는 Client의 UserInfo Vector
					pt.setUsersInRoom(room.getUsersInfo());
					sendProtocol(pt);
					System.out.println("<ClientManager> send GAME_CREATED");

					break;
				case Protocol.LOBBY_JOIN_ROOM:
					System.out.println("<ClientManager> Protocol.LOBBY_JOIN_ROOM");
					if (server.checkFullRoom(pt.getRoomName())) {
						pt.setStatus(Protocol.LOBBY_JOIN_ROOM_FAIL);
						sendProtocol(pt);
						System.out.println("<ClientManager> send LOBBY_JOIN_ROOM_FAIL");
					} else {
						netState = NETSTATE.Room;
						userInfo.setIsMaster(false); // 주인장 X

						// roomName = null?
						// Client를 방에 추가
						server.addUserToRoom(this, pt.getRoomName());

						pt.setStatus(Protocol.LOBBY_JOIN_ROOM_SUCCESS);
						sendProtocol(pt);
						System.out.println("<ClientManager> send LOBBY_JOIN_ROOM_SUCCESS");

						updateGameListInLobby(); // GameList업데이트
						updateUserListInLobby(); // UserList업데이트
					}
					break;
				case Protocol.GAME_JOIN_IN:
					System.out.println("<ClientManager> Protocol.GAME_JOIN_IN");

					// 해당하는 룸에 있는 참가자들에게 새로운 참여자가 왔음을 알린다.
					for (ClientManager userInRoom : room.getUsersClientManager()) {
						try {
							pt.setStatus(Protocol.GAME_JOIN_PARTIPANT);
							// 방에 있는 Client의 UserInfo Vector
							pt.setUsersInRoom(room.getUsersInfo());
							userInRoom.sendProtocol(pt);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					System.out.println("<ClientManager> send GAME_JOIN_PARTIPANT");

					updateUserListInLobby(); // UserList업데이트
					break;
				case Protocol.GAME_CHAT_MSG:
					System.out.println("<ClientManager> Protocol.GAME_CHAT_MSG");
					String answeredNickName = userInfo.getMyNickname();

					// round answer과 일치하면
					if (room.getRoundAnswer().equals(pt.getChatSentence())) {
						// 해당하는 룸에 있는 참가자들에게 답을 맞춘것을 알린다.
						for (ClientManager userInRoom : room.getUsersClientManager()) {
							if (userInRoom.getUserInfo().getMyNickname().equals(answeredNickName)) {
								// 정답자의 점수 ++
								userInRoom.getUserInfo().increaseScore();
							}
							pt.setStatus(Protocol.GAME_CORRECT_ANSWER);
							pt.setUserInfo(userInfo);
							userInRoom.sendProtocol(pt);
						}
					}
					// round answer과 일치하지 않으면
					else {
						// 해당하는 룸에 있는 참가자들에게 채팅 내용을 알린다.
						for (ClientManager userInRoom : room.getUsersClientManager()) {
							pt.setStatus(Protocol.GAME_CHAT_UPDATE);
							pt.setUserInfo(userInfo);
							userInRoom.sendProtocol(pt);
						}
					}
					break;
				case Protocol.GAME_START:
					System.out.println("<ClientManager> Protocol.GAME_START");

					// 게임 시작을 누른 사람이 주인장이면
					if (userInfo.getIsMaster()) {
						// 최소 게임 인원 = 2
						if (room.getUsersCount() >= 2) {
							gameStart(pt); // 게임시작성공 프로토콜 전송
						} else {
							pt.setStatus(Protocol.GAME_START_FAIL_LACK_USER);
						}
					}
					// 게임 시작 권한(주인장)이 없음
					else {
						pt.setStatus(Protocol.GAME_START_FAIL_NOT_MASTER);
					}
					sendProtocol(pt);
					break;
				case Protocol.GAME_LOGOUT:
					System.out.println("<ClientManager> Protocol.GAME_LOGOUT");
					netState = NETSTATE.Lobby;
					//처리!!!
					
					break;
				case Protocol.GAME_DRAW:
					System.out.println("<ClientManager> Protocol.GAME_DRAW");
					// 해당하는 룸에 있는 참가자들에게 Point를 보낸다.
					for (ClientManager userInRoom : room.getUsersClientManager()) {
						pt.setStatus(Protocol.GAME_DRAW_BROADCAST);
						userInRoom.sendProtocol(pt);
					}
					break;
				case Protocol.GAME_DRAW_ALLCLEAR:
					System.out.println("<ClientManager> Protocol.GAME_DRAW_ALLCLEAR");
					// 해당하는 룸에 있는 참가자들에게 프로토콜을 보낸다.
					for (ClientManager userInRoom : room.getUsersClientManager()) {
						pt.setStatus(Protocol.GAME_DRAW_ALLCLEAR_BROADCAST);
						userInRoom.sendProtocol(pt);
					}
					break;
				case Protocol.GAME_DRAW_ERASER:
					System.out.println("<ClientManager> Protocol.GAME_DRAW_ERASER");
					// 해당하는 룸에 있는 참가자들에게 프로토콜을 보낸다.
					for (ClientManager userInRoom : room.getUsersClientManager()) {
						pt.setStatus(Protocol.GAME_DRAW_ERASER_BROADCAST);
						userInRoom.sendProtocol(pt);
					}
					break;
				case Protocol.GAME_DRAW_SELECT_COLOR:
					System.out.println("<ClientManager> Protocol.GAME_DRAW_SELECT_COLOR");
					// 해당하는 룸에 있는 참가자들에게 선택한 색에대한 프로토콜을 보낸다.
					for (ClientManager userInRoom : room.getUsersClientManager()) {
						pt.setStatus(Protocol.GAME_DRAW_SELECT_COLOR_BROADCAST);
						userInRoom.sendProtocol(pt);
					}
					break;
				case Protocol.GAME_DRAW_TIMER_EXPIRE:
					System.out.println("<ClientManager> Protocol.GAME_DRAW_TIMER_EXPIRE");
					// 해당하는 룸에 있는 참가자들에게 게임이 끝났다는 프로토콜을 보낸다.
					timerExpireBroadcast(userInfo);
					break;
					
				case Protocol.EXIT:
					System.out.println("<ClientManager> Protocol.EXIT");
					//처리!!!
					
					break;
				} // switch문 끝
			} // try문 끝
			catch (IOException e) {
				try {
					if (netState == NETSTATE.Room) {
						// userExitInRoom();
					} else {
						dos.close();
						dis.close();
						user_socket.close();
						users.removeElement(this); // 에러가난 현재 객체를 벡터에서 지운다
						server.textArea.append("현재 벡터에 담겨진 사용자 수 : " + users.size() + "\n");
						server.textArea.append("사용자 접속 끊어짐 자원 반납\n");
						server.textArea.setCaretPosition(server.textArea.getText().length());
						server.playerCnt--;
					}
					return;
				} catch (Exception ee) {
				} // catch문 끝
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				// System.out.println(String.valueOf(readObject.toString()));
			}
		}
	}

	/** ClientManager의 Vector을 update하는 메소드 */
	public void updateAllClientManager(Vector<ClientManager> users) {
		this.users = users;
	}

	/** Lobby의 user list를 update하는 메소드 */
	public void updateUserListInLobby() {
		for (int i = 0; i < users.size(); i++) {
			ClientManager imsi = users.elementAt(i);

			// 프로토콜 전송
			Protocol pt = new Protocol();
			pt.setStatus(Protocol.LOBBY_UPDATE_USER_LIST);
			pt.setUserList(setUserListNameInLobby());

			// in Lobby 체크?
			imsi.sendProtocol(pt);
		}
	}

	/** Lobby의 user list의 이름을 setting하는 메소드 */
	public Vector<String> setUserListNameInLobby() {
		int listSize = server.users.size();
		System.out.println("listSize: " + listSize);

		Vector<String> userListName = new Vector<String>();

		for (int i = 0; i < listSize; i++) {
			// in Lobby, ""이 아닌 닉네임만
			if (server.users.get(i).getNetState() == NETSTATE.Lobby
					&& !(server.users.get(i).getNickName()).equals("")) {
				userListName.add(server.users.get(i).getNickName());
			}
		}
		return userListName;
	}

	/** Lobby의 game list를 update하는 메소드 */
	public void updateGameListInLobby() {
		for (int i = 0; i < users.size(); i++) {
			ClientManager imsi = users.elementAt(i);

			// 프로토콜 전송
			Protocol pt = new Protocol();
			pt.setStatus(Protocol.LOBBY_UPDATE_GAME_LIST);
			pt.setGameList(setGameListNameInLobby());

			// in Lobby 체크?
			imsi.sendProtocol(pt);
		}
	}

	/** Lobby의 game list의 이름을 setting하는 메소드 */
	public Vector<String> setGameListNameInLobby() {
		int listSize = server.rooms.size();
		Vector<String> gameListName = new Vector<String>();

		for (int i = 0; i < listSize; i++) {
			gameListName.add(server.rooms.get(i).getRoomName());
		}
		return gameListName;
	}

	/** Protocol broadCasting */
	public void broadCastProtocol(Protocol pt) {
		for (int i = 0; i < users.size(); i++) {
			ClientManager imsi = users.elementAt(i);
			// Lobby에 있는 client에게만 전송
			if (imsi.getNetState() == NETSTATE.Lobby) {
				imsi.sendProtocol(pt);
			}
		}
	}

	/** IMAGE 이미지 broadCasting */
	public void broadCastImage(String str) {
		for (int i = 0; i < users.size(); i++) {
			ClientManager imsi = users.elementAt(i);
			imsi.sendImage(str);
		}
	}

	/** 게임을 시작하는 메소드 */
	public void gameStart(Protocol pt) {
		System.out.println("<ClientManager> call gameStart");

		room.setRoomStatus(RoomInfo.ROOM_PLAYING);
		room.setRoundNum(RoomInfo.ROOM_ROUND_NUM);

		// 해당하는 룸에 있는 참가자들에게 게임 시작을 알린다.
		for (ClientManager userInRoom : room.getUsersClientManager()) {
			UserInfo userInfoInRoom = userInRoom.getUserInfo();
			try {
				// // 방에 있는 Client의 UserInfo Vector
				// pt.setUsersInRoom(room.getUsersInfo());

				// 만약 주인장이면
				if (userInfoInRoom.getIsMaster()) {
					userInfoInRoom.setStatus(UserInfo.QUESTRIONER_INROOM);
					pt.setUserInfo(userInfoInRoom); //질문자 세팅
					pt.setStatus(Protocol.GAME_START_SUCCESS_QUESTIONER);

					// 게임 정보 설정
					room.setRoundNum(ROUND_NUM - 1); // 시작 시 round--
					room.setRoundAnswer(getRandomWord()); // 정답단어 랜덤 세팅
					pt.setRoundAnswer(room.getRoundAnswer()); // 프로토콜에 정답저장
				}
				// 주인장이 아니면
				else {
					userInfoInRoom.setStatus(UserInfo.ANSWERER_INROOM);
					pt.getUserInfo().setMyNickname(""); //질문자 이름을 세팅
					pt.setStatus(Protocol.GAME_START_SUCCESS_ANSWER);
				}
				// 프로토콜 전송
				//pt.setStatus(Protocol.GAME_START_SUCCESS);
				userInRoom.sendProtocol(pt);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/** 사용자가 방을 나갔을 때 처리하는 메소드 */
	// void userExitInRoom() {
	// try {
	// dos.close();
	// dis.close();
	// user_socket.close();
	// if (users.size() == 1) {
	// if (server.users.size() != 0) {
	// server.users.get(0).broadCastMsg("/RMROOM " + server.rooms.indexOf(room)
	// + 1);
	// }
	// server.rooms.removeElement(room);
	// }
	// users.removeElement(this); // 에러가난 현재 객체를 벡터에서 지운다
	// if(users.size()!=0) {
	// users.get(0).broadCastMsg("/RMROOM " + playerNo);
	// }
	// server.textArea.append("현재 벡터에 담겨진 사용자 수 : " + users.size() + "\n");
	// server.textArea.append("사용자 접속 끊어짐 자원 반납\n");
	// server.textArea.setCaretPosition(server.textArea.getText().length());
	// server.playerCnt--;
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	
	/** 해당하는 룸에 있는 참가자들에게 라운드가 끝났음을 알리고 새로운 질문자와 정답을 설정하는 메소드 */
	public void timerExpireBroadcast(UserInfo userInfo) {
		UserInfo recentQuestioner = userInfo;
		UserInfo nextQuestioner;

		while (true) {
			// 같은 방에 있는 접속자 중에 다음 턴을 정한다.
			UserInfo selected = users.get(turn).getUserInfo();

			// 현재 질문자와 선택된 새로운 질문자가 같지 않으면 그대로 세팅
			if (!(selected.getMyNickname().equals(recentQuestioner.getMyNickname()))) {
				nextQuestioner = selected;
				break;
			}
			turn++;

			if (turn == users.size()) {
				turn %= users.size();
			}
		}

		// roundNum > 0: 방의 라운드가 0보다 크면 다음 라운드 진행
		if (room.getRoundNum() > 0) {
			room.setRoundNum(room.getRoundNum() - 1);

			System.out.println("roundNum : " + room.getRoundNum() + "in timer expired");

			for (ClientManager clientManager : room.getUsersClientManager()) {
				Protocol pt = new Protocol();
				pt.setUserInfo(nextQuestioner);

				if (clientManager.getUserInfo().getMyNickname().equals(nextQuestioner.getMyNickname())) {
					System.out.println("IN if");
					clientManager.getUserInfo().setStatus(UserInfo.QUESTRIONER_INROOM);

					pt.setStatus(Protocol.GAME_START_SUCCESS_QUESTIONER);
					room.setRoundAnswer(getRandomWord()); // 새로운 정답단어 세팅
					pt.setRoundAnswer(room.getRoundAnswer()); // 정답단어 프로토콜에 추가
				} else {
					System.out.println("IN else");
					clientManager.getUserInfo().setStatus(UserInfo.ANSWERER_INROOM);
					pt.setStatus(Protocol.GAME_START_SUCCESS_ANSWER);
				}
				//프로토콜 전송
				//pt.setStatus(Protocol.GAME_START_SUCCESS);
				clientManager.sendProtocol(pt);
			}
		}
		// roundNum <= 0
		else {
			Protocol pt = new Protocol();
			pt.setStatus(Protocol.GAME_ROUND_TERMINATE);
			for (ClientManager clientManager : room.getUsersClientManager()) {
				clientManager.sendProtocol(pt);
				clientManager.getUserInfo().setMyScore(0);
			}
			room.setRoomStatus(RoomInfo.ROOM_WAITNG);
		}
	}


	/* getter */
	NETSTATE getNetState() {
		return this.netState;
	}

	public RoomInfo getRoom() {
		return this.room;
	}

	public String getNickName() {
		return userInfo.getMyNickname();
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}

	/* setter */
	public void setNetState(NETSTATE netState) {
		this.netState = netState;
	}

	public void setRoom(RoomInfo room) {
		this.room = room;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}
}