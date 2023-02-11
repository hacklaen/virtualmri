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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import virtual.tools.*;


/**
 * Diese Klasse erzeugt die Bedienelemente fuer eine Turbo-Spin-Echo-Sequenz und stellt
 * diese dar.
 * <DL><DT><B>Modifications: </B><DD>
 * Thomas Hacklaender 2000.05.14:
 * Plausible Werte fuer Slider eingetragen. <br>
 * Thomas Hacklaender 2002.10.14:
 * Strings internationalisiert.<br>
 * </DD></DL>
 * @author   Christian Schalla
 * @author   Andreas Truemper
 * @version  1.2, 2002.10.14
 */
public class TurboSpinEchoUI extends PulsesequenceUI {


	/**
	 * Schieberegler fuer den zeitlichen Abstand der Echos.
	 */
	SliderPanel						pESPPanel;


	/**
	 * Schieberegler fuer die TR-Zeit.
	 */
	SliderPanel						ptrPanel;


	/**
	 * Schiberegler fuer die effektive TE-zeit.
	 */
	SliderPanel						pteffPanel;


	/**
	 * Eingabebefeld fuer die Anzahl der echos pro Repititionszyklus.
	 */
	LabelTFLabelETLPanel	pETL = new LabelTFLabelETLPanel(java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("turbospinecho.etl.title"), "1", "");


	/**
	 * Voreingestellter zeitlicher Abstand zwischen den Echos (echo spacing) von 10 ms.
	 */
	private int						ESPTime = 10;


	/**
	 * Voreingestellte TR-Zeit von 1000 ms.
	 */
	private int						trTime = 1000;


	/**
	 * Voreingestellte effektive TE-Zeit von 40 ms.
	 */
	private int						teffTime = 40;


	/**
	 * Voreingestellte Anzah von Echos pro Repititionszyklus von 3.
	 */
	private int						ETL = 3;


	/**
	 * Standardkonstruktor.
	 */
	public TurboSpinEchoUI() {}


	/**
	 * Diese Methode fuegt die Sequenzspezifischen Steuerelemente in das Panel
	 * ein. Zunaechst wird fillPanel der Oberklasse aufzurufen, um Start- und
	 * Abbrechenknopf einzufuegen, die in jeder Sequenz enthalten sind.
	 */
	void jbInit() throws Exception {

		// Enrichten des Eingabefeldes fuer die Anazhl der Echos pro Repititionszyklus
		pETL.setBounds(270, 199, 220, 20);
		pETL.setRange(1, 16);
		myPanel.add(pETL);
		pETL.getTextFieldReference().addCaretListener(new CaretListener() {


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

		// Einrichten des Schiebereglers fuer die Repititionszeit.
		ptrPanel = new SliderPanel(java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("turbospinecho.tr.title"), 100, 6100, java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("turbospinecho.tr.unit"));
		ptrPanel.setBounds(new Rectangle(5, 5, 250, 70));
		ptrPanel.setToolTip(java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("turbospinecho.tr.tooltip"));
		ptrPanel.setTextRange(100, 6100);
		ptrPanel.setTickSpacing(2000, 0);
		ptrPanel.fillPanel();
		ptrPanel.getTextFieldReference().addCaretListener(new CaretListener() {


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
		myPanel.add(ptrPanel);

		// Einrichten des Schiebereglers fuer den zeitlichen Abstand zwischen den Echos.
		pESPPanel = new SliderPanel(java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("turbospinecho.esp.title"), 2, 102, java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("turbospinecho.esp.unit"));
		pESPPanel.setBounds(new Rectangle(5, 78, 250, 70));
		pESPPanel.setToolTip(java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("turbospinecho.esp.tooltip"));
		pESPPanel.setTextRange(2, 102);
		pESPPanel.setTickSpacing(20, 0);
		pESPPanel.fillPanel();
		myPanel.add(pESPPanel);

		// Einrichten des Schiebereglers fuer die effektive TE-Zeit.
		pteffPanel = new SliderPanel(java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("turbospinecho.teff.title"), 10, 1010, java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("turbospinecho.teff.unit"));
		pteffPanel.setBounds(new Rectangle(5, 151, 250, 70));
		pteffPanel.setToolTip(java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("turbospinecho.teff.tooltip"));
		pteffPanel.setTextRange(10, 1010);
		pteffPanel.setTickSpacing(200, 0);
		pteffPanel.fillPanel();
		myPanel.add(pteffPanel);

		// th 2002.10.30
		getValues();
		updateTotalTime();
	}		// Methode jbInit
 

	/**
	 * Die Methode setzt die  Bedienelemente der Sequenzparameter auf die in
	 * den entsprecheden Klassenvariablen gespeicherten Werte.
	 */
	public void getValues() {
		pESPPanel.setValue(ESPTime);
		ptrPanel.setValue(trTime);
		pteffPanel.setValue(teffTime);
		pETL.setValue("" + ETL);
	}		// Methode getValues
 

	/**
	 * Die Methode liest die aktuellen Sequenzparameterwerte aus den
	 * Bedienelementen aus und speichert sie in den entsprechenden Klassenvariablen.
	 */
	public void setValues() {
		ESPTime = pESPPanel.getValue();
		trTime = ptrPanel.getValue();
		ETL = pETL.getValue();
		teffTime = pteffPanel.getValue();
	}		// Methode setValues
 

	/**
	 * Die Methode wird aufgerufen, wenn der Startknopf gedrueckt wurde.
	 * Es wird eine neue Instanz der Berechnungsklasse erzeugt. Dieser werden die
	 * aktuellen Sequenzparameter mitgeteilt. Dann wird die Berechnung des
	 * Intensitaetsbildes angestossen.
	 * @param e Das Ereignis, das beim Betaetigen des startknopfes ausgeloest wird.
	 */
	void pbStart_actionPerformed(ActionEvent e) {
		if (pETL.getValue() * pESPPanel.getValue() >= ptrPanel.getValue()) {
			ErrorMessage.showMessage(java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("turbospinecho.exception.etltr"));
		} else {
			if (pteffPanel.getValue() >= ptrPanel.getValue()) {
				ErrorMessage.showMessage(java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("turbospinecho.exception.tetr"));
			} else {
				super.pbStart_actionPerformed(e);

				// Erzeugen einer neuen Instanz der Berechnungsklasse
				sequence = new TurboSpinEcho();
				sequence.setUI(this);

				TurboSpinEcho tsesequence = (TurboSpinEcho) sequence;

				// Der Berechnungsklasse die aktuellen Parameterwerte mitteilen
				tsesequence.setTETime(pteffPanel.getValue());
				tsesequence.setTRTime(ptrPanel.getValue());
				tsesequence.setETL(pETL.getValue());
				tsesequence.setESPTime(pESPPanel.getValue());

				// Berechnung des Intensitaetsbildes anstossen
				mainFrame.createIntensityImage(sequence);
			}		// if-else
 		 }		// if-else
 	 }			// Methode pbStart_actionPerformed
 

	/**
	 * Die Methode wird aufgerufen, wenn der Stopknopf gedrueckt wurde. In diesem
	 * Fall wird der Thread zur Berechnung des Bildes gestoppt und der Status des
	 * Start- und Stopknopfes zurueckgesetzt.
	 * @param e Das Ereignis, das beim Betaetigen des Stoppknopfes ausgeloest wird.
	 */
	void pbStop_actionPerformed(ActionEvent e) {
		super.pbStop_actionPerformed(e);

		// Stoppen des Berechnungsthreads
		sequence.stop();
	}		// Methode pbStop_actionPerformed
 

	/**
	 * Die Methode berechnet aus den aktuell eingestellten Sequenzparameterwerten
	 * die theoretische Gesamtdauer der Berechnung des Bildes. Diese Zeit wird im
	 * Kontrollbereich des Hauptfensters angezeigt.
	 */
	void updateTotalTime() {
		long	totaltime;

		try {
			totaltime = 256 * ptrPanel.getValue() * (pRect.getValue() / 8) / pETL.getValue();
			;

			totaltime *= pNEX.getValue();

			if (tbPhOS.isSelected()) {
				if (pNEX.getValue() == 1) {

					// Phasen-OS mit einer Messung:
					// -> Messzeit verdoppelt sich,
					// S/N Verhaeltnis verbessert sich um 1,41
					totaltime *= 2;
				} 
			} 

			displayTotalTime(totaltime);

		} catch (Exception err) {}
	} 

}






/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

