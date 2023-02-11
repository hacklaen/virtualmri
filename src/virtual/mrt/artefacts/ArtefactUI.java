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
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

import virtual.mrt.*;


/**
 * Diese Klasse ist die abstrakte Oberklasse fuer alle Oberflaechenklassen fuer
 * Artefakt-Simulatoren. Sie stellt insbesondere eine Referenz auf den Bereich
 * zur Verfuegung, in das die Oberflaechenelemente hineingezeichnet werden koennen.
 * 
 * @author   Christian Schalla
 * @author   Andreas Truemper
 * @version  1.0, 15. Oktober 1999
 */
public abstract class ArtefactUI {


	/**
	 * Referenz auf den Bereich, in dem die Oberflaechenelemente des Artefakt-Simulators
	 * dargestellt werden koennen.
	 */
	protected JPanel	myPanel;


	/**
	 * Referenz auf das Hauptfenster.
	 */
	public VMRTFrame	mainFrame;


	/**
	 * Die Variable merkt sich, ob die Ereignisbehandlungsroutinen der Bedienelemente
	 * schon initialisiert wurden. Ist dies der Fall, so werden sie nicht erneut
	 * initialisiert, da ansonsten immer mehrere Ereignisse gleichzeitig ausgeloest
	 * werden.
	 */
	private boolean		isInitialized = false;


	/**
	 * Standardkonstruktor.
	 */
	public ArtefactUI() {}	// Standardkonstruktor


	/**
	 * Die Methode setzt die Referenz auf den Bereich, in dem die Oberflaechenelemente
	 * des Artefakt-Simulators dargestellt werden koennen.
	 * @param p Referenz auf den Bereich zur Darstellung der Bedienelemente.
	 */
	public void setUIPanel(JPanel p) {
		myPanel = p;
	}		// Methode set SettingPanel
 

	/**
	 * Die Methode setzt die Referenz auf das Hauptfenster.
	 * @param vmrt Die Referenz auf das Hauptfenster.
	 */
	public void setMainFrame(VMRTFrame vmrt) {
		mainFrame = vmrt;
	}		// Methode setMainFrame
 

	/**
	 * Die Methode setzt die Werte der Oberflaechenelemente auf die in den
	 * Klassenvariablen gespeicherten Werte. Dies wird beim Umschalten zwischen
	 * Artefakt-Simulatoren benötigt, um die eingestellten Werte nicht zu verlieren.
	 * In dieser Oberklasse hat die Methode allerdings keine Funktionalität.
	 */
	public abstract void getValues();


	/**
	 * Die Methode liest die aktuellen Parameterwerte aus den
	 * Bedienelementen aus und speichert sie in den entsprechenden Klassenvariablen.
	 * In dieser Oberklasse weist die Methode allerdings keine Funktionalitaet auf.
	 */
	public abstract void setValues();


	/**
	 * Die Methode stellt die Bedienelemente des Artefakt-Simulators im dafuer
	 * vorgesehenen Bereich dar.
	 */
	public void fillPanel() {
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		} 
	}		// Methode fillPanel
 

	/**
	 * Die Methode ist in dieser abstrakten Oberklasse leer. Sie wird in den
	 * abgeleiteten Klassen dazu verwendet, um weitere Bedien- und Informationselemente
	 * einzurichten.
	 */
	void jbInit() throws Exception {}


	/**
	 * Die Methode erzeugt eine neue Instanz der Berechnungsklasse des
	 * Artefakt-Simulators und liefert diese zurueck. In dieser Oberklasse
	 * hat die Methode allerdings keine Funktionalitaet.
	 */
	public abstract Artefact createManipulator();


}






/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

