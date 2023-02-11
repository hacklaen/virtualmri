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

import java.awt.*;
import java.net.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

import virtual.tools.*;


/**
 * Diese Klasse stellt einen Frame mit Fall-Informationen zu den Rohdaten dar.
 * 
 * <DL><DT><B>Modifications: </B><DD>
 * Thomas Hacklaender 2000.8.14:
 * setInfo(File, String) geloescht, da es nicht mehr gebraucht wird.<br>
 * Thomas Hacklaender 2002.10.13:
 * Strings internationalisiert.<br>
 * </DD></DL>
 * @author   Thomas Hacklaender
 * @version  1.3, 2002.10.27
 */
public class CaseInfoFrame extends JFrame {

	// th 2005.11.11, added
	ResourceBundle	frameRsrc = PropertyResourceBundle.getBundle("virtual/mrt/resources/vmrt_frame");

	// th 2002.10.27
	final Dimension defaultSize = new Dimension(500, 560);

	JScrollPane			textScrollPane = new JScrollPane();
	JEditorPane			textEditorPane = new JEditorPane();
	BorderLayout		borderLayout1 = new BorderLayout();


	/**
	 * Standardkonstruktor. Er initialisiert das Fenster.
	 */
	public CaseInfoFrame() {
		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}


	/**
	 * Die Methode richtet die Bedienelemente ein und stellt diese dar.
	 */
	private void jbInit() throws Exception {

		// Groesse des Fensters festlegen.
		this.setSize(defaultSize);
		Dimension si = getSize();
		Dimension scrRes = this.getToolkit().getScreenSize();

		setLocation((scrRes.width - si.width) / 2, (scrRes.height - si.height) / 2);

		this.getContentPane().setLayout(borderLayout1);

		textScrollPane.setPreferredSize(defaultSize);
		textEditorPane.setPreferredSize(defaultSize);
		textEditorPane.setMinimumSize(new Dimension(256, 256));
		textEditorPane.setEditable(false);

		this.getContentPane().add(textScrollPane, BorderLayout.CENTER);
		textScrollPane.getViewport().add(textEditorPane, null);
	} 


	/**
	 * Fuellt die EditorPane mit dem Inhalt einer Datei.
	 * 
	 * @param infoFile  Verweis auf die Datei.
	 * @param title     Neuer Titel des Fensters.
	 */

	/*
	 * public void setInfo(File infoFile, String title) {
	 * setInfo(Tools.file2URL(infoFile), title);
	 * }
	 */


	/**
	 * Fuellt die EditorPane mit dem Inhalt einer URL.
	 * @param infoFileURL Verweis auf die URL
	 * @param title       Neuer Titel des Fensters.
	 */
	public void setInfo(URL infoFileURL, String title) {
		this.setTitle(frameRsrc.getString("caseinfoframe.dialog.info.title") + " " + title);
		try {
			textEditorPane = new JEditorPane(infoFileURL);
		} catch (Exception e) {
			System.out.println("URL: " + infoFileURL + " not found.");
		} 
		textScrollPane.getViewport().removeAll();
		textScrollPane.getViewport().add(textEditorPane, null);

		this.validate();
	} 

}






/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

