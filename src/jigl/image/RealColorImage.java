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
 * A color image is a set of three gray image planes.
 */
public class RealColorImage implements Image {


	/**
	 * Set of 3 GrayImages
	 */
	protected RealGrayImage[] plane = new RealGrayImage[3];


	/**
	 * Cartesian width
	 */
	protected int							X;


	/**
	 * Cartesian height
	 */
	protected int							Y;


	/**
	 * RGB Color Model
	 */
	public static final int		RGB = 0;


	/**
	 * CMY Color Model
	 */
	public static final int		CMY = 1;


	/**
	 * YIQ Color Model
	 */
	public static final int		YIQ = 2;


	/**
	 * HSV Color Model
	 */
	public static final int		HSV = 3;


	/**
	 * HLS Color Model
	 */
	public static final int		HLS = 4;


	/**
	 * Current Color Model; Default is RGB
	 */
	protected int							colorModel = RGB;


	/*
	 * constructors
	 */


	/**
	 * Creates an empty two dimensional RealColorImage with a height and width of zero
	 */
	public RealColorImage() {
		X = 0;
		Y = 0;
		plane[0] = null;
		plane[1] = null;
		plane[2] = null;
	}


	/**
	 * Creates a two dimensional RealColorImage with a height and width of x and y repectively
	 */
	public RealColorImage(int x, int y) {
		X = x;
		Y = y;
		plane[0] = new RealGrayImage(X, Y);
		plane[1] = new RealGrayImage(X, Y);
		plane[2] = new RealGrayImage(X, Y);
	}


	/**
	 * Creates a two dimensional GrayImage (shallow copy) from GrayImage img
	 */
	public RealColorImage(RealColorImage img) {
		X = img.X();
		Y = img.Y();
		plane[0] = img.plane(1);
		plane[1] = img.plane(2);
		plane[2] = img.plane(3);
	}


	/**
	 * Creates a two dimensional GrayImage from the standard java.awt.Image
	 */
	public RealColorImage(java.awt.Image img) {
		int w = img.getWidth(DummyObserver.dummy);
		int h = img.getHeight(DummyObserver.dummy);

		X = w;
		Y = h;
		plane[0] = new RealGrayImage(X, Y);
		plane[1] = new RealGrayImage(X, Y);
		plane[2] = new RealGrayImage(X, Y);
		InitFromImage(img, 0, 0, w, h);
	}


	/**
	 * Makes a deep copy of this image
	 * @param none
	 * @return a deep copy of RealColorImage
	 */
	public Image copy() {
		RealColorImage	c = new RealColorImage(X, Y);

		c.setColorModel(colorModel);
		for (int y = 0; y < Y; y++) {
			for (int x = 0; x < X; x++) {
				c.plane[0].set(x, y, plane[0].get(x, y));
				c.plane[1].set(x, y, plane[1].get(x, y));
				c.plane[2].set(x, y, plane[2].get(x, y));
			} 
		} 
		return c;
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
	 * Returns the color model.
	 * <DT><DL><DL><RGB = 0 </DT>
	 * <DT>CMY = 1 </DT>
	 * <DT>YIQ = 2 </DT>
	 * <DT>HSV = 3 </DT>
	 * <DT>HLS = 4 </DT>
	 * <DT>default = RGB</DT>
	 * <DT></DT></DL></DL>
	 * @param none
	 */
	public final int getColorModel() {
		return colorModel;
	} 


	/**
	 * Returns the color model.
	 * <DT><DL><DL><RGB = 0 </DT>
	 * <DT>CMY = 1 </DT>
	 * <DT>YIQ = 2 </DT>
	 * <DT>HSV = 3 </DT>
	 * <DT>HLS = 4 </DT>
	 * <DT>default = RGB</DT>
	 * <DT></DT></DL></DL>
	 * @param none
	 */
	public final void setColorModel(int cm) {
		colorModel = cm;
	} 


	/**
	 * Returns the plane of this image<P>
	 * If this were an RGB image, plane(0) would return the
	 * red plane. <P>
	 * @param p the plane of this image
	 * @return a shallow copy
	 */
	public final RealGrayImage plane(int p) {
		return plane[p];
	} 


	/**
	 * Set the plane to a RealColorImage pl<P>
	 * @param p the plane of this image
	 * @param pl RealColorImage to set the plane to
	 * @return a shallow copy
	 */
	public final void setPlane(int p, RealGrayImage pl) {
		plane[p] = pl;
	} 


	/**
	 * initializes plane data from a Java image.	Used by the
	 * java image constructor.
	 */
	private void InitFromImage(java.awt.Image img, int x, int y, int w, int h) {
		int						pixels[] = new int[w * h];
		PixelGrabber	pg = new PixelGrabber(img, x, y, w, h, pixels, 0, w);

		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
			System.err.println("interrupted waiting for pixels!");
			return;
		} 
		if ((pg.status() & ImageObserver.ABORT) != 0) {
			System.err.println("image fetch aborted or errored");
			return;
		} 

		// convert from grabbed pixels
		int red = 0;
		int green = 0;
		int blue = 0;
		int index = 0;

		for (int iy = 0; iy < Y; iy++) {
			for (int ix = 0; ix < X; ix++) {
				red = 0x0FF & pixels[index] >> 16;
				green = 0x0FF & pixels[index] >> 8;
				blue = 0x0FF & pixels[index];
				plane[0].set(ix, iy, (short) red);
				plane[1].set(ix, iy, (short) green);
				plane[2].set(ix, iy, (short) blue);
				index++;
			} 
		} 
	} 


	/**
	 * Returns the pixel value at the given x, y value as a triplet
	 * @param x  the X coordinant
	 * @param y  the Y coordinant
	 * @return three element array of integers
	 */
	public final float[] get(int x, int y) {
		float[] color = new float[3];

		color[0] = plane[0].get(x, y);
		color[1] = plane[1].get(x, y);
		color[2] = plane[2].get(x, y);
		return color;
	} 


	/**
	 * Sets the pixel value at the given x, y value as a triplet
	 * @param x  the X coordinant
	 * @param y  the Y coordinant
	 * @param v  array of three integers holding the values for the set
	 */
	public final void set(int x, int y, float[] v) {
		plane[0].set(x, y, v[0]);
		plane[1].set(x, y, v[1]);
		plane[2].set(x, y, v[2]);
	} 


	/**
	 * Adds a triplet to a single pixel
	 * @param x X-coordinant
	 * @param y Y-coordinant
	 * @param value triplet to add to the pixel
	 */
	public final void add(int x, int y, float[] value) {
		plane[0].add(x, y, value[0]);
		plane[1].add(x, y, value[1]);
		plane[2].add(x, y, value[2]);
	} 


	/**
	 * Adds a triplet to a single pixel
	 * @param x X-coordinant
	 * @param y Y-coordinant
	 * @param val0 value to add to the pixel in the first plane
	 * @param val1 value to add to the pixel in the second plane
	 * @param val2 value to add to the pixel in the third plane
	 */
	public final void add(int x, int y, float val0, float val1, float val2) {
		plane[0].add(x, y, val0);
		plane[1].add(x, y, val1);
		plane[2].add(x, y, val2);
	} 


	/**
	 * Subtracts a triplet from a single pixel
	 * @param x X-coordinant
	 * @param y Y-coordinant
	 * @param value triplet to add to the pixel
	 */
	public final void subtract(int x, int y, float[] value) {
		plane[0].subtract(x, y, value[0]);
		plane[1].subtract(x, y, value[1]);
		plane[2].subtract(x, y, value[2]);
	} 


	/**
	 * Subtracts a triplet from a single pixel
	 * @param x X-coordinant
	 * @param y Y-coordinant
	 * @param val0 value to subtract from the pixel in the first plane
	 * @param val1 value to subtract from the pixel in the second plane
	 * @param val2 value to subtract from the pixel in the third plane
	 */
	public final void subtract(int x, int y, float val0, float val1, float val2) {
		plane[0].subtract(x, y, val0);
		plane[1].subtract(x, y, val1);
		plane[2].subtract(x, y, val2);
	} 


	/**
	 * Multiplies a triplet with a single pixel
	 * @param x X-coordinant
	 * @param y Y-coordinant
	 * @param value triplet to add to the pixel
	 */
	public final void multiply(int x, int y, float[] value) {
		plane[0].multiply(x, y, value[0]);
		plane[1].multiply(x, y, value[1]);
		plane[2].multiply(x, y, value[2]);
	} 


	/**
	 * Multiplies a triplet with a single pixel
	 * @param x X-coordinant
	 * @param y Y-coordinant
	 * @param val0 value to multiply the pixel by in the first plane
	 * @param val1 value to multiply the pixel by in the second plane
	 * @param val2 value to multiply the pixel by in the third plane
	 */

	public final void multiply(int x, int y, float val0, float val1, float val2) {
		plane[0].multiply(x, y, val0);
		plane[1].multiply(x, y, val1);
		plane[2].multiply(x, y, val2);
	} 


	/**
	 * Divides a single pixel by a triplet
	 * @param x X-coordinant
	 * @param y Y-coordinant
	 * @param value triplet to add to the pixel
	 */
	public final void divide(int x, int y, float[] value) {
		plane[0].divide(x, y, value[0]);
		plane[1].divide(x, y, value[1]);
		plane[2].divide(x, y, value[2]);
	} 


	/**
	 * Divides a single pixel by a triplet
	 * @param x X-coordinant
	 * @param val0 value to divide by the pixel in the first plane
	 * @param val1 value to divide by the pixel in the second plane
	 * @param val2 value to divide by the pixel in the third plane
	 */
	public final void divide(int x, int y, float val0, float val1, float val2) {
		plane[0].divide(x, y, val0);
		plane[1].divide(x, y, val1);
		plane[2].divide(x, y, val2);
	} 


	/**
	 * Finds the minimum value of all the planes of this image
	 * @param none
	 * @return an float containing the minimum value
	 */
	public final float min() {
		float m1, m2, m3;
		float min = Float.MAX_VALUE;

		m1 = plane[0].min();
		m2 = plane[1].min();
		m3 = plane[2].min();
		if (m1 < min) {
			min = m1;
		} 
		if (m2 < min) {
			min = m2;
		} 
		if (m3 < min) {
			min = m3;
		} 
		return min;
	} 


	/**
	 * Finds the minimum value of a single plane of this image
	 * @param p the plane
	 * @return an float containing the minimum value
	 */
	public final float min(int p) {
		return plane[p].min();
	} 


	/**
	 * Finds the maximum value of all the planes of this image
	 * @param none
	 * @return an float containing the maximum value
	 */
	public final float max() {
		float m1, m2, m3;
		float max = Float.MIN_VALUE;

		m1 = plane[0].max();
		m2 = plane[1].max();
		m3 = plane[2].max();
		if (m1 > max) {
			max = m1;
		} 
		if (m2 > max) {
			max = m2;
		} 
		if (m3 > max) {
			max = m3;
		} 
		return max;
	} 


	/**
	 * Finds the maximum value of a single plane of this image
	 * @param p the plane
	 * @return an integer containing the maximum value
	 */
	public final float max(int p) {
		return plane[p].max();
	} 

	/*
	 * Image-wide scalar operations
	 */


	/**
	 * Adds all the values together
	 */
	public final float[] addSum() {
		float[] sum = new float[3];

		sum[0] = plane(0).addSum();
		sum[1] = plane(1).addSum();
		sum[2] = plane(2).addSum();
		return sum;
	} 


	/**
	 * Adds absolute value of all the values together
	 */
	public final float[] absSum() {
		float[] sum = new float[3];

		sum[0] = plane(0).absSum();
		sum[1] = plane(1).absSum();
		sum[2] = plane(2).absSum();
		return sum;
	} 


	/**
	 * Adds the square of all the values together
	 */
	public final double[] sqrSum() {
		double[]	sum = new double[3];

		sum[0] = plane(0).sqrSum();
		sum[1] = plane(1).sqrSum();
		sum[2] = plane(2).sqrSum();
		return sum;
	} 


	/**
	 * Adds triplet to all the pixels in this image
	 * @param val0 value to add to the pixel in the first plane
	 * @param val1 value to add to the pixel in the second plane
	 * @param val2 value to add to the pixel in the third plane
	 * @return this
	 */
	public final RealColorImage add(float val0, float val1, float val2) {
		plane[0] = plane[0].add(val0);
		plane[1] = plane[1].add(val1);
		plane[2] = plane[2].add(val2);
		return this;
	} 


	/**
	 * Adds triplet to all the pixels in this image
	 * @param v triplet to be added to the pixels
	 * @return this
	 */
	public final RealColorImage add(float[] v) {
		plane[0] = plane[0].add(v[0]);
		plane[1] = plane[1].add(v[1]);
		plane[2] = plane[2].add(v[2]);
		return this;
	} 


	/**
	 * Adds a value to all the pixels in a plane of this image
	 * @param p the plane
	 * @param v value to be added to the pixels
	 * @return this
	 */
	public final RealColorImage add(int p, float v) {
		plane[p] = plane[p].add(v);
		return this;
	} 


	/**
	 * Subtracts a triplet from all the pixels of this image
	 * @param val0 value to subtract from the pixel in the first plane
	 * @param val1 value to subtract from the pixel in the second plane
	 * @param val2 value to subtract from the pixel in the third plane
	 * @return this
	 */
	public final RealColorImage subtract(float val0, float val1, float val2) {
		plane[0] = plane[0].subtract(val0);
		plane[1] = plane[1].subtract(val1);
		plane[2] = plane[2].subtract(val2);
		return this;
	} 


	/**
	 * Subtracts a triplet from all the pixels of this image
	 * @param v triplet to be subtracted from the pixels
	 * @return this
	 */
	public final RealColorImage subtract(float[] v) {
		plane[0] = plane[0].subtract(v[0]);
		plane[1] = plane[1].subtract(v[1]);
		plane[2] = plane[2].subtract(v[2]);
		return this;
	} 


	/**
	 * subtracts the value from all pixels in plane p
	 */


	/**
	 * Subtracts a value from all the pixels in a plane of this image
	 * @param p the plane
	 * @param v value to be added to the pixels
	 * @return this
	 */
	public final RealColorImage subtract(int p, float v) {
		plane[p] = plane[p].subtract(v);
		return this;
	} 



	/**
	 * Multiplies a triplet by all the pixels of this image
	 * @param v triplet to be multiplied by
	 * @return this
	 */
	public final RealColorImage multiply(float[] v) {
		plane[0] = plane[0].multiply(v[0]);
		plane[1] = plane[1].multiply(v[1]);
		plane[2] = plane[2].multiply(v[2]);
		return this;
	} 


	/**
	 * Multiplies a triplet by all the pixels of this image
	 * @param val0 value to multiply the pixel by in the first plane
	 * @param val1 value to multiply the pixel by in the second plane
	 * @param val2 value to multiply the pixel by in the third plane
	 * @return this
	 */
	public final RealColorImage multiply(float val0, float val1, float val2) {
		plane[0] = plane[0].multiply(val0);
		plane[1] = plane[1].multiply(val1);
		plane[2] = plane[2].multiply(val2);
		return this;
	} 


	/**
	 * Multiplies all the pixels in a plane of this image by a value
	 * @param p the plane
	 * @param v value to be added to the pixels
	 * @return this
	 */
	public final RealColorImage multiply(int p, float v) {
		plane[p] = plane[p].multiply(v);
		return this;
	} 


	/**
	 * Divides all the pixels of this image by a triplet
	 * @param v triplet to be divided by
	 * @return this
	 */
	public final RealColorImage divide(float[] v) {
		plane[0] = plane[0].divide(v[0]);
		plane[1] = plane[1].divide(v[1]);
		plane[2] = plane[2].divide(v[2]);
		return this;
	} 


	/**
	 * Divides all the pixels of this image by a triplet
	 * @param val0 value to divide by the pixel in the first plane
	 * @param val1 value to divide by the pixel in the second plane
	 * @param val2 value to divide by the pixel in the third plane
	 * @return this
	 */
	public final RealColorImage divide(float val0, float val1, float val2) {
		plane[0] = plane[0].divide(val0);
		plane[1] = plane[1].divide(val1);
		plane[2] = plane[2].divide(val2);
		return this;
	} 


	/**
	 * Divides all the pixels in a plane of this image by a value
	 * @param p the plane
	 * @param v value to be added to the pixels
	 * @return this
	 */
	public final RealColorImage divide(int p, float v) {
		plane[p] = plane[p].divide(v);
		return this;
	} 

	/*
	 * Image-by-image arithmetic operations
	 */


	/**
	 * Adds another RealColorImage to this image
	 * @param im the RealColorImage to add
	 * @return this
	 */
	public final RealColorImage add(RealColorImage im) {
		plane[0] = plane[0].add(im.plane(0));
		plane[1] = plane[1].add(im.plane(1));
		plane[2] = plane[2].add(im.plane(2));
		return this;
	} 


	/**
	 * Subtracts a RealColorImage from this image
	 * @param im the RealColorImage to subtract
	 * @return this
	 */
	public final RealColorImage subtract(RealColorImage im) {
		plane[0] = plane[0].subtract(im.plane(0));
		plane[1] = plane[1].subtract(im.plane(1));
		plane[2] = plane[2].subtract(im.plane(2));
		return this;
	} 


	/**
	 * Subtracts a RealColorImage from this image and returns the absolute value
	 * @param im the RealColorImage to diff
	 * @return this
	 */
	public final RealColorImage diff(RealColorImage im) {
		plane[0] = plane[0].diff(im.plane(0));
		plane[1] = plane[1].diff(im.plane(1));
		plane[2] = plane[2].diff(im.plane(1));
		return this;
	} 


	/**
	 * Multiplies a RealColorImage by this image
	 * @param im the RealColorImage to multiply
	 * @return this
	 */
	public final RealColorImage multiply(RealColorImage im) {
		plane[0] = plane[0].multiply(im.plane(0));
		plane[1] = plane[1].multiply(im.plane(1));
		plane[2] = plane[2].multiply(im.plane(2));
		return this;
	} 


	/**
	 * Divides this image by a RealColorImage
	 * @param im the RealColorImage to divide
	 * @return this
	 */
	public final RealColorImage divide(RealColorImage im) {
		plane[0] = plane[0].divide(im.plane(0));
		plane[1] = plane[1].divide(im.plane(1));
		plane[2] = plane[2].divide(im.plane(2));
		return this;
	} 


	/**
	 * Prints the string in integer format.
	 * <DT><DL><DL>-Example of output on an image with width 100 and height 120:</DT>
	 * <DL>       <DT>100 : 120</DT>
	 * <DT>10 87 32 65 32 65 40 59 43 12 43 ...</DT>
	 * <DT>100 : 120</DT>
	 * <DT>10 20 32 12 32 56 40 45 42 39 43 ...</DT>
	 * <DT>100 : 120</DT>
	 * <DT>10 20 32 12 54 56 73 59 42 23 43 ...</DT></DL></DL></DL>
	 */
	public final String toString() {
		String	str = "";		// = ndims + " planes\n";

		str += plane[0].toString();
		str += plane[1].toString();
		str += plane[2].toString();
		return str;
	} 


	/**
	 * Convert this image to a Java Image.	The RealColorImage is assumed
	 * to be RGB. <P>
	 * @param none
	 * @see java.awt.ImageProducer
	 */
	public final ImageProducer getJavaImage() {

		// get range of this image
		double	min = (double) min();
		double	max = (double) max();

		// keep byte images in original range
		if (min >= 0 && max <= 255) {
			min = 0.0;
			max = 255.0;
		} 
		double	range = max - min;

		// convert jigl image to java image
		int			pix[] = new int[X * Y];
		int			index = 0;
		int			red = 0;
		int			green = 0;
		int			blue = 0;
		float[] color = new float[3];

		for (int y = 0; y < Y; y++) {
			for (int x = 0; x < X; x++) {

				// map image values
				color = get(x, y);
				red = (int) ((255.0 / range) * ((double) color[0] - min));
				green = (int) ((255.0 / range) * ((double) color[1] - min));
				blue = (int) ((255.0 / range) * ((double) color[2] - min));

				// take lower 8 bits
				red = 0x00FF & red;
				green = 0x00FF & green;
				blue = 0x00FF & blue;

				// put this pixel in the java image
				pix[index] = (0xFF << 24) | (red << 16) | (green << 8) | blue;
				index++;
			} 
		} 

		// return java image
		return new MemoryImageSource(X, Y, pix, 0, X);
	} 


	/**
	 * Scales the range of this image to byte (0..255)
	 * @param none
	 */
	public void byteSize() {

		// get range of this image
		double	min = (double) min();
		double	max = (double) max();

		// keep byte images in original range

		double	range = max - min;

		// convert to byte depth
		int			red = 0;
		int			green = 0;
		int			blue = 0;
		float		color[] = new float[3];

		for (int y = 0; y < Y; y++) {
			for (int x = 0; x < X; x++) {
				color = get(x, y);
				red = (int) ((255.0 / range) * ((double) color[0] - min));
				green = (int) ((255.0 / range) * ((double) color[1] - min));
				blue = (int) ((255.0 / range) * ((double) color[2] - min));

				// take lower 8 bits
				red = 0x00FF & red;
				green = 0x00FF & green;
				blue = 0x00FF & blue;
				color[0] = red;
				color[1] = green;
				color[2] = blue;
				set(x, y, color);
			} 
		} 

	} 


	/**
	 * Clips the range of this image to an arbitrary min/max
	 * @param min minimum value
	 * @param max maximum value
	 */
	public final void clip(int min, int max) {

		// clip each plane
		plane[0].clip(min, max);
		plane[1].clip(min, max);
		plane[2].clip(min, max);

	} 


	// *****************************************************************************************************
	// **********************************  ROI *************************************************************
	// *****************************************************************************************************


	/**
	 * Makes a deep copy of a Region of Interest
	 * @param r Region of Interest
	 * @return a deep copy of RealColorImage
	 */
	public Image copy(ROI r) {
		RealColorImage	c = new RealColorImage(X, Y);

		c.setColorModel(colorModel);
		for (int y = r.uy(); y < r.ly(); y++) {
			for (int x = r.ux(); x < r.lx(); x++) {
				c.plane[0].set(x, y, plane[0].get(x, y));
				c.plane[1].set(x, y, plane[1].get(x, y));
				c.plane[2].set(x, y, plane[2].get(x, y));
			} 
		} 
		return c;
	} 


	/**
	 * Returns the pixel value at the given x, y value as a triplet in a Region of Interest
	 * @param x  the X coordinant
	 * @param y  the Y coordinant
	 * @param r Region of Interest
	 * @return three element array of integers
	 */
	public final float[] get(int x, int y, ROI r) {
		float[] color = new float[3];

		color[0] = plane[0].get(x + r.ux(), y + r.uy());
		color[1] = plane[1].get(x + r.ux(), y + r.uy());
		color[2] = plane[2].get(x + r.ux(), y + r.uy());
		return color;
	} 


	/**
	 * Sets the pixel value at the given x, y value as a triplet in a Region of Interest
	 * @param x  the X coordinant
	 * @param y  the Y coordinant
	 * @param v  array of three integers holding the values for the set
	 * @param r Region of Interest
	 */
	public final void set(int x, int y, int[] v, ROI r) {
		plane[0].set(x, y, v[0], r);
		plane[1].set(x, y, v[1], r);
		plane[2].set(x, y, v[2], r);
	} 


	/**
	 * Adds a triplet to a single pixel in a Region of Interest
	 * @param x X-coordinant
	 * @param y Y-coordinant
	 * @param val0 value to add to the pixel in the first plane
	 * @param val1 value to add to the pixel in the second plane
	 * @param val2 value to add to the pixel in the third plane
	 * @param r Region of Interest
	 */
	public final void add(int x, int y, float val0, float val1, float val2, ROI r) {
		plane[0].set(x, y, val0, r);
		plane[1].set(x, y, val1, r);
		plane[2].set(x, y, val2, r);
	} 


	/**
	 * Adds a triplet to a single pixel in a Region of Interest
	 * @param x X-coordinant
	 * @param y Y-coordinant
	 * @param value triplet to subtract from the pixel
	 * @param r Region of Interest
	 */
	public final void add(int x, int y, float[] value, ROI r) {
		plane[0].add(x, y, value[0], r);
		plane[1].add(x, y, value[1], r);
		plane[2].add(x, y, value[2], r);
	} 


	/**
	 * Subtracts a triplet from a single pixel in a Region of Interest
	 * @param x X-coordinant
	 * @param y Y-coordinant
	 * @param val0 value to subtract from the pixel in the first plane
	 * @param val1 value to subtract from the pixel in the second plane
	 * @param val2 value to subtract from the pixel in the third plane
	 * @param r Region of Interest
	 */
	public final void subtract(int x, int y, float val0, float val1, float val2, ROI r) {
		plane[0].subtract(x, y, val0, r);
		plane[1].subtract(x, y, val1, r);
		plane[2].subtract(x, y, val2, r);

	} 


	/**
	 * Subtracts a triplet from a single pixel in a Region of Interest
	 * @param x X-coordinant
	 * @param y Y-coordinant
	 * @param value triplet to subtract from to the pixel
	 * @param r Region of Interest
	 */
	public final void subtract(int x, int y, float[] value, ROI r) {
		plane[0].subtract(x, y, value[0], r);
		plane[1].subtract(x, y, value[1], r);
		plane[2].subtract(x, y, value[2], r);
	} 


	/**
	 * Multiplies a triplet with a single pixel in a Region of Interest
	 * @param x X-coordinant
	 * @param y Y-coordinant
	 * @param val0 value to multiply the pixel by in the first plane
	 * @param val1 value to multiply the pixel by in the second plane
	 * @param val2 value to multiply the pixel by in the third plane
	 * @param r Region of Interest
	 */
	public final void multiply(int x, int y, float val0, float val1, float val2, ROI r) {
		plane[0].multiply(x, y, val0, r);
		plane[1].multiply(x, y, val1, r);
		plane[2].multiply(x, y, val2, r);
	} 


	/**
	 * Multiplies a triplet with a single pixel in a Region of Interest
	 * @param x X-coordinant
	 * @param y Y-coordinant
	 * @param value triplet to multiply by the pixel
	 * @param r Region of Interest
	 */
	public final void multiply(int x, int y, float[] value, ROI r) {
		plane[0].multiply(x, y, value[0], r);
		plane[1].multiply(x, y, value[1], r);
		plane[2].multiply(x, y, value[2], r);
	} 


	/**
	 * Divides a single pixel by a triplet in a Region of Interest
	 * @param x X-coordinant
	 * @param y Y-coordinant
	 * @param value triplet to add to the pixel
	 * @param r Region of Interest
	 */
	public final void divide(int x, int y, float[] value, ROI r) {
		plane[0].divide(x, y, value[0], r);
		plane[1].divide(x, y, value[1], r);
		plane[2].divide(x, y, value[2], r);
	} 


	/**
	 * Divides a single pixel by a triplet in a Region of Interest
	 * @param x X-coordinant
	 * @param val0 value to divide by the pixel in the first plane
	 * @param val1 value to divide by the pixel in the second plane
	 * @param val2 value to divide by the pixel in the third plane
	 * @param r Region of Interest
	 */
	public final void divide(int x, int y, float val0, float val1, float val2, ROI r) {
		plane[0].divide(x, y, val0, r);
		plane[1].divide(x, y, val1, r);
		plane[2].divide(x, y, val2, r);
	} 


	/**
	 * Finds the minimum value of all the planes in a Region of Interest
	 * @param r Region of Interest
	 * @return an integer containing the minimum value
	 */
	public final float min(ROI r) {
		float m1, m2, m3;
		float min = Float.MAX_VALUE;

		m1 = plane[0].min(r);
		m2 = plane[1].min(r);
		m3 = plane[2].min(r);
		if (m1 < min) {
			min = m1;
		} 
		if (m2 < min) {
			min = m2;
		} 
		if (m3 < min) {
			min = m3;
		} 
		return min;
	} 


	/**
	 * Finds the minimum value of a single plane in a Region of Interest
	 * @param p the plane
	 * @param r Region of Interest
	 * @return an integer containing the minimum value
	 */
	public final float min(int p, ROI r) {
		return plane[p].min(r);
	} 


	/**
	 * Finds the maximum value of all the planes in a Region of Interest
	 * @param r Region of Interest
	 * @return an integer containing the maximum value
	 */
	public final float max(ROI r) {
		float m1, m2, m3;
		float max = Float.MIN_VALUE;

		m1 = plane[0].max(r);
		m2 = plane[1].max(r);
		m3 = plane[2].max(r);
		if (m1 > max) {
			max = m1;
		} 
		if (m2 > max) {
			max = m2;
		} 
		if (m3 > max) {
			max = m3;
		} 
		return max;
	} 


	/**
	 * Finds the maximum value of a single plane in a Region of Interest
	 * @param p the plane
	 * @param r Region of Interest
	 * @return an float containing the maximum value
	 */
	public final float max(int p, ROI r) {
		return plane[p].max(r);
	} 

	/*
	 * Image-wide scalar operations
	 */


	/**
	 * Adds triplet to all the pixels in a Region of Interest
	 * @param v triplet to be added to the pixels
	 * @param r Region of Interest
	 * @return this
	 */
	public final RealColorImage add(float[] v, ROI r) {
		plane[0] = plane[0].add(v[0], r);
		plane[1] = plane[1].add(v[1], r);
		plane[2] = plane[2].add(v[2], r);
		return this;
	} 


	/**
	 * Adds triplet to all the pixels in this image in a Region of Interest
	 * @param val0 value to add to the pixel in the first plane
	 * @param val1 value to add to the pixel in the second plane
	 * @param val2 value to add to the pixel in the third plane
	 * @param r Region of Interest
	 * @return this
	 */
	public final RealColorImage add(float val0, float val1, float val2, ROI r) {
		plane[0] = plane[0].add(val0, r);
		plane[1] = plane[1].add(val1, r);
		plane[2] = plane[2].add(val2, r);
		return this;
	} 


	/**
	 * Adds a value to all the pixels in a plane in a Region of Interest
	 * @param p the plane
	 * @param v value to be added to the pixels
	 * @param r Region of Interest
	 * @return this
	 */
	public final RealColorImage add(int p, float v, ROI r) {
		plane[p] = plane[p].add(v, r);
		return this;
	} 


	/**
	 * Subtracts a triplet from all the pixels in a Region of Interest
	 * @param v triplet to be subtracted from the pixels
	 * @param r Region of Interest
	 * @return this
	 */
	public final RealColorImage subtract(float[] v, ROI r) {
		plane[0] = plane[0].subtract(v[0], r);
		plane[1] = plane[1].subtract(v[1], r);
		plane[2] = plane[2].subtract(v[2], r);
		return this;
	} 


	/**
	 * Subtracts a triplet from all the pixels in a Region of Interest
	 * @param val0 value to subtract from the pixel in the first plane
	 * @param val1 value to subtract from the pixel in the second plane
	 * @param val2 value to subtract from the pixel in the third plane
	 * @param r Region of Interest
	 * @return this
	 */
	public final RealColorImage subtract(float val0, float val1, float val2, ROI r) {
		plane[0] = plane[0].subtract(val0, r);
		plane[1] = plane[1].subtract(val1, r);
		plane[2] = plane[2].subtract(val2, r);
		return this;
	} 


	/**
	 * subtracts the value from all pixels in plane p
	 */


	/**
	 * Subtracts a value from all the pixels in a Region of Interest
	 * @param p the plane
	 * @param v value to be added to the pixels
	 * @param r Region of Interest
	 * @return this
	 */
	public final RealColorImage subtract(int p, float v, ROI r) {
		plane[p] = plane[p].subtract(v, r);
		return this;
	} 


	/**
	 * Multiplies a triplet by all the pixels in a Region of Interest
	 * @param v triplet to be multiplied by
	 * @param r Region of Interest
	 * @return this
	 */
	public final RealColorImage multiply(float[] v, ROI r) {
		plane[0] = plane[0].multiply(v[0], r);
		plane[1] = plane[1].multiply(v[1], r);
		plane[2] = plane[2].multiply(v[2], r);
		return this;
	} 


	/**
	 * Multiplies a triplet by all the pixels of this image in a Region of Interest
	 * @param val0 value to multiply the pixel by in the first plane
	 * @param val1 value to multiply the pixel by in the second plane
	 * @param val2 value to multiply the pixel by in the third plane
	 * @param r Region of Interest
	 * @return this
	 */
	public final RealColorImage multiply(float val0, float val1, float val2, ROI r) {
		plane[0] = plane[0].multiply(val0, r);
		plane[1] = plane[1].multiply(val1, r);
		plane[2] = plane[2].multiply(val2, r);
		return this;
	} 


	/**
	 * multiplies all pixels in plane p by the value
	 */


	/**
	 * Multiplies all the pixels in a plane in a Region of Interest by a value
	 * @param p the plane
	 * @param v value to be added to the pixels
	 * @param r Region of Interest
	 * @return this
	 */
	public final RealColorImage multiply(int p, float v, ROI r) {
		plane[p] = plane[p].multiply(v, r);
		return this;
	} 


	/**
	 * Divides all the pixels in a Region of Interest by a triplet
	 * @param v triplet to be divided by
	 * @param r Region of Interest
	 * @return this
	 */
	public final RealColorImage divide(float[] v, ROI r) {
		plane[0] = plane[0].divide(v[0], r);
		plane[1] = plane[1].divide(v[1], r);
		plane[2] = plane[2].divide(v[2], r);
		return this;
	} 


	/**
	 * Divides all the pixels in a Region of Interest by a triplet
	 * @param val0 value to divide by the pixel in the first plane
	 * @param val1 value to divide by the pixel in the second plane
	 * @param val2 value to divide by the pixel in the third plane
	 * @param r Region of Interest
	 * @return this
	 */
	public final RealColorImage divide(float val0, float val1, float val2, ROI r) {
		plane[0] = plane[0].divide(val0, r);
		plane[1] = plane[1].divide(val1, r);
		plane[2] = plane[2].divide(val2, r);
		return this;
	} 


	/**
	 * divides all pixels in plane p by the value
	 */


	/**
	 * Divides all the pixels in a plane in a Region of Interest by a value
	 * @param p the plane
	 * @param v value to be added to the pixels
	 * @param r Region of Interest
	 * @return this
	 */
	public final RealColorImage divide(int p, float v, ROI r) {
		plane[p] = plane[p].divide(v, r);
		return this;
	} 

	/*
	 * Image-by-image arithmetic operations
	 */


	/**
	 * Adds a Region of Interest from another RealColorImage to a Region of Interest from this image
	 * @param im the RealColorImage to add
	 * @param sourceImage Region of Interest for the Source Image
	 * @param destImage Region of Interest for the Destination Image
	 * @return this
	 */
	public final RealColorImage add(RealColorImage im, ROI sourceImage, ROI destImage) {
		plane[0] = plane[0].add(im.plane(0), sourceImage, destImage);
		plane[1] = plane[1].add(im.plane(1), sourceImage, destImage);
		plane[2] = plane[2].add(im.plane(2), sourceImage, destImage);
		return this;
	} 


	/**
	 * Subtracts a Region of Interest from another RealColorImage from a Region of Interest from this image
	 * @param im the RealColorImage to subtract
	 * @param sourceImage Region of Interest for the Source Image
	 * @param destImage Region of Interest for the Destination Image
	 * @return this
	 */
	public final RealColorImage subtract(RealColorImage im, ROI sourceImage, ROI destImage) {
		plane[0] = plane[0].subtract(im.plane(0), sourceImage, destImage);
		plane[1] = plane[1].subtract(im.plane(1), sourceImage, destImage);
		plane[2] = plane[2].subtract(im.plane(2), sourceImage, destImage);
		return this;
	} 


	/**
	 * Multiplies a Region of Interest from another RealColorImage by a Region of Interest from this image
	 * @param im the RealColorImage to multiply
	 * @param sourceImage Region of Interest for the Source Image
	 * @param destImage Region of Interest for the Destination Image
	 * @return this
	 */
	public final RealColorImage multiply(RealColorImage im, ROI sourceImage, ROI destImage) {
		plane[0] = plane[0].multiply(im.plane(0), sourceImage, destImage);
		plane[1] = plane[1].multiply(im.plane(1), sourceImage, destImage);
		plane[2] = plane[2].multiply(im.plane(2), sourceImage, destImage);
		return this;
	} 


	/**
	 * Divides a Region of Interest from this image by a Region of Interest from another RealColorImage
	 * @param im the RealColorImage to divide
	 * @param sourceImage Region of Interest for the Source Image
	 * @param destImage Region of Interest for the Destination Image
	 * @return this
	 */
	public final RealColorImage divide(RealColorImage im, ROI sourceImage, ROI destImage) {
		plane[0] = plane[0].divide(im.plane(0), sourceImage, destImage);
		plane[1] = plane[1].divide(im.plane(1), sourceImage, destImage);
		plane[2] = plane[2].divide(im.plane(2), sourceImage, destImage);
		return this;
	} 


	/**
	 * Prints a Region of Interest from this images in float format.
	 * <DT><DL><DL>-Example of output on an image with width 100 and height 120:</DT>
	 * <DL>       <DT>100 : 120</DT>
	 * <DT>10 87 32 65 32 65 40 59 43 12 43 ...</DT>
	 * <DT>100 : 120</DT>
	 * <DT>10 20 32 12 32 56 40 45 42 39 43 ...</DT>
	 * <DT>100 : 120</DT>
	 * <DT>10 20 32 12 54 56 73 59 42 23 43 ...</DT></DL></DL></DL>
	 * @param r Region of Interest
	 */
	public final String toString(ROI r) {
		String	str = "";		// = ndims + " planes\n";

		str += plane[0].toString(r);
		str += plane[1].toString(r);
		str += plane[2].toString(r);
		return str;
	} 


	/**
	 * Scales the range of this image to byte (0..255) in a Region of Interest
	 * @param r Region of Interest
	 */
	public void byteSize(ROI r) {

		// get range of this image
		double	min = (double) min();
		double	max = (double) max();


		double	range = max - min;

		// convert to byte depth
		int			red = 0;
		int			green = 0;
		int			blue = 0;
		float		color[] = new float[3];

		for (int y = r.uy(); y < r.ly(); y++) {
			for (int x = r.ux(); x < r.ly(); x++) {
				color = get(x, y);
				red = (int) ((255.0 / range) * ((double) color[0] - min));
				green = (int) ((255.0 / range) * ((double) color[1] - min));
				blue = (int) ((255.0 / range) * ((double) color[2] - min));

				// take lower 8 bits
				red = 0x00FF & red;
				green = 0x00FF & green;
				blue = 0x00FF & blue;
				color[0] = red;
				color[1] = green;
				color[2] = blue;
				set(x, y, color);
			} 
		} 

	} 


	/**
	 * Clears an image to a value
	 * @param v value to clear to
	 */
	public final void clear(float v) {
		plane[0].clear(v);
		plane[1].clear(v);
		plane[2].clear(v);

	} 


	/**
	 * Clips the range of a Region of Interest to an arbitrary min/max
	 * @param min minimum value
	 * @param max maximum value
	 * @param r Region of Interest+
	 */
	public final void clip(int min, int max, ROI r) {

		// clip each plane
		plane[0].clip(min, max, r);
		plane[1].clip(min, max, r);
		plane[2].clip(min, max, r);

	} 

}


/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

