package UI;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.io.IOException;
import java.awt.Color;
import javax.swing.ImageIcon;
import javax.swing.Timer;
import org.json.JSONObject;
import Client.TCPClient;

public class WaitingRoom extends JFrame {
	private JPanel contentPane;
	private Timer timer;
	private JLabel lbWaiting = null;
	private Integer timeLeft = null;
	private JLabel lbCounter = null;
	
	TCPClient tcpClient = null;

	public WaitingRoom(TCPClient tcpClient) throws IOException {
		this.tcpClient = tcpClient;
		setTitle("Waiting Room");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 690, 277);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lbRacingArena = new JLabel("RACING ARENA");
		lbRacingArena.setFont(new Font("Tahoma", Font.PLAIN, 24));
		lbRacingArena.setHorizontalAlignment(SwingConstants.CENTER);
		lbRacingArena.setBounds(252, 11, 414, 33);
		contentPane.add(lbRacingArena);
		
		JPanel sidePanel = new JPanel();
		sidePanel.setBackground(Color.WHITE);
		sidePanel.setBounds(0, 0, 240, 240);
		contentPane.add(sidePanel);
		sidePanel.setLayout(null);
		
		JLabel lbRacingBg = new JLabel("");
		lbRacingBg.setIcon(new ImageIcon(WaitingRoom.class.getResource("/images/racing-arena-bg.png")));
		lbRacingBg.setBounds(0, 0, 240, 240);
		lbRacingBg.setHorizontalAlignment(SwingConstants.LEFT);
		lbRacingBg.setVerticalAlignment(SwingConstants.TOP);
		sidePanel.add(lbRacingBg);
		
		lbWaiting = new JLabel("Waiting for other players ...");
		lbWaiting.setHorizontalAlignment(SwingConstants.CENTER);
		lbWaiting.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lbWaiting.setBounds(252, 75, 414, 59);
		contentPane.add(lbWaiting);
		
		lbCounter = new JLabel("");
		lbCounter.setHorizontalAlignment(SwingConstants.CENTER);
		lbCounter.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lbCounter.setBounds(252, 144, 414, 36);
		contentPane.add(lbCounter);
	}
	
	public void run() {
		waitingTask.start();
	}
	
	Thread waitingTask = new Thread() {
		@Override
		public void run() {
			try {
				JSONObject resJson = tcpClient.receive();
				String eventType = resJson.getString("event");
				timeLeft = resJson.getInt("readyTime");
				int maxPoint = resJson.getInt("maxPoint");
				

				if (eventType.equals("CLIENT_GAME_START")) {
					lbWaiting.setText("Game will start in ...");
			        lbCounter.setText(timeLeft + "s");
					timer = new Timer(1000, e -> {
						timeLeft--;
				        lbCounter.setText(timeLeft + "s");
				        if (timeLeft <= 0) {
				            timer.stop();
				            setVisible(false);
							GamePlay gameScreen = new GamePlay(tcpClient, maxPoint);
							gameScreen.setVisible(true);
							gameScreen.run();
				        }
				    });
				    timer.start();
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
}
