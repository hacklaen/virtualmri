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


/**
 * This implements the Region of Interest
 */

package jigl.image;

import java.awt.image.*;


/**
 * The ROI class is the Region of Interest of a JIGL Image
 * @see ImageCanvas
 */
public class ROI {

	int[] ROI_Array = new int[4];


	/**
	 * Creates a Region of Interest initialized to (0,0,0,0)
	 */
	public ROI() {
		ROI_Array[0] = 0;
		ROI_Array[1] = 0;
		ROI_Array[2] = 0;
		ROI_Array[3] = 0;
	}


	/**
	 * Creates a Region of Interest initialized to (a,b,c,d)
	 * @param a upper x corner of the Region of Interest
	 * @param b upper y corner of the Region of Interest
	 * @param c lower x corner of the Region of Interest
	 * @param d lower y corner of the Region of Interest
	 */
	public ROI(int a, int b, int c, int d) {
		ROI_Array[0] = a;
		ROI_Array[1] = b;
		ROI_Array[2] = c;
		ROI_Array[3] = d;
	}


	/**
	 * Returns the upper y corner of the Region of Interest
	 */
	public int uy() {
		return ROI_Array[0];
	} 


	/**
	 * Returns the upper x corner of the Region of Interest
	 */
	public int ux() {
		return ROI_Array[1];
	} 


	/**
	 * Returns the lower y corner of the Region of Interest
	 */
	public int ly() {
		return ROI_Array[2];
	} 


	/**
	 * Returns the lower x corner of the Region of Interest
	 */
	public int lx() {
		return ROI_Array[3];
	} 


	/**
	 * Returns the width of the Region of Interest
	 */
	public int X() {
		return lx() - ux();
	} 


	/**
	 * Returns the height of the Region of Interest
	 */
	public int Y() {
		return ly() - uy();
	} 


	/**
	 * Sets the Region of Interest
	 * @param a upper x corner of the Region of Interest
	 * @param b upper y corner of the Region of Interest
	 * @param c lower x corner of the Region of Interest
	 * @param d lower y corner of the Region of Interest
	 */
	public void setROI(int a, int b, int c, int d) {
		ROI_Array[0] = a;
		ROI_Array[1] = b;
		ROI_Array[2] = c;
		ROI_Array[3] = d;
	} 

}







/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

