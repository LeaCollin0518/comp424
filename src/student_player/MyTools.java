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
    
public static int evaluateBoard(TablutBoardState bs, TablutBoardState clonedBS) {
    	
    	int score = 0;
    	int captureCenter = 0;
    	
    	// get my player id
    	int player_id = clonedBS.getTurnPlayer();
    	    	
    	 // look at opponent and its number of pieces
        int opponent = clonedBS.getOpponent();        
        int numberOfOpponentPieces = clonedBS.getNumberPlayerPieces(opponent);
        // Check if any opponents were captured, move better if we capture more
        
        if(player_id == TablutBoardState.SWEDE) {
        	if(numberOfOpponentPieces < 16) {
        		int numberCaptured = 16 - numberOfOpponentPieces;
        		score += numberCaptured*20;
        	}
        }
        else {
        	if(numberOfOpponentPieces < 9) {
        		int numberCaptured = 9 - numberOfOpponentPieces;
        		score += numberCaptured*30;
        	}
        }
        
        int numberOfPieces = bs.getNumberPlayerPieces(player_id);
        int newNumberOfPieces = clonedBS.getNumberPlayerPieces(player_id);
        
        // penalize board if we lost players
        if (newNumberOfPieces < numberOfPieces) {
        	score -= 50*(numberOfPieces - newNumberOfPieces);
        }

        // will be used for checking if capture is possible
        Coord center = Coordinates.get(4,4);
        Coord startKingPos = bs.getKingPosition();
        Coord endKingPos = clonedBS.getKingPosition();
        
        
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
        
        for (Coord neighbor : centerNeighbors) {
        	if (bs.isOpponentPieceAt(neighbor) && !clonedBS.isOpponentPieceAt(neighbor)) {
        		captureCenter = 50;
        	}
        }
        
    	for (Coord neighbor : cornerNeighbors) {
        	if (bs.isOpponentPieceAt(neighbor) && clonedBS.coordIsEmpty(neighbor)) {
        		score += 200;
        	}
        }
    	
        if(player_id == TablutBoardState.MUSCOVITE) {
            score += captureCenter; 
        } 

        // has the king moved yet?
        else {
        	if(clonedBS.getTurnNumber() > 10 && !endKingPos.equals(center)) {
        		score += 250;
        	}	
        }
        
        try {
	        List<Coord> kingNeighbors = Coordinates.getNeighbors(endKingPos);
		    int moveDistance = Coordinates.distanceToClosestCorner(endKingPos);
		    int kingScore = 0;
		    List<Boolean> presentOpponents = new ArrayList<>();
		    for (Coord neighbor : kingNeighbors) {
		    	if (clonedBS.isOpponentPieceAt(neighbor)) {
		    		presentOpponents.add(true);
		    	}
		    }
		    
		   if (moveDistance < 8 && !cornerNeighbors.contains(endKingPos) && presentOpponents.isEmpty()) {
		        kingScore += 50;
		    }
		    else {
		    	kingScore -= 50;
		    }
		    
		    HashSet<Coord> opponentsAt = clonedBS.getOpponentPieceCoordinates();
		    
		    // check to see if final position is at the edge (but is not a corner) and there is no one in the lane...Swedes win!
		   if ((endKingPos.x == 0 || endKingPos.x == 8 || endKingPos.y == 0 || endKingPos.y == 8) && !cornerNeighbors.contains(endKingPos)) {
		    	for(Coord enemy : opponentsAt) {
	    			if (enemy.x != 0 || enemy.x != 8 || enemy.y != 8 || enemy.y != 8) {
	        			kingScore += 5000;
	        		}
		    	}
		    
		   }
		    
		    if(cornerNeighbors.contains(endKingPos)) {
		    	kingScore = -30000;
		    }
		    if(Coordinates.isCorner(endKingPos)) {
		    	kingScore = 30000;
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
        
        // if the moves leads to winning ... do it
        if (clonedBS.getWinner() == player_id) {
            score = Integer.MAX_VALUE;
        }
    	
		return score;
    	
    }
    
    public static Move minimax(TablutBoardState state, int player) {
    	Move myMove = state.getRandomMove();
    	int depth = 0;
    	int bestScore = Integer.MIN_VALUE;
    	
    	List<TablutMove> options = state.getAllLegalMoves();
    	
    	for(TablutMove move : options) {
    		TablutBoardState cloneBS = (TablutBoardState) state.clone();

            // Process that move, as if we actually made it happen.
            cloneBS.processMove(move);
            int moveScore = MIN(state, cloneBS, depth + 1, player);
            if (moveScore > bestScore) {
            	bestScore = moveScore;
            	myMove = move;
            }
    	}
    	
    	return myMove;
    }
    
    public static int MIN(TablutBoardState state, TablutBoardState clonedBS, int depth, int player) {
    	int bestScore;
    	if(clonedBS.getWinner() == 1 || clonedBS.getWinner() == 0) {
    		if(clonedBS.getWinner() == player) {
    			return Integer.MAX_VALUE;
    		}
    		else {
    			return Integer.MIN_VALUE;
    		}
    	}
    	else if (depth == 2) {
    		return -1*evaluateBoard(state, clonedBS);
    	}
    	else {
    		bestScore = Integer.MAX_VALUE;
    		List<TablutMove> options = clonedBS.getAllLegalMoves();
    		for(TablutMove move : options) {
    			TablutBoardState bs = (TablutBoardState) clonedBS.clone();
    			bs.processMove(move);
    			int moveScore = MAX(clonedBS, bs, depth + 1, player);
    			if(moveScore < bestScore) {
    				bestScore = moveScore;
    			}
    		}
    		
    	}
    	
    	return bestScore;
    }
    
    public static int MAX(TablutBoardState state, TablutBoardState clonedBS, int depth, int player) {
    	int bestScore;
    	if(clonedBS.getWinner() == 1 || clonedBS.getWinner() == 0) {
    		if(clonedBS.getWinner() == player) {
    			return Integer.MAX_VALUE;
    		}
    		else {
    			return Integer.MIN_VALUE;
    		}
    	}
    	else if (depth == 2) {
    		return evaluateBoard(state, clonedBS);
    	}
    	else {
    		bestScore = Integer.MIN_VALUE;
    		List<TablutMove> options = clonedBS.getAllLegalMoves();
    		for(TablutMove move : options) {
    			TablutBoardState bs = (TablutBoardState) clonedBS.clone();
    			bs.processMove(move);
    			int moveScore = MIN(clonedBS, bs, depth + 1, player);
    			if(moveScore > bestScore) {
    				bestScore = moveScore;
    			}
    		}
    		
    	}
    	
    	return bestScore;
    }

}
