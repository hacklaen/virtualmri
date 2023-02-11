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
import jigl.math.*;

import java.awt.image.*;


/**
 * A complex image is a set of two real gray image planes.<P>
 * ComplexImage implements Image
 */

public class ComplexImage implements Image {


	/**
	 * The Real plane of the image
	 */
	protected RealGrayImage real;


	/**
	 * The Imaginary part of the image
	 */
	protected RealGrayImage imag;


	/**
	 * Cartesian width of the image
	 */
	protected int						X;


	/**
	 * Cartesian height of the image
	 */
	protected int						Y;

	/*
	 * constructors
	 */


	/**
	 * Creates a ComplexImage with height and width of zero and the real and imaginary
	 * planes set to null
	 */
	public ComplexImage() {
		X = 0;
		Y = 0;
		real = null;
		imag = null;
	}


	/**
	 * Creates a ComplexImage with height and width of x and y repectively
	 */
	public ComplexImage(int x, int y) {
		X = x;
		Y = y;
		real = new RealGrayImage(X, Y);
		imag = new RealGrayImage(X, Y);
	}


	/**
	 * Creates a ComplexImage as a shallow copy of a complex image
	 */
	public ComplexImage(ComplexImage img) {
		X = img.X();
		Y = img.Y();
		real = img.real();
		imag = img.imag();
	}


	/*
	 * Java image conversion
	 */


	/**
	 * Creates a ComplexImage from the standard java.awt.Image
	 */
	public ComplexImage(java.awt.Image img) {
		real = new RealGrayImage(img);
		X = real.X();
		Y = imag.Y();
		imag = new RealGrayImage(X, Y);
	}


	/**
	 * Returns the Java Image from this image
	 * @see ImageProducer
	 */
	public ImageProducer getJavaImage() {
		return real.getJavaImage();
	} 


	/*
	 * image shape parameters
	 */


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


	/*
	 * copiers
	 */


	/**
	 * Makes a deep copy of this image
	 * @param none
	 * @return a deep copy of ComplexImage
	 */
	public Image copy() {
		ComplexImage	c = new ComplexImage(X, Y);

		for (int y = 0; y < Y; y++) {
			for (int x = 0; x < X; x++) {
				c.real.set(x, y, real.get(x, y));
				c.imag.set(x, y, imag.get(x, y));
			} 
		} 
		return c;
	} 


	/**
	 * Returns the real plane of this image
	 */
	public final RealGrayImage real() {
		return real;
	} 


	/**
	 * Set the real plane of this image
	 * @param pl the RealGrayImage to set the real plane to
	 */
	public final void setReal(RealGrayImage pl) {
		real = pl;
	} 


	/**
	 * Returns the imaginary plane of this image
	 */
	public final RealGrayImage imag() {
		return imag;
	} 


	/**
	 * Set the real plane of this image
	 * @param pl the RealGrayImage to set the real plane to
	 */
	public final void setImag(RealGrayImage pl) {
		imag = pl;
	} 


	/*
	 * buffer access
	 */


	/**
	 * Returns the pixel value at the given x, y value of the real plane
	 * @param x  the X coordinant
	 * @param y  the Y coordinant
	 */
	public final float getReal(int x, int y) {
		return real.get(x, y);
	} 


	/**
	 * Returns the pixel value at the given x, y value of the imaginary plane
	 * @param x  the X coordinant
	 * @param y  the Y coordinant
	 */
	public final float getImag(int x, int y) {
		return imag.get(x, y);
	} 


	/**
	 * Sets the pixel value at x, y to a given value of the real plane
	 * @param x the X coordinant
	 * @param y the Y coordinant
	 * @param v the value to set the pixel to
	 */
	public final void setReal(int x, int y, float v) {
		real.set(x, y, v);
	} 


	/**
	 * Sets the pixel value at x, y to a given value of the imaginary
	 * @param x the X coordinant
	 * @param y the Y coordinant
	 * @param v the value to set the pixel to
	 */
	public final void setImag(int x, int y, float v) {
		imag.set(x, y, v);
	} 


	/**
	 * Sets the pixel value at x, y to a given value of this image
	 * @param x the X coordinant
	 * @param y the Y coordinant
	 * @param r the value to set the pixel to in the real plane
	 * @param i the value to set the pixel to in the imaginary plane
	 */
	public final void set(int x, int y, float r, float i) {
		real.set(x, y, r);
		imag.set(x, y, i);
	} 

	/*
	 * range functions
	 */


	/**
	 * Returns the minimum magnitude of the complex number in this image
	 * @param none
	 */
	public final Complex min() {
		Complex p = new Complex();
		Complex min = new Complex(Double.MAX_VALUE, Double.MAX_VALUE);

		for (int y = 0; y < Y; y++) {
			for (int x = 0; x < X; x++) {
				p.real(getReal(x, y));
				p.imag(getImag(x, y));
				if (p.mod() < min.mod()) {
					min = p;
				} 
			} 
		} 
		return min;
	} 


	/**
	 * Returns the maximum magnitude of the complex number in this image
	 * @param none
	 */
	public final Complex max() {
		Complex p = new Complex();
		Complex max = new Complex(Double.MIN_VALUE, Double.MIN_VALUE);

		for (int y = 0; y < Y; y++) {
			for (int x = 0; x < X; x++) {
				p.real(getReal(x, y));
				p.imag(getImag(x, y));
				if (p.mod() > max.mod()) {
					;
				} 
				max = p;
			} 
		} 
		return max;
	} 




	/*
	 * single-pixel arithmetic operations
	 */


	/**
	 * Adds a value to a single pixel
	 * @param x X-coordinant
	 * @param y Y-coordinant
	 * @param r the value to add to the pixel in the real plane
	 * @param i the value to add to the pixel in the imaginary plane
	 */
	public final void add(int x, int y, float r, float i) {
		real.add(x, y, r);
		imag.add(x, y, i);
	} 


	/**
	 * Subtracts a value from a single pixel
	 * @param x X-coordinant
	 * @param y Y-coordinant
	 * @param r the value to add to the pixel in the real plane
	 * @param i the value to add to the pixel in the imaginary plane
	 */
	public final void subtract(int x, int y, float r, float i) {
		real.subtract(x, y, r);
		imag.subtract(x, y, i);
	} 


	/**
	 * Multiply a single pixel by a value
	 * @param x X-coordinant
	 * @param y Y-coordinant
	 * @param r the value to add to the pixel in the real plane
	 * @param i the value to add to the pixel in the imaginary plane
	 */
	public final void multiply(int x, int y, float r, float i) {
		float a = real.get(x, y) * r - imag.get(x, y) * i;
		float b = real.get(x, y) * i + imag.get(x, y) * r;

		real.set(x, y, a);
		imag.set(x, y, b);
	} 


	/**
	 * Divide a single pixel by a value
	 * @param x X-coordinant
	 * @param y Y-coordinant
	 * @param r the value to add to the pixel in the real plane
	 * @param i the value to add to the pixel in the imaginary plane
	 */
	public final void divide(int x, int y, float r, float i) {
		float a;
		float b;
		float mag2;

		mag2 = r * r + i * i;
		a = (real.get(x, y) * r + imag.get(x, y) * i) / mag2;
		b = (imag.get(x, y) * r - real.get(x, y) * i) / mag2;
		real.set(x, y, a);
		imag.set(x, y, b);
	} 


	/**
	 * Adds another ComplexImage to this image
	 * @param im the ComplexImage to add
	 * @return this
	 */
	public final void add(ComplexImage im) {
		for (int y = 0; y < Y; y++) {
			for (int x = 0; x < X; x++) {
				add(x, y, im.getReal(x, y), im.getImag(x, y));
			} 
		} 
	} 


	/**
	 * Subtracts another ComplexImage from this image
	 * @param im the ComplexImage to subtract
	 * @return this
	 */
	public final void subtract(ComplexImage im) {
		for (int y = 0; y < Y; y++) {
			for (int x = 0; x < X; x++) {
				subtract(x, y, im.getReal(x, y), im.getImag(x, y));
			} 
		} 
	} 


	/**
	 * Subtracts a RealColorImage from this image and returns the absolute value
	 * @param im the RealColorImage to diff
	 * @return this
	 */
	public final ComplexImage diff(ComplexImage im) {

		real = real.diff(im.real());
		imag = imag.diff(im.imag());
		return this;
	} 


	/**
	 * Multiplies this image by another ComplexImage
	 * @param im the ComplexImage to multiply
	 * @return this
	 */
	public final void multiply(ComplexImage im) {
		for (int y = 0; y < Y; y++) {
			for (int x = 0; x < X; x++) {
				multiply(x, y, im.getReal(x, y), im.getImag(x, y));
			} 
		} 
	} 


	/**
	 * Divides this image by another ComplexImage
	 * @param im the ComplexImage to divide
	 * @return this
	 */
	public final void divide(ComplexImage im) {
		for (int y = 0; y < Y; y++) {
			for (int x = 0; x < X; x++) {
				divide(x, y, im.getReal(x, y), im.getImag(x, y));
			} 
		} 
	} 


	/*
	 * image-wide arithmetic operations
	 */


	/**
	 * Adds a value to all the pixels in this image
	 * @param r value to be added to the pixels in the real plane
	 * @param i value to be added to the pixels in the imaginary plane
	 * @return this
	 */
	public final ComplexImage add(float r, float i) {
		for (int y = 0; y < Y; y++) {
			for (int x = 0; x < X; x++) {
				add(x, y, r, i);
			} 
		} 
		return this;
	} 


	/**
	 * Subtracts a value from all the pixels in this image
	 * @param r value to be subtract from the pixels in the real plane
	 * @param i value to be subtracted from  pixels in the imaginary plane
	 * @return this
	 */
	public final ComplexImage subtract(float r, float i) {
		for (int y = 0; y < Y; y++) {
			for (int x = 0; x < X; x++) {
				subtract(x, y, r, i);
			} 
		} 
		return this;
	} 


	/**
	 * Multiplies all the pixels in this image by a value
	 * @param r value to be multiplied by the pixels in the real plane
	 * @param i value to be multiplied by the pixels in the imaginary plane
	 * @return this
	 */
	public final ComplexImage multiply(float r, float i) {
		for (int y = 0; y < Y; y++) {
			for (int x = 0; x < X; x++) {
				multiply(x, y, r, i);
			} 
		} 
		return this;
	} 


	/**
	 * Divides all the pixels by a value in this image
	 * @param r value to be divided into the pixels in the real plane
	 * @param i value to be divided into the pixels in the imaginary plane
	 * @return this
	 */
	public final ComplexImage divide(float r, float i) {
		for (int y = 0; y < Y; y++) {
			for (int x = 0; x < X; x++) {
				divide(x, y, r, i);
			} 
		} 
		return this;
	} 


	/**
	 * Prints the image in integer format.
	 * <DT><DL><DL>-Example of output on an image with width 100 and height 120:</DT>
	 * <DL>       <DT>100 : 120</DT>
	 * <DT>5 23 54 7 3 23 46 253 23 53 65 34 ...</DT></DL>
	 * <DL>       <DT>100 : 120</DT>
	 * <DT>10 20 32 12 32 56 40 59 42 39 43 ...</DT></DL></DL></DL>
	 */
	public String toString() {
		String	str;

		str = real.toString();
		str += imag.toString();
		return str;
	} 


	/**
	 * ****************************************************************************************************
	 * *******************************ROI STUFF************************************************************
	 * **************************************************************************************************
	 */


	/**
	 * Makes a deep copy of a Region of Interest
	 * @param r Region of Interest
	 * @return a deep copy of ComplexImage
	 */
	public Image copy(ROI r) {
		ComplexImage	c = new ComplexImage(r.X(), r.Y());

		for (int y = r.uy(); y < r.ly(); y++) {
			for (int x = r.ux(); x < r.uy(); x++) {
				c.real.set(x, y, real.get(x, y));
				c.imag.set(x, y, imag.get(x, y));
			} 
		} 
		return c;
	} 


	/**
	 * Returns the pixel value at the given x, y value of a Region of Interest in the real plane
	 * @param x  the X coordinant
	 * @param y  the Y coordinant
	 * @param r   Region of Interest
	 */
	public final float getReal(int x, int y, ROI r) {
		return real.get(x + r.ux(), y + r.uy());
	} 


	/**
	 * Returns the pixel value at the given x, y value of a Region of Interest in the imaginary plane
	 * @param x  the X coordinant
	 * @param y  the Y coordinant
	 * @param r   Region of Interest
	 */
	public final float getImag(int x, int y, ROI r) {
		return imag.get(x + r.ux(), y + r.uy());
	} 


	/**
	 * Sets tthe pixel value at the given x, y value of a Region of Interest in the real plane
	 * @param x  the X coordinant
	 * @param y  the Y coordinant
	 * @param r   Region of Interest
	 */
	public final void setReal(int x, int y, float v, ROI r) {
		real.set(x + r.ux(), y + r.uy(), v);
	} 


	/**
	 * Sets the pixel value at the given x, y value of a Region of Interest in the imaginary plane
	 * @param x  the X coordinant
	 * @param y  the Y coordinant
	 * @param r   Region of Interest
	 */
	public final void setImag(int x, int y, float v, ROI r) {
		imag.set(x + r.ux(), y + r.uy(), v);
	} 


	/**
	 * Sets the pixel value at x, y to a given value in a Region of Interest
	 * @param x the X coordinant
	 * @param y the Y coordinant
	 * @param r the value to set the pixel to in the real plane
	 * @param i the value to set the pixel to in the imaginary plane
	 * @param r2 Region of Interest
	 */
	public final void set(int x, int y, float r, float i, ROI r2) {
		real.set(x, y, r, r2);
		imag.set(x, y, i, r2);
	} 

	/*
	 * range functions
	 */


	/**
	 * Returns the minimum magnitude of a Region of Interest
	 * @param r Region of Interest
	 */
	public final Complex min(ROI r) {
		Complex p = new Complex();
		Complex min = new Complex(Double.MAX_VALUE, Double.MAX_VALUE);

		for (int y = r.uy(); y < r.ly(); y++) {
			for (int x = r.ux(); x < r.ux(); x++) {
				p.real(getReal(x, y));
				p.imag(getImag(x, y));
				if (p.mod() < min.mod()) {
					min = p;
				} 
			} 
		} 
		return min;
	} 


	/**
	 * Returns the maximum magnitude of a Region of Interest
	 * @param r Region of Interest
	 */
	public final Complex max(ROI r) {
		Complex p = new Complex();
		Complex max = new Complex(Double.MIN_VALUE, Double.MIN_VALUE);

		for (int y = r.uy(); y < r.ly(); y++) {
			for (int x = r.ux(); x < r.ux(); x++) {
				p.real(getReal(x, y));
				p.imag(getImag(x, y));
				if (p.mod() > max.mod()) {
					;
				} 
				max = p;
			} 
		} 
		return max;
	} 



	/*
	 * single-pixel arithmetic operations
	 */


	/**
	 * Adds a value to a single pixel in a Region of Interest
	 * @param x X-coordinant
	 * @param y Y-coordinant
	 * @param r the value to add to the pixel in the real plane
	 * @param i the value to add to the pixel in the imaginary plane
	 * @param r2 Region of Interest
	 */
	public final void add(int x, int y, float r, float i, ROI r2) {
		real.add(x, y, r, r2);
		imag.add(x, y, i, r2);
	} 


	/**
	 * Subtracts a value from a single pixel in a Region of Interest
	 * @param x X-coordinant
	 * @param y Y-coordinant
	 * @param r the value to add to the pixel in the real plane
	 * @param i the value to add to the pixel in the imaginary plane
	 * @param r2 Region of Interest
	 */
	public final void subtract(int x, int y, float r, float i, ROI r2) {
		real.subtract(x, y, r, r2);
		imag.subtract(x, y, i, r2);
	} 


	/**
	 * Multiply a single pixel by a value in a Region of Interest
	 * @param x X-coordinant
	 * @param y Y-coordinant
	 * @param r the value to add to the pixel in the real plane
	 * @param i the value to add to the pixel in the imaginary plane
	 * @param r2 Region of Interest
	 */
	public final void multiply(int x, int y, float r, float i, ROI r2) {
		float a = real.get(x, y) * r - imag.get(x, y) * i;
		float b = real.get(x, y) * i + imag.get(x, y) * r;

		real.set(x, y, a, r2);
		imag.set(x, y, b, r2);
	} 


	/**
	 * Divide a single pixel by a value in a Region of Interest
	 * @param x X-coordinant
	 * @param y Y-coordinant
	 * @param r the value to add to the pixel in the real plane
	 * @param i the value to add to the pixel in the imaginary plane
	 * @param r2 Region of Interest
	 */
	public final void divide(int x, int y, float r, float i, ROI r2) {
		float a;
		float b;
		float mag2;

		mag2 = r * r + i * i;
		a = (real.get(x, y) * r + imag.get(x, y) * i) / mag2;
		b = (imag.get(x, y) * r - real.get(x, y) * i) / mag2;
		real.set(x, y, a, r2);
		imag.set(x, y, b, r2);
	} 



	/*
	 * image-wide arithmetic operations
	 */


	/**
	 * Adds a value to all the pixels in a Region of Interest
	 * @param r value to be added to the pixels in the real plane
	 * @param i value to be added to the pixels in the imaginary plane
	 * @param r2 Region of Interest
	 * @return this
	 */
	public final ComplexImage add(float r, float i, ROI r2) {
		for (int y = r2.uy(); y < r2.ly(); y++) {
			for (int x = r2.ux(); x < r2.lx(); x++) {
				add(x, y, r, i);
			} 
		} 
		return this;
	} 


	/**
	 * Subtracts a value from all the pixels in a Region of Interest
	 * @param r value to be subtract from the pixels in the real plane
	 * @param i value to be subtracted from  pixels in the imaginary plane
	 * @param r2 Region of Interest
	 * @return this
	 */
	public final ComplexImage subtract(float r, float i, ROI r2) {
		for (int y = r2.uy(); y < r2.ly(); y++) {
			for (int x = r2.ux(); x < r2.lx(); x++) {
				subtract(x, y, r, i);
			} 
		} 
		return this;
	} 


	/**
	 * Multiplies all the pixels by a value in a Region of Interest
	 * @param r value to be multiplied by the pixels in the real plane
	 * @param i value to be multiplied by the pixels in the imaginary plane
	 * @param r2 Region of Interest
	 * @return this
	 */
	public final ComplexImage multiply(float r, float i, ROI r2) {
		for (int y = r2.uy(); y < r2.ly(); y++) {
			for (int x = r2.ux(); x < r2.lx(); x++) {
				multiply(x, y, r, i);
			} 
		} 
		return this;
	} 


	/**
	 * Divides all the pixels by a value in a Region of Interest
	 * @param r value to be divided into the pixels in the real plane
	 * @param i value to be divided into the pixels in the imaginary plane
	 * @param r2 Region of Interest
	 * @return this
	 */
	public final ComplexImage divide(float r, float i, ROI r2) {
		for (int y = r2.uy(); y < r2.ly(); y++) {
			for (int x = r2.ux(); x < r2.lx(); x++) {
				divide(x, y, r, i);
			} 
		} 
		return this;
	} 


	/**
	 * Prints the Region of Interest in integer format.
	 * <DT><DL><DL>-Example of output on an image with width 100 and height 120:</DT>
	 * <DL>       <DT>100 : 120</DT>
	 * <DT>5 23 54 7 3 23 46 253 23 53 65 34 ...</DT></DL>
	 * <DL>       <DT>100 : 120</DT>
	 * <DT>10 20 32 12 32 56 40 59 42 39 43 ...</DT></DL></DL></DL>
	 * @param r Region of Interest
	 */
	public String toString(ROI r) {
		String	str;

		str = real.toString(r);
		str += imag.toString(r);
		return str;
	} 


	/**
	 * Adds a Region of Interest in another GrayImage from a Region of Interest of this image
	 * @param im the ComplexImage to add
	 * @param sourceImage Region of Interest for the Source Image
	 * @param destImage Region of Interest for the Destination Image
	 * @return this
	 */
	public final void add(ComplexImage im, ROI sourceImage, ROI destImage) {
		for (int y = sourceImage.uy(); y < sourceImage.ly(); y++) {
			for (int x = sourceImage.ux(); x < sourceImage.lx(); x++) {
				add(x, y, im.getReal(x - sourceImage.ux() + destImage.ux(), y - sourceImage.uy() + destImage.uy()), im.getImag(x - sourceImage.ux() + destImage.ux(), y - sourceImage.uy() + destImage.uy()));
			} 
		} 
	} 


	/**
	 * Subtracts a Region of Interest in another GrayImage from a Region of Interest of this image
	 * @param im the ComplexImage to subtract
	 * @param sourceImage Region of Interest for the Source Image
	 * @param destImage Region of Interest for the Destination Image
	 * @return this
	 */
	public final void subtract(ComplexImage im, ROI sourceImage, ROI destImage) {
		for (int y = sourceImage.uy(); y < sourceImage.ly(); y++) {
			for (int x = sourceImage.ux(); x < sourceImage.lx(); x++) {
				subtract(x, y, im.getReal(x - sourceImage.ux() + destImage.ux(), y - sourceImage.uy() + destImage.uy()), im.getImag(x - sourceImage.ux() + destImage.ux(), y - sourceImage.uy() + destImage.uy()));
			} 
		} 
	} 


	/**
	 * Multiplies a Region of Interest of another GrayImage by a Region of Interest of this image
	 * @param im the ComplexImage to multiply
	 * @param sourceImage Region of Interest for the Source Image
	 * @param destImage Region of Interest for the Destination Image
	 * @return this
	 */
	public final void multiply(ComplexImage im, ROI sourceImage, ROI destImage) {
		for (int y = sourceImage.uy(); y < sourceImage.ly(); y++) {
			for (int x = sourceImage.ux(); x < sourceImage.lx(); x++) {
				multiply(x, y, im.getReal(x - sourceImage.ux() + destImage.ux(), y - sourceImage.uy() + destImage.uy()), im.getImag(x - sourceImage.ux() + destImage.ux(), y - sourceImage.uy() + destImage.uy()));
			} 
		} 
	} 


	/**
	 * Divides by a Region of Interest in this image by a Region of Interest of another ComplexImage
	 * @param im the ComplexImage to divide
	 * @param sourceImage Region of Interest for the Source Image
	 * @param destImage Region of Interest for the Destination Image
	 * @return this
	 */
	public final void divide(ComplexImage im, ROI sourceImage, ROI destImage) {
		for (int y = sourceImage.uy(); y < sourceImage.ly(); y++) {
			for (int x = sourceImage.ux(); x < sourceImage.lx(); x++) {
				divide(x, y, im.getReal(x - sourceImage.ux() + destImage.ux(), y - sourceImage.uy() + destImage.uy()), im.getImag(x - sourceImage.ux() + destImage.ux(), y - sourceImage.uy() + destImage.uy()));
			} 
		} 
	} 


	/**
	 * Returns the MagnitudeImage (RealGrayImage) of the this ComplexImage
	 */
	public final RealGrayImage getMagnitudeImage() {
		RealGrayImage im = new RealGrayImage(X, Y);
		float					value = 0;

		for (int x = 0; x < X; x++) {
			for (int y = 0; y < Y; y++) {
				value = (float) (Math.sqrt((Math.pow(getReal(x, y), 2) + Math.pow(getImag(x, y), 2))));
				im.set(x, y, value);
			} 
		} 
		return im;
	} 

}


/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

