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

import ij.*;
import ij.plugin.*;
import ij.plugin.filter.*;
import ij.process.*;
import ij.gui.*;
import ij.measure.*;

/**
 * The plugin calculates a T1 parameter image from images of real world MR examinations.
 * It is assumed, that a a multi-echo Spin-Echo sequence was used for examination.
 * The T1 parameter image contains the T1 relaxation time of each pixel measured 
 * in milliseconds. The pixel values are limited to the interval from 0 to 8000.
 * The plugin requires the selection of a stack of two 16 bit grayscale images.
 *
 * @author Thomas Hacklaender
 * @version 2006-04-13
 */
public class T1_Calculator implements PlugInFilter {
    
    private final String    VERSION = "2.0";
    
    private double          tr = 30.0;
    private double          te = 6.0;
    private double          alpha1 = 10.0;
    private double          alpha2 = 80.0;
    private int             backgroundSignal = 5;
    private ImageStack      stack = null;
    private int             width = 0;
    private int             height = 0;
    private ShortProcessor  t1Result;
    
    
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
        ImagePlus t1_img;
        
        if (stack.getSize() != 2) {
            IJ.showMessage("Exactly 2 slices required.");
            return;
        }
        
        if (! getParameter()) return;
        
        if (stack == null) return;
        
        width = stack.getWidth();
        height = stack.getHeight();
        t1Result = new ShortProcessor(width, height);
        
        calculate();
        
        t1Result.setMinAndMax(0.0, 1500.0);
        
        t1_img = new ImagePlus("t1", t1Result);
        t1_img.show();
    }
    
    
    /**
     * This methodes does the calculation of the parameter image.
     */
    private void calculate() {
        double q;
        double t1;
        double sina1 = Math.sin(Math.PI * alpha1 / 180.0);
        double cosa1 = Math.cos(Math.PI * alpha1 / 180.0);
        double sina2 = Math.sin(Math.PI * alpha2 / 180.0);
        double cosa2 = Math.cos(Math.PI * alpha2 / 180.0);
        
        for (int y = 0; y < height; y++) {
            
            IJ.showProgress(y, height);
            
            for (int x = 0; x< width; x++) {
                
                int s1 = stack.getProcessor(1).getPixel(x, y);
                int s2 = stack.getProcessor(2).getPixel(x, y);
                
                if ((s1 <= backgroundSignal) || (s2 <= backgroundSignal)) continue;
                
                q = ((double) s1) / ((double) s2);
                t1 = tr / Math.log((sina1 * cosa2 - q * sina2 * cosa1) / (sina1 - q * sina2));
                
                // T1 must be positive
                if (t1 < 0.0) {
                    t1 = 0.0;
                }
                // T1 must be smaller than 8000 ms. Physiological the limit is 3600 ms.
                if (t1 > 8000.0) {
                    t1 = 8000.0;
                }
                
                t1Result.putPixel(x, y, (int) t1);
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
        GenericDialog gd = new GenericDialog("T1 Calculator: Ver. " + VERSION);
        gd.addNumericField("Repetion Time TR:", tr, 1);
        gd.addNumericField("Echo Time TE:", te, 1);
        gd.addNumericField("Flip Angle alpha1:", alpha1, 1);
        gd.addNumericField("Flip Angle alpha2:", alpha2, 1);
        gd.addNumericField("Background Signal Intensity:", calculateBackground(), 0);
        gd.showDialog();
        if (gd.wasCanceled())
            return false;
        tr = gd.getNextNumber();
        te = gd.getNextNumber();
        alpha1 = gd.getNextNumber();
        alpha2 = gd.getNextNumber();
        backgroundSignal = (int) gd.getNextNumber();
        
        return true;
    }
    
    
}