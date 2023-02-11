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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import virtual.tools.*;


/**
 * Diese Klasse ist die Oberflaechenklassen fuer Simualationen ohne Artefakte.
 * @author   Thomas Hacklaender
 * @version  1.0, 2000.8.20
 */
public class NoArtefactUI extends ArtefactUI {


	/**
	 * Referenz auf die Berechnungsklasse des Artefakt-Simulators.
	 */
	NoArtefact	myManipulator;


	/**
	 * Stadardkonstrukor. Nicht loeschen oder aendern.
	 */
	public NoArtefactUI() {}	// Standardkonstruktor


	/**
	 * Die Methode richtet die Oberflächenelemente ein und fuegt sie dem
	 * Darstellungsbereich (myPanel) hinzu. Hier muessen entsprechende Ergaenzungen
	 * vogenommen werden.
	 */
	public void jbInit() {}


	/**
	 * Die Methode wird beim Umschalten auf einen anderen Artefakt-Simulator
	 * aufgerufen. An dieser Stelle koennen die Einstellungen der Oberflaechenelmente
	 * gespeichert werden.
	 */
	public void setValues() {}


	/**
	 * Die Methode wird aufgerufen, wenn dieser Artefakt-Simulator aus der Auswahlbox
	 * ausgewählt wird. Zuvor gespeicherte Einstellungen des Simulators werden dann
	 * wiederhergestellt.
	 */
	public void getValues() {}


	/**
	 * Die Methode erzeugt eine neue Instanz der Berechnungsklasse dieses
	 * Artefakt-Simulators und liefert diese an die aufrufende Pulssequenz zurück.
	 * Nach der Erzeugung der Berechnungsklasse werden dieser die aktuell
	 * eingestellten Parameter mitgeteilt. Die Methode muss vorhanden sein und
	 * entsprechend angepasst werden.
	 */
	public Artefact createManipulator() {
		myManipulator = new NoArtefact();

		return myManipulator;
	} 

}






/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

