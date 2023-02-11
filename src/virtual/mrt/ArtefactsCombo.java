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
package virtual.mrt;


import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

import virtual.mrt.artefacts.*;


/**
 * Die Klasse verwaltet die Auswahlbox, zur Auswahl der Artefakt-Simulatoren.
 * Die Methode getArtefacts liest die Artefaktklassen und Artefaktnamen aus der Property-Datei
 * aus und erzeugt ueber Reflections Instanzen der ArtefactUI-Klassen.
 * Auch die Ereignisbehandlung der Auswahlbox findet in dieser Klasse statt. Beim
 * Wehcseln des artefakt-Simulators werden die eingestellten Werte ueber "setValue"
 * in der ArtefactUI-Klasse gespeichert und ueber "getValue" wiederhergestellt.
 * <DL><DT><B>Modifications: </B><DD>
 * Thomas Hacklaender 2000.8.20:
 * Einen Fehler korrigiert: Nachdem man einen Artefakt ausgewaehlt hatte,
 * war die Auswahl 'Kein Artefakt' wirkungslos. Lösung: Fuer 'Kein Artefakt'
 * wurde die neue Klasse NoArtefact erstellt. Sie wurde auch als gueltiger Artefakt
 * in die Property Datei vmrt_artefacts.properties aufgenommen. Damit fallen
 * die Korrekturen der Indizes in getArtefacts, getSelectedArtefactUI weg.
 * Der neue "Artefakt" wurde auch in die Property Datei der Pulssequenzen
 * vmrt_sequences.properties aufgenommen.<br>
 * Thomas Hacklaender 2000.8.17:
 * In der Methode getArtefacts den String strArtefactUI auf die neue Packagestruktur
 * geaendert.<br>
 * Thomas Hacklaender 2002.10.13:
 * Strings internationalisiert.<br>
 * Thomas Hacklaender 2002.10.14:
 * Resource Bundles fuer die Property-Dateien benoetigen nicht die Angabe einer Locale.<br>
 * </DD></DL>
 * @author   Christian Schalla
 * @author   Andreas Truemper
 * @version  1.2, 2002.10.14
 */
public class ArtefactsCombo extends JComboBox {


	// Artefakte aus der Resourcedatei einlesen
	// th 2002.10.14
	// Locale					myLoc = new Locale("", "");
	// ResourceBundle	artefactsRsrc = PropertyResourceBundle.getBundle("vmrt_artefacts", myLoc);
	// ResourceBundle	artefactsRsrc = PropertyResourceBundle.getBundle("vmrt_artefacts");
	// th 2005.11.11
	// Resourcebundle moved to virtual/mrt/resources
	ResourceBundle		artefactsRsrc = PropertyResourceBundle.getBundle("virtual/mrt/resources/vmrt_artefacts");

	// th 2005.11.11, added
	ResourceBundle		frameRsrc = PropertyResourceBundle.getBundle("virtual/mrt/resources/vmrt_frame");


	/**
	 * Vektor zur Aufnahme der Artefakt-Klassen.
	 */
	private Vector		artefactClasses;


	/**
	 * Referenz auf das Hauptfenster.
	 */
	private VMRTFrame mainFrame;


	/**
	 * Referenz auf den Bereich, in dem die GUI-Elemente fuer die Artefaktsimulation
	 * dargestellt werden koennen.
	 */
	private JPanel		pArtefactUIPanel;


	/**
	 */
	private int				oldindex;


	/**
	 * Der Konstruktor setzt die Referenzen zum Hauptfenster und zum Panel, in dem
	 * die GUI-Elemente der Artefaktsimulation dargestellt werden koennen.
	 * Ausserdem wird ein leerer Vektor zur Aufnahme der Artefaktsimulatoren angelegt.
	 * @param frame Rueckreferenz zum Hauptfenster.
	 * @param seqSetpanel Referenz auf den Darstellungsbereich fuer die Bedienelemente
	 * der Artefakt-Simulatoren.
	 */
	public ArtefactsCombo(VMRTFrame frame, JPanel seqSetPanel) {
		mainFrame = frame;
		pArtefactUIPanel = seqSetPanel;
		artefactClasses = new Vector(10, 5);
	}		// Konstruktor


	/**
	 * Die Methode laedt die Artefaktklassenamen und die Artefaktnamen aus der
	 * Property-Datei. Eine Instanz des Artefakts und dessen GUI-Klasse
	 * wird ueber den Namen des Artefakts in der Auswahlliste erzeugt. Eine Referenz
	 * auf die ArtefactUI-Klasse wird im Vektor artefactClasses gespeichert. Dieses Konzept
	 * ermoeglicht es, dem Programm ganz einfach neue Artefakt-Simulatoren hinzuzufuegen.
	 * Fuer jedes Artefakt muessen zwei Klassen implementiert werden, eine UI-Klasse und
	 * eine Berechnungsklasse, anschliessend muessen Name des Artefakts und Klassenname
	 * der Berechnungsklasse in die Property-Datei eingetragen werden.
	 */
	public void getArtefacts() {

		for (int i = 1; i <= 20; i++) {
			String			s = "Manipulator_Class_" + i;
			String			w = null;
			String			artName = null;
			ArtefactUI	artefact;

			try {
				w = artefactsRsrc.getString(s);

				// Anfuehrungszeichen entfernen
				w = w.replace('"', ' ');
				w = w.trim();

				// Wenn der Name nicht gefunden wird, dann benutze den Klassennamen
				// zur Anzeige in der Combobox.
				try {
					artName = artefactsRsrc.getString("Manipulator_Name_" + i);
				} catch (Exception e) {
					artName = w;
				} 

				// Anfuehrungszeichen entfernen
				artName = artName.replace('"', ' ');
				artName = artName.trim();

				// Geaendert: tha 2000.8.17
				// String	strArtefactUI = "artefactsRsrc." + w + "UI";
				String	strArtefactUI = "virtual.mrt.artefacts." + w + "UI";

				Object	myArtefact = null;
				Class		art = null;

				// Die Klasse ueber den Namen sequenceUIName ermitteln, mit Reflectionpaket.
				try {
					art = Class.forName(strArtefactUI);
				} catch (Exception e) {
					e.printStackTrace();
				} 

				// Neue Instanz dieser Klasse ableiten
				try {
					myArtefact = art.newInstance();
				} catch (Exception e) {
					e.printStackTrace();
				} 

				// Frame und Sequenzpanel setzen
				artefact = (ArtefactUI) myArtefact;
				artefact.setMainFrame(mainFrame);
				artefact.setUIPanel(pArtefactUIPanel);
				mainFrame.repaint();

				// in Vektor eintragen
				artefactClasses.addElement(artefact);
			} catch (Exception e) {

				// Resource nicht gefunden, Schleife beenden
				break;
			} 

			// String einfuegen (Namen des Sequenz, nicht die Klasse)
			this.addItem(artName);
		} 
		addItemListener(new java.awt.event.ItemListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void itemStateChanged(ItemEvent e) {
				cb_itemStateChanged(e);
			} 

		});
		oldindex = 0;
	}		// Methode getSequences
 

	/**
	 * Die Methode wird aufgerufen, wenn ein neuer Artefakt-Simulator aus der Auswahlbox
	 * ausgewaehlt wird. Der Bereich zur Darstellung der Oberflaechenelemente des
	 * Artefakt-Simulators wird dann geloescht und anschliessend mit den Elementen des
	 * neuen Simulators neu gefuellt.
	 * @param e Das Ereignis beim Auswaehlen eines anderen Artefakt-Simulators aus der Auswahlbox.
	 */
	private void cb_itemStateChanged(ItemEvent e) {

		pArtefactUIPanel.removeAll();
		pArtefactUIPanel.repaint();

		ArtefactUI	art;

		try {

			// Geaendert: tha 2000.8.20
			// 'NoArtefact' ist als Artefakt in die Proüperty Datei aufgenommen.
			// Der Index muss dehalb nicht mehr korrigiert werden.
			// art = (ArtefactUI) artefactClasses.elementAt(oldindex - 1);
			art = (ArtefactUI) artefactClasses.elementAt(oldindex);

			// Werte abspeichern
			art.setValues();
		} catch (Exception err) {}


		int index = getSelectedIndex();

		oldindex = index;
		try {

			// Geaendert: tha 2000.8.20
			// 'NoArtefact' ist als Artefakt in die Proüperty Datei aufgenommen.
			// Der Index muss dehalb nicht mehr korrigiert werden.
			// art = (ArtefactUI) artefactClasses.elementAt(index - 1);
			art = (ArtefactUI) artefactClasses.elementAt(index);

		} catch (Exception err) {
			return;
		} 

		art.fillPanel();
		art.getValues();

		// Der folgende Text in der Statusleiste wird eigentlich nur gesetzt, damit
		// ein repaint stattfindet, so dass die neuen Bedienelemente wirklich dargestellt
		// werden. Aufgrund eines Bugs im GridBagLayout wuerde dies sonst naemlich nicht
		// passieren. DAs repaint findet auch nur statt, wenn sich tatsaechlich etwas
		// geaendert hat. Daher die zweite komische Zeile ...
		mainFrame.setStatusBar(frameRsrc.getString("statusbar.note.start"));
		mainFrame.setStatusBar(mainFrame.getStatusText() + " ");
	}		// Methode cb_itemStateChanged(ItemEvent e)
 

	/**
	 * Die Methode liefert eine Referenz auf die UI-Klasse des in der Auswahlbox
	 * selektierten Artefakt-Simulators.
	 * @return Referenz auf die UI-Klasse des ausgewählten Artefakts.
	 */
	public ArtefactUI getSelectedArtefactUI() {
		int index = getSelectedIndex();

		try {

			// Geaendert: tha 2000.8.20
			// 'NoArtefact' ist als Artefakt in die Proüperty Datei aufgenommen.
			// Der Index muss dehalb nicht mehr korrigiert werden.
			// return (ArtefactUI) artefactClasses.elementAt(index - 1);
			return (ArtefactUI) artefactClasses.elementAt(index);
		} catch (Exception err) {
			return null;
		} 

	}		// Methode getSelectedArtefact()
 
}






/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

