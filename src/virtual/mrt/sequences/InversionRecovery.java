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
 * Die Klasse berechnet ein Intensitaetsbild mittels einer Inversion-Recovery-Sequenz.
 * Geraeteparameter sind Repititions- und Inversionszeit.
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
public class InversionRecovery extends Pulsesequence {


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
	final double	GYRO = 42.6;			// (Mhz/Tesla)


	/**
	 * Repititionszeit.
	 */
	private int		trTime;


	/**
	 * Inversionszeit
	 */
	private int		tiTime;


	/**
	 * Standardkonstruktor.
	 */
	public InversionRecovery() {}		// Standardkonstruktor


	/**
	 * Die Methode fuegt die Sequenzparameterwerte zum berechneten Bild hinzu.
	 * Dadurch kann eine DICOM-Datei mit allen relevanten Informationen
	 * gespeichert werden.
	 */
	protected void addSequenceParameterToImage() {
		ResultIP.setTR((double) trTime);
		ResultIP.setTI((double) tiTime);
		ResultIP.setSequence("IR");
	}		// Methode addSequenceParameterToImage()
 

	/**
	 * Die Methode berechnet aus den Rohdatenmatrizen und den Sequenzparameterwerten
	 * die Intensitaetsmatrix. Ausserdem ist sie fuer die Aktualisierung der
	 * Fortschrittsanzeige und das Einhalten des Simulationszeitfaktors verantwortlich.
	 */
	public void calculate() {
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

				// Divison durch 0 abfangen
				if (T1Matrix[x][y] == 0) {
					T1Matrix[x][y] = 1;

				} 
				IntensityMatrix[x][y] = (int) Math.abs(((PDMatrix[x][y]) * (1 - 2 * Math.exp((double) (-tiTime) / (double) (T1Matrix[x][y])) + Math.exp((double) (-trTime) / (double) (tiTime)))));
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
	 * Die Methode setzt das Klassenattribut fuer die TI-Zeit nach dem Auselesen
	 * aus dem Bedienelement.
	 * @param ti TI-Zeit.
	 */
	public void setTITime(int ti) {
		tiTime = ti;
	}		// Methode setTITime
 

	/**
	 * Die Methode liefert die eingestellte Repititionszeit zurueck.
	 * @return Die Repititionszeit.
	 */
	public int getTRTime() {
		return trTime;
	} 


	/**
	 * Die Methode liefert die eingestellte Inversionszeit zurueck.
	 * @return Die Inversionszeit.
	 */
	public int getTITime() {
		return tiTime;
	} 

}






/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

