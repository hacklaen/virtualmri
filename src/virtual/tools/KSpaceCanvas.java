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
import javax.swing.*;


/**
 * Die Klasse kapselt die Zeichenflaeche und das darin darzustellende Bild
 * des k-Raum Anzeigefensters. Sie sorgt dafuer, dass das entsprechende Bild
 * auf der Zeichenflaeche dargestellt wird.
 * 
 * @author   Christian Schalla
 * @author   Andreas Truemper
 * @version  1.0, 10. August 1999
 */
public class KSpaceCanvas extends JPanel {


	/**
	 * Das auf der Zeichenflaeche darzustellende Bild.
	 */
	private Image myImage;


	/**
	 * Standardkonstruktor.
	 */
	public KSpaceCanvas() {}


	/**
	 * Die Methode setzt das zu zeichnende Bild auf das der Methode uebergebene Bild.
	 * @param img Das auf der Zeichenflaeche darzustellende Bild.
	 */
	public void setImage(Image img) {
		myImage = img;
	}		// Methode setImage
 

	/**
	 * Die Methode stellt  das Bild, das in der Instanzvariablen myImage gespeichert
	 * ist auf der Zeichegnflaeche dar. Ist das Bild nicht gesetzt, wird die
	 * Zeichenflaeche geloescht.
	 * @param g Der Graphikkontext.
	 */
	public void paint(Graphics g) {
		if (myImage != null) {
			g.drawImage(myImage, 0, 0, this);
		} else {
			g.clearRect(0, 0, 256, 256);
		}		// if
 	 }		// Mathode paint
 
}






/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

