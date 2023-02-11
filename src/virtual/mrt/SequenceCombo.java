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

import virtual.mrt.sequences.*;


/**
 * Die Klasse verwaltet die Auswahlbox zur Auswahl der Pulssequenz.
 * Die Methode getSequences liest die Sequenzklassen und Sequenznamen aus der Property-Datei
 * aus und erzeugt ueber Reflections Instanzen der SequenzUI-Klassen.
 * Auch die Ereignisbehandlung der Auswahlbox findet in dieser Klasse statt. Jetzt
 * werden auch die eingestellten Werte ueber setValue in der SequenzUI-Klasse gespeichert
 * und ueber getValue wieder gelesen.
 * <DL><DT><B>Modifications: </B><DD>
 * Thomas Hacklaender 2000.8.17:
 * In der Methode getSequences den String strSequenceUI auf die neue Packagestruktur
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
public class SequenceCombo extends JComboBox {


	// th 2002.10.14
	// Locale					myLoc = new Locale("", "");
	// ResourceBundle	artefactsRsrc = PropertyResourceBundle.getBundle("vmrt_artefacts", myLoc);
	// ResourceBundle	artefactsRsrc = PropertyResourceBundle.getBundle("vmrt_artefacts");
	// th 2005.11.11
	// Resourcebundle moved to virtual/mrt/resources
	ResourceBundle		artefactsRsrc = PropertyResourceBundle.getBundle("virtual/mrt/resources/vmrt_artefacts");

	// Sequenzen aus der Resourcedatei einlesen
	// th 2002.10.14
	// ResourceBundle	sequencesRsrc = PropertyResourceBundle.getBundle("vmrt_sequences", myLoc);
	// ResourceBundle	sequencesRsrc = PropertyResourceBundle.getBundle("vmrt_sequences");
	// th 2005.11.11
	// Resourcebundle moved to virtual/mrt/resources
	ResourceBundle		sequencesRsrc = PropertyResourceBundle.getBundle("virtual/mrt/resources/vmrt_sequences");

	// th 2005.11.11, added
	ResourceBundle		frameRsrc = PropertyResourceBundle.getBundle("virtual/mrt/resources/vmrt_frame");


	/**
	 * Vektor zur Aufnahme der Pulssequenz-Klassen.
	 */
	private Vector		sequenceClasses;


	/**
	 * Referenz auf das Hauptfenster.
	 */
	private VMRTFrame mainFrame;


	/**
	 * Referenz auf das Einstellungsfenster fuer die Pulsseqeunzen.
	 */
	private JPanel		pSequenceSettingPanel;


	/**
	 * Referenz auf das Infofenster fuer die Pulsseqeunzen.
	 */
	private JPanel		pSequenceInfoPanel;


	/**
	 * Der Index der zuletzt selektierten Sequenz in der Auswahlbox.
	 */
	private int				oldindex;


	/**
	 * Der Konstruktor setzt die Referenzen zum Hauptfenster, zum Einstellungs- und
	 * zum Infofenster der Pulssequenzen. Ausserdem wird ein leerer Vektor zur
	 * Aufnahme der Pulssequenzen angelegt.
	 * @param frame Rueckreferenz zum Hauptfenster.
	 * @param seqSetpanel Referenz auf das Einstellungsfenster fuer die Pulssequenzen.
	 * @param seqInfoPanel Referenz auf das Infofenster fuer die Pulssequenzen.
	 */
	public SequenceCombo(VMRTFrame frame, JPanel seqSetPanel, JPanel seqInfoPanel) {
		mainFrame = frame;
		pSequenceSettingPanel = seqSetPanel;
		pSequenceInfoPanel = seqInfoPanel;
		sequenceClasses = new Vector(10, 5);
	}		// Konstruktor


	/**
	 * Die Methode laedt die Klassennamen der Sequenz-berechnungsklassen und die
	 * Sequenznamen aus der Property-Datei. Eine Instanz der Sequenz und deren UI-Klasse
	 * wird ueber den Namen der Sequenz in der Auswahlliste erzeugt. Eine Referenz
	 * auf die PulssequenzUI-Klasse wird im Vektor sequenceClasses gespeichert. Dieses Konzept
	 * ermoeglicht es, dem Programm ganz einfach neue Pulssequenzen hinzuzufuegen.
	 * Fuer jede Sequenz muessen zwei Klassen implementiert werden, eine UI-Klasse und
	 * eine Berechnungsklasse, anschliessend muessen Name und Klassenname der Berechnungsklasse
	 * in die Property-Datei eingetragen werden.
	 */
	public void getSequences() {

		String[]	kSpaceManipulators = new String[20];
		int				inumManipulators = 0;
		int				i = 0;

		for (i = 1; i <= 20; i++) {
			try {
				String	hlp = artefactsRsrc.getString("Manipulator_Class_" + i);

				hlp = hlp.replace('"', ' ');
				hlp = hlp.trim();
				kSpaceManipulators[i - 1] = hlp;
			} catch (Exception e) {
				break;
			} 
		} 

		// Geloescht: tha 2000.8.19
		// NoArtefact ist jetzt auch ein gueltiger Manipulator
		// if (inumManipulators = i > 0) {
		// i - 1;
		// } else {
		// 0;
		// }
		// 
		// inumManipulators = i > 0 ? i - 1 : 0;
		inumManipulators = i;


		for (i = 1; i <= 20; i++) {
			String					s = "Sequence_Class_" + i;
			String					w = null;
			String					seqName = null;
			PulsesequenceUI sequence;

			try {
				w = sequencesRsrc.getString(s);

				// Anfuehrungszeichen entfernen
				w = w.replace('"', ' ');
				w = w.trim();

				// Wenn der Name nicht gefunden wird, dann benutze den Klassennamen
				// zur Anzeige in der Combobox.
				try {
					seqName = sequencesRsrc.getString("Sequence_Name_" + i);
				} catch (Exception e) {
					seqName = w;
				} 
				;

				// Die Klasse zur k-Raum-Manipulation auslesen.
				// Diese Klassen implementieren die Bewegungssimulation.
				// Anfuehrungszeichen entfernen
				seqName = seqName.replace('"', ' ');
				seqName = seqName.trim();
				String	strSequenceUI = "virtual.mrt.sequences." + w + "UI";
				Object	mySequence = null;
				Class		seq = null;

				// Die Klasse ueber den Namen sequenceUIName ermitteln, mit Reflectionpaket.
				try {
					seq = Class.forName(strSequenceUI);
				} catch (Exception e) {
					e.printStackTrace();
				} 
				;

				// Neue Instanz dieser Klasse ableiten
				try {
					mySequence = seq.newInstance();
				} catch (Exception e) {
					e.printStackTrace();
				} 
				;

				// Frame und Sequenzpanel setzen
				sequence = (PulsesequenceUI) mySequence;
				sequence.setMainFrame(mainFrame);
				sequence.setSettingPanel(pSequenceSettingPanel);
				sequence.setInfoPanel(pSequenceInfoPanel);
				String	methodName = null;

				for (int x = 0; x < inumManipulators; x++) {
					try {
						methodName = sequencesRsrc.getString(kSpaceManipulators[x] + "_" + i);
					} catch (Exception e) {
						break;
					} 
					methodName = methodName.replace('"', ' ').trim();
					sequence.addProperty(kSpaceManipulators[x], methodName);
				} 

				mainFrame.repaint();

				// in Vektor eintragen
				sequenceClasses.addElement(sequence);
			} catch (Exception e) {

				// Resource nicht gefunden, Schleife beenden
				break;
			} 

			// String einfuegen (Namen des Sequenz, nicht die Klasse)
			this.addItem(seqName);
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
		PulsesequenceUI seq = (PulsesequenceUI) sequenceClasses.firstElement();

		seq.fillPanel();
		oldindex = 0;
	}		// Methode getSequences
 

	/**
	 * Die Methode wird aufgerufen, wenn eine neue Pulssequenz aus der Auswahlbox
	 * ausgewaehlt wird. Der Bereich zur Darstellung der Oberflaechenelemente der
	 * Pulssequenz wird dann geloescht und anschliessend mit den Elementen der
	 * neuen Pulssequenz neu gefuellt.
	 * @param e Das Ereignis beim Auswaehlen einer anderen Pulssequenz aus der Auswahlbox.
	 */
	private void cb_itemStateChanged(ItemEvent e) {
		PulsesequenceUI seq;

		seq = (PulsesequenceUI) sequenceClasses.elementAt(oldindex);

		// Werte abspeichern
		seq.setValues();
		pSequenceSettingPanel.removeAll();
		pSequenceSettingPanel.repaint();
		pSequenceInfoPanel.removeAll();
		pSequenceInfoPanel.repaint();
		int index = getSelectedIndex();

		oldindex = index;
		seq = (PulsesequenceUI) sequenceClasses.elementAt(index);
		seq.fillPanel();
		seq.getValues();

		// Der folgende Text in der Statusleiste wird eigentlich nur gesetzt, damit
		// ein repaint stattfindet, so dass die neuen Bedienelemente wirklich dargestellt
		// werden. Aufgrund eines Bugs im GridBagLayout wuerde dies sonst naemlich nicht
		// passieren. DAs repaint findet auch nur statt, wenn sich tatsaechlich etwas
		// geaendert hat. Daher die zweite komische Zeile ...
		mainFrame.setStatusBar(frameRsrc.getString("statusbar.note.start"));
		mainFrame.setStatusBar(mainFrame.getStatusText() + " ");
	}		// Methode cb_itemStateChanged(ItemEvent e)
 
}






/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

