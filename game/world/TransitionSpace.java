package game.world;

import game.player.Direction;
import game.player.Position;

/**
 * A space in the map, on which the player must be standing in order to enter a
 * destination Area.
 * 
 * @author Daniel Anastasi 300145878, Hector (Fang Zhao 300364061)
 *
 */
public class TransitionSpace extends GroundSpace {

    /**
     * The position of this object in the area.
     */
    private final Position currentPosition;

    /**
     * The position in the destination area from this space.
     */
    private final Position destPosition;

    /**
     * The direction which the player must travel from this space to enter the destination
     * area.
     */
    private final Direction direction;

    /**
     * @param The
     *            position of this object on the map.
     * @param The
     *            destination position in the destination area.
     * @param Direction
     *            to face in order to enter destination area.
     */
    public TransitionSpace(Position pos, Position destPos, Direction direction) {
        this.currentPosition = pos;
        this.destPosition = destPos;
        this.direction = direction;
    }

    /**
     * 
     * @return
     */
    public Position getDestination() {
        return destPosition;
    }

    /**
     * 
     * @return
     */
    public Direction getDirection() {
        return direction;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((destPosition == null) ? 0 : destPosition.hashCode());
        result = prime * result + ((direction == null) ? 0 : direction.hashCode());
        result = prime * result
                + ((currentPosition == null) ? 0 : currentPosition.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TransitionSpace other = (TransitionSpace) obj;
        if (destPosition == null) {
            if (other.destPosition != null)
                return false;
        } else if (!destPosition.equals(other.destPosition))
            return false;
        if (direction != other.direction)
            return false;
        if (currentPosition == null) {
            if (other.currentPosition != null)
                return false;
        } else if (!currentPosition.equals(other.currentPosition))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "T";
    }
}
