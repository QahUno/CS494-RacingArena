package Client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

import javax.print.attribute.standard.RequestingUserName;

import org.json.*;

public class TCPClient {	
    public SocketChannel client = null;
    
    public String username = null;

    public Integer point =  null;

    public Boolean isReady = false;

//    public Integer answer = null;

//    public Instant timestamp = null;

    public Integer consecutiveFailedAnswer = 0;

    public Boolean isEliminated = false;
    
    private Logger logger = null;

    public TCPClient(String SERVER_IP, int SERVER_PORT, Logger logger) throws IOException {
        client = SocketChannel.open();
//        client.configureBlocking(false);
        client.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT));
        this.logger = logger;
        this.logger.info("Established client socket");
    }
    
    public void run() throws IOException {
    	JSONObject req = new JSONObject();
    	req.put("event", "SERVER_REGISTER");
    	req.put("name", "Huy2");

    	JSONObject res = sendRequest(req);
    	
    	
    	handleClose();
    }
        
    // send request & receive response (JsonObject)
    public JSONObject sendRequest(JSONObject requestJsonObj) throws IOException {
    	this.send(requestJsonObj);
	    return this.receive();
    }
    
    public void send(JSONObject reqJson) throws IOException {
		ByteBuffer reqBuffer = ByteBuffer.allocate(1024);
		reqBuffer.put(reqJson.toString().getBytes());
		reqBuffer.flip();
		this.client.write(reqBuffer);
    }
    
    public JSONObject receive() throws IOException {
	    ByteBuffer resBuffer = ByteBuffer.allocate(1024);
	    int bytesCount = client.read(resBuffer);

	    if (bytesCount > 0) {
	    	resBuffer.flip();
	    	return new JSONObject (new String(resBuffer.array()));
	    }	

	    return null;
    }
    
    public boolean handleRegister(String registeredName) throws IOException {
    	JSONObject reqJsonObj = new JSONObject();
    	reqJsonObj.put("name", registeredName);
    	
    	JSONObject resJsonObj = sendRequest(reqJsonObj);
    	if(resJsonObj == null) {
            this.logger.info("Server has problem");
    		return false; // Server has problem
    	}
    	
    	boolean status = (boolean)resJsonObj.get("status");
    	if(status) {
        	this.logger.info("Register successfully");	
    	}else {
        	this.logger.info("Register fail");	
    	}
    	return status;
    }

    
    public void handleClose() throws IOException {
    	client.close();
    }
    
}