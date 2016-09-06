package game.world;

/**
 * This abstract class represents a position on map.
 * 
 * @author Hector (Fang Zhao 300364061)
 *
 */
public abstract class Position {

    private final int x;
    private final int y;
    
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }
}