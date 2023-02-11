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
package virtual.dv;

import java.io.*;
import javax.swing.*;

import virtual.tools.*;

import rad.dicom.dcm.*;
import rad.dicom.ima.*;
import rad.ijplugin.dcm.*;
import rad.ijplugin.util.*;


/**
 * Die Klasse implementiert eine Routine zum Laden einer Serie von
 * DICOM-Bildern. Damit waehrend des Ladens der DICOM-Viewer nicht blockiert wird,
 * ist die Klasse als Thread implementiert. Waehrend des Ladens der Bilder wird
 * die Fortschrittsanzeige im DICOM-Viewer aktualisiert.
 * <DL><DT><B>Modifications: </B><DD>
 * Keine.<br>
 * </DD></DL>
 * @author   Thomas Hacklaender
 * @version  1.1, 2000.8.20
 */
public class SeriesLoader extends Thread {


	/**
	 * Rueckreferenz zum aufrufenden Fenster.
	 */
	private ViewerFrame			mainFrame;


	/**
	 * IOD des Referenzbildes.
	 */
	private GeneralImageIOD refIOD;


	/**
	 * Import-Data-Block der Dialogbox.
	 */
	private ImportData			imda;


	/**
	 * Eine Referenz auf die Fortschrittsanzeige im DICOM-Viewer.
	 */
	private JProgressBar		pBar;


	/**
	 * Der Konstruktor setzt die uebergebenen Referenzen auf das aufrufende Fenster,
	 * auf eine Datei der zu ladenden Serie und auf die Fortschrittsanzeige im
	 * DICOM-Viewer.
	 * @param mf Eine Rueckreferenz zum aufrufenden Fenster.
	 * @param giodList Die Liste der einzuladenedn Bilder.
	 */
	public SeriesLoader(ViewerFrame mf, GeneralImageIOD ri, ImportData id) {
		mainFrame = mf;
		refIOD = ri;
		imda = id;
		pBar = mainFrame.getProgressBar();
	}


	/**
	 * Die Methode laedt die Bilder ein.
	 */
	public void run() {
		GeneralImageIOD[] gil = null;

		// Es wird immer eine Imageliste, evl. nur aus einem Bild bestehend, eingelesen

		if (imda.isMultiImage) {
			gil = getGeneralImageIODList(refIOD, imda);
		} else {
			gil = new GeneralImageIOD[1];
			gil[0] = refIOD;
		} 

		pBar.setValue(0);
		mainFrame.statusBar.setText("Lade Bilder...");
		for (int i = 0; i < gil.length; i++) {
			pBar.setValue((i * 100) / gil.length);
			loadDcmImage(gil[i]);
		} 
		pBar.setValue(0);
		mainFrame.statusBar.setText("Bereit.");
	} 


	/**
	 * Durchsucht das Verzeichnis, in dem sich die Referenz - GeneralImageIOD
	 * befindet auf weitere DICOM Dateien des selben Patienten die gleichzeitig
	 * noch eine zusaetzliche Bindigung erfuellen (Methode isValid).
	 * @param refFile File Descriptor der Referenz GeneralImageIOD.
	 * @param refGI   Die Referenz GeneralImageIOD.
	 * @param imda    Import-Data-Block der Dialogbox.
	 * @return        Die Liste der GeneralImageIOD's, die die Bedingungen erfuellen.
	 */
	private GeneralImageIOD[] getGeneralImageIODList(GeneralImageIOD refGI, ImportData imda) {
		int								i, k;
		File							f;
		GeneralImageIOD		aktGI;
		GeneralImageIOD[] aktGIL;
		String[]					fNameList;
		DcmDataObject			ddo;
		File							parent;

		parent = new File(imda.theFile.getParent());
		fNameList = parent.list();
		Util.bubbleSortStrings(fNameList);

		pBar.setValue(0);
		mainFrame.statusBar.setText("Analysiere Bildserie...");
		aktGIL = new GeneralImageIOD[fNameList.length];
		i = 0;
		for (k = 0; k < fNameList.length; k++) {

			pBar.setValue((k * 100) / fNameList.length);

			f = new File(parent + parent.separator + fNameList[k]);
			if (f.isFile()) {
				try {

					// Ueberprueft, ob die Datei eine gueltige DICOM Datei ist
					if (DcmInputStream.isDicomStream(f)) {

						// Datei ist gueltig, in GeneralImageIOD einlesen
						ddo = DcmInputStream.loadDDO(f);
						aktGI = new GeneralImageIOD(ddo, imda.allowACRNema);

						// Ueberprueft , ob das Bild eingelesen werden soll.
						if (isValidImage(refGI, aktGI, imda)) {
							aktGIL[i++] = aktGI;
						} 
					} 
				} catch (Exception err) {}
			} 
		} 

		pBar.setValue(0);
		mainFrame.statusBar.setText("Bereit.");

		// Das Array aktGIL ist moeglicherweise nicht vollstaendig gefuellt.
		GeneralImageIOD[] retGIL = new GeneralImageIOD[i];

		for (k = 0; k < i; k++) {
			retGIL[k] = aktGIL[k];
		} 
		return retGIL;
	} 


	/**
	 * Ueberprueft, ob das aktuelle Bild der Liste eingelesen werden
	 * soll oder nicht.
	 * @param refGI   Die GeneralImageIOD des Referenzbildes.
	 * @param aktGI   Die GeneralImageIOD des aktuellen Bildes.
	 * @param imda    Import-Data-Block der Dialogbox.
	 * @return        true, wenn das Bild eingelesen werden soll.
	 */
	private boolean isValidImage(GeneralImageIOD refGI, GeneralImageIOD aktGI, ImportData imda) {
		if (refGI.patientName.compareTo(aktGI.patientName) == 0) {
			int i = aktGI.imageNumber;

			return imda.multiImageList.contains(new Integer(i));
		} 
		return false;
	} 


	/**
	 * Die Methode laedt aufgrund der Angabe des Dateinamens und der Verzeichnisses
	 * ein DICOM-Bild. Dabei wird zunaechst ein DcmDataObject erzeugt, daraus dann
	 * wiederum ein DcmImage-Objekt und aus diesem dann schliesslich ein ImagePlus-Objekt.
	 * @param strDirectory Das Verzeichnis, in dem das zu ladende Bild steht.
	 * @parm strFile Der Dateiname des zu ladenden Bildes
	 */
	public void loadDcmImage(GeneralImageIOD gi) {
		DcmDataObject ddo = null;
		DcmImage			di = null;
		ImagePlus			ip;
		String[]			sa;
		int						windowCenter;
		int						windowWidth;

		ddo = gi.headerDDO.getCopyOfMe();

		// Dummy 1*1 Pixelmatrix ergaenzen, die spaeter von ImagePlus.setDDO
		// wieder geloescht werden kann.
		ddo.push(new DcmValue(DcmDDE.DD_PixelData, new byte[4]));

		// Erstellen des DcmImage
		try {
			di = new DcmImage(gi);
		} catch (Exception e) {
			e.printStackTrace();
		} 

		// Erzeugen eines ImagePlus-Objektes aus dem DcmImage
		ip = mainFrame.pCanvas.createNewImage(di);

		// Zusaetlich das DcmDataObject zum ImagePlus speichern. damit gehen keine
		// Informationen verloren
		ip.setDDO(ddo);

		// Fensterungswerte aus Bild extrahieren
		// VOI LUT Module PS 3.3-C.11.2:
		// - Center und Window sind optionale Attribute.
		// - sind mehere Paare definiert, wird das erste Paar genommen.
		if (ddo.isAvailable(DcmDDE.DD_WindowCenter) & ddo.hasValue(DcmDDE.DD_WindowCenter)) {
			sa = DcmValue.str2StringArray(ddo.getString(DcmDDE.DD_WindowCenter), "\\");
			windowCenter = (int) DcmValue.str2Long(sa[0], 2048);
			sa = DcmValue.str2StringArray(ddo.getString(DcmDDE.DD_WindowWidth), "\\");
			windowWidth = (int) DcmValue.str2Long(sa[0], 4096);
		} else {
			windowCenter = 2048;
			windowWidth = 4096;
		} 
		mainFrame.pCenter.setValue(windowCenter);
		mainFrame.pWindow.setValue(windowWidth);
	} 

}




/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

