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

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;

// import virtual.mrt.*;
import virtual.tools.*;


/**
 * Diese Klasse repraesentiert das Fenster zur Darstellung des k-Raums eines
 * Bildes. Anstatt des k-Raums (Magnitudenbild der Fourier-Transformation) des
 * selektierten Bildes kann auch das Phasenbild oder der Real- oder
 * Imaginaerteil der Fourier-Transformation dargestellt werden.
 * <br>
 * <DL><DT><B>Modifications: </B><DD>
 * Thomas Hacklaender 2002.10.12:
 * setLabel seit Java 1.1 deprecated.<br>
 * Thomas Hacklaender 2002.10.13:
 * Strings internationalisiert.<br>
 * @author   Christian Schalla
 * @author   Andreas Truemper
 * @version  1.2, 2002.10.16
 */
public class KSpaceFrame extends JFrame {


	/**
	 * Konstante fuer die Breite des k-Raum-Fensters.
	 */
	final private int FRAME_WIDTH = 272;


	/**
	 * Konstante fuer die Hoehe des k-Raum-Fensters.
	 */
	final private int FRAME_HEIGHT = 330;


	/**
	 * Das Bild, dessen k-Raum dargestellt werden soll.
	 */
	private ImagePlus ip;


	/**
	 * Das Magnitudenbild (k-Raum) des selektierten Bildes.
	 */
	private Image			magnitudeImage = null;


	/**
	 * Das Phasenbild des selektierten Bildes.
	 */
	private Image			phaseImage = null;


	/**
	 * Der Realteil der Fourier-Transformation des selektierten Bildes.
	 */
	private Image			realImage = null;


	/**
	 * Der Imaginaerteil der Fourier-Transformation des selektierten Bildes.
	 */
	private Image			imagImage = null;


	/**
	 * Der Knopf zur Darstellung des Magnitudenbildes der Fourier-Transformation
	 * (k-Raum).
	 */
	JToggleButton			tbMagnitudeImage = new JToggleButton();


	/**
	 * Der Knopf zur Darstellung des Phasenbildes.
	 */
	JToggleButton			tbPhaseImage = new JToggleButton();


	/**
	 * Der Knopf zur Darstellung des Realteils der Fourier-Transformation.
	 */
	JToggleButton			tbFFTReal = new JToggleButton();


	/**
	 * Der Knopf zur Darstellung des Imaginaerteils der Fourier-Transformation.
	 */
	JToggleButton			tbFFTImag = new JToggleButton();


	/**
	 * Gruppe fuer die Knoepfe, die die Auswahl des darzustellenden Bildes
	 * ermoeglichen.
	 */
	ButtonGroup				myButtonGroup = new ButtonGroup();


	/**
	 * Referenz auf die Zeichenflaeche, auf der das ausgewaehlte Bild dargestellt
	 * werden soll.
	 */
	KSpaceCanvas			myCanvas = new KSpaceCanvas();


	/**
	 * Der Konstruktor stellt das k-Raum-Anzeigefenster dar. Gleichzeitig merkt er
	 * sich das Bild, fuer den der k-Raum angezeigt werden soll und berechnet
	 * zunaecht das Magnitudenbild der Fourier-Transformation und stellt dieses
	 * dar.
	 * @param imgp Das Bild, dessen k-Raum angezeigt werden soll.
	 */
	public KSpaceFrame(ImagePlus imgp) {
		super(java.util.ResourceBundle.getBundle("virtual/tools/resources/tools_loc").getString("kspaceframe.title"));
		ip = imgp;

		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		};
                                
		// tha 2006.1.3
		// Frame zentrieren.
		Dimension scrRes = this.getToolkit().getScreenSize();
		Dimension fs = this.getSize();

		this.setLocation((scrRes.width - fs.width) / 2, (scrRes.height - fs.height) / 2);


		// Magnitudenbild berechnen und darstellen
		calculateAndDisplayMagnitudeImage();
	}		// Konstruktor KSpaceFrame


	/**
	 * Die Methode konvertiert ein zweidimensionales Feld von Ganzzahlen in
	 * ein Grauwertbild (Java-Image).
	 * @param source Das in ein Image zu konvertierende zweidim. Feld von Ganzzahlen.
	 */
	public Image convertIntArrayToImage(int[][] source) {

		// Umwandeln des 2-dim. Feldes in ein 1-dim. das als ImageSource verwendet
		// werden kann
		int[] intArray = FFTTools.convertIntArrayToImage(source);

		// Erzeugen eines neuen Image-Objektes
		Image dest = createImage(new MemoryImageSource(256, 256, intArray, 0, 256));

		return dest;
	}		// Methode convertIntArrayToImage
 

	/**
	 * Die Methode berechnet das Magnitudenbild der Fourier-Transformation des
	 * selektierten Bildes. Dabei ist zu beachten, das das angezeigte Bild um
	 * die halbe Bildbreite bzw. Hoehe nach rechts bzw. unten verschoben ist und
	 * die Grauwerte logarithmisch skaliert wurden. Nur so ist eine kontrastreiche
	 * Anzeige moeglich.
	 */
	public void calculateAndDisplayMagnitudeImage() {
		if (magnitudeImage == null) {

			// Auslesen des Real- und Imaginaerteils der Fouriertransformation des
			// selektierten Bildes. zusaetzlich verschieben
			float[][] shiftedImgRe = FFTTools.shift(ip.getFFTImageReal(), 128, 128);
			float[][] shiftedImgIm = FFTTools.shift(ip.getFFTImageImag(), 128, 128);

			// Magnitudenbild berechnen
			float[][] magImage = FFTTools.magnitude(shiftedImgRe, shiftedImgIm);

			// Magnitudenbild logarithmisch skalieren
			float[][] magscaleImage = FFTTools.scaleMagnitude(magImage);

			// Magnitudenbild Fenstern
			int[][]		windowedImg = FFTTools.autoWindow(magscaleImage);

			// gefenstertes Magnitudenbild in ein Image-Objekt umwandeln
			magnitudeImage = convertIntArrayToImage(windowedImg);
		}		// if
 
		// Der Canvas-Klasse das neue Bild uebergeben und neu zeichnen
		myCanvas.setImage(magnitudeImage);
		myCanvas.repaint();
	}		// calculateAndDisplayMagnitudeImage
 

	/**
	 * Die Methode berechnet das Phasenbild der Fourier-Transformation des
	 * selektierten Bildes. Dabei ist zu beachten, das das angezeigte Bild um
	 * die halbe Bildbreite bzw. Hoehe nach rechts bzw. unten verschoben ist und
	 * die Grauwerte logarithmisch skaliert wurden. Nur so ist eine kontrastreiche
	 * Anzeige moeglich.
	 */
	public void calculateAndDisplayPhaseImage() {
		if (phaseImage == null) {

			// Auslesen des Real- und Imaginaerteils der Fouriertransformation des
			// selektierten Bildes. zusaetzlich verschieben
			float[][] shiftedImgRe = FFTTools.shift(ip.getFFTImageReal(), 128, 128);
			float[][] shiftedImgIm = FFTTools.shift(ip.getFFTImageImag(), 128, 128);

			// Phasenbild berechnen
			float[][] phImage = FFTTools.calcPhase(shiftedImgRe, shiftedImgIm);

			// Phasenbild logarithmisch skalieren
			float[][] phscaleImage = phImage;		// FFTTools.scaleMagnitude(phImage);

			// Phasenbild Fenstern
			int[][]		windowedImg = FFTTools.autoWindow(phscaleImage);

			// gefenstertes Phasenbild in ein Image-Objekt umwandeln
			phaseImage = convertIntArrayToImage(windowedImg);
		}																			// if
 
		// Der Canvas-Klasse das neue Bild uebergeben und neu zeichnen
		myCanvas.setImage(phaseImage);
		myCanvas.repaint();
	}		// calculateAndDisplayPhaseImage
 

	/**
	 * Die Methode berechnet den Realteil der Fourier-Transformation des
	 * selektierten Bildes. Dabei ist zu beachten, das das angezeigte Bild um
	 * die halbe Bildbreite bzw. Hoehe nach rechts bzw. unten verschoben ist und
	 * die Grauwerte logarithmisch skaliert wurden. Nur so ist eine kontrastreiche
	 * Anzeige moeglich.
	 */
	public void calculateAndDisplayRealImage() {
		if (realImage == null) {

			// Auslesen des Realteils der Fouriertransformation des
			// selektierten Bildes. Zusaetzlich verschieben
			float[][] shiftedImgRe = FFTTools.shift(ip.getFFTImageReal(), 128, 128);

			// Logarithmisch skalieren
			float[][] magscaleImage = FFTTools.scaleMagnitude(shiftedImgRe);

			// Fenstern
			int[][]		windowedImg = FFTTools.autoWindow(magscaleImage);

			// Umwandeln in ein Java-Image
			realImage = convertIntArrayToImage(windowedImg);
		}		// if
 
		/*
		 * Experiment
		 * float[][][] fftOfReal = FFTTools.getFFT(ip.getFFTImageReal());
		 * int[][] realfftOfReal = FFTTools.autoWindow(fftOfReal[1]);
		 * Image myimg = convertIntArrayToImage(realfftOfReal);
		 * myCanvas.setImage(myimg);
		 */

		// Der Canvas-Klasse das neue Bild uebergeben und neu zeichnen
		myCanvas.setImage(realImage);
		myCanvas.repaint();
	}		// Methode calculateAndDisplayRealImage()
 

	/**
	 * Die Methode berechnet den Imaginaerteil der Fourier-Transformation des
	 * selektierten Bildes. Dabei ist zu beachten, das das angezeigte Bild um
	 * die halbe Bildbreite bzw. Hoehe nach rechts bzw. unten verschoben ist und
	 * die Grauwerte logarithmisch skaliert wurden. Nur so ist eine kontrastreiche
	 * Anzeige moeglich.
	 */
	public void calculateAndDisplayImagImage() {
		if (imagImage == null) {

			// Auslesen des Imaginaerteils der Fouriertransformation des
			// selektierten Bildes. Zusaetzlich verschieben
			float[][] shiftedImgIm = FFTTools.shift(ip.getFFTImageImag(), 128, 128);

			// Logarithmisch skalieren
			float[][] magscaleImage = FFTTools.scaleMagnitude(shiftedImgIm);

			// Fenstern
			int[][]		windowedImg = FFTTools.autoWindow(magscaleImage);

			// Konvertieren in ein java-Image
			imagImage = convertIntArrayToImage(windowedImg);
		}		// if
 
		// Der Canvas-Klasse das neue Bild uebergeben und neu zeichnen
		myCanvas.setImage(imagImage);
		myCanvas.repaint();
	}		// Methode calculateAndDisplayRealImage()
 

	/**
	 * Die Methode baut das k-Raum-Anzeigefenster und seine Elemente auf und stellt
	 * sie dar. Zusaetzlich werden ActionListener fuer die Knoepfe angelegt.
	 */
	private void jbInit() throws Exception {

		// Setzen einiger Fenstereigenschaften
		// this.setSize(FRAME_WIDTH, FRAME_HEIGHT);
                // this.setLocation(150, 150);
            
		// tha 2006.1.3
                if (System.getSecurityManager() == null) {
                    this.setSize(FRAME_WIDTH, FRAME_HEIGHT);
                } else {
                    // Wenn das Programm innerhalb eines Security Managers läuft,
                    // wird die Warnung "" von 18 Pt am unteren Bildrand eingeblendet.
                    // Sie überdeckt den Bildinhalt.
                    this.setSize(FRAME_WIDTH, FRAME_HEIGHT + 18);
                }
                
		this.setResizable(false);

		// Knopf zur Darstellung des Magnitudenbildes einrichten
		// th 2002.10.12
		// tbMagnitudeImage.setText("jToggleButton1");
		// tbMagnitudeImage.setLabel("Mag");
		tbMagnitudeImage.setText(java.util.ResourceBundle.getBundle("virtual/tools/resources/tools_loc").getString("kspaceframe.btn.magnitude.title"));
		tbMagnitudeImage.setSelected(true);
		tbMagnitudeImage.setBounds(new Rectangle(5, 275, 59, 25));
		tbMagnitudeImage.addActionListener(new java.awt.event.ActionListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void actionPerformed(ActionEvent e) {
				calculateAndDisplayMagnitudeImage();

				// tbMagnitudeImage_actionPerformed(e);
			} 

		});

		// Knopf zur Darstellung des Phasenbildes einrichten
		// th 2002.10.12
		// tbPhaseImage.setText("jToggleButton2");
		// tbPhaseImage.setLabel("Pha");
		tbPhaseImage.setText(java.util.ResourceBundle.getBundle("virtual/tools/resources/tools_loc").getString("kspaceframe.btn.phase.title"));
		tbPhaseImage.setBounds(new Rectangle(71, 275, 59, 25));
		tbPhaseImage.addActionListener(new java.awt.event.ActionListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void actionPerformed(ActionEvent e) {
				calculateAndDisplayPhaseImage();

				// tbPhaseImage_actionPerformed(e);
			} 

		});

		// Knopf zur Darstellung des Realteils der FFT einrichten
		// th 2002.10.12
		// tbFFTReal.setText("jToggleButton3");
		// tbFFTReal.setLabel("Real");
		tbFFTReal.setText(java.util.ResourceBundle.getBundle("virtual/tools/resources/tools_loc").getString("kspaceframe.btn.real.title"));
		tbFFTReal.setBounds(new Rectangle(136, 275, 59, 25));
		tbFFTReal.addActionListener(new java.awt.event.ActionListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void actionPerformed(ActionEvent e) {
				calculateAndDisplayRealImage();

				// tbFFTReal_actionPerformed(e);
			} 

		});

		// Knopf zur Darstellung des Imaginaerteils der FFT einrichten
		// th 2002.10.12
		// tbFFTImag.setText("jToggleButton4");
		// tbFFTImag.setLabel("Imag");
		tbFFTImag.setText(java.util.ResourceBundle.getBundle("virtual/tools/resources/tools_loc").getString("kspaceframe.btn.imag.title"));
		tbFFTImag.setMargin(new Insets(2, 2, 2, 2));
		tbFFTImag.setBounds(new Rectangle(203, 275, 59, 25));
		tbFFTImag.addActionListener(new java.awt.event.ActionListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void actionPerformed(ActionEvent e) {
				calculateAndDisplayImagImage();

				// tbFFTImag_actionPerformed(e);
			} 

		});

		// Knoepfe gruppieren
		myButtonGroup.add(tbMagnitudeImage);
		myButtonGroup.add(tbPhaseImage);
		myButtonGroup.add(tbFFTReal);
		myButtonGroup.add(tbFFTImag);

		// Einrichten der Zeichenflaeche
		myCanvas.setBackground(new Color(204, 204, 204));
		myCanvas.setBounds(new Rectangle(5, 5, 256, 256));

		// Hinzufuegen der einzelnen Elemente zum Fenster
		this.getContentPane().setLayout(null);
		this.getContentPane().add(tbMagnitudeImage, null);
		this.getContentPane().add(tbPhaseImage, null);
		this.getContentPane().add(tbFFTReal, null);
		this.getContentPane().add(tbFFTImag, null);
		this.getContentPane().add(myCanvas, null);
	}		// Methode jbInit
 
}






/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

