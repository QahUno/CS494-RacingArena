package Server;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.json.JSONObject;

public class TCPServer extends Thread {
    private int SERVER_PORT;
    private Selector selector = null;
    private ServerSocketChannel serverSocketChannel = null;
    private ServerSocket serverSocket = null;
    private Logger logger = null;
    private GameRoom gameRoom = null;
    private GameManager gameManager = null;
    
//    private is

    public TCPServer(int port, Logger logger, GameRoom gameRoom, GameManager gameManager) throws IOException {
        this.SERVER_PORT = port;
        this.logger = logger;

        this.selector = Selector.open();
        this.serverSocketChannel = ServerSocketChannel.open();
        this.serverSocket = serverSocketChannel.socket();
        this.serverSocket.bind(new InetSocketAddress("0.0.0.0", port));
        
        this.serverSocketChannel.configureBlocking(false);
        this.serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT, null);

        this.gameRoom = gameRoom;
        this.gameManager = gameManager;
    }

    @Override
    public void run() {
        this.logger.info("Ready to serve requests!");
        this.gameManager.start();
        Iterator<SelectionKey> readyKey;
        while (true) {
            try {
                if(selector.select() <= 0) { // No ready
                	continue;
                }
                
                Set<SelectionKey> readySet = selector.selectedKeys();
                readyKey = readySet.iterator();
                
                while (readyKey.hasNext()) {
                    SelectionKey key = (SelectionKey) readyKey.next();
                    readyKey.remove();
                    
                    if (key.isAcceptable()) { // Client's request is ready
                        SocketChannel client = this.serverSocketChannel.accept();
                        client.configureBlocking(false); // Non-blocking

                        SelectionKey selectionKey = client.register(selector, SelectionKey.OP_READ);
                        Player player = new Player(selectionKey, null, 0);
                        this.gameRoom.addPlayer(selectionKey, player);
                    }
                    if (key.isReadable()) { // Handle all request
                        String req = gameRoom.hashmapPlayers.get(key).read();
                        if(req != null) {
                        	JSONObject reqJson = new JSONObject(req);
                        	String eventType = reqJson.getString("event");
                        	if(eventType != null) { // Valid request
                        		if(eventType.equals("SERVER_REGISTER")) {
                                	this.logger.info(eventType);
                        			this.handleRegister(key, reqJson);
                        		} else if(eventType.equals("SERVER_CLOSE")) { // "{status: true/false}"
                        			this.handleClose(key);
                        		} else if(eventType.equals("SERVER_ANSWER")) {
                        			this.handleAnswer(key, reqJson);
                        		}
                        	}
                        } else {
	                    	JSONObject resObj = new JSONObject();
	                    	resObj.put("error_msg", "Invalid request");
	                    	this.gameRoom.hashmapPlayers.get(key).write(resObj.toString());
                        }
                    }
                }
            } catch (Exception exception) {
                this.logger.info(exception.getMessage());
            }
        }
    }
    
    public void handleClose(SelectionKey key) throws IOException {
    	this.logger.info("Handle close");
    	JSONObject resJson = new JSONObject();
//    	resJson.put("status", true);
    	this.gameRoom.hashmapPlayers.get(key).write(resJson.toString());
		this.gameRoom.hashmapPlayers.get(key).close();	
    }
    
    public void handleRegister(SelectionKey key, JSONObject reqJson) {
    	this.logger.info("Handle register");
		String registeredName = reqJson.getString("name");
		JSONObject resJson = new JSONObject();
		if(this.gameRoom.isFull == true) {
			resJson.put("status", false);
			resJson.put("isFull", true);
			this.logger.info("Room is full");
		} else {
			if(registeredName != null && !isNameExist(registeredName)) {
				this.gameRoom.hashmapPlayers.get(key).name = registeredName;
				this.gameRoom.hashmapPlayers.get(key).isRegistered = true;
				resJson.put("status", true);
		    	this.logger.info("Successfully register");
				if(this.gameRoom.getRegisteredPlayers().size() == this.gameRoom.MAX_PLAYER) {
					this.gameRoom.isFull = true;
				}
			} else {
				resJson.put("status", false);
				this.logger.info("Unsuccessfully register");
			}
			resJson.put("isFull", false);
		}
		this.gameRoom.hashmapPlayers.get(key).write(resJson.toString());
	}
    
    public boolean isNameExist(String registeredName) {
		for(Player registeredPlayer : this.gameRoom.getRegisteredPlayers()) {
			if(registeredPlayer.name != null && registeredPlayer.name.equals(registeredName)) {
				return true;
			}
		}
		return false; 
    }
    
    public void handleAnswer(SelectionKey key, JSONObject reqObj) {
    	this.logger.info("Handle answer");
    	System.out.println(reqObj.toString());
    	Integer answer = reqObj.getInt("answer");
    	if(answer != null) {
        	this.gameRoom.hashmapPlayers.get(key).answer = reqObj.getInt("answer");
        	this.gameRoom.hashmapPlayers.get(key).timestamp	= Instant.parse(reqObj.getString("timestamp"));
    	}
    }
}
