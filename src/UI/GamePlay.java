package UI;

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.ImageIcon;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.time.Instant;
import java.awt.event.ActionEvent;
import javax.swing.JTextPane;
import javax.swing.text.StyledDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.border.CompoundBorder;
import org.json.JSONArray;
import org.json.JSONObject;

import Client.TCPClient;

public class GamePlay extends JFrame {
	private JPanel contentPane;
    private JTable leaderboardTable;
    private JPanel leaderboardPanel = null;
    private DefaultTableModel leaderboardModel = null;
    private JLabel leaderboardLabel = null;
    private JTextPane txtQuestion;
    private JTextField tfAnswer;
    private JPanel panel = null;
    private JPanel gamePanel;
    private JPanel pTimer = null;
    private JLabel lbTimer = null;
    private JButton btnSubmit = null;
    private JLabel lbRound = null;
    private Timer timer = null;
    private Integer duration = null;
    private Integer maxPoint = null;
    private TCPClient tcpClient = null;
    private JTextPane tpNotification;
    private JTable table;
	private JTextPane tpInfo;
	private JLabel lbCarBg;
	/**
	 * Launch the application.
	 */

	/**
	 * Create the frame.
	 */
	public GamePlay(TCPClient tcpClient, Integer maxPoint) {
		this.tcpClient = tcpClient;
		this.maxPoint = maxPoint;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 480);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		// Create leaderboard panel
		leaderboardPanel = new JPanel();
		leaderboardPanel.setBounds(542, 0, 244, 279);
		leaderboardPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY, 2), new EmptyBorder(5, 5, 5, 5)));
		leaderboardPanel.setBackground(new Color(255, 255, 200));
		leaderboardPanel.setLayout(null);

		leaderboardLabel = new JLabel("🏆 Leaderboard");
		leaderboardLabel.setBounds(7, 7, 230, 20);
		leaderboardLabel.setFont(new Font("Dialog", Font.BOLD, 18));
		leaderboardLabel.setHorizontalAlignment(JLabel.CENTER);
		leaderboardPanel.add(leaderboardLabel);

		leaderboardModel = new DefaultTableModel(new Object[][]{}, new String[]{"rank", "name", "point"});
		leaderboardTable = new JTable(leaderboardModel);
		leaderboardTable.setBounds(7, 40, 230, 232);
		leaderboardTable.setGridColor(Color.WHITE);
		leaderboardTable.setShowGrid(false);
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		leaderboardTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
		leaderboardTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
		leaderboardTable.setBackground(new Color(255, 255, 200));
		leaderboardTable.getTableHeader().setBackground(new Color(200, 200, 255));
		leaderboardTable.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 14));
		leaderboardTable.getTableHeader().setOpaque(false);
		leaderboardPanel.add(leaderboardTable);

		contentPane.setLayout(null);
		contentPane.add(leaderboardPanel);
		setContentPane(contentPane);
		
		// game panel
		gamePanel = new JPanel();
		gamePanel.setBackground(new Color(127, 255, 212));
		gamePanel.setBounds(0, 0, 542, 443);
		contentPane.add(gamePanel);
		gamePanel.setLayout(null);
		
		pTimer = new JPanel();
		pTimer.setBorder(new CompoundBorder());
		pTimer.setBounds(391, 10, 141, 53);
		gamePanel.add(pTimer);
		pTimer.setLayout(null);
		
		lbTimer = new JLabel("");
		lbTimer.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lbTimer.setHorizontalAlignment(SwingConstants.CENTER);
		lbTimer.setBounds(0, 0, 141, 53);
		pTimer.add(lbTimer);
		
		lbRound = new JLabel("");
		lbRound.setFont(new Font("Tahoma", Font.PLAIN, 24));
		lbRound.setHorizontalAlignment(SwingConstants.CENTER);
		lbRound.setBounds(161, 88, 196, 53);
		gamePanel.add(lbRound);
		
		tfAnswer = new JTextField();
		tfAnswer.setBackground(UIManager.getColor("TextField.background"));
		tfAnswer.setFont(new Font("Tahoma", Font.PLAIN, 18));
		tfAnswer.setBounds(86, 339, 351, 34);
		gamePanel.add(tfAnswer);
		tfAnswer.setColumns(10);
		
		btnSubmit = new JButton("Submit");
		btnSubmit.setBackground(UIManager.getColor("Button.background"));
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handleAnswer();
			}
		});
		btnSubmit.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnSubmit.setBounds(207, 383, 96, 34);
		gamePanel.add(btnSubmit);
		
		txtQuestion = new JTextPane();
		txtQuestion.setFont(new Font("Tahoma", Font.PLAIN, 18));
		txtQuestion.setEditable(false);
		txtQuestion.setBounds(86, 151, 351, 158);
		gamePanel.add(txtQuestion);
		
		tpInfo = new JTextPane();
		tpInfo.setEditable(false);
		tpInfo.setBounds(0, 10, 0, 0);
		tpInfo.setFont(new Font("Tahoma", Font.PLAIN, 16));
		gamePanel.add(tpInfo);
		
		lbCarBg = new JLabel("");
		lbCarBg.setIcon(new ImageIcon(GamePlay.class.getResource("/images/bg-racing-car.png")));
		lbCarBg.setBounds(0, 0, 542, 443);
		gamePanel.add(lbCarBg);
		
		JPanel pNotification = new JPanel();
		pNotification.setBackground(new Color(0, 255, 127));
		pNotification.setBounds(542, 277, 244, 166);
		contentPane.add(pNotification);
		pNotification.setLayout(null);
		
		JLabel lbNotification = new JLabel("🔔 Notification");
		lbNotification.setBackground(new Color(124, 252, 0));
		lbNotification.setFont(new Font("Dialog", Font.BOLD, 18));
		lbNotification.setVerticalAlignment(SwingConstants.CENTER);
		lbNotification.setHorizontalAlignment(SwingConstants.CENTER);
		lbNotification.setBounds(0, 0, 244, 28);
		pNotification.add(lbNotification);
		
		table = new JTable();
		table.setBackground(Color.ORANGE);
		table.setBounds(164, 161, -68, -68);
		pNotification.add(table);
		
		tpNotification = new JTextPane();
		tpNotification.setEditable(false);
		tpNotification.setFont(new Font("Tahoma", Font.PLAIN, 15));
		tpNotification.setBackground(new Color(0, 255, 127));
		tpNotification.setBounds(0, 29, 244, 137);
		pNotification.add(tpNotification);
		
	}
	
	public void run() {
		newTask.start();
	}
	
	Thread newTask = new Thread() {
		public void run() {
			while(true) {				
				//1. Round start: initialize the game board
				JSONObject resRoundStart = null;
				try {
					resRoundStart = tcpClient.receive();
					
					System.out.println("round start" + resRoundStart);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				String event = resRoundStart.getString("event");
				if(event.equals("CLIENT_GAME_END")) {
					try {
						handleGameEndUI(resRoundStart);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}
				
			    try {
					handleRoundStartUI(resRoundStart);
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				//2. In game: just thread sleep
			    try {
					Thread.sleep(duration*1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			    
			    //3. Round end: receive the verification
			    JSONObject resRoundEnd = null;
				try {
					resRoundEnd = tcpClient.receive();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				try {
					handleRoundEndUI(resRoundEnd);
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				// Execution delay
			    try {
					Thread.sleep(3000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			    
			    // 4. Game end
//			    try {
//					handleGameEndUI(resRoundStart);
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}

			}
		}
	};
	
	public void handleRoundStartUI(JSONObject resRoundStart) throws BadLocationException {
		int round = resRoundStart.getInt("round");
		String expression = resRoundStart.getString("expression");
		JSONArray ranking = resRoundStart.getJSONArray("ranking");
		duration = resRoundStart.getInt("duration");
		
		// username + score
		String s1 = "Username: " + this.tcpClient.username;
		String s2 = "Score: " + this.maxPoint;
		int mx = Math.max(s1.length(), s2.length());
		tpInfo.setBounds(0, 10, mx * 11, 46);
		String userInfo = String.format("👤 Username: %s\n💯 Score: %d / %d", this.tcpClient.username, this.tcpClient.point, this.maxPoint);	
		StyledDocument info = tpInfo.getStyledDocument();
		info.remove(0, info.getLength());
		Style infoStyle = tpInfo.addStyle("Color Style", null);
		StyleConstants.setForeground(infoStyle, Color.darkGray);
		info.insertString(info.getLength(), userInfo, infoStyle);
		
		lbRound.setText(String.format("Round %d", round));
		leaderboardModel.setRowCount(0);
		for(int i = 0; i < ranking.length(); ++i) {
			JSONObject row = ranking.getJSONObject(i);
			leaderboardModel.addRow(new Object[] {"#" + row.getInt("rank"), row.getString("name"), row.getInt("point") });;
		}
					 
		// add red question to question pane
		StyledDocument question = txtQuestion.getStyledDocument();
		question.remove(0, question.getLength());
		Style style = txtQuestion.addStyle("Color Style", null);
		StyleConstants.setForeground(style, Color.RED);
	    question.insertString(question.getLength(), "QUESTION: " + expression, style);
		
	    // center text
	    SimpleAttributeSet center = new SimpleAttributeSet ();
	    StyleConstants.setAlignment (center, StyleConstants.ALIGN_CENTER);
	    question.setParagraphAttributes (0, question.getLength (), center, false);
	    
	    // countdown timer
	    lbTimer.setText(String.format("%02d:%02d", duration / 60, duration % 60));
	    timer = new Timer(1000, e -> {
	        duration--;
	        lbTimer.setText(String.format("%02d:%02d", duration / 60, duration % 60));
	        if (duration <= 0) {
	            timer.stop();
	        } else if (duration <= 10) {
	            lbTimer.setForeground(Color.RED);
	        }
	    });
	    timer.start();

	    // set back to default
	    if (!tcpClient.isEliminated) {
	    	tfAnswer.setText("");
	    	tfAnswer.setEditable(true);
	    	tfAnswer.setForeground(Color.BLACK);
	    	btnSubmit.setVisible(true);
	    }
	}
	
	public void handleRoundEndUI(JSONObject resRoundEnd) throws BadLocationException {
		int expectedResult = resRoundEnd.getInt("expectedResult");
		// Let player know these:
		if(!tcpClient.isEliminated) {
			boolean isEliminated = resRoundEnd.getBoolean("isEliminated"); // this player is eliminated or not
			JSONArray eliminate = resRoundEnd.getJSONArray("eliminate"); // all eliminated player's name
			Boolean status = resRoundEnd.getBoolean("status"); // answer true/false
			int extraPoint = resRoundEnd.getInt("extraPoint"); // extra point for the fastest
			int point = resRoundEnd.getInt("point");
			
			this.tcpClient.point = point;
			System.out.println(isEliminated);
			if (isEliminated) {
				tcpClient.isEliminated = true;
				tfAnswer.setEnabled(false);
				tfAnswer.setText("YOU ARE DISQUALIFIED!");
				btnSubmit.setVisible(false);
			}
			else if (!status) {
				tfAnswer.setForeground(Color.RED);
			}
			else {
				tfAnswer.setForeground(Color.GREEN);
			}
			int len = eliminate.length();
			if (len > 0) {
//				String s = "Disqualifiers of this round:\n";
//				for (int i = 0; i < eliminate.length(); i++) {
//					s += (i + 1) + ". " + eliminate.getString(i) + "\n";
//				}
				String s = "";
				for (int i = 0; i < eliminate.length(); i++) {
					s += eliminate.getString(i) + " has been disqualified!\n";
				}
				
				// add red notification to notification pane
				StyledDocument notification = tpNotification.getStyledDocument();
//				notification.remove(0, notification.getLength());
				Style style = tpNotification.addStyle("Color Style", null);
				StyleConstants.setForeground(style, Color.RED);
				notification.insertString(notification.getLength(), s, style);
				
			    // center text
			    SimpleAttributeSet center = new SimpleAttributeSet ();
			    StyleConstants.setAlignment (center, StyleConstants.ALIGN_CENTER);
			    notification.setParagraphAttributes (0, notification.getLength (), center, false);		
			}
		}
		 
		// add green answer to question pane
		StyledDocument question = txtQuestion.getStyledDocument();
		Style style = txtQuestion.addStyle("Color Style", null);
		StyleConstants.setForeground(style, Color.GREEN);
	   question.insertString(question.getLength(), "\n\nAnswer: " + expectedResult, style);
	}
	
	public void handleAnswer() {
			String answer = tfAnswer.getText();
			JSONObject resJson = new JSONObject();
			resJson.put("event", "SERVER_ANSWER");
			resJson.put("answer", answer);
			resJson.put("timestamp", Instant.now());
			try {
				tcpClient.send(resJson);
				tfAnswer.setEditable(false);
				btnSubmit.setVisible(false);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
//			}
		}
	}
	
	public void handleGameEndUI(JSONObject resRoundStart) throws IOException {
		String winner = resRoundStart.getString("winner");
		setVisible(false);
		tcpClient.handleClose();
		WinnerScreen winnerScreen = new WinnerScreen(winner);
		winnerScreen.setVisible(true);
	}
}