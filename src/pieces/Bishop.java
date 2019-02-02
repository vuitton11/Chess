package pieces;

/**
 * Class representation of a Bishop, that extends the superclass 'ChessPiece'
 *
 */
public class Bishop extends ChessPiece{
	
	/**
	 * Initializes a new Bishop object with a player assignment
	 * @param player "Black", "White", or "" to assign player to the piece
	 */
	public Bishop(String player) {
		super(player);
	}
}
