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

import virtual.tools.*;

import rad.dicom.dcm.*;


/**
 * Die Klasse repraesentiert die Zeichenflaeche des k-Raum-Manipulators. In ihr
 * werden alle 4 darzustellenden Bilder verwaltet. Das Originalbild,
 * der original k-Raum und der manipulierte k-Raum werden der Klasse einfach uebergeben.
 * Die Ruecktransformation wird innerhalb dieser Klasse aus dem uebergebenen Real-
 * und Imaginaerteil neu berechnet.
 * <DL><DT><B>Modifications: </B><DD>
 * Thomas Hacklaender 2000.8.13:
 * Konstruktor KSpaceManipulatorCanvas(ViewerFrame) auf KSpaceManipulatorCanvas(JFrame)
 * geaendert.<br>
 * Thomas Hacklaender 2000.8.13:
 * In recalcTargetImage die x- und y-Koordinate getauscht. Dies war ein Bug, der
 * durch die ebenfalls falsche Koordinatenrichtung un ImagePlus.calcFFT() bislang
 * kompensiert wurde.<br>
 * Thomas Hacklaender 2000.8.13:
 * Debug-Meldungen in paint geloescht.<br>
 * Thomas Hacklaender 2002.10.13:
 * Strings internationalisiert.<br>
 * </DD></DL>
 * @author   Christian Schalla
 * @author   Andreas Truemper
 * @version  1.2, 2002.10.16
 */
public class KSpaceManipulatorCanvas extends JPanel {


	/**
	 * Das ImagePlus-Objekt des Originalbildes.
	 */
	private ImagePlus		sourceImg = null;


	/**
	 * Das zuruecktranformierte Bild.
	 */
	ImagePlus						destImg;


	/**
	 * Der unveraenderte k-Raum des Originalbildes.
	 */
	private Image				sourceKSpace = null;


	/**
	 * Der manipulierte Realteil des k-Raums.
	 */
	private float[][]		targetKSpaceReal;


	/**
	 * Der manipulierte Imaginaerteil des k-Raums.
	 */
	private float[][]		targetKSpaceImag;


	/**
	 * Das Magnitudenbild des manipulierten k-Raums.
	 */
	private Image				targetKSpace = null;


	/**
	 * Das aus dem manipulierten k-Raum zuruecktransformierte Bild.
	 */
	private Image				targetImg = null;


	/**
	 * Rueckreferenz zum Hauptfenster
	 */
	private ImagePanel	backReference;


	/**
	 * Der Konstruktor setzt den Hintergrund der Zeichenflaeche auf schwarz.
	 */
	public KSpaceManipulatorCanvas(ImagePanel ip) {

		// Geaendert: tha 2000.8.17
		// public KSpaceManipulatorCanvas(ViewerFrame myViewer) {
		this.setBackground(Color.black);
		backReference = ip;
	}		// Konstruktor


	/**
	 * Die Methode speichert das Originalbild in einer Instanzvariablen.
	 * @param i Das ImagePlus-Objekt des im DICOM-Viewer selektierten Bildes.
	 */
	public void setSourceImg(ImagePlus i) {
		sourceImg = i;

		// int[] intArray = FFTTools.getShiftedFFTImageSource(sourceImg);
		// sourceKSpace = createImage(new MemoryImageSource(256,256,intArray,0,256));
	}		// Methode setSourceImage
 

	/**
	 * Die Methode speichert das Magnitudenbild der Fourier-Transformation des
	 * Originalbildes in einer Instanzvariablen.
	 * @param i Der unveraenderte k-Raum des Originalbildes.
	 */
	public void setSourceKSpace(Image i) {
		sourceKSpace = i;
	}		// Methode setSourceKSpace
 

	/**
	 * Die Methode speichert den Real- und Imaginaerteil des manipulierten k-Raums
	 * in Instanzvariablen. Ausserdem wird das Magnitudenbild des manipulierten
	 * k-Raums gesetzt und daraus ein darstellbares Java-Image berechnet.
	 * @param re Der Realteil des manipulierten k-Raums.
	 * @param re Der Imaginaerteil des manipulierten k-Raums.
	 * @param re Das Magnitudenbild des manipulierten k-Raums.
	 */
	public void setTargetKSpace(float[][] re, float[][] im, int[] mag) {
		targetKSpaceReal = re;
		targetKSpaceImag = im;
		targetKSpace = createImage(new MemoryImageSource(256, 256, mag, 0, 256));
	}		// Methode setTargetKSpace
 

	/**
	 * Die Methode berechnet aus dem Real- und Imaginaerteils des manipulierten
	 * k-Raums die Ruecktransformation.
	 */
	public void recalcTargetImage() {

		// invers fouriertransformiertes Float-Bild berechnen. 12-Bit!!!
		float[][] reFFT = FFTTools.getReshiftedIFFT(targetKSpaceReal, targetKSpaceImag);

		// nach Short konvertieren
		short[]		intIFFT = new short[256 * 256];

		for (int y = 0; y < 256; y++) {
			for (int x = 0; x < 256; x++) {

				// Geaendert: tha 2000.8.13
				// intIFFT[(x * 256) + y] = (short) reFFT[x][y];
				// In DcmImage laeft die x-Koordinate als erste
				intIFFT[(y * 256) + x] = (short) reFFT[x][y];
			} 
		} 

		// Auslesen des DcmDataObjects aus dem Ursprungsbild
		DcmDataObject myddo = sourceImg.getDDO();

		// DcmImage aus der Bildmatrix und dem DicomDataObject des
		// Ursprungsbildes erzeugen
		DcmImage			dcmimg = null;

		try {
			dcmimg = new DcmImage(myddo);
			dcmimg.pixel16 = intIFFT;
		} catch (Exception err) {
			err.printStackTrace();
		} 

		// ImagePlus-Objekt erzeugen.
		destImg = new ImagePlus(dcmimg);

		// DDo im ImagePlus abspeichern
		destImg.setDDO(myddo);

		// Erzeugungsdatum und Uhrzeit im Bild speichern
		destImg.setCurrentDate();
		destImg.optimalWindowing();
		destImg.updateAWTImage();

		// 8-Bit Bild zur Konvertierung in ein Java-Image erstellen
		targetImg = destImg.getAWTImage();
	} 


	/**
	 * Die Methode setzt das ruecktransformierte Bild. Dies wird beim Fenstern des
	 * ruecktransformierten Bildes benoetigt.
	 * @param img Das Bild, das als ruecktransformiertes Bild angezeigt werden soll.
	 */
	public void setTragetImage(Image img) {
		targetImg = img;
	} 


	/**
	 * Die Methode aktualisiert die Zeichenflaeche des k-Raum-Manipulators.
	 * Zunaecht wird die gesamte zeichenflaeche geloescht, dann werden die vier
	 * Bilder eingezeichnet, dann die Bildbeschriftungen und schliesslich noch
	 * eine Umrandung um die Bilder.
	 * @param g Der Graphikkontext.
	 */
	public void paint(Graphics g) {

		// tha 2000.8.13
		// System.out.println("Paint");
		g.setColor(Color.black);
		g.fillRect(0, 0, 512, 512);


		// Einzeichnen der 4 Bilder und der Bildbeschriftungen
		g.setColor(Color.green);
		if (sourceImg != null) {
			g.drawImage(sourceImg.getAWTImage(), 0, 0, 256, 256, this);
			g.drawString(java.util.ResourceBundle.getBundle("virtual/tools/resources/tools_loc").getString("kspacemanipulatorcanvas.original.title"), 5, 15);
		} 
		if (sourceKSpace != null) {
			g.drawImage(sourceKSpace, 256, 0, 256, 256, this);
			g.drawString(java.util.ResourceBundle.getBundle("virtual/tools/resources/tools_loc").getString("kspacemanipulatorcanvas.kspace.original.title"), 261, 15);
		} 

		if (targetKSpace != null) {
			g.drawImage(targetKSpace, 0, 256, 256, 256, this);
			g.drawString(java.util.ResourceBundle.getBundle("virtual/tools/resources/tools_loc").getString("kspacemanipulatorcanvas.kspace.manipulated.title"), 5, 271);

			// tha 2000.8.13
			// System.out.println("Man. K-Raum geziechnet");
		} 

		if (targetImg != null) {
			g.drawImage(targetImg, 256, 256, 256, 256, this);
			g.drawString(java.util.ResourceBundle.getBundle("virtual/tools/resources/tools_loc").getString("kspacemanipulatorcanvas.kspace.reversed.title"), 261, 271);
		} 


		// Einzeichnen der Bildumrandung
		g.setColor(Color.white);
		g.drawRect(0, 0, 511, 511);
		g.drawLine(0, 256, 512, 256);
		g.drawLine(256, 0, 256, 512);
	}		// Methode paint
 

	/**
	 * Die Methode liefert das zuruecktransformierte Bild zurueck.
	 * @return Das zuruecktransformierte Bild.
	 */
	public ImagePlus getDestinationImage() {
		return destImg;
	} 

}






/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

