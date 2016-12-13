package superPanel;

import javax.swing.JPanel;

import network.Protocol;

public abstract class ReceiveJPanel extends JPanel {
	// public abstract void receiveMSG(String msg);
	/** 서버로부터 Protocol을 수신받는 오버라이딩 메서드 */
	public abstract void receiveProtocol(Protocol pt);
}
