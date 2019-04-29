package ui;

import game.Position;
import misc.Pair;
import misc.TreeNode;
import game.Game;
import game.Piece;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/**
 * This is the main Board that holds all of the pieces and squares. All of the
 * {@code Game} components are accessed here.
 *
 * @author Abel MacNeil, Jurgen Aliaj
 */
public final class Board extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int size;
	private int depth = 5; // search depth for the AI
	private Game game;
	private JButton[][] squares;
	private ImagePanel[][] imgs;
	private boolean isFirstClick;
	private Piece pieceToMove;
	private Position positionToMoveTo;
	private Color lightColor;
	private Color darkColor;
	private Color highLightGreen;
	private Color highLightRed;
	private ChessFrame cframe;
	private Timer timerObj;
	private ActionListener chessTask;

	/**
	 * Main Constructor, creates a new Board to be added to another JCompnonent.
	 *
	 * @param size   the size of each square on the board.
	 * @param game   the current game in play
	 * @param cframe the frame that the board will be added to.
	 */
	public Board(int size, Game game, ChessFrame cframe) {
		super();
		setLayout(null);
		this.lightColor = ChessFrame.lightColor;
		this.darkColor = ChessFrame.darkColor;
		this.highLightGreen = new Color(45, 235, 160);
		this.highLightRed = new Color(190, 85, 100);
		this.size = size; // initialize
		this.game = game;
		this.cframe = cframe;
		squares = new JButton[8][8];
		imgs = new ImagePanel[8][8];
		isFirstClick = true;
		
		init();

		this.chessTask = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				runAI();
			}
		};
		timerObj = new Timer(50, chessTask);
		timerObj.setRepeats(true);
		timerObj.start();
	}

	/**
	 * Initializes the frame's components
	 */
	public void init() {
		// loop through all of the squares
		for (int i = squares.length - 1; i > -1; i--) {
			for (int j = 0; j < squares.length; j++) {
				squares[i][j] = new JButton();// create new button
				// determines whether or not to place the background the square as light or dark
				if ((i % 2 == 0 && j % 2 == 1) || (i % 2 == 1 && j % 2 == 0)) {
					squares[i][j].setBackground(ChessFrame.lightColor);
				} else {
					squares[i][j].setBackground(ChessFrame.darkColor);
				}
				// sets a border for the button
				squares[i][j].setBorder(BorderFactory.createEmptyBorder());
				squares[i][j].setLayout(null); // sets the layout manage to null
				imgs[i][j] = new ImagePanel(""); // creates an empty image panel
				// if the square is occupied place an image of the piece occupying the place
				if (game.board[i][j].isOccupied()) {
					imgs[i][j] = new ImagePanel(game.board[i][j].getPiece().getImagePath());
					squares[i][j].add(imgs[i][j]); // adds the image
					imgs[i][j].setBounds(0, 0, size, size); // sets the bounds of the image
				}

				add(squares[i][j]); // adds the buttons
				// sets the bonds of the button with respect to the board
				squares[i][j].setBounds((j) * size, (7 - i) * size, size, size);
				squares[i][j].addActionListener(this); // adds an ActionListener for actions
			}
		}
	}
	
	public void setDepth(int depth) {
		this.depth = depth;
	}
	
	public int getDepth() {
		return this.depth;
	}

	/**
	 * Sets the light color for the board
	 *
	 * @param light the light color
	 */
	public void setLightColor(Color light) {
		setColors(light, darkColor);
	}

	/**
	 * Sets the dark color for the board
	 *
	 * @param dark the dark color
	 */
	public void setDarkColor(Color dark) {
		setColors(lightColor, dark);
	}

	/**
	 * Sets the colors for the board
	 *
	 * @param light the light color
	 * @param dark  the dark color
	 */
	public void setColors(Color light, Color dark) {
		// copies our variables
		this.lightColor = light;
		this.darkColor = dark;
		ChessFrame.lightColor = light;
		ChessFrame.darkColor = dark;
		// loops and sets the colors for all of the squares
		for (int i = squares.length - 1; i > -1; i--) {
			for (int j = 0; j < squares.length; j++) {
				// determines whether or not to make the square light or dark
				if ((i % 2 == 0 && j % 2 == 1) || (i % 2 == 1 && j % 2 == 0)) {
					squares[i][j].setBackground(lightColor);
				} else {
					squares[i][j].setBackground(darkColor);
				}
			}
		}
		// sets the colors for number and letter labels on the left an bottom
		for (int i = 0; i < cframe.letterLbls.length; i++) {
			// every other label is dark
			if (i % 2 != 0) {
				cframe.letterLbls[i].setBackground(lightColor);
				cframe.numLbls[i].setBackground(lightColor);
			} else {
				cframe.letterLbls[i].setBackground(darkColor);
				cframe.numLbls[i].setBackground(darkColor);
			}
			cframe.letterLbls[i].setForeground(lightColor);
			cframe.numLbls[i].setForeground(lightColor);
		}
	}

	/**
	 * Updates the board, setting the piece's images.
	 */
	public void updateBoard() {
		// loops through all of the buttons
		for (int i = squares.length - 1; i > -1; i--) {
			for (int j = 0; j < squares.length; j++) {
				squares[i][j].remove(imgs[i][j]);// gets rid of the old image
				// if the pieces is occupied set the image
				if (game.board[i][j].getPiece() != null) {
					imgs[i][j] = new ImagePanel(game.board[i][j].getPiece().getImagePath());
					squares[i][j].add(imgs[i][j]);
					imgs[i][j].setBounds(0, 0, size, size);
				} else { // sets the image to empty otherwise
					imgs[i][j] = new ImagePanel("");
					squares[i][j].add(imgs[i][j]);
					imgs[i][j].setBounds(0, 0, size, size);
				}
			}
		}
	}

	/**
	 * This method is called when a button is pressed
	 *
	 * @param e the ActionEvent for the button.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// loops through all the buttons
		if (game.getCurrentTurn() == Game.WHITE) { 
			for (int i = squares.length - 1; i > -1; i--) {
				for (int j = 0; j < squares.length; j++) {
					// if a square is pressed
					if (squares[i][j] == e.getSource()) {
						// If the square is occupied and it is the first click
						if (!game.board[i][j].isEmpty() && isFirstClick) {
							// saves the current piece
							this.pieceToMove = game.board[i][j].getPiece();
							// if the pieces is ours, set the background green
							if (game.board[i][j].getPiece().getColor() == game.getCurrentTurn()) {
								squares[i][j].setBackground(highLightGreen);
							} else {// otherwise set it red
								squares[i][j].setBackground(highLightRed);
							}
							isFirstClick = !isFirstClick;// not first click anymore

							// if is not the first click
						} else if (!isFirstClick) {
							isFirstClick = !isFirstClick;// set to opposite
							// if we click our piece on the second click
							if (game.board[i][j].isOccupied()
									&& game.board[i][j].getPiece().getColor() == game.getCurrentTurn()) {
								// resets the colors
								setColors(lightColor, darkColor);
								// recursively calls the method again, this time as a first click
								actionPerformed(e);
								return;
							}
							// save the position clicked
							this.positionToMoveTo = game.board[i][j];
							String chessNotation = null;
							// if the piece can move to the new postion and it is our color...
							if (this.pieceToMove.isValidMove(positionToMoveTo)
									&& pieceToMove.getColor() == game.getCurrentTurn()) {

								chessNotation = game.moveString(pieceToMove, positionToMoveTo);
								// save any piece that might be removed
								Piece removed = game.nextTurn(pieceToMove, positionToMoveTo).getLeft();

								// if a piece has been captured
								if (removed != null) {
									// add the piece to the side
									cframe.addRemovedPiece(removed);
									// remove the image from the square
									squares[i][j].remove(imgs[i][j]);
								}
								this.updateBoard();// update the board
								cframe.setTurnText(game.getCurrentTurn());// let the user know who's turn it is
							} else {
								setColors(lightColor, darkColor);
								return;
							}
							// check if the game is over
							if (game.isUnplayable(game.getCurrentTurn())) {
								// let the user know who won the game
								if (game.getCurrentTurn() == Game.BLACK && game.inCheck(Game.BLACK)) {
									chessNotation = chessNotation.substring(0, chessNotation.length() - 1) + "#";
									System.out.print(game.getTotalMoves() + chessNotation);
									System.out.print(" ");
									JOptionPane.showMessageDialog(cframe, "Checkmate, white wins!");
								} else if (game.getCurrentTurn() == Game.WHITE && game.inCheck(Game.WHITE)) {
									chessNotation = chessNotation.substring(0, chessNotation.length() - 1) + "#";
									System.out.print(game.getTotalMoves() + chessNotation);
									System.out.print(" ");
									JOptionPane.showMessageDialog(cframe, "Checkmate, black wins!");
								} else {
									System.out.print(game.getTotalMoves() + chessNotation);
									System.out.print(" ");
									JOptionPane.showMessageDialog(cframe, "Stalemate, it's a draw!");
								}
								cframe.reset(); // resets the game
							} else if (game.isDraw()) { // if the game is a draw
								System.out.print(game.getTotalMoves() + chessNotation);
								System.out.print(" ");
								JOptionPane.showMessageDialog(cframe, "Draw by insufficient material.");
								cframe.reset();
							} else {
								System.out.print(game.getTotalMoves() + chessNotation);
								System.out.print(" ");
							}
							setColors(lightColor, darkColor);// sets the colors
						}
					}
				}
			}
		}
	}

	public void runAI() {
		if (game.getCurrentTurn() == Game.BLACK) {
			long time = System.currentTimeMillis();
			TreeNode<Game> root = new TreeNode<Game>(game);
			TreeNode<Game> tree = game.constructGameTree(root, depth, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
			Pair<Position, Position> move = tree.getMove();

			if (move == null) {
				return;
			}

			Position curPos = move.getLeft();
			Position nextPos = move.getRight();

			int i = nextPos.getCol();
			int j = nextPos.getRow();

			Piece toMove = curPos.getPiece();

			String chessNotation = game.moveString(toMove, nextPos);

			Piece removed = game.nextTurnAutoQueen(toMove, nextPos).getLeft();

			// if a piece has been captured
			if (removed != null) {
				// add the piece to the side
				cframe.addRemovedPiece(removed);
				// remove the image from the square
				squares[i][j].remove(imgs[i][j]);
			}
			this.updateBoard(); // update the board
			cframe.setTurnText(game.getCurrentTurn()); // let the user know who's turn it is
			// check if the game is over
			if (game.isUnplayable(game.getCurrentTurn())) {
				// let the user know who won the game
				if (game.getCurrentTurn() == Game.BLACK && game.inCheck(Game.BLACK)) {
					chessNotation = chessNotation.substring(0, chessNotation.length() - 1) + "#";
					System.out.print(chessNotation);
					System.out.print(" ");
					JOptionPane.showMessageDialog(cframe, "Checkmate, white wins!");
				} else if (game.getCurrentTurn() == Game.WHITE && game.inCheck(Game.WHITE)) {
					chessNotation = chessNotation.substring(0, chessNotation.length() - 1) + "#";
					System.out.print(chessNotation);
					System.out.print(" ");
					JOptionPane.showMessageDialog(cframe, "Checkmate, black wins!");
				} else {
					System.out.print(chessNotation);
					System.out.print(" ");
					JOptionPane.showMessageDialog(cframe, "Stalemate, it's a draw!");
				}
				cframe.reset(); // resets the game
			} else if (game.isDraw()) { // if the game is a draw
				System.out.print(chessNotation);
				System.out.print(" ");
				JOptionPane.showMessageDialog(cframe, "Draw by insufficient material.");
				cframe.reset();
			} else {
				System.out.print(chessNotation);
				System.out.print(" ");
			}
			System.out.println(System.currentTimeMillis() - time);
		}
	}

	/**
	 * sets the current game
	 * 
	 * @param game the new game to be set
	 */
	public void setGame(Game game) {
		this.game = game;
	}
}
