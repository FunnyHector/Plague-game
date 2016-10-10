package server.game.player;

/**
 * This class represents a set of geographical information for a player. It
 * contains a pair of (x, y) coordinates, the id number of current area, and a
 * facing direction.
 *
 * @author Hector (Fang Zhao 300364061)
 *
 */
public class Position {

	/**
	 * X coordinate.
	 */
	public final int x;

	/**
	 * Y coordinate.
	 */
	public final int y;

	/**
	 * Which area is this position.
	 */
	public final int areaId;

	/**
	 * The player's facing direction
	 */
	private Direction direction;

	/**
	 * Constructor
	 * 
	 * @param x
	 *            --- x
	 * @param y
	 *            --- y
	 * @param areaId
	 *            --- areaId
	 * @param direction
	 *            --- direction
	 */
	public Position(int x, int y, int areaId, Direction direction) {
		this.x = x;
		this.y = y;
		this.areaId = areaId;
		this.direction = direction;
	}

	/**
	 * Get the direction
	 * 
	 * @return --- the direction.
	 */
	public Direction getDirection() {
		return direction;
	}

	/**
	 * Set the direction
	 * 
	 * @param direction
	 *            --- the new direction
	 */
	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	/**
	 * Get the position in front of this position. Direction remain unchanged.
	 * 
	 * @return --- The position in front of this position. Direction remain
	 *         unchanged.
	 */
	public Position frontPosition() {
		switch (direction) {
		case East:
			return new Position(x + 1, y, areaId, direction);
		case North:
			return new Position(x, y - 1, areaId, direction);
		case South:
			return new Position(x, y + 1, areaId, direction);
		case West:
			return new Position(x - 1, y, areaId, direction);
		default:
			return null; // dead code
		}
	}

	/**
	 * Get the position at back of this position. Direction remain unchanged.
	 * 
	 * @return --- The position at back of this position. Direction remain
	 *         unchanged.
	 */
	public Position backPosition() {
		switch (direction) {
		case East:
			return new Position(x - 1, y, areaId, direction);
		case North:
			return new Position(x, y + 1, areaId, direction);
		case South:
			return new Position(x, y - 1, areaId, direction);
		case West:
			return new Position(x + 1, y, areaId, direction);
		default:
			return null; // dead code
		}
	}

	/**
	 * Get the position on the left of this position. Direction remain
	 * unchanged.
	 * 
	 * @return --- The position on the left of this position. Direction remain
	 *         unchanged.
	 */
	public Position leftPosition() {
		switch (direction) {
		case East:
			return new Position(x, y - 1, areaId, direction);
		case North:
			return new Position(x - 1, y, areaId, direction);
		case South:
			return new Position(x + 1, y, areaId, direction);
		case West:
			return new Position(x, y + 1, areaId, direction);
		default:
			return null; // dead code
		}
	}

	/**
	 * Get the position on the right of this position. Direction remain
	 * unchanged.
	 * 
	 * @return --- The position on the right of this position. Direction remain
	 *         unchanged.
	 */
	public Position rightPosition() {
		switch (direction) {
		case East:
			return new Position(x, y + 1, areaId, direction);
		case North:
			return new Position(x + 1, y, areaId, direction);
		case South:
			return new Position(x - 1, y, areaId, direction);
		case West:
			return new Position(x, y - 1, areaId, direction);
		default:
			return null; // dead code
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + areaId;
		result = prime * result + ((direction == null) ? 0 : direction.hashCode());
		result = prime * result + x;
		result = prime * result + y;
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
		Position other = (Position) obj;
		if (areaId != other.areaId)
			return false;
		if (direction != other.direction)
			return false;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "[" + areaId + ", " + x + ", " + y + ", " + direction + "]";
	}
}
