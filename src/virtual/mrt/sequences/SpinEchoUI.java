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
 * Diese Klasse erzeugt die Bedienelemente fuer eine Spin-Echo-Sequenz und stellt
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
public class SpinEchoUI extends PulsesequenceUI {


	/**
	 * Schieberegler fuer die TE-Zeit.
	 */
	SliderPanel ptePanel;


	/**
	 * Schieberegler fuer die TR-Zeit.
	 */
	SliderPanel ptrPanel;


	/**
	 * Voreingestellte TE-Zeit von 120 ms.
	 */
	private int teTime = 120;


	/**
	 * Voreingestellte TR-Zeit von 3000 ms.
	 */
	private int trTime = 3000;


	/**
	 * Standardkonstruktor.
	 */
	public SpinEchoUI() {}


	/**
	 * Diese Methode fuegt die Sequenzspezifischen Steuerelemente in das Panel
	 * ein. Zunaechst wird fillPanel der Oberklasse aufzurufen, um Start- und
	 * Abbrechenknopf einzufuegen, die in jeder Sequenz enthalten sind.
	 */
	void jbInit() throws Exception {

		// Schieberegler fuer die TR-zeit einrichten
		ptrPanel = new SliderPanel(java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("spinecho.tr.title"), 100, 6100, java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("spinecho.tr.unit"));
		ptrPanel.setBounds(new Rectangle(5, 5, 250, 70));
		ptrPanel.setToolTip(java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("spinecho.tr.tooltip"));
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

		// Schieberegler fuer die TE-Zeit einrichten
		ptePanel = new SliderPanel(java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("spinecho.te.title"), 10, 1010, java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("spinecho.te.unit"));
		ptePanel.setBounds(new Rectangle(5, 78, 250, 70));
		ptePanel.setToolTip(java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("spinecho.te.tooltip"));
		ptePanel.setTextRange(10, 1010);
		ptePanel.setTickSpacing(200, 0);
		ptePanel.fillPanel();
		myPanel.add(ptePanel);

		// th 2002.10.30
		getValues();
		updateTotalTime();
	}		// Methode jbInit
 

	/**
	 * Die Methode setzt die  Bedienelemente der Sequenzparameter auf die in
	 * den entsprecheden Klassenvariablen gespeicherten Werte.
	 */
	public void getValues() {
		ptePanel.setValue(teTime);
		ptrPanel.setValue(trTime);
	}		// Methode getValues
 

	/**
	 * Die Methode liest die aktuellen Sequenzparameterwerte aus den
	 * Bedienelementen aus und speichert sie in den entsprechenden Klassenvariablen.
	 */
	public void setValues() {
		teTime = ptePanel.getValue();
		trTime = ptrPanel.getValue();
	}		// Methode setValues
 


	/**
	 * Die Methode wird aufgerufen, wenn der Startknopf gedrueckt wurde.
	 * Es wird eine neue Instanz der Berechnungsklasse erzeugt. Dieser werden die
	 * aktuellen Sequenzparameter mitgeteilt. Dann wird die Berechnung des
	 * Intensitaetsbildes angestossen.
	 * @param e Das Ereignis, das beim Betaetigen des startknopfes ausgeloest wird.
	 */
	void pbStart_actionPerformed(ActionEvent e) {
		if (ptePanel.getValue() < ptrPanel.getValue()) {
			super.pbStart_actionPerformed(e);

			// erzeugen einer neuen Instanz der Berechnungsklasse
			sequence = new SpinEcho();
			sequence.setUI(this);

			SpinEcho	sesequence = (SpinEcho) sequence;

			// Der Berechnungsklasse die Parameterwerte mitteilen
			sesequence.setTETime(ptePanel.getValue());
			sesequence.setTRTime(ptrPanel.getValue());

			// Berechnung des Intensitaetsbildes anstossen
			mainFrame.createIntensityImage(sequence);
		} else {
			ErrorMessage.showMessage(java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("spinecho.exception.tetr"));
		} 
	}		// Methode pbStart_actionPerformed(ActionEvent e)
 

	/**
	 * Die Methode wird aufgerufen, wenn der Stopknopf gedrueckt wurde. In diesem
	 * Fall wird der Thread zur Berechnung des Bildes gestoppt und der Status des
	 * Start- und Stopknopfes zurueckgesetzt.
	 * @param e Das Ereignis, das beim Betaetigen des Stoppknopfes ausgeloest wird.
	 */
	void pbStop_actionPerformed(ActionEvent e) {
		super.pbStop_actionPerformed(e);

		// Berechnungsthread stoppen
		sequence.stop();
	}		// Methode  pbStop_actionPerformed(ActionEvent e)
 

	/**
	 * Die Methode berechnet aus den aktuell eingestellten Sequenzparameterwerten
	 * die theoretische Gesamtdauer der Berechnung des Bildes. Diese Zeit wird im
	 * Kontrollbereich des Hauptfensters angezeigt.
	 */
	void updateTotalTime() {
		long	totaltime;

		try {
			totaltime = 256 * ptrPanel.getValue() * pRect.getValue() / 8;

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

