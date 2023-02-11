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
 * The plugin replaces a subset of pixels with the weighted mean of the
 * pixel values of the 48 surround-ing nearest neighbors. Only those pixels
 * whose values are lower than a given absolute value are proc-essed.
 * The plugin could be used to smooth pixels with numerical errors in the
 * calculation of the parameter images. Often these errors result in non
 * physiological low or negative pixel values.
 * The plugin requires the selection of a 16 bit grayscale image.
 *
 * @author Thomas Hacklaender
 * @version 2006-04-13
 */
public class Selective_Smooth_Filter implements PlugInFilter {
    
    private final String    VERSION = "2.0";
    
    private double          cutOff = 2.0;
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
                if (s < cutOff) {
                    neighborhood = calcNeighborhood(x, y);
                    ip.putPixel(x, y, neighborhood);
                }
            }
        }
        
    }
    
    
    /**
     *
     * @param x
     * @param y
     * @return
     */
    private int calcNeighborhood(int x, int y) {
        final int[][] weights = { {1, 1, 1, 1, 1, 1, 1},
        {1, 3, 3, 3, 3, 3, 1},
        {1, 3, 7, 7, 7, 3, 1},
        {1, 3, 7, 0, 7, 3, 1},
        {1, 3, 7, 7, 7, 3, 1},
        {1, 3, 3, 3, 3, 3, 1},
        {1, 1, 1, 1, 1, 1, 1} };
        int w;
        
        int mean = 0;
        int n = 0;
        
        for (int xOff = - 3; xOff <= 3; xOff++) {
            for (int yOff = - 3; yOff <= 3; yOff++) {
                if (((x + xOff) < 0) || ((x + xOff) >= width)) continue;
                if (((y + yOff) < 0) || ((y + yOff) >= height)) continue;
                w = weights[xOff + 3][yOff + 3];
                mean += w * ip.getPixel(x + xOff, y + yOff);
                n += w;
            }
        }
        
        if (n == 0) {
            return 0;
        }
        
        return mean / n;
    }
    
    
    /**
     * Displays a dialog box for input of user data.
     * @return true, if data were entered; false if the dialog box was canceled.
     */
    public boolean getParameter() {
        GenericDialog gd = new GenericDialog("Selective Smooth Filter: Ver. " + VERSION);
        gd.addNumericField("Cut Off Value:", cutOff, 1);
        gd.showDialog();
        if (gd.wasCanceled())
            return false;
        cutOff = gd.getNextNumber();
        
        return true;
    }
    
    
}
