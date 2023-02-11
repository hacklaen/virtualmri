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
package virtual.mrt.artefacts;

import virtual.mrt.sequences.*;
import virtual.tools.*;


/**
 * Diese Klasse ist die Berechnungsklasse zur Simualation eines
 * periodischen Bewegungs-Artefakts. Es kann eine x- und y-Amplidude, eine
 * Frequenz und ein start- und Stopzeitpunkt der Bewegung angegeben werden.
 * <DL><DT><B>Modifications: </B><DD>
 * Thomas Hacklaender 2000.8.15:
 * In motion_Std die statischen Methodenaufrufe doFFT(Image, boolean) der
 * jigl-Bibliothek für forward und reverse Transformation durch forwarfFFT und
 * reverseFFT in FFTTools ersetzt.<br>
 * Thomas Hacklaender 2000.8.14:
 * Debug-Output in motion_Std auskommentiert.<br>
 * Thomas Hacklaender 2000.4.3:
 * In der Methode calculate alle aktuell definierten Sequenzen eingefuegt.<br>
 * Thomas Hacklaender 2002.10.13:
 * Strings internationalisiert.<br>
 * </DD></DL>
 * @author   Christian Schalla
 * @author   Andreas Truemper
 * @version  1.2, 2002.10.13
 */
public class PeriodicMotion extends Artefact {


	/**
	 * x-Amplitude der periodischen Bewegung.
	 */
	private int				xAmp;


	/**
	 * y-Amplitude der periodischen Bewegung.
	 */
	private int				yAmp;


	/**
	 * Frequenz der periodischen Bewegung.
	 */
	private int				freq;


	/**
	 * Startzeitpunkt der periodischen Bewegung.
	 */
	private int				startTime;


	/**
	 * Stopzeitpunkt der periodischen Bewegung.
	 */
	private int				stopTime;


	/**
	 * Eingestellte Repititionszeit der aufrufenden Pulssequenz.
	 */
	private int				trTime;


	/**
	 * Konstante fuer die Genauigkeit der Berechnung. Sie muss eine Zweierpotenz
	 * im Interval [1;256] sein.
	 */
	private final int NUMSTEPS = 16;


	/**
	 * Standardkonstruktor.
	 */
	public PeriodicMotion() {}	// Standardkonstruktor


	/**
	 * Diese Methode wird von der aufrufenden Pulssequenz zuerst aufgerufen.
	 * Der uebergebene Methodenname gibt an, welche Methode die korrekte
	 * Berechnungsvorschrift enthaelt. Vor dem aufruf dieser Methode werden
	 * noch die benoetigten Parameter aus der aufrufenden Pulssequenz ausgelesen.
	 * @param method Der Name der Methode, die die korrekte Berechnungsvorschrift enthaelt.
	 */
	public int[][] calculate(String method) {

		// Bestimmen des Namens des aufrufenden Pulssequenz
		String	seqName = sequence.getClass().getName();

		seqName = seqName.substring(seqName.lastIndexOf('.') + 1);


		// Casten der Sequenz und Auslesen der benoetigten Parameter

		if (seqName.compareTo("Flash") == 0) {
			Flash flSequence = (Flash) sequence;

			trTime = flSequence.getTRTime();

		} else if (seqName.compareTo("GERefocused") == 0) {
			GERefocused gereSequence = (GERefocused) sequence;

			trTime = gereSequence.getTRTime();

		} else if (seqName.compareTo("GESpoiled") == 0) {
			GESpoiled gespSequence = (GESpoiled) sequence;

			trTime = gespSequence.getTRTime();

		} else if (seqName.compareTo("InversionRecovery") == 0) {
			InversionRecovery irSequence = (InversionRecovery) sequence;

			trTime = irSequence.getTRTime();

		} else if (seqName.compareTo("SaturationRecovery") == 0) {
			SaturationRecovery	srSequence = (SaturationRecovery) sequence;

			trTime = srSequence.getTRTime();

		} else if (seqName.compareTo("SpinEcho") == 0) {
			SpinEcho	seSequence = (SpinEcho) sequence;

			trTime = seSequence.getTRTime();

		} else if (seqName.compareTo("TurboSpinEcho") == 0) {
			TurboSpinEcho tseSequence = (TurboSpinEcho) sequence;

			trTime = tseSequence.getTRTime();

		} else {

			// Nicht definierte Sequenz
			System.out.println(java.util.ResourceBundle.getBundle("virtual/mrt/artefacts/resources/artefacts_loc").getString("periodicmotion.warning.sequence.notsupported"));
		} 

		// Initialisieren des Rueckgabebildes
		int[][] result = null;

		// Entscheiden, welche Methode aufgerufen werden muss.
		if (method.compareTo("Motion_Std") == 0) {
			result = motion_Std();
		} else if (method.compareTo("Motion_TSE") == 0) {
			System.out.println(java.util.ResourceBundle.getBundle("virtual/mrt/artefacts/resources/artefacts_loc").getString("periodicmotion.warning.tse.notimplemented"));
		} 

		return result;
	}		// Methode calculate
 

	/**
	 * Die Methode enthaelt die Standard-Berechnungsvorschrift zur Simulation
	 * einer periodischen Bewegung.
	 */
	public int[][] motion_Std() {

		// Initialisieren des Ergebnisbildes
		int[][]									res = new int[256][256];

		// Initialisieren der Werte fuer die Verschiebung
		int											dx = 0;
		int											dy = 0;

		// Anlegen eines komplexen Bildes fuer die FFT
		jigl.image.ComplexImage kSpace = new jigl.image.ComplexImage(256, 256);

		// Bewegung im k-Raum simulieren
		for (int i = 0; i < NUMSTEPS; i++) {

			// Fortschrittsanzeige setzen
			progressBar.setValue(i * (256 / NUMSTEPS));

			// 10 ms warten, damit andere Aktionen durchgefuehrt werden koennen
			try {
				Thread.sleep(10);
			} catch (Exception e) {}

			// Berechnen der Position des Bildes zum aktuellen Zeitpunkt
			double	t = i * (256 / NUMSTEPS) * trTime / 1000;		// Zeit in sec

			// Auskommentiert: tha 2000.8.14
			// System.out.println("t: " + t + " start: " + startTime + " stop: " + stopTime);
			if (t >= stopTime) {
				t = stopTime;
			} 
			if (t >= startTime) {
				int relTime = (int) (t - startTime);
				int absTime = (int) (60 / freq);
				int absTimeDiv2 = (int) (60 / freq / 2);

				if ((relTime % absTime) < absTimeDiv2) {

					// zunehmende Periode
					dx = (relTime % absTime) * xAmp / absTimeDiv2;
					dy = (relTime % absTime) * yAmp / absTimeDiv2;
				} else {

					// abnehmende periode
					dx = ((relTime % absTime) - absTimeDiv2) * xAmp / absTimeDiv2;
					dy = ((relTime % absTime) - absTimeDiv2) * yAmp / absTimeDiv2;
				} 
			} 

			// Verschieben des Bildes
			int[][]										movedImage = move(dx, dy);

			// Aus dem verschobenen Bild ein RealGrayImage machen, damit die FFT
			// darauf angewendet werden kann
			jigl.image.RealGrayImage	inComImg = new jigl.image.RealGrayImage(256, 256);

			for (int x = 0; x < 256; x++) {
				for (int y = 0; y < 256; y++) {
					inComImg.set(x, y, movedImage[x][y]);
				} 
			} 

			// Durchfuehren der FFT auf dem verschobenen Bild

			jigl.image.ComplexImage transComImg = FFTTools.forwardFFT(inComImg);

			// Kopieren einiger Zeilen in den endgültigen k-Raum
			for (int line = i * (256 / NUMSTEPS); line < (256 / NUMSTEPS) * (i + 1); line++) {
				for (int x = 0; x < 256; x++) {
					kSpace.setImag(x, line, transComImg.getImag(x, line));
					kSpace.setReal(x, line, transComImg.getReal(x, line));
				} 
			} 
		} 

		// Ruecktransformation durchfuehren
		jigl.image.ComplexImage invImg = null;

		invImg = FFTTools.reverseFFT(kSpace);

		// Magnitudenbild berechnen
		for (int x = 0; x < 256; x++) {
			for (int y = 0; y < 256; y++) {
				res[x][y] = (int) Math.sqrt(Math.pow(invImg.getImag(x, y), 2) + Math.pow(invImg.getReal(x, y), 2));
			} 
		} 

		// Ruecktransformiertes Ergebnisbild zurueckliefern
		return res;
	}		// Methode motion_Std
 

	/**
	 * Die Methode dient zum Setzen der x-Amplitude nach dem
	 * Auslesen aus dem entsprechenden Bedienelement.
	 * @param x x-Amplitude der Bewegung.
	 */
	public void setXAmplitude(int x) {
		xAmp = x;
	} 


	/**
	 * Die Methode dient zum Setzen der y-Amplitude nach dem
	 * Auslesen aus dem entsprechenden Bedienelement.
	 * @param y y-Amplitude der Bewegung.
	 */
	public void setYAmplitude(int y) {
		yAmp = y;
	} 


	/**
	 * Die Methode dient zum Setzen der Bewgungsfrequenz nach dem
	 * Auslesen aus dem entsprechenden Bedienelement.
	 * @param x x-Amplitude der Bewegung.
	 */
	public void setFrequency(int f) {
		freq = f;
	} 


	/**
	 * Die Methode dient zum Setzen des Startzeitpunkts der Translation nach dem
	 * Auslesen aus dem entsprechenden Bedienelement.
	 * @param s Startzeitpunkt der Translation.
	 */
	public void setStartTime(int s) {
		startTime = s;
	} 


	/**
	 * Die Methode dient zum Setzen des Stopzeitpunkts der Translation nach dem
	 * Auslesen aus dem entsprechenden Bedienelement.
	 * @param s Stopzeitpunkt der Translation.
	 */
	public void setStopTime(int s) {
		stopTime = s;
	} 


	/**
	 * Die Methode erzeugt eine Kopie des Intensitaetsbildes und verschiebt diese
	 * Kopie um den angegebenen Betrag nach rechts und nach unten. Die aus dem
	 * Bild geschobenen Pixel erscheinen nicht (!) wieder an der anderen Seite.
	 * Darin besteht der Unterschied zur Methode shift der Klasse FFTTools.
	 * @param h Anzahl der Pixel, um die das Bild horizontal verschoben werden soll.
	 * @param v Anzahl der Pixel, um die das Bild vertikal verschoben werden soll.
	 * @return Das verschobene Intensitaetsbild.
	 */
	private int[][] move(int h, int v) {
		int[][] movedImg = new int[256][256];

		for (int x = 0; x < 256; x++) {
			for (int y = 0; y < 256; y++) {
				if ((x - h) > 0 && (y - v) > 0) {
					movedImg[x][y] = intensityimg[x - h][y - v];
				} else {
					movedImg[x][y] = 0;
				}		// if-else
 			 }		// for y
 		 }			// for x
		 return movedImg;
	}		// Methode move
 
}			// Klasse PeriodicMotion






/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

