package pieces;

/**
 * Class representation of a Pawn, that extends the superclass 'ChessPiece'
 *
 */
public class Pawn extends ChessPiece{
	
	/**
	 * Initializes a new Pawn object with a player assignment
	 * @param player "Black", "White", or "" to assign player to the piece
	 */
	public Pawn(String player) {
		super(player);
	}
}
