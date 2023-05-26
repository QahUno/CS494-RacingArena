package UI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.UIManager;

import Client.AppClient;
import Client.TCPClient;

public class WinnerScreen extends JFrame {
    private String winner = null;
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;
    
    private Image avatar;
    
    public WinnerScreen(String winner) throws IOException {
    	this.winner = winner;
    	avatar = ImageIO.read(new File("src/images/avatar.jpg"));
        
        // Set up the frame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setTitle("Winner Game Screen");
        
        // Add the main panel
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(255, 215, 0)); 
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                g.fillRect(0, 0, screenSize.width, screenSize.height);
                g.drawImage(avatar, 250, 150, 100, 100, null);
                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial", Font.BOLD, 24));
                FontMetrics fm = g.getFontMetrics();
                int stringWidth = fm.stringWidth(String.format("Congratulations, %s wins!", winner));
                int x = 300 - stringWidth / 2;
                g.drawString(String.format("Congratulations, %s wins!", winner), x, 300);
            }
        };
        mainPanel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        getContentPane().add(mainPanel);
        mainPanel.setLayout(null);
        
        JLabel lblNewLabel = new JLabel("🥇 WINNER");
        lblNewLabel.setForeground(Color.RED);
        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel.setFont(new Font("Dialog", Font.BOLD, 48));
        lblNewLabel.setBounds(0, 0, 586, 166);
        mainPanel.add(lblNewLabel);
        
        JButton btnNewButton = new JButton("Replay ⟳");
        btnNewButton.setBackground(UIManager.getColor("Button.background"));
        btnNewButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		setVisible(false);
        		// how to create new game, new process, new port ???
//        		AppClient newGame = new AppClient();
				try {
					TCPClient client = new TCPClient(AppClient.SERVER_IP, AppClient.APP_PORT, AppClient.logger);
					Register registerScreen = new Register(client);
	        		registerScreen.setVisible(true);

				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}        		
        	}
        });
        btnNewButton.setFont(new Font("Dialog", Font.BOLD, 18));
        btnNewButton.setBounds(242, 319, 122, 34);
        mainPanel.add(btnNewButton);
        
        setVisible(true);
    }
    
//    public static void main(String[] args) throws IOException {
//        new WinnerScreen();
//    }
}
