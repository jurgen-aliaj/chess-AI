package game;

import java.util.ArrayList;

/**
 * This is the class which defines the bishop piece and how it moves
 *
 * @author Jurgen Aliaj
 */
public class Bishop extends Piece {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double value = 3;
	/**
     *
     * @param pos
     * @param color
     * @param game
     */
    public Bishop(Position pos, int color, Game game) {
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
     * Checks that the move does not violate bishop rules
     */
    public boolean isValidCapture(Position newPos) {
        int deltaX = (newPos.getCol() - curPos.getCol()), dirX = 0;
        int deltaY = (newPos.getRow() - curPos.getRow()), dirY = 0;
        if (deltaX != 0) {
            dirX = deltaX / Math.abs(deltaX); // convert to unit vector to find direction of movement
        }
        if (deltaY != 0) {
            dirY = deltaY / Math.abs(deltaY); //{...}
        }
        return newPos != curPos // must not click the same position
                && Math.abs(deltaX) == Math.abs(deltaY) // must be in the same diagonal
                && game.nothingInBetween(newPos, game.board[curPos.getRow() + dirY][curPos.getCol() + dirX], dirX, dirY) // not jumping
                && (!newPos.isOccupied()
                || newPos.getPiece().getColor() != getColor()); // the piece must not attack its own side
    }

	public double getValue() {
		return value + 0.1*pieceValueTable()[curPos.getRow()][curPos.getCol()];
	}
	
	@Override
	protected double[][] pieceValueTable() {
		double[][] table = {
            { -2.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -2.0},
            { -1.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0, -1.0},
            { -1.0,  0.0,  0.5,  1.0,  1.0,  0.5,  0.0, -1.0},
            { -1.0,  0.5,  0.5,  1.0,  1.0,  0.5,  0.5, -1.0},
            { -1.0,  0.0,  1.0,  1.0,  1.0,  1.0,  0.0, -1.0},
            { -1.0,  1.0,  1.0,  1.0,  1.0,  1.0,  1.0, -1.0},
            { -1.0,  0.5,  0.0,  0.0,  0.0,  0.0,  0.5, -1.0},
            { -2.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -2.0}
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
		
		addDiagonalMoves(moves);
		
		return moves;
	}
}
