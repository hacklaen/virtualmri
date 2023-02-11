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


/**
 * Die abstarkte Klasse dient als Basis fuer die scrollbaren Darstellungsebenen
 * fuer ein oder vier Bilder. Alle abgeleiteten Klassen muessen mindestens eine
 * Methode zum darstellen eines Bildes implementieren.
 * 
 * @author   Thomas Hacklaender
 * @version  1.0, 2000.8.17
 */
public abstract class ImagePanel extends JScrollPane {


	/**
	 * Die Methode erzeugt aus dem uebergebenen DcmImage-Objekt ein neues ImagePlus-Objekt
	 * und fuegt dieses in den Bildstapel ein.
	 * @param di Das DcmImag-Objekt, aus dem ein ImagePlus-Objekt erzeugt werden soll.
	 * @return Das neu erzeugte ImagePlus-Objekt.
	 */
	public abstract ImagePlus createNewImage(DcmImage di);


	/**
	 * Die Methode fuegt das uebergebene ImagePlus-Objekt in den Bildstapel ein.
	 * @param ip Das ImagePlus-Objekt, das in den Stack eingefuegt werden soll.
	 * @return Die Position, an der das ImagePlus-Objekt eingefuegt wurde.
	 */
	public abstract int createNewImage(ImagePlus ip);

}



/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

