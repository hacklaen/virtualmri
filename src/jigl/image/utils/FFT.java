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

package jigl.image.utils;
import jigl.*;
import jigl.image.*;


/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class FFT {

	static final int		MAX_POW = 31;
	static final int[]	pow2 = new int[MAX_POW + 1];


	// static initializer
	static {
		pow2[0] = 1;

		for (int i = 1; i <= MAX_POW; i++) {
			pow2[i] = 2 * pow2[i - 1];
		} 
	} 


	/**
	 * Method declaration
	 *
	 *
	 * @param i
	 * @param k
	 *
	 * @return
	 *
	 * @see
	 */
	static int BitReverse(int i, int k) {
		int rev = 0;

		for (int j = 0; j < k; ++j) {
			if ((i & pow2[k - j - 1]) > 0) {
				rev |= pow2[j];
			} 
		} 

		return rev;
	} 


	/**
	 * Dummy Constructor
	 */
	public FFT() {}


	/**
	 * Do Forward FFT calculation
	 */
	static public ComplexImage forward(Image im) {
		return doFFT(im, true);
	} 


	/**
	 * Do Inverse FFT calculation
	 */
	static public ComplexImage inverse(Image im) {
		return doFFT(im, false);
	} 


	/**
	 * Do FFT calculation @param im image to do FFT on.
	 * @param forward true=forward false=inverse
	 */
	static public ComplexImage doFFT(Image im, boolean forward) {

		int				x = 0;
		int				lgn = 0;
		int				n = 0;
		int				rev = 0;
		int				size = im.X() * im.Y();
		float[][] a = null;

		while (x <= MAX_POW) {

			// find the next-highest power of two after the total size of im
			if (pow2[x] >= size) {
				lgn = x;
				n = pow2[lgn];

				a = new float[2][n];

				// load the values from im into the new ComplexImage,
				// in reverse bit order, padding with 0's.
				for (int y = 0; y < n; y++) {
					rev = BitReverse(y, lgn);
					try {
						if (forward == true) {
							if (im instanceof RealGrayImage) {
								RealGrayImage ub = (RealGrayImage) im;

								if (y < (ub.X() * ub.Y())) {
									a[0][rev] = ub.get(y % ub.X(), y / ub.X());
									a[1][rev] = 0f;
								} else {
									a[0][rev] = 0f;
									a[1][rev] = 0f;
								} 
							} else {
								System.out.println("Nichtunterstuetzter Bildtype bei FFT");

								// unsupported type - throw exception?
							} 
						} else {	// rueckwaerts
							if (im instanceof ComplexImage) {
								ComplexImage	ci = (ComplexImage) im;

								if (y < (ci.X() * ci.Y())) {
									a[0][rev] = (float) ci.getReal(y % ci.X(), y / ci.X()) / (float) n;
									a[1][rev] = (float) ci.getImag(y % ci.X(), y / ci.X()) / (float) n;
								} else {
									a[0][rev] = (float) 0f;
									a[1][rev] = (float) 0f;
								} 
							} else {
								System.out.println("Nichtunterstuetzter Bildtype bei FFT");

								// unsupported type - throw exception?
							} 
						}					// rueckwaerts
 
					}						// try
					 catch (ArrayIndexOutOfBoundsException e) {
						a[0][rev] = (float) 0f;
						a[1][rev] = (float) 0f;
					} catch (Exception e) {
						System.out.println("FFT Error:  " + e.toString());
					} 
				}							// for
				 break;
			}								// if
			 x++;
		} 



		// float[] t = new float[2];
		// float[] u = new float[2];
		float			t0, t1, u0, u1;
		float			temp;
		float			t2;
		int				m = 0;
		int				mdiv2 = 0;

		// float[] omega_m = null;
		// float[] omega_m = new float[2];
		// float[] omega = new float[2];
		float			omega_m0 = 0f, omega_m1 = 0f;
		float			omega0, omega1;
		float[][] b = new float[2][n];
		int				i, j, k;
		int				w, h, kplusmdiv2;

		w = im.X();
		h = im.Y();
		double	minustwotimesPIdivm;
		double	minustwotimesPI = (-2.0) * Math.PI;
		double	twotimesPIdivm;
		double	twotimesPI = (2.0) * Math.PI;


		long		starttime = System.currentTimeMillis();

		// do the FFT
		for (int s = 1; s <= lgn; s++) {
			m = pow2[s];

			// mdiv2 = m / 2;
			mdiv2 = m >> 1;		// neu Jens

			// omega_m = new float[2];

			if (forward == true) {
				minustwotimesPIdivm = minustwotimesPI / (double) m;
				omega_m0 = (float) (Math.cos(minustwotimesPIdivm));
				omega_m1 = (float) (Math.sin(minustwotimesPIdivm));
			} else {
				twotimesPIdivm = twotimesPI / (double) m;
				omega_m0 = (float) (Math.cos(twotimesPIdivm));
				omega_m1 = (float) (Math.sin(twotimesPIdivm));
			} 

			// float[] omega = {(float)1f, (float)0f};
			// float[] omega = {/*(float)*/1f, /*(float)*/0f}; // neu Jens

			omega0 = 1f;
			omega1 = 0f;

			for (j = 0; j < mdiv2; ++j) {
				for (k = j; k < n; k += m) {
					kplusmdiv2 = k + mdiv2;
					t0 = omega0;
					t1 = omega1;
					u0 = a[0][k];
					u1 = a[1][k];

					temp = (t0 * a[0][kplusmdiv2]) - (t1 * a[1][kplusmdiv2]);
					t1 = (t0 * a[1][kplusmdiv2]) + (t1 * a[0][kplusmdiv2]);
					t0 = temp;

					// a[k] = u + t; better way to do this?
					a[0][k] = u0 + t0;
					a[1][k] = u1 + t1;
					a[0][kplusmdiv2] = u0 - t0;
					a[1][kplusmdiv2] = u1 - t1;
				} 
				t2 = (omega0 * omega_m0) - (omega1 * omega_m1);
				omega1 = (omega0 * omega_m1) + (omega1 * omega_m0);
				omega0 = t2;
			} 
		} 

		// ComplexImage ar = new ComplexImage(im.X(),im.Y());
		ComplexImage	ar = new ComplexImage(w, h);

		for (i = 0; i < n; i++) {

			// ar.set(i % w, i / w, (float)a[0][i], (float)a[1][i]);
			ar.set(i % w, i / w, a[0][i], a[1][i]);
		} 

		long	endtime = System.currentTimeMillis();

		// IJ.write("Dauer der FFT: " + (endtime - starttime) + "ms");

		return ar;

	} 







	/*
	 * float[] t = new float[2];
	 * float[] u = new float[2];
	 * float temp;
	 * float t2;
	 * int m=0;
	 * int mdiv2=0;
	 * float[] omega_m=null;
	 * //	float[][] b=new float[2][n];
	 * // do the FFT
	 * for (int s = 1; s <= lgn; s++) {
	 * m =pow2[s];
	 * mdiv2 = m / 2;
	 * omega_m = new float[2];
	 * if (forward == true) {
	 * omega_m[0] = (float)(Math.cos((-2.0 * Math.PI) / (double) m));
	 * omega_m[1] = (float)(Math.sin((-2.0 * Math.PI) / (double) m));
	 * }
	 * else {
	 * omega_m[0] = (float)(Math.cos(2.0 * Math.PI / (double) m));
	 * omega_m[1] = (float)(Math.sin(2.0 * Math.PI / (double) m));
	 * }
	 * float[] omega = {(float)1f, (float)0f};
	 * for (int j = 0; j < mdiv2; ++j) {
	 * for (int k = j; k < n; k += m) {
	 * t[0] = omega[0]; t[1] = omega[1];
	 * u[0] = a[0][k]; u[1] = a[1][k];
	 * temp = (t[0]*a[0][k+mdiv2]) - (t[1]*a[1][k+mdiv2]);
	 * t[1] = (t[0]*a[1][k+mdiv2]) + (t[1]*a[0][k+mdiv2]);
	 * t[0] = temp;
	 * // a[k] = u + t; better way to do this?
	 * a[0][k] = u[0]+t[0]; a[1][k] = u[1]+t[1];
	 * a[0][k+mdiv2] = u[0]-t[0]; a[1][k+mdiv2] = u[1]-t[1];
	 * } // for k
	 * t2 = (omega[0]*omega_m[0]) - (omega[1]*omega_m[1]);
	 * omega[1] = (omega[0]*omega_m[1]) + (omega[1]*omega_m[0]);
	 * omega[0] = t2;
	 * } // for j
	 * } // for s
	 * ComplexImage ar = new ComplexImage(im.X(),im.Y());
	 * for (int i=0; i<n; i++) {
	 * ar.set(i % ar.X(), i / ar.X(), (float)a[0][i], (float)a[1][i]);
	 * } // for i
	 * return ar;
	 * }
	 */
}



/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

