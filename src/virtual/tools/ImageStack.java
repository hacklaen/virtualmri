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

import java.awt.Image;
import java.util.*;


/**
 * Die Klasse verwaltet einen Stapel von ImagePlus-Objekten. Dies sind Bild-Objekte,
 * die ein 12-Bit Bild, ein gefenstertes 8-Bit Bild und einige Zusatzinformationen
 * enthalten.
 * Die Klasse ImageStack bietet Moeglichkeiten, ImagePlus-Objekte zum Stapel
 * hinzuzufuegen und zu loeschen sowie ImagePlus-Objekte aus dem Stapel
 * zurueckzuliefern.
 * 
 * @author   Christian Schalla
 * @author   Andreas Truemper
 * @version  1.0, 15. Oktober 1999
 */
public class ImageStack {


	/**
	 * Der Vektor enthaelt alle ImagePlus-Objekte.
	 */
	private Vector	images;


	/**
	 * Standardkonstruktor. Er initialisiert einen Vektor der Groesse 256 mit
	 * leeren Objekten.
	 */
	public ImageStack() {
		images = new Vector(256, 10);
		for (int i = 0; i < 256; i++) {
			images.addElement(null);
		} 
	}


	/**
	 * Die Methode bestimmt die Anzahl der ImagePlus-Objekt im Stapel und liefert
	 * diese Anzahl zurueck.
	 * @return Die Anzahl der ImagePlus-Objekte im Stapel.
	 */
	public int getStackSize() {
		int max = 0;

		for (int i = 1; i < images.size(); i++) {
			if (images.elementAt(i) != null) {
				max++;
			} 
		} 
		return max;
	} 


	/**
	 * Die Methode bestimmt die erste freie Position im Vektor der ImagePlus-Objekte.
	 * @return Die erste freie Stelle im Stapel.
	 */
	public int getNextFreePosition() {
		int pos = 0;

		for (int i = 1; i < images.size(); i++) {
			if (images.elementAt(i) == null) {
				pos = i;
				break;
			} 
		} 
		return pos;
	} 


	/**
	 * Die Methode erzeugt ein neues ImagePlus-Objekt an der angegebenen Position
	 * im Stapel. Dazu wird das uebergebene DcmImage-Objekt verwendet.
	 * @param iposition Die Position, an der das  ImagePlus-Objekt im Stapel angelegt
	 * werden soll.
	 * @param di Das DcmImage-Objekt, aus dem ein ImagePlus-Objekt erzeugt werden soll.
	 * @return Das neu erzeugte ImagePlus-Objekt.
	 */
	public ImagePlus createPictureAtPos(int iposition, DcmImage di) {
		ImagePlus ip = new ImagePlus(di);

		images.setElementAt(ip, iposition);
		return ip;
	} 


	/**
	 * Die Methode fuegt ein uebergebenes ImagePlus-Objekt an der angegebenen Stelle
	 * in den Stapel ein.
	 * @param iposition Die Position, an der das  ImagePlus-Objekt im Stapel eingefuegt
	 * werden soll.
	 * @param myip Das ImagePlus-Objekt, das in den Stapel eingefuegt werden soll.
	 */

	public void setPictureAtPos(int iposition, ImagePlus myip) {
		images.setElementAt(myip, iposition);
	} 


	/**
	 * Die Methode liefert ein ImagePlus-Objekt aus dem Stapel zurueck. Die Position
	 * des zurueckzuliefernden Bildes muss angegeben werden. Ist das Bild nicht
	 * vorhanden, wird null zurueckgeliefert.
	 * @param iposition Die Position des gewuenschten Bildes im Stapel.
	 * @return Das angeforderte ImagePlus-Objekt.
	 */
	public ImagePlus getPictureAtPos(int iposition) {
		ImagePlus ip = null;

		try {
			ip = ((ImagePlus) images.elementAt(iposition));
		} catch (Exception err) {}
		return ip;
	}		// Methode getPictureAtPos
 

	/**
	 * Die Methode loescht den gesamten Bildstapel. Danach stehen keine
	 * ImagePlus-Objekte mehr zur Verfuegung.
	 */
	public void clearImageStack() {
		for (int i = 0; i < 256; i++) {
			images.setElementAt(null, i);
		} 
	}		// Methode clearImageStack
 

	/**
	 * Die Methode loescht das Bild an der angegebenen Position des Bildstapels.
	 * @param i Die Position des Bildes, das geloescht werden soll.
	 */
	public void deletePictureAtPos(int i) {
		images.setElementAt(null, i);
	}		// Methode deletePictureAtPos
 

}






/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

