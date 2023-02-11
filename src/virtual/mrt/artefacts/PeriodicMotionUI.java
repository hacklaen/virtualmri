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
 * Diese Klasse ist die Oberflaechenklasse fuer die Simualation eines
 * periodischen Bewegungs-Artefakts. Es kann eine x- und y-Amplidude, eine
 * Frequenz und ein Start- und Stopzeitpunkt der Bewegung angegeben werden.
 * <DL><DT><B>Modifications: </B><DD>
 * Thomas Hacklaender 2002.10.13:
 * Strings internationalisiert.<br>
 * </DD></DL>
 * @author   Christian Schalla
 * @author   Andreas Truemper
 * @version  1.2, 2002.10.13
 */
public class PeriodicMotionUI extends ArtefactUI {


	/**
	 * Referenz auf die Berechnungsklasse.
	 */
	PeriodicMotion						myManipulator;


	/**
	 * Bedienelemente fuer die x-Amplitude.
	 */
	private LabelTFLabelPanel pXAmp = new LabelTFLabelPanel(java.util.ResourceBundle.getBundle("virtual/mrt/artefacts/resources/artefacts_loc").getString("periodicmotionui.label.x.title"), "1", java.util.ResourceBundle.getBundle("virtual/mrt/artefacts/resources/artefacts_loc").getString("periodicmotionui.label.x.unit"));


	/**
	 * Bedienelemente fuer die y-Amplitude.
	 */
	private LabelTFLabelPanel pYAmp = new LabelTFLabelPanel(java.util.ResourceBundle.getBundle("virtual/mrt/artefacts/resources/artefacts_loc").getString("periodicmotionui.label.y.title"), "0", java.util.ResourceBundle.getBundle("virtual/mrt/artefacts/resources/artefacts_loc").getString("periodicmotionui.label.y.unit"));


	/**
	 * Bedienelemente fuer die Frequenz.
	 */
	private LabelTFLabelPanel pFreq = new LabelTFLabelPanel(java.util.ResourceBundle.getBundle("virtual/mrt/artefacts/resources/artefacts_loc").getString("periodicmotionui.label.frequency.title"), "5", java.util.ResourceBundle.getBundle("virtual/mrt/artefacts/resources/artefacts_loc").getString("periodicmotionui.label.frequency.unit"));


	/**
	 * Bedienelemente fuer den Startzeitpunkt der period. Bewegung.
	 */
	private LabelTFLabelPanel pStartTime = new LabelTFLabelPanel(java.util.ResourceBundle.getBundle("virtual/mrt/artefacts/resources/artefacts_loc").getString("periodicmotionui.label.start.title"), "0", java.util.ResourceBundle.getBundle("virtual/mrt/artefacts/resources/artefacts_loc").getString("periodicmotionui.label.start.unit"));


	/**
	 * Bedienelemente fuer den Stopzeitpunkt der period. Bewegung.
	 */
	private LabelTFLabelPanel pStopTime = new LabelTFLabelPanel(java.util.ResourceBundle.getBundle("virtual/mrt/artefacts/resources/artefacts_loc").getString("periodicmotionui.label.end.title"), "99999", java.util.ResourceBundle.getBundle("virtual/mrt/artefacts/resources/artefacts_loc").getString("periodicmotionui.label.end.unit"));


	/**
	 * Standardkonstruktor.
	 */
	public PeriodicMotionUI() {}


	/**
	 * Die Methode richtet die Oberflächenelmente ein und fuegt sie dem
	 * Darstellungspanel (myPanel) hinzu.
	 */
	public void jbInit() {

		// GUI-elemente der Artefakt-Karteikarte einrichten

		pXAmp.setBounds(5, 0, 255, 20);
		pYAmp.setBounds(5, 25, 255, 20);
		pFreq.setBounds(5, 50, 255, 20);
		pStartTime.setBounds(5, 75, 255, 20);
		pStopTime.setBounds(5, 100, 255, 20);

		myPanel.add(pXAmp);
		myPanel.add(pYAmp);
		myPanel.add(pFreq);
		myPanel.add(pStartTime);
		myPanel.add(pStopTime);

	}		// Methode jbInit
 

	/**
	 * Die Methode liest die aktuellen Parameterwerte aus den
	 * Bedienelementen aus und speichert sie in den entsprechenden Klassenvariablen.
	 */
	public void setValues() {}


	/**
	 * Die Methode setzt die Werte der Oberflaechenelmente auf die in den
	 * Klassenvariablen gespeicherten Werte. Dies wird beim Umschalten zwischen
	 * Artefakt-Simulatoren benötigt.
	 */
	public void getValues() {}


	/**
	 * Die Methode erzeugt eine neue Instanz der Berechnungsklasse des
	 * Artefakt-Simulators und liefert diese zurueck. Ausserdem werden die wie
	 * Einstellungen der Bedienelemente an die Berechnungsklasse uebergeben.
	 */
	public Artefact createManipulator() {
		myManipulator = new PeriodicMotion();
		myManipulator.setProgressBar(mainFrame.getProgressBar());
		myManipulator.setXAmplitude(pXAmp.getValue());
		myManipulator.setYAmplitude(pYAmp.getValue());
		myManipulator.setFrequency(pFreq.getValue());
		myManipulator.setStartTime(pStartTime.getValue());
		myManipulator.setStopTime(pStopTime.getValue());

		return myManipulator;
	} 

}		// Klasse PeriodicMotionUI






/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

