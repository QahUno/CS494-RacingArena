package Server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.time.Instant;

public class Player implements Comparable<Player> {

    public SelectionKey key;
    public SocketChannel client;

    public String name = null;

    public Integer point = null;

    public Boolean isReady = false;
    public Boolean isRegistered = false;

    public Integer answer = null;

    public Instant timestamp = null;

    public Integer consecutiveFailedAnswer = 0;

    public Boolean isEliminated = false;

    public Player(SelectionKey key, String name, Integer point) {
        this.key = key;
        this.client = (SocketChannel) key.channel();
        this.name = name;
        this.point = point;
    }

	public String read() {
        if (this.key.isReadable()) {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            String msg = null;

            try {
                this.client.read(buffer);
            } catch (IOException e) {
                //do nothing
            }
            msg = new String(buffer.array()).trim();
            if (msg.length() > 0) {
                return msg;
            }
            return null;
        } else {
            return null;
        }
    }

    public Boolean write(String msg) {
        if (!this.isActive()) {
            return false;
        }
        try {
            this.client.write(ByteBuffer.wrap(msg.getBytes()));
        } catch (IOException e) {
        }
        return true;
    }

    public void resetAnswer() {
        this.answer = null;
        this.timestamp = null;
    }

    public Boolean isActive() {
        return !this.client.socket().isClosed();
    }
    
    public int compareTo(Player anotherPlayer) {
        return anotherPlayer.point - this.point;
    }
    
    public void close() throws IOException {
    	this.client.close();
    }
}
