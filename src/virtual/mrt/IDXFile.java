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
package virtual.mrt;

import java.io.*;
import java.util.*;
import javax.swing.*;


/**
 * Diese Klasse implementiert einen Datei-Filter fuer Dateien mit der Endung .idx,
 * der fuer einen Dateiauswahldialog verwendet werden kann. Der Filter unterdrueckt
 * dann alle Dateien, die nicht die Dateiendung haben, die durch diesen Filter
 * vorgegeben ist.<br>
 * Thomas Hacklaender 2002.10.13:
 * Strings internationalisiert.<br>
 * @author   Christian Schalla
 * @author   Andreas Truemper
 * @version  1.2, 2002.10.13
 */
public class IDXFile extends javax.swing.filechooser.FileFilter {

	// th 2005.11.11, added
	ResourceBundle	frameRsrc = PropertyResourceBundle.getBundle("virtual/mrt/resources/vmrt_frame");


	/**
	 * Die Methode prueft fuer eine angegebene Datei, ob diese die Endung idx
	 * hat. Ist das der Fall, wird True zurueckgeliefrt, sonst False.
	 * @param myFile Die Datei, deren Endung ueberprueft werden soll.
	 * @return True, wenn die Datei die Endung .idx hat, sonst False.
	 */
	public boolean accept(File myFile) {
		if (myFile.isDirectory()) {
			return true;
		} 
		boolean ok = false;
		String	suf = getSuffix(myFile);

		if (suf != null) {
			if (suf.compareTo("idx") == 0) {
				ok = true;
			} 
		} else {
			ok = true;
		} 
		return ok;
	}		// Methode accept
 

	/**
	 * Die Methode liefert eine Beschreibung dieses Filters. Die Beschreibung
	 * wird im Dateiauswahldialog angezeigt. Anhand dieser Beschreibung kann der
	 * Benutzet den Filter identifizieren und auswaehlen.
	 * @return Eine Beschreibung des Dateifilters.
	 */
	public String getDescription() {
		return frameRsrc.getString("idxfile.description");
	}		// Methode getDescription
 

	/**
	 * Die Methode liefert den Suffix eines Dateinamens, also die Zeichen, die
	 * hinter dem letzten Punkt folgen.
	 * @param f Die Datei, deren Suffix bestimmt werden soll.
	 * @return Suffix der Datei.
	 */
	private String getSuffix(File f) {
		String	s = f.getPath();
		String	suffix = null;
		int			i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			suffix = s.substring(i + 1).toLowerCase();
		} 
		return suffix;
	}		// Methode getSuffix
 
}






/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

