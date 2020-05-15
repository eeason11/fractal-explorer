import javax.swing.JComponent;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.Dimension;

/** This class derives the JComponent class of the Swing API. Its purpose
 *  is to represent and store an image that is easily manipulated and
 *  can be displayed at any time. A single BufferedImage field is used to
 *  store the current state of the image.
 */
public class JImageDisplay extends JComponent {
    /** Reference to the image whose contents will be written to. **/
    private BufferedImage image;

    /** Initialize a new image to manipulate and display and setup its dimensions
     * to the specified values. */
    public JImageDisplay(int width, int height) {
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        super.setPreferredSize(new Dimension(width, height));
    }

    /** Draws the graphics object g to the image. */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(this.image, 0, 0, this.image.getWidth(), this.image.getHeight(), null);
    }

    /** Sets all pixels in the image to black. */
    public void clearImage() {
        int width = this.image.getWidth();
        int height = this.image.getHeight();
        int rgbBlack = 0;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                this.image.setRGB(i, j, rgbBlack);
            }
        }
    }

    /** Sets the pixel at location (x, y) to color rgbColor. */
    public void drawPixel(int x, int y, int rgbColor) {
        this.image.setRGB(x, y, rgbColor);
    }

    /** Public accessor method for BufferedImage image field. */
    public BufferedImage getBufferedImage() {
        return this.image;
    }
}