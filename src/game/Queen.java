package game;

import java.util.ArrayList;

/**
 * This class defines the queen piece and how it moves
 *
 * @author Jurgen Aliaj
 */
public class Queen extends Piece {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double value = 9;
	
	/**
     *
     * @param pos
     * @param color
     * @param game
     */
    public Queen(Position pos, int color, Game game) {
        super(pos, color, game);
    }

    @Override
    /**
     * Checks that the move does not violate general rules
     */
    public boolean isValidMove(Position newPos) {
        return super.isValidMove(newPos) && isValidCapture(newPos);
    }

    @Override
    /**
     * Checks that the move does not violate queen rules
     */
    public boolean isValidCapture(Position newPos) {
        int deltaX = (newPos.getCol() - curPos.getCol()), dirX = 0;
        int deltaY = (newPos.getRow() - curPos.getRow()), dirY = 0;
        if (deltaX != 0) {
            dirX = deltaX / Math.abs(deltaX); // convert to unit vector to obtain direction
        }
        if (deltaY != 0) {
            dirY = deltaY / Math.abs(deltaY); // {...}
        }
        return newPos != curPos // must not click the same position
                && ((deltaX == 0 || deltaY == 0) || Math.abs(deltaX) == Math.abs(deltaY)) // move up/down or left/right or diagonally
                && game.nothingInBetween(newPos, game.board[curPos.getRow() + dirY][curPos.getCol() + dirX], dirX, dirY) // no jumping
                && (!newPos.isOccupied()
                || newPos.getPiece().getColor() != getColor()); // must not attack its own side
    }

	public double getValue() {
		return value + 0.1*pieceValueTable()[curPos.getRow()][curPos.getCol()];
	}
	
	@Override
	protected double[][] pieceValueTable() {
		double[][] table = {
			{ -2.0, -1.0, -1.0, -0.5, -0.5, -1.0, -1.0, -2.0},
			{ -1.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0, -1.0},
			{ -1.0,  0.0,  0.5,  0.5,  0.5,  0.5,  0.0, -1.0},
			{ -0.5,  0.0,  0.5,  0.5,  0.5,  0.5,  0.0, -0.5},
			{  0.0,  0.0,  0.5,  0.5,  0.5,  0.5,  0.0, -0.5},
			{ -1.0,  0.5,  0.5,  0.5,  0.5,  0.5,  0.0, -1.0},
			{ -1.0,  0.0,  0.5,  0.0,  0.0,  0.0,  0.0, -1.0},
			{ -2.0, -1.0, -1.0, -0.5, -0.5, -1.0, -1.0, -2.0}
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
		
		// top left diagonal
		for (int i = 0; i < Math.min(col, 7-row); i++) {
			moves.add(game.board[row + i + 1][col - i - 1]);
		}
		// bottom left diagonal
		for (int i = 0; i < Math.min(col, row); i++) {
			moves.add(game.board[row - i - 1][col - i - 1]);
		}
		// bottom right diagonal
		for (int i = 0; i < Math.min(7-col, row); i++) {
			moves.add(game.board[row - i - 1][col + i + 1]);
		} 
		// top right diagonal
		for (int i = 0; i < Math.min(7-col, 7-row); i++) {
			moves.add(game.board[row + i + 1][col + i + 1]);
		}
		
		for (int i = 0; i < 8; i++) {
			if (i != row) {
				moves.add(game.board[i][col]);
			}
			if (i != col) {
				moves.add(game.board[row][i]);
			}
		}
		
		return moves;
	}
}
