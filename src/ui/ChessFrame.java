package ui;

import game.Game;
import game.Piece;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * This class deals with the graphical user interface
 *
 * @author Abel Macneil
 */
public final class ChessFrame extends JFrame implements WindowListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected static Color darkColor = new Color(125, 135, 150);
    protected static Color lightColor = new Color(232, 235, 239);
    protected Color backgroundColor = new Color(0, 0, 0);
    protected Game game;
    protected JLabel currentTurnLbl;
    protected JLabel checkLbl;
    protected JLabel[] letterLbls;
    protected JLabel[] numLbls;
    protected JLabel removedPieceLbl;
    private int removePieceLblX;
    private int removePieceLblY;
    private final int LETTER_NUM_WIDTH = 25;
    private final int WIDTH = 1000;
    private final int HEIGHT = 660;
    public static final int SQUARE_SIZE = 70;
    protected Board gameBoard;
    private ChessMenuBar menuBar;
    private final String SAVE_LOCATION = "src/ui/Game.save";

    /**
     * Creates an instance of a ChessFrame.
     */
    public ChessFrame(Game g) {
        super("Chess 2.0");

        setLayout(null);
        setSize(WIDTH, HEIGHT);
        //initializes the gui components
        game = initGame(g);//initialize the game
        gameBoard = new Board(SQUARE_SIZE, game, this);
        currentTurnLbl = new JLabel("");
        checkLbl = new JLabel("");
        checkLbl.setFont(new Font("Calibri", Font.BOLD, 30));
        checkLbl.setForeground(Color.white);
        menuBar = new ChessMenuBar(this);
        setTurnText(game.getCurrentTurn());
        currentTurnLbl.setFont(new Font("Calibri", Font.BOLD, 23));
        currentTurnLbl.setForeground(lightColor);
        removedPieceLbl = new JLabel();
        removePieceLblX = 0;
        removePieceLblY = 0;

        letterLbls = new JLabel[8];
        numLbls = new JLabel[8];
        
        //loops through the nunmber and letter labels on the left and bottom
        for (int i = 0; i < letterLbls.length; i++) {
            //sets to a letter represntation of the number
            letterLbls[i] = new JLabel((char) ('A' + i) + "", JLabel.CENTER);
            //sets the lable to a number
            numLbls[i] = new JLabel((7 - i + 1) + "", JLabel.CENTER);
            //every odd label has a light background, others have a dark one.
            if (i % 2 != 0) {
                letterLbls[i].setBackground(lightColor);
                numLbls[i].setBackground(lightColor);
            } else {
                letterLbls[i].setBackground(darkColor);
                numLbls[i].setBackground(darkColor);
            }
            //set the font to have a light color
            letterLbls[i].setForeground(lightColor);
            numLbls[i].setForeground(lightColor);

            //enable an etched border (looks nice)
            //letterLbls[i].setBorder(BorderFactory.createLineBorder(Color.BLACK));
            //numLbls[i].setBorder(BorderFactory.createLineBorder(Color.BLACK));
            add(letterLbls[i]);//add them
            add(numLbls[i]);
            //set the coordinates with respect to the frame
            letterLbls[i].setBounds(LETTER_NUM_WIDTH + i * SQUARE_SIZE, SQUARE_SIZE * 8, SQUARE_SIZE, LETTER_NUM_WIDTH);
            numLbls[i].setBounds(0, i * SQUARE_SIZE, LETTER_NUM_WIDTH, SQUARE_SIZE);
        }
        //add the gui components
        add(gameBoard);
        add(checkLbl);
        add(currentTurnLbl);
        add(removedPieceLbl);
        //set the bounds of the components
        gameBoard.setBounds(LETTER_NUM_WIDTH, 0, SQUARE_SIZE * 8, SQUARE_SIZE * 8);
        checkLbl.setBounds(754, -21, 200, 100);
        currentTurnLbl.setBounds(705, 10, 250, 100);
        removedPieceLbl.setBounds(600, 100, 700, 700);
        //set any removed pieces to be displayed
        for (int i = 0; i < game.removed.size(); i++) {
            addRemovedPiece(game.removed.get(i));
        }
        setJMenuBar(menuBar);//adds menu bar
        addWindowListener(this);//for window closing
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//exits when X button is clicked
        setResizable(false);
        getContentPane().setBackground(darkColor);//background
        setVisible(true);
    }

    /**
     * Resets the gui components
     */
    public void reset() {
        game = new Game();//new game
        this.setTurnText(game.getCurrentTurn());
        //remove gameboard an replace with new
        remove(gameBoard);
        gameBoard = new Board(SQUARE_SIZE, game, this);
        add(gameBoard);
        menuBar.update();
        //remove
        remove(removedPieceLbl);
        //sets coordinates of the gamboard
        gameBoard.setBounds(LETTER_NUM_WIDTH, 0, SQUARE_SIZE * 8, SQUARE_SIZE * 8);
        //resets the removed piece panel
        removedPieceLbl = new JLabel();
        removePieceLblX = 0;
        removePieceLblY = 0;
        add(removedPieceLbl);
        removedPieceLbl.setBounds(600, 100, 700, 700);
    }

    /**
     * adds a removed piece to the removed piece panel to the right of the board
     *
     * @param p the piece to be added
     */
    public void addRemovedPiece(Piece p) {
        int nImgY = 6;//number of images to be displayed vertically
        ImagePanel img = new ImagePanel(p.getImagePath());//image of the piece to be added
        removedPieceLbl.add(img);
        //sets the coordinates
        img.setBounds(removePieceLblX, removePieceLblY, SQUARE_SIZE, SQUARE_SIZE);
        //if there is no room downward set the coordinates to make a new column to the right
        if (removePieceLblY == (nImgY - 1) * (SQUARE_SIZE + 5)) {
            removePieceLblX += SQUARE_SIZE + 5;
            removePieceLblY = 0;
        } else {//increment the space between the pieces vertically
            removePieceLblY += SQUARE_SIZE + 5;
        }
    }

    /**
     * Sets the text for the current turn
     *
     * @param turn whose turn is is (Game.BLACK or Game.WHITE)
     */
    public void setTurnText(int turn) {
        if (turn == Game.BLACK) {
            currentTurnLbl.setText("Black's Turn (thinking...)");
        } else {
            currentTurnLbl.setText("White's Turn");
        }
    }
    
    /**
     * Initializes the game via the serialization process, if the {@code Game}
     * cannot be loaded, it returns a new {@code Game}.
     *
     * @return The {@code Game} to be played.
     */
    private Game initGame(Game x) {
    	if (x == null) {
    		Game g = loadGame();
    		return (g == null) ? new Game() : g;
    	}
        return x;
    }

    /**
     * Saves the current {@code Game} via the serialization process.
     */
    private void saveGame() {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(SAVE_LOCATION));
            oos.writeObject(game);
            oos.close();
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }

    /**
     * Loads a new game {@code Game} via the serialization process.
     *
     * @return The {@code Game} saved from the previous play, if the game cannot
     * be loaded it returns null.
     */
    private Game loadGame() {
        ObjectInputStream ois = null;
        Game g = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(SAVE_LOCATION));
            g = (Game) ois.readObject();
            ois.close();
        } catch (Exception ex) {
            return null;
        }
        return g;
    }

    /**
     * Triggered when the user closes the window; saves the game
     *
     * @param e
     */
    @Override
    public void windowClosing(WindowEvent e) {
        saveGame();
    }

    public static void main(String args[]) {
        new ChessFrame(null); // creates a new chessframe
    }

    /*The following are several methods that need to be implemented because
     * this class implements WindowListener.
     * None are used.
     */
    @Override
    public void windowOpened(WindowEvent we) {
    }

    @Override
    public void windowClosed(WindowEvent we) {
    }

    @Override
    public void windowIconified(WindowEvent we) {
    }

    @Override
    public void windowDeiconified(WindowEvent we) {
    }

    @Override
    public void windowActivated(WindowEvent we) {
    }

    @Override
    public void windowDeactivated(WindowEvent we) {
    }
}
