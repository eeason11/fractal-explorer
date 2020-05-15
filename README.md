# fractal-explorer
Java UI application for viewing Mandelbrot, Tricorn, and Burning Ship fractals.

Includes click-to-zoom functionality, dropdown menu selection of fractals, a reset zoom button, and image saving functionality.
Implemented using Java AWT and Swing APIs for graphics and display components and ImageIO package for writing saved images to disk.
Takes advantage of Swing's SwingWorker multi-threading of tasks for computing pixel color values when user clicks to zoom or switches
fractals.

I authored every file except FractalGenerator.java, which is a brief abstract class provided by my Caltech CS 11 Instructor as starter code.
