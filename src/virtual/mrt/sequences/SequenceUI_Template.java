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
 * Die Klasse bildet eine Vorlage zur Erstellung Oberflaechenklasse
 * einer neuen Pulssequenz.
 * Der Klassenname sollte geaendert werden. Ebenso die Namen der Konstruktoren.
 * Auch andere Vorkommen der Zeichenkette SequenceUI_Template sind durch den
 * entsprechenden Sequenznamen zu ersetzen.
 * Als einziges Bedienelemement enthaelt diese Vorlage einen Schieberegler fuer
 * die Repititionszeit, die in jeder aktuellen Pulssequenz sinnvoll ist.
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
public class SequenceUI_Template extends PulsesequenceUI {


	/**
	 * Schieberegler zur Einstellung der Repititionszeit.
	 */
	SliderPanel								ptrPanel;


	/**
	 * Verweis auf die zugehoerige Pulssequenz-Berechnungsklasse.
	 */
	private Sequence_Template sequence;


	/**
	 * Klasseninterne Variable fuer die Repititionszeit.
	 */
	private int								trTime = 1000;


	/**
	 * Standardkonstruktor.
	 */
	public SequenceUI_Template() {}


	/**
	 * Die Methode fuegt die sequenzspezifischen Steuerelemente in den Darstellungsbereich
	 * ein. Das Erstellen des Start- und Stop-Knopfes geschieht bereits in der
	 * Oberklasse.
	 */
	void jbInit() throws Exception {

		// Erstellen der Bedienelemente fuer die Repititionszeit
		// Andere Sequenzparameter brauchen entsprechende Bedienelemente
		ptrPanel = new SliderPanel(java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("sequenceui.tr.title"), 100, 6100, java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("sequenceui.tr.unit"));
		ptrPanel.setBounds(new Rectangle(5, 5, 250, 70));
		ptrPanel.setToolTip(java.util.ResourceBundle.getBundle("virtual/mrt/sequences/resources/sequences_loc").getString("sequenceui.tr.tooltip"));
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

		// th 2002.10.30
		getValues();
		updateTotalTime();
	}		// Methode jbInit
 

	/**
	 * Die Methode liest die aktuellen Sequenzparametereinstellungen aus den Bedienelementen
	 * aus und speichert diese in Instanzvariablen. Die Methode muss
	 * vorhanden sein, da sie in der Oberklasse als abstrakt definiert ist.
	 * Sie muss ggf. um weitere Parameter ergaenzt werden.
	 */
	public void setValues() {
		trTime = ptrPanel.getValue();
	}		// Methode setValues
 

	/**
	 * Die Methode setzt die Einstellung der Bedienelemente auf die in
	 * den Instanzvariablen vorgegebenen Werte. Die Methode muss
	 * vorhanden sein, da sie in der Oberklasse als abstrakt definiert ist.
	 * Sie muss ggf. um weitere Parameter ergaenzt werden.
	 */
	public void getValues() {
		ptrPanel.setValue(trTime);
	}		// Methode getValues
 

	/**
	 * Die Methode wird aufgerufen, wenn der Startknopf der Pulssequenz
	 * gedrueckt wird. Es wird dann eine neue Instanz der Berechnungsklasse
	 * erzeugt. Ihr werden die aktuellen Parametereinstellungen uebergeben.
	 * Aus diesen Werten wird dann ein Intensitaetsbild berechnet.
	 * Die Methode muss vorhanden sein, da sie in der Oberklasse als abstrakt
	 * definiert ist. Sie muss gff. um das Setzen weiterer Sequenzparameter ergaenzt
	 * werden. Ausserdem muss der Name der zu erzeugenden Berechnungsklassen-Instanz
	 * angepasst werden.
	 */
	void pbStart_actionPerformed(ActionEvent e) {

		// Methode der Oberklasse aufrufen
		super.pbStart_actionPerformed(e);

		// Erzeugen einer neuen Instanz der Berechnungsklasse
		// Der Name der Berechnungsklasse muss angepasst werden.
		sequence = new Sequence_Template();
		sequence.setUI(this);

		// Der Berechnungsklasse die Sequenzparameter mitteilen. Ggf. um weitere
		// Parameter ergaenzen
		sequence.setTRTime(ptrPanel.getValue());

		// Anstossen der Berechnung des Intensitaetsbildes
		mainFrame.createIntensityImage(sequence);

	}		// Methode pbStart_actionPerformed
 

	/**
	 * Die Methode wird aufgerufen, wenn der Stop-Knopf der Pulssequenz
	 * gedrueckt wurde. In der Oberklasse wird dann der Status der Knoepfe
	 * zurueckgesetzt. In dieser Klasse muss dann noch der Thread der
	 * Berechnungsklasse gestoppt werden.
	 * Die Methode muss vorhanden sein, da sie in der Oberklasse als abstrakt
	 * definiert ist. Sie sollte nicht veraendert werden.
	 */
	void pbStop_actionPerformed(ActionEvent e) {

		// Aufrufen der Methode der Oberklasse
		super.pbStop_actionPerformed(e);

		// Stoppen des Threads
		sequence.stop();
	}		// Methode pbStop_actionPerformed
 

	/**
	 * Die Methode berechnet die Dauer der Aufnahme aus den eingestellten
	 * Parameterwerten neu und stellt die berechnete Zeit dar.
	 * Die Methode muss ggf. angepasst werden, wenn weitere Parameter
	 * Einfluss auf die Aufnahmezeit haben.
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

