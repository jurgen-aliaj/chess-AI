package game;

import java.io.Serializable;

/**
 * This class defines a position object
 *
 * @author Abel MacNeil
 */
public class Position implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int row;
    private int col;
    private Piece piece;

    /**
     * Creates an instance of a position with row and column
     * @param row the row
     * @param col the column
     */
    public Position(int row, int col) {
        this(row, col, null);
    }

    /**
     * Creates an instance of a position with row, column and a piece
     * @param row the row
     * @param col the column
     * @param p the current piece
     */
    public Position(int row, int col, Piece p) {
        this.row = row;
        this.col = col;
        this.piece = p;
    }

    /**
     * Whether or not the position has a piece
     * @return boolean whether or not the position has a piece
     */
    public boolean isOccupied() {
        return piece != null;
    }

    /**
     * Gets the current piece
     * @return the current piece
     */
    public Piece getPiece() {
        return this.piece;
    }

    /**
     * Sets the current piece
     * @param p the new piece
     */
    public void setPiece(Piece p) {
        this.piece = p;
    }

    /**
     * gets the current row
     * @return the current row
     */
    public int getRow() {
        return row;
    }

    /**
     * gets the current column
     * @return the current row
     */
    public int getCol() {
        return col;
    }

    /**
     * Converts the Position to a String
     * @return string of the position object
     */
    @Override
    public String toString() {
        return Character.toString((char) (col + 'a')) + Integer.toString(row + 1);
    }

    /**
     * Whether or not the position has a piece.
     * @return boolean whether or not the position has a piece.
     */
    public boolean isEmpty() {
        return getPiece() == null;
    }
}
