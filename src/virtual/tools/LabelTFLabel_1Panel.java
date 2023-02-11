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
 * Erweiterung der Klasse LabelTFLabelPanel:
 * F�r die Rechteck-Einstellung sollen nur 4 und 8 gew�hlt werden d�rfen.
 * Zu diesem Zweck werden die Methoden pbUp_actionPerformed, pbDown_actionPerformed,
 * checkValue und focusLost ueberdefiniert.
 * 
 * @author   Christian Schalla
 * @author   Andreas Truemper
 * @version  1.0, 15. Oktober 1999
 */

public class LabelTFLabel_1Panel extends LabelTFLabelPanel {


	/**
	 * Der Konstruktor legt den Text der beiden Beschriftungsfelder sowie den
	 * initialen Inhalt des Textfeldes fest.
	 * @param lab1 Erste Beschriftung.
	 * @param txt Initialer Inhalt des Textfeldes.
	 * @param lab2 Zweite Beschriftung.
	 */
	public LabelTFLabel_1Panel(String lab1, String txt, String lab2) {
		super(lab1, txt, lab2);
	}		// Konstruktor LabelTFLabelETLPanel(String lab1, String txt, String lab2)


	/**
	 * Die Methode wird aufgerufen, wenn der Knopf zur Erhoehung des Wertes des
	 * Textfeldes gedrueckt wurde. Wenn das Textfeld aktiviert ist, wird der
	 * Wert erhoeht, soweit er noch im zulaessigen Bereich liegt.
	 */
	void pbUp_actionPerformed() {
		if (Integer.parseInt(tfText.getText()) == 0) {
			tfText.setText("" + 2);
		} else {
			int val = Integer.parseInt(tfText.getText()) + 1;

			if (checkValue(val)) {
				tfText.setText("" + val);
			} 
		} 
	}		// Methode pbUp_actionPerformed
 

	/**
	 * Die Methode wird aufgerufen, wenn der Knopf zur Verminderung des Wertes des
	 * Textfeldes um 1 gedrueckt wurde. Wenn das Textfeld aktiviert ist, wird der
	 * Wert vermindert, soweit er noch im zulaessigen Bereich liegt.
	 */
	void pbDown_actionPerformed() {
		if (Integer.parseInt(tfText.getText()) == 2) {
			tfText.setText("" + 0);
		} else {
			int val = Integer.parseInt(tfText.getText()) - 1;

			if (checkValue(val)) {
				tfText.setText("" + val);
			} 
		} 
	}		// Methode pbDown_actionPerformed
 

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
				if (!checkValue(myval)) {
					if (myval < 2) {
						tfText.setText("" + 0);
					} else {
						tfText.setText("" + 256);
					} 
				} 
			} catch (Exception err) {}
		}		// if
 	 }		// Methode tfText_lostFocus
 

	/**
	 * Die Methode ueberprueft, ob der ubergebene Wert im erlaubten
	 * Bereich liegt.
	 * @return True, falls Inhalt der Wert im erlaubten Intervall, sonst False.
	 */
	boolean checkValue(int myval) {
		boolean ok = true;

		if (rangeIsSet) {
			if ((myval < minval) || (myval > maxval)) {
				ok = false;
			}		// if
 		 }		// if
 
		if (myval == 1) {
			ok = false;
		} 

		return ok;
	}		// Methode checkValue
 
}






/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

