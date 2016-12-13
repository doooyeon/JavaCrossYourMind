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
	public ClientManager(Socket soc, Vector<ClientManager> vc, int playerNo, Server server) { // �����ڸ޼ҵ�
		// �Ű������� �Ѿ�� �ڷ� ����
		this.server = server;
		this.user_socket = soc;
		this.users = vc;
		this.playerNo = playerNo;

		this.userInfo = new UserInfo();

		UserNetwork();
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

				// Protocol ptSendToClient = new Protocol();

				server.textArea.append("�������� ���� ��ȣ : " + pt.getStatus() + "\n");
				switch (pt.getStatus()) {

				case Protocol.HOME_LOGIN:
					System.out.println("<ClientManager> Protocol.LOGIN");
					pt.setStatus(Protocol.HOME_SUCCESSLOGIN);
					pt.setRoomSize(server.rooms.size()); // ������� �ִ� ���� ���� ����
					sendProtocol(pt);
					System.out.println("<ClientManager> send SUCCESSLOGIN");
					netState = NETSTATE.Lobby; // �κ�� �̵�
					userInfo.setMyNickname(pt.getUserInfo().getMyNickname());

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
				// else if (splitMsg[0].equals("/LOGOUT")) {
				// System.out.println("�α׾ƿ�!");
				// netState = NETSTATE.Home;
				// }
				// break;
				//
				// case Room:
				// break;
				// }
				case Protocol.LOBBY_CREATE_ROOM:
					System.out.println("<ClientManager> Protocol.CREATE_ROOM");
					String roomName = pt.getRoomName(); // ����ڰ� �Է��� ���̸�

					if (server.checkDuplicateRoomName(roomName)) {
						pt.setStatus(Protocol.LOBBY_CREATE_ROOM_FAIL);
						sendProtocol(pt);
						System.out.println("<ClientManager> send CREATE_ROOM_DENIED");
					} else {
						updateGameListInLobby(); // GameList������Ʈ

						netState = NETSTATE.Room;
						room = new RoomInfo(roomName); // ���ο� �� ����
						userInfo.setIsMaster(true); // ���������� ǥ��

						server.addRoom(room); // ���� ���Ϳ� �߰�
						server.addUserToRoom(this, roomName); // Client�� �濡 �߰�

						pt.setStatus(Protocol.LOBBY_CREATE_ROOM_SUCCESS);
						pt.setUsersInRoom(room.getUsersInfo()); // �濡 �ִ� Client��
																// UserInfo
																// Vector
						sendProtocol(pt);
						System.out.println("<ClientManager> send CREATE_ROOM_SUCCESS");

						updateUserListInLobby(); // UserList������Ʈ
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
	void broadCastProtocol(Protocol pt) {
		for (int i = 0; i < users.size(); i++) {
			ClientManager imsi = users.elementAt(i);
			// Lobby�� �ִ� client���Ը� ����
			if (imsi.getNetState() == NETSTATE.Lobby) {
				imsi.sendProtocol(pt);
			}
		}
	}

	/** IMAGE �̹��� broadCasting */
	void broadCastImage(String str) {
		for (int i = 0; i < users.size(); i++) {
			ClientManager imsi = users.elementAt(i);
			imsi.sendImage(str);
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