package student_player;

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