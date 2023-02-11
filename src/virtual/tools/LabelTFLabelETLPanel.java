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
 * Für die "Echo-Train-Length" sollen nur 1,2,4,8,16 gewählt werden dürfen.
 * Zu diesem Zweck werden die Methoden pbUp_actionPerformed und
 * pbDown_actionPerformed überdefiniert.
 * 
 * @author   Christian Schalla
 * @author   Andreas Truemper
 * @version  1.0, 15. Oktober 1999
 */

public class LabelTFLabelETLPanel extends LabelTFLabelPanel {


	/**
	 * Der Konstruktor legt Text der beiden Beschriftungsfelder sowie den
	 * initialen Inhalt des Textfeldes fest.
	 * @param lab1 Erste Beschriftung.
	 * @param txt Initialer Inhalt des Textfeldes.
	 * @param lab2 Zweite Beschriftung.
	 */
	public LabelTFLabelETLPanel(String lab1, String txt, String lab2) {
		super(lab1, txt, lab2);
	}		// Konstruktor LabelTFLabelETLPanel(String lab1, String txt, String lab2)


	/**
	 * Die Methode wird aufgerufen, wenn der Knopf zur Erhoehung des Wertes des
	 * Textfeldes gedrueckt wurde. Wenn das Textfeld aktiviert ist, wird der
	 * Wert erhoeht, soweit er noch im zulaessigen Bereich liegt.
	 */
	void pbUp_actionPerformed() {
		try {
			int newvalue = Integer.parseInt(tfText.getText()) * 2;

			if (checkValue(newvalue) == false) {
				return;
			} else {
				if (tfText.isEnabled()) {
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
			int newvalue = Integer.parseInt(tfText.getText()) / 2;

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
					tfText.setText("" + (int) Math.pow(2, (int) ((Math.log(myval) / Math.log(2)) + 0.5)));
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
 
		if (((int) Math.pow(2, (int) ((Math.log(myval) / Math.log(2)) + 0.5))) != myval) {
			ok = false;
		} 

		return ok;
	}		// Methode checkValue
 
}






/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

