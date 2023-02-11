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

import javax.swing.*;

import virtual.mrt.sequences.*;


/**
 * Diese Klasse ist die abstrakte Oberklasse fuer alle Berechnungsklassen fuer
 * Artefakt-Simulatoren. Sie stellt insbesondere eine Referenz auf die
 * aufrufende Pulssequenz zur Verfuegung und speichert das von der Pulssequenz
 * erzeugte Intensitaetsbild.
 * 
 * @author   Christian Schalla
 * @author   Andreas Truemper
 * @version  1.0, 15. Oktober 1999
 */
public abstract class Artefact extends Thread {


	/**
	 * Das von der aufrufenden Pulssequenz erzeugte Intensitaetsbild, in dem
	 * Artefakte simuliert werden sollen.
	 */
	protected int[][]				intensityimg;


	/**
	 * Referenz auf die aufrufende Pulssequenz.
	 */
	protected Pulsesequence sequence;


	/**
	 * Referenz auf die Fortschrittsanzeige im VMRT-Fenster.
	 */
	protected JProgressBar	progressBar;


	/**
	 * Standardkonstruktor.
	 */
	public Artefact() {}	// Standardkonstruktor


	/**
	 * Diese Methode wird von der aufrufenden Pulssequenz immer zuerst aufgerufen.
	 * Der uebergebene Methodenname gibt an, welche Methode die korrekte
	 * Berechnungsvorschrift enthaelt. Die Methode weist in dieser Oberklasse
	 * allerdings keine Funktionalitaet auf.
	 * @param method Der Name der Methode, die die korrekte Berechnungsvorschrift
	 * fuer das Artefakt enthaelt.
	 * @return Das manipulierte Intensitaetsbild.
	 */
	public abstract int[][] calculate(String method);


	/**
	 * Die Methode wird von der aufrufenden Pulssequenz aufgerufen und setzt das
	 * von der Pulssequenz berechnete Intensitaetsbild.
	 * @param intimg Das zu manipulierende Intensitaetsbild.
	 */
	public void setIntensityImage(int[][] intimg) {
		intensityimg = intimg;
	}		// Methode setIntensityImage
 

	/**
	 * Die Methode wird von der aufrufenden Pulssequenz aufgerufen und setzt eine
	 * Referenz auf selbige.
	 * @param seq Referenz auf die aufrufende Pulssequenz.
	 */
	public void setSequence(Pulsesequence seq) {
		sequence = seq;
	}		// Methode setSequence
 

	/**
	 * Die Methode wird von der aufrufenden Pulssequenz aufgerufen und setzt eine
	 * Referenz auf die Fortschrittsanzeige im VMRT-Fenster.
	 * @param pb Referenz auf die Fortschrittsanzeige im VMRT-Fenster.
	 */
	public void setProgressBar(JProgressBar pb) {
		progressBar = pb;
	}		// Methode setProgressBar
 
}






/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

