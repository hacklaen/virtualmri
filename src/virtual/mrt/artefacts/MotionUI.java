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
 * Diese Klasse ist die Oberflaechenklasse zur Simualation eines Bewegungs-Artefakts.
 * Dabei beschraenkt sich die Bewegung auf eine einfache Translation. Es kann
 * ein Start- und ein Stopzeitpunkt angegeben werden und die Bewegungsgeschwindigkeit
 * fuer x- und y-Richtung separat festgelegt werden.
 * <DL><DT><B>Modifications: </B><DD>
 * Thomas Hacklaender 2002.10.13:
 * Strings internationalisiert.<br>
 * </DD></DL>
 * @author   Christian Schalla
 * @author   Andreas Truemper
 * @version  1.2, 2002.10.13
 */
public class MotionUI extends ArtefactUI {


	/**
	 * Referenz auf die Berechnungsklasse.
	 */
	Motion										myManipulator;


	/**
	 * Bedienelemente für die Bewegungsgeschwindigkeit in x-Richtung.
	 */
	private LabelTFLabelPanel pXSpeed = new LabelTFLabelPanel(java.util.ResourceBundle.getBundle("virtual/mrt/artefacts/resources/artefacts_loc").getString("motionui.label.x.title"), "1", java.util.ResourceBundle.getBundle("virtual/mrt/artefacts/resources/artefacts_loc").getString("motionui.label.x.unit"));


	/**
	 * Bedienelemente für die Bewegungsgeschwindigkeit in y-Richtung.
	 */
	private LabelTFLabelPanel pYSpeed = new LabelTFLabelPanel(java.util.ResourceBundle.getBundle("virtual/mrt/artefacts/resources/artefacts_loc").getString("motionui.label.y.title"), "0", java.util.ResourceBundle.getBundle("virtual/mrt/artefacts/resources/artefacts_loc").getString("motionui.label.y.unit"));


	/**
	 * Bedienelemente für den Startzeitpunkt der Translation.
	 */
	private LabelTFLabelPanel pStartTime = new LabelTFLabelPanel(java.util.ResourceBundle.getBundle("virtual/mrt/artefacts/resources/artefacts_loc").getString("motionui.label.start.title"), "0", java.util.ResourceBundle.getBundle("virtual/mrt/artefacts/resources/artefacts_loc").getString("motionui.label.start.unit"));


	/**
	 * Bedienelemente für den Stopzeitpunkt der Translation.
	 */
	private LabelTFLabelPanel pStopTime = new LabelTFLabelPanel(java.util.ResourceBundle.getBundle("virtual/mrt/artefacts/resources/artefacts_loc").getString("motionui.label.end.title"), "99999", java.util.ResourceBundle.getBundle("virtual/mrt/artefacts/resources/artefacts_loc").getString("motionui.label.end.unit"));


	/**
	 * Standardkonstruktor.
	 */
	public MotionUI() {}


	/**
	 * Die Methode richtet die Oberflächenelemente ein und fuegt sie dem
	 * Darstellungsbereich (myPanel) hinzu.
	 */
	public void jbInit() {

		// GUI-Elemente der Artefakt-Karteikarte einrichten
		pXSpeed.setBounds(5, 0, 255, 20);
		pYSpeed.setBounds(5, 25, 255, 20);
		pStartTime.setBounds(5, 50, 255, 20);
		pStopTime.setBounds(5, 75, 255, 20);

		myPanel.add(pXSpeed);
		myPanel.add(pYSpeed);
		myPanel.add(pStartTime);
		myPanel.add(pStopTime);
	}		// Methode jbInit
 

	/**
	 * Die Methode liest die aktuellen Parameterwerte aus den
	 * Bedienelementen aus und speichert sie in den entsprechenden Instanzvariablen.
	 */
	public void setValues() {}


	/**
	 * Die Methode setzt die Werte der Oberflaechenelemente auf die in den
	 * Instanzvariablen gespeicherten Werte. Dies wird beim Umschalten zwischen
	 * Artefakt-Simulatoren benötigt.
	 */
	public void getValues() {}


	/**
	 * Die Methode erzeugt eine neue Instanz der Berechnungsklasse des
	 * Artefakt-Simulators und liefert diese zurueck. Ausserdem werden die
	 * Einstellungen der Bedienelemente an die Berechnungsklasse uebergeben.
	 */
	public Artefact createManipulator() {

		// Erzeugen einer neuen Instanz der Berechnungsklasse
		myManipulator = new Motion();

		// Werte der bedienelemenet auslesen udn an Berechnungsklasse uebergeben
		myManipulator.setProgressBar(mainFrame.getProgressBar());
		myManipulator.setXSpeed(pXSpeed.getValue());
		myManipulator.setYSpeed(pYSpeed.getValue());
		myManipulator.setStartTime(pStartTime.getValue());
		myManipulator.setStopTime(pStopTime.getValue());

		return myManipulator;
	}		// Methode creatManipulator
 
}			// Klasse MotionUI






/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

