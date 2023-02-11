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
import java.net.*;


/**
 * Diese Klasse stellt einen Frame mit den Copyright-Informationen dar.
 * 
 * <br>
 * Thomas Hacklaender 2006.1.2:
 * Überdeckung durch Meldung des Security Managers am unteren Bildrand berücksichtigt.<br>
  * tha 2002.08.16:
 * Methode setImage von String auf URL geaendert.<br>
 * tha 2000.05.14:
 * Plausible Werte fuer Slider eingetragen. <br>
 * tha 2002.10.29:
 * HTML basierte About-Box. <br>
 * <br>
 * @author   Thomas Hacklaender
 * @version  2002.10.29
 */
public class AboutBox extends JFrame {

	JEditorPane		textEditorPane = new JEditorPane();
	BorderLayout		borderLayout1 = new BorderLayout();


	/**
	 * Constructor declaration
	 * 
	 * 
	 */
	public AboutBox() {
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
		// tha 2006.1.2
                if (System.getSecurityManager() == null) {
                    //this.setSize(510, 450);
                    this.setSize(640, 480);
                } else {
                    // Wenn das Programm innerhalb eines Security Managers läuft,
                    // wird die Warnung "" von 18 Pt am unteren Bildrand eingeblendet.
                    // Sie überdeckt den Bildinhalt.
                    this.setSize(640, 498);
                }

                Dimension frameSize = this.getSize();
                Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
                this.setLocation((scrSize.width - frameSize.width) / 2, (scrSize.height - frameSize.height) / 2);

		this.getContentPane().setLayout(borderLayout1);

		textEditorPane.setPreferredSize(this.getSize());
		textEditorPane.setMinimumSize(new Dimension(256, 256));
		textEditorPane.setEditable(false);

		this.getContentPane().add(textEditorPane, BorderLayout.CENTER);
	} 


	/**
	 * Method declaration
	 * 
	 * 
	 * @param imgName
	 * 
	 * @see
	 */
	public void setFileURL(URL fileURL) {
		try {
			textEditorPane = new JEditorPane(fileURL);
		} catch (Exception e) {
			System.out.println("File-URL: " + fileURL + " not found.");
		} 
		this.getContentPane().add(textEditorPane, BorderLayout.CENTER);

		this.validate();
	} 

}




/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

