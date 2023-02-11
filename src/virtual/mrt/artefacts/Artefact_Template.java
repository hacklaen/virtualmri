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


/**
 * Die Klasse dient als Vorlage zur Erzeugung neuer Berechnungsklassen fuer
 * Artefakt-Simulatoren.
 * 
 * @author   Christian Schalla
 * @author   Andreas Truemper
 * @version  1.0, 15. Oktober 1999
 */
public class Artefact_Template extends Artefact {

	// Hier werden die Klassenvariablen zu den Bedienelementen der UI-Klasse
	// definiert, z.B.
	// private int xSpeed;


	/**
	 * Standardkonstruktor.
	 */
	public Artefact_Template() {}		// Standardkonstruktor


	/**
	 * Diese Methode wird immer von der aufrufenden Pulssequenz aufgerufen. Der
	 * Parameter enthalt den Namen der Methode, die die Berechnungsfunktion fuer
	 * die aufrufende Pulsequenz enthaelt.
	 * @param method Der Name der Berechnungsmethode, die aufgerufen werden soll.
	 * @return Das manipulierte Intensitaetsbild.
	 */
	public int[][] calculate(String method) {
		String	seqName = sequence.getClass().getName();

		seqName = seqName.substring(seqName.lastIndexOf('.') + 1);

		// Hier wird eine Instanz der aufrufenden Pulssequenz erzeugt,
		// ggf. muss die Liste erweitert werden
		if (seqName.compareTo("SaturationRecovery") == 0) {
			SaturationRecovery	srSequence = (SaturationRecovery) sequence;

			// an dieser Stelle koennen wichtige Sequenzparameter ausgelesen werden,
			// z.B.
			// trTime = srSequence.getTRTime();
			// Später steht die Sequenz nicht mehr zur Verfuegung
		} 

		// Initialisieren des zurueckzuliefernden Intensitaetsbildes
		int[][] result = null;

		// Ueberpruefen, welche Methode aufgerufen werden soll,
		// ggf. erweitern
		if (method.compareTo("Artefact_Std") == 0) {
			result = artefact_Std();
		} 

		// Zurueckliefern des manipulierten Intensitaetsbildes
		return result;
	}		// Methode calculate
 


	/**
	 * Method declaration
	 * 
	 * 
	 * @return
	 * 
	 * @see
	 */
	public int[][] artefact_Std() {

		// Initialisieren des manipulierten Ergebnisbildes
		int[][] res = new int[256][256];

		// An dieser Stelle folgt die Implementierung der Artefakt-Simulation.
		// Man hat ueber "intensityimg" zugriff auf das Intensitaetsbild, das nach
		// der Berechnung der Pulssequenz entstanden ist.

		// Zurueckliefern des manipulierten Ergebnisbildes
		return res;
	}		// Methode motion_Std
 

}






/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

