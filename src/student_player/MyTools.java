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
    
public static Integer evaluateBoard(TablutBoardState bs, boolean maxPlayer) {
    	Integer score = 0;
    	if(maxPlayer) {
    		if(bs.gameOver()) {
    			return bs.getWinner() == bs.getOpponent() ? Integer.MIN_VALUE : Integer.MAX_VALUE;
    		}
    		if(bs.getTurnPlayer() == TablutBoardState.SWEDE) {
        		score -= 5 * Coordinates.distanceToClosestCorner(bs.getKingPosition());
        	}
        	score += bs.getNumberPlayerPieces(bs.getTurnPlayer()) - bs.getNumberPlayerPieces(bs.getOpponent());
    	}
    	else {
    		if(bs.gameOver()) {
    			return bs.getWinner() == bs.getOpponent() ? Integer.MAX_VALUE : Integer.MIN_VALUE;
    		}
    		score += bs.getNumberPlayerPieces(bs.getOpponent()) - bs.getNumberPlayerPieces(bs.getTurnPlayer());
    	}
    	
    	
    	return score;
   }
    
   	public static Pair<TablutMove, Integer> minimax(TablutBoardState state, int depth, int alpha, int beta, boolean maxPlayer) {
   		if(depth == 2 || state.gameOver()) {
   			Integer score = evaluateBoard(state, maxPlayer);
   			return new Pair<>(null, score);
   		}
   		
   		TablutMove bestMove = null;
   		
   		List<TablutMove> options = state.getAllLegalMoves();
   		if(maxPlayer) {
   			Integer v = Integer.MIN_VALUE;
   			for(TablutMove move : options) {
   				TablutBoardState clone = (TablutBoardState) state.clone();
   				clone.processMove(move);
   				Pair<TablutMove, Integer> bestSoFar = minimax(clone, depth + 1, alpha, beta, false);
   				if (v < bestSoFar.second()) {
   					v = bestSoFar.second();
   					alpha = Math.max(alpha, v);
   					bestMove = move;
   				}
   				if(beta <= alpha) {
   					break;
   				}
   			}
   			if(bestMove == null) {
   				TablutMove random = (TablutMove) state.getRandomMove();
   				return new Pair<>(random, v);
   			}
   			else {
   				return new Pair<>(bestMove, v);
   			}
   		}
   		else {
   			Integer v = Integer.MAX_VALUE;
   			for(TablutMove move : options) {
   				TablutBoardState clone = (TablutBoardState) state.clone();
   				clone.processMove(move);
   				Pair<TablutMove, Integer> score = minimax(clone, depth + 1, alpha, beta, true);
   				if(v > score.second()) {
   					v = score.second();
   					beta = Math.min(beta, v);
   					bestMove = move;
   				}
   				if(beta <= alpha) {
   					break;
   				}
   			}
   			if(bestMove == null) {
   				TablutMove random = (TablutMove) state.getRandomMove();
   				return new Pair<>(random, v);
   			}
   			else {
   				return new Pair<>(bestMove, v);
   			}
   		}
   	}

}
