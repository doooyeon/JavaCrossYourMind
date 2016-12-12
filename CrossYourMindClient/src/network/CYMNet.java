package network;

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
import java.net.UnknownHostException;

import javax.swing.ImageIcon;

import home.HomePanel;
import lobby.LobbyPanel;
import room.RoomPanel;
import superPanel.ReceiveJPanel;

public class CYMNet {
	private static enum NETSTATE {
		Home, Lobby, Room, Game
	};

	// for TCP
	private static final int PORT = 30000;
	private static final String IP = "127.0.0.1";

	// for connection
	private Socket socket; // �������
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;
	private FileInputStream fis;
	private FileOutputStream fos;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;

	private Object readObject; // ������ Protocol Object
	private Thread th;

	// for GUI
	private ReceiveJPanel panel; // �޼����� ���� �г�(�гθ��� �������̵� ó��)
	private HomePanel homePanel;
	private LobbyPanel lobbyPanel;
	private RoomPanel roomPanel;

	// GAME INFO
	private NETSTATE netState = NETSTATE.Home;

	/** CYMNet construction */
	public CYMNet() { //
	}

	/* set State */
	public void setStateToHome() {
		netState = NETSTATE.Home;
	}

	public void setStateToLobby() {
		netState = NETSTATE.Lobby;
	}

	public void setStateToRoom() {
		netState = NETSTATE.Room;
	}

	/* set Panel */
	public void setHomePanel(HomePanel homePanel) {
		this.homePanel = homePanel;
	}

	public void setLobbyPanel(LobbyPanel lobbyPanel) {
		this.lobbyPanel = lobbyPanel;
	}

	public void setRoomPanel(RoomPanel roomPanel) {
		this.roomPanel = roomPanel;
	}

	/* change Panel */
	public void toHomePanel() {
		this.panel = this.homePanel;
	}

	public void toLobbyPanel() {
		this.panel = this.lobbyPanel;
	}

	public void toRoomPanel() {
		this.panel = this.roomPanel;
	}

	/** TCP IP ������ ���� */
	public void network() {

		try {
			socket = new Socket(IP, PORT);
			if (socket != null) {// socket�� null���� �ƴҶ� ��! ����Ǿ�����
				Connection(); // ���� �޼ҵ带 ȣ��
			}
		} catch (UnknownHostException e) {

		} catch (IOException e) {
			System.out.println("���� ���� ����!!");
			System.exit(0);
		}
	}

	/** �������� �޼ҵ� ����κ� */
	public void Connection() {
		try { // ��Ʈ�� ����
			os = socket.getOutputStream();
			is = socket.getInputStream();
			dos = new DataOutputStream(os);
			dis = new DataInputStream(is);
			oos = new ObjectOutputStream(os);
			// ois = new ObjectInputStream(is);
		} catch (IOException e) {
			System.out.println("��Ʈ�� ���� ����!!");
		}

		receiveProtocol();
	}

	/** �����κ��� Protocol�� ���Ź޴� ������ */
	public void receiveProtocol() {
		// �����带 ������ �����κ��� ���������� ����
		th = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					switch (netState) {
					case Home:
						System.out.println("<CYMNet> Enter Home");
					case Lobby:
						System.out.println("<CYMNet> Enter Lobby");
						try {
							System.out.println("ois -> 1111");
							ois = new ObjectInputStream(is);
							System.out.println("ois -> 2222");
							readObject = ois.readObject();
							System.out.println("ois -> 3333");

							Protocol pt = (Protocol) readObject;
							panel.receiveProtocol(pt);// �� �гο��� ���� �޼��� ó��
						} catch (IOException e) {
							System.out.println(String.valueOf(readObject) + " Protocol ���� ����!!(IOException)");
							// ������ ���� ��ſ� ������ ������ ��� ������ �ݴ´�
							try {
								os.close();
								is.close();
								dos.close();
								dis.close();
								oos.close();
								ois.close();
								socket.close();
								break; // ���� �߻��ϸ� while�� ����
							} catch (IOException e1) {
							}
							break;
						} catch (ClassNotFoundException e) {
							System.out
									.println(String.valueOf(readObject) + " Protocol ���� ����!!(ClassNotFoundException)");
							e.printStackTrace();
							break;
						} catch (ClassCastException e) {
							System.out.println(String.valueOf(readObject) + " Protocol ���� ����!!(ClassCastException)");
							e.printStackTrace();
							break;
						} catch (NullPointerException e) {
							System.out.println(String.valueOf(readObject) + " Protocol ���� ����!!(NullPointerException)");
							e.printStackTrace();
							break;
						}
					case Room:
						System.out.println("<CYMNet> Room ����");
					}
				} // while�� ��
			}// run�޼ҵ� ��
		});// thread ���� ��
		th.start();
	}

	/** ������ Protocol�� �۽��ϴ� �޼ҵ� */
	public void sendProtocol(Protocol pt) {
		try {
			oos.writeObject(pt);
			oos.flush();
		} catch (Exception e) {
			System.out.println("Protocol �۽� ����!!");
			e.printStackTrace();
		}
	}

	/** ������ ������ �۽��ϴ� �޼ҵ� */
	public void sendFile(File sendFile, long fileSize) {
		try {
			int byteSize = 10000;
			byte[] sendFileTobyteArray = new byte[byteSize]; // ����Ʈ �迭 ����

			fis = new FileInputStream(sendFile); // ���Ͽ��� �о���� ���� ��Ʈ�� ����

			int n = 0;
			int count = 0;

			// ������ ũ�⸸ŭ�� ������.
			while (count < fileSize) {
				n = fis.read(sendFileTobyteArray);
				dos.write(sendFileTobyteArray, 0, n);
				count += n;
			}

			fis.close();
		} catch (IOException e) {
			System.out.println("�޼��� �۽� ����!!");
			return;
		}
	}

	/** �����κ��� ������ �����ϴ� �޼ҵ� */
	public void receiveFile(String receiveFilePath, String receiveFileName, long receiveFileSize) {
		int byteSize = 10000;
		byte[] ReceiveByteArrayToFile = new byte[byteSize];

		try {
			fos = new FileOutputStream(receiveFilePath + "/" + receiveFileName);

			int n = 0;
			int count = 0;

			// ������ ũ�⸸ŭ�� �����Ѵ�.
			while (count < receiveFileSize) {
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

	/** �����κ��� ����, ������¡ �̹����� �����ϴ� �޼ҵ� */
	public ImageIcon[] receiveImageIcon() {
		ImageIcon[] receiveImage = null;
		receiveImage = new ImageIcon[2];

		try {
			ois = new ObjectInputStream(is);

			receiveImage[0] = (ImageIcon) ois.readObject();
			receiveImage[1] = (ImageIcon) ois.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return receiveImage;
	}
}
