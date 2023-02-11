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
package virtual.mrt.sequences;

import virtual.tools.*;


/**
 * Die Klasse berechnet ein Intensitaetsbild mittels einer Flash-Sequenz.
 * Geraeteparameter sind Repititionszeit, die Anzahl der Echos pro Repititionszyklus,
 * der Echoabstabnd und die effektive Echozeit.
 * <DL><DT><B>Modifications: </B><DD>
 * Thomas Hacklaender 2000.8.15:
 * In calculate die statischen Methodenaufrufe doFFT(Image, boolean) der
 * jigl-Bibliothek für forward und reverse Transformation durch forwardFFT und
 * reverseFFT in FFTTools ersetzt.<br>
 * Thomas Hacklaender 2000.04.03:
 * Zahlenwert fuer DELTAB und GYRO korrigiert. <br>
 * Thomas Hacklaender 2000.04.03:
 * Methode getTRTime eingefuegt. <br>
 * Thomas Hacklaender 2000.05.14:
 * Zahlenwert fuer DELTAB fuer 1,0 T korrigiert. <br>
 * Thomas Hacklaender 2002.10.14:
 * Strings internationalisiert.<br>
 * </DD></DL>
 * @author   Christian Schalla
 * @author   Andreas Truemper
 * @version  1.2, 2002.10.14
 */
public class TurboSpinEcho extends Pulsesequence {


	/**
	 * Magnetfeldinhomogenitaet.
	 * Wir gehen von einem 1,0T Magnet aus, der im Abbildungsvolumen von
	 * 0,5 m Durchmesser eine Feldinhomogenitaet von 5 ppm aufweist.
	 * bei einem FOV von 50 cm und 256 Pixel Aufloesung hat jedes Pixel
	 * eine Inhomoginitaet von 19,5 * 10^-9 T.
	 * Berechnung der Relaxationszeit T2* siehe:
	 * Hashemi R: MRI the basics. Williams, Baltimore, 1997. Seite 48.
	 */
	final double	DELTAB = 19.5E-9;


	/**
	 * Gyromagnetische Verhaeltnis fuer Wasserstoff.
	 */
	final double	GYRO = 42.6;	// (Mhz/Tesla)


	/**
	 * Repititionszeit.
	 */
	private int		trTime;


	/**
	 * Effektive Echozeit.
	 */
	private int		teTime;


	/**
	 * Anzahl der Echos pro Repititionszyklus (echo train length).
	 */
	private int		etl;


	/**
	 * Zeitlicher Abstand zwischen den Echos (echo spacing).
	 */
	private int		ESPTime;


	/**
	 * Standardkonstruktor
	 */
	public TurboSpinEcho() {}


	/**
	 * Die Methode fuegt die Sequenzparameterwerte zum berechneten Bild hinzu.
	 * Dadurch kann eine DICOM-Datei mit allen relevanten Informationen
	 * gespeichert werden.
	 */
	protected void addSequenceParameterToImage() {
		ResultIP.setTR((double) trTime);
		ResultIP.setTE((double) teTime);
		ResultIP.setETL(etl);
		ResultIP.setESP((double) ESPTime);
		ResultIP.setSequence("TSE");
	}		// Methode addSequenceParameterToImage()
 

	/**
	 * Die Methode berechnet aus den Rohdatenmatrizen und den Sequenzparameterwerten
	 * die Intensitaetsmatrix. Im Fall der Turbo-spin-Echo-Sequenz muessen dabei
	 * verschiedene Intensitaetsbilder und deren Fouriertransformationen berechnet
	 * werden. Aus jeder dieser Fouriertransformationen werden dann ein paar Teilen
	 * genommen und zu einem neuen k-Raum zusammen gefuegt. Dann wird das
	 * entgueltige Intensitaetsbild als Ruecktransformation dieses k-Raums ermittelt.
	 * Ausserdem ist diese Methode fuer die Aktualisierung der
	 * Fortschrittsanzeige und das Einhalten des Simulationszeitfaktors verantwortlich.
	 */
	public void calculate() {

		// Umsetzen aller Werte in den Matrizen, so dass keine Division durch
		// Null auftreten kann
		for (int x = 0; x < PDMatrix.length; x++) {
			for (int y = 0; y < PDMatrix[0].length; y++) {
				if (T1Matrix[x][y] == 0) {
					T1Matrix[x][y] = 1;
				} 
				if (T2Matrix[x][y] == 0) {
					T2Matrix[x][y] = 1;
				} 
			} 
		} 


		jigl.image.RealGrayImage	origComImg = new jigl.image.RealGrayImage(256, 256);
		jigl.image.ComplexImage		transComImg = new jigl.image.ComplexImage(256, 256);
		jigl.image.ComplexImage		finalComImg = new jigl.image.ComplexImage(256, 256);
		jigl.image.ComplexImage		refftComImg = new jigl.image.ComplexImage(256, 256);
		jigl.image.ComplexImage		shiftedComImg = new jigl.image.ComplexImage(256, 256);

		// Berechnen des Echos, welches in der Mitte des k-Raums platziert wird
		int												diff = 100000;
		int												bestecho = 9999;

		for (int i = 0; i < etl; i++) {
			int hlp = Math.abs((ESPTime * (i + 1)) - teTime);

			if (hlp < diff) {
				diff = hlp;
				bestecho = i + 1;
			} 
		} 

		// Das Array echolines enthaelt fuer jede Zeile das Echo, dessen Signal in
		// der entsprechenden k-Raum Zeile verwendet werden soll
		int[] echolines = new int[PDMatrix[0].length];
		int		echodist = 0;
		int		uppos = 127;
		int		downpos = 128;
		int		range = (PDMatrix.length / (2 * etl));

		// Fuellen des Arrays mit den Echonummern. Dabei wird so vorgegangen, dass
		// das signalstaerkste Echo in der Mitte des k-Raum platziert wird. die anderen
		// Echos werden dann jeweils zur Haelft ueber- und unterhalb der Mitte platziert.
		for (int i = 0; i <= Math.max(bestecho, (etl - bestecho)); i++) {
			int zeile = 0;

			if ((bestecho + i) <= etl) {
				for (zeile = uppos; zeile > uppos - range; zeile--) {
					echolines[zeile] = bestecho + i - 1;
				} 
				uppos = zeile;
				for (zeile = downpos; zeile < downpos + range; zeile++) {
					echolines[zeile] = bestecho + i - 1;
				} 
			} 
			downpos = zeile;
			if (i == 0) {
				continue;
			} 
			if ((bestecho - i) > 0) {
				for (zeile = uppos; zeile > uppos - range; zeile--) {
					echolines[zeile] = bestecho - i - 1;
				} 
				uppos = zeile;
				for (zeile = downpos; zeile < downpos + range; zeile++) {
					echolines[zeile] = bestecho - i - 1;
				} 
				downpos = zeile;
			} 
		} 

		// Nun wird fuer jedes Echo eine Intesitaetsmatrix (bzw. ein ComplexImage)
		// berechnet. Jedes einzelne dieser Bilder wird fouriertransformiert. An
		// schliessend werden einige Zeilen jedes dieser Bilder in einen k-Raum
		// zusammenkopiert. Dieser k-Raum wird dann schliesslich ruecktransformiert.
		progressBar.setMaximum(etl);
		for (int n = 0; n < etl; n++) {
			progressBar.setValue(n);
			try {
				sleep(25);
			} catch (Exception err) {}
			;

			for (int x = 0; x < PDMatrix.length; x++) {
				for (int y = 0; y < PDMatrix[0].length; y++) {
					int hlp = (int) ((PDMatrix[x][y]) * (Math.exp((double) (-n - 1) * (double) (ESPTime) / (double) (T2Matrix[x][y]))) * (1 - Math.exp((double) (-trTime) / (double) (T1Matrix[x][y]))));

					if (hlp < 0) {
						System.out.println("Warnung: negativer Wert in TSE Matrix!");
					} 
					origComImg.set(x, y, hlp);
				}			// for y
 			 }			// for y
 
			// Fourier-Transformation des Intensitaetsbildes fuer das aktuelle Echo

			transComImg = FFTTools.forwardFFT(origComImg);

			// Kopieren der benoetigten Zeilen in den endgueltigen k-Raum
			for (int x = 0; x < 256; x++) {
				for (int y = 0; y < 256; y++) {
					if (echolines[y] == n) {
						finalComImg.setReal(x, y, transComImg.getReal(x, y));
						finalComImg.setImag(x, y, transComImg.getImag(x, y));

					}		// if
 				 }		// for y
 			 }			// for x
 
		}					// for etl
 
		// Nun muessen die beiden float-Arrays wieder in ein ComplexImage umgewandelt
		// werden, damit die inverse FFT durchgefuehrt werden kann
		jigl.image.ComplexImage finalFFTImg = new jigl.image.ComplexImage(256, 256);

		for (int x = 0; x < 256; x++) {
			for (int y = 0; y < 256; y++) {
				finalFFTImg.setReal(x, y, finalComImg.getReal(x, y));
				finalFFTImg.setImag(x, y, finalComImg.getImag(x, y));
			} 
		} 

		// Nun wird der endgueltige k-Raum zuruecktransformiert. Dadurch ergibt sich
		// das gewuenschte Bild

		refftComImg = FFTTools.reverseFFT(finalFFTImg);

		// Und nun wird mit den Wertes des zuruecktransformierten Bildes noch
		// die Intensitaetsmatrix gefuellt. Daraus wird in weiteren Methoden das
		// 12-Bit-Bild berechnet.
		for (int x = 0; x < 256; x++) {
			for (int y = 0; y < 256; y++) {
				IntensityMatrix[x][y] = (int) (Math.sqrt(Math.pow(refftComImg.getReal(x, y), 2) + Math.pow(refftComImg.getImag(x, y), 2)));
			}		// for x
 		 }		// for y
 
		// In super.calculate wird die Fortschrittsanzeige zurueckgesetzt
		// und dem Hautfenster mitgeteilt, dass die Berechnung fertig ist.
		super.calculate();
	} 


	/**
	 * Die Methode setzt das Klassenattribut fuer die TR-Zeit nach dem Auslesen
	 * aus dem Bedienelement.
	 * @param tr TR-Zeit.
	 */
	public void setTRTime(int tr) {
		trTime = tr;
	}		// Methode setTRTime
 

	/**
	 * Die Methode setzt das Klassenattribut fuer die effektive TE-Zeit nach dem
	 * Auslesen aus dem Bedienelement.
	 * @param te TE-Zeit.
	 */
	public void setTETime(int te) {
		teTime = te;
	}		// Methode setTETime
 

	/**
	 * Die Methode setzt das Klassenattribut fuer die Anzahl der Echos pro
	 * Repititionszyklus nach dem Auslesen aus dem Bedienelement.
	 * @param e Anzahl der Echos pro Repititionszyklus (echo train length).
	 */
	public void setETL(int e) {
		etl = e;
	}		// Methode setETL
 

	/**
	 * Die Methode setzt das Klassenattribut fuer den zeitlichen Abstand zwischen
	 * den Echos nach dem Auslesen aus dem Bedienelement.
	 * @param e Zeitlicher Abstand zwischen den Echos (echo spacing).
	 */
	public void setESPTime(int e) {
		ESPTime = e;
	}		// Methode setESPTime
 

	/**
	 * Die Methode liefert die aktuell eingestellte Repititionszeit der Pulssequenz
	 * zurueck.
	 * @return TR-Zeit.
	 */
	public int getTRTime() {
		return trTime;
	} 

}






/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

