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

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;


/**
 * Die Klasse erweitert das Standardmodell fuer Dokumente um die Methode
 * insertString. Via Integer.parseInt wird geprueft, ob die eingegebene Zeichenkette
 * gueltig ist. Dies ist der Fall, wenn es sich um eine (positive oder negative)
 * Ganzzahl handelt.
 * 
 * @author   Christian Schalla
 * @author   Andreas Truemper
 * @version  1.0, 15. Oktober 1999
 */
public class IntegerDocument extends PlainDocument {


	/**
	 * Ueberladene Methode der Klasse PlainDocument. Sie sorgt dafuer, daﬂ nur Zahlen als
	 * Eingabe akzeptiert werden.
	 * @param  offset Einfuegeposition.
	 * @param  s Die Zeichenkette im Dokument.
	 */
	public void insertString(int offset, String s, AttributeSet attributeSet) throws BadLocationException {

		// Integer eingegeben ?
		if ((offset == 0) && (s.compareTo("-") == 0)) {
			super.insertString(offset, s, attributeSet);
		} else {
			try {
				Integer.parseInt(s);
			} catch (Exception ex) {

				// Fehler bei parseInt: Signalton ausgeben und nichts tun
				Toolkit.getDefaultToolkit().beep();
				return;
			} 

			// parseInt hat funktioniert: Fuege String durch Aufruf der Methode
			// insertString der Oberklasse ein
			super.insertString(offset, s, attributeSet);
		}		// if-else
 	 }		// Methode insertString
 
}






/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

