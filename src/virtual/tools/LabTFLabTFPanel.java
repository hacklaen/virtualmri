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
 * Diese Klasse stellt ein Panel mit zwei Labeln und zwei Textfeldern zur Verfügung.
 * Die Labels koennen nur durch den Konstruktor gesetzt werden, die Textfelder
 * auch per Methode.<br>
 * <DL><DT><B>Modifications: </B><DD>
 * Thomas Hacklaender 2002.10.12:
 * isFocusTraversable ist set deprecated.<br>
 * </DD></DL>
 * @author   Christian Schalla
 * @author   Andreas Truemper
 * @version  1.2, 2002.10.12
 */
public class LabTFLabTFPanel extends JPanel {


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
		public NoFocusButton() {
			super();
		}

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
	JLabel					lLabel1 = new JLabel();


	/**
	 * Erstes Textfeld.
	 */
	JTextField			tfText1 = new JTextField();


	/**
	 * Zweite Beschriftung.
	 */
	JLabel					lLabel2 = new JLabel();


	/**
	 * Zeites Textfeld.
	 */
	JTextField			tfText2 = new JTextField();


	/**
	 * Knopf zum Erhoehen des Wertes des ersten Textfeldes um 1.
	 */
	JButton					pbUp1 = new NoFocusButton();


	/**
	 * Knopf zum Vermindern des Wertes des ersten Textfeldes um 1.
	 */
	JButton					pbDown1 = new NoFocusButton();


	/**
	 * Knopf zum Erhoehen des Wertes des zweiten Textfeldes um 1.
	 */
	JButton					pbUp2 = new NoFocusButton();


	/**
	 * Knopf zum Vermindern des Wertes des zweiten Textfeldes um 1.
	 */
	JButton					pbDown2 = new NoFocusButton();


	/**
	 * Symbol: Pfeil nach oben fuer die Knoepfe an den Textfeldern.
	 */
	ImageIcon				iUp = new ImageIcon();


	/**
	 * Symbol: Pfeil nach unten fuer die Knoepfe an den Textfeldern.
	 */
	ImageIcon				iDown = new ImageIcon();


	/**
	 * Inhalt des ersten Beschriftungsfeldes.
	 */
	private String	label1;


	/**
	 * Inhalt des zweiten Beschriftungsfeldes.
	 */
	private String	label2;


	/**
	 * Inhalt des ersten Textfeldes.
	 */
	private String	text1;


	/**
	 * Inhalt des zweiten Textfeldes.
	 */
	private String	text2;


	/**
	 * Kleinster zulaessiger Wert fuer das erste Textfeld.
	 */
	private int			minval1 = 0;


	/**
	 * Kleinster zulaessiger Wert fuer das zweite Textfeld.
	 */
	private int			minval2 = 0;


	/**
	 * Groesster zulaessiger Wert fuer das erste Textfeld.
	 */
	private int			maxval1 = 0;


	/**
	 * Groesster zulaessiger Wert fuer das zweite Textfeld.
	 */
	private int			maxval2 = 0;


	/**
	 * Schalter fuer eingeschraenktes Eingabeintervall fuer das erste Textfeld.
	 */
	private boolean range1IsSet = false;


	/**
	 * Schalter fuer eingeschraenktes Eingabeintervall fuer das zweite Textfeld.
	 */
	private boolean range2IsSet = false;


	/**
	 * Merkt sich, ob die GUI schon initialisiert wurde, damit Listener nicht
	 * mehrmals initialisiert werden.
	 */
	private boolean isInitialized = false;


	/**
	 * Der Konstruktor legt Text der beiden Beschriftungsfelder sowie den
	 * initialen Inhalt der beiden Textfelder fest.
	 * @param lab1 Erste Beschriftung.
	 * @param txt1 Initialer Inhalt des ersten Textfeldes.
	 * @param lab2 Zweite Beschriftung.
	 * @param txt2 Initialer Inhalt des zweiten Textfeldes.
	 */
	public LabTFLabTFPanel(String lab1, String txt1, String lab2, String txt2) {
		label1 = lab1;
		label2 = lab2;
		text1 = txt1;
		text2 = txt2;
		fillPanel();
	}		// Kosntruktor


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

		iUp = new ImageIcon(LabTFLabTFPanel.class.getResource("arrowup.gif"));
		iDown = new ImageIcon(LabTFLabTFPanel.class.getResource("arrowdown.gif"));

		lLabel1.setText(label1);
		lLabel1.setBounds(0, 0, 70, 20);
		add(lLabel1);

		tfText1.setBounds(79, 0, 43, 20);
		add(tfText1);
		tfText1.setDocument(new IntegerDocument());
		tfText1.setText(text1);
		tfText1.addFocusListener(new java.awt.event.FocusAdapter() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void focusLost(FocusEvent e) {
				tfText1_focusLost();
			} 

		});


		lLabel2.setText(label2);
		lLabel2.setBounds(145, 0, 45, 20);
		lLabel2.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		add(lLabel2);

		tfText2.setBounds(199, 0, 40, 20);
		add(tfText2);
		tfText2.setDocument(new IntegerDocument());
		tfText2.setText(text2);
		tfText2.addFocusListener(new java.awt.event.FocusAdapter() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void focusLost(FocusEvent e) {
				tfText2_focusLost();
			} 

		});


		pbUp1.setBounds(123, 0, 15, 10);
		pbUp1.setMargin(new Insets(0, 0, 0, 0));
		pbUp1.setIcon(iUp);
		if (!isInitialized) {
			pbUp1.addActionListener(new java.awt.event.ActionListener() {


				/**
				 * Method declaration
				 * 
				 * 
				 * @param e
				 * 
				 * @see
				 */
				public void actionPerformed(ActionEvent e) {
					pbUp1_actionPerformed();
				} 

			});
		} 
		add(pbUp1);

		pbDown1.setBounds(123, 10, 15, 10);
		pbDown1.setMargin(new Insets(0, 0, 0, 0));
		pbDown1.setIcon(iDown);
		if (!isInitialized) {
			pbDown1.addActionListener(new java.awt.event.ActionListener() {


				/**
				 * Method declaration
				 * 
				 * 
				 * @param e
				 * 
				 * @see
				 */
				public void actionPerformed(ActionEvent e) {
					pbDown1_actionPerformed();
				} 

			});
		} 
		add(pbDown1);

		pbUp2.setBounds(240, 0, 15, 10);
		pbUp2.setMargin(new Insets(0, 0, 0, 0));
		pbUp2.setIcon(iUp);
		if (!isInitialized) {
			pbUp2.addActionListener(new java.awt.event.ActionListener() {


				/**
				 * Method declaration
				 * 
				 * 
				 * @param e
				 * 
				 * @see
				 */
				public void actionPerformed(ActionEvent e) {
					pbUp2_actionPerformed();
				} 

			});
		} 
		add(pbUp2);

		pbDown2.setBounds(240, 10, 15, 10);
		pbDown2.setMargin(new Insets(0, 0, 0, 0));
		pbDown2.setIcon(iDown);
		if (!isInitialized) {
			pbDown2.addActionListener(new java.awt.event.ActionListener() {


				/**
				 * Method declaration
				 * 
				 * 
				 * @param e
				 * 
				 * @see
				 */
				public void actionPerformed(ActionEvent e) {
					pbDown2_actionPerformed();
				} 

			});
		} 
		add(pbDown2);

		isInitialized = true;
	}		// Methode jbInit
 

	/**
	 * Die Methode setzt den Wert des ersten Textfeldes auf den uebergebenen Wert.
	 * @param txt1 Der zu setzende Text.
	 */
	public void setValue1(String txt1) {
		tfText1.setText(txt1);
	}		// Methode setValue1
 

	/**
	 * Die Methode setzt den Wert des zweiten Textfeldes auf den uebergebenen Wert.
	 * @param txt2 Der zu setzende Text.
	 */
	public void setValue2(String txt2) {
		tfText2.setText(txt2);
	}		// Methode setValue2
 

	/**
	 * Die Methode liefert den aktuellen Inhalt des ersten Textfelde zurueck.
	 * @return Der Text (bzw. die Zahl) im ersten Textfeld.
	 */
	public int getValue1() {
		return Integer.parseInt(tfText1.getText());
	}		// Methode getValue2
 

	/**
	 * Die Methode liefert den aktuellen Inhalt des zweiten Textfelde zurueck.
	 * @return Der Text (bzw. die Zahl) im zweiten Textfeld.
	 */
	public int getValue2() {
		return Integer.parseInt(tfText2.getText());
	}		// Methode getValue2
 

	/**
	 * Die Methode liefert eine Referenz auf das erste Textfeld zurueck. Dadurch koennen
	 * von aussen EventListener hinzugefuegt werden.
	 * @return Eine Referentz auf das erste Textfeld.
	 */
	public JTextField getTextFieldReference1() {
		return tfText1;
	}		// getTextFieldReference1
 

	/**
	 * Die Methode liefert eine Referenz auf das zweite Textfeld zurueck. Dadurch koennen
	 * von aussen EventListener hinzugefuegt werden.
	 * @return Eine Referentz auf das zweite Textfeld.
	 */
	public JTextField getTextFieldReference2() {
		return tfText2;
	}		// Methode getTextFiledReference2
 

	/**
	 * Die Methode wird aufgerufen, wenn der Knopf zur Erhoehung des Wertes des ersten
	 * Textfeldes um 1 gedrueckt wurde. Wenn das Textfeld aktiviert ist, wird der
	 * Wert erhoeht, soweit er noch im zulaessigen Bereich liegt.
	 */
	void pbUp1_actionPerformed() {
		try {
			int newvalue = Integer.parseInt(tfText1.getText()) + 1;

			if (!checkValue1(newvalue)) {
				return;
			} else {
				if (tfText1.isEnabled()) {
					tfText1.setText("" + newvalue);
				} 
			} 
		} catch (Exception err) {}
	}		// Methode pbUp1_actionPerformed
 

	/**
	 * Die Methode wird aufgerufen, wenn der Knopf zur Verminderung des Wertes des ersten
	 * Textfeldes um 1 gedrueckt wurde. Wenn das Textfeld aktiviert ist, wird der
	 * Wert vermindert, soweit er noch im zulaessigen Bereich liegt.
	 */
	void pbDown1_actionPerformed() {
		try {
			int newvalue = Integer.parseInt(tfText1.getText()) - 1;

			if (!checkValue1(newvalue)) {
				return;
			} else {
				if (tfText1.isEnabled()) {
					tfText1.setText("" + newvalue);
				} 
			} 
		} catch (Exception err) {}

	}		// Methode pbDown1_actionPerformed
 

	/**
	 * Die Methode wird aufgerufen, wenn der Knopf zur Erhoehung des Wertes des zweiten
	 * Textfeldes um 1 gedrueckt wurde. Wenn das Textfeld aktiviert ist, wird der
	 * Wert erhoeht, soweit er noch im zulaessigen Bereich liegt.
	 */
	void pbUp2_actionPerformed() {
		try {
			int newvalue = Integer.parseInt(tfText2.getText()) + 1;

			if (!checkValue2(newvalue)) {
				return;
			} else {
				if (tfText2.isEnabled()) {
					tfText2.setText("" + newvalue);
				} 
			} 
		} catch (Exception err) {}
	}		// Methode pbUp2_actionPerformed
 

	/**
	 * Die Methode wird aufgerufen, wenn der Knopf zur Verminderung des Wertes des zweiten
	 * Textfeldes um 1 gedrueckt wurde. Wenn das Textfeld aktiviert ist, wird der
	 * Wert vermindert, soweit er noch im zulaessigen Bereich liegt.
	 */
	void pbDown2_actionPerformed() {
		try {
			int newvalue = Integer.parseInt(tfText2.getText()) - 1;

			if (!checkValue2(newvalue)) {
				return;
			} else {
				if (tfText2.isEnabled()) {
					tfText2.setText("" + newvalue);
				} 
			} 
		} catch (Exception err) {}
	}		// Methode pbDown2_actionPerformed
 

	/**
	 * Die Methode setzt das Intervall der zulaessigen Werte fuer das erste Textfeld.
	 * @param Kleinster zulaessiger Wert fuer das erste Textfeld.
	 * @param Groesster zulaessiger Wert fuer das erste Textfeld.
	 */
	public void setRange1(int min, int max) {
		minval1 = min;
		maxval1 = max;
		range1IsSet = true;
	}		// Methode setRange1
 

	/**
	 * Die Methode setzt das Intervall der zulaessigen Werte fuer das zweite Textfeld.
	 * @param Kleinster zulaessiger Wert fuer das zweite Textfeld.
	 * @param Groesster zulaessiger Wert fuer das zweite Textfeld.
	 */
	public void setRange2(int min, int max) {
		minval2 = min;
		maxval2 = max;
		range2IsSet = true;
	}		// Methode setRange2
 

	/**
	 * die Methode ueberprueft den Inahlt des ersten Textfeldes beim Verlieren des
	 * Fokus auf Korrektheit, das heisst, ob der Wert im erlaubten Intevall liegt.
	 */
	void tfText1_focusLost() {
		if (!checkValue1()) {
			try {
				int myval = Integer.parseInt(tfText1.getText());

				if (myval > maxval1) {
					tfText1.setText("" + maxval1);
				} 
				if (myval < minval1) {
					tfText1.setText("" + minval1);
				} 
			} catch (Exception err) {}
		}		// if
 	 }		// Methode tfText1_lostFocus
 

	/**
	 * die Methode ueberprueft den Inahlt des zweiten Textfeldes beim Verlieren des
	 * Fokus auf Korrektheit, das heisst, ob der Wert im erlaubten Intevall liegt.
	 */
	void tfText2_focusLost() {
		if (!checkValue2()) {
			try {
				int myval = Integer.parseInt(tfText2.getText());

				if (myval > maxval2) {
					tfText2.setText("" + maxval2);
				} 
				if (myval < minval2) {
					tfText2.setText("" + minval2);
				} 
			} catch (Exception err) {}
		}		// if
 	 }		// Methode tfText2_lostFocus
 

	/**
	 * Die Methode ueberprueft, ob der Inhalt des ersten Textfeldes im erlaubten
	 * Intervall liegt.
	 * @return True, falls Inhalt des ersten Textfeldes im erlaubten Intervall, sonst False.
	 */
	boolean checkValue1() {
		boolean ok = true;
		int			myval = Integer.parseInt(tfText1.getText());

		ok = checkValue1(myval);

		return ok;
	}		// Methode checkValue1
 

	/**
	 * Die Methode ueberprueft, ob der uebergebene Wert im erlaubten
	 * Intervall liegt fuer das erste Textfeld.
	 * @return True, falls Inhalt der Wert im erlaubten Intervall, sonst False.
	 */
	boolean checkValue1(int myval) {
		boolean ok = true;

		if (range1IsSet) {
			if ((myval < minval1) || (myval > maxval1)) {
				ok = false;
			}		// if
 		 }		// if
 
		return ok;
	}		// Methode checkValue1
 

	/**
	 * Die Methode ueberprueft, ob der Inhalt des zweiten Textfeldes im erlaubten
	 * Intervall liegt.
	 * @return True, falls Inhalt des zweiten Textfeldes im erlaubten Intervall, sonst False.
	 */
	boolean checkValue2() {
		boolean ok = true;
		int			myval = Integer.parseInt(tfText2.getText());

		ok = checkValue2(myval);

		return ok;
	}		// Methode checkValue2
 

	/**
	 * Die Methode ueberprueft, ob der uebergebene Wert im erlaubten
	 * Intervall fuer das zweite textfeld liegt.
	 * @return True, falls Inhalt der Wert im erlaubten Intervall, sonst False.
	 */
	boolean checkValue2(int myval) {
		boolean ok = true;

		if (range2IsSet) {
			if ((myval < minval2) || (myval > maxval2)) {
				ok = false;
			}		// if
 		 }		// if
 
		return ok;
	}		// Methode checkValue2
 


}






/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

