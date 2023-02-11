/*
 * Copyright (C) 1999 Christian Schalla, Andreas Truemper
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU  General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * http://www.gnu.org/copyleft/copyleft.html
 */

import ij.*;


/**
 * This class calculates a linear least square fit to a given matrix of value.<br>
 * References:<br>
 * R. Sedgewick 	        : "Algorithmen in C"<br>
 * Conte, de Boor 	  	: "Elementary Numerical Analysis"<br>
 * Rivest, Corhen Leiserson  	: "Introduction to Algorithms"<br>
 * Gene H. Holub, C. Van Loan	: "Matrix Computations"<br>
 * Forsythe		        : "Computer-Verfahren"<br>
 * <br>
 * Modifications:<br>
 * Thomas Hacklaender 2002-11-2:
 * Umstellung auf die Package-Hirarchie de.iftm.ij.plugins.vmrt.* <br>
 * Thomas Hacklaender 2000-05-10:
 * Textausgabe in ImageJ statt Debug-Window. <br>
 *
 * @author   Christian Schalla
 * @author   Andreas Truemper
 * @version  2002-11-02, 1.2
 */
public class LeastSquare {
    
    public final boolean	DEBUG_MODE = false;
    
    private double[][]		samples;
    private double[]		weights;
    private double[][]		leastsquarematrix;
    private double[][]		vectors;
    private double[]		solution;
    
    
    /**
     * Constructor declaration.
     */
    public LeastSquare() {}
    
    
    /**
     * Sets the matrix of input pairs x, y.
     *
     * @param sam the matrix of input pairs [x][y]
     */
    public void setSample(double[][] sam) {
        samples = sam;
    }
    
    
    /**
     * Sets the vector of weights for the input pairs.
     *
     * @param w the vector of weights for the input pairs.
     */
    public void setWeights(double w[]) {
        weights = w;
    }
    
    
    /**
     * Performes the linear least square fit to the given input pairs. The fit
     * is of the form y(x) = m * x + b
     *
     */
    public void calculate() {
        createLeastSquareMatrix();
        eliminate();
        substitute();
    }
    
    
    /**
     * Returns the result of the linear least square fit to y(x) = m * x + b.
     *
     * @return [0] parameter b, [1] parameter m.
     */
    public double[] getSolution() {
        return solution;
    }
    
    
    /**
     * Method declaration
     *
     */
    public void normalizeWeights() {
        double  sum = 0.0;
        
        for (int i = 0; i < weights.length; i++) {
            sum += weights[i];
        }
        for (int i = 0; i < weights.length; i++) {
            weights[i] = weights[i] / sum;
        }
    }
    
    
    /**
     * Method declaration
     *
     */
    private void createLeastSquareMatrix() {
        leastsquarematrix = new double[2][3];
        int M = 2;
        int N = samples.length;
        
        fillvectors();
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < M + 1; j++) {
                double  t = 0.0;
                
                for (int k = 0; k < N; k++) {
                    t += vectors[i][k] * vectors[j][k] * weights[k];
                }
                leastsquarematrix[i][j] = t;
            }
        }
        if (DEBUG_MODE) {
            printMatrix(leastsquarematrix);
        }
    }
    
    
    /**
     * Method declaration
     *
     */
    private void fillvectors() {
        
        // Hilfsmethode:
        // Erstelle die Vektoren zum Füllen der Matrix
        vectors = new double[3][samples.length];
        int i, j;
        
        // Werte für die 0-te Potenz (alle auf 1)
        for (i = 0; i < vectors[0].length; i++) {
            vectors[0][i] = 1;
        }
        
        // Werte für x^1, x-Werte der vorgegebenen Punkte
        for (i = 0; i < samples.length; i++) {
            vectors[1][i] = samples[i][0];
        }
        
        // y-Werte der Punkte
        for (i = 0; i < samples.length; i++) {
            vectors[2][i] = samples[i][1];
        }
    }
    
    
    /**
     * Method declaration
     *
     */
    private void eliminate() {
        
        // Elimination der Matrix nach dem Gauß-Verfahren.
        int		i = 0;
        int		j = 0;
        int		k = 0;
        int		max = 0;
        double  t;
        int		N = leastsquarematrix[0].length - 1;
        
        for (i = 0; i < N; i++) {
            max = i;
            
            // Pivot-Element suchen
            for (j = i + 1; j < N; j++) {
                if (Math.abs(leastsquarematrix[j][i]) > Math.abs(leastsquarematrix[max][i])) {
                    max = j;
                }
            }
            
            // Zeilen vertauschen, so daß die Zeile mit dem größten Element in der
            // aktuell betrachteten Spalte die erste Zeile ist.
            for (k = i; k < N + 1; k++) {
                t = leastsquarematrix[i][k];
                leastsquarematrix[i][k] = leastsquarematrix[max][k];
                leastsquarematrix[max][k] = t;
            }
            if (DEBUG_MODE) {
                printMatrix(leastsquarematrix);
            }
            
            // Die eigentliche Elimination:
            // Erzeuge Einheitsmatrix
            for (j = i + 1; j < N; j++) {
                for (k = N; k >= i; k--) {
                    leastsquarematrix[j][k] -= leastsquarematrix[i][k] * leastsquarematrix[j][i] / leastsquarematrix[i][i];
                }
            }
            if (DEBUG_MODE) {
                printMatrix(leastsquarematrix);
            }
        }
    }
    
    
    /**
     * Method declaration
     *
     */
    private void substitute() {
        int     j, k;
        double  t;
        int	N = leastsquarematrix.length;
        
        solution = new double[2];
        
        for (j = N - 1; j >= 0; j--) {
            t = 0.0;
            for (k = j + 1; k < N; k++) {
                t += leastsquarematrix[j][k] * solution[k];
            }
            solution[j] = (leastsquarematrix[j][N] - t) / leastsquarematrix[j][j];
        }
    }
    
    
    /**
     * Only for debugging. Prints a matrix to stdout.
     *
     * @param a the matrix to print.
     */
    public void printMatrix(double a[][]) {
        int i, j;
        
        i = a.length;
        j = a[0].length;
        
        IJ.write(i + "x" + j + "-Matrix");
        for (i = 0; i < a.length; i++) {
            IJ.write("(");
            for (j = 0; j < a[0].length; j++) {
                IJ.write(" " + a[i][j]);
            }
            IJ.write(" )" + "\n");
        }
        
    }
    
    
    /**
     * Only for debugging. Prints to matrix of input pairs to stdout.
     *
     */
    public void printSample() {
        for (int i = 0; i < samples.length; i++) {
            IJ.write(i + "(x = " + samples[i][0] + ", y = " + samples[i][1] + ")" + "\n");
        }
    }
    
}
