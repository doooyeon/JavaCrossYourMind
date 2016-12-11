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

	// TCP
	private static final int PORT = 30000;
	private static final String IP = "127.0.0.1";

	private Socket socket; // �������
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;
	private FileInputStream fis;
	private FileOutputStream fos;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;

	// GUI
	private ReceiveJPanel panel; // �޼����� ���� �г�
	private HomePanel homePanel;
	private LobbyPanel lobbyPanel;
	private RoomPanel roomPanel;

	// GAME INFO
	private int playerNo;
	private NETSTATE netState = NETSTATE.Home;

	public CYMNet() { // ������
	}

	public void setStateToHome() {
		netState = NETSTATE.Home;
	}

	public void setStateToLobby() {
		netState = NETSTATE.Lobby;
	}

	public void setStateToRoom() {
		netState = NETSTATE.Room;
	}

	public void setHomePanel(HomePanel homePanel) {
		this.homePanel = homePanel;
	}

	public void setLobbyPanel(LobbyPanel lobbyPanel) {
		this.lobbyPanel = lobbyPanel;
	}
	
	public void setRoomPanel(RoomPanel roomPanel) {
		this.roomPanel = roomPanel;
	}

	public void toHomePanel() {
		this.panel = this.homePanel;
	}

	public void toLobbyPanel() {
		this.panel = this.lobbyPanel;
	}

	public void toRoomPanel() {
		this.panel = this.roomPanel;
	}

	public void network() {
		// TCP IP ������ ����
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

	public void Connection() { // ���� ���� �޼ҵ� ����κ�
		try { // ��Ʈ�� ����
			is = socket.getInputStream();
			dis = new DataInputStream(is);
			os = socket.getOutputStream();
			dos = new DataOutputStream(os);

			// playerNo = dis.readInt();
			// System.out.println(playerNo);
		} catch (IOException e) {
			System.out.println("��Ʈ�� ���� ����!!");
		}

		Thread th = new Thread(new Runnable() { // �����带 ������ �����κ��� �޼����� ����
			@Override
			public void run() {
				while (true) {
					switch (netState) {
					case Home:
					case Lobby:
						try {
							String msg = dis.readUTF();
							msg = msg.trim();
							panel.receiveMSG(msg);
							// ���� �޼��� ó��
						} catch (IOException e) {
							System.out.println("�޼��� ���� ����!!");
							// ������ ���� ��ſ� ������ ������ ��� ������ �ݴ´�
							try {
								os.close();
								is.close();
								dos.close();
								dis.close();
								socket.close();
								break; // ���� �߻��ϸ� while�� ����
							} catch (IOException e1) {

							}
							break;
						}
						break;
						
					case Room:
						
					}
				} // while�� ��

			}// run�޼ҵ� ��
		});
		th.start();
	}

	public void sendMSG(String str) { // ������ �޼����� ������ �޼ҵ�
		try {
			dos.writeUTF(str);
		} catch (IOException e) {
			System.out.println("�޼��� �۽� ����!!");
			return;
		}
	}
	
	public void sendFile(File sendFile, long fileSize) { // ������ ������ ������ �޼ҵ�
		try {
			int byteSize = 10000;
			byte[] sendFileTobyteArray = new byte[byteSize]; // ����Ʈ �迭 ����

			fis = new FileInputStream(sendFile); // ���Ͽ��� �о���� ���� ��Ʈ�� ����

			int n = 0;
			int count = 0;
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
	
	public ImageIcon [] receiveImageIcon() {
		ImageIcon [] receiveImage = null;
		receiveImage = new ImageIcon[2];

		try {
			ois = new ObjectInputStream(is);
			receiveImage[0] = (ImageIcon) ois.readObject();
			receiveImage[1] = (ImageIcon) ois.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		}catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return receiveImage;
	}
	
	public void receiveFile(String receiveFilePath, String receiveFileName, int receiveFileSize) {
		int byteSize = 10000;
		byte[] ReceiveByteArrayToFile = new byte[byteSize];

		try {
			fos = new FileOutputStream(receiveFilePath + "/" + receiveFileName);
			
			int n = 0;
			int count = 0;
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

	public int getPlayerNum() {
		return playerNo;
	}

	public void setPlayerNum(int playerNum) {
		this.playerNo = playerNum;
	}
	
}
