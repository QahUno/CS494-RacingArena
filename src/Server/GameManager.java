package Server;

import org.json.*;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

public class GameManager extends Thread {
    private static final Integer MAX_POINT = 5;

    private static final Integer DURATION = 25;

    private static final Integer READY_TIME = 3;

    private static final Integer MAX_FAILED_ANSWER = 3;

    private Logger logger = null;

    private GameRoom gameRoom = null;

    public GameManager(Logger logger, GameRoom gameRoom) {
        this.logger = logger;
        this.gameRoom = gameRoom;
    }

    @Override
    public void run() {
    	while(true) {
    		// Whenever game start -> continue even after that there is a player exiting
    		// Only stop when no one in room
    		// if no one in room -> reset the game
        	if(this.gameRoom.isFull == true) {
        		this.logger.info("In game phase");
        		// I. Send signal to START GAME for all players
        		JSONObject startGameJson = new JSONObject();
        		startGameJson.put("event", "CLIENT_GAME_START");
        		startGameJson.put("readyTime", READY_TIME);
        		startGameJson.put("maxPoint", MAX_POINT);
        		
        		for(Player registeredPlayer: this.gameRoom.getRegisteredPlayers()) {
        			registeredPlayer.write(startGameJson.toString());
        		}
        		
        		try {
					Thread.sleep(READY_TIME*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        		
//        		for(int ROUND = 1; ROUND <= MAX_ROUND; ++ROUND) {
        		int ROUND = 1;
        		boolean isContinue = true;
        		while(isContinue) {
        			
        			// Game end
        			for(Player readyPlayer : this.gameRoom.getReadyPlayers()) {
        				if(readyPlayer.point >= MAX_POINT) {
        					isContinue = false;
        					break;
        				}
        			}
        			
        			// all players are eliminated
        			if(this.gameRoom.getReadyPlayers().size() == 0) {
        				isContinue = false;
        			}
        			
        			if (!isContinue) {
    					try {
							handleEndGame();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
        				continue;
        			}
        		
        			
            		// 1. SEND ROUND INFO
            		this.logger.info("Round" + ROUND);
        			JSONObject startRoundJson = new JSONObject();
        			this.gameRoom.gameExpression = new GameExpression();
        			
        			startRoundJson.put("event", "CLIENT_ROUND_START");
        			startRoundJson.put("round", ROUND);
        			startRoundJson.put("expression", this.gameRoom.gameExpression.convertToString());
        			startRoundJson.put("ranking", this.handleRanking());
        			startRoundJson.put("duration", DURATION);        			
        			
        			// Still send to eliminated player
        			for(Player registeredPlayer : this.gameRoom.getRegisteredPlayers()) {
        				registeredPlayer.write(startRoundJson.toString());
        			}
        			
        			// 2. DURATION
        			try {
						Thread.sleep(DURATION*1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
        			
        			// 3. VERIFY
        			int cntIncorrect = 0;
        			Player earliestPlayer = null;
        			Map<Player, JSONObject> listAnswerJson = new HashMap<Player, JSONObject>();
        			JSONArray eliminate = new JSONArray();
        			for(Player registeredPlayer : this.gameRoom.getRegisteredPlayers()) {
        				JSONObject answerJson = new JSONObject();
        				answerJson.put("expectedResult", this.gameRoom.gameExpression.expectedResult);
        				if(!registeredPlayer.isEliminated) {
	         				if(registeredPlayer.answer != null && registeredPlayer.answer == this.gameRoom.gameExpression.expectedResult) {
	        					++registeredPlayer.point;
	        					registeredPlayer.consecutiveFailedAnswer = 0;
	         					answerJson.put("status", true);
	         					
	         					if(earliestPlayer == null) {
	         						earliestPlayer = registeredPlayer;
	         					} else {
	         				        Instant earliest = earliestPlayer.timestamp;
	         				        Instant instant = registeredPlayer.timestamp;
	
	         				        int result = instant.compareTo(earliest);
	         				        if (result < 0) {
	         				        	earliestPlayer = registeredPlayer;
	         				        }
	         					}
	    						answerJson.put("isEliminated", false);
	        				}else {
	        					++cntIncorrect;
	        					if(registeredPlayer.point != 0) {
	            					--registeredPlayer.point;	
	        					}        					
	        					++registeredPlayer.consecutiveFailedAnswer;
	        					if(registeredPlayer.consecutiveFailedAnswer == MAX_FAILED_ANSWER) {
	        						registeredPlayer.isEliminated = true;
	        						answerJson.put("isEliminated", true);
	        						eliminate.put(registeredPlayer.name);
	        					} else {
	        						answerJson.put("isEliminated", false);
	        					}
	        					answerJson.put("status", false);

	        				}
        			}
         				listAnswerJson.put(registeredPlayer, answerJson);
        			}

        			for (Map.Entry<Player, JSONObject> entry : listAnswerJson.entrySet()) {
        			    JSONObject tmp = entry.getValue();
        			    tmp.put("eliminate", eliminate);
        			    if(entry.getKey() == earliestPlayer) {
        			    	earliestPlayer.point += cntIncorrect;
            			    tmp.put("extraPoint", cntIncorrect);
        			    } else {
        			    	tmp.put("extraPoint", 0);
        			    }
        			    tmp.put("point", entry.getKey().point);
        			    entry.getKey().write(tmp.toString());
        			}
        			
        			this.handleResetRound();
        			
           			try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
           			++ROUND;
        		}
        		// END GAME
//        		try {
//					handleEndGame();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
        	}
    	}
    }
    
    public void handleEndGame() throws IOException {
    	// send end game info
    	JSONObject reqGameEndJson = new JSONObject();
    	reqGameEndJson.put("event", "CLIENT_GAME_END");
    	List<Player> players = this.gameRoom.getReadyPlayers();
    	Collections.sort(players);
    	if(players.size() == 0) { // all players are eliminated
    		reqGameEndJson.put("winner", "No One");
    	} else {
    		reqGameEndJson.put("winner", players.get(0).name);
    	}
    	
    	for(Player registeredPlayer : this.gameRoom.getRegisteredPlayers()) {
    		registeredPlayer.write(reqGameEndJson.toString());
    	}
    	
    	// clean all players
    	for(Player player : this.gameRoom.getRegisteredPlayers()) {
    		player.close();	
    	}
    	this.gameRoom.hashmapPlayers.clear();
    	this.gameRoom.isFull = false;
    }
    
    public JSONArray handleRanking() {
    	List<Player> players = this.gameRoom.getReadyPlayers();
    	Collections.sort(players);
      	
    	JSONArray rankingArrayJson = new JSONArray();
    	int rank = 1;
    	for(Player player : players) {
    		JSONObject obj = new  JSONObject();
    		obj.put("rank", rank);
    		obj.put("name", player.name);
    		obj.put("point", player.point);
    		rankingArrayJson.put(obj);
    		++rank;
    	}
    	
    	return rankingArrayJson;
    }
    
    public void handleResetRound() {
    	for(Player registeredPlayer : this.gameRoom.getReadyPlayers()) {
    		registeredPlayer.answer = null;
    		registeredPlayer.timestamp = null;
    	}
    }
}
