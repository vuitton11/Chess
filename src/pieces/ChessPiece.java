package pieces;

import chess.Board;

/**
 * Super class for each chess piece
 *
 */
public class ChessPiece {
	
	/**
	 * Player (White/Black) attached to the chess piece, empty if a whitespace or blackspace
	 */
	public String player;
	
	/**
	 * Number of times a piece has moved during the game
	 */
	public int moveCount = 0;
	
	/**
	 * Set to true if moveCount == 1 and it moves two spaces, stays true for one turn
	 */
	public boolean en_passant = false;
	
	/**
	 * Initializes a new ChessPiece with player equal to Black, White, or "" if piece is a whitespace or blackspace
	 * @param player "White" or "Black" or "" 
	 */
	public ChessPiece(String player) {
		this.player = player;
	}
	/**
	 * This returns the simple class name of the piece, such as "King","Rook","whitespace", etc.
	 * @return Class name of piece
	 */
	public String getPieceName() {
		return this.getClass().getSimpleName();
	}
	
	/**
	 * This returns the formal piece game code (ex: "bk" for Black King). It examines the
	 * player field of the ChessPiece to determine the color, and uses getPieceName() to
	 * complete the piece code.
	 * @return Piece game code
	 */
	public String toString() {
		String c = this.getPieceName();
		String p = "w";
		if(this.player == "Black") {
			p = "b";
		}
		switch(c) {
			case "Pawn":
				return p+"p";
			case "Bishop":
				return p+"B";
			case "Knight":
				return p+"N";
			case "King":
				return p+"K";
			case "Queen":
				return p+"Q";
			case "Rook":
				return p+"R";
			case "blackspace":
				return "##";
			default:
				return "  ";
		}
	}
	
	
	/**
	 * This function uses logic against each piece and it's movement to determine if the path
	 * from start to end is a valid move for the piece. It checks against the current state of the
	 * board, to also determine if other pieces cause a conflict, making the move invalid.
	 * 
	 * @param player Player name ("White", "Black", or "") at start position of move
	 * @param start Coords object holding the start position of piece
	 * @param end Coords object holding the end position of the move
	 * @param endSpot Player name ("White", "Black", or "") at end position of move
	 * @param game Current game board
	 * @return True if the move is valid, false otherwise
	 */
	public boolean isMoveValid(String player, Coords start, Coords end, String endSpot, Board game) {
		//System.out.println(start.x + "," + start.y  + " -> "+ end.x + "," + end.y); 
		//System.out.println(player);
		//System.out.println((start.x - end.x) +"" + (start.y - end.y));
		//System.out.println("This spot equals = " + endSpot);
			/* If the player tries to move a piece that is not theirs */
			if(player != this.player) {
				return false;
			}
		
			if(this instanceof Bishop) { //Moves diagonal
				if(end.x == start.x || end.y == start.y) { //Invalid - the end point is in vertical or horizontal space
					return false;
				}else if( start.x > end.x && start.y > end.y) { //Moving North West
					for(int i = 1; i < 8; i++) {
						if((start.x - i) < 0 || (start.y - i) < 0) { //Invalid move 
							return false;
						}else if((player != endSpot && endSpot != "") && (start.x - i == end.x && start.y - i == end.y)) { //Checks to see if the endpoint is the enemy's piece
							return true;
						}else if(game.board[start.x - i][start.y - i].player == "White" || game.board[start.x - i][start.y - i].player == "Black") { //End point is blocked by another piece
							return false;
						}else if(start.x - i == end.x && start.y - i == end.y) {
							return true;
						}
					}
				}else if( start.x > end.x && start.y < end.y) { //Moving North East
					for(int i = 1; i < 8; i++) {
						if((start.x - i) < 0 || (start.y + i) > 7) {//Invalid move 
							return false;
						}else if((player != endSpot && endSpot != "") && (start.x - i == end.x && start.y + i == end.y)) { //Checks to see if the endpoint is the enemy's piece
							return true;
						}else if(game.board[start.x - i][start.y + i].player == "White" || game.board[start.x - i][start.y + i].player == "Black") { //End point is blocked by another piece
							return false;
						}else if(start.x - i == end.x && start.y + i == end.y) {
							return true;
						}
					}
				
				}else if( start.x < end.x && start.y < end.y) { //Moving South East
					for(int i = 1; i < 8; i++) {
						if((start.x + i) > 7 || (start.y + i) > 7) {//Invalid move 
							return false;
						}else if((player != endSpot && endSpot != "") && (start.x + i == end.x && start.y + i == end.y)) { //Checks to see if the endpoint is the enemy's piece
							return true;
						}else if(game.board[start.x + i][start.y + i].player == "White" || game.board[start.x + i][start.y + i].player == "Black") { //End point is blocked by another piece
							return false;
						}else if(start.x + i == end.x && start.y + i == end.y) {
							return true;
						}
					}
					
				}else if( start.x < end.x && start.y > end.y) { //Moving South West 
					for(int i = 1; i < 8; i++) {
						if((start.x + i) > 7 || (start.y - i) < 0) {//Invalid move 
							return false;
						}else if((player != endSpot && endSpot != "") && (start.x + i == end.x && start.y - i == end.y)) { //Checks to see if the endpoint is the enemy's piece
							return true;
						}else if(game.board[start.x + i][start.y - i].player == "White" || game.board[start.x + i][start.y - i].player == "Black") { //End point is blocked by another piece
							return false;
						}else if(start.x + i == end.x && start.y - i == end.y) {
							return true;
						}
					}
				}
				
			}else if(this instanceof King) { //Moves in a square by cant move if space is occupied| Special move Castling
				if(Math.abs(start.x - end.x) <= 1 && Math.abs(start.y-end.y) <= 1 && !player.equals(endSpot)) {
					
					return true;
				}
				return false;
			}else if(this instanceof Knight) { //Moves in a L and backwards L shape
				if(start.x - 2 == end.x && start.y - 1 == end.y) { //Case 1
					return true;
				}else if(start.x - 2 == end.x && start.y + 1 == end.y) { //Case 2
					return true;
				}else if(start.x - 1 == end.x && start.y + 2 == end.y) { //Case 3
					return true;
				}else if(start.x + 1 == end.x && start.y + 2 == end.y) { //Case 4
					return true;
				}else if(start.x + 2 == end.x && start.y + 1 == end.y) { //Case 5
					return true;
				}else if(start.x + 2 == end.x && start.y - 1 == end.y) { //Case 6
					return true;
				}else if(start.x + 1 == end.x && start.y - 2 == end.y) { //Case 7
					return true;
				}else if(start.x - 1 == end.x && start.y - 2 == end.y) { //Case 8
					return true;
				}
			}else if(this instanceof Pawn) { //Needs to account for special move En Passant 
				if(player == "White") { //White pawns move down in terms of x
					//Start position and is planing to move forward -> 2 valid moves -> moving up one or two spaces
					if(start.x == 6 && ((start.y - end.y) == 0)) {
						if(((start.x - end.x == 1) || (start.x - end.x == 2)) && endSpot.equals("")) {
							if((start.x - end.x) == 2) {
								if(!game.board[start.x-1][start.y].player.equals("")) {
									return false;
								}
							}
							return true;
						}
					}else if(start.x - end.x == 1 && start.y -end.y == 0 && !endSpot.equals("Black")) { //Is only moving down one space in terms of x
						return true;
					}else if((start.x - end.x == 1) && ((start.y - end.y == 1)||(start.y - end.y == -1))) { //Attacking
						if(endSpot == "Black" || (game.board[end.x+1][end.y].en_passant )) {
							return true;
						}
					}
				}else if(player == "Black") {//Black Pawns move up in terms of x
					if(start.x == 1 && ((start.y - end.y) == 0)) {
						if(((start.x - end.x == -1) || (start.x - end.x == -2)) && endSpot.equals("")) {
							if((start.x - end.x) == -2) {
								if(!game.board[start.x+1][start.y].player.equals("")) {
									return false;
								}
							}
							return true;
						}
					}else if(start.x - end.x == -1 && start.y -end.y == 0 && !endSpot.equals("White")) { //Is only moving down one space in terms of x
						if(end.x == 7) { //Promotion
							
						}
						return true;
					}else if((start.x - end.x == -1) && ((start.y - end.y == 1)||(start.y - end.y == -1))) { //Attacking
						if(endSpot == "White" || (game.board[end.x-1][end.y].en_passant)) {
							return true;
						}
					}
				}
				
			}else if(this instanceof Queen) {
				if((start.x > end.x) && (start.y == end.y)) { //Moving North
					for(int i = 1; i < 8; i++) {
						if(start.x - i < 0) {//Invalid move 
							return false;
						}else if((player != endSpot && endSpot != "") && (start.x - i == end.x)) { //Checks to see if the endpoint is the enemy's piece
							return true;
						}else if(game.board[start.x - i][start.y].player == "White" || game.board[start.x - i][start.y].player == "Black") { //End point is blocked by another piece
							return false;
						}else if(start.x - i == end.x && start.y == end.y) {
							return true;
						}
					}
				}else if((start.x == end.x) && (start.y < end.y)) { //Moving East
					for(int i = 1; i < 8; i++) {
						if(start.y + i > 7) {//Invalid move 
							return false;
						}else if((player != endSpot && endSpot != "") && (start.y + i == end.y)) { //Checks to see if the endpoint is the enemy's piece
							return true;
						}else if(game.board[start.x][start.y + i].player == "White" || game.board[start.x][start.y + i].player == "Black") { //End point is blocked by another piece
							return false;
						}else if(start.x == end.x && start.y + i == end.y) {
							return true;
						}
					}	
				}else if((start.x < end.x) && (start.y == end.y)) { //Moving South
					for(int i = 1; i < 8; i++) {
						if(start.x + i > 7) {//Invalid move 
							return false;
						}else if((player != endSpot && endSpot != "") && (start.x + i == end.x)) { //Checks to see if the endpoint is the enemy's piece
							return true;
						}else if(game.board[start.x + i][start.y].player == "White" || game.board[start.x + i][start.y].player == "Black") { //End point is blocked by another piece
							return false;
						}else if(start.x + i == end.x && start.y == end.y) {
							return true;
						}
					}
				}else if((start.x == end.x) && (start.y > end.y)) { //Moving West
					for(int i = 1; i < 8; i++) {
						if(start.y - i < 0) {//Invalid move 
							return false;
						}else if((player != endSpot && endSpot != "") && (start.y - i == end.y)) { //Checks to see if the endpoint is the enemy's piece
							return true;
						}else if(game.board[start.x][start.y - i].player == "White" || game.board[start.x][start.y - i].player == "Black") { //End point is blocked by another piece
							return false;
						}else if(start.x == end.x && start.y - i == end.y) {
							return true;
						}
					}
				}else if( start.x > end.x && start.y > end.y) { //Moving North West
					for(int i = 1; i < 8; i++) {
						if(start.x - i < 0 || start.y - i < 0) { //Invalid move 
							return false;
						}else if((player != endSpot && endSpot != "") && (start.x - i == end.x && start.y - i == end.y)) { //Checks to see if the endpoint is the enemy's piece
							return true;
						}else if(game.board[start.x - i][start.y - i].player == "White" || game.board[start.x - i][start.y - i].player == "Black") { //End point is blocked by another piece
							return false;
						}else if(start.x - i == end.x && start.y - i == end.y) {
							return true;
						}
					}
				}else if( start.x > end.x && start.y < end.y) { //Moving North East
					for(int i = 1; i < 8; i++) {
						if(start.x - i < 0 || start.y + i > 7) {//Invalid move 
							return false;
						}else if((player != endSpot && endSpot != "") && (start.x - i == end.x && start.y + i == end.y)) { //Checks to see if the endpoint is the enemy's piece
							return true;
						}else if(game.board[start.x - i][start.y + i].player == "White" || game.board[start.x - i][start.y + i].player == "Black") { //End point is blocked by another piece
							return false;
						}else if(start.x - i == end.x && start.y + i == end.y) {
							return true;
						}
					}
				}else if( start.x < end.x && start.y < end.y) { //Moving South East
					for(int i = 1; i < 8; i++) {
						if(start.x + i > 7 || start.y + i > 7) {//Invalid move 
							return false;
						}else if((player != endSpot && endSpot != "") && (start.x + i == end.x && start.y + i == end.y)) { //Checks to see if the endpoint is the enemy's piece
							return true;
						}else if(game.board[start.x + i][start.y + i].player == "White" || game.board[start.x + i][start.y + i].player == "Black") { //End point is blocked by another piece
							return false;
						}else if(start.x + i == end.x && start.y + i == end.y) {
							return true;
						}
					}
					
				}else if( start.x < end.x && start.y > end.y) { //Moving South West 
					for(int i = 1; i < 8; i++) {
						if(start.x + i > 7 || start.y - i < 0) {//Invalid move 
							return false;
						}else if((player != endSpot && endSpot != "") && (start.x + i == end.x && start.y - i == end.y)) { //Checks to see if the endpoint is the enemy's piece
							return true;
						}else if(game.board[start.x + i][start.y - i].player == "White" || game.board[start.x + i][start.y - i].player == "Black") { //End point is blocked by another piece
							return false;
						}else if(start.x + i == end.x && start.y - i == end.y) {
							return true;
						}
					}
				}
			}else if(this instanceof Rook) { //Moves vertical and horizontal
				if(start.x != end.x && start.y != end.y) { //End point isnt in vertical or horizontal point 
					return false;
				}else if((start.x > end.x) && (start.y == end.y)) { //Moving North
					for(int i = 1; i < 8; i++) {
						if(start.x - i < 0) {//Invalid move 
							return false;
						}else if((player != endSpot && endSpot != "") && (start.x - i == end.x)) { //Checks to see if the endpoint is the enemy's piece
							return true;
						}else if(game.board[start.x - i][start.y].player == "White" || game.board[start.x - i][start.y].player == "Black") { //End point is blocked by another piece
							return false;
						}else if(start.x - i == end.x && start.y == end.y) {
							return true;
						}
					}
				}else if((start.x == end.x) && (start.y < end.y)) { //Moving East
					for(int i = 1; i < 8; i++) {
						if(start.y + i > 7) {//Invalid move 
							return false;
						}else if((player != endSpot && endSpot != "") && (start.y + i == end.y)) { //Checks to see if the endpoint is the enemy's piece
							return true;
						}else if(game.board[start.x][start.y + i].player == "White" || game.board[start.x][start.y + i].player == "Black") { //End point is blocked by another piece
							return false;
						}else if(start.x== end.x && start.y + i == end.y) {
							return true;
						}
					}	
				}else if((start.x < end.x) && (start.y == end.y)) { //Moving South
					for(int i = 1; i < 8; i++) {
						if(start.x + i > 7) {//Invalid move 
							return false;
						}else if((player != endSpot && endSpot != "") && (start.x + i == end.x)) { //Checks to see if the endpoint is the enemy's piece
							return true;
						}else if(game.board[start.x + i][start.y].player == "White" || game.board[start.x + i][start.y].player == "Black") { //End point is blocked by another piece
							return false;
						}else if(start.x + i == end.x && start.y == end.y) {
							return true;
						}
					}
				}else if((start.x == end.x) && (start.y > end.y)) { //Moving West
					for(int i = 1; i < 8; i++) {
						if(start.y - i < 0) {//Invalid move 
							return false;
						}else if((player != endSpot && endSpot != "") && (start.y - i == end.y)) { //Checks to see if the endpoint is the enemy's piece
							return true;
						}else if(game.board[start.x][start.y - i].player == "White" || game.board[start.x][start.y - i].player == "Black") { //End point is blocked by another piece
							return false;
						}else if(start.x == end.x && start.y - i == end.y) {
							return true;
						}
					}
				}
			}
			
			return false;
		}
}