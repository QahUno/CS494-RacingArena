package Server;

import java.io.IOException;
import java.nio.channels.SelectionKey;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class GameRoom {
    public volatile int MAX_PLAYER = 3;
    public volatile LinkedHashMap<SelectionKey, Player> hashmapPlayers= new LinkedHashMap<SelectionKey, Player>();
	public volatile GameExpression gameExpression = null;
	public volatile boolean isFull = false;
	
	private volatile Logger logger = null;
	
	public GameRoom(Logger logger) {
	    this.logger = logger;
	}
    
	public synchronized List<Player> getReadyPlayers() {
	    List<Player> readyPlayers = new ArrayList<>();

	    for (Player player : this.hashmapPlayers.values()) {
	        if (player.isRegistered && !player.isEliminated) {
	            readyPlayers.add(player);
	        }
	    }
	    
	    return readyPlayers;
	}
	
	public synchronized List<Player> getAllPlayers() {
	    List<Player> allPlayers = new ArrayList<>();

	    for (Player player : this.hashmapPlayers.values()) {
	    	allPlayers.add(player);
	    }
	    
	    return allPlayers;
	}
	
	public synchronized List<Player> getRegisteredPlayers() {
	    List<Player> registeredPlayers = new ArrayList<>();

	    for (Player player : this.hashmapPlayers.values()) {
	        if (player.isRegistered) {
	        	registeredPlayers.add(player);
	        }
	    }
	    
	    return registeredPlayers;
	}


//    public synchronized List<String> getPlayernames() {
//        List<String> Playernames = new ArrayList<>();
//        for (Player Player : this.getReadyPlayers()) {
//            Playernames.add(Player.Playername);
//        }
//        return Playernames;
//    }
//
//    public synchronized List<Integer> getPoints() {
//        List<Integer> points = new ArrayList<>();
//        for (Player Player : this.getReadyPlayers()) {
//            points.add(Player.point);
//        }
//        return points;
//    }


    public synchronized Boolean addPlayer(SelectionKey selectionKey, Player Player) {
        this.logger.info("Adding new Player");
        this.hashmapPlayers.put(selectionKey, Player);
        return true;
    }

//    public synchronized List<Player> getPlayers() {
//        return this.Players;
//    }
//
//    public synchronized List<Player> getReadyPlayers() {
//        return this.Players.stream().filter(u -> u.isReady && !u.client.socket().isClosed()).collect(Collectors.toList());
//    }
//
//    public synchronized Integer numReadyPlayers() {
//        Integer count = 0;
//        for (Player Player : this.Players) {
//            if (Player.isReady && Player.isActive()) {
//                count += 1;
//            }
//        }
//        return count;
//    }
//
//    public synchronized void startNewRound() {
//        this.gameBoard = GameBoard.generateGame();
//        for (Player Player : this.getReadyPlayers()) {
//            Player.resetAnswer();
//        }
//    }
//
//    public synchronized void reset() {
//        for (Player Player : this.Players) {
//            try {
//                Player.client.close();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
//        this.Players.clear();
//    }
}
