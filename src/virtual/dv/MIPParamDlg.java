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
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import virtual.tools.*;


/**
 * Die Klasse implementiert ein Dialogfenster zur Eingabe der Parameter zur
 * Berechnung eines MIP-Bildes. Die Parameter sind in das Start und Endbild
 * der Serie, die in die Berechnung mit einbezogen werden sollen.
 * <br>
 * <DL><DT><B>Modifications: </B><DD>
 * Thomas Hacklaender 2002.10.12:
 * setLabel seit Java 1.1 deprecated.<br>
 * 
 * @author   Christian Schalla
 * @author   Andreas Truemper
 * @version  1.1, 2002.10.12
 */
public class MIPParamDlg extends JDialog {


	/**
	 * Rahmen, in dem die Bedienelemente dargestellt werdn.
	 */
	private Panel				panel1 = new Panel();


	/**
	 * Label zur Beschreibung des Dialogs
	 */
	private JLabel			lText = new JLabel();


	/**
	 * Label zur Beschreibung des Dialogs
	 */
	private JLabel			jLabel1 = new JLabel();


	/**
	 * Eingabefelder fuer die zu beruecksichtigenden Bilder
	 */
	LabTFLabTFPanel			pImages = new LabTFLabTFPanel("Von Bild", "1", "bis Bild", "1");


	/**
	 * Knopf zur Bestaetigung der Parameter
	 */
	private JButton			pbOK = new JButton();


	/**
	 * Knopf zum Abbrechen
	 */
	private JButton			pbCancel = new JButton();


	/**
	 * Rueckreferenz auf das aufrufende Fenster
	 */
	private ViewerFrame mainFrame;


	/**
	 * Der Konstrukor erzeugt ein Dialogfenster mit dem angegebenen Titel.
	 * Es kann angegeben werden, ob der Dialog modal oder nicht modal sein soll.
	 * In diesem Konstruktor wird die Goesse und Lage des Dialogfeldes festgelegt.
	 * Das Dialogfenster wir immer genau mittig uf dem aufrufenden Fenster
	 * dargestellt.
	 * @param frame Referenz auf das aufrufende Fenster.
	 * @param title Titel des Dialogfensters.
	 * @param Schalter fuer modale / nicht modale Darstellung
	 */
	public MIPParamDlg(Frame frame, String title, boolean modal) {
		super(frame, title, modal);
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);

		// Initialisieren der Bedienelemente
		try {
			jbInit();
			getContentPane().add(panel1);
			pack();
		} catch (Exception ex) {
			ex.printStackTrace();
		} 

		// Rueckreferenz speichern
		mainFrame = (ViewerFrame) frame;

		// Groesse und Position des dialog festlegen
		this.setSize(272, 150);
		this.setResizable(false);
		this.setLocation(frame.getLocationOnScreen().x + (frame.getSize().width / 2) - (this.getSize().width / 2), frame.getLocationOnScreen().y + (frame.getSize().height / 2) - (this.getSize().width / 2));
	}		// Konstruktor MIPParamDlg(Frame frame, String title, boolean modal)


	/**
	 * Der Konstruktor erzeugt ein nicht modales Dialogfenster ohne Titel.
	 * @param frame Referenz auf das aufrufende Fenster.
	 */
	public MIPParamDlg(Frame frame) {
		this(frame, "", false);
	}		// Konstruktor MIPParamDlg(Frame frame)


	/**
	 * Der Konstruktor erzeugt ein Dialogfenster ohne Titel. Es kann angegeben werden,
	 * ob der Dialog modal oder nicht modal sein soll.
	 * @param frame Referenz auf das aufrufende Fenster.
	 * @param Schalter fuer modale / nicht modale Darstellung
	 */
	public MIPParamDlg(Frame frame, boolean modal) {
		this(frame, "", modal);
	}		// Konstruktor MIPParamDlg(Frame frame, boolean modal)


	/**
	 * Der Konstrukor erzeugt ein nicht modales Dialogfenster mit dem angegebenen Titel.
	 * @param frame Referenz auf das aufrufende Fenster.
	 * @param title Titel des Dialogfensters.
	 */
	public MIPParamDlg(Frame frame, String title) {
		this(frame, title, false);
	}		// Konstruktor MIPParamDlg(Frame frame, String title)


	/**
	 * Die Methode richtet die Bedienelemente ein und stellt diese dar.
	 */
	private void jbInit() throws Exception {

		// Einrichten des Beschreibungstextes
		panel1.setSize(new Dimension(234, 151));
		lText.setText("Welche Bilder moechten sie in die MIP-");
		lText.setForeground(Color.black);
		lText.setBounds(new Rectangle(5, 9, 296, 17));
		jLabel1.setText("Berechnung einbeziehen?");
		jLabel1.setForeground(Color.black);
		jLabel1.setBounds(new Rectangle(5, 24, 242, 15));

		// Einrichten der Texteingabefelder mit Beschriftung
		pImages.setBounds(5, 59, 255, 30);

		// OK-Knopf einrichten
		pbOK.setText("OK");
		pbOK.setBounds(new Rectangle(5, 93, 120, 25));
		pbOK.addActionListener(new java.awt.event.ActionListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void actionPerformed(ActionEvent e) {
				pbOK_actionPerformed();
			} 

		});

		// Abrrechen-Knopf einrichten
		// th 2002.10.12
		// pbCancel.setText("jButton2");
		// pbCancel.setLabel("Abbrechen");
		pbCancel.setText("Abbrechen");
		pbCancel.setBounds(new Rectangle(140, 93, 120, 25));
		pbCancel.addActionListener(new java.awt.event.ActionListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void actionPerformed(ActionEvent e) {
				pbCancel_actionPerformed();
			} 

		});

		// Alle Bedienelemente dem Panel hinzufuegen
		panel1.setLayout(null);
		panel1.add(lText, null);
		panel1.add(pImages, null);
		panel1.add(pbOK, null);
		panel1.add(pbCancel, null);
		panel1.add(jLabel1, null);
	}		// Methode jbInit
 

	/**
	 * Die Methode setzt den Inhalt der beiden Textfelder auf die angegebenen Werte.
	 * Diese Mthode wird zur sinnvollen Voreinstellung der Textfelder benoetigt und vom
	 * DICOM-Viewer aufgerufen.
	 * @param from Inhalt des ersten Textfeldes (Startbild fuer die Berechnung).
	 * @param to Inhalt des zweiten Textfeldes (Endbild fuer die Berechnung).
	 */
	public void setImages(int from, int to) {
		pImages.setRange1(from, to);
		pImages.setRange2(from, to);
		pImages.setValue1("" + from);
		pImages.setValue2("" + to);
	}		// Methode setImages(int from, int to)
 

	/**
	 * Die Methode schliesst das Dialogfenster, wenn der Schliessen-Knopf gedrueckt
	 * wurde.
	 * @param e Ereignis, das bei der Zustandsaenderung des Fensters erzeugt wird.
	 */
	protected void processWindowEvent(WindowEvent e) {
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			cancel();
		} 
		super.processWindowEvent(e);
	}		// Methode processWindowEvent(WindowEvent e)
 

	/**
	 * Die Methode zerstoert das aktuelle Dialogfenster. Sie wird aufgerufen, wenn
	 * der Schliessen- oder der Abbrechen-Knopf gedrueckt wurde.
	 */
	private void cancel() {
		dispose();
	}		// Methode cancel()
 

	/**
	 * Die Methode wird aufgerufen, wenn der Abbrechen-Knopf gedrueckt wurde.
	 * Das Dialogfenster wird dann zerstoert.
	 */
	private void pbCancel_actionPerformed() {
		cancel();
	}		// Methode pbCancel_actionPerformed()
 

	/**
	 * Die Methode wird aufgerufen, wenn der OK-Knopf gedrueckt wurde. Es wird
	 * dann die Methode zur Berechnung des MIP-Bildes mit den entsprechenden
	 * Paramtern aufgerufen. Anschliessend wird das Dialogfenster geschlossen.
	 * Das Dialogfenster wird dann zerstoert.
	 */
	private void pbOK_actionPerformed() {
		mainFrame.calcMIPImage(pImages.getValue1(), pImages.getValue2());
		cancel();
	}		// Methode pbOK_actionPerformed()
 
}






/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

