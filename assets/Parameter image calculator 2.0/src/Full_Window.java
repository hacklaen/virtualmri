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
 * The plugin sets the window of the selected image to the interval of 0 up to
 * a given upper limit.
 * The plugin requires the selection of a 16 bit grayscale image.
 *
 * @author Thomas Hacklaender
 * @version 2006-04-13
 */
public class Full_Window implements PlugInFilter {
    
    private final String    VERSION = "2.0";
    
    private double          upperLimit = 1000.0;
    
    
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
        
        if (! getParameter()) return;
        
        theIP.setMinAndMax(0.0, upperLimit);
    }
    
    
    /**
     * Displays a dialog box for input of user data.
     * @return true, if data were entered; false if the dialog box was canceled.
     */
    public boolean getParameter() {
        GenericDialog gd = new GenericDialog("Full Window: Ver. " + VERSION);
        gd.addNumericField("Upper Limit:", upperLimit, 0);
        gd.showDialog();
        if (gd.wasCanceled())
            return false;
        upperLimit = gd.getNextNumber();
        
        return true;
    }
    
}
