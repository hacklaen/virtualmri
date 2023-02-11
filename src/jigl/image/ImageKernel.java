/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

/*
 * JIGL--Java Imaging and Graphics Library
 * Copyright (C)1999 Brigham Young University
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 * 
 * A copy of the GNU Library General Public Licence is contained in
 * /jigl/licence.txt
 */

package jigl.image;
import jigl.*;


/**
 * ImageKernel is an extension of GrayImage with some pre-defined kernals.
 */

public class ImageKernel extends RealGrayImage {


	/**
	 * Uniform Kernal.
	 * <TABLE BORDER CELLPADDING=5 COLS=3 WIDTH="13" >
	 * <TR><TD>1</TD><TD>1</TD><TD>1</TD></TR>
	 * <TR><TD>1</TD><TD>1</TD><TD>1</TD></TR>
	 * <TR><TD>1</TD><TD>1</TD><TD>1</TD></TR>
	 * </TABLE>
	 */
	public final int	UNIFORM = 0;


	/**
	 * Sorbel Kernal with X orientation.
	 * <TABLE BORDER CELLPADDING=5 COLS=3 WIDTH="13" >
	 * <TR><TD>-1</TD><TD>0</TD><TD>1</TD></TR>
	 * <TR><TD>-2</TD><TD>0</TD><TD>2</TD></TR>
	 * <TR><TD>-1</TD><TD>0</TD><TD>1</TD></TR>
	 * </TABLE>
	 */
	public final int	SOBEL_X = 1;


	/**
	 * Sobel Kernal with Y orientation.
	 * <TABLE BORDER CELLPADDING=5 COLS=3 WIDTH="13" >
	 * <TR><TD>-1</TD><TD>-2</TD><TD>-1</TD></TR>
	 * <TR><TD>0</TD><TD>0</TD><TD>0</TD></TR>
	 * <TR><TD>1</TD><TD>2</TD><TD>1</TD></TR>
	 * </TABLE>
	 */
	public final int	SOBEL_Y = 2;


	/**
	 * Prewitt Kernal with X orientation.
	 * <TABLE BORDER CELLPADDING=5 COLS=3 WIDTH="13" >
	 * <TR><TD>-1</TD><TD>0</TD><TD>1</TD></TR>
	 * <TR><TD>-1</TD><TD>0</TD><TD>1</TD></TR>
	 * <TR><TD>-1</TD><TD>0</TD><TD>1</TD></TR>
	 * </TABLE>
	 */
	public final int	PREWITT_X = 3;


	/**
	 * Prewitt Kernal with Y orientation.
	 * <TABLE BORDER CELLPADDING=5 COLS=3 WIDTH="13" >
	 * <TR><TD>-1</TD><TD>-1</TD><TD>-1</TD></TR>
	 * <TR><TD>0</TD><TD>0</TD><TD>0</TD></TR>
	 * <TR><TD>1</TD><TD>1</TD><TD>1</TD></TR>
	 * </TABLE>
	 */
	public final int	PREWITT_Y = 4;


	/**
	 * Laplacian Kernel.
	 * <TABLE BORDER CELLPADDING=5 COLS=3 WIDTH="13" >
	 * <TR><TD>0</TD><TD>1</TD><TD>0</TD></TR>
	 * <TR><TD>1</TD><TD>4</TD><TD>1</TD></TR>
	 * <TR><TD>0</TD><TD>1</TD><TD>0</TD></TR>
	 * </TABLE>
	 */
	public final int	LAPLACIAN = 5;


	/**
	 * Laplacian 8 Kernel.
	 * <TABLE BORDER CELLPADDING=5 COLS=3 WIDTH="13" >
	 * <TR><TD>1</TD><TD>1</TD><TD>1</TD></TR>
	 * <TR><TD>1</TD><TD>-8</TD><TD>1</TD></TR>
	 * <TR><TD>1</TD><TD>1</TD><TD>1</TD></TR>
	 * </TABLE>
	 */
	public final int	LAPLACIAN_8 = 6;


	/**
	 * Unsharpen Kernel.
	 * <TABLE BORDER CELLPADDING=5 COLS=3 WIDTH="13" >
	 * <TR><TD>0</TD><TD>-1</TD><TD>0</TD></TR>
	 * <TR><TD>-1</TD><TD>5</TD><TD>-1</TD></TR>
	 * <TR><TD>0</TD><TD>-1</TD><TD>0</TD></TR>
	 * </TABLE>
	 */
	public final int	UNSHARP = 7;


	/**
	 * Unsharpen 8 Kernel.
	 * <TABLE BORDER CELLPADDING=5 COLS=3 WIDTH="13" >
	 * <TR><TD>-1</TD><TD>-1</TD><TD>-1</TD></TR>
	 * <TR><TD>-1</TD><TD>9</TD><TD>-1</TD></TR>
	 * <TR><TD>-1</TD><TD>-1</TD><TD>-1</TD></TR>
	 * </TABLE>
	 */
	public final int	UNSHARP_8 = 8;


	/**
	 * Creates a kenel of one of the predefined types
	 * @param val kernel type
	 * @see jigl.image.ImageKernel#UNIFORM
	 * @see jigl.image.ImageKernel#SOBEL_X
	 * @see jigl.image.ImageKernel#SOBEL_Y
	 * @see jigl.image.ImageKernel#PREWITT_X
	 * @see jigl.image.ImageKernel#PREWITT_Y
	 * @see jigl.image.ImageKernel#LAPLACIAN
	 * @see jigl.image.ImageKernel#LAPLACIAN_8
	 * @see jigl.image.ImageKernel#UNSHARP
	 * @see jigl.image.ImageKernel#UNSHARP_8
	 */
	public ImageKernel(int val) throws InvalidKernelException {

		super(3, 3);

		float[][] data2 = null;

		switch (val) {

		case 0: {
			float[][] data1 = {
				 {
					1, 1, 1
				}, {
					1, 1, 1
				}, {
					1, 1, 1
				}
			};

			data2 = data1;
			break;
		} 

		case 1: {
			float[][] data1 = {
				 {
					-1, 0, 1
				}, {
					-2, 0, 2
				}, {
					-1, 0, 1
				}
			};

			data2 = data1;
			break;
		} 

		case 2: {
			float[][] data1 = {
				 {
					-1, -2, -1
				}, {
					0, 0, 0
				}, {
					1, 2, 1
				}
			};

			data2 = data1;
			break;
		} 

		case 3: {
			float[][] data1 = {
				 {
					-1, 0, 1
				}, {
					-1, 0, 1
				}, {
					-1, 0, 1
				}
			};

			data2 = data1;
			break;
		} 

		case 4: {
			float[][] data1 = {
				 {
					-1, -1, -1
				}, {
					0, 0, 0
				}, {
					1, 1, 1
				}
			};

			data2 = data1;
			break;
		} 

		case 5: {
			float[][] data1 = {
				 {
					0, 1, 0
				}, {
					1, -4, 1
				}, {
					0, 1, 0
				}
			};

			data2 = data1;
			break;
		} 

		case 6: {
			float[][] data1 = {
				 {
					1, 1, 1
				}, {
					1, -8, 1
				}, {
					1, 1, 1
				}
			};

			data2 = data1;
			break;
		} 

		case 7: {
			float[][] data1 = {
				 {
					0, -1, 0
				}, {
					-1, 5, -1
				}, {
					0, -1, 0
				}
			};

			data2 = data1;
			break;
		} 

		case 8: {
			float[][] data1 = {
				 {
					-1, -1, -1
				}, {
					-1, 9, -1
				}, {
					-1, -1, -1
				}
			};

			data2 = data1;
			break;
		} 
		case 9: {
			float[][] data1 = {
				 {
					0, 0, 0
				}, {
					1, 1, 0
				}, {
					0, 1, 0
				}
			};

			data2 = data1;
			break;
		} 

		default:
			throw new InvalidKernelException();
		}
		data = data2;
	}


	/**
	 * Creates a uniform kernel of specified size
	 * @param val uniform value for the kernel
	 * @param dimension size of kernel (dimension X dimension)
	 */
	public ImageKernel(float val, int dimension) {
		super(dimension, dimension);
		for (int x = 0; x < dimension; x++) {
			for (int y = 0; y < dimension; y++) {
				data[x][y] = val;
			}
		}

	}


	/**
	 * Creates a kernel out of the given data.
	 * Note: The kernel created will have dimensions equal to the number of elements in the first row.
	 * @param dat two-dimensional data array
	 */
	public ImageKernel(float[][] dat) {
		super(dat.length, dat.length);
		data = dat;

	}


	/**
	 * Creates an ImageKernel from a RealGrayImage
	 */
	public ImageKernel(RealGrayImage img) {
		super(img);
	}


	/**
	 * Normalizes the ImageKernel by 255
	 */
	public void normalize() {
		for (int x = 0; x < X; x++) {
			for (int y = 0; y < Y; y++) {
				data[y][x] = data[y][x] / 255;
			} 
		} 

	} 

}



/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

