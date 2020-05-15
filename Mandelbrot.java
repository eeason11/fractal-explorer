import java.awt.geom.Rectangle2D;

/** This class is a subclass of FractalGenerator and thus
 *  implements the methods getInitialRange and 
 *  numIterations according to Mandelbrot fractal
 *  specific properties.
 */
public class Mandelbrot extends FractalGenerator {
    
    /** Mandelbrot specific x range used to set the initial range of the fractal. */
    private static final double RANGEX = -2.0;

    /** Mandelbrot specific y range used to set the initial range of the fractal. */
    private static final double RANGEY = -1.5;

    /** Mandelbrot specific dimension (width and height) used to set the 
     *  initial range of the fractal. */
    private static final double DIM = 3.0;

    /** Maximum number of iterations to be used during generation of the fractal. */
    public static final int MAX_ITERATIONS = 2000;

    /**
     * Sets the specified rectangle to contain the initial range suitable for
     * the fractal being generated.
     */
    
    public void getInitialRange(Rectangle2D.Double range) {
        range.x = RANGEX;
        range.y = RANGEY;
        range.width = DIM;
        range.height = DIM;
    }

    /**
     * Given a coordinate <em>x</em> + <em>iy</em> in the complex plane,
     * computes and returns the number of iterations before the fractal
     * function escapes the bounding area for that point.  A point that
     * doesn't escape before the iteration limit is reached is indicated
     * with a result of -1.
     */
    public int numIterations(double x, double y) {
        int numIterations = 0;
        double nextReal = 0;
        double nextImag = 0;
        double real = 0;
        double imag = 0;
        while ((numIterations < MAX_ITERATIONS) && ((real * real) + (imag * imag) <= 4)) {
            nextReal = (real * real) - (imag * imag) + x;
            nextImag = 2 * real * imag + y;
            real = nextReal;
            imag = nextImag;
            numIterations++;
        }   
        if (numIterations == MAX_ITERATIONS) {
            numIterations = -1;
        }
        return numIterations;
    }

    @Override
    /** Returns the name of the generator, "Mandelbrot". */
    public String toString() {
        return "Mandelbrot";
    }
}