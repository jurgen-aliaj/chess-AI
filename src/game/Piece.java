package game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.stream.Collectors;

import misc.Pair;

/**
 * This is the general abstract class which is extended by all pieces
 *
 * @author Abel MacNeil, Jurgen Aliaj
 */
public abstract class Piece implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    protected int color;
    protected Position curPos;
    protected Game game;
    public int nMoves;

    /**
     * Creates and instance of a Piece with position, color, and current game.
     * @param pos The piece's current position.
     * @param color The color (Game.BLACK or Game.WHITE)
     * @param game the current game in progress
     */
    public Piece(Position pos, int color, Game game) {
        this.curPos = pos;
        this.color = color;
        this.game = game;
        nMoves = 0;
    }

    /**
     * Check if the move does not result in the king being exposed.
     * All subclasses should call super.isValidMove(pos);
     *
     * @param newPos the position to move to
     * @return whether or not the new position is a valid move
     */
    public boolean isValidMove(Position newPos) {
        Position oldPos = curPos; // we will temporarily move the piece to the new position
        Pair<Piece, Boolean> tuple = this.move(newPos);
        Piece captured = tuple.getLeft();
        Boolean enPassMove = tuple.getRight();
        
        boolean n = game.inCheck(game.getCurrentTurn()); // check if the side is in check
        newPos.getPiece().moveBack(oldPos, captured, enPassMove); // move the pieces back to their original positions
        
        return !n;
    }
    
    /**
     * Check if the move results in check
     *
     * @param newPos the position to move to
     * @return whether or not the new position results in check for the opposing side
     */
    public boolean givesCheck(Position newPos) {
        Position oldPos = curPos; // we will temporarily move the piece to the new position
        Pair<Piece, Boolean> tuple = this.move(newPos);
        Piece captured = tuple.getLeft();
        Boolean enPassMove = tuple.getRight();
        
        boolean n = game.inCheck(1-game.getCurrentTurn()); // check if other side is in check
        // move the pieces back to their original positions
        newPos.getPiece().moveBack(oldPos, captured, enPassMove); 
        
        return n;
    }
    
    /**
     * Returns a list of possible moves for this piece,
     * i.e. a list of legal moves the piece can move to if the board is empty
     * 
     * @return list of moves
     */
    public abstract ArrayList<Position> getPossibleMoves();
    
    /**
     * Returns a list of legal moves for this piece. Converts the format
     * of the moves to <Position, Position>. I.e. the move (e2, e4) means
     * move the piece on e2 to e4.
     * 
     * @return list of moves
     */
	public ArrayList<Pair<Position,Position>> getLegalMoves() {
		return (ArrayList<Pair<Position,Position>>) getPossibleMoves()
				.stream()
				.filter(x -> isValidMove(x))
				.map(x -> new Pair<Position,Position>(curPos, x))
				.collect(Collectors.toList());
	}

    /**
     * Whether or not the move to the new position results in a valid capture.
     * 
     * @param pos the new Position
     * @return boolean whether or not the move to the new position results in a valid capture.
     */
    public abstract boolean isValidCapture(Position pos);

    /**
     * Returns the heuristic value of the piece
     * 
     * @return decimal representing the value of the piece
     */
    public abstract double getValue();
    
    /**
     * Gives a bonus value to pieces on "good" squares,
     * or a punitive value for piece on "bad" squares.
     * 
     * @return a table of values corresponding to good and bad squares
     */
    protected abstract double[][] pieceValueTable();
    
    /**
     * Gets the current position.
     * @return the current position
     */
    public Position getPosition() {
        return this.curPos;
    }

    /**
     * Sets the current position
     * @param pos the new position
     */
    public void setPosition(Position pos) {
        this.curPos = pos;
    }
    
    /**
     * Move a piece to a new position
     *
     * @param newPos the new position
     * @return captured piece
     */
    public Pair<Piece,Boolean> move(Position newPos) {
    	Boolean enPassMove = false;
        Piece captured = newPos.getPiece();
        
        // if the en passant, remove the piece behind pawn
        if (curPos.getPiece() instanceof Pawn && ((Pawn) curPos.getPiece()).enPassant(newPos)) {
        	enPassMove = true;
            captured = game.board[curPos.getRow()][newPos.getCol()].getPiece();
            game.pieces.remove(captured);
            game.board[curPos.getRow()][newPos.getCol()].setPiece(null);
        }
        curPos.setPiece(null);//sets current postion to null (empty)
        newPos.setPiece(this);
        curPos = newPos;//set the current position as the new position
        nMoves++;//increment the number of moves this piece has taken
        
        //remove the piece if it exists and is not a King
        if (captured != null && !(captured instanceof King)) {
            game.pieces.remove(captured);
            game.removed.add(captured);
        }
        
        return new Pair<Piece,Boolean>(captured, enPassMove);
    }
    
    /**
     * Move a piece to its previous position
     *
     * @param oldPos the position to move back to
     */
    public void moveBack(Position oldPos, Piece captured, Boolean wasEnPassant) {
    	Position newPos = curPos;
    	
    	curPos.setPiece(null);//sets current postion to null (empty)
        curPos = oldPos;//set the current position as the new position
        curPos.setPiece(this);
        nMoves--;//decrement the number of moves this piece has taken
        
        if (captured != null && !(captured instanceof King)) {
            game.pieces.add(captured);
            game.removed.remove(captured);
        }
        
    	if(wasEnPassant) {
        	game.board[oldPos.getRow()][newPos.getCol()].setPiece(captured);
        } else if (captured != null) {
        	newPos.setPiece(captured);
        }
    }
    
    /**
     * Finds all possible moves along the diagonals.
     * This method will be useful for the bishop and the queen.
     * 
     * @param moves - the set of moves to add to
     */
    public void addDiagonalMoves(ArrayList<Position> moves) {
    	int row = curPos.getRow();
		int col = curPos.getCol();
		
		// top left diagonal
		for (int i = 0; i < Math.min(col, 7-row); i++) {
			moves.add(game.board[row + i + 1][col - i - 1]);
			if (game.board[row + i + 1][col - i - 1].isOccupied()) {
				break;
			}
		}
		// bottom left diagonal
		for (int i = 0; i < Math.min(col, row); i++) {
			moves.add(game.board[row - i - 1][col - i - 1]);
			if (game.board[row - i - 1][col - i - 1].isOccupied()) {
				break;
			}
		}
		// bottom right diagonal
		for (int i = 0; i < Math.min(7-col, row); i++) {
			moves.add(game.board[row - i - 1][col + i + 1]);
			if (game.board[row - i - 1][col + i + 1].isOccupied()) {
				break;
			}
		} 
		// top right diagonal
		for (int i = 0; i < Math.min(7-col, 7-row); i++) {
			moves.add(game.board[row + i + 1][col + i + 1]);
			if (game.board[row + i + 1][col + i + 1].isOccupied()) {
				break;
			}
		}
    }
    
    /**
     * Finds all possible moves along the files and ranks.
     * This method will be useful for the rook and the queen.
     * 
     * @param moves - the set of moves to add to
     */
    public void addOrthogonalMoves(ArrayList<Position> moves) {
    	int row = curPos.getRow();
		int col = curPos.getCol();
		
		// upper file
		for (int i = row+1; i < 8; i++) {
			moves.add(game.board[i][col]);
			if (game.board[i][col].isOccupied()) {
				break;
			}
		}
		
		// lower file
		for (int i = row-1; i >= 0; i--) {
			moves.add(game.board[i][col]);
			if (game.board[i][col].isOccupied()) {
				break;
			}
		}
		
		// right rank
		for (int i = col+1; i < 8; i++) {
			moves.add(game.board[row][i]);
			if (game.board[row][i].isOccupied()) {
				break;
			}
		}
		
		// left rank
		for (int i = col-1; i >= 0; i--) {
			moves.add(game.board[row][i]);
			if (game.board[row][i].isOccupied()) {
				break;
			}
		}
    }

    /**
     * Return the color
     */
    public int getColor() {
        return color;
    }

    /**
     * Converts the Piece to a String
     * @return string of the piece object
     */
    @Override
    public String toString() {
        char col = 'W';
        if (getColor() == Game.BLACK) {
            col = 'B';
        }
        return this.getClass().getSimpleName() + "-" + col + "-" + curPos;
    }

    /**
     * Gets the path to the image for the current piece.
     * ex src/chess/images/w_rook70.png
     * @return the path to the image for the current piece
     */
    public String getImagePath() {
        String result = "src/images/";
        if (getColor() == Game.WHITE) {
            result += "w";
        } else {
            result += "b";
        }
        result += "_" + this.getClass().getSimpleName().toLowerCase() + "70.png";
        return result;
    }
    
	protected void reverse(double[][] A) {
		for(int i = 0; i < 4; i++) {
			double[] temp = A[i];
			A[i] = A[7-i];
			A[7-i] = temp;
		}
	}
}
