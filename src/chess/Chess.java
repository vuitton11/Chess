package chess;

import java.util.ArrayList;
import java.util.Scanner;

import pieces.*;



/**
 * This class is to run the chess game. The game is initialized with the starting player as White, and the opponent as Black
 * Input for each move can be up to 3 arguments separated by spaces.
 * Game will continue as long as a checkmate does not exist, or a player resigns, or both players agree to draw.
 *
 *
 */
public class Chess {
	/**
	 * Board instance for the current game
	 */
	public static Board game = new Board();
	
	/**
	 * The current player who's turn it is
	 */
	public static String currentPlayer = "White";
	
	/**
	 * The player who's turn it is current not
	 */
	public static String opponent = "Black";
	
	/**
	 * Set to true if one player asks for a draw. If the other player does not respond positively on the next turn,
	 * the value is set back to false
	 */
	public static boolean draw_initialized = false;
	
	/**
	 * ChessPiece which the opponent has one move to attack through en passant
	 * currentEnPassant is removed/changed after every turn
	 */
	public static ChessPiece currentEnPassant = null;
	
	public static void main(String[] args) {
		game.initialize();
		
		boolean GAME_OVER = false;
		Scanner scan = new Scanner(System.in);
	
		
		while(!GAME_OVER) {
			game.printGame();
			if(CheckForCheck(opponent)) {
				if(!getOutOfCheck(currentPlayer)) {
					System.out.println("Checkmate");
					System.out.println(opponent + " wins");
					System.exit(0);
				}else {
					System.out.println("Check");
				}
			}
			
			boolean validMove = false;
			while(!validMove){
				System.out.println();
				System.out.print(currentPlayer + "'s move: ");
				String input = scan.nextLine();
				String[] split_input = input.split(" ");
				
				String extraInput="";
				String targetLocation="";
				String pieceLocation = split_input[0];
				if(split_input[0].equals("resign")) {
					System.out.println(opponent + " wins");
					System.exit(0);
				}
				if(split_input.length > 1) {
					targetLocation = split_input[1];
				}
				if(split_input.length > 2) {
					extraInput = split_input[2];
				}
				
				if(!split_input[0].equals("draw") && draw_initialized) {
					draw_initialized = false;
				}else if(extraInput.equals("draw?")) {
					draw_initialized = true;
				}else if(split_input[0].equals("draw") && draw_initialized) {
					System.out.println("draw");
					System.exit(0);
				}
				
				
				Coords pieceCoords = FileRankToCoords(pieceLocation);
				Coords targetCoords = FileRankToCoords(targetLocation);
				
				ChessPiece piece = pieceAt(pieceCoords);
				
				validMove = verifyMove(piece, pieceCoords, targetCoords);
			
				/* Pre-conditions to validate castling attempt */
				if(piece.getPieceName().equals("King") && piece.moveCount == 0
						&& pieceCoords.x == targetCoords.x && Math.abs(pieceCoords.y-targetCoords.y) == 2) {
					validMove = checkForCastling(piece, pieceCoords, targetCoords);
				}
				
				if(validMove) {
					/* If Checkmate */
			
					if(pieceAt(targetCoords).getPieceName().equals("King")) {
						MoveChessPiece(pieceCoords, targetCoords);
						game.printGame();
						System.out.println(currentPlayer + " wins");
						GAME_OVER = true;
						continue;
					}
					MoveChessPiece(pieceCoords, targetCoords);
					piece.moveCount++;
					if(pieceAt(targetCoords).getPieceName().equals("Pawn") && targetCoords.x != 0 && targetCoords.x != 7){
						Coords spaceBelow = new Coords(targetCoords.x+1,targetCoords.y);
						Coords spaceAbove = new Coords(targetCoords.x-1, targetCoords.y);
						if(pieceAt(spaceBelow) == currentEnPassant) {
							FillSpace(spaceBelow);
						}else if(pieceAt(spaceAbove) == currentEnPassant) {
							FillSpace(spaceAbove);
						}
					}
					
					if(currentEnPassant != null) {
						currentEnPassant.en_passant = false;
						currentEnPassant = null;
					}
					
					if(piece.getPieceName().equals("Pawn") & piece.moveCount == 1 && targetCoords.x == 3 || targetCoords.x == 4) {
						piece.en_passant = true;
						currentEnPassant = piece;
					}
					
					if(pieceAt(targetCoords).getPieceName().equals("Pawn") && (targetCoords.x == 0 || targetCoords.x == 7)) {
							applyPromotion(targetCoords, extraInput);
					}
				}else{
					System.out.println("Illegal move, try again");
				}
			}	
			System.out.println();
			ChangePlayer();
		}
		scan.close();
		System.exit(0);
	}
	
	/**
	 * This method will promote a pawn, with the value of 's' determining the piece the Pawn will be promoted to.
	 * On default, if 's' is blank, the Pawn will be promoted to a Queen
	 * 
	 * @param c Coords object with position of Pawn to be promoted
	 * @param s Single character string indicating the piece the player wants to promote their pawn to
	 * 
	 */
	public static void applyPromotion(Coords c, String s) {
		switch(s) {
		case "N":
			game.board[c.x][c.y] = new Knight(currentPlayer);
			break;
		case "R":
			game.board[c.x][c.y] = new Rook(currentPlayer);
			break;
		case "B":
			game.board[c.x][c.y] = new Bishop(currentPlayer);
			break;
		default:
			game.board[c.x][c.y] = new Queen(currentPlayer);
			break;
		}
	}

	/**
	 * Returns the ChessPiece object at the position of Coords c
	 * @param c Coords object holding the position of the piece you want to retrieve
	 * @return ChessPiece at position c.x,c.y
	 */
	public static ChessPiece pieceAt(Coords c) {
		return game.board[c.x][c.y];
	}
	
	/**
	 * Determines whether or not a castling attempt is valid
	 * @param p King being moved
	 * @param start start position of piece
	 * @param destination end position of piece
	 * @return boolean
	 */
	public static boolean checkForCastling(ChessPiece p, Coords start, Coords destination) {
		int row = start.x;
		int col1 = start.y;
		int col2 = destination.y;
		
		int rookColumn = col1 > col2? 0 : 7;
		int rookMoveCol = rookColumn == 0? 3 : 5;
		ChessPiece r = game.board[row][rookColumn];
		if(!r.getPieceName().equals("Rook") || !r.player.equals(currentPlayer)
				|| r.moveCount > 0 || !hasClearPath(p,start,new Coords(row, Math.abs(rookColumn - 1)))){
			return false;
		}
		
		ArrayList<Coords> path = buildLinearPath(start, new Coords(row, col2));
		
		/*	Cannot escape Check */
		if(CheckForCheck(opponent)) {
			return false;
		}
		
		/*	Cannot move through or into a check */
		for(int i = 0; i < path.size(); i++) {
			Coords c = path.get(i);
			/* Check if path is blocked*/
			if(!pieceAt(c).player.equals("")) {
				return false;
			}
			
			if(isSelfDestructive(start, c)) {
				return false;
			}
		}
		
		MoveChessPiece(new Coords(row, rookColumn), new Coords(row, rookMoveCol));
		return true;
	}
	
	/**
	 * Verifies that the move specified by the player is valid
	 * @param piece piece to move
	 * @param start position of piece
	 * @param destination where you want to move piece to
	 * @return true if move is verified, false otherwise and the move will not occur 
	 */
	public static boolean verifyMove(ChessPiece piece, Coords start, Coords destination) {
		if(piece.isMoveValid(currentPlayer, start, destination,pieceAt(destination).player, game) && hasClearPath(piece, start,destination)){
			return !isSelfDestructive(start, destination);
		}
		return false;
	}
	/**
	 * Determines if this move puts the player's own king in check
	 * @param start chess piece's starting position
	 * @param destination chess piece's destination
	 * @return true if this move will result in placing the player's own king in check, otherwise false
	 */
	public static boolean isSelfDestructive(Coords start, Coords destination) {
		ChessPiece temp = pieceAt(destination);
		MoveChessPiece(start, destination);
		boolean check = CheckForCheck(opponent);
		MoveChessPiece(destination, start);
		game.board[destination.x][destination.y] = temp;
		return check;
	}
	
	/**
	 * Determines whether or not the path from start to destination is blocked by one of the player's own pieces
	 * @param piece ChessPiece being moved
	 * @param start start position of piece
	 * @param destination end position of piece
	 * @return false if the path from start to destination is blocked by one of the player's own pieces, otherwise true
	 */
	public static boolean hasClearPath(ChessPiece piece, Coords start, Coords destination) {
		ArrayList<Coords> path = GetMovementPath(piece, start, destination);
		for(int k = 0; k < path.size(); k++) {
			ChessPiece pieceOnPath = pieceAt(path.get(k));
			/* If a player has a piece of theirs blocking the path */
			if(pieceOnPath.player.equals(currentPlayer)) {
				return false;
			}
		}
		return true;	
	}
	
	
	/**
	 * Generates the movement path as a linear path from start to destination
	 * @param piece chess piece being moved
	 * @param start start position of piece
	 * @param destination end position of piece
	 * @return List of coordinates of the linear path from start to destination
	 */
	public static ArrayList<Coords> GetMovementPath(ChessPiece piece, Coords start, Coords destination){
		ArrayList<Coords> path = new ArrayList<Coords>();
		String name = piece.getPieceName();
		
		if(name.equals("Queen") || name.equals("Rook") || name.equals("Bishop") || name.equals("Pawn")) {
			path = buildLinearPath(start,destination);
		}else {
			path.add(destination);
		}
		return path;
	}
	
	/**
	 * Builds a list of (row,col) pairs that is a linear path from start to destination
	 * @param start start position
	 * @param destination end position
	 * @return Coords ArrayList holding a linear path from start to destination
	 */
	public static ArrayList<Coords> buildLinearPath(Coords start, Coords destination){
		ArrayList<Coords> path = new ArrayList<Coords>();
		
		int x1 = start.x;
		int y1 = start.y;
		int x2 = destination.x;
		int y2= destination.y;
		
		
		int deltaX = x1 > x2? -1 : 1;;
		int deltaY = y1 > y2? -1 : 1;;

		/*Horizontal Path */
		if(y1 == y2) {
			do {
				x1 += deltaX;
				path.add(new Coords(x1, y1));
			}while(x1 != x2);
			
		}	/* Vertical Path */
		else if(x1 == x2) {
			do {
				y1 += deltaY;
				path.add(new Coords(x1, y1));
			}while(y1 != y2);
		}	/* Diagonal Path */
		else if(x1 != x2 && y1 != y2) {
			do {
				x1 += deltaX;
				y1 += deltaY;
				path.add(new Coords(x1, y1));
			}while(y1 != y2);
		}
		
		return path;
	}
	
	/**
	 * Moves a chess piece from current location to the target location
	 * @param piece start position of moving piece
	 * @param target position to move piece to
	 */
	public static void MoveChessPiece(Coords piece, Coords target) {
		game.board[target.x][target.y] = game.board[piece.x][piece.y];
		FillSpace(piece);
	}
	
	/**
	 * Fills a tile as a whitespace or blackspace based on its position on the board
	 * @param tile position of tile to fill
	 */
	public static void FillSpace(Coords tile) {
		int tileVal = tile.x + tile.y;
		if(tileVal % 2 == 0) {
			game.board[tile.x][tile.y] = new whitespace();
		}else {
			game.board[tile.x][tile.y] = new blackspace();
		}
	}
	
	/**
	 * Determines whether the player's King is under check or checkmate
	 * @param defendingPlayer Player that wants to get out of check
	 * @return true if the player can get out of check, otherwise false
	 */
	public static boolean getOutOfCheck(String defendingPlayer){
		for(int i = 0; i < 7; i++) {
			for(int j = 0; j < 7; j++) {
				ChessPiece p = game.board[i][j];
				if(p.player.equals(defendingPlayer)) {
					if(canSaveKing(p, new Coords(i,j))) {
						return true;
					}
				}
			}
		}
		return false;
	}
	/**
	 * Checks if any valid move of piece p can get King out of check
	 * @param p Chess piece to try to save King
	 * @param position Coords position of ChessPiece p
	 * @return true if the piece has a valid move to save the king, false otherwise
	 */
	public static boolean canSaveKing(ChessPiece p, Coords position) {
		for(int i = 0; i < 7; i++) {
			for(int j = 0; j < 7; j++) {
				Coords c = new Coords(i,j);
				if(!pieceAt(c).getPieceName().equals("King") && p.isMoveValid(p.player, position, c, pieceAt(c).player, game) && !isSelfDestructive(position, c)) {
					//System.out.println("Move: " + p.toString() + " to " + pieceAt(c).toString());
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Determines if the attacking player puts their opponent's king in check
	 * @param attackingPlayer Player who is checking their opponent's King
	 * @return true if the opponent's King is in check, otherwise false
	 */
	public static boolean CheckForCheck(String attackingPlayer) {
		for(int i=0;i<8;i++) {
			for(int j=0;j<8;j++) {
				ChessPiece p = game.board[i][j];
				
				if(!p.player.equals(attackingPlayer) && p.getPieceName().equals("King")) {
					Coords KingLocation = new Coords(i,j);
					for(int k=0;k<8;k++) {
						for(int l=0;l<8;l++) {
							ChessPiece c = game.board[k][l];
							Coords PieceLocation = new Coords(k,l);
							if(c.player.equals(attackingPlayer)) {
								//System.out.print(c.player + " " + c.getPieceName() + " from " + PieceLocation.x + "," + PieceLocation.y + " to " + KingLocation.x + "," + KingLocation.y + " )");
								//System.out.println(c.isMoveValid(attackingPlayer, PieceLocation, KingLocation, pieceAt(KingLocation).player, game));
							}
							if(c.player == attackingPlayer && c.isMoveValid(attackingPlayer, PieceLocation, KingLocation, pieceAt(KingLocation).player, game)) {
									return true;
							}
						}
					}
					break;
				}
			}
		}
		return false;
	}
	
	/**
	 * Converts FileRank (ex: 'd7') to row,column position on the game board as a Coords object
	 * @param filerank String such as "e2" to convert to row, column numeric values
	 * @return Coords object holding the row (x) , column (y) values 
	 */
	public static Coords FileRankToCoords(String filerank) {
		int x = 8 - Character.getNumericValue(filerank.charAt(1));
		int y = Character.toLowerCase(filerank.charAt(0)) - 'a';
		
		Coords result = new Coords(x, y);
		return result;
	}

	/**
	 * Swaps currentPlayer and opponent
	 */
	public static void ChangePlayer() {
		String temp = opponent;
		opponent = currentPlayer;
		currentPlayer = temp;
	}

}
