/*
 * Copyright (C) 2002 Thomas Hacklaender, mailto:hacklaender@iftm.de
 *
 * IFTM Institut fuer Telematik in der Medizin GmbH, www.iftm.de
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

import java.util.*;

import ij.*;
import ij.plugin.*;
import ij.plugin.filter.*;
import ij.process.*;
import ij.gui.*;
import ij.measure.*;

/**
 * The plugin calculates a T2 and proton density parameter images from images of 
 * real world MR examinations.
 * It is assumed, that a multi-echo Spin-Echo sequence was used for examination.
 * The T2 parameter image contains the T2 relaxation time of each pixel measured 
 * in milliseconds. The pixel values are limited to the interval from 0 to 8000.
 * The PD parameter image contains the proton density of each pixel measured in 
 * (percent * 10). The pixel values are limited to the interval from 0 to 1000.
 * The plugin requires the selection of a stack of at least three 16 bit grayscale images.
 *
 * @author Thomas Hacklaender
 * @version 2006-04-13
 */
public class T2_Calculator implements PlugInFilter {
    
    private final String    VERSION = "2.0";
    
    private double          tr = 5000.0;
    private double          teFirst = 20.0;
    private double          teStep = 20.0;
    private double          alpha = 90.0;
    private int             backgroundSignal = 5;
    private ImageStack      stack = null;
    private int             width = 0;
    private int             height = 0;
    private ShortProcessor  t2Result;
    private ShortProcessor  pdResult;
    
    
    /** 
     * This method is called once when the filter is loaded.
     * @param the argument specified for this plugin in IJ_Props.txt. May be blank. 
     * @param imp is the currently active image.
     * @return a flag word that specifies the filters capabilities.
     */
    public int setup(String arg, ImagePlus imp) {
        if (arg.equals("about")) {
            // Nothing to do
            return DONE;
        }
        
        if (imp != null) {
            stack = imp.getStack();
        }
        
        return DOES_16 + STACK_REQUIRED;
    }
    
    
    /**
     * Filters use this method to process the image. If the SUPPORTS_STACKS flag 
     * was set, it is called for each slice in a stack. ImageJ will lock the 
     * image before calling this method and unlock it when the filter is finished.
     * @param ip the ImageProcessor of the image to process.
     */
    public void run(ImageProcessor ip) {
        ImagePlus t2_img;
        ImagePlus pd_img;
        
        if (stack.getSize() < 3) {
            IJ.showMessage("At least 3 slices required.");
            return;
        }
        
        if (! getParameter()) return;
        
        if (stack == null) return;
        
        width = stack.getWidth();
        height = stack.getHeight();
        t2Result = new ShortProcessor(width, height);
        pdResult = new ShortProcessor(width, height);
        
        calculate();
        
        t2Result.setMinAndMax(0.0, 500.0);
        pdResult.setMinAndMax(0.0, 1000.0);
        
        pd_img = new ImagePlus("pd", pdResult);
        pd_img.show();
        t2_img = new ImagePlus("t2", t2Result);
        t2_img.show();
    }
    
    
    /**
     * This methodes does the calculation of the parameter image.
     */
    private void calculate() {
        int         numSlices = stack.getSize();
        double[][]  samples = new double[numSlices][2];
        double[]    weights = new double[numSlices];
        double[]    solution = null;
        int         signal;
        double      meanSignal;
        double      t2;
        double      pd;
        LeastSquare ls = new LeastSquare();
        double[][]  pdRaw = new double[width][height];
               
        for (int y = 0; y < height; y++) {
            
            IJ.showProgress(y, height);
            
            for (int x = 0; x< width; x++) {
                meanSignal = 0;
                for (int slice = 1; slice < numSlices; slice++) {
                    // TE
                    samples[slice][0] = teFirst + (slice - 1)* teStep;
                    // Signal intensity
                    signal = stack.getProcessor(slice).getPixel(x, y);
                    meanSignal += signal;
                    samples[slice][1] = Math.log((double) signal);
                    // No weighting of samples
                    weights[slice] = 1.0;
                }
                meanSignal /= numSlices;
                if (meanSignal < backgroundSignal) continue;
                
                ls.setSample(samples);
                ls.setWeights(weights);
                ls.normalizeWeights();
                ls.calculate();
                solution = ls.getSolution();
                
                // b
                pdRaw[x][y] = Math.exp(solution[0]);
                
                // m
                t2 = -1.0 / solution[1];
                
                // T2 must be positive
                if (t2 < 0.0) {
                    t2 = 0.0;
                }
                // T2 must be smaller than 8000 ms. Physiological the limit is 3600 ms.
                if (t2 > 8000.0) {
                    t2 = 8000.0;
                }
                t2Result.putPixel(x, y, (int) t2);
            }
            
            // Guess PD
            double max = 0.0;
            for (int x = 0; x< width; x++) {
                for (int slice = 1; slice < numSlices; slice++) {
                    if (pdRaw[x][y] > max) max = pdRaw[x][y];
                }
            }
            double factor = 1000.0 / max;
            for (int x = 0; x< width; x++) {
                for (int slice = 1; slice < numSlices; slice++) {
                    pd = pdRaw[x][y] * factor;
                    // pd must be positive
                    if (pd < 0.0) {
                        pd = 0.0;
                    }
                    // pd must be smaller than 100
                    if (pd > 1000.0) {
                        pd = 1000.0;
                    }
                    pdResult.putPixel(x, y, (int) pd);
                }
            }
        }
    }
    
    
    /**
     * Calculates the background signal intensity of the image.
     * @return the background signal intensity.
     */
    private double calculateBackground() {
        int     n = 0;
        double  mean = 0.0;
        
        for (int slice = 1; slice < stack.getSize(); slice++) {
            for (int y = 0; y < 20; y++) {
                for (int x = 0; x< 20; x++) {
                    mean += stack.getProcessor(slice).getPixel(x, y);
                    n++;
                }
            }
        }
        mean /= n;
        
        return 2 * mean;
    }
    
    
    /**
     * Displays a dialog box for input of user data.
     * @return true, if data were entered; false if the dialog box was canceled.
     */
    private boolean getParameter() {
        GenericDialog gd = new GenericDialog("T2 Calculator: Ver. " + VERSION);
        gd.addNumericField("Repetion Time TR:", tr, 1);
        gd.addNumericField("Echo Time First Echo TEfirst:", teFirst, 1);
        gd.addNumericField("Increment of Echo Time", teStep, 1);
        gd.addNumericField("Flip Angle:", alpha, 1);
        gd.addNumericField("Background Signal Intensity:", calculateBackground(), 0);
        gd.showDialog();
        if (gd.wasCanceled())
            return false;
        tr = gd.getNextNumber();
        teFirst = gd.getNextNumber();
        teStep = gd.getNextNumber();
        alpha = gd.getNextNumber();
        backgroundSignal = (int) gd.getNextNumber();
        
        return true;
    }
    
    
}