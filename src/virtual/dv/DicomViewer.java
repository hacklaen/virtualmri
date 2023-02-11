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
package virtual.dv;

import java.awt.*;
import java.applet.*;
import javax.swing.*;

import virtual.tools.*;


/**
 * Die Klasse ist die Hauptklasse des DICOM-Viewers. Sie wird nur zum
 * Starten benoetigt, indem sie eine Instanz von DicomViewer erzeugt und deren
 * init-Methode aufruft. Dort wird dann eine Instanz von ViewerFrame erzeugt,
 * wodurch das DICOM-Viewer-Fenster dargestellt wird.
 * 
 * @author   Christian Schalla
 * @author   Andreas Truemper
 * @version  2002.8.19
 * 
 * <DL><DT><B>Modifications: </B><DD>
 * Thomas Hacklaender 2000.7.2:
 * Spash-Screen zeigt die AboutBox
 * </DD></DL>
 */
public class DicomViewer extends Applet {

	private boolean			packFrame = false;


	/**
	 * Referenz auf die Fensterklasse.
	 */
	private ViewerFrame frame;


	/**
	 * Standardkonstruktor. Es wird eine Instanz von ViewerFrame erzeugt.
	 * Dadurch wird das Hauptfenster des DICOM-Viewers dargestellt.
	 */
	public DicomViewer() {}


	/**
	 * In dieser Methode wird das Look&Feel gesetzt. Es wird das Systemtypische
	 * Look&Feel verwendet. Ausserdem wird eine Instanz dieser Klasse instanziiert
	 * und automatisch gestartet.
	 * @param args Befehlszeilenargumente. Sie werden hier einfach ignoriert.
	 */
	public static void main(String[] args) {
		DicomViewer dv = new DicomViewer();

		dv.runAppletApplication(true);
	} 


	/**
	 * Method declaration
	 * 
	 * 
	 * @see
	 */
	public void init() {
		runAppletApplication(false);
	} 


	/**
	 * Method declaration
	 * 
	 * 
	 * @param isApplication
	 * 
	 * @see
	 */
	public void runAppletApplication(boolean isApplication) {
		System.out.println("Version 2.2");
		System.out.println("(c) 2000-2002 T. Hacklaender, C. Schalla, A. Truemper");
		System.out.println("Das Programm unterliegt der GPL Lizenz");

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {}

		Dimension scrRes = this.getToolkit().getScreenSize();

		AboutBox	abox = new AboutBox();

		// th 2002.10.29
		// abox.setImage(this.getClass().getResource("resources/dv.jpg"));
		abox.setFileURL(this.getClass().getResource("resources/splash_de.htm"));
		abox.show();

		// Instanz des Hauptfensters erzeugen
		frame = new ViewerFrame();

		frame.setAppType(isApplication);

		// Frames validieren, die eine voreingestellte Groesse besitzen
		// Frames packen, die nuetzliche bevorzugte Infos ueber die Groesse besitzen,
		// z.B. aus ihrem Layout
		if (packFrame) {
			frame.pack();
		} else {
			frame.validate();
		} 
		Dimension fs = frame.getSize();

		frame.setLocation((scrRes.width - fs.width) / 2, (scrRes.height - fs.height) / 2);
		frame.setVisible(true);

		abox.dispose();
	} 


}






/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

