package Client;

import java.io.IOException;
import java.util.logging.Logger;

import UI.Register;

public class AppClient {
    public static Logger logger = Logger.getLogger("root");
    public static final int APP_PORT = 8001;
    public static final String SERVER_IP = "localhost";

	public static void main(String[] args) throws IOException {
		TCPClient client = new TCPClient(SERVER_IP, APP_PORT, logger);
		Register registerScreen = new Register(client);
		registerScreen.setVisible(true);
	}
}
