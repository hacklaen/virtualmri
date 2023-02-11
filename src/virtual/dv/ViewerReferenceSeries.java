/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

/*
 * Copyright (C) 1999 Christian Schalla,
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
package virtual.dv;


import rad.dicom.dcm.*;
import rad.dicom.ima.*;


/**
 * Die Klasse implementiert eine Routine zum Laden einer Serie von
 * DICOM-Bildern. Damit waehrend des Ladens der DICOM-Viewer nicht blockiert wird,
 * ist die Klasse als Thread implementiert. Waehrend des Ladens der Bilder wird
 * die Fortschrittsanzeige im DICOM-Viewer aktualisiert.
 * <DL><DT><B>Modifications: </B><DD>
 * </DD></DL>
 * @author   Thomas Hacklaender
 * @version  2000.07.10
 */
public class ViewerReferenceSeries {


	/**
	 * Bildpixel. Hier werden im Konstruktor die fuer alle Bilder der Serie
	 * gleichen Elemente gespeichert
	 */
	private short[] pixel16Background;
	private int			nRows;
	private int			nColumns;
	private int			bits;


	/**
	 * Standardkonstruktor. Erzeugt den Hintergrund, dh. den fixen Anteil des
	 * Referenzbildes.
	 * @param r     Bildgroesse in y-Richtung
	 * @param c     Bildgroesse in x-Richtung
	 * @param bits  Anzahl der signifikanten Bits/Pixel
	 */
	public ViewerReferenceSeries(int nRow, int nCol, int b) {

		nRows = nRow;
		nColumns = nCol;
		bits = b;
		pixel16Background = new short[nRows * nColumns];

		// Rahmen
		fillRect(0, 0, 256, 2, 1500, pixel16Background);
		fillRect(0, 254, 256, 2, 1500, pixel16Background);
		fillRect(0, 0, 2, 256, 1500, pixel16Background);
		fillRect(254, 0, 2, 256, 1500, pixel16Background);

		// Graukeile
		for (int r = 0; r < nRows; r++) {
			for (int c = 230; c < 240; c++) {
				pixel16Background[r * nColumns + c] = (short) (255 - r);
			} 
			for (int c = 242; c < 252; c++) {
				pixel16Background[r * nColumns + c] = (short) (4095 - r * 16);
			} 
		} 

		// Schnittphantom
		fillRect(8, 8, 104, 104, 4095, pixel16Background);

		// MIP Phantom
		fillRect(120, 8, 104, 104, 1250, pixel16Background);

		// Aufloesungsphantom
		createStripes(8, 120, 4095, pixel16Background);
		createStripes(8, 216, 4095, pixel16Background);
		createStripes(192, 120, 4095, pixel16Background);
		createStripes(192, 216, 4095, pixel16Background);

		// Quadrate zur Optimierung der Fensterung
		fillRect(64, 132, 48, 48, 1952, pixel16Background);
		fillRect(120, 132, 48, 48, 2016, pixel16Background);
		fillRect(64, 188, 48, 48, 2080, pixel16Background);
		fillRect(120, 188, 48, 48, 2144, pixel16Background);

		// ...dafuer die Innenquadrate
		fillRect(76, 144, 24, 24, 1968, pixel16Background);
		fillRect(132, 200, 24, 24, 2096, pixel16Background);
	}


	/**
	 * Die Methode liefert das Referenzbild zur Position zPosition.
	 * @param zPosition Bildposition [mm] in z-Richtung
	 * @author Thomas Hacklaender
	 * @version 2000.07.10
	 */
	public short[] getImage(int zPosition) {

		// Ein neues Pixelarray aus dem Hintergrundbild erzeugen
		short[] pixel16 = (short[]) pixel16Background.clone();

		// Positionsabhaengige Elemente ergaenzen

		// Schnittphantom
		fillRect(10 + (zPosition / 4) * 3, 8, 6, 104, 0, pixel16);
		fillRect(60, 104 - (zPosition / 4) * 3, 52, 6, 0, pixel16);

		// MIP Phantom
		fillRect(148 + (zPosition / 4) * 3, 36 + (zPosition / 4) * 3, 4, 4, 4095, pixel16);
		fillRect(196 - (zPosition / 4) * 3, 36 + (zPosition / 4) * 3, 4, 4, 4095, pixel16);

		return pixel16;
	} 


	/**
	 * Die Methode liefert das DcmDataObject zum Referenzbild an Position zPosition.
	 * @param zPosition Bildposition [mm] in z-Richtung
	 * @param imaNumber Bildnummer
	 * @author Thomas Hacklaender
	 * @version 2000.07.10
	 */
	public DcmDataObject getDDO(int zPosition, int imaNumber) {
		SecondaryCaptureIOD scIOD = new SecondaryCaptureIOD();

		scIOD.imageNumber = String.valueOf(imaNumber);
		scIOD.set16UBitGrayImage(getImage(zPosition), nRows, nColumns, bits);
		scIOD.setVOILUTModule((int) Math.pow(2, bits - 1), (int) Math.pow(2, bits));
		DcmDataObject ddo = scIOD.getDDO(DcmDataObject.LITTLE_ENDIAN, DcmDataObject.IMPLICITE_VR, DcmDataObject.PLAIN_STORAGE);

		// Image Plane Module PS 3.3-C.7.6.2:
		String				zPositionStr = Double.toString(((double) zPosition));

		ddo.setString(DcmDDE.DD_PixelSpacing, "2.0\\2.0");
		ddo.setString(DcmDDE.DD_ImageOrientationPatient, "1\\0\\0\\0\\1\\0");
		ddo.setString(DcmDDE.DD_ImagePositionPatient, Double.toString(2.0 * nColumns) + "\\" + Double.toString(2.0 * nRows) + "\\" + zPositionStr);
		ddo.setString(DcmDDE.DD_SliceThickness, "4.0");
		ddo.setString(DcmDDE.DD_SliceLocation, zPositionStr);
		return ddo;
	} 


	/**
	 * Die Methode erzeugt ein Streifenmuster von 32*32 Pixel Groesse.
	 * @param x  	  x-Koordinate der linken oberen Ecke
	 * @param y  	  y-Koordinate der linken oberen Ecke
	 * @param val 	Pixelwert
	 * @param pix  	Pixelarray
	 * @param nColumns  Bildgroesse in x-Richtung
	 * @author Thomas Hacklaender
	 * @version 2000.7.9
	 */
	private void createStripes(int x, int y, int val, short[] pix) {
		short sv;

		for (int ix = x; ix < x + 16; ix++) {
			if ((ix % 2) >= 1) {
				sv = (short) val;
			} else {
				sv = 0;
			} 
			for (int iy = y; iy < y + 16; iy++) {
				pix[iy * nColumns + ix] = sv;
			} 
		} 

		for (int iy = y; iy < y + 16; iy++) {
			if ((iy % 2) >= 1) {
				sv = (short) val;
			} else {
				sv = 0;
			} 
			for (int ix = x + 16; ix < x + 32; ix++) {
				pix[iy * nColumns + ix] = sv;
			} 
		} 

		for (int iy = y + 16; iy < y + 32; iy += 2) {
			if ((iy % 4) >= 2) {
				sv = (short) val;
			} else {
				sv = 0;
			} 
			for (int ix = x; ix < x + 16; ix++) {
				pix[iy * nColumns + ix] = sv;
				pix[(iy + 1) * nColumns + ix] = sv;
			} 
		} 

		for (int ix = x + 16; ix < x + 32; ix += 2) {
			if ((ix % 4) >= 2) {
				sv = (short) val;
			} else {
				sv = 0;
			} 
			for (int iy = y + 16; iy < y + 32; iy++) {
				pix[iy * nColumns + ix] = sv;
				pix[iy * nColumns + ix + 1] = sv;
			} 
		} 
	} 


	/**
	 * Zeichnet ein gefuelltes Rechteck mit einem definierten Wert.
	 * @param x  	  x-Koordinate der linken oberen Ecke
	 * @param y  	  y-Koordinate der linken oberen Ecke
	 * @param w  	  Breite
	 * @param h  	  Höhe
	 * @param val 	Pixelwert
	 * @param pix  	Pixelarray
	 * @author Thomas Hacklaender
	 * @version 2000.7.9
	 */
	private void fillRect(int x, int y, int w, int h, int val, short[] pix) {
		short sv = (short) val;

		for (int ix = x; ix < x + w; ix++) {
			for (int iy = y; iy < y + h; iy++) {
				pix[iy * nColumns + ix] = sv;
			} 
		} 
	} 

}







/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

