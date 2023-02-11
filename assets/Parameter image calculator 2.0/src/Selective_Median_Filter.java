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
 * The plugin replaces a subset of pixels with the median of the pixel
 * values of the 8 surrounding nearest neighbors. Only those pixels whose
 * values are higher than the median of the nearest neighbors multi-plied
 * by a given factor are processed.
 * The plugin could be used to smooth pixels with numerical errors in the
 * calculation of the parameter images. Often these errors result in non
 * physiological high pixel values.
 * The plugin requires the selection of a 16 bit grayscale image.
 *
 * @author Thomas Hacklaender
 * @version 2006-04-13
 */

public class Selective_Median_Filter implements PlugInFilter {
    
    private final String    VERSION = "2.0";
    
    private double          limitFactor = 3.0;
    private ImageProcessor  ip = null;
    private int             width;
    private int             height;
    
    
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
        return DOES_16;
    }
    
    
    /**
     * Filters use this method to process the image. If the SUPPORTS_STACKS flag 
     * was set, it is called for each slice in a stack. ImageJ will lock the 
     * image before calling this method and unlock it when the filter is finished.
     * @param ip the ImageProcessor of the image to process.
     */
    public void run(ImageProcessor theIP) {
        int s;
        int neighborhood;
        int limit;
        
        this.ip = theIP;
        
        width = ip.getWidth();
        height = ip.getHeight();
        
        if (! getParameter()) return;
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x< width; x++) {
                s = ip.getPixel(x, y);
                
                neighborhood = calcNeighborhood(x, y);
                limit = ((int)(limitFactor * neighborhood));
                if (s > limit) {
                    ip.putPixel(x, y, neighborhood);
                }
            }
        }
        
    }
    
    
    /**
     * Calculates the median of the signal intensity of the 8 neares neighbors 
     * of a given pixel.
     * @param x x coordinate of the central pixel.
     * @param y y coordinate of the central pixel.
     * @return the median of the signal intensity of the 8 neares neighbors.
     */
    private int calcNeighborhood(int x, int y) {
        int sMedian;
        int[] sArray = new int[8];
        int n = 0;
        
        for (int nx = x - 1; nx <= x + 1; nx++) {
            for (int ny = y - 1; ny <= y + 1; ny++) {
                if ((nx == x) && (ny == y)) continue;
                if ((nx < 0) || (nx >= width)) continue;
                if ((ny < 0) || (ny >= height)) continue;
                sArray[n] = ip.getPixel(nx, ny);
                n++;
            }
        }
        
        if (n == 0) {
            return 0;
        }
        
        Arrays.sort(sArray, 0, n-1);
        
        if ((n % 2) == 0) {
            // even
            sMedian = (sArray[n / 2] + sArray[(n / 2) - 1]) / 2;
        } else {
            // odd
            sMedian = sArray[n / 2];
        }
        
        return sMedian;
    }
    
    
    /**
     * Displays a dialog box for input of user data.
     * @return true, if data were entered; false if the dialog box was canceled.
     */
    public boolean getParameter() {
        GenericDialog gd = new GenericDialog("Selective Median Filter: Ver. " + VERSION);
        gd.addNumericField("Factor for lower limit of inclusion of pixels:", limitFactor, 1);
        gd.showDialog();
        if (gd.wasCanceled())
            return false;
        limitFactor = gd.getNextNumber();
        
        return true;
    }
    
    
}
