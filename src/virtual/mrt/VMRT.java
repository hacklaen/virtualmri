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
import java.util.*;
import javax.swing.UIManager;
import javax.swing.*;

import virtual.tools.*;


/**
 * Die Klasse ist die Hauptklasse des virtuellen Tomographen. Sie wird nur zum
 * Starten benoetigt, indem eine Instanz von VMRT erzeugt wird. In deren Konstruktor
 * wird dan wiederum eine Instnaz von VMRTFrame erzeugt, wodurch das Hauptfenster
 * des virtuellen Tomographen dargestellt wird.
 * <DL><DT><B>Modifications: </B><DD>
 * Thomas Hacklaender 2006.1.2:
 * Applet/Application als Startklassen getrennt.<br>
 * Thomas Hacklaender 2000.7.2:
 * Spash-Screen zeigt die AboutBox
 * Thomas Hacklaender 2002.10.13:
 * Strings internationalisiert.<br>
 * </DD></DL>
 * @author   Christian Schalla
 * @author   Andreas Truemper
 * @version  1.2, 2002.10.13
 */
public class VMRT {


	/**
	 * Standardkonstruktor.
	 */
	public VMRT() {}

        
	/**
	 * Die Methode erzeugt eine neue Instanz der Klasse VMRT und setzt das
	 * Look&Feel. Es wird das Systemtypische Look&Feel verwendet.
	 * @param args Befehlszeilenargumente. Sie werden hier einfach ignoriert.
	 */
	public static void main(String[] args) {
		(new VMRT()).runApplication();
	} 


	/**
	 * Method declaration
	 * 
	 * 
	 * @param isApplication
	 * 
	 * @see
	 */
	public void runApplication() {

            VMRTFrame.setLookAndFeel();

            // Instanz des Hauptfensters erzeugen
            VMRTFrame frame = new VMRTFrame(true);
            frame.showCentered();
	} 

}






/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

