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
	private static final int ROUND_NUM = 4; // �� ���� ����
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
	private UserInfo userInfo; //���� ����
	private RoomInfo room = null; //���� �����ִ� ���� ����
	private Vector<ClientManager> users; //���� �濡 �ִ� ������� ����
	private int playerNo; //�����ڼ�
	private int turn = 0; //���带 ��� �������ϴ� int��

	// for
	private NETSTATE netState = NETSTATE.Home;
	private Object readObject;
	

	/** ClientManager Construction */
	public ClientManager(Socket soc, Vector<ClientManager> vc, int playerNo, Server server) { // �����ڸ޼ҵ�
		// �Ű������� �Ѿ�� �ڷ� ����
		this.server = server;
		this.user_socket = soc;
		this.users = vc;
		this.playerNo = playerNo;

		this.userInfo = new UserInfo();
		
		//������ �����ϴ� �� �ʿ��� timerBroadcast
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

	/** ��� Ŭ���̾�Ʈ���� 1�ʰ� �������� �˸��� �޼ҵ� */
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

	/** ��Ʈ�� ���� �޼ҵ� */
	public void UserNetwork() {
		try {
			is = user_socket.getInputStream();
			dis = new DataInputStream(is);
			os = user_socket.getOutputStream();
			dos = new DataOutputStream(os);
			ois = new ObjectInputStream(is);

			server.textArea.append("Player NO. " + playerNo + "����\n");
			server.textArea.setCaretPosition(server.textArea.getText().length());
		} catch (Exception e) {
			server.textArea.append("��Ʈ�� ���� ����\n");
			server.textArea.setCaretPosition(server.textArea.getText().length());
		}
	}

	/** �ܾ� ����Ʈ �ʱ�ȭ �޼ҵ� */
	public void initWordList(){
		wordList.add("����");
		wordList.add("�ӼӸ�");
		wordList.add("������");
		wordList.add("��ũ����");
	}
	
	/** �ܾ� ����Ʈ���� �������� �����ϴ� �޼ҵ� */
	public String getRandomWord(){
		Random random = new Random();
		return wordList.get(random.nextInt(wordList.size()));
	}
	
	/** Protocol Ŭ���̾�Ʈ�� Protocol�� �۽��ϴ� �޼ҵ� */
	public void sendProtocol(Protocol pt) {
		try {
			oos = new ObjectOutputStream(os);

			oos.writeObject(pt);
			oos.flush();
		} catch (IOException e) {
			server.textArea.append("�޽��� �۽� ���� �߻�\n");
			server.textArea.setCaretPosition(server.textArea.getText().length());
		}
	}

	/** IMAGE Ŭ���̾�Ʈ�� ������ ������¡ �̹����� �۽��ϴ� �޼ҵ� */
	public void sendImage(String fineName) {
		Image originalImage = Toolkit.getDefaultToolkit().getImage(receiveFilePath + "/" + fineName); // ImageIcon
		ImageIcon originalImageIcon = new ImageIcon(originalImage);
		Image resizingImage = originalImage.getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH); // resize
		ImageIcon resizingImageIcon = new ImageIcon(resizingImage);

		try {
			oos = new ObjectOutputStream(os);
			oos.writeObject(originalImageIcon);
			System.out.println("OriginalImageIcon ����!");
			oos.writeObject(resizingImageIcon);
			System.out.println("ResizingImageIcon ����!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** FILE Ŭ���̾�Ʈ�κ��� ������ �����ϴ� �޼ҵ� */
	public void receiveFile(String fileName, long fileSize) {
		int byteSize = 10000;
		byte[] ReceiveByteArrayToFile = new byte[byteSize];

		String saveFolder = receiveFilePath; // ���
		File targetDir = new File(saveFolder);

		if (!targetDir.exists()) { // ���丮 ������ ����.
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

	/** FILE Ŭ���̾�Ʈ�� ������ �۽��ϴ� �޼ҵ� */
	public void sendFile(File sendFile, long fileSize) {
		int byteSize = 10000;
		byte[] sendFileTobyteArray = new byte[byteSize]; // ����Ʈ �迭 ����
		try {
			fis = new FileInputStream(sendFile); // ���Ͽ��� �о���� ���� ��Ʈ�� ����
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

	/** Protocol�� �����Ͽ� ó���ϴ� �޼ҵ� -> Thread */
	@Override
	public void run() {
		while (true) {
			try {
				readObject = ois.readObject();
				Protocol pt = (Protocol) readObject;

				server.textArea.append("�������� ���� ��ȣ : " + pt.getStatus() + "\n");
				switch (pt.getStatus()) {

				case Protocol.HOME_LOGIN:
					System.out.println("<ClientManager> Protocol.LOGIN");
					pt.setStatus(Protocol.HOME_SUCCESSLOGIN);
					pt.setRoomSize(server.rooms.size()); // ������� �ִ� ���� ���� ����
					sendProtocol(pt);
					System.out.println("<ClientManager> send SUCCESSLOGIN");
					netState = NETSTATE.Lobby; // �κ�� �̵�

					userInfo = pt.getUserInfo(); // �������ݷ� ���� ����� ���� ����
					updateGameListInLobby(); // GameList������Ʈ
					updateUserListInLobby(); // UserList������Ʈ
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
					File sendFile = new File(receiveFilePath + "/" + pt.getSendFileName()); // ����
																							// ����
					long fileSize = (int) sendFile.length(); // ���� ũ�� �޾ƿ���
					pt.setStatus(Protocol.LOBBY_CHAT_FILESEND);
					pt.setSendFileSize(fileSize);
					sendProtocol(pt);
					System.out.println("<ClientManager> send FILESEND");

					sendFile(sendFile, fileSize);
					break;
				case Protocol.LOBBY_CREATE_ROOM:
					System.out.println("<ClientManager> Protocol.CREATE_ROOM");
					String roomName = pt.getRoomName(); // ����ڰ� �Է��� ���̸�

					if (server.checkDuplicateRoomName(roomName)) {
						pt.setStatus(Protocol.LOBBY_CREATE_ROOM_FAIL);
						sendProtocol(pt);
						System.out.println("<ClientManager> send CREATE_ROOM_DENIED");
					} else {
						netState = NETSTATE.Room;
						room = new RoomInfo(roomName); // ���ο� �� ����
						userInfo.setIsMaster(true); // ���������� ǥ��

						server.addRoom(room); // ���� ���Ϳ� �߰�
						// roomName = null?
						server.addUserToRoom(this, roomName); // Client�� �濡 �߰�

						pt.setStatus(Protocol.LOBBY_CREATE_ROOM_SUCCESS);
						sendProtocol(pt);
						System.out.println("<ClientManager> send CREATE_ROOM_SUCCESS");

						updateGameListInLobby(); // GameList������Ʈ
						updateUserListInLobby(); // UserList������Ʈ
					}
					break;
				case Protocol.LOBBY_LOGOUT:
					System.out.println("<ClientManager> Protocol.LOBBY_LOGOUT");
					netState = NETSTATE.Home;
					
					//ó��!!
					break;
				case Protocol.GAME_IN:
					System.out.println("<ClientManager> Protocol.GAME_IN");
					pt.setStatus(Protocol.GAME_CREATED);
					// �濡 �ִ� Client�� UserInfo Vector
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
						userInfo.setIsMaster(false); // ������ X

						// roomName = null?
						// Client�� �濡 �߰�
						server.addUserToRoom(this, pt.getRoomName());

						pt.setStatus(Protocol.LOBBY_JOIN_ROOM_SUCCESS);
						sendProtocol(pt);
						System.out.println("<ClientManager> send LOBBY_JOIN_ROOM_SUCCESS");

						updateGameListInLobby(); // GameList������Ʈ
						updateUserListInLobby(); // UserList������Ʈ
					}
					break;
				case Protocol.GAME_JOIN_IN:
					System.out.println("<ClientManager> Protocol.GAME_JOIN_IN");

					// �ش��ϴ� �뿡 �ִ� �����ڵ鿡�� ���ο� �����ڰ� ������ �˸���.
					for (ClientManager userInRoom : room.getUsersClientManager()) {
						try {
							pt.setStatus(Protocol.GAME_JOIN_PARTIPANT);
							// �濡 �ִ� Client�� UserInfo Vector
							pt.setUsersInRoom(room.getUsersInfo());
							userInRoom.sendProtocol(pt);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					System.out.println("<ClientManager> send GAME_JOIN_PARTIPANT");

					updateUserListInLobby(); // UserList������Ʈ
					break;
				case Protocol.GAME_CHAT_MSG:
					System.out.println("<ClientManager> Protocol.GAME_CHAT_MSG");
					String answeredNickName = userInfo.getMyNickname();

					// round answer�� ��ġ�ϸ�
					if (room.getRoundAnswer().equals(pt.getChatSentence())) {
						// �ش��ϴ� �뿡 �ִ� �����ڵ鿡�� ���� ������� �˸���.
						for (ClientManager userInRoom : room.getUsersClientManager()) {
							if (userInRoom.getUserInfo().getMyNickname().equals(answeredNickName)) {
								// �������� ���� ++
								userInRoom.getUserInfo().increaseScore();
							}
							pt.setStatus(Protocol.GAME_CORRECT_ANSWER);
							pt.setUserInfo(userInfo);
							userInRoom.sendProtocol(pt);
						}
					}
					// round answer�� ��ġ���� ������
					else {
						// �ش��ϴ� �뿡 �ִ� �����ڵ鿡�� ä�� ������ �˸���.
						for (ClientManager userInRoom : room.getUsersClientManager()) {
							pt.setStatus(Protocol.GAME_CHAT_UPDATE);
							pt.setUserInfo(userInfo);
							userInRoom.sendProtocol(pt);
						}
					}
					break;
				case Protocol.GAME_START:
					System.out.println("<ClientManager> Protocol.GAME_START");

					// ���� ������ ���� ����� �������̸�
					if (userInfo.getIsMaster()) {
						// �ּ� ���� �ο� = 2
						if (room.getUsersCount() >= 2) {
							gameStart(pt); // ���ӽ��ۼ��� �������� ����
						} else {
							pt.setStatus(Protocol.GAME_START_FAIL_LACK_USER);
						}
					}
					// ���� ���� ����(������)�� ����
					else {
						pt.setStatus(Protocol.GAME_START_FAIL_NOT_MASTER);
					}
					sendProtocol(pt);
					break;
				case Protocol.GAME_LOGOUT:
					System.out.println("<ClientManager> Protocol.GAME_LOGOUT");
					netState = NETSTATE.Lobby;
					//ó��!!!
					
					break;
				case Protocol.GAME_DRAW:
					System.out.println("<ClientManager> Protocol.GAME_DRAW");
					// �ش��ϴ� �뿡 �ִ� �����ڵ鿡�� Point�� ������.
					for (ClientManager userInRoom : room.getUsersClientManager()) {
						pt.setStatus(Protocol.GAME_DRAW_BROADCAST);
						userInRoom.sendProtocol(pt);
					}
					break;
				case Protocol.GAME_DRAW_ALLCLEAR:
					System.out.println("<ClientManager> Protocol.GAME_DRAW_ALLCLEAR");
					// �ش��ϴ� �뿡 �ִ� �����ڵ鿡�� ���������� ������.
					for (ClientManager userInRoom : room.getUsersClientManager()) {
						pt.setStatus(Protocol.GAME_DRAW_ALLCLEAR_BROADCAST);
						userInRoom.sendProtocol(pt);
					}
					break;
				case Protocol.GAME_DRAW_ERASER:
					System.out.println("<ClientManager> Protocol.GAME_DRAW_ERASER");
					// �ش��ϴ� �뿡 �ִ� �����ڵ鿡�� ���������� ������.
					for (ClientManager userInRoom : room.getUsersClientManager()) {
						pt.setStatus(Protocol.GAME_DRAW_ERASER_BROADCAST);
						userInRoom.sendProtocol(pt);
					}
					break;
				case Protocol.GAME_DRAW_SELECT_COLOR:
					System.out.println("<ClientManager> Protocol.GAME_DRAW_SELECT_COLOR");
					// �ش��ϴ� �뿡 �ִ� �����ڵ鿡�� ������ �������� ���������� ������.
					for (ClientManager userInRoom : room.getUsersClientManager()) {
						pt.setStatus(Protocol.GAME_DRAW_SELECT_COLOR_BROADCAST);
						userInRoom.sendProtocol(pt);
					}
					break;
				case Protocol.GAME_DRAW_TIMER_EXPIRE:
					System.out.println("<ClientManager> Protocol.GAME_DRAW_TIMER_EXPIRE");
					// �ش��ϴ� �뿡 �ִ� �����ڵ鿡�� ������ �����ٴ� ���������� ������.
					timerExpireBroadcast(userInfo);
					break;
					
				case Protocol.EXIT:
					System.out.println("<ClientManager> Protocol.EXIT");
					//ó��!!!
					
					break;
				} // switch�� ��
			} // try�� ��
			catch (IOException e) {
				try {
					if (netState == NETSTATE.Room) {
						// userExitInRoom();
					} else {
						dos.close();
						dis.close();
						user_socket.close();
						users.removeElement(this); // �������� ���� ��ü�� ���Ϳ��� �����
						server.textArea.append("���� ���Ϳ� ����� ����� �� : " + users.size() + "\n");
						server.textArea.append("����� ���� ������ �ڿ� �ݳ�\n");
						server.textArea.setCaretPosition(server.textArea.getText().length());
						server.playerCnt--;
					}
					return;
				} catch (Exception ee) {
				} // catch�� ��
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				// System.out.println(String.valueOf(readObject.toString()));
			}
		}
	}

	/** ClientManager�� Vector�� update�ϴ� �޼ҵ� */
	public void updateAllClientManager(Vector<ClientManager> users) {
		this.users = users;
	}

	/** Lobby�� user list�� update�ϴ� �޼ҵ� */
	public void updateUserListInLobby() {
		for (int i = 0; i < users.size(); i++) {
			ClientManager imsi = users.elementAt(i);

			// �������� ����
			Protocol pt = new Protocol();
			pt.setStatus(Protocol.LOBBY_UPDATE_USER_LIST);
			pt.setUserList(setUserListNameInLobby());

			// in Lobby üũ?
			imsi.sendProtocol(pt);
		}
	}

	/** Lobby�� user list�� �̸��� setting�ϴ� �޼ҵ� */
	public Vector<String> setUserListNameInLobby() {
		int listSize = server.users.size();
		System.out.println("listSize: " + listSize);

		Vector<String> userListName = new Vector<String>();

		for (int i = 0; i < listSize; i++) {
			// in Lobby, ""�� �ƴ� �г��Ӹ�
			if (server.users.get(i).getNetState() == NETSTATE.Lobby
					&& !(server.users.get(i).getNickName()).equals("")) {
				userListName.add(server.users.get(i).getNickName());
			}
		}
		return userListName;
	}

	/** Lobby�� game list�� update�ϴ� �޼ҵ� */
	public void updateGameListInLobby() {
		for (int i = 0; i < users.size(); i++) {
			ClientManager imsi = users.elementAt(i);

			// �������� ����
			Protocol pt = new Protocol();
			pt.setStatus(Protocol.LOBBY_UPDATE_GAME_LIST);
			pt.setGameList(setGameListNameInLobby());

			// in Lobby üũ?
			imsi.sendProtocol(pt);
		}
	}

	/** Lobby�� game list�� �̸��� setting�ϴ� �޼ҵ� */
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
			// Lobby�� �ִ� client���Ը� ����
			if (imsi.getNetState() == NETSTATE.Lobby) {
				imsi.sendProtocol(pt);
			}
		}
	}

	/** IMAGE �̹��� broadCasting */
	public void broadCastImage(String str) {
		for (int i = 0; i < users.size(); i++) {
			ClientManager imsi = users.elementAt(i);
			imsi.sendImage(str);
		}
	}

	/** ������ �����ϴ� �޼ҵ� */
	public void gameStart(Protocol pt) {
		System.out.println("<ClientManager> call gameStart");

		room.setRoomStatus(RoomInfo.ROOM_PLAYING);
		room.setRoundNum(RoomInfo.ROOM_ROUND_NUM);

		// �ش��ϴ� �뿡 �ִ� �����ڵ鿡�� ���� ������ �˸���.
		for (ClientManager userInRoom : room.getUsersClientManager()) {
			UserInfo userInfoInRoom = userInRoom.getUserInfo();
			try {
				// // �濡 �ִ� Client�� UserInfo Vector
				// pt.setUsersInRoom(room.getUsersInfo());

				// ���� �������̸�
				if (userInfoInRoom.getIsMaster()) {
					userInfoInRoom.setStatus(UserInfo.QUESTRIONER_INROOM);
					pt.setUserInfo(userInfoInRoom); //������ ����
					pt.setStatus(Protocol.GAME_START_SUCCESS_QUESTIONER);

					// ���� ���� ����
					room.setRoundNum(ROUND_NUM - 1); // ���� �� round--
					room.setRoundAnswer(getRandomWord()); // ����ܾ� ���� ����
					pt.setRoundAnswer(room.getRoundAnswer()); // �������ݿ� ��������
				}
				// �������� �ƴϸ�
				else {
					userInfoInRoom.setStatus(UserInfo.ANSWERER_INROOM);
					pt.getUserInfo().setMyNickname(""); //������ �̸��� ����
					pt.setStatus(Protocol.GAME_START_SUCCESS_ANSWER);
				}
				// �������� ����
				//pt.setStatus(Protocol.GAME_START_SUCCESS);
				userInRoom.sendProtocol(pt);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/** ����ڰ� ���� ������ �� ó���ϴ� �޼ҵ� */
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
	// users.removeElement(this); // �������� ���� ��ü�� ���Ϳ��� �����
	// if(users.size()!=0) {
	// users.get(0).broadCastMsg("/RMROOM " + playerNo);
	// }
	// server.textArea.append("���� ���Ϳ� ����� ����� �� : " + users.size() + "\n");
	// server.textArea.append("����� ���� ������ �ڿ� �ݳ�\n");
	// server.textArea.setCaretPosition(server.textArea.getText().length());
	// server.playerCnt--;
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	
	/** �ش��ϴ� �뿡 �ִ� �����ڵ鿡�� ���尡 �������� �˸��� ���ο� �����ڿ� ������ �����ϴ� �޼ҵ� */
	public void timerExpireBroadcast(UserInfo userInfo) {
		UserInfo recentQuestioner = userInfo;
		UserInfo nextQuestioner;

		while (true) {
			// ���� �濡 �ִ� ������ �߿� ���� ���� ���Ѵ�.
			UserInfo selected = users.get(turn).getUserInfo();

			// ���� �����ڿ� ���õ� ���ο� �����ڰ� ���� ������ �״�� ����
			if (!(selected.getMyNickname().equals(recentQuestioner.getMyNickname()))) {
				nextQuestioner = selected;
				break;
			}
			turn++;

			if (turn == users.size()) {
				turn %= users.size();
			}
		}

		// roundNum > 0: ���� ���尡 0���� ũ�� ���� ���� ����
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
					room.setRoundAnswer(getRandomWord()); // ���ο� ����ܾ� ����
					pt.setRoundAnswer(room.getRoundAnswer()); // ����ܾ� �������ݿ� �߰�
				} else {
					System.out.println("IN else");
					clientManager.getUserInfo().setStatus(UserInfo.ANSWERER_INROOM);
					pt.setStatus(Protocol.GAME_START_SUCCESS_ANSWER);
				}
				//�������� ����
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