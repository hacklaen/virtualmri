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
 * Diese Klasse erzeugt die Bedienelemente fuer eine Inversion-Recovery-Sequenz
 * und stellt diese dar.
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
public class InversionRecoveryUI extends PulsesequenceUI {


	/**
	 * Schieberegler fuer die TR-Zeit.
	 */
	private SliderPanel ptrPanel;


	/**
	 * Schieberegler fuer die TI-Zeit.
	 */
	private SliderPanel ptiPanel;


	/**
	 * Voreingestellte TR-Zeit von 1000 ms.
	 */
	private int					trTime = 1000;


	/**
	 * Voreingestellte TI-Zeit von 100 ms.
	 */
	private int					tiTime = 100;


	/**
	 * Standardkonstruktor.
	 */
	public InversionRecoveryUI() {}		// Standardkonstruktor


	/**
	 * Diese Methode fuegt die Sequenzspezifischen Steuerelemente in das Panel
	 * ein. Zunaechst wird fillPanel der Oberklasse aufzurufen, um Start- und
	 * Abbrechenknopf einzufuegen, die in jeder Sequenz enthalten sind.
	 */
	void jbInit() throws Exception {

		// Schieberegler fuer TR-Zeit einrichten
		ptrPanel = new SliderPanel(java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("inversionrecovery.tr.title"), 100, 6100, java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("inversionrecovery.tr.unit"));
		ptrPanel.setBounds(new Rectangle(5, 5, 250, 70));
		ptrPanel.setToolTip(java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("inversionrecovery.tr.tooltip"));
		ptrPanel.setTextRange(100, 6100);
		ptrPanel.setTickSpacing(2000, 0);
		ptrPanel.fillPanel();
		myPanel.add(ptrPanel);
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

		// Schieberegler fuer TI-Zeit einrichten
		ptiPanel = new SliderPanel(java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("inversionrecovery.ti.title"), 10, 6010, java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("inversionrecovery.ti.unit"));
		ptiPanel.setBounds(new Rectangle(5, 78, 250, 70));
		ptiPanel.setToolTip(java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("inversionrecovery.ti.tooltip"));
		ptiPanel.setTextRange(11, 6010);
		ptiPanel.setTickSpacing(2000, 0);
		ptiPanel.fillPanel();
		myPanel.add(ptiPanel);

		// th 2002.10.30
		getValues();
		updateTotalTime();
	}		// Methode jbInit
 

	/**
	 * Die Methode setzt die  Bedienelemente der Sequenzparameter auf die in
	 * den entsprecheden Klassenvariablen gespeicherten Werte.
	 */
	public void getValues() {
		ptrPanel.setValue(trTime);
		ptiPanel.setValue(tiTime);
	}		// Methode getValues
 

	/**
	 * Die Methode liest die aktuellen Sequenzparameterwerte aus den
	 * Bedienelementen aus und speichert sie in den entsprechenden Klassenvariablen.
	 */
	public void setValues() {
		trTime = ptrPanel.getValue();
		tiTime = ptiPanel.getValue();
	}		// Methode setValues
 

	/**
	 * Die Methode wird aufgerufen, wenn der Startknopf gedrueckt wurde.
	 * Es wird eine neue Instanz der Berechnungsklasse erzeugt. Dieser werden die
	 * aktuellen Sequenzparameter mitgeteilt. Dann wird die Berechnung des
	 * Intensitaetsbildes angestossen.
	 * @param e Das Ereignis, das beim Betaetigen des Startknopfes ausgeloest wird.
	 */
	void pbStart_actionPerformed(ActionEvent e) {
		if (ptiPanel.getValue() < ptrPanel.getValue()) {
			super.pbStart_actionPerformed(e);

			// Erzeugen eines neuen Berechnugsobjektes
			sequence = new InversionRecovery();
			sequence.setUI(this);

			InversionRecovery irsequence = (InversionRecovery) sequence;

			// Uebergeben der Seuqenzparameter an das Berechnungsobjekt
			irsequence.setTITime(ptiPanel.getValue());
			irsequence.setTRTime(ptrPanel.getValue());

			// Berechnung des Intensitaetsbildes anstossen
			mainFrame.createIntensityImage(sequence);
		} else {
			ErrorMessage.showMessage(java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("inversionrecovery.exception.titr"));
		} 
	}		// Methode  pbStart_actionPerformed(ActionEvent e)
 

	/**
	 * Die Methode wird aufgerufen, wenn der Stopknopf gedrueckt wurde. In diesem
	 * Fall wird der Thread zur Berechnung des Bildes gestoppt und der Status des
	 * Start- und Stopknopfes zurueckgesetzt.
	 * @param e Das Ereignis, das beim Betaetigen des Stoppknopfes ausgeloest wird.
	 */
	void pbStop_actionPerformed(ActionEvent e) {
		super.pbStop_actionPerformed(e);

		// Stoppen des Berechnungthreads
		sequence.stop();
	} 


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

