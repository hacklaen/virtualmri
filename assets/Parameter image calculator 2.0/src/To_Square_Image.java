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
 * The plugin creates a new image of square size and copies the selected image 
 * onto the centre of newly created image. The size of the created image 
 * corresponds to the width or height of the selected image whichever is bigger.
 * The plugin requires the selection of a 16 bit grayscale image.
 *
 * @author Thomas Hacklaender
 * @version 2006-04-13
 */
public class To_Square_Image implements PlugInFilter {
    
    private final String    VERSION = "2.0";
    
    private String          title;

    
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
            title = imp.getTitle();
        }
        
        return DOES_16;
    }
    
    
    /**
     * Filters use this method to process the image. If the SUPPORTS_STACKS flag 
     * was set, it is called for each slice in a stack. ImageJ will lock the 
     * image before calling this method and unlock it when the filter is finished.
     * @param ip the ImageProcessor of the image to process.
     */
    public void run(ImageProcessor ip) {
        int size;
        ShortProcessor destination = null;
        ImagePlus destination_img;
        int xOffset;
        int yOffset;
        
        int width = ip.getWidth();
        int height = ip.getHeight();
        
        if (width > height) {
            size = width;
        } else {
            size = height;
        }
        
        xOffset = (size - width) / 2;
        yOffset = (size -height) / 2;
        destination = new ShortProcessor(size, size);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x< width; x++) {
                destination.putPixel(x + xOffset, y + yOffset, ip.getPixel(x, y));
            }
        }
        
        destination_img = new ImagePlus(title, destination);
        destination_img.show();
    }
    
}
