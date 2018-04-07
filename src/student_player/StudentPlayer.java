package student_player;

import boardgame.Move;
import boardgame.Player;
import tablut.TablutBoardState;
import tablut.TablutMove;
import tablut.TablutPlayer;
import tablut.RandomTablutPlayer;
import tablut.GreedyTablutPlayer;

/** A player file submitted by a student. */
public class StudentPlayer extends TablutPlayer {
    /**
     * You must modify this constructor to return your student number. This is
     * important, because this is what the code that runs the competition uses to
     * associate you with your agent. The constructor should do nothing else.
     */
    public StudentPlayer() {
        super("260618407");
    }

    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */
    public Move chooseMove(TablutBoardState bs) {
        // Is random the best you can do?
        Move myMove = bs.getRandomMove();

        myMove = (MyTools.minimax(bs, 2, Integer.MIN_VALUE, Integer.MAX_VALUE, true)).first();
        
        // however if the Muscovites are going first, always do same first move
        if (player_id == TablutBoardState.MUSCOVITE) {
        	
        	if (bs.getTurnNumber() == 0) {
        		
        		// always start with this move
        		myMove = new TablutMove(7, 4, 7, 3, player_id);
        	}
        }
        
        // Return your move to be processed by the server.
        return myMove;
	}
}