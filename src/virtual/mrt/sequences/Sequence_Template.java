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
 * Die Klasse bildet eine Vorlage zur Erstellung neuer Pulssequenzen.
 * Der Klassenname sollte geaendert werden. Ebenso die Namen der Konstruktoren.
 * Auch andere Vorkommen der Zeichenkette Sequence_Template sind durch den
 * entsprechenden Sequenznamen zu ersetzen.
 * Als einzigen Sequenzparameter enthaelt diese Vorlage die Repititionszeit,
 * die in jeder aktuellen Pulssequenz sinnvoll ist.
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
public class Sequence_Template extends Pulsesequence {


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
	 * Klasseninterne Variable fuer die Repititionszeit. Der Wert wird aus den
	 * GUI-Elementen ausgelesen. Andere Sequenzparameter benoetigen entsprechende
	 * Variablen.
	 */
	private int		trTime;


	/**
	 * Standardkonstruktor.
	 */
	public Sequence_Template() {}


	/**
	 * Die Methode setzt die Parameterwerte der Sequenz in dem erzeugten Bild.
	 * Diese Werte werden dann im DICOM-Format mit abgespeichert. Hat die Sequenz
	 * weitere Parameter, so muessen diese ebenfalls gesetzt werden. Weitere
	 * Methoden zum Setzen der Parameter sind setTI, setTE, setFA, setESP und
	 * setETL.
	 */
	protected void addSequenceParameterToImage() {
		ResultIP.setTR((double) trTime);
		ResultIP.setSequence("Sequence_Template");
	}		// Methode addSequenceParameterToImage()
 

	/**
	 * Die Methode uebernimmt die Berechnung des Intensitaetsbildes.
	 * Um die Umrechnung in ein 12-Bit-Bild muss man sich nicht kuemmern. Dies
	 * wird in der Oberklasse erledigt.
	 */
	public void calculate() {

		// Durchlaufen der Zeilen der Rohdatenmatrizen. Nach jeder Zeile wird die
		// Fortschrittsanzeige um 1/256-stel erhoeht.
		for (int x = 0; x < PDMatrix.length; x++) {

			// Aktualisieren der Fortschrittsanzeige
			progressBar.setValue(x);

			// Nun wird die Restzeit der Berechnung berechnet und angezeigt.
			try {
				long	remainingtime = trTime * (256 - x);

				myUIClass.displayRemainingTime(remainingtime);
				sleep((long) ((((double) (iTimeFactor)) / 100) * trTime));
			} catch (Exception e) {}

			// Durchlaufen der Spalten der Rohdatenmatrizen. Hier wird nun nach
			// der entsprechenden Pulssequenzformel der Wert an jeder Stelle der
			// Intensitaetsmatrix berechnet.
			for (int y = 0; y < PDMatrix[0].length; y++) {
				IntensityMatrix[x][y] = 0;
			}		// for y
 
		}			// for x
 
		// In super.calculate wird die Fortschrittsanzeige zurueckgesetzt
		// und dem Hautfenster mitgeteilt, dass die Berechnung fertig ist.
		super.calculate();
	}		// Methode calculate
 

	/**
	 * Die Methode setzt die TR-Zeit. Sie sollte nicht geaendert oder geloescht
	 * werden. Weitere Sequenzparameter benoetigen eine entsprechende Methode.
	 * @param tr TR-Zeit
	 */
	public void setTRTime(int tr) {
		trTime = tr;
	}		// Methode setTRTime
 
}






/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

