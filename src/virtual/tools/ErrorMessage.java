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

// import virtual.mrt.*;


/**
 * Diese Klasse stellt den text einer Java-Exception (Ausnahme)
 * in einem sog. Message-Dialog dar. Dieser Dialog wird z.B. dann verwendet,
 * wenn der Benutzer Eingaben in ein Eingabefeld vorgenommen hat, die nicht
 * dem zulaessigen Wertebereich entsprechen.
 * <DL><DT><B>Modifications: </B><DD>
 * Thomas Hacklaender 2002.10.13:
 * Strings internationalisiert.<br>
 * </DD></DL>
 * @author   Christian Schalla
 * @author   Andreas Truemper
 * @version  1.2, 2002.10.13
 */
public class ErrorMessage {


	/**
	 * Rueckreferenz uaf das aufrufende Fenster.
	 */
	private static Frame	mainFrame;


	/**
	 * Standradkonstruktor.
	 */
	public ErrorMessage() {}


	/**
	 * Die Methode zeigt den Text einer Exception als Fehlerdialog an.
	 * @param e Die Ausnahme, deren Text im Fehlerdialog angezeigt werden soll.
	 */
	public static void showMessage(Exception e) {
		Toolkit.getDefaultToolkit().beep();
		JOptionPane.showMessageDialog(mainFrame, e.getMessage(), java.util.ResourceBundle.getBundle("virtual/tools/resources/tools_loc").getString("errormessage.title"), JOptionPane.ERROR_MESSAGE);
	}		// Methode showMessage
 

	/**
	 * Die Methode zeigt den Text einer Exception als Fehlerdialog an.
	 * @param e Die Ausnahme, deren Text im Fehlerdialog angezeigt werden soll.
	 */
	public static void showMessage(String str) {
		Toolkit.getDefaultToolkit().beep();
		JOptionPane.showMessageDialog(mainFrame, str, java.util.ResourceBundle.getBundle("virtual/tools/resources/tools_loc").getString("errormessage.title"), JOptionPane.ERROR_MESSAGE);
	}		// Methode showMessage
 

	/**
	 * Die Methode setzt die Referenz auf das aufrufende Fenster.
	 * @param mnFrame Referenz auf das aufrufende Fenster.
	 */
	public static void setMainFrame(Frame mnFrame) {
		mainFrame = mnFrame;
	}		// Methode setMainFrame
 
}






/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

