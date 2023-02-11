/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

/*
 * JIGL--Java Imaging and Graphics Library
 * Copyright (C)1999 Brigham Young University
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 * A copy of the GNU Library General Public Licence is contained in
 * /jigl/licence.txt
 */
package jigl.image;
import jigl.*;

import java.awt.image.*;


/**
 * Image is the interface for ColorImage, ComplexImage, ConvertSignal, DiscreteSignal <p>
 * In addition, all typed Image classes must support the following typed methods:
 * 
 * get
 * set
 * clear
 * ...
 */
public interface Image {


	/**
	 * Returns the width of the image
	 */
	public int X();


	/**
	 * Returns the height of the image
	 */
	public int Y();


	/**
	 * Returns a string representation of an image
	 */
	public String toString();


	/**
	 * Turns this image into a Java Image (java.awt.Image).
	 * @param none
	 * @see java.awt.ImageProducer
	 */
	public ImageProducer getJavaImage();


	/**
	 * Returns a deep copy of an image
	 */
	public Image copy();


	/**
	 * Method declaration
	 *
	 *
	 * @param r
	 *
	 * @return
	 *
	 * @see
	 */
	public Image copy(ROI r);


}		// image




/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

