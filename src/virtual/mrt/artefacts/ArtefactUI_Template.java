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
 * Die Klasse dient als Vorlage zur Erzeugung neuer Oberflaechenklassen fuer
 * Artefakt-Simulatoren.
 * 
 * @author   Christian Schalla
 * @author   Andreas Truemper
 * @version  1.0, 15. Oktober 1999
 */
public class ArtefactUI_Template extends ArtefactUI {


	/**
	 * Referenz auf die Berechnungsklasse des Artefakt-Simulators.
	 * Der Klassenname "Artefact_Template" muss angepasst werden.
	 */
	Artefact_Template myManipulator;

	// Hier folgt die Erzeugung der Oberflaechenelmente des Artefakt-Simulators, z.B:
	// private tools.LabelTFLabelPanel pXSpeed    = new tools.LabelTFLabelPanel("x-Geschwindigkeit:","1","Pix/min");


	/**
	 * Stadardkonstrukor. Nicht loeschen oder aendern.
	 */
	public ArtefactUI_Template() {}		// Standardkonstruktor


	/**
	 * Die Methode richtet die Oberflächenelemente ein und fuegt sie dem
	 * Darstellungsbereich (myPanel) hinzu. Hier muessen entsprechende Ergaenzungen
	 * vogenommen werden.
	 */
	public void jbInit() {

		// GUI-Elemente der Artefakt-Karteikarte einrichten, z.B.:
		// pXSpeed.setBounds(5, 0, 255, 20);
		// myPanel.add(pXSpeed);

	}																	// Methode jbInit
 

	/**
	 * Die Methode wird beim Umschalten auf einen anderen Artefakt-Simulator
	 * aufgerufen. An dieser Stelle koennen die Einstellungen der Oberflaechenelmente
	 * gespeichert werden.
	 */
	public void setValues() {}				// Methode setValues


	/**
	 * Die Methode wird aufgerufen, wenn dieser Artefakt-Simulator aus der Auswahlbox
	 * ausgewählt wird. Zuvor gespeicherte Einstellungen des Simulators werden dann
	 * wiederhergestellt.
	 */
	public void getValues() {}				// Methode getValue


	/**
	 * Die Methode erzeugt eine neue Instanz der Berechnungsklasse dieses
	 * Artefakt-Simulators und liefert diese an die aufrufende Pulssequenz zurück.
	 * Nach der Erzeugung der Berechnungsklasse werden dieser die aktuell
	 * eingestellten Parameter mitgeteilt. Die Methode muss vorhanden sein und
	 * entsprechend angepasst werden.
	 */
	public Artefact createManipulator() {
		myManipulator = new Artefact_Template();

		myManipulator.setProgressBar(mainFrame.getProgressBar());

		// myManipulator.setXSpeed(pXSpeed.getValue());

		return myManipulator;
	}		// Methode createManipulator()
 
}			// Klasse MotionUI






/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

