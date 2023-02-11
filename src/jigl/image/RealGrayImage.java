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
 * RealGrayImage is a 2-d array of floats
 */

public class RealGrayImage implements Image {


	/**
	 * Two dimensional float array
	 */
	protected float[][] data;


	/**
	 * Cartesian width
	 */
	protected int				X;


	/**
	 * Cartesian height
	 */
	protected int				Y;


	/**
	 * Creates an empty two dimensional RealGrayImage with a height and width of zero
	 */
	public RealGrayImage() {
		X = 0;
		Y = 0;
		data = null;
	}


	/**
	 * addes 30.6.99 CS
	 */
	public float[][] getData() {
		return data;
	} 


	/**
	 * Creates a two dimensional RealGrayImage with a height and width of x and y repectively
	 */
	public RealGrayImage(int x, int y) {
		X = x;
		Y = y;
		data = new float[Y][X];
	}


	/**
	 * Creates a two dimensional GrayImage with a height and width of x and y repectively
	 * @param x width of image
	 * @param y height of image
	 * @param dat one dimensional array of float.  The array is length x*y.
	 */
	public RealGrayImage(int x, int y, float[] dat) {
		X = x;
		Y = y;
		data = new float[Y][X];
		for (int a = 0; a < y; a++) {
			for (int b = 0; b < x; b++) {
				data[a][b] = dat[a * Y + b];
			}
		}
	}


	/**
	 * Creates a two dimensional GrayImage from a two dimensional array
	 * @param dat two dimensional array of short
	 */

	public RealGrayImage(float[][] dat) {

		int max = 0;

		Y = dat.length;

		// for (int y=0; y<X; y++)
		// if (dat[y].length>max) max=dat[y].length;
		X = dat[0].length;

		System.out.println("Konstruktor RealGrayImage" + X + " " + Y);

		/*
		 * for (int a=0; a<Y; a++)
		 * for (int b=0; b<X; b++)
		 * if (b<data[a].length) data[a][b]=dat[a][b];
		 * else data[a][b]=0;
		 */
		data = dat;

	}


	/**
	 * Creates an two dimensional RealGrayImage (shallow copy) from RealGrayImage img
	 */
	public RealGrayImage(RealGrayImage img) {
		X = img.X();
		Y = img.Y();
		data = img.data;
	}


	/**
	 * Creates a two dimensional RealGrayImage from the standard java.awt.Image
	 */
	public RealGrayImage(java.awt.Image img) {
		int w = img.getWidth(DummyObserver.dummy);
		int h = img.getHeight(DummyObserver.dummy);

		X = w;
		Y = h;
		data = new float[Y][X];
		InitFromImage(img, 0, 0, w, h);
	}


	/**
	 * Makes a deep copy of this image
	 * @param none
	 * @return a deep copy of RealGrayImage
	 */
	public Image copy() {
		RealGrayImage g = new RealGrayImage(X, Y);

		for (int y = 0; y < Y; y++) {
			for (int x = 0; x < X; x++) {
				g.data[y][x] = data[y][x];
			} 
		} 
		return g;
	} 



	/**
	 * Method declaration
	 *
	 *
	 * @param kernel
	 *
	 * @see
	 */
	public void convolve(ImageKernel kernel) {

		int				NumX = kernel.X();
		int				NumY = kernel.Y();
		int				midX = NumX / 2;
		int				midY = NumY / 2;
		double		sum = 0;
		float[][] value = new float[Y][X];

		float[][] kern = ConvertImage.toRealGray(kernel).data;

		// for every pixel in the original image
		for (int y = 0; y < Y; y++) {
			for (int x = 0; x < X; x++) {

				// Convolve with the kernel
				sum = 0;
				for (int j = -midY; j <= midY; j++) {
					for (int i = -midX; i <= midX; i++) {
						if ((y + j >= 0) && (x + i >= 0) && (y + j < Y) && (x + i < X) && (midY + j >= 0) && (midX + i >= 0) && (midY + j < NumY) && (midX + i < NumX)) {

							// try{

							sum += data[y + j][x + i] * kern[midY + j][midX + i];

							// }
							// catch (Exception e){System.out.print("");}

						} 
					} 
				}
				value[y][x] = (float) sum;
			} 

		} 

		this.data = value;


	} 


	/**
	 * Method declaration
	 *
	 *
	 * @param img
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 *
	 * @see
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
		float red = 0;
		float green = 0;
		float blue = 0;
		int		index = 0;

		for (int iy = 0; iy < Y; iy++) {
			for (int ix = 0; ix < X; ix++) {
				red = 0x0FF & pixels[index] >> 16;
				green = 0x0FF & pixels[index] >> 8;
				blue = 0x0FF & pixels[index];
				data[iy][ix] = (float) ((double) red * 0.299 + (double) green * 0.587 + (double) blue * 0.114);
				index++;
			} 
		} 
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
	public final float get(int x, int y) {
		return data[y][x];
	} 


	/**
	 * Sets the pixel value at x, y to a given value
	 * @param x the X coordinant
	 * @param y the Y coordinant
	 * @param value the value to set the pixel to
	 */
	public final void set(int x, int y, float value) {
		data[y][x] = (float) value;
	} 




	/**
	 * Clears the image to zero
	 * @param none
	 */
	public final RealGrayImage clear() {
		clear(0);
		return this;
	} 


	/**
	 * Clears to constant value
	 * @param val the value to "clear" the image to
	 */
	public final RealGrayImage clear(float val) {
		int x, y;

		for (y = 0; y < Y; y++) {
			for (x = 0; x < X; x++) {
				data[y][x] = (float) val;
			} 
		} 
		return this;
	} 


	/**
	 * Adds a value to a single pixel
	 * @param x X-coordinant
	 * @param y Y-coordinant
	 * @param value the value to add to the pixel
	 */
	public final void add(int x, int y, float value) {
		data[y][x] += (float) value;
	} 


	/**
	 * Subtracts a value from a single pixel
	 * @param x X-coordinant
	 * @param y Y-coordinant
	 * @param value the value to subtract from the pixel
	 */
	public final void subtract(int x, int y, float value) {
		data[y][x] -= (float) value;
	} 


	/**
	 * Mutiplies a single pixel by a value
	 * @param x X-coordinant
	 * @param y Y-coordinant
	 * @param value - the value to mutiply to the pixel
	 */
	public final void multiply(int x, int y, float value) {
		data[y][x] *= (float) value;
	} 


	/**
	 * Divides a single pixel by a value
	 * @param x X-coordinant
	 * @param y Y-coordinant
	 * @param value - the value to mutiply to the pixel
	 */
	public final void divide(int x, int y, float value) {
		data[y][x] /= (float) value;
	} 


	/**
	 * Finds the minimum value of this image
	 * @param none
	 * @return an float containing the minimum value
	 */
	public final float min() {
		float p;
		float min = Short.MAX_VALUE;

		for (int y = 0; y < Y; y++) {
			for (int x = 0; x < X; x++) {
				p = data[y][x];
				if (p < min) {
					min = p;
				} 
			} 
		} 
		return (float) min;
	} 


	/**
	 * Finds the maximum value of this image
	 * @param none
	 * @return an integer containing the maximum value
	 */
	public final float max() {
		float p;
		float max = Short.MIN_VALUE;

		for (int y = 0; y < Y; y++) {
			for (int x = 0; x < X; x++) {
				p = data[y][x];
				if (p > max) {
					max = p;
				} 
			} 
		} 
		return (float) max;
	} 


	/**
	 * Adds all the values together
	 */
	public final float addSum() {
		float sum = 0;

		for (int y = 0; y < Y; y++) {
			for (int x = 0; x < X; x++) {
				sum += data[y][x];
			}
		}
		return sum;
	} 


	/**
	 * Adds absolute value of all the values together
	 */
	public final float absSum() {
		float sum = 0;

		for (int y = 0; y < Y; y++) {
			for (int x = 0; x < X; x++) {
				if (data[y][x] < 0) {
					sum += -(data[y][x]);
				} else {
					sum += data[y][x];
				}
			}
		}
		return sum;
	} 


	/**
	 * Adds the square of all the values together
	 */
	public final double sqrSum() {
		double	sum = 0;

		for (int y = 0; y < Y; y++) {
			for (int x = 0; x < X; x++) {
				sum += (data[y][x] * data[y][x]);
			}
		}
		return sum;
	} 


	/**
	 * Adds a value to all the pixels in this image
	 * @param v value to be added to the pixels
	 * @return this
	 */
	public final RealGrayImage add(float v) {
		float sv = (float) v;

		for (int y = 0; y < Y; y++) {
			for (int x = 0; x < X; x++) {
				data[y][x] += sv;
			} 
		} 
		return this;
	} 


	/**
	 * Subtracts a value from all the pixels in this image
	 * @param v value to be added to the pixels
	 * @return this
	 */
	public final RealGrayImage subtract(float v) {
		float sv = (float) v;

		for (int y = 0; y < Y; y++) {
			for (int x = 0; x < X; x++) {
				data[y][x] -= sv;
			} 
		} 
		return this;
	} 


	/**
	 * Multiplies all the pixels in this image by a value
	 * @param v value to be added to the pixels
	 * @return this
	 */
	public final RealGrayImage multiply(float v) {
		float sv = (float) v;

		for (int y = 0; y < Y; y++) {
			for (int x = 0; x < X; x++) {
				data[y][x] *= sv;
			} 
		} 
		return this;
	} 


	/**
	 * Divides all the pixels in this image by a value
	 * @param v value to be added to the pixels
	 * @return this
	 */
	public final RealGrayImage divide(float v) {
		float sv = (float) v;

		for (int y = 0; y < Y; y++) {
			for (int x = 0; x < X; x++) {
				data[y][x] /= sv;
			} 
		} 
		return this;
	} 


	/**
	 * Adds another RealGrayImage to this image
	 * @param im the RealGrayImage to add
	 * @return this
	 */
	public final RealGrayImage add(RealGrayImage im) {
		for (int y = 0; y < Y; y++) {
			for (int x = 0; x < X; x++) {
				if (y < im.Y() && x < im.X()) {
					data[y][x] += im.get(x, y);
				} 
			} 
		} 
		return this;
	} 


	/**
	 * Subtracts a RealGrayImage from this image
	 * @param im the RealGrayImage to subtract
	 * @return this
	 */
	public final RealGrayImage subtract(RealGrayImage im) {
		for (int y = 0; y < Y; y++) {
			for (int x = 0; x < X; x++) {
				if (y < im.Y() && x < im.X()) {
					data[y][x] -= im.get(x, y);
				} 
			} 
		} 
		return this;
	} 


	/**
	 * Subtracts the second image from the first and returns the absolute value
	 */
	public final RealGrayImage diff(RealGrayImage im) {
		for (int y = 0; y < Y; y++) {
			for (int x = 0; x < X; x++) {
				data[y][x] -= im.get(x, y);
				if (data[y][x] < 0) {
					data[y][x] = (short) -data[y][x];
				} 
			} 
		} 
		return this;
	} 


	/**
	 * Multiplies a RealGrayImage by this image
	 * @param im the RealGrayImage to multiply
	 * @return this
	 */
	public final RealGrayImage multiply(RealGrayImage im) {
		for (int y = 0; y < Y; y++) {
			for (int x = 0; x < X; x++) {
				if (y < im.Y() && x < im.X()) {
					data[y][x] *= im.get(x, y);
				} 
			} 
		} 
		return this;
	} 


	/**
	 * Divides this image by a RealGrayImage
	 * @param im the RealGrayImage to divide
	 * @return this
	 */
	public final RealGrayImage divide(RealGrayImage im) {
		for (int y = 0; y < Y; y++) {
			for (int x = 0; x < X; x++) {
				if (y < im.Y() && x < im.X()) {
					data[y][x] /= im.get(x, y);
				} 
			} 
		} 
		return this;
	} 


	/**
	 * Prints the string in integer format.
	 * <DT><DL><DL>-Example of output on an image with width 100 and height 120:</DT>
	 * <DL>       <DT>100 : 120</DT>
	 * <DT>10 20 32 12 32 56 40 59 42 39 43 ...</DT></DL></DL></DL>
	 */
	public String toString() {
		String	str = X + ":" + Y + "\n";

		for (int x = 0; x < X; x++) {
			for (int y = 0; y < Y; y++) {
				str += data[y][x] + " ";
			} 
			str += "\n";
		} 
		return str;
	} 


	/**
	 * Turns this image into a Java Image (java.awt.Image). Note: This method also scales the image
	 * so all the values are between 0 and 255.
	 * @param none
	 * @see java.awt.ImageProducer
	 */
	public ImageProducer getJavaImage() {

		// get range of this image
		float min = min();
		float max = max();

		// keep byte images in original range
		if (min >= 0 && max <= 255) {
			min = 0;
			max = 255;
		} 
		float range = max - min;

		// convert jigl image to java image
		int		pix[] = new int[X * Y];
		int		index = 0;
		int		value = 0;

		for (int y = 0; y < Y; y++) {
			for (int x = 0; x < X; x++) {

				// scale image values
				value = (int) ((255.0 / range) * ((int) data[y][x] - min));

				value = 0x00FF & value;		// take lower 8 bits

				// put this pixel in the java image
				pix[index] = (0xFF << 24) | (value << 16) | (value << 8) | value;
				index++;
			} 
		} 

		// return java image
		return new MemoryImageSource(X, Y, pix, 0, X);
	} 


	/*
	 * Level operations
	 */


	/**
	 * Scales the range of this image to byte (0..255)
	 * @param none
	 */
	public void byteSize() {

		// get range of this image
		float min = min();
		float max = max();


		float range = max - min;

		// convert to byte depth
		int		value = 0;

		for (int y = 0; y < Y; y++) {
			for (int x = 0; x < X; x++) {
				value = (int) ((255.0 / range) * ((float) data[y][x] - min));
				value = 0x00FF & value;
				data[y][x] = (float) value;
			} 
		} 

	} 


	/**
	 * Clips the range of this image to an arbitrary min/max
	 * @param min minimum value
	 * @param max maximum value
	 */
	public void clip(int min, int max) {
		float value = 0;

		for (int y = 0; y < Y; y++) {
			for (int x = 0; x < X; x++) {
				value = data[y][x];
				value = (value > max) ? max : value;
				value = (value < min) ? min : value;
				data[y][x] = (float) value;
			} 
		} 

	} 


	/**
	 * Method declaration
	 *
	 *
	 * @param vals
	 * @param size
	 *
	 * @return
	 *
	 * @see
	 */
	private float[] sort(float vals[], int size) {
		int		i, j;
		float temp;

		for (i = 0; i < size; i++) {
			for (j = 0; j < size - 1; j++) {
				try {
					if (vals[j] > vals[j + 1]) {
						temp = vals[j];
						vals[j] = vals[j + 1];
						vals[j + 1] = temp;
					} 
				} catch (Exception e) {}
			} 
		}

		return vals;
	} 


	/**
	 * Performs median filter on this image
	 * @param size the size of the median filter
	 */
	public void median(int size) {

		int		NumX = (size > X) ? X : size;
		int		NumY = (size > Y) ? Y : size;
		int		midX = NumX / 2;
		int		midY = NumY / 2;
		float value[] = new float[NumX * NumY];
		int		count;

		// for every pixel in the original image
		for (int y = 0; y < Y; y++) {
			for (int x = 0; x < X; x++) {

				// find median value
				count = 0;
				for (int j = -midY; j <= midY; j++) {
					for (int i = -midX; i <= midX; i++) {
						try {
							value[count++] = data[y + j][x + i];
						} catch (Exception e) {

							// ignore out of bounds pixels
						} 
					} 
				} 
				value = sort(value, count);
				data[y][x] = (float) value[count / 2];
			} 
		} 

	} 


	// **************************************************************************************
	// ****************************         ROI Stuff         *******************************
	// **************************************************************************************



	/**
	 * Makes a deep copy of a Region of Interest
	 * @param r Region of Interest
	 * @return a deep copy of RealGrayImage
	 */
	public Image copy(ROI r) {
		RealGrayImage g = new RealGrayImage((r.lx() - r.ux()), (r.ly() - r.uy()));

		for (int y = r.uy(); y < r.ly(); y++) {
			for (int x = r.ux(); x < r.lx(); x++) {
				g.data[y][x] = data[y][x];
			} 
		} 
		return g;
	} 


	/**
	 * Sets the pixel value at x, y to a given value in a Region of Interest
	 * @param x the X coordinant
	 * @param y the Y coordinant
	 * @param value the value to set the pixel to
	 * @param r Region of Interest
	 */
	public final void set(int x, int y, float value, ROI r) {
		data[(r.uy() + y)][(r.ux() + x)] = (float) value;
	} 


	/**
	 * Adds a value to a single pixel in a Region of Interest
	 * @param x X-coordinant
	 * @param y Y-coordinant
	 * @param value the value to add to the pixel
	 * @param r Region of Interest
	 */
	public final void add(int x, int y, float value, ROI r) {
		data[(r.uy() + y)][(r.ux() + x)] += (float) value;
	} 


	/**
	 * Subtracts a value from a single pixel in a Region of Interest
	 * @param x X-coordinant
	 * @param y Y-coordinant
	 * @param value the value to subtract from the pixel
	 * @param r Region of Interest
	 */

	public final void subtract(int x, int y, float value, ROI r) {
		data[(r.uy() + y)][(r.ux() + x)] -= (float) value;
	} 


	/**
	 * Mutiplies a single pixel by a value in a Region of Interest
	 * @param x X-coordinant
	 * @param y Y-coordinant
	 * @param value - the value to mutiply to the pixel
	 * @param r Region of Interest
	 */
	public final void multiply(int x, int y, float value, ROI r) {
		data[(r.uy() + y)][(r.ux() + x)] *= (float) value;
	} 


	/**
	 * Divides a single pixel by a value in a Region of Interest
	 * @param x X-coordinant
	 * @param y Y-coordinant
	 * @param value - the value to mutiply to the pixel
	 * @param r Region of Interest
	 */
	public final void divide(int x, int y, float value, ROI r) {
		data[(r.uy() + y)][(r.ux() + x)] /= (float) value;
	} 


	/**
	 * Finds the minimum value in a Region of Interest
	 * @param r Region of Interest
	 * @return an integer containing the minimum value
	 * 
	 */
	public final float min(ROI r) {
		float p;
		float min = Short.MAX_VALUE;

		for (int y = r.uy(); y < r.ly(); y++) {
			for (int x = r.ux(); x < r.lx(); x++) {
				p = data[y][x];
				if (p < min) {
					min = p;
				} 
			} 
		} 
		return (float) min;
	} 


	/**
	 * Finds the maximum value in a Region of Interest
	 * @param r Region of Interest
	 * @return an integer containing the maximum value
	 */
	public final float max(ROI r) {
		float p;
		float max = Short.MIN_VALUE;

		for (int y = r.uy(); y < r.ly(); y++) {
			for (int x = r.ux(); x < r.lx(); x++) {
				p = data[y][x];
				if (p > max) {
					max = p;
				} 
			} 
		} 
		return (float) max;
	} 


	/**
	 * Adds a value to all the pixels in a Region of Interest
	 * @param v value to be added to the pixels
	 * @param r Region of Interest
	 * @return this
	 */
	public final RealGrayImage add(float v, ROI r) {
		float sv = (float) v;

		for (int y = r.uy(); y < r.ly(); y++) {
			for (int x = r.ux(); x < r.lx(); x++) {
				data[y][x] += sv;
			} 
		} 
		return this;
	} 


	/**
	 * Subtracts a value from all the pixels in a Region of Interest
	 * @param v value to be added to the pixels
	 * @param r Region of Interest
	 * @return this
	 */
	public final RealGrayImage subtract(float v, ROI r) {
		float sv = (float) v;

		for (int y = r.uy(); y < r.ly(); y++) {
			for (int x = r.ux(); x < r.lx(); x++) {
				data[y][x] -= sv;
			} 
		} 
		return this;
	} 


	/**
	 * Multiplies all the pixels in a Region of Interest by a value
	 * @param v value to be added to the pixels
	 * @param r Region of Interest
	 * @return this
	 */
	public final RealGrayImage multiply(float v, ROI r) {
		float sv = (float) v;

		for (int y = r.uy(); y < r.ly(); y++) {
			for (int x = r.ux(); x < r.lx(); x++) {
				data[y][x] *= sv;
			} 
		} 
		return this;
	} 


	/**
	 * Divides all the pixels by a value in a Region of Interest
	 * @param v value to be added to the pixels
	 * @param r Region of Interest
	 * @return this
	 */
	public final RealGrayImage divide(float v, ROI r) {
		float sv = (float) v;

		for (int y = r.uy(); y < r.ly(); y++) {
			for (int x = r.ux(); x < r.lx(); x++) {
				data[y][x] /= sv;
			} 
		} 
		return this;
	} 


	/**
	 * Adds a Region of Interest in another RealGrayImage to a Region of Interest of this image
	 * @param im the RealGrayImage to add
	 * @param sourceImage Region of Interest for the Source Image
	 * @param destImage Region of Interest for the Destination Image
	 * @return this
	 */
	public final RealGrayImage add(RealGrayImage im, ROI sourceImage, ROI destImage) {
		for (int y = sourceImage.uy(); y < sourceImage.ly(); y++) {
			for (int x = sourceImage.ux(); x < sourceImage.lx(); x++) {
				data[x][y] += im.get((x - sourceImage.ux() + destImage.ux()), (y - sourceImage.uy() + destImage.uy()));
			} 
		} 
		return this;
	} 


	/**
	 * Subtracts a Region of Interest in another RealGrayImage from a Region of Interest of this image
	 * @param im the RealGrayImage to subtract
	 * @param sourceImage Region of Interest for the Source Image
	 * @param destImage Region of Interest for the Destination Image
	 * @return this
	 */
	public final RealGrayImage subtract(RealGrayImage im, ROI sourceImage, ROI destImage) {
		for (int y = sourceImage.uy(); y < sourceImage.ly(); y++) {
			for (int x = sourceImage.ux(); x < sourceImage.lx(); x++) {
				data[x][y] -= im.get((x - sourceImage.ux() + destImage.ux()), (y - sourceImage.uy() + destImage.uy()));
			} 
		} 
		return this;
	} 


	/**
	 * Multiplies a Region of Interest of another RealGrayImage by a Region of Interest of this image
	 * @param im the RealGrayImage to multiply
	 * @param sourceImage Region of Interest for the Source Image
	 * @param destImage Region of Interest for the Destination Image
	 * @return this
	 */
	public final RealGrayImage multiply(RealGrayImage im, ROI sourceImage, ROI destImage) {
		for (int y = sourceImage.uy(); y < sourceImage.ly(); y++) {
			for (int x = sourceImage.ux(); x < sourceImage.lx(); x++) {
				data[x][y] *= im.get((x - sourceImage.ux() + destImage.ux()), (y - sourceImage.uy() + destImage.uy()));
			} 
		} 
		return this;
	} 


	/**
	 * Divides by a Region of Interest in this image by a Region of Interest of another RealGrayImage
	 * @param im the RealGrayImage to divide
	 * @param sourceImage Region of Interest for the Source Image
	 * @param destImage Region of Interest for the Destination Image
	 * @return this
	 */
	public final RealGrayImage divide(RealGrayImage im, ROI sourceImage, ROI destImage) {
		for (int y = sourceImage.uy(); y < sourceImage.ly(); y++) {
			for (int x = sourceImage.ux(); x < sourceImage.lx(); x++) {
				data[x][y] /= im.get((x - sourceImage.ux() + destImage.ux()), (y - sourceImage.uy() + destImage.uy()));
			} 
		} 
		return this;
	} 


	/**
	 * Prints the string in integer format in a Region of Interest.
	 * @param r Region of Interest
	 * <DT><DL><DL>-Example of output on an image with width 100 and height 120:</DT>
	 * <DL>       <DT>100 : 120</DT>
	 * <DT>10 20 32 12 32 56 40 59 42 39 43 ...</DT></DL></DL></DL>
	 */
	public String toString(ROI r) {
		String	str = X + ":" + Y + "\n";

		for (int x = r.ux(); x < r.lx(); x++) {
			for (int y = r.uy(); y < r.ly(); y++) {
				str += data[y][x] + " ";
			} 
			str += "\n";
		} 
		return str;
	} 


	/**
	 * Clips the range of this image to an arbitrary min/max in a Region of Interest
	 * @param min minimum value
	 * @param max maximum value
	 * @param r Region of Interest
	 */
	public void clip(int min, int max, ROI r) {
		float value = 0;

		for (int y = r.uy(); y < r.ly(); y++) {
			for (int x = r.ux(); x < r.lx(); x++) {
				value = data[y][x];
				value = (value > max) ? max : value;
				value = (value < min) ? min : value;
				data[y][x] = (float) value;
			} 
		} 

	} 


	/**
	 * Performs median filter in a Region of Interest
	 * @param size the size of the median filter
	 * @param r Region of Interest
	 */
	public void median(int size, ROI r) {

		int		NumX = (size > X) ? X : size;
		int		NumY = (size > Y) ? Y : size;
		int		midX = NumX / 2;
		int		midY = NumY / 2;
		float value[] = new float[NumX * NumY];
		int		count;

		// for every pixel in the original image
		for (int y = r.uy(); y < r.ly(); y++) {
			for (int x = r.ux(); x < r.lx(); x++) {

				// find median value
				count = 0;
				for (int j = -midY; j <= midY; j++) {
					for (int i = -midX; i <= midX; i++) {
						try {
							value[count++] = data[y + j][x + i];
						} catch (Exception e) {

							// ignore out of bounds pixels
						} 
					} 
				} 
				value = sort(value, count);
				data[y][x] = (float) value[count / 2];
			} 
		} 

	} 


}


/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

