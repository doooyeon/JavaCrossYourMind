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

public class Server extends JFrame {
	private static final int PORT = 30000;
	private JPanel contentPane;
	private JButton startBtn; // ������ �����Ų ��ư
	JTextArea textArea; // Ŭ���̾�Ʈ �� ���� �޽��� ���

	int playerCnt = 0;
	private ServerSocket socket; // ��������
	private Socket soc; // �������
	
	Vector<Room> rooms  = new Vector<Room>(); // ����� ����ڸ� ������ ����;
	Vector<ClientManager> users = new Vector<ClientManager>(); // ����� ����ڸ� ������ ����

	
	public Server() {
		init();
	}

	private void init() { // GUI�� �����ϴ� �޼ҵ�
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

		startBtn = new JButton("���� ����");
		Myaction action = new Myaction();
		startBtn.addActionListener(action); // ����Ŭ������ �׼� �����ʸ� ��ӹ��� Ŭ������
		startBtn.setBounds(0, 325, 264, 37);
		contentPane.add(startBtn);
		textArea.setEditable(false); // textArea�� ����ڰ� ���� ���ϰԲ� ���´�.
	}
	
	class Myaction implements ActionListener { // ����Ŭ������ �׼� �̺�Ʈ ó�� Ŭ����
		@Override
		public void actionPerformed(ActionEvent e) {
			// �׼� �̺�Ʈ�� sendBtn�϶� �Ǵ� textField ���� Enter key ġ��
			if (e.getSource() == startBtn)
				server_start();
		}
	}
	
	void addRoom(Room room) {
		this.rooms.add(room);
	}
	void addUserToRoom(ClientManager clientManager, int roomIdx) {	
		rooms.get(roomIdx).addUser(clientManager);
	}
	
	private void server_start() {
		try {
			socket = new ServerSocket(PORT); // ������ ��Ʈ ���ºκ�
			startBtn.setText("����������");
			startBtn.setEnabled(false); // ������ ���̻� �����Ű�� �� �ϰ� ���´�

			if (socket != null) { // socket �� ���������� ��������
				Connection();
			}
		} catch (IOException e) {
			textArea.append("������ �̹� ������Դϴ�...\n");

		}

	}

	private void Connection() {
		Thread th = new Thread(new Runnable() { // ����� ������ ���� ������
			@Override
			public void run() {
				while (true) { // ����� ������ ����ؼ� �ޱ� ���� while��
					try {
						textArea.append("����� ���� �����...\n");
						soc = socket.accept(); // accept�� �Ͼ�� �������� ���� �����
						playerCnt++;
						textArea.append("����� ����!!\n");
						ClientManager user = new ClientManager(soc, users, playerCnt, Server.this);
						users.add(user); // �ش� ���Ϳ� ����� ��ü�� �߰�
						user.start(); // ���� ��ü�� ������ ����
					} catch (IOException e) {
						textArea.append("!!!! accept ���� �߻�... !!!!\n");
					}
				}
			}
		});
		th.start();
	}

	public static void main(String[] args) {
		Server frame = new Server();
		frame.setVisible(true);
	}
}
