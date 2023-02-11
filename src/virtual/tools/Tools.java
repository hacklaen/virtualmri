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

import java.net.*;
import java.io.*;
import javax.swing.*;


/**
 * Die Klasse stellt einige Grundlegende Hilfsfunktionen zur Verfuegung, die
 * an verschiedenen Stellen benoetigt werden.
 * <DL><DT><B>Modifications: </B><DD>
 * Thomas Hacklaender 2000.07.13:
 * Methoden url2File und file2URL implementiert. <br>
 * Thomas Hacklaender 2002.10.13:
 * Strings internationalisiert.<br>
 * </DD></DL>
 * @author   Christian Schalla
 * @author   Andreas Truemper
 * @version  1.2, 2002.10.16
 */
public class Tools {


	/**
	 * Standardkonstruktor
	 */
	public Tools() {}		// Standardkonstruktor


	/**
	 * Die Methode wandelt eine Zeichenkette in eine Ganzzahl um. Zuvor muss
	 * sichergestellt sein, dass die Zeichenkette auch wirklich nur eine
	 * Ganzzahl enthaelt
	 * @param st Die umzuwandelnde Zeichenkette
	 * @return Die Ganzahl, die aus der Zeichenkette gewonnen wurde
	 */
	public static int stringToInt(String st) {
		int i = 0;

		if (checkInteger(st)) {
			i = Integer.parseInt(st);
		} 
		return i;
	}		// Methode stringToInt
 

	/**
	 * Die Methode prueft, ob es sich bei der uebergebenen Zeichenkette um eine
	 * Ganzzhal handelt.
	 * @param st Die zu pruefende Zeichenkette
	 * @return Wahrheitswert. true, wenn Zeichenkette eine Ganzzahl ist, sonst false
	 */
	public static boolean checkInteger(String st) {
		boolean b = false;

		try {
			int i = Integer.parseInt(st);

			b = true;
		} catch (Exception e) {
			b = false;
		}		// catch
		 return b;
	}		// Methode checkInteger
 

	/**
	 * Die Method prueft, ob eine Ganzzahl innerhalb eines Intervalls liegt.
	 * @param value Die zu pruefende Zahl
	 * @param Die untere Schranke des Intervalls
	 * @param Die obere Schranke des Intervalls
	 * @return Wahrheitswert. true, wenn Wert innerhalb des Intervalls liegt, sonst false
	 */
	public static boolean checkIntRange(String value, int min, int max) {
		boolean b = false;

		if (checkInteger(value)) {
			int val = stringToInt(value);

			if ((val >= min) & (val <= max)) {
				b = true;
			} 
		} 
		return b;
	}		// Methode checkIntRange
 

	/**
	 * Die Methode blendet eine Fehlerbox in der Bildschirmmitte auf.
	 * @param msg Der in der Dialogbox darzustellende Text.
	 */
	public static void showErrorMessage(String msg) {
		JOptionPane.showMessageDialog(null, msg, java.util.ResourceBundle.getBundle("virtual/tools/resources/tools_loc").getString("tools.errormessage.title"), JOptionPane.ERROR_MESSAGE);
	}		// Methode showErrorMessage
 

	/**
	 * Die Methode liefert zu einer URL ein Objekt vom Typ File. Die URL muss
	 * muss vom Protokoll-Typ "file" sein. Sonst wird 'null' zurueckgegeben.
	 * 
	 * @param theURL  Die URL .
	 * @return        Das File-Objekt.
	 * @author Thomas Hacklaender
	 */
	public static File url2File(URL theURL) {
		if (theURL.getProtocol().compareTo("file") != 0) {
			return null;
		} 
		String	p = theURL.getFile();
		int			i = p.lastIndexOf('/');

		if (p.startsWith("/")) {
			;
			p = p.substring(1, i);
		} else {
			p = p.substring(0, i);
		} 

		p = p.replace('/', File.separatorChar);

		String	c = theURL.getFile();
		int			j = c.lastIndexOf('/');

		c = c.substring(j + 1);

		return new File(p, c);
	} 


	/**
	 * Die Methode liefert zu einem File ein Objekt vom Typ URL.
	 * @param theFile Der File.
	 * @return        Das URL-Objekt.
	 */
	public static URL file2URL(File theFile) {
		try {
			return new URL("file", "", theFile.getPath().replace(File.separatorChar, '/'));
		} catch (Exception e) {
			return null;
		} 
	} 

}








/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

