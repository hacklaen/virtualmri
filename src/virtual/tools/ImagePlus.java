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

import java.awt.image.*;
import java.awt.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import javax.swing.*;

import rad.dicom.dcm.*;


/**
 * Die Klasse ImagePlus kapselt ein DcmImage-Objekt, welches ein 12-Bit
 * DICOM-Bild repraesentiert, inklusive des gefensterten AWT-Bildes, des
 * DcmDataObjects, welches alle Bildparameter enthaelt, und einiger anderer
 * Informationen.
 * <DL><DT><B>Modifications: </B><DD>
 * Thomas Hacklaender 2000.8.15:
 * In calcFFT den statischen Methodenaufruf doFFT(Image, boolean) der
 * jigl-Bibliothek durch FFTTools.forwardFFT ersetzt.<br>
 * Thomas Hacklaender 2000.8.13:
 * In der jigl-Bibliothek Version 1.3 und 1.4 ist die Methode
 * RealGrayImage(float[][] dat) fehlerhat implementtiert. Um die Bibliothek
 * trotzdem als Ganzes uebernehmen zu koennen, sind hier und in FFTTools
 * einige Anpassungen notwendig:<br>
 * In der Methode calcFFT den Aufruf RealGrayImage(float[][]) durch
 * FFTTools.makeRealGrayImage(int, int, short[][]) ersetzt.<br>
 * Thomas Hacklaender 2000.4.2:<br>
 * Row und Column Pixelspacing koennen unterschiedlich sein. Es wurden deshalb
 * zwei neue Methoden, getPixelSpacingRow und getPixelSpacingColumn definiert.<br>
 * Thomas Hacklaender 2000.4.3:<br>
 * Umstellung auf die aktuelle Version 2.1 des dcm-Package.
 * Thomas Hacklaender 2002.10.12:<br>
 * Datumsformatierung geaendert, da ab Java 1.1 deprecated
 * (Aenderungen mit th 2002.10.12 markiert).
 * Thomas Hacklaender 2002.10.13:
 * Strings internationalisiert.<br>
 * </DD></DL>
 * @author   Christian Schalla
 * @author   Andreas Truemper
 * @version  1.2, 2002.10.16
 */
public class ImagePlus {


	/**
	 * Das gefensterte AWT-Bild.
	 */
	private Image					img;


	/**
	 * Das DcmImage-Objekt. Es uebernimmt u.a. die Umrechnung des Bildes von
	 * 12 auf 8 Bit.
	 */
	private DcmImage			DcmImg;


	/**
	 * Das DcmDataObject, welches alle Bildparameter enthaelt.
	 */
	private DcmDataObject ddo;


	/**
	 * Der Realteil der Fourier-Transformation des Bildes.
	 */
	private float[][]			fftImageReal = null;


	/**
	 * Der Imaginaerteil der Fourier-Transformation des Bildes.
	 */
	private float[][]			fftImageImag = null;


	/**
	 * Markierung die angibt, ob die zur Verfuegung stehende Fourier-Transformation
	 * das Bildes aktuell ist.
	 */
	private boolean				fftOK = false;


	/**
	 * Ein Feld von Texten, die oben links im Bild eingeblendet werden koennen.
	 */
	private String[]			textNW;


	/**
	 * Ein Feld von Texten, die oben rechts im Bild eingeblendet werden koennen.
	 */
	private String[]			textNE;


	/**
	 * Ein Feld von Texten, die unten links im Bild eingeblendet werden koennen.
	 */
	private String[]			textSW;


	/**
	 * Ein Feld von Texten, die unten rechts im Bild eingeblendet werden koennen.
	 */
	private String[]			textSE;


	/**
	 * Die Zeilen- und Spaltenaufloesung des gespeicherten Bildes.
	 */
	private int						size;


	/**
	 * Standardkonstruktor
	 */
	public ImagePlus() {}


	/**
	 * Dieser Konstruktor erzeugt ein ImagePlus-Objekt aus einem DcmImage-Objekt.
	 * Dabei wird gleichzeitig die Aufloesung des Bildes ermittelt.
	 * @param di Das DcmImage-Objeckt, aus dem das ImagePlus-Objekt erzeugt wird.
	 */
	public ImagePlus(DcmImage di) {
		DcmImg = di;
		size = DcmImg.rows;
	}


	/**
	 * Die Methode liefert ein Feld von Texten, die oben links im Bild eingeblendet
	 * werden koennen. Die Auswahl der Texte, die dort erscheinen, findet an dieser
	 * Stelle statt. Die Klasse myPanel, die die Hauptzeichenflaeche repraesentiert,
	 * stellt diese Texte ohne weitere Beachtung des Inhalts lediglich dar.
	 * @return Die Zeichenketten, die oben links im Bild dargestellt werden sollen.
	 */
	public String[] getTextNW() {
		textNW = new String[6];
		textNW[0] = java.util.ResourceBundle.getBundle("virtual/tools/resources/tools_loc").getString("imageplus.text.nw.sequence") + getSequence();
		if (getTR() != "") {
			textNW[1] = "TR: " + Double.valueOf(getTR());
		} else {
			textNW[1] = "TR: ";
		} 
		if (getTI() != "") {
			textNW[2] = "TI: " + Double.valueOf(getTI());
		} else {
			textNW[2] = "TI: ";
		} 
		if (getTE() != "") {
			textNW[3] = "TE: " + Double.valueOf(getTE());
		} else {
			textNW[3] = "TE: ";
		} 
		if (getFA() != "") {
			textNW[4] = "FA: " + Double.valueOf(getFA());
		} else {
			textNW[4] = "FA: ";
		} 
		textNW[5] = "ETL :" + getETL();
		return textNW;
	} 


	/**
	 * Die Methode liefert ein Feld von Texten, die oben rechts im Bild eingeblendet
	 * werden koennen. Die Auswahl der Texte, die dort erscheinen, findet an dieser
	 * Stelle statt. Die Klasse myPanel, die die Hauptzeichenflaeche repraesentiert,
	 * stellt diese Texte ohne weitere Beachtung des Inhalts lediglich dar.
	 * @return Die Zeichenketten, die oben rechts im Bild dargestellt werden sollen.
	 */
	public String[] getTextNE() {
		textNE = new String[4];

		// th 2002.10.12
		// textNE[0] = getDate().toLocaleString();
		textNE[0] = java.util.ResourceBundle.getBundle("virtual/tools/resources/tools_loc").getString("imageplus.text.ne.examdate");
		textNE[0] += java.text.DateFormat.getDateInstance().format(this.getDate());
		textNE[1] = java.util.ResourceBundle.getBundle("virtual/tools/resources/tools_loc").getString("imageplus.text.ne.name") + getPatientName();

		// th 2002.10.12
		// textNE[2] = "Geb.Dat.: " + getPatientBirthdate().toLocaleString().substring(0, 10);
		textNE[2] = java.util.ResourceBundle.getBundle("virtual/tools/resources/tools_loc").getString("imageplus.text.nw.birthdate");
		textNE[2] += java.text.DateFormat.getDateInstance().format(getPatientBirthdate());
		textNE[3] = java.util.ResourceBundle.getBundle("virtual/tools/resources/tools_loc").getString("imageplus.text.nw.sex") + getPatientSex();
		return textNE;
	} 


	/**
	 * Die Methode liefert ein Feld von Texten, die unten links im Bild eingeblendet
	 * werden koennen. Die Auswahl der Texte, die dort erscheinen, findet an dieser
	 * Stelle statt. Die Klasse myPanel, die die Hauptzeichenflaeche repraesentiert,
	 * stellt diese Texte ohne weitere Beachtung des Inhalts lediglich dar.
	 * @return Die Zeichenketten, die unten links im Bild dargestellt werden sollen.
	 */
	public String[] getTextSW() {
		textSW = new String[3];
		textSW[2] = "SP: " + getSliceLocation();
		textSW[1] = "C: " + getCenter();
		textSW[0] = "W: " + getWindow();
		return textSW;
	} 


	/**
	 * Die Methode liefert ein Feld von Texten, die unten rechts im Bild eingeblendet
	 * werden koennen. Die Auswahl der Texte, die dort erscheinen, findet an dieser
	 * Stelle statt. Die Klasse myPanel, die die Hauptzeichenflaeche repraesentiert,
	 * stellt diese Texte ohne weitere Beachtung des Inhalts lediglich dar.
	 * @return Die Zeichenketten, die unten rechts im Bild dargestellt werden sollen.
	 */
	public String[] getTextSE() {
		textSE = null;
		return textSE;
	} 


	/**
	 * Die Methode liefert den Namen der Pulssequenz zurueck, mit der das Bild
	 * aufgenommen wurde.
	 * @return Der Name der Pulssequenz.
	 */
	public String getSequence() {
		return ddo.getString(DcmDDE.DD_SequenceName);
	} 


	/**
	 * Die Methode liefert die Repititionszeit zurueck, mit der das Bild
	 * aufgenommen wurde.
	 * @return Die Repititionszeit als Zeichenkette.
	 */
	public String getTR() {
		return ddo.getString(DcmDDE.DD_RepetitionTime);
	} 


	/**
	 * Die Methode liefert die Inversionszeit zurueck, mit der das Bild
	 * aufgenommen wurde. Wurde das Bild mit einer Sequenz aufgenommen, die diesen
	 * Parameter nicht beinhaltet, ist der Rueckgabewert leer.
	 * @return Die Inversionszeit als Zeichenkette oder eine leere Zeichenkette.
	 */
	public String getTI() {
		return ddo.getString(DcmDDE.DD_InversionTime);
	} 


	/**
	 * Die Methode liefert die Echozeit zurueck, mit der das Bild
	 * aufgenommen wurde. Wurde das Bild mit einer Sequenz aufgenommen, die diesen
	 * Parameter nicht beinhaltet, ist der Rueckgabewert leer.
	 * @return Die Echozeit als Zeichenkette oder eine leere Zeichenkette.
	 */
	public String getTE() {
		return ddo.getString(DcmDDE.DD_EchoTime);
	} 


	/**
	 * Die Methode liefert die Anzahl der Echos pro Repititionszyklus (Echo Train Length)
	 * zurueck, mit der das Bild aufgenommen wurde. Wurde das Bild mit einer Sequenz
	 * aufgenommen, die diesen Parameter nicht beinhaltet, ist der Rueckgabewert leer.
	 * @return Die Anzahl der Echos pro Repititionszyklus als Zeichenkette oder
	 * eine leere Zeichenkette.
	 */
	public String getETL() {
		return ddo.getString(DcmDDE.DD_EchoTrainLength);
	} 


	/**
	 * Die Methode liefert die Position des Bildes (der Schicht) im Gesamtdatensatz
	 * zurueck. Die Position haengt von den Einstellungen des Koordinatensystems
	 * bei der Aufnahme ab.
	 * @return Die Schichtposition als Zeichenkette.
	 */
	public String getSliceLocation() {
		return ddo.getString(DcmDDE.DD_SliceLocation);
	} 


	/**
	 * Die Methode liefert den Flipwinkel zurueck, mit dem das Bild
	 * aufgenommen wurde. Wurde das Bild mit einer Sequenz aufgenommen, die diesen
	 * Parameter nicht beinhaltet, ist der Rueckgabewert leer.
	 * @return Der Flipwinkel als Zeichenkette oder eine leere Zeichenkette.
	 */
	public String getFA() {
		return ddo.getString(DcmDDE.DD_FlipAngle);
	} 


	/**
	 * Die Methode liest den Namen des Patienten, der auf dem Bild
	 * dargestellt ist, aus dem DcmDataObject aus und liefert ihn zurueck.
	 * @return Der Patientenname als Zeichenkette.
	 */
	public String getPatientName() {
		return ddo.getString(DcmDDE.DD_PatientsName);
	} 


	/**
	 * Die Methode liest das Geburtsdateum des Patienten, der auf dem Bild
	 * dargestellt ist, aus dem DcmDataObject aus und liefert es zurueck.
	 * @return Das Geburtsdatum des Patienten als Datums-Objekt.
	 */
	public Date getPatientBirthdate() {
		Date	dat = new Date();

		if (ddo != null) {
			String	date = ddo.getString(DcmDDE.DD_PatientsBirthDate);

			// th 2002.10.12
			// dat = new Date(Integer.parseInt(date.substring(2, 4)),
			// Integer.parseInt(date.substring(4, 6)) - 1,
			// Integer.parseInt(date.substring(6)));
			java.util.Calendar.getInstance().set(Integer.parseInt(date.substring(0, 4)) - 1900, Integer.parseInt(date.substring(4, 6)) - 1, Integer.parseInt(date.substring(6)));
		} 

		return dat;
	} 


	/**
	 * Die Methode liest das Geschlecht des Patienten, der auf dem Bild
	 * dargestellt ist, aus dem DcmDataObject aus und liefert es zurueck.
	 * @return Das Geschlecht des Patienten als Zeichenkette.
	 */
	public String getPatientSex() {
		return ddo.getString(DcmDDE.DD_PatientsSex);
	} 


	/**
	 * Die Methode liest den Pixelabstand des Bildes aus dem DcmDataObject aus
	 * und liefert ihn zurueck. Dieser wird benoetigt, um Distanzmessungen durchfuehren
	 * zu koennen.
	 * !! Row und Column Pixelspacing koennen unterschiedlich sein.
	 * !! Es wurden deshlab zwei zusaetzliche Methoden, getPixelSpacingRow
	 * !! getPixelSpacingColumn implementiert.
	 * @return Der Pixelabstand.
	 */
	public double getPixelSpacing() {
		return getPixelSpacingRow();
	} 


	/**
	 * Die Methode liest den Pixelabstand des Bildes in Zeilenrichtung aus
	 * dem DcmDataObject aus und liefert ihn zurueck.
	 * @return Der Pixelabstand.
	 */
	public double getPixelSpacingRow() {

		// Image Plane Module PS 3.3-C.7.6.2:
		String[]	sa = DcmValue.str2StringArray(ddo.getString(DcmDDE.DD_PixelSpacing), "\\");
		double		pixelSpacingRow = 0.0;
		double		pixelSpacingColumn = 0.0;

		if (sa.length > 0) {
			pixelSpacingRow = DcmValue.str2Double(sa[0], 0.0);
		} 
		if (sa.length > 1) {
			pixelSpacingColumn = DcmValue.str2Double(sa[1], 0.0);
		} 
		return pixelSpacingRow;
	} 


	/**
	 * Die Methode liest den Pixelabstand des Bildes in Zeilenrichtung aus
	 * dem DcmDataObject aus und liefert ihn zurueck.
	 * @return Der Pixelabstand.
	 */
	public double getPixelSpacingColumn() {

		// Image Plane Module PS 3.3-C.7.6.2:
		String[]	sa = DcmValue.str2StringArray(ddo.getString(DcmDDE.DD_PixelSpacing), "\\");
		double		pixelSpacingRow = 0.0;
		double		pixelSpacingColumn = 0.0;

		if (sa.length > 0) {
			pixelSpacingRow = DcmValue.str2Double(sa[0], 0.0);
		} 
		if (sa.length > 1) {
			pixelSpacingColumn = DcmValue.str2Double(sa[1], 0.0);
		} 
		return pixelSpacingColumn;
	} 


	/**
	 * Die Methode liest das Aufnahmedatum und die Aufnahmezeit des Bildes aus dem
	 * DcmDataObject aus und liefert es zurueck.
	 * @return Das Aufnahmedatum und die Aufnahmezeit des Bildes als Datums-Objekt.
	 */
	public Date getDate() {
		Date	dat = new Date();

		if (ddo != null) {
			String	imgdate = ddo.getString(DcmDDE.DD_ImageDate);
			String	imgtime = ddo.getString(DcmDDE.DD_ImageTime);

			// th 2002.10.12
			// dat = new Date(Integer.parseInt(imgdate.substring(2, 4)),
			// Integer.parseInt(imgdate.substring(4, 6)) - 1,
			// Integer.parseInt(imgdate.substring(6)),
			// Integer.parseInt(imgtime.substring(0, 2)),
			// Integer.parseInt(imgtime.substring(2, 4)),
			// Integer.parseInt(imgtime.substring(4, 6)));
			java.util.Calendar.getInstance().set(Integer.parseInt(imgdate.substring(0, 4)) - 1900, Integer.parseInt(imgdate.substring(4, 6)) - 1, Integer.parseInt(imgdate.substring(6)), Integer.parseInt(imgtime.substring(0, 2)), Integer.parseInt(imgtime.substring(2, 4)), Integer.parseInt(imgtime.substring(4, 6)));
		} 

		return dat;
	} 


	/**
	 * Die Methode liest das Zentrum der Fensterung aus dem DcmDataObject aus und
	 * liefert es zurueck.
	 * @return Das Zentrum der Fensterung als Ganzzahlenwert.
	 */
	public int getCenter() {
		return DcmImg.getCenter();
	} 


	/**
	 * Die Methode liest die Breite des Fensterungsbereichs aus dem DcmDataObject
	 * aus und liefert sie zurueck.
	 * @return Die Breite des Fensterungsbereichs als Ganzzahlenwert.
	 */
	public int getWindow() {
		return DcmImg.getWidth();
	} 


	/**
	 * Die Methode setzt den Namen der Aufnahmesequenz im DcmDataObject.
	 * @param s Der Name der Pulssequenz, mit der das Bild aufgenommen wurde.
	 */
	public void setSequence(String s) {
		ddo.setString(DcmDDE.DD_SequenceName, s);
	} 


	/**
	 * Die Methode setzt die Repititionszeit im DcmDataObject.
	 * @param tr Die Repititionszeit [ms], mit der das Bild aufgenommen wurde.
	 */
	public void setTR(double tr) {
		ddo.setString(DcmDDE.DD_RepetitionTime, (new Double(tr)).toString());
	} 


	/**
	 * Die Methode setzt die Inversionszeit im DcmDataObject.
	 * @param ti Die Inversionszeit [ms], mit der das Bild aufgenommen wurde.
	 */
	public void setTI(double ti) {
		ddo.setString(DcmDDE.DD_InversionTime, (new Double(ti)).toString());
	} 


	/**
	 * Die Methode setzt die Echozeit im DcmDataObject.
	 * @param te Die Echozeit [ms], mit der das Bild aufgenommen wurde.
	 */
	public void setTE(double te) {
		ddo.setString(DcmDDE.DD_EchoTime, (new Double(te)).toString());
	} 


	/**
	 * Die Methode setzt die Anzahl der Echos pro Repititionszyklus (Echo Train Length)
	 * im DcmDataObject.
	 * @param etl Die Anzahl der Echos pro Repititionszyklus, mit der das Bild
	 * aufgenommen wurde.
	 */
	public void setETL(int etl) {
		ddo.setString(DcmDDE.DD_EchoTrainLength, (new Integer(etl)).toString());
	} 


	/**
	 * Die Methode setzt den zeitlichen Abstand der Echos im DcmDataObject.
	 * @param esp Der zeitliche Abstand [ms] der Echos.
	 */
	public void setESP(double esp) {

		// ddo.setString(, (new Double(esp)).toString());
	} 


	/**
	 * Die Methodes setzt die Schichtposition im DcmDataObject. Die Position haengt
	 * von der Wahl des Koordinatensystems bei der Aufnahme ab.
	 * @param sl Die Schichtposition des aktuellen Bildes.
	 */
	public void setSliceLocation(double sl) {
		ddo.setString(DcmDDE.DD_SliceLocation, (new Double(sl)).toString());
	} 


	/**
	 * Die Methode setzt den Flipwinkel im DcmDataObject.
	 * @param fa Der Flipwinkel, mit dem das Bild aufgenommen wurde.
	 */
	public void setFA(double fa) {
		ddo.setString(DcmDDE.DD_FlipAngle, (new Double(fa)).toString());
	} 


	/**
	 * Die Methode setzt den Patiemtennamen im DcmDataObject.
	 * @param name Der Name des Patienten, der auf dem Bild dargestellt ist.
	 */
	public void setPatientName(String name) {
		ddo.setString(DcmDDE.DD_PatientsName, name);
	} 


	/**
	 * Die Methode setzt das Geschlecht des Patienten im DcmDataObject.
	 * @param s Das Geschlecht des Patienten, der auf dem Bild dargestellt ist.
	 */
	public void setPatientSex(String s) {
		ddo.setString(DcmDDE.DD_PatientsSex, s);
	} 


	/**
	 * Die Methode fuegt dem ImagPlus-Objekt ein DcmDataObject hinzu. Darin sind
	 * alle Bild- und Patienteninformationen sowie weitere Informationen enthalten.
	 * Das DcmDataObject wird benoetigt, um beim Abspeichern des Bildes all diese
	 * Informationen ebenfalls abspeichern zu koennen. Wuerde man sich das Objekt
	 * nicht merken, wuerden zwischen Speichern und Laden eines Bildes alle darin
	 * enthaltenen Informationen verloren gehen.
	 * Da das DcmDataObject urspruenglich auch die Pixeldaten enthaelt, diese jedoch
	 * schon im DcmImage gespeichert werden, werden die Pixeldaten aus dem
	 * DcmDataObject geloescht.
	 * @param d Das DcmDataObject, das alle wichtigen Bildinformationen enthaelt.
	 */
	public void setDDO(DcmDataObject d) {

		// Zunaecht loeschen der Pixeldaten aus dem DDO, da diese schon im
		// DcmImage gespeichert werden
		d.removeObject(DcmDDE.DD_PixelData);

		// Dann merken der Referenz auf ds DDO
		ddo = d;
	} 


	/**
	 * Die Methode liefert das DcmDataObject mit allen Bildinformationen zurueck.
	 * @return Das DcmDataObject zum aktuellen Bild.
	 */
	public DcmDataObject getDDO() {
		return ddo;
	} 


	/**
	 * Die Methode liefert das gefensterte Bild als Java-AWT-Bild zurueck.
	 * @return Das gefensterte Java-Bild.
	 */
	public Image getAWTImage() {
		if (img == null) {

			// Erzeugen eines AWT-Images aus der ImageSource im DcmImage
			JFrame	dummy = new JFrame();

			img = dummy.getToolkit().getDefaultToolkit().createImage(DcmImg.imgSource);
		} 
		return img;
	} 


	/**
	 * Die Methode liefert den Grauwert des 12-Bit Bildes an der angegebenen
	 * Position zurueck.
	 * @param x x-Koordinate des zu ermittelnden Grauwerts.
	 * @param y y-Koordinate des zu ermittelnden Grauwerts.
	 * @return Der Grauwert an der Position (x,y).
	 */
	public int getGrayValue(int x, int y) {
		int gw = DcmImg.pixel16[size * y + x];

		return gw;
	} 


	/**
	 * Die Methode liefert das gefensterte AWT-Bild als zweidimensionales Feld
	 * von 16-Bit-Zahlen zurueck. Benoetigt wird diese Methode zur Berechnung eines
	 * Histogramms.
	 * return Das gefensterte AWT-Bild als zweidimensionales Feld von 16-Bit-Zahlen.
	 */
	public short[][] get8BitImageShort() {

		// Erzeugen eines Arrays von Integer-Werten, in das die Pixel hineinkopiert
		// werden solle. Das ist noch nicht das zurueckgegebene Objekt.
		int						pixels[] = new int[size * size];

		// Erzeugen eines PixelGrabbers. Dieser liest die Puixel aus einem Image ein
		// und schreibt sie in das uebergebene Array von Ganzzahlen
		PixelGrabber	grabber = new PixelGrabber(img, 0, 0, size, size, pixels, 0, size);

		try {
			grabber.grabPixels();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 

		short[][] shortImage = new short[size][size];

		// Umkopieren der Pixel aus dem eindimensionalen Feld in das zweidimensionale.
		// Dabei umwandlung in short.
		for (int y = 0; y < size; y++) {
			for (int x = 0; x < size; x++) {
				shortImage[x][y] = (short) ((pixels[y * size + x]) & 0xff);
			} 
		} 

		return shortImage;
	} 


	/**
	 * Die Methode liefert den angegebenen  Teil des gefensterten (8-Bit) Bildes
	 * zurueck. Das Ergebnis wird benoetigt, um einen Ausschnitt des Bildes
	 * vergroessern zu koennen (Lupe).
	 * @param x Die linke x-Koordinate des Ausschnitts.
	 * @param y Die obere y-Koordinate des Ausschnitts.
	 * @param w Die Breite des Ausschnitts.
	 * @param h Die Hoehe des Ausschnitts.
	 */
	public Image getImagePart(int x, int y, int w, int h) {

		// Erzeugen eines Arrays von Integer-Werten, in das die Pixel hineinkopiert
		// werden solle. Das ist noch nicht das zurueckgegebene Objekt.
		int						pixels[] = new int[w * h];

		// Erzeugen eines PixelGrabbers. Dieser liest die Pixel aus einem Image ein
		// und schreibt sie in das uebergebene Array von Ganzzahlen
		PixelGrabber	grabber = new PixelGrabber(img, x, y, w, h, pixels, 0, w);

		try {
			grabber.grabPixels();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 

		// Erzeugen eines Image-Objekts aus dem Array
		Frame dummy = new Frame();
		Image imagePart = dummy.getToolkit().getDefaultToolkit().createImage(new MemoryImageSource(w, h, pixels, 0, w));

		return imagePart;
	} 


	/**
	 * Die Methode spiegelt das 12-Bit Bild. Da dabei keine Bildinformationen
	 * verloren gehen, wird die Operation direkt auf dem 12-Bit-Bild im
	 * DcmImage-Objekt durchgefuehrt. Nach dem Spiegeln wird auch das 8-Bit
	 * Java-AWT-Bild neu berechnet.
	 */
	public void mirrorHor() {

		// Anlegen einer temporaeren Kopie fuer das gespiegelte Bild
		short[] mirrorImage = new short[size * size];

		// Spiegeln an der y-Achse
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				mirrorImage[(size * y) + x] = DcmImg.pixel16[(size * y) + (size - 1 - x)];
			} 
		} 
		DcmImg.pixel16 = mirrorImage;

		// 8-Bit-Bild aktualisieren
		updateAWTImage();
		fftOK = false;
	} 


	/**
	 * Die Methode berechnet das 8-Bit Java-AWT-Bild auf Grundlage des 12-Bit Bildes
	 * (im DcmImage-Objekt) und den aktuellen Fensterungseinstellungen neu.
	 */
	public void updateAWTImage() {
		DcmImg.updateImageSource();
		Frame dummy = new Frame();

		img = dummy.getToolkit().getDefaultToolkit().createImage(DcmImg.imgSource);
	} 


	/**
	 * Die Methode rotiert das 12-Bit Bild um 90 Grad im Uhrzeigersinn.
	 * Da dabei keine Bildinformationen verloren gehen, wird die Operation direkt
	 * auf dem 12-Bit-Bild im DcmImage-Objekt durchgefuehrt. Nach dem Rotieren
	 * wird auch das 8-Bit Java-AWT-Bild neu berechnet.
	 */
	public void rotate90CW() {

		// Anlegen einer temporaeren Kopie fuer das rotierte Bild
		short[] rotateImage = new short[DcmImg.pixel16.length];

		// Rotieren
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				rotateImage[(size * y) + x] = DcmImg.pixel16[(size * (size - 1 - x)) + y];
			} 
		} 
		DcmImg.pixel16 = rotateImage;

		// 8-Bit-Bild aktualisieren
		updateAWTImage();
		fftOK = false;
	} 


	/**
	 * Die Methode liefert die Zeilen- bzw. Spaltenaufloesung des Bildes zurueck.
	 * @return Die Aufloesung des Bildes.
	 */
	public int getSize() {
		return size;
	} 


	/**
	 * Die Methode invertiert das 12-Bit Bild. Da dabei keine Bildinformationen
	 * verloren gehen, wird die Operation direkt auf dem 12-Bit-Bild im
	 * DcmImage-Objekt durchgefuehrt. Nach dem Invertieren wird auch das 8-Bit
	 * Java-AWT-Bild neu berechnet.
	 */
	public void invert() {

		// Invertieren
		for (int i = 0; i < size * size; i++) {
			DcmImg.pixel16[i] = (short) (4095 - DcmImg.pixel16[i]);
		} 

		// 8-Bit-Bild aktualisieren
		updateAWTImage();
		fftOK = false;
	}		// Methode invert
 

	/**
	 * Die Methode liefert das 12-Bit-Bild als Feld von 16-Bit-Zahlen zurueck.
	 * @return Das Bild als Feld von 16-Bit-Zahlen.
	 */
	public short[] getImage12BitShort() {
		return DcmImg.pixel16;
	} 


	/**
	 * Die Methode setzt das Datum des Aufrufs dieser Methode im DcmDataObject.
	 */
	public void setCurrentDate() {
		SimpleDateFormat	fd = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat	ft = new SimpleDateFormat("HHmmss.SSS");
		SimpleDateFormat	fs = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		Date							cdt = new Date();

		String						dateAndTimeSuffix = fs.format(cdt);

		String						studyDate = fd.format(cdt);
		String						studyTime = ft.format(cdt);
		String						dateOfSecondaryCapture = fd.format(cdt);
		String						timeOfSecondaryCapture = ft.format(cdt);
		String						imageDate = fd.format(cdt);
		String						imageTime = ft.format(cdt);

		// Setzen der Datums- und Zeitinformationen im DDO
		ddo.setString(DcmDDE.DD_ImageDate, imageDate);
		ddo.setString(DcmDDE.DD_ImageTime, imageTime);
		ddo.setString(DcmDDE.DD_StudyDate, studyDate);
		ddo.setString(DcmDDE.DD_StudyTime, studyDate);
	} 


	/**
	 * Die Methode aktualiert die Einstellungen fuer die Fensterung im DcmDataObject.
	 * Nach der Aktualisierung wird auch das 8-Bit-Bild neu berechnet.
	 * @param c Das Zentrum des darzustellenden Fensters.
	 * @param w Die Breite des darzustellenden Fensters.
	 */
	public void setCW(int c, int w) {

		// Zunaechst Fensterung im DDO setzen. Dadurch wird sie mit abgespeichert
		ddo.setString(DcmDDE.DD_WindowCenter, Integer.toString(c));
		ddo.setString(DcmDDE.DD_WindowWidth, Integer.toString(w));

		// Fensterung im DcmImage setzen und dann neue MemoryImageSource berechnen
		DcmImg.setCW(c, w);

		// tha:2000.07.17 // System.out.println("c: " + c + " w: " + w);
		updateAWTImage();
	} 


	/**
	 * Die Methode aktualiert die Einstellungen fuer die Fensterung im DcmDataObject.
	 * Nach der Aktualisierung wird auch das 8-Bit-Bild neu berechnet.
	 * @param c Das Zentrum des darzustellenden Fensters.
	 */
	public void setCenter(int c) {

		// Zunaechst Fensterung im DDO setzen. Dadurch wird sie mit abgespeichert
		ddo.setString(DcmDDE.DD_WindowCenter, Integer.toString(c));

		// Fensterung im DcmImage setzen und dann neue MemoryImageSource berechnen
		DcmImg.setCenter(c);
		updateAWTImage();
	} 


	/**
	 * Die Methode aktualiert die Einstellungen fuer die Fensterung im DcmDataObject.
	 * Nach der Aktualisierung wird auch das 8-Bit-Bild neu berechnet.
	 * @param w Die Breite des darzustellenden Fensters.
	 */
	public void setWindow(int w) {

		// Zunaechst Fensterung im DDO setzen. Dadurch wird sie mit abgespeichert
		ddo.setString(DcmDDE.DD_WindowWidth, Integer.toString(w));

		// Fensterung im DcmImage setzen und dann neue MemoryImageSource berechnen
		DcmImg.setWindow(w);
		updateAWTImage();
	} 



	/**
	 * Die Methode liefert den minimalen Grauwert des 12-Bit-Bildes zurueck.
	 * @return Der minimale Grauwert im 12-Bit Bild.
	 */
	public int getMinGrayValue12Bit() {
		int ming = 999999;

		for (int x = 0; x < DcmImg.pixel16.length; x++) {
			if (DcmImg.pixel16[x] < ming) {
				ming = DcmImg.pixel16[x];
			} 
		} 
		return ming;
	} 


	/**
	 * Die Methode liefert den maximalen Grauwert des 12-Bit-Bildes zurueck.
	 * @return Der maximale Grauwert im 12-Bit Bild.
	 */
	public int getMaxGrayValue12Bit() {
		int maxg = 0;

		for (int x = 0; x < DcmImg.pixel16.length; x++) {
			if (DcmImg.pixel16[x] > maxg) {
				maxg = DcmImg.pixel16[x];
			} 
		} 
		return maxg;
	} 


	/**
	 * Die Methode fenstert das aktuelle Bild optimal, d.h. es werden alle
	 * Grauwerte, die groesser sind als Null, auf einen Bereich von 256 Grauwerten
	 * abgebildet.
	 */
	public void optimalWindowing() {

		// tha:2000.07.17 // System.out.println("optimalWindowing");
		int min = getMinGrayValue12Bit();
		int max = getMaxGrayValue12Bit();

		// tha:2000.07.17 // System.out.println("min: " + min + " max: " + max);
		int width = max - min;
		int center = min + (width / 2);

		// tha:2000.07.17 // System.out.println("center: " + center + " width: " + width);
		setCW(center, width);
	} 


	/**
	 * Die Methode berechnet die Fast-Fourier-Transformation des 12-Bit-Bildes.
	 * Real- und Imaginarteil werden in Instanzvariablen abgespeichert und stehen
	 * damit in Zukunft zur Verfuegung, ohne neu berechnet werden zu muessen.
	 * Daneben wird allerdings noch eine Markierung fuer die Aktualitaet der
	 * Fourier-Transformation mitgefuehrt. Eine Spiegelung oder Invertierung des
	 * Bildes macht diese naemlich ungueltig. Da die Berechnung der FFT jedoch
	 * einige Zeit benoetigt, ist es nicht sinnvoll, diese staendig neu
	 * zu berechnen, obwohl sie garnicht benoetigt wird.
	 */
	private void calcFFT() {

		/*
		 * Ersetzt: tha 2000.8.13
		 * // Konvertieren des 12-Bit Bildes nach float
		 * float[][] myFloatImage = new float[size][size];
		 * for (int x = 0; x < size; x++) {
		 * for (int y = 0; y < size; y++) {
		 * myFloatImage[x][y] = (float) (DcmImg.pixel16[(size * y) + x]);
		 * }
		 * }
		 * // Erzeugen eines REalGrayImage-Objektes, mit dem die FFT durchgefuehrt wird
		 * jigl.image.RealGrayImage	rgImg = new jigl.image.RealGrayImage(myFloatImage);
		 */
		jigl.image.RealGrayImage	rgImg = FFTTools.makeRealGrayImage(size, size, DcmImg.pixel16);

		// Durchfuehren der FFT
		jigl.image.ComplexImage		cImg = FFTTools.forwardFFT(rgImg);

		// Extrahieren des Real- und Imaginaerteils aus dem komplexen Bild
		fftImageReal = new float[size][size];
		fftImageImag = new float[size][size];
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				fftImageReal[x][y] = cImg.getReal(x, y);
				fftImageImag[x][y] = cImg.getImag(x, y);
			}		// for
 		 }		// for
 
		// Markieren der FFT als aktuell
		fftOK = true;
	}		// Methode calcFFT
 

	/**
	 * Die Methode liefert den Realteil der Fourier-Transformation des 12-Bit Bildes
	 * zurueck.
	 * @return Der Realteil der Fourier-Transformation.
	 */
	public float[][] getFFTImageReal() {
		if ((fftImageReal == null) || (fftOK == false)) {
			calcFFT();
		} 
		return fftImageReal;
	} 


	/**
	 * Die Methode liefert den Imaginaerteil der Fourier-Transformation des 12-Bit Bildes
	 * zurueck.
	 * @return Der Imaginaerteil der Fourier-Transformation.
	 */
	public float[][] getFFTImageImag() {
		if ((fftImageImag == null) || (fftOK == false)) {
			calcFFT();
		} 
		return fftImageImag;
	} 

}






/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

