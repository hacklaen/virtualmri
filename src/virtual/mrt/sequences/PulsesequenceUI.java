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
package virtual.mrt.sequences;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.Properties;

import virtual.mrt.*;
import virtual.mrt.artefacts.*;
import virtual.tools.*;


/**
 * Diese Klasse ist eine abstrakte Oberklasse fuer alle Pulssequenz-Oberflaechenklassen.
 * Die Klasse stellt einige Standardmethoden zur Verfuegung, die jede Unterklasse
 * ueberschreiben muss. Das stellt sicher, das Erweiterungen dieser Klasse auf
 * jeden Fall im  Hauptfenster dargestellt werden koennen.
 * 
 * <DL><DT><B>Modifications: </B><DD>
 * tha 2000.05.14: tbSettings, tbPhOS und tbFreqOS von JToggleButton in
 * JCheckBox geaendert <br>
 * tha 2000.05.14: Neue Listener Methoden fuer tbPhOS, tbFreqOS und pRect
 * implementiert. <br>
 * Thomas Hacklaender 2000.05.14:
 * Defaultwert von tbPhOS und tbFreqOS ist false. <br>
 * Thomas Hacklaender 2000.05.14:
 * getSNRatio neu definiert. <br>
 * Thomas Hacklaender 2002.10.13:
 * Strings internationalisiert.<br>
 * </DD></DL>
 * @author   Christian Schalla
 * @author   Andreas Truemper
 * @version  1.2, 2002.10.13
 */
public abstract class PulsesequenceUI {


	/**
	 * Einheit fuer Minutenangaben.
	 */
	static private String		MINUTE_UNIT = java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("pulssequenceui.minute.unit");


	/**
	 * Einheit fuer Sekundenangaben.
	 */
	static private String		SECOND_UNIT = java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("pulssequenceui.second.unit");


	/**
	 * Einheit fuer Pixelgroesse.
	 */
	static private String		PIXEL_UNIT = java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("pulssequenceui.label.pixelsize.unit");


	/**
	 * Knopf, um die Berechnung des Intensitsetsbildes zu Starten.
	 */
	JButton									pbStart = new JButton();


	/**
	 * Knopf, um die Berechnung des Intensitaetsbildes zu Stoppen.
	 */
	JButton									pbStop = new JButton();


	// tha 2000.05.14: JToggleButton in JCheckBox geaendert


	/**
	 * Knopf zum Einblenden der Parametereinstellungen.
	 */
	JCheckBox								tbSettings = new JCheckBox();


	/**
	 * Beschriftung zur Auswahl der Spule.
	 */
	JLabel									lCoil = new JLabel();


	/**
	 * Auswahlbox zur Auswahl der Spule.
	 */
	JComboBox								cbCoil = new JComboBox();


	/**
	 * Elemente zur Einstellung der Bildmatrixgroesse.
	 */
	LabelTFLabelPanel				pMatrix = new LabelTFLabelPanel(java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("pulssequenceui.label.matrix.title"), "256", java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("pulssequenceui.label.matrix.unit"));


	/**
	 * Elemente zur Einstellung des Field of View (FOV).
	 */
	LabelTFLabelPanel				pFOV = new LabelTFLabelPanel(java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("pulssequenceui.label.fov.title"), "256", java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("pulssequenceui.label.fov.unit"));


	/**
	 * Elemente zur Einstellung der Bildproportionen.
	 */
	LabelTFLabelPanel				pRect = new LabelTFLabelPanel(java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("pulssequenceui.label.rectfov.title"), "8", java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("pulssequenceui.label.rectfov.unit"));


	/**
	 * Elemente zur Einstellung der Schichtdicke.
	 */
	LabelTFLabelPanel				pThickness = new LabelTFLabelPanel(java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("pulssequenceui.label.thickness.title"), "6", java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("pulssequenceui.label.thickness.unit"));


	/**
	 * Elemente zur Einstellung der Anzahl der Anregungen.
	 */
	LabelTFLabelPanel				pNEX = new LabelTFLabelPanel(java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("pulssequenceui.label.nex.title"), "1", "");


	/**
	 * Beschriftung des Knopfes zum Ein- und Ausschalten des Phasenoversamplings.
	 */
	JLabel									lPhOS = new JLabel();


	/**
	 * Beschriftung des Knopfes zum Ein- und Ausschalten des Frequenzoversamplings.
	 */
	JLabel									lFreqOS = new JLabel();


	// tha 2000.05.14: JToggleButton in JCheckBox geaendert


	/**
	 * Knopf zur Ein- und Ausschalten des Phasenoversamplings.
	 */
	JCheckBox								tbPhOS = new JCheckBox();


	// tha 2000.05.14: JToggleButton in JCheckBox geaendert


	/**
	 * Knopf zur Ein- und Ausschalten des Frequenzoversamplings.
	 */
	JCheckBox								tbFreqOS = new JCheckBox();


	/**
	 * Elemente zur Anzeige der Gesamtberechnungzeit.
	 */

	// ThreeLabelPanel		pTotalTime = new ThreeLabelPanel(java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("pulssequenceui.label.measurementtime.title"), " 0 " + MINUTE_UNIT + " 0 " + SECOND_UNIT, "");
	ThreeLabelPanel					pTotalTime = new ThreeLabelPanel(java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("pulssequenceui.label.measurementtime.title"), "--", "");


	/**
	 * Elemente zur Anzeige der Restberechnungzeit.
	 */

	// ThreeLabelPanel		pRemainingTime = new ThreeLabelPanel(java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("pulssequenceui.label.resttime.title"), " 0 " + MINUTE_UNIT + " 0 " + SECOND_UNIT, "");
	ThreeLabelPanel					pRemainingTime = new ThreeLabelPanel(java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("pulssequenceui.label.resttime.title"), "--", "");


	/**
	 * Elemente zur Anzeige der Pixelgroesse.
	 */

	// ThreeLabelPanel		pPixelSize = new ThreeLabelPanel(java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("pulssequenceui.label.pixelsize.title"), "--", java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("pulssequenceui.label.pixelsize.unit"));
	ThreeLabelPanel					pPixelSize = new ThreeLabelPanel(java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("pulssequenceui.label.pixelsize.title"), "--", "");


	/**
	 * Elemente zur Anzeige des Signal-zu-Rausch-Verhaeltnisses.
	 */

	// ThreeLabelPanel		pSNRatio = new ThreeLabelPanel(java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("pulssequenceui.label.sn.title"), "--", "");
	ThreeLabelPanel					pSNRatio = new ThreeLabelPanel(java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("pulssequenceui.label.sn.title"), "--", "");


	/**
	 * Referenz auf den Bereich, in dem die Oberflaechenelemente der Pulssequenz
	 * dargestellt werden koennen.
	 */
	protected JPanel				myPanel;


	/**
	 * Referenz auf den Bereich, in dem die Informationselemente der Pulssequenz
	 * dargestellt werden koennen.
	 */
	protected JPanel				myInfoPanel;


	/**
	 * Referenz auf einen Artefakt-Simulator.
	 */
	protected Artefact			artefact;


	/**
	 * Referenz auf das Hauptfenster.
	 */
	protected VMRTFrame			mainFrame;


	/**
	 * Eigenschaftsfeld für Methodenaufrufe für k-Raummanipulationen.
	 * Beispiel: Bewegungsartefakte werden durch die Klasse "Motion" implementiert,
	 * abhängig von der Pulssequenz werden jedoch verschieden Methoden aufgerufen.
	 * Über das Hauptfenster wird der Klassenname des gewählten Manipulators bestimmt,
	 * dann wird aus dieser Tabelle die aufzurufende Methode ausgelesen.
	 * Ein Eintrag wäre dann "Motion_1=Motion_Std". Damit wird in Motion die Methode
	 * Motion_Std aufgerufen.
	 */
	protected Properties		kSpaceManipulator = new Properties();


	/**
	 * Die Variable merkt sich, ob die Listener der Bedienelemente schon initialisiert
	 * wurden. Das verhindert eine doppelte Initialisierung und somit ein doppeltes
	 * Ausfuehren der Ereignisroutinen, wenn die entsprechenden Ereignisse eintreten.
	 */
	private boolean					isInitialized = false;


	/**
	 * Referenz auf die Pulssequenz-Berechnungsklasse.
	 */
	protected Pulsesequence sequence;


	/**
	 * Standardkonstruktor.
	 */
	public PulsesequenceUI() {}		// Standardkonstruktor


	/**
	 * Die Methode setzt die Referenz auf den Bereich, in dem die Oberflaechenelemente
	 * der Pulssequenz dargestellt werden koennen.
	 * @param sequencePanel Referenz auf den Bereich zur Darstellung der Einstellungselemente.
	 */
	public void setSettingPanel(JPanel sequencePanel) {
		myPanel = sequencePanel;
		myPanel.setLayout(null);
	}		// Methode set SettingPanel
 

	/**
	 * Die Methode setzt die Referenz auf den Bereich, in dem die Informations-
	 * elemente der Pulssequenz dargestellt werden koennen.
	 * @param sequencePanel Referenz auf den Bereich zur Darstellung der Informationselemente.
	 */
	public void setInfoPanel(JPanel sequenceInfoPanel) {
		myInfoPanel = sequenceInfoPanel;
	}		// Methode setInfoPanel
 

	/**
	 * Die Methode setzt die Referenz auf das Hauptfenster.
	 * @param vmrt Die Referenz auf das Hauptfenster.
	 */
	public void setMainFrame(VMRTFrame vmrt) {
		mainFrame = vmrt;
	}		// Methode setMainFrame
 

	/**
	 * Die Methode setzt die Einstellungen der Bedienelemente der Sequenz auf die in
	 * den entsprecheden Klassenvariablen gespeicherten Werte. Die Methode wird beim
	 * Umschalten zwischen den verschiedenen Pulssequenzen verwendet, um zuvor
	 * gemachte Einstellungen wiederherzustellen. In dieser Oberklasse
	 * weist die Methode allerdings keine Funktionalitaet auf.
	 */
	public abstract void getValues();


	/**
	 * Die Methode liest die aktuellen Sequenzparameterwerte aus den
	 * Bedienelementen aus und speichert sie in den entsprechenden Instanzvariablen.
	 * Die Methode wird beim Umschalten zwischen den verschiedenen Pulssequenzen
	 * verwendet, um die Einstellungen der Bedienelemente zu sichern und sie bei
	 * erneuter Auswahl der Pulssequenz ggf. wieder herstellen zu koennen.
	 * In dieser Oberklasse weist die Methode allerdings keine Funktionalitaet auf.
	 */
	public abstract void setValues();


	/**
	 * Die Methode fuegt einige Bedien- und Informationselemente in die Einstellungs- und
	 * Informationsbereiche, die fuer alle Pulssequenzen sinnvoll sind.
	 */
	public void fillPanel() {

		// Elemente zur Spulenauswahl einrichten
		lCoil.setText(java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("pulssequenceui.label.coil.title"));
		lCoil.setBounds(270, 7, 100, 20);
		myPanel.add(lCoil);
		cbCoil.setBounds(390, 7, 115, 20);
		if (isInitialized == false) {
			cbCoil.addItem(java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("pulssequenceui.label.coil.organ"));
			cbCoil.addItem(java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("pulssequenceui.label.coil.body"));
		} 
		myPanel.add(cbCoil);

		// Elemente zur Einstellung der Matrixgroesse einrichten
		pMatrix.setBounds(270, 31, 220, 20);
		pMatrix.setRange(256, 256);
		pMatrix.getTextFieldReference().setEnabled(false);
		myPanel.add(pMatrix);

		// Elemente zur Einstellung des FOV einrichten
		pFOV.setBounds(270, 55, 220, 20);
		pFOV.setRange(32, 256);
		myPanel.add(pFOV);

		// Elemente zur Einstellung der Bildproportionen einrichten
		pRect.setBounds(270, 79, 220, 20);
		pRect.setRange(4, 8);
		myPanel.add(pRect);

		// Elemente zur Einstellung der Schichtdicke einrichten
		pThickness.setBounds(270, 103, 220, 20);
		pThickness.setRange(1, 20);
		myPanel.add(pThickness);

		// Elemente zur Einstellung der Anzahl der Anregungen einrichten
		pNEX.setBounds(270, 127, 220, 20);
		pNEX.setRange(1, 16);
		myPanel.add(pNEX);

		// Elemente zur Einstellung des Phasenoversamplings einrichten
		lPhOS.setBounds(270, 151, 220, 20);
		lPhOS.setText(java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("pulssequenceui.label.oversampling.phase.title"));
		myPanel.add(lPhOS);

		tbPhOS.setBounds(390, 151, 55, 20);
		tbPhOS.setSelected(false);
		myPanel.add(tbPhOS);

		// Elemente zur Einstellung des Frequenzoversamplings einrichten
		lFreqOS.setBounds(270, 175, 220, 20);
		lFreqOS.setText(java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("pulssequenceui.label.oversampling.frequency.title"));
		myPanel.add(lFreqOS);

		tbFreqOS.setBounds(390, 175, 55, 20);
		tbFreqOS.setSelected(false);
		myPanel.add(tbFreqOS);

		// Checkbox fuer die Settings der Pulssequenz
		tbSettings.setText(java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("pulssequenceui.checkbox.parametert.title"));
		tbSettings.setBounds(new Rectangle(10, 7, 220, 20));
		if (myPanel.isShowing()) {
			tbSettings.setSelected(true);
		} else {
			tbSettings.setSelected(false);
		} 

		// Elemente zur Anzeige der Gesamtzeit einrichten
		pTotalTime.setBounds(10, 55, 220, 20);
		myInfoPanel.add(pTotalTime);

		// th 2002.10.30
		pTotalTime.setValue("?? " + MINUTE_UNIT + " ?? " + SECOND_UNIT);

		// Elemente zur Anzeige der Restzeit einrichten
		pRemainingTime.setBounds(10, 79, 220, 20);
		myInfoPanel.add(pRemainingTime);

		// th 2002.10.30
		displayRemainingTime(0);

		// Elemente zur Anzeige der Pixelgroesse einrichten
		pPixelSize.setBounds(10, 103, 220, 20);
		myInfoPanel.add(pPixelSize);

		// th 2002.10.30
		displayPixelSize();

		// Elemente zur Anzeige des SNR einrichten
		pSNRatio.setBounds(10, 127, 220, 20);
		myInfoPanel.add(pSNRatio);

		// Start- und Stopknopf einrichten
		pbStart.setText(java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("pulssequenceui.btn.start.title"));
		pbStart.setBounds(new Rectangle(10, 185, 100, 25));
		pbStop.setText(java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("pulssequenceui.btn.stop.title"));
		pbStop.setBounds(new Rectangle(125, 185, 100, 25));
		pbStop.setEnabled(false);

		// ActionListener nur initialisieren, wenn noetig
		// Sonst werden u.a. bei mehrfacher Selektion der Sequenz auch
		// mehrere ActionListener fuer die Buttons eingefuegt.
		if (isInitialized == false) {

			pbStart.addActionListener(new java.awt.event.ActionListener() {


				/**
				 * Method declaration
				 * 
				 * 
				 * @param e
				 * 
				 * @see
				 */
				public void actionPerformed(ActionEvent e) {
					pbStart_actionPerformed(e);
				} 

			});

			pbStop.addActionListener(new java.awt.event.ActionListener() {


				/**
				 * Method declaration
				 * 
				 * 
				 * @param e
				 * 
				 * @see
				 */
				public void actionPerformed(ActionEvent e) {
					pbStop_actionPerformed(e);
				} 

			});

			tbSettings.addActionListener(new java.awt.event.ActionListener() {


				/**
				 * Method declaration
				 * 
				 * 
				 * @param e
				 * 
				 * @see
				 */
				public void actionPerformed(ActionEvent e) {
					tbSettings_actionPerformed(e);
				} 

			});

			tbPhOS.addActionListener(new ActionListener() {


				/**
				 * Method declaration
				 * 
				 * 
				 * @param e
				 * 
				 * @see
				 */
				public void actionPerformed(ActionEvent e) {
					updateTotalTime();
					displaySNRatio();
				} 

			});

			tbFreqOS.addActionListener(new ActionListener() {


				/**
				 * Method declaration
				 * 
				 * 
				 * @param e
				 * 
				 * @see
				 */
				public void actionPerformed(ActionEvent e) {
					displaySNRatio();
				} 

			});

			pFOV.getTextFieldReference().addCaretListener(new CaretListener() {


				/**
				 * Method declaration
				 * 
				 * 
				 * @param e
				 * 
				 * @see
				 */
				public void caretUpdate(CaretEvent e) {
					displayPixelSize();
				} 

			});

			pRect.getTextFieldReference().addCaretListener(new CaretListener() {


				/**
				 * Method declaration
				 * 
				 * 
				 * @param e
				 * 
				 * @see
				 */
				public void caretUpdate(CaretEvent e) {
					updateTotalTime();
				} 

			});

			pThickness.getTextFieldReference().addCaretListener(new CaretListener() {


				/**
				 * Method declaration
				 * 
				 * 
				 * @param e
				 * 
				 * @see
				 */
				public void caretUpdate(CaretEvent e) {
					displayPixelSize();
				} 

			});

			pNEX.getTextFieldReference().addCaretListener(new CaretListener() {


				/**
				 * Method declaration
				 * 
				 * 
				 * @param e
				 * 
				 * @see
				 */
				public void caretUpdate(CaretEvent e) {
					updateTotalTime();
					displaySNRatio();
				} 

			});

			isInitialized = true;

		}		// end if (isInitialized = false);
 
		myInfoPanel.add(tbSettings, null);
		myInfoPanel.add(pbStart, null);
		myInfoPanel.add(pbStop, null);
		displayPixelSize();

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
	 * Die Methode aktiviert oder deaktiviert den Startknopf. D.h. sie ermoeglicht
	 * das Betaetigen des Knopfes oder verhindert dies.
	 * @param flag True, wenn der Knopf aktiviert werden soll, sonst False.
	 */
	public void enableStartButton(boolean flag) {
		pbStart.setEnabled(flag);
	}		// Methode enableStartButton(boolean flag)
 

	/**
	 * Die Methode aktiviert oder deaktiviert den Stopknopf. D.h. die ermoeglicht
	 * das Betaetigen des Knopfes oder verhindert dies.
	 * @param flag True, wenn der Knopf aktiviert werden soll, sonst False.
	 */
	public void enableStopButton(boolean flag) {
		pbStop.setEnabled(flag);
	}		// Methode enableStopButton(boolean flag)
 

	/**
	 * Die Methode wird aufgerufen, wenn der Startknopf gedrueckt wurde. In diesem
	 * Fall wird der Startknopf deaktiviert und der Stoppknopf aktiviert. Darueber
	 * hinaus wird eine Instanz eines potentiellen Artefakt-Simulators erzeugt.
	 * @param e Das Ergeignis, das beim Betaetigen des Startkopfes ausgeloest wird.
	 */
	void pbStart_actionPerformed(ActionEvent e) {
		pbStart.setEnabled(false);
		pbStop.setEnabled(true);

		mainFrame.setStatusBar(java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("pulssequenceui.statusbar.note.calculation"));

		// Erzeugen einer neuen Instanz eines potentiellen Artefakt-Simulators.
		ArtefactUI	aftUI = mainFrame.getSelectedArtefactUI();

		if (aftUI != null) {
			artefact = aftUI.createManipulator();
		} 
	}		// Methode  pbStart_actionPerformed(ActionEvent e)
 

	/**
	 * Die Methode wird aufgerufen, wenn der Stoppknopf gedrueckt wurde. In diesem
	 * Fall wird der Stoppknopf deaktiviert und der Startknopf aktiviert.
	 * Ausserdem wird die Fortschrittsanzeige und die Restzeit auf Null zurueckgesetzt.
	 * @param e Das Ergeignis, das beim Betaetigen des Stoppknopfes ausgeloest wird.
	 */
	void pbStop_actionPerformed(ActionEvent e) {
		pbStart.setEnabled(true);
		pbStop.setEnabled(false);

		pRemainingTime.setValue("0 " + MINUTE_UNIT + " 0 " + SECOND_UNIT);

		mainFrame.getProgressBar().setValue(0);
		mainFrame.setStatusBar(java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("pulssequenceui.statusbar.note.aborted"));
	}		// Ende pbStop_actionPerformed
 

	/**
	 * Die Methode wird aufgerufen, wenn der Knopf zur Anzeige des Einstellungsbereichs
	 * gedrueckt wurde. Der Einstellungbereich wird dann entweder ein-  oder
	 * ausgeblendet.
	 * @param e Das Ergeignis, das beim Betaetigen des Knopfes ausgeloest wird.
	 */
	void tbSettings_actionPerformed(ActionEvent e) {
		if (tbSettings.isSelected()) {
			myPanel.setVisible(true);
		} else {
			myPanel.setVisible(false);
		} 
		mainFrame.repaint();
	}		// Methode tbSettings_actionPerformed(ActionEvent e)
 

	/**
	 * Die Methode zeigt die uebergebene Gesamtberechnungszeit im Informationsbereich
	 * der Pulssequenz an. Dazu wird die uebergebene Zeit zunaecht in Minuten und
	 * Sekunden umgerechnet.
	 * @param totaltime Die Gesamtberechnungzeit in ms.
	 */
	void displayTotalTime(long totaltime) {

		long	ttimeS = (totaltime / 1000) % 60;
		long	ttimeM = ((totaltime / 1000) / 60);

		pTotalTime.setValue(ttimeM + " " + MINUTE_UNIT + " " + ttimeS + " " + SECOND_UNIT);
	} 


	/**
	 * Die Methode ist in dieser abstrakten Oberklasse leer. In den abgeleiteten
	 * Klassen wird in ihr die Gesamtzeit der Berechnung berechnet.
	 * Die Methode muss in dieser Klasse nur vorhanden sein, damit der Entwickler
	 * sich nicht mehr um die automatische Aktualisierung der Darstellung der
	 * Gesamtzeit kuemmern muss.
	 */
	void updateTotalTime() {}		// Methode updateTotalTime


	/**
	 * Die Methode zeigt die uebergebene Restberechnungszeit im Informationsbereich
	 * der Pulssequenz an. Dazu wird die uebergebene Zeit zunaecht in Minuten und
	 * Sekunden umgerechnet.
	 * @param remainingtime Die Restberechnungzeit in ms.
	 */
	void displayRemainingTime(long remainingtime) {
		long	ttimeS = (remainingtime / 1000) % 60;
		long	ttimeM = ((remainingtime / 1000) / 60) % 60;

		pRemainingTime.setValue(ttimeM + " " + MINUTE_UNIT + " " + ttimeS + " " + SECOND_UNIT);
	}		// Methode displayRemainingTime
 

	/**
	 * Die Methode liefert den Index der selektierten Aufnahmespule zurueck.
	 * @return Der Index der selektierten Spule in der Auswahlbox.
	 */
	public int getCoil() {
		return cbCoil.getSelectedIndex();
	}		// getCoil
 

	/**
	 * Die Methode berechnet die aktuelle Pixelgroesse aufgrund der Einstellungen
	 * von Matrixgroesse und Field of View. Die Pixelgroesse wird dann im
	 * Informationsbereich angezeigt.
	 */
	public void displayPixelSize() {
		String				ps = "";
		DecimalFormat form = new DecimalFormat("0.00");

		try {
			double	size = (double) pFOV.getValue() / (double) pMatrix.getValue();

			ps = (String) form.format(size);
			pPixelSize.setValue(ps + " " + PIXEL_UNIT);
			displaySNRatio();
		} catch (Exception err) {}
		;
	} 


	/**
	 * Die Methode berechnet das aktuelle Signal-zu-Rausch-Verhaeltnis und zeigt
	 * dieses im Informationsbereich an.
	 */
	public void displaySNRatio() {
		double				sn = getSNRatio();
		String				snr = "";

		DecimalFormat form = new DecimalFormat("0.000");

		try {
			snr = (String) form.format(sn);
		} catch (Exception e) {}

		// th 2002.10.30
		// pSNRatio.setValue("" + snr);
		pSNRatio.setValue(snr);
	} 


	// tha 2000.05.14: Neu definiert


	/**
	 * Die Methode berechnet das aktuelle Signal-zu-Rausch-Verhaeltnis
	 * aufgrund der eingestellten Sequenzparameter. <br>
	 * Hashemi R: MRI the basics. Williams, Baltimore, 1997. Kapitel 17, S. 167.
	 * Definition des Bandbreite:
	 * Hashemi R: MRI the basics. Williams, Baltimore, 1997. Kapitel 15, S. 161.
	 */
	public double getSNRatio() {
		int						nex;						// Anzahl der Messungen
		int						ny = 256;				// Anzahl Phasenkodierschritte
		double				GYRO = 42.6;		// Gyromagnetisches Verhaeltnis fuer H [MHz/T]
		double				GRAD = 10.0;		// Gradientenfeldstaerke [mT/m]
		double				bw;							// Bandbreite [Hz]
		double				sn = 0.0;				// Signal zu Rausch Verhaeltnis
		double				fov;						// Field of View [mm]
		double				pixsize;				// Pixelflaeche [mm^2]
		double				vol;						// Voxelvolumen [mm^3]

		// Der Korrekturfaktor wird so gewaelt, dass fuer folgende Parameter
		// Messungen = 1
		// FOV = 256
		// Schichtdicke = 6
		// Phasen-OV = nein
		// Das S/N Verhaeltnis gerade 1.000 betraegt.
		final double	factor = 3.44;	// Korrekturfaktor

		try {
			nex = pNEX.getValue();

			// Berechnung der tatsaechlich zu messenden Phasenkodierschritte
			if (tbPhOS.isSelected()) {
				ny *= 2;								// Anzahl der Phasenkodierschritte wird verdoppelt
				if (nex > 1) {
					nex /= 2;							// Anzahl der Messungen wird halbiert (wenn moeglich)
				} 
			} 

			// Berechnen des Probenvolumens
			fov = pFOV.getValue();
			pixsize = fov / (double) pMatrix.getValue();
			vol = Math.pow(pixsize, 2) * (double) pThickness.getValue();

			// Berechnung der tatsaechlich zu messenden Field of View
			if (tbFreqOS.isSelected()) {
				fov *= 2;								// FOV wird in Frequenzrichtung verdoppelt
			} 

			// Berechnung der Bandbreite
			bw = GYRO * GRAD * fov;		// [MHz/T * mT/m * mm] = [Hz]

			// Berechnen des Signal-Rausch-Verhaeltnisses
			sn = factor * vol * Math.sqrt(ny * nex / bw);

		} catch (Exception err) {}

		return sn;
	} 


	/**
	 * Diese Methode fügt ein Element in die Eigenschaftsliste kSpaceManipulator
	 * ein.
	 * @param className Name der Berechnungsklasse des Artefakt-Simulators.
	 * @param methodName Name der Methode, die die korrekte Berechnungsfunktion enthaelt.
	 */
	public void addProperty(String className, String methodName) {
		kSpaceManipulator.put(className, methodName);
	} 


	/**
	 * Method declaration
	 * 
	 * 
	 * @param className
	 * 
	 * @return
	 * 
	 * @see
	 */
	public String getProperty(String className) {
		return (String) kSpaceManipulator.get(className);
	} 


	/**
	 * Die Methode liefert den Namen der Methode der ausgeaehlten Artefakt-Klasse
	 * zurueck, die die fuer diese Pulssequenz korrekte Berechnungsvorschrift
	 * enthaelt.
	 * @return Der Name der Methode der Artefakt-Klasse, die die korrekte
	 * Berechnungsvorschrift enthaelt.
	 */
	public String getArtefactMethod() {
		if (artefact != null) {
			String	artName = artefact.getClass().getName();

			artName = artName.substring(artName.lastIndexOf('.') + 1, artName.length());
			return getProperty(artName);
		} else {
			return null;
		} 
	} 


	/**
	 * Die Methode liefert eine Referenz auf einen ggf. ausgewählten Artefakt-Simulator
	 * zurueck.
	 * @return Eine Referenz auf die Berechnungsklasse des ausgewaehlten
	 * Artefakt-Simulators.
	 */
	public Artefact getArtefact() {
		return artefact;
	}		// Methode getArtefact
 
}






/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

