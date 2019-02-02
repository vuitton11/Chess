package pieces;

/**
 * This class is to store x,y (row, col) coordinates to reference the game board
 *
 */
public class Coords {
	/**
	 * Row position in game board
	 */
	public int x;
	
	/**
	 * Column position in game board
	 */
	public int y;
	
	/**
	 * Initializes a Coords object with x,y positions
	 * @param x Row position in game board
	 * @param y Column position in game board
	 */
	public Coords(int x, int y) {
		this.x = x;
		this.y = y;
	}
}
