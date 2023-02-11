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
 * Diese Klasse ist die Berechnungsklasse fuer Simualationen ohne Artefakte.
 * @author   Thomas Hacklaender
 * @version  1.0, 2000.8.20
 */
public class NoArtefact extends Artefact {


	/**
	 * Standardkonstruktor.
	 */
	public NoArtefact() {}


	/**
	 * Diese Methode wird immer von der aufrufenden Pulssequenz aufgerufen. Der
	 * Parameter enthalt den Namen der Methode, die die Berechnungsfunktion fuer
	 * die aufrufende Pulsequenz enthaelt.
	 * @param method Der Name der Berechnungsmethode, die aufgerufen werden soll.
	 * @return Das manipulierte Intensitaetsbild.
	 */
	public int[][] calculate(String method) {

		// An dieser Stelle folgt die Implementierung der Artefakt-Simulation.
		// Man hat ueber "intensityimg" zugriff auf das Intensitaetsbild, das nach
		// der Berechnung der Pulssequenz entstanden ist.

		return intensityimg;
	} 

}




/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

