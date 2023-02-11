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
 * BinaryImage is a 2-d array of byte.  All the values in a BinaryImage is always eithier a one or a zero.
 */

public class BinaryImage implements Image {


	/**
	 * Two dimensional integer array
	 */
	protected byte[][]	data;


	/**
	 * Cartesian width
	 */
	protected int				X;


	/**
	 * Cartesian height
	 */
	protected int				Y;


	/**
	 * Creates an empty two dimensional BinaryImage with a height and width of zero
	 */
	public BinaryImage() {
		X = 0;
		Y = 0;
		data = null;
	}


	/**
	 * Creates a two dimensional BinaryImage with a height and width of x and y repectively
	 */
	public BinaryImage(int x, int y) {
		X = x;
		Y = y;
		data = new byte[Y][X];
	}


	/**
	 * Gets the JavaImage from a JiglImage
	 */
	public ImageProducer getJavaImage() {

		// get range of this image
		int min = 0;
		int max = 1;

		// keep byte images in original range
		if (min >= 0 && max <= 255) {
			min = 0;
			max = 255;
		} 
		int range = max - min;

		// convert jigl image to java image
		int pix[] = new int[X * Y];
		int index = 0;
		int value = 0;

		for (int y = 0; y < Y; y++) {
			for (int x = 0; x < X; x++) {

				// scale image values
				value = (int) ((255.0 / range) * ((float) data[y][x] - min));

				value = 0x00FF & value;		// take lower 8 bits

				// put this pixel in the java image
				pix[index] = (0xFF << 24) | (value << 16) | (value << 8) | value;
				index++;
			} 
		} 

		// return java image
		return new MemoryImageSource(X, Y, pix, 0, X);
	} 


	/**
	 * Creates a two dimensional BinaryImage with a height and width of x and y repectively
	 * @param x width of image
	 * @param y height of image
	 * @param dat one dimensional array of short.  The array is length x*y.
	 */

	public BinaryImage(int x, int y, byte[] dat) {
		X = x;
		Y = y;
		int count = 0;

		data = new byte[Y][X];
		for (int a = 0; a < Y; a++) {
			for (int b = 0; b < X; b++) {

				if (dat[a * Y + b] > 0) {
					data[a][b] = 1;
				} else {
					data[a][b] = 0;
				}
			} 
		}
	}


	/**
	 * Creates a two dimensional BinaryImage from a GrayImage.
	 * Any pixel value above 0 is assigned the 1.
	 */
	public BinaryImage(GrayImage img) {
		X = img.X();
		Y = img.Y();
		data = new byte[Y][X];
		for (int a = 0; a < X; a++) {
			for (int b = 0; b < Y; b++) {
				if (img.get(a, b) > 0) {
					data[b][a] = 1;
				} else {
					data[b][a] = 0;
				}
			}
		}
	}


	/**
	 * Creates a two dimensional BinaryImage from a RealGrayImage.
	 * Any pixel value above 0 is assigned the 1.
	 */
	public BinaryImage(RealGrayImage img) {
		X = img.X();
		Y = img.Y();
		data = new byte[Y][X];
		for (int a = 0; a < X; a++) {
			for (int b = 0; b < Y; b++) {
				if (img.get(a, b) > 0) {
					set(a, b, 1);
				} else {
					set(a, b, 0);
				}
			}
		}
	}


	/**
	 * Creates a two dimensional BinaryImage (shallow copy) from BinaryImage img
	 */
	public BinaryImage(BinaryImage img) {
		X = img.X();
		Y = img.Y();
		data = img.data;
	}


	/**
	 * Makes a deep copy of this image
	 * @param none
	 * @return a deep copy of BinaryImage
	 */
	public Image copy() {
		BinaryImage g = new BinaryImage(X, Y);

		for (int y = 0; y < Y; y++) {
			for (int x = 0; x < X; x++) {
				g.data[y][x] = data[y][x];
			} 
		} 
		return g;
	} 


	/**
	 * Makes a deep copy of this image in a Region of Interest
	 * @param none
	 * @return a deep copy of BinaryImage
	 */
	public Image copy(ROI r) {
		BinaryImage g = new BinaryImage(X, Y);

		for (int y = r.uy(); y < r.ly(); y++) {
			for (int x = r.lx(); x < r.ux(); x++) {
				g.data[y][x] = data[y][x];
			} 
		} 
		return g;
	} 


	/**
	 * Returns the width (maximum X value)
	 * @param none
	 */
	public final int X() {
		return X;
	} 


	/**
	 * Returns the height (maximum Y value)
	 * @param none
	 */
	public final int Y() {
		return Y;
	} 


	/**
	 * Returns the pixel value at the given x, y value
	 * @param x  the X coordinant
	 * @param y  the Y coordinant
	 */
	public final byte get(int x, int y) {
		return (byte) data[y][x];
	} 


	/**
	 * Sets the pixel value at x, y to a given value
	 * @param x the X coordinant
	 * @param y the Y coordinant
	 * @param value the value to set the pixel to if greater than zero, it is given a value of 1
	 */
	public final void set(int x, int y, int value) {
		if (value > 0) {
			data[y][x] = 1;
		} else {
			data[y][x] = 0;
		}
	} 


	/**
	 * Finds the union between this image and another BinaryImage
	 * @return this
	 */
	public final BinaryImage union(BinaryImage image) {

		for (int y = 0; y < Y; y++) {
			for (int x = 0; x < X; x++) {
				if (image.get(x, y) != data[y][x]) {
					data[y][x] = 1;

				} 
			}
		}
		return this;
	} 


	/**
	 * Finds the intersection between this image and another BinaryImage
	 * @return this
	 */
	public final BinaryImage intersection(BinaryImage image) {

		for (int y = 0; y < Y; y++) {
			for (int x = 0; x < X; x++) {
				if (image.get(x, y) != data[y][x]) {
					data[y][x] = 0;

				} 
			}
		}
		return this;
	} 


	/**
	 * Returns the complement of this image
	 */
	public final BinaryImage compliment(BinaryImage image) {

		for (int y = 0; y < Y; y++) {
			for (int x = 0; x < X; x++) {
				if (data[y][x] == 0) {
					data[y][x] = 1;
				} else {
					data[y][x] = 0;
				}
			}
		}

		return this;
	} 


	/**
	 * Counts the number of "on" pixels
	 */
	public final int count() {

		int count = 0;

		for (int y = 0; y < Y; y++) {
			for (int x = 0; x < X; x++) {
				if (data[y][x] != 0) {
					count++;

				} 
			}
		}
		return count;
	} 


	/**
	 * Returns the difference of this image and a BinaryImage
	 * @return this
	 */
	public final BinaryImage difference(BinaryImage image) {

		for (int y = 0; y < Y; y++) {
			for (int x = 0; x < X; x++) {
				if ((data[y][x] == 1) && (image.get(x, y) == 0)) {
					data[y][x] = 1;
				} else if ((data[y][x] == 1) && (image.get(x, y) == 0)) {
					data[y][x] = 1;
				} else {
					data[y][x] = 0;
				}
			}
		}

		return this;
	} 


	/**
	 * Performs a shift on this image
	 * @param horizonal for right shift horizonal is positive for left it is negative
	 * @param vertical for down shift vertical is positive for up it is negative
	 * @return this
	 */
	public final BinaryImage shift(int horizonal, int vertical) {

		BinaryImage image2 = (BinaryImage) this.copy();
		byte				set1 = 0;

		for (int y = 0; y < Y; y++) {
			for (int x = 0; x < X; x++) {
				if (y - vertical < 0 || y - vertical >= Y) {
					set1 = 0;
				} else if (x - horizonal < 0 || x - horizonal >= X) {
					set1 = 0;
				} else {
					set1 = image2.get(x - horizonal, y - vertical);
				}
				data[y][x] = set1;
			} 
		}
		return this;
	} 

}



/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

