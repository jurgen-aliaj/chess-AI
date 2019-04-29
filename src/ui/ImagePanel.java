package ui;

import java.awt.*;
import javax.swing.*;

/**
 * Creates a panel with an image on it
 *
 * @version Oct 31, 2011
 * @author Abel MacNeil
 */
public class ImagePanel extends JComponent {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//VARIABLES
    private final int SIZE = 1;
    private Image thumbNail;
    private String imagePath = null;

    /**
     * main constructor to create the desired image's file paths
     *
     * @param imagePath the location of the image's folder
     */
    public ImagePanel(String imagePath) {
        this.imagePath = imagePath;
    }

    //draws the image
    @Override
    protected void paintComponent(Graphics g) {
        //creates the image and draws it
        thumbNail = Toolkit.getDefaultToolkit().getImage(imagePath);
        if (thumbNail != null) {
            g.drawImage(thumbNail, SIZE, SIZE, this);
        }
    }
}
