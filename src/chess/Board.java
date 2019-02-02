package chess;

import pieces.*;

/**
 * Class representation of the game board for chess
 *
 */
public class Board {
	/**
	 * Representation of game board as a 2d array of ChessPiece objects
	 */
	public ChessPiece[][] board;
	
	/**
	 * Initializes the game board by first laying all the white and black tiles in their correct positions.
	 * Then fills in the white and black pieces at their start positions.
	 */
	public void initialize() {
		
		/*	Initialize Board
		 *  with 
		 *  black/white tiles	*/
		board = new ChessPiece[8][8];
		layTiles();
		
		/*	  Lay black pieces	  */
		board[0][0] = new Rook("Black");
		board[0][1] = new Knight("Black");
		board[0][2] = new Bishop("Black");
		board[0][3] = new Queen("Black");
		board[0][4] = new King("Black");
		board[0][5] = new Bishop("Black");
		board[0][6] = new Knight("Black");
		board[0][7] = new Rook("Black");
		for(int i=0;i<8;i++) {
			board[1][i] = new Pawn("Black");
		}
		
		/*	  Lay white pieces	  */
		board[7][0] = new Rook("White");
		board[7][1] = new Knight("White");
		board[7][2] = new Bishop("White");
		board[7][3] = new Queen("White");
		board[7][4] = new King("White");
		board[7][5] = new Bishop("White");
		board[7][6] = new Knight("White");
		board[7][7] = new Rook("White");
		for(int i=0;i<8;i++) {
			board[6][i] = new Pawn("White");
		}
	}
	
	/**
	 * Prints the 2d array using each piece's 2 character long game code using ChessPiece.toString()
	 * Prints the pieces, tiles, and border file and rank value indicators for the player to index the board
	 */
	public void printGame() {
		for(int i=0;i<8;i++) {
			for(int j=0;j<8;j++) {
				System.out.print(board[i][j].toString()+" ");
			}
			System.out.println(8-i);
		}
		System.out.println(" a  b  c  d  e  f  g  h");
	}
	
	/**
	 * Considers each tile on the board, and determines that if the value of the row + column is even,
	 * then the space is a whitespace, otherwise a blackspace.
	 */
	private void layTiles() {
		for(int i=0;i<8;i++) {
			for(int j=0;j<8;j++) {
				int tileVal = i + j;
				if(tileVal % 2 == 0) {
					board[i][j] = new whitespace();
				}else {
					board[i][j] = new blackspace();
				}
			}
		}
	}
}
