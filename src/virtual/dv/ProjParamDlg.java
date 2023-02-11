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
 * Berechnung einer 90°-Projektion des Datensatzes. Die Parameter sind die Nummer
 * des Start- und Endbildes, die in die Berechnung mit einbezogen werden sollen,
 * die Schicht, die berechnet werden soll (1-256) und ob eine Interpolation
 * vorgenommen werden soll.
 * <br>
 * <DL><DT><B>Modifications: </B><DD>
 * Thomas Hacklaender 2002.10.12:
 * setLabel seit Java 1.1 deprecated.<br>
 * 
 * @author   Christian Schalla
 * @author   Andreas Truemper
 * @version  1.1, 2002.10.12
 */
public class ProjParamDlg extends JDialog {


	/**
	 * Rahmen, in dem die Bedienelemente dargestellt werdn.
	 */
	private Panel							panel1 = new Panel();


	/**
	 * Label zur Beschreibung des Dialogs
	 */
	private JLabel						lText = new JLabel();


	/**
	 * Elemente zur Eingabe der Bilder, die in die Berechnung einbezogen
	 * werden sollen
	 */
	private LabTFLabTFPanel		pImages = new LabTFLabTFPanel("Von Bild", "1", "bis Bild", "1");


	/**
	 * OK-Knopf
	 */
	private JButton						pbOK = new JButton();


	/**
	 * Abbrechen-Knopf
	 */
	private JButton						pbCancel = new JButton();


	/**
	 * Label fuer die zu berechnende Schicht
	 */
	private JLabel						jLabel1 = new JLabel();


	/**
	 * Textfeld zur Eingabe der zu berechnenden Schicht
	 */
	private LabelTFLabelPanel pSlice = new LabelTFLabelPanel("Schicht", "128", "");


	/**
	 * Kontrollkaestchen zur Auswahl der Interpolation
	 */
	private JCheckBox					cbInterpolate = new JCheckBox();


	/**
	 * Label fuer die Interpolation
	 */
	private JLabel						jLabel2 = new JLabel();


	/**
	 * Rueckreferenz auf das aufrufende Fenster
	 */
	private ViewerFrame				mainFrame;


	/**
	 * Meker fuer die zu berechnende Projektion
	 */
	private int								projection = 1;


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
	public ProjParamDlg(Frame frame, String title, boolean modal) {
		super(frame, title, modal);

		// Darstellen der Bedienelemente
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		try {
			jbInit();
			getContentPane().add(panel1);
			pack();
		} catch (Exception ex) {
			ex.printStackTrace();
		} 

		// Rueckreferenz auf das aufrufende Fenster speichern
		mainFrame = (ViewerFrame) frame;

		// Groesse und Position festlegen
		this.setSize(272, 250);
		this.setResizable(false);
		this.setLocation(frame.getLocationOnScreen().x + (frame.getSize().width / 2) - (this.getSize().width / 2), frame.getLocationOnScreen().y + (frame.getSize().height / 2) - (this.getSize().width / 2));
	}		// Konstruktor ProjParamDlg(Frame frame, String title, boolean modal)


	/**
	 * Der Konstruktor erzeugt ein nicht modales Dialogfenster ohne Titel.
	 * @param frame Referenz auf das aufrufende Fenster.
	 */
	public ProjParamDlg(Frame frame) {
		this(frame, "", false);
	}		// Konstruktor ProjParamDlg(Frame frame)


	/**
	 * Der Konstruktor erzeugt ein Dialogfenster ohne Titel. Es kann angegeben werden,
	 * ob der Dialog modal oder nicht modal sein soll.
	 * @param frame Referenz auf das aufrufende Fenster.
	 * @param Schalter fuer modale / nicht modale Darstellung
	 */
	public ProjParamDlg(Frame frame, boolean modal) {
		this(frame, "", modal);
	}		// Konstruktor ProjParamDlg(Frame frame, boolean modal)


	/**
	 * Der Konstrukor erzeugt ein nicht modales Dialogfenster mit dem angegebenen Titel.
	 * @param frame Referenz auf das aufrufende Fenster.
	 * @param title Titel des Dialogfensters.
	 */
	public ProjParamDlg(Frame frame, String title) {
		this(frame, title, false);
	}		// Konstruktor ProjParamDlg(Frame frame, String title)


	/**
	 * Die Methode richtet die Bedienelemente ein und stellt diese dar.
	 */
	void jbInit() throws Exception {

		// Einrichten des Panels, in dem die bedienelemente dargestellt werden
		panel1.setSize(new Dimension(359, 269));

		// Einrichten des Dialogtextes
		lText.setText("Welche Bilder moechten sie in die");
		lText.setForeground(Color.black);
		lText.setBounds(new Rectangle(5, 9, 296, 17));
		jLabel1.setText("Berechnung einbeziehen?");
		jLabel1.setForeground(Color.black);
		jLabel1.setBounds(new Rectangle(5, 24, 242, 15));

		// Einrichten der Elemente zur Eingabe der Bilder, die in die Berechnung
		// einbezogen werden sollen
		pImages.setBounds(5, 59, 255, 30);

		// Einrichten des OK-Knopfes
		pbOK.setText("OK");
		pbOK.setBounds(new Rectangle(5, 194, 120, 25));
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

		// Einrichten des Abbrechen-Knopfes
		// th 2002.10.12
		// pbCancel.setText("jButton2");
		// pbCancel.setLabel("Abbrechen");
		pbCancel.setText("Abbrechen");
		pbCancel.setBounds(new Rectangle(140, 194, 120, 25));
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

		// Einrichten der Elemente zur Eingabe der Schicht, die berechnet werden soll
		jLabel2.setText("Welche Schicht moechten Sie berechnen?");
		jLabel2.setForeground(Color.black);
		jLabel2.setBounds(new Rectangle(5, 93, 237, 15));

		pSlice.setBounds(5, 123, 255, 30);
		pSlice.setRange(0, 255);

		// Einricht der Checkbox zum Ein/Ausschalten der Interpolation
		cbInterpolate.setText("Interpolation ein/aus");
		cbInterpolate.setSelected(true);
		cbInterpolate.setBounds(new Rectangle(5, 155, 144, 23));

		// Hinzufuegen der Bedienelemente zum Panel
		panel1.setLayout(null);
		panel1.add(lText, null);
		panel1.add(pImages, null);
		panel1.add(pbOK, null);
		panel1.add(pbCancel, null);
		panel1.add(jLabel1, null);
		panel1.add(pSlice, null);
		panel1.add(cbInterpolate, null);
		panel1.add(jLabel2, null);
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
	 * Die Methode setz den Code fuer die zu berechnende Projektion. In Abhaengigkeit
	 * von dieser Einstellung wird eine der beiden 90 Grad Projektionen durchgefuehrt.
	 * @param nr Code fuer die Projektionsart
	 */
	public void setProjectionNr(int nr) {
		projection = nr;
	}		// Methode setProjectionNr(int nr)
 

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
	} 


	/**
	 * Die Methode wird aufgerufen, wenn der Abbrechen-Knopf gedrueckt wurde.
	 * Das Dialogfenster wird dann zerstoert.
	 */
	private void pbCancel_actionPerformed() {
		cancel();
	}		// Methode pbCancel_actionPerformed
 

	/**
	 * Die Methode wird aufgerufen, wenn der OK-Knopf gedrueckt wurde. Es wird
	 * dann eine der beiden Methoden zur Berechnung einer 90°-Projektion
	 * mit den entsprechenden Parametern aufgerufen. Anschliessend wird das
	 * Dialogfenster geschlossen.
	 */
	private void pbOK_actionPerformed() {
		int			from = pImages.getValue1();
		int			to = pImages.getValue2();
		int			slice = pSlice.getValue();
		boolean inter = cbInterpolate.isSelected();

		if (projection == 2) {
			mainFrame.createNewProj2Image(from, to, slice, inter);
		} else {
			mainFrame.createNewProj1Image(from, to, slice, inter);
		} 

		dispose();
	}		// Methode pbOK_actionPerformed()
 
}







/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

