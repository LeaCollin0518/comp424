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

        myMove = MyTools.minimax(bs, bs.getTurnPlayer());
        
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
        

	// For Debugging purposes only.
    public static void main(String[] args) {
    	int i = 0;
    	int numWins = 0;
    	while(i < 100) {
    		TablutBoardState b = new TablutBoardState();
            Player swede = new StudentPlayer();
            swede.setColor(TablutBoardState.SWEDE);

            Player muscovite = new GreedyTablutPlayer();
            muscovite.setColor(TablutBoardState.MUSCOVITE);
            
            Player player = muscovite;
            while (!b.gameOver()) {
                Move m = player.chooseMove(b);
                b.processMove((TablutMove) m);
                player = (player == muscovite) ? swede : muscovite;
                //System.out.println("\nMOVE PLAYED: " + m.toPrettyString());
                //b.printBoard();
            }
            if(b.getWinner() == 1) {
            	System.out.println("Game: " + i);
            	System.out.println("Number of moves: " + b.getTurnNumber());
            	numWins++;
            }
            //System.out.println(TablutMove.getPlayerName(b.getWinner()) + " WIN!");
            i++;
    	}
    	System.out.println("Number of wins: " + numWins);
        
    }
}