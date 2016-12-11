 import java.awt.Image;
import java.awt.Toolkit;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Vector;
import javax.swing.ImageIcon;

public class ClientManager extends Thread {
	private static enum NETSTATE {
		Home, Lobby, Room
	};
	private String receiveFilePath = "C:/Program Files/CrossYourMindServer";
	
	//for connection
	private Server server;
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;
	private FileInputStream fis;
	private FileOutputStream fos;
	private ObjectOutputStream oos;
	private Socket user_socket;
	
	private Room room = null;
	private Vector<ClientManager> users;
	private int playerNo;
	private String userID;
	
	private NETSTATE netState = NETSTATE.Home;

	public String getUserID() {
		return userID;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public void setNetState(NETSTATE netState) {
		this.netState = netState;
	}

	NETSTATE getNetState() {
		return this.netState;
	}

	void changeUsers(Vector<ClientManager> users) {
		this.users = users;
	}

	
	public ClientManager(Socket soc, Vector<ClientManager> vc, int playerNo, Server server) { // 생성자메소드
		// 매개변수로 넘어온 자료 저장
		this.server = server;
		this.user_socket = soc;
		this.users = vc;
		this.playerNo = playerNo;

		UserNetwork();
	}
	
	void UserNetwork() {
		try {
			is = user_socket.getInputStream();
			dis = new DataInputStream(is);
			os = user_socket.getOutputStream();
			dos = new DataOutputStream(os);

			server.textArea.append("Player NO. " + playerNo + "접속\n");
			server.textArea.setCaretPosition(server.textArea.getText().length());
		} catch (Exception e) {
			server.textArea.append("스트림 셋팅 에러\n");
			server.textArea.setCaretPosition(server.textArea.getText().length());
		}
	}

	
//	void userExitInRoom() {
//		try {
//			dos.close();
//			dis.close();
//			user_socket.close();
//			if (users.size() == 1) {
//				if (server.users.size() != 0) {
//					server.users.get(0).broadCastMsg("/RMROOM " + server.rooms.indexOf(room) + 1);
//				}
//				server.rooms.removeElement(room);
//			}
//			users.removeElement(this); // 에러가난 현재 객체를 벡터에서 지운다
//			if(users.size()!=0) {
//				users.get(0).broadCastMsg("/RMROOM " + playerNo);
//			}
//			server.textArea.append("현재 벡터에 담겨진 사용자 수 : " + users.size() + "\n");
//			server.textArea.append("사용자 접속 끊어짐 자원 반납\n");
//			server.textArea.setCaretPosition(server.textArea.getText().length());
//			server.playerCnt--;
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	void broadCast(String str) {
		for (int i = 0; i < users.size(); i++) {
			ClientManager imsi = users.elementAt(i);
			imsi.sendString(str);
		}
	}
	
	public void sendString(String str) {
		try {
			dos.writeUTF(str);
		} 
		catch (IOException e) {
			server.textArea.append("메시지 송신 에러 발생\n");	
			server.textArea.setCaretPosition(server.textArea.getText().length());
		}
	}
	
	void broadCastImage(String str) {
		for (int i = 0; i < users.size(); i++) {
			ClientManager imsi = users.elementAt(i);
			imsi.sendImage(str);
		}
	}
	
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
	
	public void receiveFile(String fileName, int fileSize) {
		int byteSize = 10000;
		byte[] ReceiveByteArrayToFile = new byte[byteSize];
		
		String saveFolder =  receiveFilePath;  //경로        
        File targetDir = new File(saveFolder);  
        
        if(!targetDir.exists()) { //디렉토리 없으면 생성.
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
	
	public void sendFile(File sendFile, int fileSize) {
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
	
	public void run() { // 메세지 받아서 처리하는 쓰레드
		while (true) {
			String msg = "";
			String splitMsg[];
			try {
				msg = dis.readUTF();
			} catch (IOException e) {
				try {
					if (netState == NETSTATE.Room) {
						//userExitInRoom();
					}
					else {
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
			} // 바깥 catch문끝'
			msg = msg.trim();
			splitMsg = msg.split(";");
			if (splitMsg.length < 1)
				return;
			server.textArea.append("메세지 받음 : " + msg + "\n");
			switch (netState) {
			case Home:
				if (splitMsg[0].equals("/LOGIN")) {
					sendString("/SUCCESSLOGIN;" + splitMsg[1] + ";" + splitMsg[2] + ";" + splitMsg[3] + ";"
							+ server.rooms.size());
					netState = NETSTATE.Lobby;
					userID = splitMsg[1];
					sendString(splitMsg[1] + "님 환영합니다."); // 연결된 사용자에게
				}
				break;
			case Lobby:
				if (splitMsg[0].equals("/MSG")) {
					broadCast("/MSG;" + splitMsg[1] + ";" + splitMsg[2] + ";" + splitMsg[3] + ";" + splitMsg[4] + ";"
							+ splitMsg[5] + ";" + splitMsg[6]);
				} 
				else if(splitMsg[0].equals("/IMAGE")) {
					receiveFile(splitMsg[6], Integer.parseInt(splitMsg[7]));
					broadCast("/IMAGE;" + splitMsg[1] + ";" + splitMsg[2] + ";" + splitMsg[3] + ";"
							 + splitMsg[4] + ";"  + splitMsg[5] + ";"  + splitMsg[6]);
					broadCastImage(splitMsg[6]);
				} 
				else if(splitMsg[0].equals("/FILE")) {
					receiveFile(splitMsg[6], Integer.parseInt(splitMsg[7]));
					broadCast("/FILE;" + splitMsg[1] + ";" + splitMsg[2] + ";" + splitMsg[3] + ";"
							 + splitMsg[4] + ";"  + splitMsg[5] + ";"  + splitMsg[6]);
				} 
				else if (splitMsg[0].equals("/FILESAVE")) {
					File sendFile = new File(receiveFilePath + "/" + splitMsg[1]); // 파일 생성
					int fileSize = (int) sendFile.length(); // 파일 크기 받아오기
					broadCast("/FILESEND;" + splitMsg[1] + ";" + fileSize);
					sendFile(sendFile, fileSize);
				} 
				else if (splitMsg[0].equals("/LOGOUT")) {
					System.out.println("로그아웃!");
					netState = NETSTATE.Home;
				}
				break;

			case Room:
				break;
			}
			
		}
	}
	
}
