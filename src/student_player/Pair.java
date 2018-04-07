package student_player;


//class to be able to return a move and its score in minimax
@SuppressWarnings("hiding")
public class Pair<TablutMove, Integer> {
    public final TablutMove a;
    public final Integer b;

    public Pair(TablutMove a, Integer b) {
        this.a = a;
        this.b = b;
    }
    
    public TablutMove first() {
    	return this.a;
    }
    
    public Integer second() {
    	return this.b;
    }
};