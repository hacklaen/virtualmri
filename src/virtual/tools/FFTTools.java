/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

/*
 * Copyright (C) 1999, 2000 Thomas Hacklaender, Christian Schalla,
 * Andreas Truemper
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License 2
 * as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package virtual.tools;

import java.awt.*;
import javax.swing.*;
import java.awt.image.*;


/**
 * Die Klasse kapselt einige Funktionen, die zur Berechnung der
 * Fouriertransformation eines Bildes nuetzlich sind.
 * <DL><DT><B>Modifications: </B><DD>
 * Thomas Hacklaender 2000.8.15:
 * In getReshiftedIFFT den statischen Methodenaufruf doFFT(Image, boolean) durch
 * die eigene Methode reverseFFT ersetzt.<br>
 * Thomas Hacklaender 2000.8.15:
 * Zwei neue statische Methoden zur Fouriertransformation und Rücktransformation
 * forwardFFT und reverseFFT implementiert. Diese verwenden die jigl-Bibliothek.
 * In jigl 1.3 lauten die Methoden FFT.forward und FFT.inverse. Sie sind statisch
 * definiert. In jigl 1.4 sind sie nicht statisch und lauten forward und reverse.
 * Durch Introspection wird eine Kompatibilität mit den jigl  1.3 und 1.4
 * erzielt.<br>
 * Thomas Hacklaender 2000.8.13:
 * !!! Achtung: Die jigl-Bibliothek speichert die Bildmatrizen in der
 * Form data[y][x], dieses Projekt aber in der Form data[x][y]. Deshalb
 * duerfen die jigl-Konstruktoren, die im Aufruf eine Bildmatrix vorsehen
 * nicht verwendet werden.!!!<br>
 * Thomas Hacklaender 2000.8.13:
 * Methode getFFT(float[][]) geloescht, da sie im Projekt nicht mehr
 * benoetigt wird.<br>
 * Thomas Hacklaender 2000.8.13:
 * In der jigl-Bibliothek Version 1.3 und 1.4 ist die Methode
 * RealGrayImage(float[][] dat) fehlerhat implementtiert. Um die Bibliothek
 * trotzdem als Ganzes uebernehmen zu koennen, sind hier und in ImagePlus
 * einige Anpassungen notwendig:<br>
 * Methode RealGrayImage makeRealGrayImage(int, int, short[]) eingefügt.<br>
 * Methode RealGrayImage makeRealGrayImage(float[][]) eingefügt.<br>
 * In der Methode getFFT den Aufruf von RealGrayImage(float[][]) durch
 * makeRealGrayImage(float[][]) ersetzt.<br>
 * Thomas Hacklaender 2002.10.13:
 * Strings internationalisiert.<br>
 * </DD></DL>
 * @author   Christian Schalla
 * @author   Andreas Truemper
 * @version  1.2, 2002.10.13
 */
public class FFTTools {


	/**
	 * Standardkonstruktor.
	 */
	public FFTTools() {}


	/**
	 * Die Methode berechnet die Fourier-Trasformation eines Bildes
	 * @param img Das Ursprungsbild.
	 * @return Die Fourier-Trasformierte des Ursprungsbildes.
	 * @author Thomas Hacklaender
	 */
	public static jigl.image.ComplexImage forwardFFT(jigl.image.Image img) {
		jigl.image.ComplexImage fourImage = null;

		jigl.image.utils.FFT		fft = new jigl.image.utils.FFT();

		try {

			// Testet, ob jigl Version 1.3
			java.lang.reflect.Method	m = fft.getClass().getDeclaredMethod("inverse", new Class[] {
				jigl.image.Image.class
			});

			// ist Version 1.3
			m = fft.getClass().getDeclaredMethod("forward", new Class[] {
				jigl.image.Image.class
			});
			fourImage = (jigl.image.ComplexImage) m.invoke(null, new Object[] {
				img
			});
			m = null;
		} catch (Exception e1) {

			// jigl Version 1.4
			try {
				java.lang.reflect.Method	m = fft.getClass().getDeclaredMethod("forward", new Class[] {
					jigl.image.Image.class
				});

				fourImage = (jigl.image.ComplexImage) m.invoke(fft, new Object[] {
					img
				});
				m = null;
			} catch (Exception e2) {
				System.out.println(java.util.ResourceBundle.getBundle("virtual/tools/resources/tools_loc").getString("ffttools.exception.notfound.forward"));
			} 
		} 
		fft = null;
		return fourImage;
	} 


	/**
	 * Die Methode berechnet die Fourier-Rücktrasformation eines Bildes
	 * @param fourImage Das Fourier-Bild.
	 * @return Die Rücktransformierte des Fourier-Bildes.
	 * @author Thomas Hacklaender
	 */
	public static jigl.image.ComplexImage reverseFFT(jigl.image.Image img) {
		jigl.image.ComplexImage compImage = null;

		jigl.image.utils.FFT		fft = new jigl.image.utils.FFT();

		try {

			// jigl Version 1.3
			java.lang.reflect.Method	m = fft.getClass().getDeclaredMethod("inverse", new Class[] {
				jigl.image.Image.class
			});

			compImage = (jigl.image.ComplexImage) m.invoke(null, new Object[] {
				img
			});
			m = null;
		} catch (Exception e1) {

			// jigl Version 1.4
			try {
				java.lang.reflect.Method	m = fft.getClass().getDeclaredMethod("reverse", new Class[] {
					jigl.image.Image.class
				});

				compImage = (jigl.image.ComplexImage) m.invoke(fft, new Object[] {
					img
				});
				m = null;
			} catch (Exception e2) {
				System.out.println(java.util.ResourceBundle.getBundle("virtual/tools/resources/tools_loc").getString("ffttools.exception.notfound.inverse"));
			} 
		} 
		fft = null;
		return compImage;
	} 


	/**
	 * Die Methode berechnet aus dem gegebenen Real- und Imaginaerteil der FFT
	 * eines Bildes das Magnitudenbild der Fouriertransformation.
	 * @param imageReal Der Realteil der FFT des Ursprungsbildes.
	 * @param imageImag Der Imaginaerteil der FFT des Ursprungsbildes.
	 * @return Ein eindimensionales Feld von Ganzzahlen, das zur Erzeugung einer
	 * MemoryImageSource benutzt werden kann.
	 */
	private static int[] getShiftedFFTImageSource(float[][] imageReal, float[][] imageImag) {

		// Shiften
		float[][] shiftedImgRe = shift(imageReal, 128, 128);
		float[][] shiftedImgIm = shift(imageImag, 128, 128);

		// Berechnen des Magnitudenbildes
		float[][] magImage = magnitude(shiftedImgRe, shiftedImgIm);

		// Logarithmisch skalieren
		float[][] magscaleImage = scaleMagnitude(magImage);

		// Fenstern
		int[][]		windowedImg = autoWindow(magscaleImage);

		// In ein 1-dim. Feld umwandeln
		int[]			magnitudeImage = convertIntArrayToImage(windowedImg);

		return magnitudeImage;
	}		// Methode getShiftedFFTImageSource(float[][] imageReal, float[][] imageImag)
 

	/**
	 * Die Methode berechnet aus einem ImagePlus-Objekt
	 * das Magnitudenbild der Fouriertransformation.
	 * @param ip das Ursprungsbild.
	 * @return Ein eindimensionales Feld von Ganzzahlen, das zur Erzeugung einer
	 * MemoryImageSource benutzt werden kann.
	 */
	public static int[] getShiftedFFTImageSource(ImagePlus ip) {

		// Auslesen der Real- und Imaginaerteils der FFT des Bildes
		float[][] imageReal = ip.getFFTImageReal();
		float[][] imageImag = ip.getFFTImageImag();

		// Berechnen des magnitudenbildes
		return getShiftedFFTImageSource(imageReal, imageImag);
	}		// Methode getShiftedFFTImageSource(ImagePlus ip)
 

	/**
	 * Die Methode berechnet die Ruecktransformation eines fouriertransformierten
	 * Bildes.
	 * @param imgReal Der Reateil der Fourier-Transformation.
	 * @param imgImag Der Imaginaerteil der Fourier-Transformation.
	 * @return Ein eindimensionales Feld von Ganzzahlen, das zur Erzeugung einer
	 * MemoryImageSource benutzt werden kann.
	 */
	public static float[][] getReshiftedIFFT(float[][] imgReal, float[][] imgImag) {

		// Zurueckshiften des Bildes
		float[][]								re = shift(imgReal, 128, 128);
		float[][]								im = shift(imgImag, 128, 128);

		int											size = imgReal.length;

		// Erzeugen eines ComplexImage-Objekts
		jigl.image.ComplexImage comImg = new jigl.image.ComplexImage(size, size);

		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				comImg.setReal(x, y, re[x][y]);
				comImg.setImag(x, y, im[x][y]);
			}		// for
 		 }		// for
 
		// Ruecktransformation
		jigl.image.ComplexImage origImage = null;

		origImage = FFTTools.reverseFFT(comImg);

		// Real- und Imaginaerteil auslesen
		float[][] origImageReal = new float[size][size];
		float[][] origImageImag = new float[size][size];

		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				origImageReal[x][y] = origImage.getReal(x, y);
				origImageImag[x][y] = origImage.getImag(x, y);
			}		// for
 		 }		// for
 
		float[][] magImage = magnitude(origImageReal, origImageImag);

		return magImage;
	}		// getReshiftedIFFT(float[][] imgReal, float[][] imgImag)
 

	/**
	 * Die Methode berechnet die Ruecktransformation eines fouriertransformierten
	 * Bildes und stellt diese als eindimensionales Feld von Ganzzahlen und in
	 * 8-Bit konvertiert zur Verfuegung.
	 * @param imgReal Der Reateil der Fourier-Transformation.
	 * @param imgImag Der Imaginaerteil der Fourier-Transformation.
	 * @return Ein eindimensionales Feld von Ganzzahlen, das zur Erzeugung einer
	 * MemoryImageSource benutzt werden kann.
	 */
	public static int[] getReshiftedIFFTImageSource(float[][] imgReal, float[][] imgImag) {
		float[][] reshiftedIFFT = getReshiftedIFFT(imgReal, imgImag);
		int[][]		windowedImage = autoWindow(reshiftedIFFT);
		int[]			finalImage = convertIntArrayToImage(windowedImage);

		return finalImage;
	}		// Methode get ReshiftedIFFTImageSource
 

	/**
	 * Die Methode konvertiert ein zweidimensionales Feld von Fließkommazahlen
	 * in ein eindimensionales Feld von Ganzzahlen und wandelt dieses in 8 Bit um.
	 * @param origImg Das Fliekommaarray.
	 * @return Das eindimensionale Feld von Ganzzahlen, das als ImageSource verwendet
	 * kann.
	 */
	public static int[] convertTo8BitImageSrc(float[][] origImg) {
		int[][] windowedImage = autoWindow(origImg);
		int[]		finalImage = convertIntArrayToImage(windowedImage);

		return finalImage;
	} 


	/**
	 * Die Methode verschiebt ein Bild um den angegebenen Betrag nach rechts und
	 * nach unten.
	 * @param img Das zu verschiebende Bild.
	 * @param offx Der Betrag, um den das Bild nach rechts verschoben werden soll.
	 * @param offy Der Betrag, um den das Bild nach unten verschoben werden soll.
	 * @return Das verschobene Bild.
	 */
	public static float[][] shift(float[][] img, int offx, int offy) {
		int				size = img.length;
		float[][] shiftedImage = new float[size][size];

		for (int y = 0; y < size; y++) {
			for (int x = 0; x < size; x++) {
				shiftedImage[(x + offx) % size][(y + offy) % size] = img[x][y];
			} 
		} 
		return shiftedImage;
	}		// Methode shift
 

	/**
	 * Die Methode verschiebt ein Bild um den angegebenen Betrag nach rechts und
	 * nach unten.
	 * @param img Das zu verschiebende Bild.
	 * @param offx Der Betrag, um den das Bild nach rechts verschoben werden soll.
	 * @param offy Der Betrag, um den das Bild nach unten verschoben werden soll.
	 * @return Das verschobene Bild.
	 */
	public static jigl.image.ComplexImage shift(jigl.image.ComplexImage img, int offx, int offy) {
		int											size = 256;
		jigl.image.ComplexImage shiftedImage = new jigl.image.ComplexImage(256, 256);

		for (int y = 0; y < size; y++) {
			for (int x = 0; x < size; x++) {
				shiftedImage.set((x + offx) % size, (y + offy) % size, img.getReal(x, y), img.getImag(x, y));
			} 
		} 
		return shiftedImage;
	}		// Methode shift
 

	/**
	 * Die Methode berechnet aus dem Real- und Imaginaerteil eines fouriertransformierten
	 * Bildes das Magnitudenbild.
	 * @param re Der Realteil des fouriertransformierten Bildes.
	 * @param im Der Imaginaerteil des fouriertransformierten Bildes.
	 * @return Das Magnitudenbild.
	 */
	public static float[][] magnitude(float re[][], float im[][]) {
		int		size = re.length;
		float result[][] = new float[size][size];

		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				result[x][y] = (float) (Math.sqrt(re[x][y] * re[x][y] + im[x][y] * im[x][y]));
			} 
		} 
		return result;
	}		// Methode magnitude
 

	/**
	 * Die Methode berechnet aus dem Real- und Imaginaerteil eines fouriertransformierten
	 * Bildes das Phasenbild.
	 * @param re Der Realteil des fouriertransformierten Bildes.
	 * @param im Der Imaginaerteil des fouriertransformierten Bildes.
	 * @return Das Phasenbild.
	 */
	public static float[][] calcPhase(float re[][], float im[][]) {
		float result[][] = new float[256][256];

		for (int x = 0; x < 256; x++) {
			for (int y = 0; y < 256; y++) {
				result[x][y] = (float) (Math.atan(im[x][y] / re[x][y]));
			} 
		} 
		return result;
	}		// Methode calcPhase
 

	/**
	 * Die Methode skaliert die Grauwerte eine Bildes logarithmisch. Dies wird
	 * zur Anzeige des Magnitudenbildes einer Fouriertransformation benoetigt.
	 * @param img Das Bild, dessen Grauwerte log. skaliert werden sollen.
	 * @return Das Bild, dessen Grauwerte log. skaliert wurden.
	 */
	public static float[][] scaleMagnitude(float img[][]) {
		int		size = img.length;
		float result[][] = new float[size][size];
		float max = 0;

		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				result[x][y] = (float) (30 * (Math.log(1 + img[x][y])) / Math.log(1.1));
				if (result[x][y] > max) {
					max = result[x][y];

					// if (result[x][y]>255) result[x][y]=255;
				} 
			} 
		} 
		return result;
	}		// Methode scaleMagnitude
 

	/**
	 * Die Methode fenstert ein Bild so, dass alle Grauwerte dargestellt werden.
	 * @param Das zu fensternde Bild.
	 * @return Das automatisch gefensterte Bild.
	 */
	public static int[][] autoWindow(float img[][]) {
		int			size = img.length;
		long		min = 999999999;
		long		max = 0;
		int[][] windowedImage = new int[size][size];
		double	step;

		// Bestimmen des groessten und kleinsten Wertes
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				if ((long) img[x][y] > max) {
					max = (long) img[x][y];
				} 
				if ((long) img[x][y] < min) {
					min = (long) img[x][y];
				} 
			} 
		} 

		// Nun aufgrund des groessten und kleinsten Wertes auf 256 Graustufen Fenstern
		step = (double) (max - min) / (double) 256;
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				int val = (int) ((double) (img[x][y] - min) / step);

				windowedImage[x][y] = val;
			} 
		} 

		return windowedImage;

	}		// Methode autoWindow
 

	/**
	 * Die Methode konvertiert ein zweidimensionales Feld von Ganzzahlen in
	 * ein eindimensionales Feld von Ganzzahlen, aus dem ein Grauwertbild mittels
	 * einer MemoryImageSource erstellt werden kann.
	 * @param source Das zu konvertierende zweidimensionale Feld.
	 * @return Das Feld, welches zur Erzeugung einer MemoryImageSource dienen kann.
	 */
	public static int[] convertIntArrayToImage(int[][] source) {
		int		size = source.length;
		int[] intArray = new int[size * size];

		for (int i = 0; i < (size * size); i++) {
			int hlp = (int) source[i / size][i % size];

			hlp = hlp & 0xff;
			intArray[i] = (255 << 24) | hlp << 16 | hlp << 8 | hlp;
		} 

		return intArray;
	}		// Methode convertIntArrayToImage
 

	/**
	 * Die Methode liefert den Realteil eines komplexen Bildes zurueck.
	 * @param  ci Das komplexe Bild.
	 * @return Der Realteil des komplexen Bildes.
	 */
	public static float[][] getReal(jigl.image.ComplexImage ci) {
		final int X = 256;
		final int Y = 256;

		float[][] re = new float[X][Y];

		for (int x = 0; x < X; x++) {
			for (int y = 0; y < Y; y++) {
				re[x][y] = ci.getReal(x, y);
			} 
		} 
		return re;
	} 


	/**
	 * Die Methode liefert den Imaginaerteil eines komplexen Bildes zurueck.
	 * @param  ci Das komplexe Bild.
	 * @return Der Imaginaerteil des komplexen Bildes.
	 */
	public static float[][] getImag(jigl.image.ComplexImage ci) {
		final int X = 256;
		final int Y = 256;

		float[][] im = new float[X][Y];

		for (int x = 0; x < X; x++) {
			for (int y = 0; y < Y; y++) {
				im[x][y] = ci.getImag(x, y);
			} 
		} 
		return im;
	} 


	/**
	 * Die Methode erstellt aus einer Matrix von Integerwerten ein RealGrayImage-Objekt.
	 * @param img Die Matrix mit Integerwerten.
	 * @return Das RealGrayImage-Objekt.
	 */
	public static jigl.image.RealGrayImage makeRealGrayImage(int[][] img) {
		jigl.image.RealGrayImage	rg = new jigl.image.RealGrayImage(img.length, img[0].length);

		for (int x = 0; x < img.length; x++) {
			for (int y = 0; y < img[0].length; y++) {
				rg.set(x, y, (float) img[x][y]);
			} 
		} 
		return rg;
	} 


	/**
	 * Die Methode erstellt aus einer Matrix von Float-Werten ein RealGrayImage-Objekt.
	 * @param img Die Matrix mit Float-Werten.
	 * @return Das RealGrayImage-Objekt.
	 * @author tha 2000.8.13
	 */
	public static jigl.image.RealGrayImage makeRealGrayImage(float[][] img) {
		jigl.image.RealGrayImage	rg = new jigl.image.RealGrayImage(img.length, img[0].length);

		for (int x = 0; x < img.length; x++) {
			for (int y = 0; y < img[0].length; y++) {
				rg.set(x, y, img[x][y]);
			} 
		} 
		return rg;
	} 


	/**
	 * Die Methode erstellt aus einem Vektor von Short-Werten ein RealGrayImage-Objekt.
	 * @param dimX Groesse des Bildes in x-Richtung
	 * @param dimY Groesse des Bildes in y-Richtung
	 * @param img Der Vektor mit Short-Werten.
	 * @return Das RealGrayImage-Objekt.
	 * @author tha 2000.8.13
	 */
	public static jigl.image.RealGrayImage makeRealGrayImage(int dimX, int dimY, short[] img) {
		jigl.image.RealGrayImage	rg = new jigl.image.RealGrayImage(dimX, dimY);

		for (int x = 0; x < dimX; x++) {
			for (int y = 0; y < dimY; y++) {
				rg.set(x, y, (float) img[y * dimX + x]);
			} 
		} 
		return rg;
	} 


	/**
	 * Die Methode berechnet die Fourier-Transformation eines Bildes und liefert
	 * diese als 3-dimensionales Feld zurueck.
	 * @param myFloatImage Das Bild, dessen FFT berechnet werden soll.
	 * @return Ein dredimensionales Feld von Ganzzahlen. returnImg[0] ist der
	 * Realteil, returnImg[1] der Imaginaerteil. die 2. Komponente des Feldes
	 * repraesentiert die x-Koordinate, die 3. Komponente die y-Koordinate.
	 */

	/*
	 * tha 2000.8.13: Methode wird nicht laenger benoetigt
	 * public static float[][][] getFFT(float[][] myFloatImage) {
	 * // Erzeugen eines REalGrayImage-Objektes, mit dem die FFT durchgefuehrt wird
	 * // Ersetzt: 2000.8.13 tha
	 * // jigl.image.RealGrayImage	rgImg = new jigl.image.RealGrayImage(myFloatImage);
	 * jigl.image.RealGrayImage	rgImg = makeRealGrayImage(myFloatImage);
	 * // Durchfuehren der FFT
	 * jigl.image.ComplexImage		cImg = jigl.image.utils.FFT.doFFT(rgImg, true);
	 * // Extrahieren des Real- und Imaginaerteils uas dem komplexen Bild
	 * float[][]									fftImageReal = new float[256][256];
	 * float[][]									fftImageImag = new float[256][256];
	 * for (int x = 0; x < 256; x++) {
	 * for (int y = 0; y < 256; y++) {
	 * fftImageReal[x][y] = cImg.getReal(x, y);
	 * fftImageImag[x][y] = cImg.getImag(x, y);
	 * }		// for
	 * }		// for
	 * 
	 * float[][][] returnimg = new float[2][256][256];
	 * returnimg[0] = fftImageReal;
	 * returnimg[1] = fftImageImag;
	 * return returnimg;
	 * }
	 */

}






/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

