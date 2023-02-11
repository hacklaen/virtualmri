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
package virtual.tools;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;


/**
 * Diese Klasse stellt einen Rahmen mit Beschriftung, Textfeld und Schieberegler
 * zur Verfuegung.
 * Die Werte von Schieberegler und Textfeld werden synchronisiert. Im Textfeld sind nur
 * ganzzahlige Eingaben in einem bestimmten Wertebereich zulaessig.<br>
 * Thomas Hacklaender 2002.10.30:
 * Inset left fuer das erste Label.<br>
 * @author   Christian Schalla
 * @author   Andreas Truemper
 * @version  1.1, 2002.10.30
 */
public class SliderPanel extends JPanel {


	/**
	 * Beschriftung des Schiebereglers.
	 */
	JLabel					lSliderLabel = new JLabel();


	/**
	 * Das Textfeld.
	 */
	JTextField			tfSliderTextField = new JTextField();


	/**
	 * Der Schieberegler.
	 */
	JSlider					slSlider;


	/**
	 * Die Beschriftung fuer ein optionales Einheitenzeichen.
	 */
	JLabel					lUnitLabel = new JLabel();


	/**
	 * Inhalt des Beschriftungsfeldes.
	 */
	private String	label;


	/**
	 * Inhalt der Beschriftung fuer die Einheit.
	 */
	private String	unit = "";


	/**
	 * Minimalwert fuer den Schieberegler.
	 */
	private int			iminimum;


	/**
	 * Maximalwert fuer den Schieberegler.
	 */
	private int			imaximum;


	/**
	 * Minimalwert fuer das Textfeld.
	 */
	private int			itminimum;


	/**
	 * Maximalwert fuer das Textfeld.
	 */
	private int			itmaximum;


	/**
	 * Tooltip-Text.
	 */
	private String	strtoolTip = "";


	/**
	 * Grosser Abstand der Ticks.
	 */
	private int			imajorspacing = 1000;


	/**
	 * Kleiner Abstand der Ticks.
	 */
	private int			iminorspacing = 0;


	/**
	 * Wenn dieser Merker wahr ist, ignoriert der Schieberegler das Event "item-State-Changed"
	 * Der Schieberegler und das Textfeld werden synchronisiert, allerdings soll es auch
	 * moeglich sein, im Textfeld groessere Werte einzugeben als fuer den Slider
	 * erlaubt ist. Die lostFocus-Methode des Textfeldes prueft die Grenzen und
	 * passt den Schieberegler an den Wert des Textfeldes an. Die setValue-Methode des
	 * Schiebereglers loest aber wiederum ein state-Changed-Ereignis aus, somit wuerde das
	 * Textfeld wieder auf das Maximum des Schiebereglers gesetzt. Daher wird zunaechst
	 * diese Flag aus "true" gesetzt, dadurch wird dieses Ereignis unterdrueckt.
	 */
	private boolean ignoreStateChange = false;


	/**
	 * Layout zur Anordnung der einzelnen Oberflaechenelemente.
	 */
	GridBagLayout		gridBagLayout1 = new GridBagLayout();


	/**
	 * Der Konstruktor erzeugt die einzelnen Bedienelemente und stellt diese
	 * entsprechend dem gewaehlten Layout dar.
	 */
	public SliderPanel() {
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		} 
	}		// Standardkonstruktor


	/**
	 * Der Konstruktor setzt die Beschriftung sowie den Minimal- und Maximalwert
	 * des Schiebereglers und des Textfeldes. Die Grenzwerte sind bei Aufruf dieses
	 * Konstruktors fuer den Schieberegler und das Textfeld identisch.
	 * @param lab Beschriftung des Schiebereglers.
	 * @param min Minimalwert fuer den Schieberegler und das Textfeld.
	 * @param max Maximalwert fuer den Schieberegler und das Textfeld.
	 */
	public SliderPanel(String lab, int min, int max) {
		label = lab;
		iminimum = min;
		imaximum = max;
		itminimum = min;
		itmaximum = max;
	}		// Kosntruktor SliderPanel(String lab, int min, int max)


	/**
	 * Der Konstruktor setzt die Beschriftung sowei den Minimal- und Maximalwert
	 * des Schiebereglers und des Textfeldes. Die Grenzwerte sind bei Aufruf dieses
	 * Konstruktors fuer den Schieberegler und das Textfeld identisch. Ausserdem
	 * wird die Beschriftung fuer das Einheitenzeichen gesetzt.
	 * @param lab Beschriftung des Schiebereglers.
	 * @param min Minimalwert fuer den Schieberegler und das Textfeld.
	 * @param max Maximalwert fuer den Schieberegler und das Textfeld.
	 * @param u Beschriftung fuer das Einheitenzeichen.
	 */
	public SliderPanel(String lab, int min, int max, String u) {
		label = lab;
		iminimum = min;
		imaximum = max;
		itminimum = min;
		itmaximum = max;
		unit = u;
	}		// Konstruktor  SliderPanel(String lab, int min, int max, String u)


	/**
	 * Die Methode stellt die Bedienelemente dieser Klasse dar.
	 */
	public void fillPanel() {
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		} 
	}		// Methode fillPanel
 

	/**
	 * Die Methode legt den ToolTip-Text fest.
	 * @param tip Der ToolTip-Text.
	 */
	public void setToolTip(String tip) {
		strtoolTip = tip;
	}		// Methode setToolTipText
 

	/**
	 * Die Methode legt das erlaubte Intervall fuer Werte im Textfeld
	 * fest. Dieses kann von dem Intervall fuer den Schieberegler getrennt
	 * gesetzt werden.
	 * @param min Kleinster erlaubter Wert fuer das Textfeld.
	 * @param max Groesster erlaubter Wert fuer das Textfeld.
	 */
	public void setTextRange(int min, int max) {
		itminimum = min;
		itmaximum = max;
	}		// Methode setTextRange
 

	/**
	 * Die Methode legt das kleine und grosse TickSpacing fuer den Schieberegler
	 * fest.
	 * @param minor Das kleine Tickspacing.
	 * @param major Das grosse Tickspacing.
	 */
	public void setTickSpacing(int major, int minor) {
		imajorspacing = major;
		iminorspacing = minor;
	}		// Methode setTickSpacing(int major, int minor)
 

	/**
	 * Die Methode erzeugt die einzelnen Bedienelemente dieser Klasse und stellt
	 * diese dar.
	 */
	void jbInit() throws Exception {
		GridBagConstraints	c = new GridBagConstraints();

		this.setLayout(gridBagLayout1);

		c.fill = GridBagConstraints.BOTH;

		// Erste Beschriftung Einfuegen
		c.weightx = 1.0;
		c.weighty = 0.5;

		// th 2002.10.30
		c.insets = new Insets(0, 5, 0, 0);
		gridBagLayout1.setConstraints(lSliderLabel, c);
		add(lSliderLabel);
		lSliderLabel.setText(label);
		lSliderLabel.setForeground(Color.black);

		// Textfeld einfuegen
		gridBagLayout1.setConstraints(tfSliderTextField, c);
		add(tfSliderTextField);
		tfSliderTextField.setDocument(new IntegerDocument());
		tfSliderTextField.setText("" + iminimum);

		// Zweite Beschriftung einfuegen
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridBagLayout1.setConstraints(lUnitLabel, c);
		add(lUnitLabel);
		lUnitLabel.setText(" " + unit);
		lUnitLabel.setForeground(Color.black);

		// Slider einfuegen
		slSlider = new JSlider(JSlider.HORIZONTAL, iminimum, imaximum, iminimum);
		c.weightx = 3.0;
		c.weighty = 0.5;

		gridBagLayout1.setConstraints(slSlider, c);
		add(slSlider);
		slSlider.setToolTipText(strtoolTip);
		slSlider.setFont(new Font("Dialog", 0, 10));
		slSlider.setPaintLabels(true);
		slSlider.setMajorTickSpacing(imajorspacing);
		slSlider.setValue(iminimum);
		tfSliderTextField.setText("" + slSlider.getValue());

		if (iminorspacing > 0) {
			slSlider.setMinorTickSpacing(iminorspacing);
		} 

		this.setBorder(BorderFactory.createEtchedBorder());

		// Action-Listener, um die Werte von Textfeld und Slider zu synchronisieren
		slSlider.addChangeListener(new javax.swing.event.ChangeListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void stateChanged(ChangeEvent e) {
				slSlider_stateChanged();
			} 

		});

		tfSliderTextField.addFocusListener(new java.awt.event.FocusAdapter() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void focusLost(FocusEvent e) {
				tfSliderTextField_focusLost();
			} 

		});
	}		// Methode jbInit
 

	/**
	 * Die Methode wird aufgerufen, wenn sich Zustand des Schieberegler aendert.
	 * Dann wird das Textfeld mit dem Schieberegler synchronisiert.
	 */
	void slSlider_stateChanged() {
		int tr;

		tr = slSlider.getValue();

		if (!ignoreStateChange) {
			tfSliderTextField.setText("" + tr);
		} else {
			ignoreStateChange = false;
		}		// if-else
 	 }		// Methode  slSlider_stateChanged()
 

	/**
	 * Die Methode ueberprueft die Gueltigkeit des Wertes im Textfeld, wenn dieses
	 * den Fokus verliert.
	 */
	void tfSliderTextField_focusLost() {
		ignoreStateChange = true;
		int tr;

		try {
			tr = Integer.parseInt(tfSliderTextField.getText());
		} catch (Exception err) {
			tr = 0;
			Toolkit.getDefaultToolkit().beep();
			tfSliderTextField.setText("0");
		} 
		if (tr > itmaximum) {
			tr = itmaximum;
			Toolkit.getDefaultToolkit().beep();
			tfSliderTextField.setText("" + tr);
		} 
		if (tr < itminimum) {
			tr = itminimum;
			Toolkit.getDefaultToolkit().beep();
			tfSliderTextField.setText("" + tr);
		} 
		slSlider.setValue(tr);
	}		// Methode tfSliderTextField_focusLost()
 

	/**
	 * Die Methode liefert den aktuellen Wert des Textfeldes.
	 * @return Der aktuelle Wert des Textfeldes.
	 */
	public int getValue() {

		/*
		 * Kein Test auf Integer und Wertebereich mehr noetig, dies wird im bereits
		 * Event-Handler erledigt.
		 */
		return Integer.parseInt(tfSliderTextField.getText());
	}		// Methode getValue
 

	/**
	 * Die Methode setzt den Wert des Schiebereglers. Dies ist noetig, um den
	 * Schieberegler mit dem Textfeld zu synchronisieren, wenn in das
	 * Textfeld ein Wert eingegeben wurde.
	 * @param ivalue Der Wert, auf den der Schieberegler gesetzt werden soll.
	 */
	public void setValue(int ivalue) {
		slSlider.setValue(ivalue);
	}		// Methode setValue
 

	/**
	 * Die Methode liefert eine Referenz auf das Textfeld zurueck. Dadurch koennen
	 * von aussen EventListener hinzugefuegt werden.
	 * @return Eine Referenz auf das Textfeld.
	 */
	public JTextField getTextFieldReference() {
		return tfSliderTextField;
	}		// Methode  getTextFieldReference()
 

	/**
	 * Wert, auf den der Slider minimal eingestellt werden kann.
	 * Die Werte Minimum und Maximum im Konstruktor bestimmen die dargestellten
	 * Beschriftungen UND die minimalen und maximalen Werte. Wenn die minimal
	 * und maximal einstellbaren Werte abweichen, werden sie über diese Methode
	 * explizit gesetzt.
	 * @param min Minimal einstellbarer Wert für den Schieberegler.
	 */
	public void setSliderMinimum(int min) {
		slSlider.getModel().setMinimum(min);
	}		// Methode  setSliderMinimum()
 

	/**
	 * Wert, auf den der Slider maximal eingestellt werden kann.
	 * Die Werte Minimum und Maximum im Konstruktor bestimmen die dargestellten
	 * Beschriftungen UND die minimalen und maximalen Werte. Wenn die minimal
	 * und maximal einstellbaren Werte abweichen, werden sie über diese Methode
	 * explizit gesetzt.
	 * @param max Maximal einstellbarer Wert für den Schieberegler.
	 */
	public void setSliderMaximum(int max) {
		slSlider.getModel().setMaximum(max);
	}		// Methode  setSliderMaximum()
 

}




/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

