package game;

import java.util.ArrayList;

/**
 * This class defines the pawn piece and how it moves
 *
 * @author Jurgen Aliaj
 */
public class Pawn extends Piece {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double value = 1;
	public int movedTwo;
	
    /**
     *
     * @param pos
     * @param color
     * @param game
     */
    public Pawn(Position pos, int color, Game game) {
        super(pos, color, game);
        movedTwo = 0;
    }

    @Override
    /**
     * Checks that move does not violate general rules
     */
    public boolean isValidMove(Position newPos) {
        return super.isValidMove(newPos) && isValidCapture(newPos);
    }

    @Override
    /**
     * Checks that move does not violate pawn rules
     */
    public boolean isValidCapture(Position newPos) { // a pawn can move one up, two up, diagonally (when capturing) or en passant
        return curPos != newPos && (oneUp(newPos) || twoUp(newPos) || capture(newPos) || enPassant(newPos));
    }

    /**
     * Check that the pawn moves one square up
     *
     * @param newPos
     * @return
     */
    private boolean oneUp(Position newPos) {
        return (getColor() == Game.BLACK && newPos.getRow() == curPos.getRow() - 1 // forward 1
                || getColor() == Game.WHITE && newPos.getRow() == curPos.getRow() + 1)
                && newPos.getCol() == curPos.getCol() // same column
                && !newPos.isOccupied(); // new position must be empty, regardless of color
    }

    /**
     * Checks that the pawn moves two squares up
     *
     * @param newPos
     * @return
     */
    public boolean twoUp(Position newPos) {
        return (getColor() == Game.BLACK && newPos.getRow() == 4 // forward 2
                || getColor() == Game.WHITE && newPos.getRow() == 3)
                && newPos.getCol() == curPos.getCol() // same column
                && !newPos.isOccupied() // new position must be empty
                && ((!game.board[newPos.getRow() + 1][newPos.getCol()].isOccupied() && getColor() == Game.BLACK) // the position that is jumped over must be empty
                || (!game.board[newPos.getRow() - 1][newPos.getCol()].isOccupied() && getColor() == Game.WHITE)) // the position that is jumped over must be empty
                && nMoves == 0; // can only start by moving two up
    }

    /**
     * Checks if the pawn is capturing
     *
     * @param newPos
     * @return
     */
    private boolean capture(Position newPos) {
        return newPos.isOccupied() // new position must be occupied by a different colored piece
                && newPos.getPiece().getColor() != getColor()
                && Math.abs(newPos.getCol() - curPos.getCol()) == 1 // 1 column left/right
                && (getColor() == Game.BLACK && newPos.getRow() == curPos.getRow() - 1 // forward up
                || getColor() == Game.WHITE && newPos.getRow() == curPos.getRow() + 1);
    }

    /**
     * Check for en passant
     *
     * @param newPos
     * @return
     */
    public boolean enPassant(Position newPos) {
        boolean leftBoundaryPawn = false, rightBoundaryPawn = false, middleBoardPawn = false;
        if (curPos.getCol() == 0) {
            leftBoundaryPawn = rightEnPassant(newPos); // check if on left boundary
        } else if (curPos.getCol() == 7) {
            rightBoundaryPawn = leftEnPassant(newPos); // check right boundary
        } else {
            middleBoardPawn = leftEnPassant(newPos) || rightEnPassant(newPos); //otherwise check for both
        }
        return (leftBoundaryPawn || rightBoundaryPawn || middleBoardPawn);
    }

    /**
     * Check if moving right side (these methods were introduced to avoid a null
     * pointer exception from pawns on the edges of the board)
     *
     * @param newPos
     * @return
     */
    public boolean rightEnPassant(Position newPos) {
    	int x = curPos.getRow();
    	int y = curPos.getCol();
        return game.board[x][y + 1].getPiece() instanceof Pawn // piece directly beside is pawn
                && game.board[x][y + 1].getPiece().getColor() != getColor()
        		&& ((Pawn) game.board[x][y + 1].getPiece()).movedTwo == game.totalTurns // piece has jumped two positions and only has 1 move
                && ((newPos.getRow() == x + 1 && getColor() == Game.WHITE) // forward up
                || (newPos.getRow() == x - 1 && getColor() == Game.BLACK))
                && newPos.getCol() == y + 1; // diagonally to the right
    }

    /**
     * Check if moving left side {...}
     *
     * @param newPos
     * @return
     */
    public boolean leftEnPassant(Position newPos) {
    	int x = curPos.getRow();
    	int y = curPos.getCol();
        return game.board[x][y-1].getPiece() instanceof Pawn // {...}
                && game.board[x][y - 1].getPiece().getColor() != getColor()
        		&& ((Pawn) game.board[x][y - 1].getPiece()).movedTwo == game.totalTurns
                && ((newPos.getRow() == x + 1 && getColor() == Game.WHITE)
                || (newPos.getRow() == x - 1 && getColor() == Game.BLACK))
                && newPos.getCol() == y - 1;
    }

	public double getValue() {
		return value + 0.1*pieceValueTable()[curPos.getRow()][curPos.getCol()];
	}
	
	@Override
	protected double[][] pieceValueTable() {
		double[][] table = {
	        {0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0},
	        {5.0,  5.0,  5.0,  5.0,  5.0,  5.0,  5.0,  5.0},
	        {1.0,  1.0,  2.0,  3.0,  3.0,  2.0,  1.0,  1.0},
	        {0.5,  0.5,  1.0,  2.5,  2.5,  1.0,  0.5,  0.5},
	        {0.0,  0.0,  0.0,  2.0,  2.0,  0.0,  0.0,  0.0},
	        {0.5, -0.5, -1.0,  0.0,  0.0, -1.0, -0.5,  0.5},
	        {0.5,  1.0, 1.0,  -2.0, -2.0,  1.0,  1.0,  0.5},
	        {0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0}
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
		
		if (getColor() == Game.WHITE) {
			if (row == 1) {
				moves.add(game.board[row+2][col]);
			}
			moves.add(game.board[row+1][col]);
			if (col - 1 >= 0) {
				moves.add(game.board[row+1][col-1]);
			}
			if (col + 1 < 8) {
				moves.add(game.board[row+1][col+1]);
			}
		} else {
			if (row == 6) {
				moves.add(game.board[row-2][col]);
			}
			moves.add(game.board[row-1][col]);
			if (col - 1 >= 0) {
				moves.add(game.board[row-1][col-1]);
			}
			if (col + 1 < 8) {
				moves.add(game.board[row-1][col+1]);
			}
		}
		
		return moves;
	}
}