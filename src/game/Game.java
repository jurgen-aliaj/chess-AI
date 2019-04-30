package game;

import java.io.*;
import java.util.*;
import javax.swing.JOptionPane;

import misc.Pair;
import misc.TreeNode;

/**
 * This initializes the board and controls the flow of the game by switching
 * between player turns
 *
 * @author Abel MacNeil, Jurgen Aliaj
 */
public class Game implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int BLACK = 1;
    public static final int WHITE = 0;
    public ArrayList<Piece> pieces;
    protected List<Piece> promotedPawns;
    public List<Piece> removed;
    
    /**
     * the games's board positions in an 8x8 array
     */
    public Position[][] board;
    private int currentTurn;
    public int totalTurns = 0;

    /**
     * Creates a new instance of the Game class
     */
    public Game() {
        //initializes the variables
        pieces = new ArrayList<>();
        removed = new ArrayList<>();
        promotedPawns = new ArrayList<>();
        board = new Position[8][8];
        //loops through the board setting default values
        for (int i = board.length - 1; i > -1; i--) {
            for (int j = 0; j < board[i].length; j++) {
                board[i][j] = new Position(i, j);
            }
        }
        // initialize board pieces with their colors
        for (int color = 0; color < 2; color++) {
            //sets the pieces according to their order
            board[color * 7][0].setPiece(new Rook(board[color * 7][0], color, this));
            board[color * 7][1].setPiece(new Knight(board[color * 7][1], color, this));
            board[color * 7][2].setPiece(new Bishop(board[color * 7][2], color, this));
            board[color * 7][3].setPiece(new Queen(board[color * 7][3], color, this));
            board[color * 7][4].setPiece(new King(board[color * 7][4], color, this));
            board[color * 7][5].setPiece(new Bishop(board[color * 7][5], color, this));
            board[color * 7][6].setPiece(new Knight(board[color * 7][6], color, this));
            board[color * 7][7].setPiece(new Rook(board[color * 7][7], color, this));
        }
        // sets the pawns
        for (int i = 0; i < 8; i++) {
            board[1][i].setPiece(new Pawn(board[1][i], WHITE, this));
            board[6][i].setPiece(new Pawn(board[6][i], BLACK, this));
        }
        // initialize list of pieces
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                //if occupied add the piece to the list
                if (board[i][j].isOccupied()) {
                    pieces.add(board[i][j].getPiece());
                }
            }
        }
        currentTurn = Game.WHITE; // white starts
    }
    
    public String getTotalMoves() {
    	return Integer.toString(totalTurns/2 + 1) + ". ";
    }
    
    public int getCurrentTurn() {
        return this.currentTurn;
    }
    
    /**
     * Check if the king can be attacked
     *
     * @return whether or not our king is in check
     */
    public boolean inCheck(int color) {
    	Piece p = null;
        for (int i = 0; i < pieces.size(); i++) {
        	p = pieces.get(i);
            if (p.getColor() != color && p.isValidCapture(getKingPosition(color))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return the location of the king
     *
     * @return the location of our king
     */
    public Position getKingPosition(int color) {
        Piece p = null;
    	Position kingPos = null;
        for (int i = 0; i < pieces.size(); i++) {
        	p = pieces.get(i);
            if (p.getColor() == color && p instanceof King) {
                return p.getPosition();
            }
        }
        return kingPos;
    }

    /**
     * Check to see if two positions have nothing in between them to prevent
     * pieces jumping over each other (recursive algorithm)
     *
     * @param newPos the new position
     * @param current the current position
     * @param dirX the horizontal direction (magnitude 1)
     * @param dirY the vertical direction (magnitude 1)
     * @return whether or not there is nothing in between the two positions
     */
    public boolean nothingInBetween(Position newPos, Position current, int dirX, int dirY) {
        if (current == newPos || current.getCol() + dirX == -1 || current.getRow() + dirY == -1
                || current.getCol() + dirX == 8 || current.getRow() + dirY == 8) {
            return true;
        }
        return !current.isOccupied() //recursively check if the next position is also empty
                && nothingInBetween(newPos, board[current.getRow() + dirY][current.getCol() + dirX], dirX, dirY);
    }

    /**
     * Check if there are no more possible moves
     *
     * @return whether or not there are no more possible moves
     */
    public boolean isUnplayable(int color) {
    	@SuppressWarnings("unchecked")
		ArrayList<Piece> piecesCopy = (ArrayList<Piece>) pieces.clone();
    	Piece p = null;
        for (int i = 0; i < pieces.size(); i++) {
        	p = piecesCopy.get(i);
        	if (p.getColor() != color) {
            	continue;
            }
        	if (!p.getLegalMoves().isEmpty()) {
        		return false;
        	}
        }
        return true;
    }
    
    /**
     * Get all possible moves
     *
     * @return list of legal moves
     */
	@SuppressWarnings("unchecked")
	public ArrayList<Pair<Position,Position>> getLegalMoves() {
		ArrayList<Piece> piecesCopy = (ArrayList<Piece>) pieces.clone();
    	Piece p = null;
    	ArrayList<Pair<Position,Position>> legalMoves = new ArrayList<Pair<Position,Position>>();
        for (int i = 0; i < pieces.size(); i++) {
        	p = piecesCopy.get(i);
        	if (p.getColor() != currentTurn) {
            	continue;
            }
        	// p.getLegalMoves() could be null which leads to problems
        	Optional.ofNullable(p.getLegalMoves()).ifPresent(legalMoves::addAll);
        }
        return legalMoves;
    }
    
    /**
     * Check for checkmate
     *
     * @return whether or not checkmate
     */
    public boolean isCheckMate(int color) {
        return inCheck(color) && isUnplayable(color);
    }

    /**
     * Check for stalemate
     *
     * @return
     */
    public boolean isStaleMate(int color) {
        return !inCheck(currentTurn) && isUnplayable(color);
    }

    /**
     * Check if there is a draw due to insufficient material to complete the
     * game
     *
     * @return a draw if there is insufficient material (boolean)
     */
    public boolean isDraw() {
    	Piece p = null;
        boolean insufficient = false;
        for (int i = 0; i < pieces.size(); i++) {
        	p = pieces.get(i);
            insufficient = p instanceof Knight || p instanceof Bishop;
        }
        return pieces.size() == 2 || (pieces.size() == 3 && insufficient);
    }
    
    /**
     * Moves to the next turn. {@code Piece.isValidMove()} must be called first.
     *
     * @param piece the piece to move
     * @param newPos the new position to move to
     * @return the piece capture (null otherwise)
     */
    public Pair<Piece,ArrayList<Boolean>> nextTurn(Piece piece, Position newPos) {
    	Pair<Piece,Boolean> moveData = null;
    	Piece captured = null;
    	Boolean enPassant = false;
    	Boolean twoUp = false;
    	Boolean promotion = false;
    	Boolean shortCastle = false;
    	Boolean longCastle = false;
    	
        Position oldPos = piece.getPosition(); // save the old position
        twoUp = piece instanceof Pawn && ((Pawn) piece).twoUp(newPos); // whether or not a pawn has moved up
        moveData = piece.move(newPos); // move the piece and save any captured piece
        captured = moveData.getLeft();
        enPassant = moveData.getRight();
            
        //if the pawn has reached the other side of the board, promote it.
        if (piece instanceof Pawn && (newPos.getRow() == 7 || newPos.getRow() == 0)) {
        	promotion = true;
            int tempColor = piece.getColor();
            pieces.remove(piece); // remove the pawn
            promotedPawns.add(piece); // need to get this back at some point for backtracking
            piece.curPos.setPiece(null);
            
            //possible pieces to promote to
            Object[] possibilities = {"Queen", "Rook", "Bishop", "Knight"};
            //show an option dialogue of options and save the user's choice
            String s = (String) JOptionPane.showInputDialog(
                    null,
                    "Which piece would you like?",
                    "Pawn Promotion",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    possibilities,
                    "chess");
            pieces.remove(piece); // remove the pawn
            promotedPawns.add(piece); // need to save this for backtracking
            piece.curPos.setPiece(null);
            //based on user input set the piece to the selected piece
            if (s.equals(possibilities[0])) {
                newPos.setPiece(new Queen(newPos, tempColor, this));
            } else if (s.equals(possibilities[1])) {
                newPos.setPiece(new Rook(newPos, tempColor, this));
            } else if (s.equals(possibilities[2])) {
                newPos.setPiece(new Bishop(newPos, tempColor, this));
            } else if (s.equals(possibilities[3])) {
                newPos.setPiece(new Knight(newPos, tempColor, this));
            }
            
            pieces.add(newPos.getPiece()); //add to the list of new pieces
        } else if (twoUp) {//if the pawn has moved up two spots, the pawn can be taken via en passant
            ((Pawn) piece).movedTwo = totalTurns+1;
            //if the king moves two over to the right (castling)
        } else if (piece instanceof King && oldPos.getCol() + 2 == newPos.getCol()
                && board[oldPos.getRow()][7].isOccupied()) {
            shortCastle = true;
        	board[oldPos.getRow()][7].getPiece().move(board[oldPos.getRow()][5]);
            //if the king moves two over to the left (castling)
        } else if (piece instanceof King && oldPos.getCol() - 2 == newPos.getCol()
                && board[oldPos.getRow()][0].isOccupied()) {
        	longCastle = true;
            board[oldPos.getRow()][0].getPiece().move(board[oldPos.getRow()][3]);
        }
        currentTurn = 1 - currentTurn; //change the current turn
        totalTurns++;
        
        ArrayList<Boolean> bools = new ArrayList<Boolean>();
        bools.add(enPassant);
        bools.add(twoUp);
        bools.add(promotion);
        bools.add(shortCastle);
        bools.add(longCastle);
        return new Pair<Piece,ArrayList<Boolean>> (captured, bools); // return the captured piece
    }
    
    /**
     * Moves to the next turn. {@code Piece.isValidMove()} must be called first.
     *
     * @param piece the piece to move
     * @param newPos the new position to move to
     * @return the piece capture (null otherwise)
     */
    public Pair<Piece,ArrayList<Boolean>> nextTurnAutoQueen(Piece piece, Position newPos) {
    	Pair<Piece,Boolean> moveData = null;
    	Piece captured = null;
    	Boolean enPassant = false;
    	Boolean twoUp = false;
    	Boolean promotion = false;
    	Boolean shortCastle = false;
    	Boolean longCastle = false;
    	
        Position oldPos = piece.getPosition(); // save the old position
        twoUp = piece instanceof Pawn && ((Pawn) piece).twoUp(newPos); // whether or not a pawn has moved up
        moveData = piece.move(newPos); // move the piece and save any captured piece
        captured = moveData.getLeft();
        enPassant = moveData.getRight();
            
        //if the pawn has reached the other side of the board, promote it.
        if (piece instanceof Pawn && (newPos.getRow() == 7 || newPos.getRow() == 0)) {
        	promotion = true;
            int tempColor = piece.getColor();
            pieces.remove(piece); // remove the pawn
            promotedPawns.add(piece); // need to get this back at some point for backtracking
            piece.curPos.setPiece(null);
            // auto promote to queen for now
            newPos.setPiece(new Queen(newPos, tempColor, this));
            pieces.add(newPos.getPiece()); //add to the list of new pieces
        } else if (twoUp) {//if the pawn has moved up two spots, the pawn can be taken via en passant
            ((Pawn) piece).movedTwo = totalTurns+1;
            //if the king moves two over to the right (castling)
        } else if (piece instanceof King && oldPos.getCol() + 2 == newPos.getCol()
                && board[oldPos.getRow()][7].isOccupied()) {
            shortCastle = true;
        	board[oldPos.getRow()][7].getPiece().move(board[oldPos.getRow()][5]);
            //if the king moves two over to the left (castling)
        } else if (piece instanceof King && oldPos.getCol() - 2 == newPos.getCol()
                && board[oldPos.getRow()][0].isOccupied()) {
        	longCastle = true;
            board[oldPos.getRow()][0].getPiece().move(board[oldPos.getRow()][3]);
        }
        currentTurn = 1 - currentTurn; //change the current turn
        totalTurns++;
        
        ArrayList<Boolean> bools = new ArrayList<Boolean>();
        bools.add(enPassant);
        bools.add(twoUp);
        bools.add(promotion);
        bools.add(shortCastle);
        bools.add(longCastle);
        return new Pair<Piece,ArrayList<Boolean>> (captured, bools); //return the captured piece
    }
    
    /**
     * Rewind the previous move. {@code Piece.isValidMove()} must be called first.
     *
     * @param oldPos position to move back to
     * @param newPos position to move from
     */
    public void prevTurn(Position oldPos, Position newPos, Pair<Piece,ArrayList<Boolean>> turnData) {
    	this.totalTurns--;
    	Piece captured = turnData.getLeft();
    	ArrayList<Boolean> bools = turnData.getRight();
    	Piece toMoveBack = newPos.getPiece();
    	Boolean enPassant = bools.get(0);
    	Boolean twoUp = bools.get(1);
    	Boolean promotion = bools.get(2);
    	Boolean shortCastle = bools.get(3);
    	Boolean longCastle = bools.get(4);
    	
    	currentTurn = 1 - currentTurn; //change the current turn
        
        if(!promotion) {
        	toMoveBack.moveBack(oldPos, captured, enPassant);
        }
        
        //if the pawn has reached the other side of the board, get it back
        if (promotion) {
        	Piece resurrectedPawn = null;
            pieces.remove(toMoveBack); // remove the promoted piece
            newPos.setPiece(null);
            for (int i = 0; i < promotedPawns.size(); i++) {
            	if (promotedPawns.get(i).curPos == newPos) {
            		resurrectedPawn = promotedPawns.get(i);
            	}
            }
            promotedPawns.remove(resurrectedPawn);
            newPos.setPiece(resurrectedPawn);
            pieces.add(resurrectedPawn); // add the promoted pawn back
            resurrectedPawn.moveBack(oldPos, captured, enPassant);
        } else if (twoUp) { // if the pawn has moved up two spots, reset enPassantOpportunity
            ((Pawn) toMoveBack).movedTwo = 0;
        } else if (shortCastle) {
            board[oldPos.getRow()][5].getPiece().moveBack(board[oldPos.getRow()][7], null, false);
        } else if (longCastle) {
            board[oldPos.getRow()][3].getPiece().moveBack(board[oldPos.getRow()][0], null, false);
        }
    }
    
    /**
     * Returns a string for algebraic chess notation when playing a move
     * 
     * @param piece - the piece to move
     * @param newPos - the position to move to
     * @return
     */
    public String moveString(Piece piece, Position newPos) {
    	String p = "";
    	String capture = "";
    	String endPos = newPos.toString();
    	String check = "";
    	
    	switch (piece.getClass().getSimpleName()) {
    		case "King":
    			p = "K";
    			break;
    		case "Queen":
    			p = "Q";
    			break;
    		case "Bishop":
    			p = "B";
    			break;
    		case "Rook":
    			p = "R";
    			break;
    		case "Knight":
    			p = "N";
    			break;
    	}
    	
    	if (newPos.isOccupied()) {
    		capture = "x";
    	} else if (piece instanceof Pawn && ((Pawn)piece).enPassant(newPos)) {
    		capture = "x";
    	}
    	
    	if (piece.givesCheck(newPos)) {
    		check = "+";
    	}
    	
    	if(piece instanceof Pawn && capture == "x") {
    		p = piece.getPosition().toString().substring(0,1);
    	}
    	
    	if (piece instanceof King && ((King)piece).isCastlingShort(newPos)) {
    		return "O-O";
    	} else if (piece instanceof King && ((King)piece).isCastlingLong(newPos)) {
    		return "O-O-O";
    	}
    	
    	Piece ambigPiece = null;
    	// check for ambiguous notation
    	if (piece instanceof Knight || piece instanceof Rook) {
    		for(int i = 0; i < pieces.size(); i++) {
    			ambigPiece = pieces.get(i);
    			if (ambigPiece.getColor() == piece.getColor() 
    					&& ambigPiece.getClass() == piece.getClass() 
    					&& ambigPiece != piece
    					&& ambigPiece.isValidMove(newPos)) {
    				break;
    			} else {
    				ambigPiece = null;
    			}
    		}
    			
			if (ambigPiece != null && ambigPiece instanceof Knight) {
				if (ambigPiece.curPos.getCol() != piece.curPos.getCol()) {
					p = "N" + piece.getPosition().toString().substring(0,1);
				} else {
					p = "N" + piece.getPosition().toString().substring(1,2);
				}
			} else if (ambigPiece != null && ambigPiece instanceof Rook) {
				if (ambigPiece.curPos.getCol() != piece.curPos.getCol()) {
					p = "R" + piece.getPosition().toString().substring(0,1);
				} else {
					p = "R" + piece.getPosition().toString().substring(1,2);
				}
			}
		}
    	
    	return p + capture + endPos +  check;
    }
    
    
    /**
     * Heuristic for evaluating the position
     * 
     * @return Numerical value representing the evaluation of the position
     * (positive values favour white while negative values favour black).
     */
    public double evaluation() {
    	Piece p = null;
    	double material = 0;
    	
    	for(int i = 0; i < pieces.size(); i++) {
    		p = pieces.get(i);
    		if (p.getColor() == Game.WHITE) {
    			material += p.getValue();
    		}
    		else {
    			material -= p.getValue();
    		}
    	}
    	
    	return material;
    }
    
    /**
     * Constructs a tree of all possible positions from the given position,
     * calculates the value of the position based on minimax using alpha-beta pruning
     * 
     * @return a tree of all possible paths with evaluations
     */
    public TreeNode<Game> constructGameTree(TreeNode<Game> root, int depth, Double alpha, Double beta) {
    	Game curGame = root.getData();
    	int turn = curGame.getCurrentTurn();
    	
    	if (depth == 0) {
    		root.setValue(curGame.evaluation()); // leaf nodes have a heuristic value
    		root.setData(null);
    		return root;
    	}
    	
    	root.setValue(null);
    	
    	TreeNode<Game> childTree = null;
    	Position oldPos = null, newPos = null;
    	Piece pieceToMove = null;
    	ArrayList<Pair<Position,Position>> moves = curGame.getLegalMoves();
    	
    	if (moves.isEmpty()) {
    		// check for checkmate or stalemate
    		if (curGame.inCheck(turn)) {
    			root.setValue(turn == Game.WHITE ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
    		} else {
    			root.setValue(0.0);
    		}
    		root.setData(null);
    		return root;
    	} else if (curGame.isDraw()) {
    		root.setValue(0.0);
    		root.setData(null);
    		return root;
    	}
    	
    	moves.sort((a,b) -> (a.getRight().isOccupied() ? 0 : 1) - (b.getRight().isOccupied() ? 0 : 1));
    	Pair<Position,Position> bestMove = null;
    	Double bestMoveValue = null;
    	
    	for(Pair<Position,Position> move : moves) {
    		oldPos = move.getLeft();
    		newPos = move.getRight();
    		pieceToMove = oldPos.getPiece();
    		
    		Pair<Piece,ArrayList<Boolean>> turnData = curGame.nextTurnAutoQueen(pieceToMove, newPos);
    		
    		childTree = constructGameTree(new TreeNode<Game>(curGame), depth-1, alpha, beta);
    		
    		root.addChild(childTree);
    		
    		curGame.prevTurn(oldPos, newPos, turnData);
    		
    		if (bestMoveValue == null) {
    			bestMove = move;
    			bestMoveValue = childTree.getValue();
    		} else if (turn == Game.WHITE && childTree.getValue() > bestMoveValue) {
    			bestMove = move;
    			bestMoveValue = childTree.getValue();
    		} else if (turn == Game.BLACK && childTree.getValue() < bestMoveValue) {
    			bestMove = move;
    			bestMoveValue = childTree.getValue();
    		}
    		
    		if (turn == Game.BLACK) {
    			beta = Math.min(beta, bestMoveValue);
    			if (beta <= alpha)
    				break;
    		} else {
    			alpha = Math.max(alpha, bestMoveValue);
    			if (beta <= alpha)
    				break;
    		}
    	}
    	
    	root.setMove(bestMove);
    	root.setValue(bestMoveValue);
    	root.setData(null); // clean up
    	
    	return root;
    }
}
