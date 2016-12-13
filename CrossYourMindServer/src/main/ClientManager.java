package main;

import java.awt.Image;
import java.awt.Toolkit;
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
import java.util.Vector;

import javax.swing.ImageIcon;

import info.RoomInfo;
import info.UserInfo;
import network.Protocol;

public class ClientManager extends Thread {
	private static enum NETSTATE {
		Home, Lobby, Room
	};

	private String receiveFilePath = "C:/Program Files/CrossYourMindServer";

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
	private RoomInfo room = null;
	private UserInfo userInfo;
	private Vector<ClientManager> users;
	private int playerNo;

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

		UserNetwork();
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

				// Protocol ptSendToClient = new Protocol();

				server.textArea.append("프로토콜 수신 번호 : " + pt.getStatus() + "\n");
				switch (pt.getStatus()) {

				case Protocol.HOME_LOGIN:
					System.out.println("<ClientManager> Protocol.LOGIN");
					pt.setStatus(Protocol.HOME_SUCCESSLOGIN);
					pt.setRoomSize(server.rooms.size()); // 만들어져 있는 방의 개수 세팅
					sendProtocol(pt);
					System.out.println("<ClientManager> send SUCCESSLOGIN");
					netState = NETSTATE.Lobby; // 로비로 이동
					userInfo.setMyNickname(pt.getUserInfo().getMyNickname());

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
				// else if (splitMsg[0].equals("/LOGOUT")) {
				// System.out.println("로그아웃!");
				// netState = NETSTATE.Home;
				// }
				// break;
				//
				// case Room:
				// break;
				// }
				case Protocol.LOBBY_CREATE_ROOM:
					System.out.println("<ClientManager> Protocol.CREATE_ROOM");
					String roomName = pt.getRoomName(); // 사용자가 입력한 방이름

					if (server.checkDuplicateRoomName(roomName)) {
						pt.setStatus(Protocol.LOBBY_CREATE_ROOM_FAIL);
						sendProtocol(pt);
						System.out.println("<ClientManager> send CREATE_ROOM_DENIED");
					} else {
						updateGameListInLobby(); // GameList업데이트

						netState = NETSTATE.Room;
						room = new RoomInfo(roomName); // 새로운 방 생성
						userInfo.setIsMaster(true); // 주인장으로 표시

						server.addRoom(room); // 방을 벡터에 추가
						server.addUserToRoom(this, roomName); // Client를 방에 추가

						pt.setStatus(Protocol.LOBBY_CREATE_ROOM_SUCCESS);
						pt.setUsersInRoom(room.getUsersInfo()); // 방에 있는 Client의
																// UserInfo
																// Vector
						sendProtocol(pt);
						System.out.println("<ClientManager> send CREATE_ROOM_SUCCESS");

						updateUserListInLobby(); // UserList업데이트
					}
				}
			} catch (IOException e) {
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
	void broadCastProtocol(Protocol pt) {
		for (int i = 0; i < users.size(); i++) {
			ClientManager imsi = users.elementAt(i);
			// Lobby에 있는 client에게만 전송
			if (imsi.getNetState() == NETSTATE.Lobby) {
				imsi.sendProtocol(pt);
			}
		}
	}

	/** IMAGE 이미지 broadCasting */
	void broadCastImage(String str) {
		for (int i = 0; i < users.size(); i++) {
			ClientManager imsi = users.elementAt(i);
			imsi.sendImage(str);
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