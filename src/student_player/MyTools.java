package student_player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import coordinates.Coord;
import coordinates.Coordinates;
import tablut.TablutBoardState;
import tablut.TablutMove;

public class MyTools {
    
public static Integer evaluateBoard(TablutBoardState bs, boolean maxPlayer) {
		// score of current board to be returned to minimax
    	Integer score = 0;
    	
    	// get the player for this board, the opponent, and number of pieces for each
    	int player_id = bs.getTurnPlayer();
		int opponent = bs.getOpponent();
		int numOpponents = bs.getNumberPlayerPieces(opponent);
		int numPieces = bs.getNumberPlayerPieces(player_id);
		
		Coord center = Coordinates.get(4,4);
		List<Coord> centerNeighbors = Coordinates.getNeighbors(center);
		List<Coord> corners = Coordinates.getCorners();
		List<Coord> cornerNeighbors = new ArrayList<>();
		
		for(Coord corner : corners) {
			for(Coord cornerNeighbor : Coordinates.getNeighbors(corner)) {
				cornerNeighbors.add(cornerNeighbor);
			}
		}
			
		//strategy for the swedes
		if(bs.getTurnPlayer() == TablutBoardState.SWEDE) {
    		
			// good to capture opponents
    		if(numOpponents < 16) {
    			int numCaptured = 5*(16 - numOpponents);
    			score += numCaptured;
    		}
    		// bad to have lost players
    		if(numPieces < 9) {
    			score -= 3*(9 - numPieces);
    		}
    		try {
    			// good if we have gotten closer to a corner
    			Coord kingPos = bs.getKingPosition();
    			score -= 5 * Coordinates.distanceToClosestCorner(kingPos);

    			//if the king moved after a certain number of turns
    			if(bs.getTurnNumber() > 10 && !kingPos.equals(center)) {
    				score += 5;
    			}
    			if(bs.getTurnNumber() > 20 && !kingPos.equals(center) && !centerNeighbors.contains(kingPos)) {
    				score += 5;
    			}
    			
    			// is the king near any enemies, if so, how many?
    			List<Coord> kingNeighbors = Coordinates.getNeighbors(kingPos);
    			List<Boolean> opponentPresent = new ArrayList<>();
    			if(!kingPos.equals(center)) {
    				for (Coord kingNeighbor : kingNeighbors) {
        				if(bs.isOpponentPieceAt(kingNeighbor)) {
        					opponentPresent.add(true);
        				}
        			}
        			if(opponentPresent.size() == 2) {
        				score -= 5;
        			}
        			else if(opponentPresent.size() == 3) {
        				score -= 5;
        			}
    			}
    			
    			/*HashSet<Coord> opponentLocations = bs.getOpponentPieceCoordinates();
    			List<Boolean> opponentAtEdge = new ArrayList<>();
    			if(kingPos.x == 0 || kingPos.x == 8 || kingPos.y == 0 || kingPos.y == 8){
    				for(Coord enemy : opponentLocations) {
    					if(enemy.x == 0 || enemy.x == 8 || enemy.y == 0 || enemy.y == 8) {
    						opponentAtEdge.add(false);
    					}
    				}
    				if(opponentAtEdge.contains(true)) {
    					for(boolean b : opponentAtEdge) {
    						System.out.println(b);
    					}
    				}
    			}*/
    			
    			
    			
    		}
    		catch(Exception e) {
    			score += 0;
    		}
    		
    	}
		// strategy for the muscovites
		else {
			// more aggressive capturing strategy
			if(numOpponents < 9) {
				int numCaptured = 8*(9 - numOpponents);
				score += numCaptured;
			}
			// still bad to lose players
			if(numPieces < 16) {
				score -=5*(16 - numPieces);
			}
			
			// don't hang around the corners too much
			for(Coord cornerNeighbor : cornerNeighbors) {
				if(!bs.isOpponentPieceAt(cornerNeighbor) && !bs.coordIsEmpty(cornerNeighbor)) {
					score -= 10;
				}
			}	
			
			try {
				// the closer the king is to a corner, the worse
    			Coord kingPos = bs.getKingPosition();
    			score -= 5 * (8 - Coordinates.distanceToClosestCorner(kingPos));

    			//is the king near any enemies, if so, how many?
    			List<Coord> kingNeighbors = Coordinates.getNeighbors(kingPos);

    			// is there a muscovite next to the king? good
				for (Coord kingNeighbor : kingNeighbors) {
    				if(!bs.isOpponentPieceAt(kingNeighbor) && !bs.coordIsEmpty(kingNeighbor)) {
    					score += 10;
    				}
    			}
    		}
    		catch(Exception e) {
    			score += 0;
    		}
		}
		
    	if(maxPlayer) {
    		// check if the game leads to a win
    		if(bs.gameOver()) {
    			return bs.getWinner() == bs.getTurnPlayer() ? Integer.MAX_VALUE : Integer.MIN_VALUE;
    		}
    		return score;
    	}
    	else {
    		if(bs.gameOver()) {
    			return bs.getWinner() == bs.getTurnPlayer() ? Integer.MIN_VALUE : Integer.MAX_VALUE;
    		}
    		// return negative of score if min player, more negative ---> better
    		return -1*score;
    	}
    	
    	
    	
   }
    
	/*
	 * my implementation of minimax with alpha beta pruning
	 * returns the best move along with its score
	 */
   	public static Pair<TablutMove, Integer> minimax(TablutBoardState state, int depth, int alpha, int beta, boolean maxPlayer) {
   		
   		// if we've reached the maximum depth or the game is over, finally evaluate the board
   		if(depth == 0 || state.gameOver()) {
   			Integer score = evaluateBoard(state, maxPlayer);
   			return new Pair<>(null, score);
   		}
   		
   		TablutMove bestMove = null;
   		
   		List<TablutMove> options = state.getAllLegalMoves();
   		
   		if(maxPlayer) {
   			// iterate through all the possible moves from the current state
   			for(TablutMove move : options) {
   				TablutBoardState clone = (TablutBoardState) state.clone();
   				// process the move as if it actually happened, rest follows minimax algo
   				clone.processMove(move);
   				Pair<TablutMove, Integer> score = minimax(clone, depth - 1, alpha, beta, false);
   				if (score.second() > alpha) {
   					alpha = score.second();
   					bestMove = move;
   				}
   				if(alpha >= beta) {
   					return new Pair<>(bestMove, alpha);
   				}
   			}
   			if(bestMove == null) {
   				TablutMove random = (TablutMove) state.getRandomMove();
   				return new Pair<>(random, alpha);
   			}
   			else {
   				return new Pair<>(bestMove, alpha);
   			}
   		}
   		else {
   			for(TablutMove move : options) {
   				TablutBoardState clone = (TablutBoardState) state.clone();
   				clone.processMove(move);
   				Pair<TablutMove, Integer> score = minimax(clone, depth - 1, alpha, beta, true);
   				if(score.second() < beta) {
   					beta = score.second();
   					bestMove = move;
   				}
   				if(beta <= alpha) {
   					return new Pair<>(bestMove, beta);
   				}
   			}
   			if(bestMove == null) {
   				TablutMove random = (TablutMove) state.getRandomMove();
   				return new Pair<>(random, beta);
   			}
   			else {
   				return new Pair<>(bestMove, beta);
   			}
   		}
   	}

}
