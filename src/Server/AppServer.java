package Server;

import java.io.IOException;
import java.util.logging.Logger;

public class AppServer {
    static final int APP_PORT = 8001;
    static Logger logger = Logger.getLogger("root");
   
	public static void main(String[] args) throws IOException {
		GameRoom gameRoom = new GameRoom(logger);
		GameManager gameManager = new GameManager(logger, gameRoom);
		TCPServer server = new TCPServer(APP_PORT, logger, gameRoom, gameManager);
		server.start();
	}

}
