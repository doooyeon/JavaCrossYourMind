package lobby;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JPanel;

public class OriginalImageDialog extends JDialog {

	public OriginalImageDialog(ImageIcon image) {
		// 다이얼로그 설정
		setTitle("Image");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setMinimumSize(new Dimension(0, 0));
		setSize(image.getIconWidth()+6, image.getIconHeight()+29);
		setContentPane(new myPanel(image));
		
		// 화면 가운데서 출력
		Dimension frameSize = getSize();
		Dimension windowSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((windowSize.width - frameSize.width) / 2, (windowSize.height - frameSize.height) / 2);

		setResizable(false);
		setVisible(true);
	}

	class myPanel extends JPanel {
		private ImageIcon background;
		
		public myPanel(ImageIcon image) {
			background = image;
		}

		public void paintComponent(Graphics g) {
			Image image = background.getImage();
			super.paintComponent(g);
			g.drawImage(image, 0, 0, background.getIconWidth(), background.getIconHeight(), this);
		}
	}

}
