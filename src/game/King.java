package game;

import java.util.ArrayList;

/**
 * This class defines the king piece and how it moves
 *
 * @author Jurgen Aliaj, Abel MacNeil
 */
public class King extends Piece {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double value = 90; // the king is invaluable, but we account for this in another way
						      // see Game.constructGameTree
	
	/**
     * Inherit behavior from parent class
     *
     * @param pos
     * @param color
     * @param game
     */
    public King(Position pos, int color, Game game) {
        super(pos, color, game);
    }
    
    /**
     * Checks that the move does not violate general rules
     * 
     * @param newPos
     * @return if the move 
     */
    @Override
    public boolean isValidMove(Position newPos) {
        return super.isValidMove(newPos) && isValidCapture(newPos);
    }

    /**
     * Checks that the move does not violate king moves
     *
     * @param newPos
     * @return if the move is valid (boolean)
     */
    @Override
    public boolean isValidCapture(Position newPos) {
        int deltaX = (newPos.getCol() - curPos.getCol());
        int deltaY = (newPos.getRow() - curPos.getRow());
        return curPos != newPos
                && (Math.abs(deltaX) == 1 && Math.abs(deltaY) == 0) // left or right by 1
                | (Math.abs(deltaX) == 0 && Math.abs(deltaY) == 1) // up or down by 1
                | (Math.abs(deltaX) == 1 && Math.abs(deltaY) == 1) // diagonally by 1
                | isCastlingLong(newPos) // check for castling
                | isCastlingShort(newPos)
                && (!newPos.isOccupied()
                || newPos.getPiece().getColor() != getColor()); // cannot attack its own side
    }

    /**
     * Check if the king is castling right
     *
     * @param newPos
     * @return if the king is castling right (boolean)
     */
    public boolean isCastlingShort(Position newPos) {
        return curPos.getPiece().nMoves == 0 // both king and rook must have zero moves
                && game.board[curPos.getRow()][7].isOccupied()
                && game.board[curPos.getRow()][7].getPiece().nMoves == 0
                && newPos.getRow() == curPos.getRow() && newPos.getCol() == 6
                && game.nothingInBetween(game.board[curPos.getRow()][7], game.board[curPos.getRow()][curPos.getCol() + 1], 1, 0)
                && noInterference(curPos, game.board[curPos.getRow()][6]); // there must be no threat in the king's path
    }

    /**
     * Check if the king is castling to the left
     *
     * @param newPos
     * @return if the king is castling left (boolean)
     */
    public boolean isCastlingLong(Position newPos) {
        return curPos.getPiece().nMoves == 0 // {...}
                && game.board[curPos.getRow()][0].isOccupied()
                && game.board[curPos.getRow()][0].getPiece().nMoves == 0
                && newPos.getRow() == curPos.getRow() && newPos.getCol() == 2
                && game.nothingInBetween(game.board[curPos.getRow()][0], game.board[curPos.getRow()][curPos.getCol() - 1], -1, 0)
                && noInterference(game.board[curPos.getRow()][2], curPos);
    }


    /**
     * Check for no threats in between two positions
     *
     * @param posA
     * @param posB
     * @return whether or not there is an interference (boolean)
     */
    public boolean noInterference(Position posA, Position posB) {
    	while (posA != posB) {
    		if (!super.isValidMove(posB)) {
    			return false;
    		}
    		posB =  game.board[posB.getRow()][posB.getCol() - 1];
    	}
        return super.isValidMove(posB);
    }

	public double getValue() {
		return value + 0.1*pieceValueTable()[curPos.getRow()][curPos.getCol()];
	}
	
	@Override
	protected double[][] pieceValueTable() {
		double[][] table = {
			{ -3.0, -4.0, -4.0, -5.0, -5.0, -4.0, -4.0, -3.0 },
			{ -3.0, -4.0, -4.0, -5.0, -5.0, -4.0, -4.0, -3.0 },
			{ -3.0, -4.0, -4.0, -5.0, -5.0, -4.0, -4.0, -3.0 },
			{ -3.0, -4.0, -4.0, -5.0, -5.0, -4.0, -4.0, -3.0 },
			{ -2.0, -3.0, -3.0, -4.0, -4.0, -3.0, -3.0, -2.0 },
			{ -1.0, -2.0, -2.0, -2.0, -2.0, -2.0, -2.0, -1.0 },
			{  2.0,  2.0,  0.0,  0.0,  0.0,  0.0,  2.0,  2.0 },
			{  2.0,  3.0,  1.0,  0.0,  0.0,  1.0,  3.0,  2.0 }
		};
		if (color == Game.WHITE) {
			return table;
		} else {
			reverse(table);
			return table;
		}
	}
	
    @Override
    public ArrayList<Position> getPossibleMoves() {
    	ArrayList<Position> moves = new ArrayList<Position>();
    	
    	int row = curPos.getRow();
    	int col = curPos.getCol();
    	
    	for (int i = -1; i <= 1; i++) {
    		if (i + row < 0 || i + row > 7 || i == 0) {
    			continue;
    		}
    		moves.add(game.board[i+row][col]);
    	}
    	
    	for (int i = -1; i <= 1; i++) {
    		if (col == 7) {
    			break;
    		}
    		if (i + row < 0 || i + row > 7) {
    			continue;
    		}
    		moves.add(game.board[i+row][col+1]);
    	}
    	
    	for (int i = -1; i <= 1; i++) {
    		if (col == 0) {
    			break;
    		}
    		if (i + row < 0 || i + row > 7) {
    			continue;
    		}
    		moves.add(game.board[i+row][col-1]);
    	}
    	
    	if (col + 2 < 8) {
    		moves.add(game.board[row][col+2]);
    	}
    	if (col - 2 >= 0) {
    		moves.add(game.board[row][col-2]);
    	}
    	
        return moves;
    }
}
