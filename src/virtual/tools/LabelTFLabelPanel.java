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
 * Diese Klasse stellt ein Panel mit zwei Labeln und einem Textfeld zur Verfuegung.
 * Das erste Label enthaelt nur eine Beschriftung, die im Konstruktor
 * festgelegt wird. Das Textfeld ist ein Eingebfeld und kann aber auch per
 * Methode gesetzt werden. Der Inhalt des 2. Labels wird ebenfalls im Konstruktor
 * festgelegt und enthaelt in der Regel ein Einheitensymbol.
 * <DL><DT><B>Modifications: </B><DD>
 * Thomas Hacklaender 2000.8.14:
 * Debug-Output in pbUp_actionPerformed auskommentiert.<br>
 * Thomas Hacklaender 2002.10.12:
 * isFocusTraversable ist set deprecated.<br>
 * </DD></DL>
 * @author   Christian Schalla
 * @author   Andreas Truemper
 * @version  1.2, 2002.10.12
 */
public class LabelTFLabelPanel extends JPanel {


	/**
	 * Einfache Ableitung der JButtonklasse, die in der Tabreihenfolge ignoriert
	 * wird. Dazu muß die Methode isFocusTraversable überschrieben werden.
	 */
	public class NoFocusButton extends JButton {


		/**
		 * Constructor declaration
		 * 
		 * 
		 */

		// th 2002.10.12
		// public NoFocusButton() {
		// super();
		// }

		;


		/**
		 * Method declaration
		 * 
		 * 
		 * @return
		 * 
		 * @see
		 */

		// th 2002.10.12
		// public boolean isFocusTraversable() {
		// return false;
		// }

	}


	/**
	 * Erste Beschriftung.
	 */
	JLabel						lLabel = new JLabel();


	/**
	 * Textfeld.
	 */
	JTextField				tfText = new JTextField();


	/**
	 * Zweite Beschriftung (fuer ein Einheitensymbol)
	 */
	JLabel						lUnit = new JLabel();


	/**
	 * Knopf zum Erhoehen des Wertes des Textfeldes um 1.
	 */
	NoFocusButton			pbUp = new NoFocusButton();


	/**
	 * Knopf zum Vermindern des Wertes des Textfeldes um 1.
	 */
	NoFocusButton			pbDown = new NoFocusButton();


	/**
	 * Symbol: Pfeil nach oben fuer den Knopf pbUp.
	 */
	ImageIcon					iUp = new ImageIcon();


	/**
	 * Symbol: Pfeil nach unten fuer den Knopf pbDown.
	 */
	ImageIcon					iDown = new ImageIcon();


	/**
	 * Inhalt des ersten Beschriftungsfeldes.
	 */
	private String		label1;


	/**
	 * Inhalt des Textfeldes.
	 */
	private String		text;


	/**
	 * Inhalt des zweiten Beschriftungsfeldes.
	 */
	private String		label2;


	/**
	 * Schalter fuer eingeschraenktes Eingabeintervall.
	 */
	protected boolean rangeIsSet = false;


	/**
	 * Kleinster zulaessiger Wert fuer das Textfeld.
	 */
	protected int			minval = 0;


	/**
	 * Groesster zulaessiger Wert fuer das Textfeld.
	 */
	protected int			maxval = 0;


	/**
	 * Der Konstruktor legt Text der beiden Beschriftungsfelder sowie den
	 * initialen Inhalt des Textfeldes fest.
	 * @param lab1 Erste Beschriftung.
	 * @param txt Initialer Inhalt des Textfeldes.
	 * @param lab2 Zweite Beschriftung.
	 */
	public LabelTFLabelPanel(String lab1, String txt, String lab2) {
		label1 = lab1;
		label2 = lab2;
		text = txt;
		fillPanel();
	}		// Konstruktor LabelTFLabelPanel(String lab1, String txt, String lab2)


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
	 * Die Methode erzeugt die einzelnen Bedienelemente dieser Klasse und stellt
	 * diese dar.
	 */
	void jbInit() throws Exception {
		this.setLayout(null);

		// Laden der Icons
		iUp = new ImageIcon(this.getClass().getResource("resources/arrowup.gif"));
		iDown = new ImageIcon(this.getClass().getResource("resources/arrowdown.gif"));

		// Einrichten des ersten Labels
		lLabel.setText(label1);
		lLabel.setBounds(0, 0, 130, 20);
		add(lLabel);

		// Einrichten des Textfeldes
		tfText.setBounds(120, 0, 40, 20);
		add(tfText);
		tfText.setDocument(new IntegerDocument());
		tfText.setText(text);
		tfText.addFocusListener(new java.awt.event.FocusAdapter() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void focusLost(FocusEvent e) {
				tfText_focusLost();
			} 

		});

		// Einrichten des zweiten Labels (fuer Einheitenzeichen)
		lUnit.setText(label2);
		lUnit.setBounds(190, 0, 50, 20);
		add(lUnit);

		// Einrichten des Knopfes zum Erhoehen des Wertes des Textfeldes
		pbUp.setBounds(160, 0, 15, 10);
		pbUp.setMargin(new Insets(0, 0, 0, 0));
		pbUp.setIcon(iUp);
		pbUp.addActionListener(new java.awt.event.ActionListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void actionPerformed(ActionEvent e) {
				pbUp_actionPerformed();
			} 

		});
		add(pbUp);

		// Einrichten des Knopfes zum Vermindern des Wertes des Textfeldes
		pbDown.setBounds(160, 10, 15, 10);
		pbDown.setMargin(new Insets(0, 0, 0, 0));
		pbDown.setIcon(iDown);
		pbDown.addActionListener(new java.awt.event.ActionListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void actionPerformed(ActionEvent e) {
				pbDown_actionPerformed();
			} 

		});
		add(pbDown);
	}		// Methode jbInit
 

	/**
	 * Die Methode setzt den Wert des Textfeldes auf den uebergebenen Wert.
	 * @param txt Der zu setzende Text.
	 */
	public void setValue(String txt) {
		tfText.setText(txt);
	}		// Methode setValue
 

	/**
	 * Die Methode liefert den aktuellen Inhalt des Textfelde zurueck.
	 * @retrun Der Text (bzw. die Zahl) im Textfeld.
	 */
	public int getValue() {
		return Integer.parseInt(tfText.getText());
	}		// Methode getValue
 

	/**
	 * Die Methode liefert eine Referenz auf das Textfeld zurueck. Dadurch koennen
	 * von aussen EventListener hinzugefuegt werden.
	 * @return Eine Referentz auf das Textfeld.
	 */
	public JTextField getTextFieldReference() {
		return tfText;
	}		// Methode getTextFieldReference()
 

	/**
	 * Die Methode wird aufgerufen, wenn der Knopf zur Erhoehung des Wertes des
	 * Textfeldes um 1 gedrueckt wurde. Wenn das Textfeld aktiviert ist, wird der
	 * Wert erhoeht, soweit er noch im zulaessigen Bereich liegt.
	 */
	void pbUp_actionPerformed() {
		try {
			int newvalue = Integer.parseInt(tfText.getText()) + 1;

			if (checkValue(newvalue) == false) {

				// Auskommentiert: tha 2000.8.14
				// System.out.println("XXX");
				return;
			} else {
				if (tfText.isEnabled()) {

					// Auskommentiert: tha 2000.8.14
					// System.out.println("gesetzt");
					tfText.setText("" + newvalue);
				} 
			} 
		} catch (Exception err) {}
	}		// Methode pbUp_actionPerformed
 

	/**
	 * Die Methode wird aufgerufen, wenn der Knopf zur Verminderung des Wertes des
	 * Textfeldes um 1 gedrueckt wurde. Wenn das Textfeld aktiviert ist, wird der
	 * Wert vermindert, soweit er noch im zulaessigen Bereich liegt.
	 */
	void pbDown_actionPerformed() {
		try {
			int newvalue = Integer.parseInt(tfText.getText()) - 1;

			if (!checkValue(newvalue)) {
				return;
			} else {
				if (tfText.isEnabled()) {
					tfText.setText("" + newvalue);
				} 
			} 
		} catch (Exception err) {}
	}		// Methode pbDown_actionPerformed
 

	/**
	 * Die Methode setzt das Intervall der zulaessigen Werte fuer das Textfeld.
	 * @param Kleinster zulaessiger Wert.
	 * @param Groesster zulaessiger Wert.
	 */
	public void setRange(int min, int max) {
		minval = min;
		maxval = max;
		rangeIsSet = true;
	}		// Methode setRange
 

	/**
	 * Die Methode ueberprueft den Inhalt des Textfeldes beim Verlieren des
	 * Fokus auf Korrektheit, das heisst, ob der Wert im erlaubten Intevall liegt.
	 */
	void tfText_focusLost() {
		if (!checkValue()) {
			try {
				int myval = Integer.parseInt(tfText.getText());

				if (myval > maxval) {
					tfText.setText("" + maxval);
				} 
				if (myval < minval) {
					tfText.setText("" + minval);
				} 
			} catch (Exception err) {}
		}		// if
 	 }		// Methode tfText_lostFocus
 

	/**
	 * Die Methode ueberprueft, ob der Inhalt des Textfeldes im erlaubten
	 * Intervall liegt.
	 * @return True, falls Inhalt des Textfeldes im erlaubten Intervall, sonst False.
	 */
	boolean checkValue() {
		boolean ok = true;
		int			myval = Integer.parseInt(tfText.getText());

		ok = checkValue(myval);

		return ok;
	}		// Methode checkValue
 

	/**
	 * Die Methode ueberprueft, ob der ubergeben Wert im erlaubten
	 * Intervall liegt.
	 * @return True, falls Inhalt der Wert im erlaubten Intervall, sonst False.
	 */
	boolean checkValue(int myval) {
		boolean ok = true;

		if (rangeIsSet) {
			if ((myval < minval) || (myval > maxval)) {
				ok = false;
			}		// if
 		 }		// if
 
		return ok;
	}		// Methode checkValue
 
}






/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

