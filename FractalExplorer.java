import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileFilter;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.event.ActionEvent;

/** This class provides an interactive UI for visually exploring fractals. */
public class FractalExplorer {
    /** Width and height of the display in pixels. */
    private int size;

    /** Reference to display that will be updated as fractal is computed. */
    private JImageDisplay display;

    /** Reference to the fractal generator. */
    private FractalGenerator gen;

    /** Reference to the rectangle representing the range of the
     *  complex plane that is displayed */
    private Rectangle2D.Double range;

    /** tracks the number of rows left to be computed for the current zoom */
    private int rowsRem;

    /** Constructor: initializes size field and returns new FractalExplorer instance. */
    public FractalExplorer(int size) {

        this.size = size;
        this.gen = new Mandelbrot();
        this.range = new Rectangle2D.Double();
        gen.getInitialRange(this.range);
        
    }

    /** Class to enable multithreaded fractal computations. */
    private class FractalWorker extends SwingWorker<Object, Object> {

        /** y-coordinate of the row to compute. */
        private int y;

        /** array to store computed RGB values for each pixel in the row */
        private int[] row;

        /** Constructor: initializes y-coordinate of row. */
        public FractalWorker(int ycoord) {
            y = ycoord;
        }

        /** Implementation of background task method; computes color values
         *  for the row specified by y */
        protected Object doInBackground() {
            this.row = new int[size];
            for (int x = 0; x < size; x++) {
                double xc = gen.getCoord(range.x, range.x + 
                                            range.width, size, x);
                double yc = gen.getCoord(range.y, range.y +
                                            range.height, size, this.y);

                int numIters = gen.numIterations(xc, yc);
                int rgbColor;

                if (numIters == -1) {
                    rgbColor = 0;
                }
                else {
                    float hue = 0.7f + (float) numIters / 200f;
                    rgbColor = Color.HSBtoRGB(hue, 1f, 1f);
                }
                this.row[x] = rgbColor;
            }
            return null;
        }

        /** Implementation fo the done method; draws pixels whose colors
         *  were computed in doInBackground() */
        protected void done() {
            for (int x = 0; x < size; x++) {
                display.drawPixel(x, this.y, this.row[x]);
            }
            display.repaint(0, 0, y, size, 1);
            rowsRem--;
            if (rowsRem == 0) {
                enableUI(true);
            }
        }
    }

    /** Event action listener class for the display. */
    private class Listener implements ActionListener {
        
        /** Action handler for reset button, save image 
         *  button, and drop down menu display components. */
        public void actionPerformed(ActionEvent e) {

            /** Event handling for buttons. */
            if (e.getSource() instanceof JButton) {
                JButton b = (JButton)(e.getSource());

                /** If event is from reset button, reset the 
                 *  displayed fractal image to its original state.
                 */
                if (b.getName().equals("reset")) {
                    gen.getInitialRange(range);
                    drawFractal();
                }

                /** If event is from save button, proceed with
                 *  user directed file saving process.
                 */
                else if (b.getName().equals("save")) {

                    JFileChooser fc = new JFileChooser();
                    FileFilter filter = new FileNameExtensionFilter("PNG Images", "png");
                    fc.setFileFilter(filter);
                    fc.setAcceptAllFileFilterUsed(false);

                    if (fc.showSaveDialog((Component)b.getParent().getParent()) == JFileChooser.APPROVE_OPTION) {
                        try {
                            ImageIO.write(display.getBufferedImage(), "png", fc.getSelectedFile());
                        } catch (Exception E) {
                            JOptionPane.showMessageDialog((Component)b.getParent().getParent(), E.getMessage(), "Cannot Save Image.", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }

            /** Event handling for dropdown menu. Changes the 
             *  current generator and range to the selected 
             *  generator and its respective range. */
            else if (e.getSource() instanceof JComboBox) {
                gen = (FractalGenerator)((JComboBox)e.getSource()).getSelectedItem();
                gen.getInitialRange(range);
                drawFractal();
            }
        }
    }

    /** Mouse adapter class to provide mouse action handling 
     *  for mouse click zooming. */
    private class mouseActionHandler extends MouseAdapter {
        
        /** Upon mouse click, this event handler zooms in 
         *  on an area surrounding the location of the click. */
        @Override
        public void mouseClicked(MouseEvent e) {
            if (rowsRem != 0) {
                return;
            }
            double x = gen.getCoord(range.x, range.x + 
                                    range.width, size, e.getX());
            double y = gen.getCoord(range.y, range.y +
                                    range.height, size, e.getY());
            double zoomScale = 0.5;
            gen.recenterAndZoomRange(range, x, y, zoomScale);
            drawFractal();
        }
    }

    /** Initializes all GUI related fields (local and global) and 
     * makes GUI visible to user. */
    public void createAndShowGUI(){

        /** Sets up frame and initializes action listener. */
        JFrame frame = new JFrame("Fractal Zoom");
        frame.getContentPane().setLayout(new BorderLayout());
        this.display = new JImageDisplay(this.size, this.size);
        Listener listener = new Listener();

        /** Sets up panel for dropdown menu for generator selection. */
        JComboBox dropdown = new JComboBox();
        dropdown.addItem(new Mandelbrot());
        dropdown.addItem(new Tricorn());
        dropdown.addItem(new BurningShip());
        dropdown.addActionListener(listener);
        JPanel droPanel = new JPanel();
        droPanel.add(new JLabel("Fractal: "));
        droPanel.add(dropdown);

        /** Sets up panel for reset and save buttons. */
        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(listener);
        resetButton.setName("reset");
        JButton saveImgButton = new JButton("Save Image");
        saveImgButton.addActionListener(listener);
        saveImgButton.setName("save");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(resetButton);
        buttonPanel.add(saveImgButton);
        
        /** Adds display, panels, and the mouse event handler to frame 
         *  and sets the frame's default close operation to exit on close.*/
        frame.add(this.display, BorderLayout.CENTER);
        frame.add(droPanel, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.addMouseListener(new mouseActionHandler());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        /** Finalizes frame. */
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
    }

    /** Enables or disables UI entities according to val to avoid concurrency bugs */
    private void enableUI(Boolean val) {
        JFrame dFrame = (JFrame) SwingUtilities.getRootPane(this.display).getParent();
        for (Component c : dFrame.getComponents()) {
            if (c instanceof JPanel) {
                for (Component csub : ((JPanel)c).getComponents()) {
                    if ((csub instanceof JButton) || (csub instanceof JComboBox)) {
                        csub.setEnabled(val);
                    }
                }
            }
        }
    }

    /** Draws fractal by updating the pixels of display. */
    private void drawFractal() {
        enableUI(false);
        this.rowsRem = size;
        for (int y = 0; y < size; y++) {
            FractalWorker w = new FractalWorker(y);
            w.execute();
        }
    }

    /** Main method for launching the explorer. */
    public static void main(String args[]) {
        FractalExplorer f = new FractalExplorer(800);
        f.createAndShowGUI();
        f.drawFractal();
    }
}