package lobby;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import main.CYMFrame;


public class ProfileDialog extends JDialog {
	public ProfileDialog(ImageIcon profileImg, String id, String charName, int level) {
		// 다이얼로그 설정
		setTitle("Profile");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setSize(300, 210);
		setContentPane(new myPanel(profileImg, id, charName, level));
		
		// 화면 가운데서 출력
		Dimension frameSize = getSize();
		Dimension windowSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((windowSize.width - frameSize.width) / 2, (windowSize.height - frameSize.height) / 2);

		setResizable(false);
		setVisible(true);
	}

	class myPanel extends JPanel {
		private ImageIcon profileImg;
		
		private JLabel [] idLabel;
		private JLabel [] charNameLabel;
		private JLabel [] levelLabel;
		
		public myPanel(ImageIcon profileImg, String id, String charName, int level) {
			this.profileImg = profileImg;

			setLayout(null);
			setBackground(new Color(255, 230, 153));
			
			idLabel = new JLabel[2];
			for(int i=0; i<idLabel.length; i++) {
				idLabel[i] = new JLabel("Id:");
			}
			
			idLabel[0].setFont(new Font(CYMFrame.FONT, Font.BOLD, 17));
			idLabel[0].setBounds(160, 10, 50, 20);
			add(idLabel[0]);
			
			idLabel[1].setFont(new Font(CYMFrame.FONT, Font.PLAIN, 15));
			idLabel[1].setText(id);
			idLabel[1].setBounds(160, 30, 100, 20);
			add(idLabel[1]);
			
			charNameLabel = new JLabel[2];
			for(int i=0; i<charNameLabel.length; i++) {
				charNameLabel[i] = new JLabel("Char:");
			}
			
			charNameLabel[0].setFont(new Font(CYMFrame.FONT, Font.BOLD, 17));
			charNameLabel[0].setBounds(160, 70, 50, 20);
			add(charNameLabel[0]);
			
			charNameLabel[1].setFont(new Font(CYMFrame.FONT, Font.PLAIN, 15));
			charNameLabel[1].setText(charName);
			charNameLabel[1].setBounds(160, 90, 150, 20);
			add(charNameLabel[1]);
			
			levelLabel = new JLabel[2];
			for(int i=0; i<levelLabel.length; i++) {
				levelLabel[i] = new JLabel("Level:");
			}
			
			levelLabel[0].setFont(new Font(CYMFrame.FONT, Font.BOLD, 17));
			levelLabel[0].setBounds(160, 130, 50, 20);
			add(levelLabel[0]);
			
			levelLabel[1].setFont(new Font(CYMFrame.FONT, Font.PLAIN, 15));
			levelLabel[1].setText(Integer.toString(level));
			levelLabel[1].setBounds(160, 150, 50, 20);
			add(levelLabel[1]);			
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(profileImg.getImage(), 5, 5, this);

		}
	}
}
