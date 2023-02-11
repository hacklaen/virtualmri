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


/**
 * Die Klasse berechnet ein Intensitaetsbild mittels einer Flash-Sequenz.
 * Geraeteparameter sind Repititions- und Echozeit, sowie der Flipwinkel.
 * 
 * <DL><DT><B>Modifications: </B><DD>
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
public class Flash extends Pulsesequence {


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
	 * Echozeit.
	 */
	private int		teTime;


	/**
	 * Flipwinkel
	 */
	private int		angel;


	/**
	 * Standardkonstruktor
	 */
	public Flash() {}


	/**
	 * Die Methode fuegt die Sequenzparameterwerte zum berechneten Bild hinzu.
	 * Dadurch kann eine DICOM-Datei mit allen relevanten Informationen
	 * gespeichert werden.
	 */
	protected void addSequenceParameterToImage() {
		ResultIP.setTR((double) trTime);
		ResultIP.setTE((double) teTime);
		ResultIP.setFA((double) angel);
		ResultIP.setSequence("Flash");
	}		// Methode addSequenceParameterToImage()
 

	/**
	 * Die Methode berechnet aus den Rohdatenmatrizen und den Sequenzparameterwerten
	 * die Intensitaetsmatrix. Ausserdem ist sie fuer die Aktualisierung der
	 * Fortschrittsanzeige und das Einhalten des Simulationszeitfaktors verantwortlich.
	 */
	public void calculate() {

		// Sinus und Kosinus ausserhalb der Schleifen berechnen.
		double	dangel = (double) angel * Math.PI / 180;
		double	sinangel = Math.sin(dangel);
		double	cosangel = Math.sin(dangel);

		for (int x = 0; x < PDMatrix.length; x++) {

			// Aktualisieren der Fortschrittsanzeige
			progressBar.setValue(x);
			try {
				if ((x % 10) == 0) {
					sleep(5);
				} 
			} catch (Exception err) {}
			;

			// Berechnen und Anzeigen der Restzeit sowie ggf. warten
			try {
				int		nex = myUIClass.pNEX.getValue();
				long	remainingtime = trTime * (256 - x) * nex;

				myUIClass.displayRemainingTime(remainingtime);
				sleep((long) ((((double) (iTimeFactor)) / 100) * trTime));
			} catch (Exception e) {}

			// Berechnen der Intensitaetsmatrix
			for (int y = 0; y < PDMatrix[0].length; y++) {
				if (T1Matrix[x][y] == 0) {
					T1Matrix[x][y] = 1;
				} 
				if (T2Matrix[x][y] == 0) {
					T2Matrix[x][y] = 1;

				} 
				double	t2x = (double) (1 / ((1 / (double) T2Matrix[x][y]) + GYRO * DELTAB));

				double	a = Math.exp((double) -trTime / (double) T1Matrix[x][y]);
				double	b = Math.exp((double) -trTime / t2x);
				double	c = Math.exp((double) -teTime / t2x);

				IntensityMatrix[x][y] = (int) ((PDMatrix[x][y]) * (c * (1 - a) * sinangel) / ((1 - a * cosangel) + b * (cosangel - a)));
			}		// for y
 		 }		// for x
 
		// In super.calculate wird die Fortschrittsanzeige zurueckgesetzt
		// und dem Hautfenster mitgeteilt, dass die Berechnung fertig ist.
		super.calculate();
	}		// Methode calculate
 

	/**
	 * Die Methode setzt das Klassenattribut fuer die TR-Zeit nach dem Auselesen
	 * aus dem Bedienelement.
	 * @param tr TR-Zeit.
	 */
	public void setTRTime(int tr) {
		trTime = tr;
	}		// Methode setTRTime
 

	/**
	 * Die Methode setzt das Klassenattribut fuer die TE-Zeit nach dem Auselesen
	 * aus dem Bedienelement.
	 * @param te TE-Zeit.
	 */
	public void setTETime(int te) {
		teTime = te;
	}		// Methode setTETime
 

	/**
	 * Die Methode setzt das Klassenattribut fuer den Flipwinkel nach dem Auselesen
	 * aus dem Bedienelement.
	 * @param an Der Flipwinkel.
	 */
	public void setAngel(int an) {
		angel = an;
	}		// Methode setAngel
 

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

