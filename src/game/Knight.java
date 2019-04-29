package game;

import java.util.ArrayList;

/**
 * This class defines the knight piece and how it moves
 *
 * @author Jurgen Aliaj
 */
public class Knight extends Piece {

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
    public Knight(Position pos, int color, Game game) {
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
     * Checks that the move does not violate knight rules
     */
    public boolean isValidCapture(Position newPos) {
        return curPos != newPos // must not click same position
                && ((Math.abs(curPos.getRow() - newPos.getRow()) == 2 // up/down 2 and left/right 1
                && Math.abs(curPos.getCol() - newPos.getCol()) == 1)
                || (Math.abs(curPos.getRow() - newPos.getRow()) == 1 // or up/down 1 and left/right 2
                && Math.abs(curPos.getCol() - newPos.getCol()) == 2))
                && (!newPos.isOccupied()
                || newPos.getPiece().getColor() != getColor()); // piece must not attack its own side
    }

	public double getValue() {
		return value + 0.1*pieceValueTable()[curPos.getRow()][curPos.getCol()];
	}
	
	@Override
	protected double[][] pieceValueTable() {
		double[][] table = {
            {-5.0, -4.0, -3.0, -3.0, -3.0, -3.0, -4.0, -5.0},
            {-4.0, -2.0,  0.0,  0.0,  0.0,  0.0, -2.0, -4.0},
            {-3.0,  0.0,  1.0,  1.5,  1.5,  1.0,  0.0, -3.0},
            {-3.0,  0.5,  1.5,  2.0,  2.0,  1.5,  0.5, -3.0},
            {-3.0,  0.0,  1.5,  2.0,  2.0,  1.5,  0.0, -3.0},
            {-3.0,  0.5,  1.0,  1.5,  1.5,  1.0,  0.5, -3.0},
            {-4.0, -2.0,  0.0,  0.5,  0.5,  0.0, -2.0, -4.0},
            {-5.0, -4.0, -3.0, -3.0, -3.0, -3.0, -4.0, -5.0}
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
		
		if (row + 2 < 8 && col + 1 < 8) {
			moves.add(game.board[row+2][col+1]);
		}
		if (row + 2 < 8 && col - 1 >= 0) {
			moves.add(game.board[row+2][col-1]);
		}
		if (row - 2 >= 0 && col + 1 < 8) {
			moves.add(game.board[row-2][col+1]);
		}
		if (row - 2 >= 0 && col - 1 >= 0) {
			moves.add(game.board[row-2][col-1]);
		}
		if (row + 1 < 8 && col + 2 < 8) {
			moves.add(game.board[row+1][col+2]);
		}
		if (row + 1 < 8 && col - 2 >= 0) {
			moves.add(game.board[row+1][col-2]);
		}
		if (row - 1 >= 0 && col + 2 < 8) {
			moves.add(game.board[row-1][col+2]);
		}
		if (row - 1 >= 0 && col - 2 >= 0) {
			moves.add(game.board[row-1][col-2]);
		}
		
		return moves;
	}
}
