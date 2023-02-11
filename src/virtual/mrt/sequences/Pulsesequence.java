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

import javax.swing.*;
import java.util.*;

import virtual.mrt.*;
import virtual.mrt.artefacts.*;
import virtual.tools.*;


/**
 * Diese Klasse ist die Oberklasse fuer alle Pulssequenz-Berechnungsklassen.
 * Sie erweitert die Klasse Thread. Somit sind alle Pulssequenzen Threads.
 * Das bietet die Moeglichkeit,
 * die Abarbeitung zu stoppen und Fortschrittsanzeigen auszugeben.
 * Die Klasse definiert Methoden zur Berechnung eines Bildes mittels der
 * Pulssequenzformel. Da die Klasse abstract ist, muss sie erweitert werden.
 * <DL><DT><B>Modifications: </B><DD>
 * Thomas Hacklaender 2006.01.09:
 * Kleinere Korrekturen. Mit "tha:" bezeichnet. <br>
 * Thomas Hacklaender 2006.01.09:
 * Medianfilterung fuer dieIntensity Matrix eingebaut. <br>
 * Thomas Hacklaender 2000.02.13:
 * Eine Verkleinerung des FOV bewirkt eine Vergroesserung des
 * Bildes. integrateFOVInIntensityMartrix ersetzt durch scaleFOV. <br>
 * Thomas Hacklaender 2000.05.14:
 * simulateXAliasing in simulateFreqAliasing umbenannt. <br>
 * Thomas Hacklaender 2000.08.14:
 * Debugtext in convertIntensityMatrixTo12Bit auskommentiert.<br>
 * Thomas Hacklaender 2002.10.13:
 * Strings internationalisiert.<br>
 * </DD></DL>
 * @author   Christian Schalla
 * @author   Andreas Truemper
 * @version  1.2, 2002.10.13
 */
public abstract class Pulsesequence extends Thread {


	/**
	 * Matrix mit den T1-Werten.
	 */
	protected int[][]					T1Matrix;


	/**
	 * Matrix mit den T2-Werten.
	 */
	protected int[][]					T2Matrix;


	/**
	 * Matrix mit den Werten fuer die Protenendichte.
	 */
	protected int[][]					PDMatrix;


	/**
	 * Ergebnismatrix, enthaelt die Intensitaetswerte.
	 */
	protected int[][]					IntensityMatrix;


	/**
	 * Ergebnismatrix, automatisch auf 12-Bit gefenstert.
	 */
	protected int[][]					Result12Bit;


	/**
	 * Das Ergebnisbild (12-Bit) als ImagePlus-Objekt.
	 */
	protected ImagePlus				ResultIP;


	/**
	 * Simulationszeitfaktor (0-100 Prozent).
	 */
	protected int							iTimeFactor;


	/**
	 * Referenz zum Hauptfenster.
	 */
	protected VMRTFrame				mainFrame;


	/**
	 * Referenz zur Fortschrittsanzeige im Hauptfenster.
	 */
	protected JProgressBar		progressBar;


	/**
	 * Referenz auf die zugehoerige GUI-Klasse.
	 */
	protected PulsesequenceUI myUIClass;


	/**
	 * Standardkonstruktor. Er setzt die Prioritaet des Thread auf maximal.
	 */
	public Pulsesequence() {
		this.setPriority(Thread.MAX_PRIORITY - 1);
	}		// Standardkonstruktor


	/**
	 * Die Methode setzt die Referenz zum Hauptfenster. Anschliessend besorgt sich
	 * die Methode Referenzen auf die Fortschrittsanzeige und den Simulationszeit-
	 * faktor und speichert diese in Instanzvariablen.
	 * @param vmrt Die Referenz auf das Hauptfenster.
	 */
	public void setMainFrame(VMRTFrame vmrt) {
		mainFrame = vmrt;
		iTimeFactor = mainFrame.getTimeFactor();
		progressBar = mainFrame.getProgressBar();
	}		// Methode setMainFrame
 

	/**
	 * Die Methode ruft die Rechenfunktion der Pulssequenz auf. Sie wird
	 * automatisch aufgerufen, wenn Pulsesequence.start() aufgerufen wird.
	 * aufgrund der Thread-Funktionalitaet darf die Rechenfunktion immer nur
	 * ueber die start()-Methode aufgerufen werden.
	 */
	public void run() {
		calculate();
		addSequenceParameterToImage();
	}		// Methode run
 

	/**
	 * Die Methode fuegt das Berechnungsdatum des Ergebnisbildes zu diesem Bild
	 * hinzu. Somit ist diese Information auch vorhanden, wenn das Bild im
	 * DICOM-Format abgespeichert wird. In den Unterklassen koennen weitere
	 * Informationen (z.B. Seuqnzname) zum Bild hinzugefuegt werden.
	 */
	protected void addSequenceParameterToImage() {
		ResultIP.setCurrentDate();
	}		// Methode addSequenceParameterToImage()
 

	/**
	 * Die Method liest aus dem uebergebenen FileLoader-Objekt die Rohdatenmatrizen
	 * aus und legt sie in den lokalen Variablen ab. Ausserdem wird die
	 * Ergebnismatrix initialisiert.
	 * @param fl Das FileLoader-Objekt, dass die auszulesenden Rohdaten enthaelt.
	 */
	public void setRawData(FileLoader fl) throws NullPointerException {

		// Wenn die Berechnung ohne vorheriges Laden eines Datensatzes angestossen
		// wird, wird ein Fehler ausgegeben
		if (fl == null) {
			myUIClass.enableStartButton(true);
			myUIClass.enableStopButton(false);
			throw new NullPointerException(java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("pulssequence.exception.nodata"));
		} 

		// Auslesen der Rophdatenmatrizen
		T1Matrix = fl.getT1Matrix();
		T2Matrix = fl.getT2Matrix();
		PDMatrix = fl.getPDMatrix();

		// Initialisieren der Ergebnismatrizen
		IntensityMatrix = new int[PDMatrix.length][PDMatrix[0].length];
		Result12Bit = new int[PDMatrix.length][PDMatrix[0].length];
	}		// Methode setRawData
 

	/**
	 * Die Methode stellt die Berechnungsfunktion fuer die Pulssequenz dar.
	 * Allerdings muss die eigentliche Funktionalitaet in den Unterklassen
	 * definiert werden. Diese Methode stoesst nach Berechnung des Intensitaetsbides
	 * in der Unterklasse den potentiellen Artefakt-Simulator an.
	 * Zum Ende der Methode wird die Fortschrittsanzeige zurueckgesetzt und
	 * das Hauptfenster benachrichtigt, dass die Berechnung abgeschlossen ist.
	 */
	public void calculate() {

		// ... and dieser Stelle fuehren die Unterklassen ihren Code aus

                // tha: 2006.01.09
                checkIntensityMatrix();
                filterIntensityMatrix();
                
		// Aufrufen eines potentiellen Artefakt-Simulators
		String	strArtmethod = myUIClass.getArtefactMethod();

		if (strArtmethod != null) {
			mainFrame.setStatusBar(java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("pulssequence.statusbar.note.artefact"));
			Artefact	art = myUIClass.getArtefact();

			art.setSequence(this);
			art.setIntensityImage(IntensityMatrix);
			IntensityMatrix = art.calculate(strArtmethod);
		} 

		// Rauschen hinzufuegen
		addNoise();

		// Frequenz-Einfaltungen simulieren
		simulateFreqAliasing(myUIClass.pFOV.getValue());

		// Phasen-Einfaltungen simulieren
		// >>> tha 2000.05.14
		// simulatePhaseWrapping();
		simulatePhAliasing(myUIClass.pFOV.getValue());

		// FOV-Einstellungen beruecksichtigen
		// >>> tha 2000.05.13
		// integrateFOVInIntensityMartrix();
		scaleFOV();

		// <<<

		// Rechteckiges FOV beruecksichtigen
		simulateRectFOV();

		// Nach der Berechnung die Fortschrittsanzeige zuruecksetzen
		// und dem Hauptfenster mitteilen, dass die Berechnung fertig ist
		// (Dies ist nur so moeglich, da die Berechnung im Thread stattfindet)
		progressBar.setValue(0);
		mainFrame.setStatusBar(java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("pulssequence.statusbar.note.finished"));
		convertIntensityMatrixTo12Bit();
		ResultIP = mainFrame.drawCreatedIntensityImage(this);

		// Zuecksetzen des Start- und Stopknopfes sowie der Restzueit
		myUIClass.pbStop.setEnabled(false);
		myUIClass.pbStart.setEnabled(true);
		myUIClass.displayRemainingTime((long) 0);
	} 

        
        /**
         * Werte der IntensityMatrix mussen >= 0 sein.
         * tha: 2006.01.09
         */
        public void checkIntensityMatrix() {
            for (int x = 0; x < IntensityMatrix.length; x++) {
                for (int y = 0; y < IntensityMatrix[0].length; y++) {
                    if (IntensityMatrix[x][y] < 0) {
                        IntensityMatrix[x][y] = 0;
                    }
                }
            }
        }

        
        /**
         * Filtert die IntensityMatrix. Zur Beschleunigung wird der aussere Rahmen
         * von 1 Pixel Breite nicht berechnet.
         * tha: 2006.01.09
         */
        public void filterIntensityMatrix() {
            int medianUmgebung;
            int diff;
            
            for (int x = 1; x < IntensityMatrix.length - 1; x++) {
                for (int y = 1; y < IntensityMatrix[0].length - 1; y++) {
                    medianUmgebung = (IntensityMatrix[x-1][y-1] + IntensityMatrix[x][y-1] + IntensityMatrix[x+1][y-1] +
                                      IntensityMatrix[x-1][y]                             + IntensityMatrix[x+1][y]   +
                                      IntensityMatrix[x-1][y+1] + IntensityMatrix[x][y+1] + IntensityMatrix[x+1][y+1]) / 8;
                    diff = medianUmgebung - IntensityMatrix[x][y];
                    if (diff < 0) {
                        diff *= -1;
                    }
                    if (diff > 2 * IntensityMatrix[x][y]) {
                        IntensityMatrix[x][y] = medianUmgebung;
                    }
                }
            }
        }

	/**
	 * Die Methode liefert die Ergebnismatrix (Intensitaetsmatrix) zurueck.
	 * @return Die Ergbnismatrix.
	 */
	public int[][] getIntensityMatrix() {
		return IntensityMatrix;
	}		// Methode getIntensityMatrix
 

	/**
	 * Die Methode liefert das 12-Bit-Bild als eindimensionales Feld von 16-Bit-Zahlen
	 * zurueck.
	 * @return Das 12-Bit-Ergebnisbild.
	 */
	public short[] getShort12BitMatrix() {
		short[] shortimg = new short[Result12Bit.length * Result12Bit[0].length];

		for (int x = 0; x < Result12Bit.length; x++) {
			for (int y = 0; y < Result12Bit[0].length; y++) {
				shortimg[(y * Result12Bit[0].length) + x] = (short) Result12Bit[x][y];
			} 
		} 

		return shortimg;
	}		// Methode getShort12BitMatrix
 

	/**
	 * Die Methode setzt die Referenz auf die zugehoerige UI-Klasse.
	 * @param Referenz auf die zugehoerige UI-Klasse.
	 */
	public void setUI(PulsesequenceUI ui) {
		myUIClass = ui;
	}		// Methode setUI
 

	/**
	 * Die Methode konvertiert die zunaechst berechnete Intensitaetsmatrix in ein
	 * 12-Bit Bild. Dazu wird zunaecht ein Histogramm des Intesitaetsbildes berechnet
	 * und auf dessen Grundlage werden die Intensitaetswerte auf Grauwerte im Bereich
	 * [0;4095] abgebildet.
	 */
	public void convertIntensityMatrixTo12Bit() {

		// Anzahl der Bits pro Pixel im Intensitaetsbild
		int			OriginBitNum = (int) (Math.pow(2, 16));

		// Anzahl der Bits pro Pixel im Resultatbild; gewoehnlich 12 Bit
		int			ResultBitNum = (int) (Math.pow(2, 12));

		// Summe der Grauwerte des Bildes;
		long		sum;

		// Minimaler und maximaler Grauwert (bzw. 10% Perzentile)
		int			min, max;

		// Hilfvariable zur Bestimmung der 10 bzw 90%-Perzentile
		int			tmp;

		// Zentrum und Breite der automatischen 12-Bit Fensterung
		int			center, width;

		// Hilfsvariable fuer die Fensterung
		int			value;

		// Maximaler Intensitaetswert im Intensitaetsbild. Wird benoetigt fuer die
		// Groessenbestimmung bzw. Umrechnung des Histogramms
		int			maxint;

		// Abbildungsfaktor fuer die Fensterung. Gibt an, wie viele Grauwerte des
		// Ausgangsbildes auf einen Grauwert im Zielbild abgebildet werden
		double	step;

		// 
		int			minPixelValue = 0;

		// 
		int			maxPixelValue = OriginBitNum;

		// linke und rechte Grenze des Fensterungs-Ausschnitts
		int			minImageValue, maxImageValue;

		// Histogrammtabelle
		int[]		histogramm = new int[OriginBitNum];

		// Suchen des groessten Intensitaetswertes im Bild.
		// Daraus ergibt sich die Groesse des Histogramms. (s.u.)
		maxint = 0;
		int minint = 999999;
		// int numneg = 0;

                // tha: 2006.01.09
 		// for (int x = 0; x < 156; x++) {
		for (int x = 0; x < IntensityMatrix.length; x++) {
			// for (int y = 0; y < 256; y++) {
			for (int y = 0; y < IntensityMatrix[0].length; y++) {
				if (IntensityMatrix[x][y] > maxint) {
					maxint = IntensityMatrix[x][y];
				} 
				if (IntensityMatrix[x][y] < minint) {
					minint = IntensityMatrix[x][y];
				}
                                // tha: 2006.01.09: Unnoetig
				// if (IntensityMatrix[x][y] < 0) {
				// 	numneg++;
				//	;
				//	IntensityMatrix[x][y] = 55000;
				// } 

			} 
		} 

		// GGf. runterkonvertieren des Bildes auf 16-Bit. Ist noetig, damit
		// Histogrammtabelle nicht zu gross wird.
		double	histfactor = 1;
		int			hlp = 0;

		if (maxint > (Math.pow(2, 16) - 1)) {

			// Auskommentiert: tha 2000.8.14
			// System.out.println("Achtung! Das Intesitaetsbild muss auf 16-Bit runterkonvertiert werden!");
			histfactor = (double) (Math.pow(2, 16)) / (double) (maxint);

			// Runterkonvertieren des Bildes auf 16-Bit-Intensitaetswerte
                        // tha: 2006.01.09
			// for (int x = 0; x < 256; x++) {
			for (int x = 0; x < IntensityMatrix.length; x++) {
				// for (int y = 0; y < 256; y++) {
				for (int y = 0; y < IntensityMatrix[0].length; y++) {
					hlp = (int) (IntensityMatrix[x][y] * histfactor);
					if (hlp >= 65536) {
						hlp = 65535;
					} 
					IntensityMatrix[x][y] = hlp;
				} 
			} 
		} 

		// Berechnen des Histogramms
                // tha: 2006.01.09
		// for (int x = 0; x < 256; x++) {
		for (int x = 0; x < IntensityMatrix.length; x++) {
			//for (int y = 0; y < 256; y++) {
			for (int y = 0; y < IntensityMatrix[0].length; y++) {
				histogramm[Math.abs(IntensityMatrix[x][y])]++;
			} 
		} 

		min = 0;
		max = histogramm.length - 1;

		width = max - min;
		center = (min + max) / 2;

		minImageValue = center - (width / 2);
		maxImageValue = center + (width / 2);

		if (center > maxPixelValue) {
			center = maxPixelValue;
		} 
		if (center < minPixelValue) {
			center = minPixelValue;
		} 
		if (width > maxPixelValue) {
			width = maxPixelValue;
		} 
		if (width < 1) {
			width = 1;
		} 

		if (minImageValue < minPixelValue) {
			minImageValue = minPixelValue;
		} 
		if (maxImageValue > maxPixelValue) {
			maxImageValue = maxPixelValue;
		} 

		step = (double) ((double) (ResultBitNum) / ((double) (maxImageValue) - (double) (minImageValue) + 1.0));

		// Mappen der Grauwerte des 10%-Fensters auf 12-Bit
		value = 0;
                // tha: 2006.01.09
		// for (int x = 0; x < 256; x++) {
		for (int x = 0; x < IntensityMatrix.length; x++) {
			// for (int y = 0; y < 256; y++) {
			for (int y = 0; y < IntensityMatrix[0].length; y++) {
				value = IntensityMatrix[x][y] - minImageValue;
				if (value < 0) {
					value = 0;
				} 
				value = (int) ((double) (value) * step);
				if (value >= ResultBitNum) {
					value = ResultBitNum - 1;
				} 
				Result12Bit[x][y] = value;
			}		// for
 		 }		// for
 
	}				// Methode ConvertIntensityImageTo12Bit
 

	/*
	 * >>> tha 2000.05.13: Ersetzt durch scaleFOV. Damit wird die Realitaet
	 * besser simuliert.
	 * Die Methode integriert die aktuelle Einstellung des FOV (Field of View)
	 * in die Intensitaetsmatrix. Das bedeutet, dass die Randbereich mit Nullen
	 * gefuellt werden. Dadurch wird natuerlich das Bild kleiner. In der Realitaet
	 * wuerde sich das anders verhalten. Eine Verkleinerung des FOV haette eine
	 * Verbessung der Aufloesung zur Folge. Zwar wuerde der dargestellte Bildausschnitt
	 * kleiner, jedoch auch die Pixelgroesse. Wuerde man in der Simulation jedoch den
	 * verkleinerten Bildausschnitt auf die Ursprungsgroesse vergroessern, haette dies
	 * eine Verschlechterung der Aufloesung zur Folge, da die Rohdatenmatrizen
	 * leider keine unendlich hohe Aufloesung aufweisen. Also haben wir uns dafuer
	 * entschieden, das Bild kleiner darzustellen.
	 * 
	 * public void integrateFOVInIntensityMartrix() {
	 * int fov = 256 - myUIClass.pFOV.getValue();
	 * int fovDIV2 = fov / 2;
	 * // Schwaerzen des linken und rechten Randes
	 * for (int y = 0; y < 256; y++) {
	 * for (int x = 0; x < fovDIV2; x++) {
	 * IntensityMatrix[x][y] = 0;
	 * }		// for x
	 * for (int x = 256 - fovDIV2; x < 256; x++) {
	 * IntensityMatrix[x][y] = 0;
	 * }		// for x
	 * }		// for y
	 * 
	 * // Schwaerzen des oberen un unteren Randes
	 * for (int x = 0; x < 256; x++) {
	 * for (int y = 0; y < fovDIV2; y++) {
	 * IntensityMatrix[x][y] = 0;
	 * }		// for y
	 * for (int y = 256 - fovDIV2; y < 256; y++) {
	 * IntensityMatrix[x][y] = 0;
	 * }		// for y
	 * }		// for x
	 * }			// Methode integrateFOVInIntensityMartrix()
	 */


	/**
	 * Die Methode simuliert Einfaltungen in Frequenz-Richtung, die Aufgrund
	 * eines zu klein gewaehlten FOV entstehen. Ausserhalb des Bildes liegende
	 * signalgebende Pixel werden falsch frequenzkodiert und werden somit in
	 * den Bildbereich projiziert.
	 */
	public void simulateFreqAliasing(int freqFOV) {
		if (!myUIClass.tbFreqOS.isSelected() & (freqFOV < 256)) {

			int notvisible = 256 - freqFOV;
			int notvisibleDIV2 = notvisible / 2;

			for (int y = 0; y < 256; y++) {
				for (int x = notvisibleDIV2; x < 256 - notvisibleDIV2; x++) {
					int hlpindex1 = x + freqFOV;
					int hlpindex2 = x - freqFOV;
					int hlpval1 = 0;
					int hlpval2 = 0;

					if (hlpindex1 < 256) {
						hlpval1 = IntensityMatrix[hlpindex1][y];
					} 
					if (hlpindex2 >= 0) {
						hlpval2 = IntensityMatrix[hlpindex2][y];

					} 
					IntensityMatrix[x][y] = IntensityMatrix[x][y] + hlpval1 + hlpval2;
				} 
			} 
		} 
	} 


	/**
	 * Die Methode simuliert Einfaltungen in Phasen-Richtung, die Aufgrund
	 * eines zu klein gewaehlten FOV entstehen. Ausserhalb des Bildes liegende
	 * signalgebende Pixel werden falsch phasenkodiert und werden somit in
	 * den Bildbereich projiziert.
	 */
	public void simulatePhAliasing(int phFOV) {
		if (!myUIClass.tbPhOS.isSelected() & (phFOV < 256)) {

			int notvisible = 256 - phFOV;
			int notvisibleDIV2 = notvisible / 2;

			for (int x = 0; x < 256; x++) {
				for (int y = notvisibleDIV2; y < 256 - notvisibleDIV2; y++) {
					int hlpindex1 = y + phFOV;
					int hlpindex2 = y - phFOV;
					int hlpval1 = 0;
					int hlpval2 = 0;

					if (hlpindex1 < 256) {
						hlpval1 = IntensityMatrix[x][hlpindex1];
					} 
					if (hlpindex2 >= 0) {
						hlpval2 = IntensityMatrix[x][hlpindex2];

					} 
					IntensityMatrix[x][y] = IntensityMatrix[x][y] + hlpval1 + hlpval2;
				} 
			} 
		} 
	} 


	/**
	 * Die Methode simuliert ein rechteckiges FOV in Phasen-Richtung.
	 */
	public void simulateRectFOV() {
		int phFOV;

		if (myUIClass.pRect.getValue() != 8) {
			phFOV = myUIClass.pFOV.getValue() * myUIClass.pRect.getValue() / 8;

			// Einfaltungen simulieren
			simulatePhAliasing(phFOV);

			// nicht gemessene Bildanteile schwaerzen
			int notvisible = (256 - phFOV) / 2;

			for (int y = 0; y < 256; y++) {
				if ((y < notvisible) | (y >= phFOV + notvisible)) {
					for (int x = 0; x < 256; x++) {
						IntensityMatrix[x][y] = 0;
					} 
				} 
			} 
		} 
	} 


	/*
	 * >>> tha 2000.05.14: Ersetzt durch simulatePhAliasing
	 * Die Methode simuliert Einfaltungen in Phasenkodierrichtung, die Aufgrund
	 * eines zu klein gewaehlten FOV (bzw. Seitenverhaeltnisses) entstehen.
	 * 
	 * public void simulatePhaseWrapping() {
	 * jigl.image.RealGrayImage	origImage;
	 * jigl.image.ComplexImage		transformedImg = new jigl.image.ComplexImage(256, 256);
	 * float[][]									fftReal;
	 * float[][]									fftImag;
	 * jigl.image.ComplexImage		backTransform;
	 * if (!myUIClass.tbPhOS.isSelected()) {
	 * // Bild konvertieren
	 * origImage = fft.FFTTools.makeRealGrayImage(IntensityMatrix);
	 * // Fouriertransformation
	 * transformedImg = jigl.image.utils.FFT.forward(origImage);
	 * // Shiften
	 * transformedImg = fft.FFTTools.shift(transformedImg, 128, 128);
	 * // Jede 2. Zeile loeschen
	 * for (int x = 0; x < 256; x++) {
	 * for (int y = 0; y < 256; y++) {
	 * if ((x % 2) == 0) {
	 * transformedImg.setReal(x, y, 0);
	 * // transformedImg.setImag(x,y,0);
	 * }		// if
	 * }		// for y
	 * }			// for x
	 * 
	 * // Zurueckshiften
	 * transformedImg = fft.FFTTools.shift(transformedImg, 128, 128);
	 * // Ruecktransformation
	 * backTransform = jigl.image.utils.FFT.inverse(transformedImg);
	 * // Magnitudenbild berechnen
	 * jigl.image.RealGrayImage	magImg = backTransform.getMagnitudeImage();
	 * // Wieder in Intensitaetsmatrix kopieren
	 * for (int x = 0; x < 256; x++) {
	 * for (int y = 0; y < 256; y++) {
	 * IntensityMatrix[x][y] = (int) magImg.get(x, y);
	 * }
	 * }
	 * }
	 * }
	 */


	/**
	 * Die Methode fuegt der Intensitaetsmatrix ein Rauschen hinzu, das vom
	 * eingestellten Signal-zu-Rausch-Verhaeltnis und der ausgewaehlten Spule
	 * abhaengt. Die Spule liefert ein Grundrauschen im gesamten Bild,
	 * wohingegen das Signal-zu-Rausch-Verhaeltnis nur ein Rauschen in
	 * signalgebenden Bildteilen nach sich zieht.
	 */
	public void addNoise() {
		double	baseNoise = 0;
		int			coil = myUIClass.getCoil();
		Random	myRandomizer = new Random();
		double	meanIntensity;
		double	signalNoise = 0.0;
		int			val;
		int			lVal;
		int			bn;
		int			sn;

		double	sum = 0.0;

		for (int x = 0; x < 256; x++) {
			for (int y = 0; y < 256; y++) {
				sum += IntensityMatrix[x][y];
			} 
		} 
		meanIntensity = sum / (256 * 256);

		signalNoise = 1.0 - myUIClass.getSNRatio();
		if (signalNoise < 0.0) {
			signalNoise = 0.0;

			// 10% des S/N Faktors
		} 
		signalNoise *= 0.10;

		for (int x = 0; x < 256; x++) {
			for (int y = 0; y < 256; y++) {

				switch (coil) {
				case 0: {

					// Die Oberflaechenspule gibt optimales Signal
					baseNoise = 0.0;
					break;
				} 
				case 1: {

					// 5% des mittler Intensitaets-Wertes
					baseNoise = meanIntensity * 0.05;
					break;
				} 
				}

				val = IntensityMatrix[x][y];
				bn = (int) (baseNoise * myRandomizer.nextGaussian());
				sn = (int) (val * signalNoise * myRandomizer.nextGaussian());
				lVal = val + bn + sn;
				if (lVal < 0) {
					lVal = 0;
				} 
				if (lVal > Integer.MAX_VALUE) {
					lVal = Integer.MAX_VALUE;
				} 
				IntensityMatrix[x][y] = (int) lVal;
			} 
		} 
	}		// Methode addNoiseToPDMatrix()
 

	/**
	 * Skaliert das eingestellte FOV auf die volle Bildgroesse der IntensityMatrix
	 * von 256*256 Pixel.
	 * 
	 * @author Thomas Hacklaender
	 */
	public void scaleFOV() {

		int[][] resultMatrix = new int[256][256];

		int			fov = myUIClass.pFOV.getValue();
		double	xs;
		double	ys;
		int			xFOV = (256 - fov) / 2 - 1;
		int			yFOV = (256 - fov) / 2 - 1;
		double	xScale = 256.0 / fov;
		double	yScale = 256.0 / fov;

		for (int y = 0; y < 256; y++) {
			ys = y / yScale + yFOV;
			for (int x = 0; x < 256; x++) {
				xs = x / xScale + xFOV;
				resultMatrix[x][y] = getInterpolatedPixel(xs, ys, IntensityMatrix);
			} 
		} 

		IntensityMatrix = resultMatrix;
	} 


	/**
	 * Fuehrt eine bilineare Interpolation der Pixelwerte durch.
	 * 
	 * @param x		  x-Koordinate des zu interpoliedrenden Pixels
	 * @param y		  y-Koordinate des zu interpoliedrenden Pixels
	 * @param pixel	Die zweidimensionale Matrix der Pixelwerte
	 * @return      Der interpolierte Pixelwert.
	 * 
	 * @author Thomas Hacklaender
	 */
	private final int getInterpolatedPixel(double x, double y, int[][] pixel) {
		int			lowerLeft = 0;
		int			lowerRight;
		int			upperLeft;
		int			upperRight;

		int			xbase = (int) x;
		int			ybase = (int) y;
		double	xFraction = x - xbase;
		double	yFraction = y - ybase;

		try {
			lowerLeft = pixel[xbase][ybase];
			lowerRight = pixel[xbase + 1][ybase];
			upperLeft = pixel[xbase][ybase + 1];
			upperRight = pixel[xbase + 1][ybase + 1];
			double	upperAverage = upperLeft + xFraction * (upperRight - upperLeft);
			double	lowerAverage = lowerLeft + xFraction * (lowerRight - lowerLeft);

			return (int) (lowerAverage + yFraction * (upperAverage - lowerAverage));
		} catch (Exception e) {
			return lowerLeft;
		} 
	} 


}






/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

