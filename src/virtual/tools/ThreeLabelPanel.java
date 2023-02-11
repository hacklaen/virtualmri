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
 * Diese Klasse stellt ein Panel mit drei Labeln zur Verfuegung.
 * Das erste Label enthaelt nur eine Beschriftung, die im Konstruktor
 * festgelegt wird. Der Inhalt das zweiten Labels kann zu jedem Zeitpunkt mit
 * einer entsprechenden Methode gesetzt werden. Der Inhalt des 3. Labels wird
 * ebenfalls im Konstruktor festgelegt und enthaelt in der Regel ein
 * Einheitensymbol.
 * 
 * @author   Christian Schalla
 * @author   Andreas Truemper
 * @version  1.0, 15. Oktober 1999
 */
public class ThreeLabelPanel extends JPanel {


	/**
	 * Label zur Beschriftung des Anzeigefeldes.
	 */
	JLabel					lFix = new JLabel();


	/**
	 * Das eigentliche, aenderbare Anzeigefeld.
	 */
	JLabel					lVariable = new JLabel();


	/**
	 * Das Label fuer ein optionales Einheitensymbol.
	 */
	JLabel					lUnit = new JLabel();


	/**
	 * Der Inhalt des Beschriftungsfeldes.
	 */
	private String	label1;


	/**
	 * Der Inhalt des variablen Anzeigefeldes.
	 */
	private String	label2;


	/**
	 * Der Inhalt des Einheitenfeldes.
	 */
	private String	label3;


	/**
	 * Der Konstruktor erzeugt die Oberflaechenelemente und
	 * initialisiert den Text alle 3 Label.
	 * @param lab1 Der Inhalt des Beschriftungsfeldes.
	 * @param lab2 Der Inhalt des varianlen Anzeigefeldes.
	 * @param lab3 Der Inhalt des Einheitenfeldes.
	 */
	public ThreeLabelPanel(String lab1, String lab2, String lab3) {
		label1 = lab1;
		label2 = lab2;
		label3 = lab3;
		fillPanel();
	}		// Konstruktor


	/**
	 * Die Methode richtet die GUI-Elemente ein und stellt diese dar.
	 */
	public void fillPanel() {
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		} 
	}		// Methode fillPanel
 

	/**
	 * Die Methode richtet die GUI-Elemente ein und stellt diese dar.
	 */
	void jbInit() throws Exception {
		this.setLayout(null);

		// erstes Label (Beschriftung) einrichten
		lFix.setText(label1);
		lFix.setBounds(0, 0, 130, 20);
		add(lFix);

		// zweites Label (Anzeigefeld) einrichten
		lVariable.setText(label2);
		lVariable.setBounds(135, 0, 90, 20);
		add(lVariable);

		// drittes Label (Einheit) einrichten
		lUnit.setText(label3);
		lUnit.setBounds(230, 0, 50, 20);
		add(lUnit);
	}		// Methode jbInit
 

	/**
	 * Die Methode setzt den Wert des variablen Anzeigefeldes auf den uebergenenen
	 * Wert.
	 * @param txt Der Test, der im variablen Anzeigefeld angezeigt werden soll.
	 */
	public void setValue(String txt) {
		lVariable.setText(txt);
	}		// Methode setValue
 
}






/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

