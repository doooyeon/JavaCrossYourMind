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
	private Socket socket; // 연결소켓
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;
	private FileInputStream fis;
	private FileOutputStream fos;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;

	private Object readObject; // 수신할 Protocol Object
	private Thread th;

	// for GUI
	private ReceiveJPanel panel; // 메세지를 받을 패널(패널마다 오버라이딩 처리)
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

	/** TCP IP 서버에 접속 */
	public void network() {

		try {
			socket = new Socket(IP, PORT);
			if (socket != null) {// socket이 null값이 아닐때 즉! 연결되었을때
				Connection(); // 연결 메소드를 호출
			}
		} catch (UnknownHostException e) {

		} catch (IOException e) {
			System.out.println("소켓 접속 에러!!");
			System.exit(0);
		}
	}

	/** 실질적인 메소드 연결부분 */
	public void Connection() {
		try { // 스트림 설정
			os = socket.getOutputStream();
			is = socket.getInputStream();
			dos = new DataOutputStream(os);
			dis = new DataInputStream(is);
			oos = new ObjectOutputStream(os);
			// ois = new ObjectInputStream(is);
		} catch (IOException e) {
			System.out.println("스트림 설정 에러!!");
		}

		receiveProtocol();
	}

	/** 서버로부터 Protocol을 수신받는 스레드 */
	public void receiveProtocol() {
		// 스레드를 돌려서 서버로부터 프로토콜을 수신
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
							panel.receiveProtocol(pt);// 각 패널에서 받은 메세지 처리
						} catch (IOException e) {
							System.out.println(String.valueOf(readObject) + " Protocol 수신 에러!!(IOException)");
							// 서버와 소켓 통신에 문제가 생겼을 경우 소켓을 닫는다
							try {
								os.close();
								is.close();
								dos.close();
								dis.close();
								oos.close();
								ois.close();
								socket.close();
								break; // 에러 발생하면 while문 종료
							} catch (IOException e1) {
							}
							break;
						} catch (ClassNotFoundException e) {
							System.out
									.println(String.valueOf(readObject) + " Protocol 수신 에러!!(ClassNotFoundException)");
							e.printStackTrace();
							break;
						} catch (ClassCastException e) {
							System.out.println(String.valueOf(readObject) + " Protocol 수신 에러!!(ClassCastException)");
							e.printStackTrace();
							break;
						} catch (NullPointerException e) {
							System.out.println(String.valueOf(readObject) + " Protocol 수신 에러!!(NullPointerException)");
							e.printStackTrace();
							break;
						}
					case Room:
						System.out.println("<CYMNet> Room 들어옴");
					}
				} // while문 끝
			}// run메소드 끝
		});// thread 정의 끝
		th.start();
	}

	/** 서버로 Protocol을 송신하는 메소드 */
	public void sendProtocol(Protocol pt) {
		try {
			oos.writeObject(pt);
			oos.flush();
		} catch (Exception e) {
			System.out.println("Protocol 송신 에러!!");
			e.printStackTrace();
		}
	}

	/** 서버로 파일을 송신하는 메소드 */
	public void sendFile(File sendFile, long fileSize) {
		try {
			int byteSize = 10000;
			byte[] sendFileTobyteArray = new byte[byteSize]; // 바이트 배열 생성

			fis = new FileInputStream(sendFile); // 파일에서 읽어오기 위한 스트림 생성

			int n = 0;
			int count = 0;

			// 파일의 크기만큼만 보낸다.
			while (count < fileSize) {
				n = fis.read(sendFileTobyteArray);
				dos.write(sendFileTobyteArray, 0, n);
				count += n;
			}

			fis.close();
		} catch (IOException e) {
			System.out.println("메세지 송신 에러!!");
			return;
		}
	}

	/** 서버로부터 파일을 수신하는 메소드 */
	public void receiveFile(String receiveFilePath, String receiveFileName, long receiveFileSize) {
		int byteSize = 10000;
		byte[] ReceiveByteArrayToFile = new byte[byteSize];

		try {
			fos = new FileOutputStream(receiveFilePath + "/" + receiveFileName);

			int n = 0;
			int count = 0;

			// 파일의 크기만큼만 수신한다.
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

	/** 서버로부터 원본, 리사이징 이미지를 수신하는 메소드 */
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
