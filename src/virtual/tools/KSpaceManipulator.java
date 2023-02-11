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
import java.awt.event.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import virtual.tools.*;


/**
 * Die Klasse implementiert das Fenster und die Funktionen zur k-Raum-Manipulation.
 * Dazu zaehlen das Loeschen von Zeilen und Spalten am Rand des k-Raums, das Loeschen
 * eines zentral im k-Raum gelegenen Rechtecks und das Loeschen von Zeilen bzw.
 * Spalten in einem bestimmten Abstand.
 * <DL><DT><B>Modifications: </B><DD>
 * Thomas Hacklaender 2006.1.2:
 * Überdeckung durch Meldung des Security Managers am unteren Bildrand berücksichtigt.<br>
 * Thomas Hacklaender 2000.8.17:
 * Im Konstruktor Code zum zentrieren des Frames auf dem Bildschirm eingefuegt.<br>
 * Thomas Hacklaender 2000.8.17:
 * Konstruktor KSpaceManipulator(ImagePlus, ViewerFrame) auf
 * KSpaceManipulator(ImagePlus, JFrame) geaendert. Feld myViewerFrame vom Typ
 * ViewerFrame auf JFrame geaendert.<br>
 * Thomas Hacklaender 2000.8.13:
 * In der Methode jbInit die Hoehe des Frames von 539 auf 547 Pixel gesetzt.<br>
 * Thomas Hacklaender 2002.10.13:
 * Strings internationalisiert.<br>
 * </DD></DL>
 * @author   Christian Schalla
 * @author   Andreas Truemper
 * @version  1.2, 2002.10.16
 */
public class KSpaceManipulator extends JFrame {


	/**
	 * Standard-Mauszeiger.
	 */
	private final Cursor		DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);


	/**
	 * Mauszeiger fuer die Fensterung.
	 */
	private final Cursor		WINDOW_CURSOR = new Cursor(Cursor.MOVE_CURSOR);


	/**
	 * Das ImagePlus-Objekt des Bildes, dessen k-Raum manipuliert werden soll.
	 */
	ImagePlus								sourceImg;


	/**
	 * Der Realteil der Fourier-Transformation des Originalbildes.
	 */
	float[][]								origKSpaceReal;


	/**
	 * Der Imaginaerteil der Fourier-Transformation des Originalbildes.
	 */
	float[][]								origKSpaceImag;


	/**
	 * Der manipulierte Realteil der Fourier-Transformation des Originalbildes.
	 */
	float[][]								manipKSpaceReal;


	/**
	 * Der manipulierte Imaginaerteil der Fourier-Transformation des Originalbildes.
	 */
	float[][]								manipKSpaceImag;


	/**
	 * Das Magnitudenbild des Original-k-Raums (zur Darstellung).
	 */
	int[]										intKSpace;


	/**
	 * Das Magnitudenbild des manipulierten k-Raums (zur Darstellung).
	 */
	int[]										manipIntKSpace;


	/**
	 * Letze Mausklick-x-Koordinate.
	 */
	private int							lastMouseClickX;


	/**
	 * Letze Mausklick-y-Koordinate.
	 */
	private int							lastMouseClickY;


	/**
	 * Zeitpunkt des letzten Mausklicks.
	 */
	private long						lastTime;


	/**
	 * Fensterungszentrum-Einstellungen beim letzte Mausklick.
	 */
	private int							lastCenter;


	/**
	 * Fensterungsbreite-Einstellungen beim letzte Mausklick.
	 */
	private int							lastWindow;


	/**
	 * Die Zeichenflaeche zur Darstellung der vier Bilder.
	 */
	KSpaceManipulatorCanvas pCanvas;	// = new KSpaceManipulatorCanvas();


	/**
	 * Der Rahmen für die Bedienelemente zur k-Raum-Manipulation.
	 */
	JPanel									pToolbar = new JPanel();


	/**
	 * Der Rahmen fuer die Bedienelemente zum Loeschen der Raender.
	 */
	JPanel									pDeleteMargins = new JPanel();


	/**
	 * Der Rahmen fuer die Bedienelemente zum Loeschen eines Rechtecks.
	 */
	JPanel									pDeleteRect = new JPanel();


	/**
	 * Der Rahmen fuer die Bedienelemente zum Loeschen einzelner Zeilen/Spalten.
	 */
	JPanel									pDeleteLines = new JPanel();


	/**
	 * Kontrollkästchen zum Ein-/Ausschalten des Manipulators zum Loeschen der Ränder.
	 */
	JCheckBox								cbDeleteMargins = new JCheckBox();


	/**
	 * Kontrollkästchen zum Ein-/Ausschalten des Manipulators zum Loeschen des Rechtecks.
	 */
	JCheckBox								cbDeleteRect = new JCheckBox();


	/**
	 * Kontrollkästchen zum Ein-/Ausschalten des Manipulators zum Loeschen der Zeilen/Spalten.
	 */
	JCheckBox								cbDeleteLines = new JCheckBox();


	/**
	 * Knopf, um die Ruecktransformation des manipulierten k-Raums zu berechnen.
	 */
	JButton									pbCalcImage = new JButton();


	/**
	 * Knopf, um das ruecktransformierte Bild in den DicomViewer zu uebertragen.
	 */
	JButton									pbtoDV = new JButton();


	/**
	 * Bedienelemente für die am linken Rand zu loeschenden Spalten.
	 */
	LabelTFLabelPanel				tfDelLeftColumns = new LabelTFLabelPanel(java.util.ResourceBundle.getBundle("virtual/tools/resources/tools_loc").getString("kspacemanipulator.margin.left.title"), "0", java.util.ResourceBundle.getBundle("virtual/tools/resources/tools_loc").getString("kspacemanipulator.margin.left.unit"));


	/**
	 * Bedienelemente für die am rechten Rand zu loeschenden Spalten.
	 */
	LabelTFLabelPanel				tfDelRightColumns = new LabelTFLabelPanel(java.util.ResourceBundle.getBundle("virtual/tools/resources/tools_loc").getString("kspacemanipulator.margin.right.title"), "0", java.util.ResourceBundle.getBundle("virtual/tools/resources/tools_loc").getString("kspacemanipulator.margin.right.unit"));


	/**
	 * Bedienelemente für die am oberen Rand zu loeschenden Zeilen.
	 */
	LabelTFLabelPanel				tfDelTopRows = new LabelTFLabelPanel(java.util.ResourceBundle.getBundle("virtual/tools/resources/tools_loc").getString("kspacemanipulator.margin.top.title"), "0", java.util.ResourceBundle.getBundle("virtual/tools/resources/tools_loc").getString("kspacemanipulator.margin.top.unit"));


	/**
	 * Bedienelemente für die am unteren Rand zu loeschenden Zeilen.
	 */
	LabelTFLabelPanel				tfDelBottomRows = new LabelTFLabelPanel(java.util.ResourceBundle.getBundle("virtual/tools/resources/tools_loc").getString("kspacemanipulator.margin.bottom.title"), "0", java.util.ResourceBundle.getBundle("virtual/tools/resources/tools_loc").getString("kspacemanipulator.margin.bottom.unit"));


	/**
	 * Bedienelemente für die Breite des zu loeschenden Rechtecks.
	 */
	LabelTFLabelPanel				tfDelRectWidth = new LabelTFLabelPanel(java.util.ResourceBundle.getBundle("virtual/tools/resources/tools_loc").getString("kspacemanipulator.rect.width.title"), "0", java.util.ResourceBundle.getBundle("virtual/tools/resources/tools_loc").getString("kspacemanipulator.rect.width.unit"));


	/**
	 * Bedienelemente für die Hoehe des zu loeschenden Rechtecks.
	 */
	LabelTFLabelPanel				tfDelRectHeight = new LabelTFLabelPanel(java.util.ResourceBundle.getBundle("virtual/tools/resources/tools_loc").getString("kspacemanipulator.rect.height.title"), "0", java.util.ResourceBundle.getBundle("virtual/tools/resources/tools_loc").getString("kspacemanipulator.rect.height.unit"));


	/**
	 * Bedienelemente für den Abstand der zu loeschenden Zeilen.
	 */
	LabelTFLabel_1Panel			tfDelRows = new LabelTFLabel_1Panel(java.util.ResourceBundle.getBundle("virtual/tools/resources/tools_loc").getString("kspacemanipulator.row.title"), "0", java.util.ResourceBundle.getBundle("virtual/tools/resources/tools_loc").getString("kspacemanipulator.row.unit"));


	/**
	 * Bedienelemente für den Abstand der zu loeschenden Spalten.
	 */
	LabelTFLabel_1Panel			tfDelColumns = new LabelTFLabel_1Panel(java.util.ResourceBundle.getBundle("virtual/tools/resources/tools_loc").getString("kspacemanipulator.col.title"), "0", java.util.ResourceBundle.getBundle("virtual/tools/resources/tools_loc").getString("kspacemanipulator.col.unit"));


	/**
	 * Rueckreferenz zum Hauptfenster
	 */
	ImagePanel							theImagePanel;

	// Geaendert: tha 2000.8.17
	// ViewerFrame             myViewerFrame;


	/**
	 * Der Konstruktor stellt das k-Raum-Manipulatorfenster mit seinen Bedienelementen
	 * dar und berechnet die Fourier-Transformation des uebergebenen Bildes. Diese und
	 * das Originalbild werden auf der Zeichenflaeche dargestellt.
	 * @param i Das ImagePlus-Objekt des Bildes, dessen k-Raum manipuliert werden soll.
	 */
	public KSpaceManipulator(ImagePlus i, ImagePanel ip) {

		// Geaender: ttha 2000.8.17
		// public KSpaceManipulator(ImagePlus i, ViewerFrame myViewer) {
		theImagePanel = ip;

		pCanvas = new KSpaceManipulatorCanvas(theImagePanel);

		// Merken des selektierten Bildes
		sourceImg = i;

		// Aufbauen der GUI
		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		} 

		// Neu: tha 2000.8.17
		// Frame zentrieren.
		Dimension scrRes = this.getToolkit().getScreenSize();
		Dimension fs = this.getSize();

		this.setLocation((scrRes.width - fs.width) / 2, (scrRes.height - fs.height) / 2);

		// K-Raum des OriginalBildes berechnen und merken
		origKSpaceReal = FFTTools.shift(sourceImg.getFFTImageReal(), 128, 128);
		origKSpaceImag = FFTTools.shift(sourceImg.getFFTImageImag(), 128, 128);

		// Neues Array fuer manipulierten K-Raum erzeugen
		manipKSpaceReal = new float[origKSpaceReal.length][origKSpaceReal[0].length];
		manipKSpaceImag = new float[origKSpaceImag.length][origKSpaceImag[0].length];

		// K-Raum-Bild erzeugen und der Canvas-Klasse mitteilen und neuzeichnen
		intKSpace = FFTTools.getShiftedFFTImageSource(sourceImg);
		Image kSpace = createImage(new java.awt.image.MemoryImageSource(256, 256, intKSpace, 0, 256));

		manipIntKSpace = new int[intKSpace.length];

		pCanvas.setSourceImg(sourceImg);
		pCanvas.setSourceKSpace(kSpace);
		pCanvas.setTargetKSpace(origKSpaceReal, origKSpaceImag, intKSpace);
		pCanvas.repaint();
	}		// Konstruktor


	/**
	 * Die Methode baut das k-Raum-Manipulationsfenster und seine Elemente auf und stellt
	 * sie dar. Zusaetzlich werden ActionListener fuer die Knoepfe initialisiert.
	 */
	private void jbInit() throws Exception {

		// Fenstereigenschaften festlegen
		this.getContentPane().setLayout(null);

		// tha 2006.1.2
		// this.setSize(800, 538);
                if (System.getSecurityManager() == null) {
                    this.setSize(800, 547);
                } else {
                    // Wenn das Programm innerhalb eines Security Managers läuft,
                    // wird die Warnung "" von 18 Pt am unteren Bildrand eingeblendet.
                    // Sie überdeckt den Bildinhalt.
                    this.setSize(800, 565);
                }

		this.setTitle(java.util.ResourceBundle.getBundle("virtual/tools/resources/tools_loc").getString("kspacemanipulator.title"));

		// Zeichnflaeche einrichten
		pCanvas.setBounds(new Rectangle(0, 0, 512, 512));
		this.getContentPane().add(pCanvas, null);

		pCanvas.addMouseListener(new java.awt.event.MouseAdapter() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void mousePressed(MouseEvent e) {
				pCanvas_mousePressed(e);
			} 


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void mouseReleased(MouseEvent e) {
				pCanvas_mouseReleased(e);
			} 

		});

		pCanvas.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void mouseDragged(MouseEvent e) {
				pCanvas_mouseDragged(e);
			} 

		});


		// Kontrollbereich-Rahmen einrichten
		pToolbar.setLayout(null);
		pToolbar.setBounds(new Rectangle(515, 0, 280, 512));
		this.getContentPane().add(pToolbar, null);

		// Rahmen fuer die einzelnen Manipulatoren einrichten
		pDeleteMargins.setLayout(null);
		pDeleteMargins.setBorder(BorderFactory.createEtchedBorder());
		pDeleteMargins.setBounds(new Rectangle(2, 2, 275, 115));
		pToolbar.add(pDeleteMargins, null);

		// Kontrollkaestchen fuer "Raender loeschen" einrichten
		cbDeleteMargins.setText(java.util.ResourceBundle.getBundle("virtual/tools/resources/tools_loc").getString("kspacemanipulator.check.margin.title"));
		cbDeleteMargins.setBounds(new Rectangle(4, 2, 200, 18));
		pDeleteMargins.add(cbDeleteMargins, null);
		cbDeleteMargins.addActionListener(new java.awt.event.ActionListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void actionPerformed(ActionEvent e) {
				updateKSpaceImage();
			} 

		});

		// Bedienelemente fuer das Loeschen der Raender einrichten...
		// ...linke Spalten
		tfDelLeftColumns.setBounds(4, 21, 265, 20);
		tfDelLeftColumns.setRange(0, 255);
		pDeleteMargins.add(tfDelLeftColumns, null);
		tfDelLeftColumns.getTextFieldReference().addCaretListener(new CaretListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void caretUpdate(CaretEvent e) {
				updateKSpaceImage();
			} 

		});

		// ...rechte Spalten
		tfDelRightColumns.setBounds(4, 44, 265, 20);
		tfDelRightColumns.setRange(0, 255);
		pDeleteMargins.add(tfDelRightColumns, null);
		tfDelRightColumns.getTextFieldReference().addCaretListener(new CaretListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void caretUpdate(CaretEvent e) {
				updateKSpaceImage();
			} 

		});

		// ...obere Zeilen
		tfDelTopRows.setBounds(4, 67, 265, 20);
		tfDelTopRows.setRange(0, 255);
		pDeleteMargins.add(tfDelTopRows, null);
		tfDelTopRows.getTextFieldReference().addCaretListener(new CaretListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void caretUpdate(CaretEvent e) {
				updateKSpaceImage();
			} 

		});

		// ...untere Zeilen
		tfDelBottomRows.setBounds(4, 90, 265, 20);
		tfDelBottomRows.setRange(0, 255);
		pDeleteMargins.add(tfDelBottomRows, null);
		tfDelBottomRows.getTextFieldReference().addCaretListener(new CaretListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void caretUpdate(CaretEvent e) {
				updateKSpaceImage();
			} 

		});

		// Rahmen fuer die bedienelemente zum Loeschen eines Rechtecks einrichten
		pDeleteRect.setLayout(null);
		pDeleteRect.setBorder(BorderFactory.createEtchedBorder());
		pDeleteRect.setBounds(new Rectangle(2, 119, 275, 69));
		pToolbar.add(pDeleteRect, null);

		// Kontrollkaestchen fuer das Loeschen eines inneren Rechtecks einrichten
		cbDeleteRect.setText(java.util.ResourceBundle.getBundle("virtual/tools/resources/tools_loc").getString("kspacemanipulator.check.rect.title"));
		cbDeleteRect.setBounds(new Rectangle(4, 2, 200, 18));
		pDeleteRect.add(cbDeleteRect, null);
		cbDeleteRect.addActionListener(new java.awt.event.ActionListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void actionPerformed(ActionEvent e) {
				updateKSpaceImage();
			} 

		});

		// Bedienelemente fuer das loeschen eines Rechtecks einrichten...
		// ...Breite
		tfDelRectWidth.setBounds(4, 21, 265, 20);
		tfDelRectWidth.setRange(0, 255);
		pDeleteRect.add(tfDelRectWidth, null);
		tfDelRectWidth.getTextFieldReference().addCaretListener(new CaretListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void caretUpdate(CaretEvent e) {
				updateKSpaceImage();
			} 

		});

		// ...Hoehe
		tfDelRectHeight.setBounds(4, 44, 265, 20);
		tfDelRectHeight.setRange(0, 255);
		pDeleteRect.add(tfDelRectHeight, null);
		tfDelRectHeight.getTextFieldReference().addCaretListener(new CaretListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void caretUpdate(CaretEvent e) {
				updateKSpaceImage();
			} 

		});

		// Rahmen fuer die Bedienelemenet zum Loeschen von Zeilene/Spalten in einem
		// bestimmten Abstand einrichten
		pDeleteLines.setLayout(null);
		pDeleteLines.setBorder(BorderFactory.createEtchedBorder());
		pDeleteLines.setBounds(new Rectangle(2, 190, 275, 69));
		pToolbar.add(pDeleteLines, null);

		// Kontrollkaestchen "Zeilen/Spalten loeschen"
		cbDeleteLines.setText(java.util.ResourceBundle.getBundle("virtual/tools/resources/tools_loc").getString("kspacemanipulator.check.rowcol.title"));
		cbDeleteLines.setBounds(new Rectangle(4, 2, 200, 18));
		pDeleteLines.add(cbDeleteLines, null);
		cbDeleteLines.addActionListener(new java.awt.event.ActionListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void actionPerformed(ActionEvent e) {
				updateKSpaceImage();
			} 

		});

		// Bedienelemente zum loeschen von Zeilen
		tfDelRows.setBounds(4, 21, 265, 20);
		tfDelRows.setRange(2, 256);
		pDeleteLines.add(tfDelRows, null);
		tfDelRows.getTextFieldReference().addCaretListener(new CaretListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void caretUpdate(CaretEvent e) {
				updateKSpaceImage();
			} 

		});

		// Bedienelemente zum loeschen von Spalten
		tfDelColumns.setBounds(4, 44, 265, 20);
		tfDelColumns.setRange(2, 256);
		pDeleteLines.add(tfDelColumns, null);
		tfDelColumns.getTextFieldReference().addCaretListener(new CaretListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void caretUpdate(CaretEvent e) {
				updateKSpaceImage();
			} 

		});

		// Knopf zum Starten der Ruecktransformation einrichten
		pbCalcImage.setText(java.util.ResourceBundle.getBundle("virtual/tools/resources/tools_loc").getString("kspacemanipulator.btn.reverse.title"));
		pbCalcImage.setBounds(new Rectangle(52, 280, 175, 26));
		pToolbar.add(pbCalcImage, null);
		pbCalcImage.addActionListener(new java.awt.event.ActionListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void actionPerformed(ActionEvent e) {
				pbCalcImage_actionPerformed(e);
			} 

		});

		// Knopf zum Uebertragen des zuruecktransf. Bildes in den DV einrichten
		pbtoDV.setText(java.util.ResourceBundle.getBundle("virtual/tools/resources/tools_loc").getString("kspacemanipulator.btn.takeover.title"));
		pbtoDV.setBounds(new Rectangle(52, 485, 175, 26));
		pbtoDV.setEnabled(false);
		pToolbar.add(pbtoDV, null);
		pbtoDV.addActionListener(new java.awt.event.ActionListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void actionPerformed(ActionEvent e) {
				pbtoDV_actionPerformed(e);
			} 

		});

	}		// Methode jbInit
 

	/**
	 * Die Methode loescht die Zeilen und Spalten in einem vom Benutzer
	 * vorgegebenen Abstand. Die Methode wird bei
	 * jedem Betaetigen eines Bedienelements des k-Raum-Manipulators aufgerufen,
	 * so dass die Aenderungen sofort sichtbar werden.
	 */
	private void deleteLines() {
		int rows = 0;
		int columns = 0;

		if (cbDeleteLines.isSelected()) {

			// Auslesen der Einstellungen aus den Bedienelemeneten
			try {
				rows = tfDelRows.getValue();
				columns = tfDelColumns.getValue();
			} catch (Exception err) {}

			// Loeschen der Zeilene oben und unten
			if (rows > 0) {
				for (int x = 0; x < 256; x++) {
					for (int y = 0; y < 256; y++) {
						if ((y % rows) == 0) {
							manipIntKSpace[(256 * y) + x] = 0;
							manipKSpaceReal[x][y] = 0;
							manipKSpaceImag[x][y] = 0;
						}		// if
 					 }		// for y
 				 }			// for x
 			 }				// if rows>0
 
			// Loeschen der Spalten rechts und links
			if (columns > 0) {
				for (int x = 0; x < 256; x++) {
					for (int y = 0; y < 256; y++) {
						if ((x % columns) == 0) {
							manipIntKSpace[(256 * y) + x] = 0;
							manipKSpaceReal[x][y] = 0;
							manipKSpaceImag[x][y] = 0;
						}		// if
 					 }		// for y
 				 }			// for x
 			 }				// if columns>0
 
		}						// if Maipulator eingeschaltet
 
	}							// Methode deleteLines
 

	/**
	 * Die Methode loescht ein zentral im k-Raum gelegenes Rechteck einer vom
	 * Benutzer vorgegebenen Hoehe und Breite. Die Methode wird bei
	 * jedem Betaetigen eines Bedienelements des k-Raum-Manipulators aufgerufen,
	 * so dass die Aenderungen sofort sichtbar werden.
	 */
	private void deleteRect() {
		int width = 0;
		int height = 0;

		// Auslesen der Bedienelemente
		if (cbDeleteRect.isSelected()) {
			try {
				width = tfDelRectWidth.getValue();
				height = tfDelRectHeight.getValue();
			} catch (Exception err) {}

			// Loeschen des zentralen rechtecks
			if ((width > 0) && (height > 0)) {
				for (int x = (128 - (width / 2)); x < (128 + (width / 2)); x++) {
					for (int y = (128 - (height / 2)); y < (128 + (height / 2)); y++) {
						manipIntKSpace[(256 * y) + x] = 0;
						manipKSpaceReal[x][y] = 0;
						manipKSpaceImag[x][y] = 0;
					}		// for y
 				 }		// for x
 			 }			// if
 		 }				// if
 	 }					// Methode DeleteRect
 

	/**
	 * Die Methode loescht die Zeilen und Spalten am Rand des k-Raums, so wie der
	 * Benutzer dies mit den Bedienelementen eingestellt hat. Die Methode wird bei
	 * jedem Betaetigen eines Bedienelements des k-Raum-Manipulators aufgerufen,
	 * so dass die Aenderungen sofort sichtbar werden.
	 */
	private void deleteMargins() {
		int left = 0;
		int right = 0;
		int top = 0;
		int bottom = 0;

		// Auslesen der Bedienelemente
		if (cbDeleteMargins.isSelected()) {
			try {
				left = tfDelLeftColumns.getValue();
				right = tfDelRightColumns.getValue();
				top = tfDelTopRows.getValue();
				bottom = tfDelBottomRows.getValue();
			} catch (Exception err) {}

			// Löschen des liniken Randes
			if (left > 0) {
				for (int x = 0; x <= left; x++) {
					for (int y = 0; y < 256; y++) {
						manipIntKSpace[(y * 256) + x] = 0;
						manipKSpaceReal[x][y] = 0;
						manipKSpaceImag[x][y] = 0;
					} 
				} 
			} 

			// Loeschen des rechten randes
			if (right > 0) {
				for (int x = 256 - right; x < 256; x++) {
					for (int y = 0; y < 256; y++) {
						manipIntKSpace[(y * 256) + x] = 0;
						manipKSpaceReal[x][y] = 0;
						manipKSpaceImag[x][y] = 0;
					} 
				} 
			} 

			// Loeschen des oberen Randes
			if (top > 0) {
				for (int x = 0; x < 256; x++) {
					for (int y = 0; y <= top; y++) {
						manipIntKSpace[(y * 256) + x] = 0;
						manipKSpaceReal[x][y] = 0;
						manipKSpaceImag[x][y] = 0;
					} 
				} 
			} 

			// Loeschen des unteren Randes
			if (bottom > 0) {
				for (int x = 0; x < 256; x++) {
					for (int y = 256 - bottom; y < 256; y++) {
						manipIntKSpace[(y * 256) + x] = 0;
						manipKSpaceReal[x][y] = 0;
						manipKSpaceImag[x][y] = 0;
					} 
				} 
			} 

		}		// if Manipulator eingeschaltet
 	 }		// Methode deleteMargins
 

	/**
	 * Die Methode wird bei jedem Betaetigen eines der Bedienelemente zur
	 * k-Raum-Maipulation aufgerufen. Sie berechnet dann sowohl den darzustellenden
	 * manipulierten k-Raum neu, als auch den Real- und Imaginaerteil der
	 * Fourier-Transformation, die beide fuer die Ruecktransformation benoetigt werden.
	 */
	public void updateKSpaceImage() {

		// Zuerst einmal wieder denn vollstaendigen k-Raum kopieren
		for (int x = 0; x < 256; x++) {
			for (int y = 0; y < 256; y++) {
				manipIntKSpace[(256 * y) + x] = intKSpace[(256 * y) + x];
				manipKSpaceReal[x][y] = origKSpaceReal[x][y];
				manipKSpaceImag[x][y] = origKSpaceImag[x][y];
			} 
		} 

		// Ausfuehren der Manipulationen
		deleteMargins();
		deleteLines();
		deleteRect();

		// Manipuliertes k-Raum-Bild an Canvas uebergeben und neu darstellen
		pCanvas.setTargetKSpace(manipKSpaceReal, manipKSpaceImag, manipIntKSpace);
		pCanvas.repaint();
	} 


	/**
	 * Die Methode wird immer dann aufgerufen, wenn der Benutzer den Knopf zur
	 * Berechnung der Ruecktransformation des manipulierten k-Raums betaetigt hat.
	 * Es werden dann Matrizen des manipulierten Real- und Imaginaerteils der
	 * Fourier-Transformation erzeugt und mit diesen eine Ruecktransformation
	 * vorgenommen. Das ruecktransformaierte Bild wird dann auf der Zeichenflaeche
	 * dargestellt.
	 * @param e Das Ereigenis, das beim Betaetigen des Knopfes zur Berechnung der
	 * der Ruecktransformation ausgeloest wird.
	 */
	private void pbCalcImage_actionPerformed(ActionEvent e) {
		updateKSpaceImage();
		pCanvas.recalcTargetImage();
		pCanvas.repaint();

		pbtoDV.setEnabled(true);
	} 


	/**
	 * Die Methode wird aufgerufen, wenn der Knopf zum uebertragen des zuruecktransformierten
	 * Bildes in den DICOM-Viewer gedrueckt wird.
	 */
	private void pbtoDV_actionPerformed(ActionEvent e) {
		ImagePlus ip = pCanvas.getDestinationImage();

		theImagePanel.createNewImage(ip);
		theImagePanel.repaint();

		pbtoDV.setEnabled(false);
	} 


	/**
	 * 
	 */
	private void pCanvas_mousePressed(MouseEvent e) {

		// Setzen der Mausklickkoordinaten in Klassenvariablen
		lastMouseClickX = e.getX();
		lastMouseClickY = e.getY();

		// Setzen der Center- und Window-Einstellungen (benoetigt fuer die Fensterung)

		lastCenter = pCanvas.getDestinationImage().getCenter();
		lastWindow = pCanvas.getDestinationImage().getWindow();
		lastTime = e.getWhen();
	} 


	/**
	 * 
	 */
	private void pCanvas_mouseReleased(MouseEvent e) {

		// Standardcursor
		pCanvas.setCursor(DEFAULT_CURSOR);
	} 


	/**
	 * 
	 */
	private void pCanvas_mouseDragged(MouseEvent e) {
		int		curX, curY, difX, difY, difT;
		long	curWhen;

		// Cursor setzen
		pCanvas.setCursor(WINDOW_CURSOR);

		// Begrenzen der Durchgefuehrten Aktionen auf 10 pro Sekunde
		curWhen = e.getWhen();
		difT = (int) (curWhen - lastTime);
		if (difT < 100) {
			return;

		} 
		curX = e.getX();
		curY = e.getY();
		difX = curX - lastMouseClickX;
		difY = lastMouseClickY - curY;

		// Bei schneller Mausbewegung auch eine schnellere (grobere) Fensterung
		// durchfuehren
		if (Math.abs(difX) > 10) {
			difX *= 10;
		} 
		if (Math.abs(difY) > 10) {
			difY *= 10;
		} 

		if ((lastCenter + difY) <= 4095) {
			if ((lastCenter + difY) >= 0) {
				pCanvas.getDestinationImage().setCenter(lastCenter + difY);
				lastCenter += difY;
			} 
		} 

		if ((lastWindow + difX) <= 4096) {
			if ((lastWindow + difX) >= 1) {
				pCanvas.getDestinationImage().setWindow(lastWindow + difX);
				lastWindow += difX;
			} 
		} 

		lastMouseClickX = curX;
		lastMouseClickY = curY;
		lastTime = curWhen;

		// Aktualisieren der Fensterung des selektierten Bildes
		pCanvas.getDestinationImage().updateAWTImage();

		pCanvas.setTragetImage(pCanvas.getDestinationImage().getAWTImage());

		// Neuzeichnen des Canvasinhalts
		pCanvas.repaint();
	}		// Methode
 

}






/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

