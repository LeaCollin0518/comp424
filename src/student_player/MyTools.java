package student_player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import boardgame.Move;
import coordinates.Coord;
import coordinates.Coordinates;
import tablut.TablutBoardState;
import tablut.TablutMove;

public class MyTools {
    
public static Integer evaluateBoard(TablutBoardState bs, boolean maxPlayer, int player) {
    	
    	int score = 0;
    	int captureCenter = 0;
    	int player_id;
    	int opponent;
    	
    	// get my player id
    	if(maxPlayer) {
    		player_id = player;
    		opponent = 1 - player;
    	}
    	else {
    		player_id = 1 - player;
    		opponent = player;
    	}
      
        int numberOfOpponentPieces = bs.getNumberPlayerPieces(opponent);
        // Check if any opponents were captured, move better if we capture more
        
        int numberOfPieces = bs.getNumberPlayerPieces(player_id);
        
        if(player_id == TablutBoardState.SWEDE) {
        	if(numberOfOpponentPieces < 16) {
        		int numberCaptured = 16 - numberOfOpponentPieces;
        		score += numberCaptured*30;
        	}
        	if(numberOfPieces < 9) {
        		score -= (9-numberOfPieces)*10;
        	}
        }
        else {
        	if(numberOfOpponentPieces < 9) {
        		int numberCaptured = 9 - numberOfOpponentPieces;
        		score += numberCaptured*30;
        	}
        	if(numberOfPieces < 16) {
        		score -= (16-numberOfPieces)*10;
        	}
        }
        

        // will be used for checking if capture is possible
        Coord center = Coordinates.get(4,4);
        
        Coord kingPos = bs.getKingPosition();
        
        List<Coord> corners = Coordinates.getCorners();
        List<Coord> centerNeighbors = Coordinates.getNeighbors(center);
        List<Coord> cornerNeighbors = new ArrayList<>();
        
        for (Coord corner : corners) {
        	for (Coord neighbor : Coordinates.getNeighbors(corner)) {
        		cornerNeighbors.add(neighbor);
        	}
        }

        
        
        // for all positions next to the center, see if you can get rid of an enemy
        //  (prioritize this over other kinds of captures)
        
        /*for (Coord neighbor : centerNeighbors) {
        	if (!bs.isOpponentPieceAt(neighbor)) {
        		captureCenter = 50;
        	}
        }*/
        
    	for (Coord neighbor : cornerNeighbors) {
        	if (bs.coordIsEmpty(neighbor)) {
        		score += 200;
        	}
        }
    	
        if(player_id == TablutBoardState.MUSCOVITE) {
            score += captureCenter; 
        } 

        // has the king moved yet?
        else if(bs.getTurnNumber() > 12 && !kingPos.equals(center)) {
        		score += 250;
        }
        if(player_id == TablutBoardState.SWEDE && bs.getTurnNumber() > 20) {
        	for(Coord centerNeighbor : centerNeighbors) {
        		if (!kingPos.equals(center) && !kingPos.equals(centerNeighbor)) {
        			score += 500;
        		}
        	}
        }
        if(player_id == TablutBoardState.SWEDE && bs.getTurnNumber() > 30) {
        	score += 1000;
        }

        
        try {
	        List<Coord> kingNeighbors = Coordinates.getNeighbors(kingPos);
		    int moveDistance = Coordinates.distanceToClosestCorner(kingPos);
		    int kingScore = 0;
		    List<Boolean> presentOpponents = new ArrayList<>();
		    for (Coord neighbor : kingNeighbors) {
		    	if (bs.isOpponentPieceAt(neighbor)) {
		    		presentOpponents.add(true);
		    	}
		    }
		    
		    if (moveDistance < 8 && !cornerNeighbors.contains(kingPos) && presentOpponents.isEmpty()) {
		        kingScore += 50;
		    }
		    else if(presentOpponents.size() > 1){
		    	kingScore -= 1000;
		    }
		    else if (presentOpponents.size() == 1){
		    	kingScore -= 200;
		    }
		    
		    HashSet<Coord> opponentsAt = bs.getOpponentPieceCoordinates();
		    HashSet<Coord> myPiecesAt = bs.getPlayerPieceCoordinates();
		    List<Boolean> myPiecesPresent = new ArrayList<>();
		    
		    for(Coord myPiece : myPiecesAt) {
		    	for (Coord neighbor : kingNeighbors) {
		    		if(neighbor.equals(myPiece)) {
		    			myPiecesPresent.add(true);
		    		}
		    	}
		    	if(myPiecesPresent.size() >= 3 && kingPos.x == 0 || kingPos.x == 8 || kingPos.y == 0 || kingPos.x == 8) {
		    		kingScore -= 1000;
		    	}
		    }
		    
		    int opponentsAtEdge = 0;
		    // check to see if final position is at the edge (but is not a corner) and there is no one in the lane...Swedes win!
		   if ((kingPos.x == 0 || kingPos.x == 8 || kingPos.y == 0 || kingPos.y == 8) && !cornerNeighbors.contains(kingPos)) {
		    	for(Coord enemy : opponentsAt) {
	    			if (enemy.x != 0 || enemy.x != 8 || enemy.y != 8 || enemy.y != 8) {
	        			kingScore += 5000;
	        		}
	    			else if (enemy.x == 0 || enemy.x == 8 || enemy.y == 8 || enemy.y == 8){
	    				opponentsAtEdge ++;
	    			}
	    			else if(cornerNeighbors.contains(kingPos)) {
	    		    	kingScore = -30000;
	    		    }
	    			if(opponentsAtEdge >= 2) {
	    				System.out.println("poop");
	    				kingScore -= 5000;
	    			}
		    	}
		    
		   }
		   
		    
		    
		    if(player_id == TablutBoardState.SWEDE) {
		    	score += kingScore;
		    }
		    
		    else {
		    	score -= kingScore;
		    }
		    
		    }
        catch(Exception e) {
        	
        }
        
        if (bs.getWinner() == player_id) {
            score = Integer.MAX_VALUE;
        }
    	
        if(maxPlayer) {
        	return score;
        }
        else {
        	return -1*score;
        }
		
    	
    }
    
   	public static Pair<TablutMove, Integer> minimax(TablutBoardState state, int depth, int alpha, int beta, boolean maxPlayer, int player) {
   		if(depth == 2 || state.gameOver()) {
   			Integer score = evaluateBoard(state, maxPlayer, player);
   			return new Pair<>(null, score);
   		}
   		
   		TablutMove bestMove = null;
   		
   		List<TablutMove> options = state.getAllLegalMoves();
   		if(maxPlayer) {
   			Integer v = Integer.MIN_VALUE;
   			for(TablutMove move : options) {
   				TablutBoardState clone = (TablutBoardState) state.clone();
   				clone.processMove(move);
   				Pair<TablutMove, Integer> bestSoFar = minimax(clone, depth + 1, alpha, beta, false, player);
   				if (v < bestSoFar.second()) {
   					v = bestSoFar.second();
   					alpha = Math.max(alpha, v);
   					bestMove = move;
   				}
   				if(beta <= alpha) {
   					break;
   				}
   			}
   			return new Pair<>(bestMove, v);
   		}
   		else {
   			Integer v = Integer.MAX_VALUE;
   			for(TablutMove move : options) {
   				TablutBoardState clone = (TablutBoardState) state.clone();
   				clone.processMove(move);
   				Pair<TablutMove, Integer> score = minimax(clone, depth + 1, alpha, beta, true, player);
   				if(v > score.second()) {
   					v = score.second();
   					beta = Math.min(beta, v);
   					bestMove = move;
   				}
   				if(beta <= alpha) {
   					break;
   				}
   			}
   			return new Pair<>(bestMove, v);
   		}
   	}

}
