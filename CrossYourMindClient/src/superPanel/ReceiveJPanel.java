package superPanel;

import javax.swing.JPanel;

import network.Protocol;

public abstract class ReceiveJPanel extends JPanel {
	public abstract void receiveMSG(String msg);
	public abstract void receiveProtocol(Protocol pt);
}
