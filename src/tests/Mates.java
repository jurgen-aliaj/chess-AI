package tests;

import java.util.ArrayList;

import game.*;
import ui.*;

public class Mates {
	
    public static void main(String args[]) {
        new ChessFrame(gameMaker()); // creates a new chessframe
    }
	
    public static Game gameMaker() {
		Game g = new Game();
		
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				g.board[i][j].setPiece(null);
			}
		}
		
		g.board[1][7].setPiece(new Pawn(g.board[1][7], Game.BLACK, g));
		g.board[1][6].setPiece(new Pawn(g.board[1][6], Game.BLACK, g));
		g.board[2][7].setPiece(new Knight(g.board[2][7], Game.WHITE, g));
		g.board[7][0].setPiece(new Bishop(g.board[7][0], Game.WHITE, g));
		
		Piece blackKing = new King(g.board[0][7], Game.BLACK, g);
		blackKing.nMoves = 1;
		g.board[0][7].setPiece(blackKing);
		
		Piece whiteKing = new King(g.board[1][4], Game.WHITE, g);
		whiteKing.nMoves = 1;
		g.board[1][4].setPiece(whiteKing);
		
		g.pieces = new ArrayList<Piece>();
		
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (g.board[i][j].isOccupied()) {
					g.pieces.add(g.board[i][j].getPiece());
				}
			}
		}
		
		return g;
	}
}
