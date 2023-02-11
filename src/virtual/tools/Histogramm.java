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


/**
 * Die Klasse stellt ein Fenster zur Anzeige eines Histogramms dar. Sie
 * verwaltet das Bild, von dem das Histogramm dargestellt werden soll und
 * die benoetigten Methoden zum Berechnen des Histogramms.
 * 
 * @author   Christian Schalla
 * @author   Andreas Truemper
 * @version  1.0, 15. Oktober 1999
 */
public class Histogramm extends JFrame {


	/**
	 * Das Bild, von dem das Histogramm dargestellt werden soll.
	 */
	private short[][]			image;


	/**
	 * Die Histogrammtabelle. Sie enthaelt nach der Berechnung fuer jeden
	 * Grauwert die Anzahl der Pixel mit diesem Grauwert.
	 */
	private double[]			histogram;


	/**
	 * Die maximale Anzahl an Pixeln mit einem Grauwert
	 */
	private double				maxValue;


	/**
	 * Breite des Histogrammfensters.
	 */
	final private int			FRAME_WIDTH = 295;


	/**
	 * Hoehe des Histogrammfensters.
	 */
	final private int			FRAME_HEIGHT = 150;


	/**
	 * Wenn ein Grauwert mit mehr als durch diesen Anteil angebenen Pixeln am
	 * Gesamthistogramm beteiligt ist, wird er abgeschnitten (und im Histogramm
	 * rot markiert.
	 */
	final private double	PERCENTAGE = 0.05;


	/**
	 * Referenz auf das aufrufende Fenster.
	 */
	private Frame					parent = null;


	/**
	 * Der Konstruktor baut das Histogrammfenster auf, stellt aber noch nicht
	 * das Histogramm dar. Wenn dieser Konstruktor verwendet wird, muﬂ das
	 * Histogramm durch die Methode computeHistogramm dargestellt werden.
	 */
	public Histogramm() {
		try {
			jbInit();
		} catch (Exception e) {}
	}		// Konstruktor


	/**
	 * Der Konstruktor baut das Histogrammfenster auf und berechnet das Histogramm
	 * des uebergebenen Bildes und stellt dies dar.
	 * @param img Das Bild, dessen Histogramm berechnet werden soll.
	 * @param title Der Titel des Histogrammfensters.
	 */
	public Histogramm(Frame p, short img[][], String title) {
		super(title);
		parent = p;
		image = img;
		computeHistogramm();
		try {
			jbInit();
		} catch (Exception e) {}
	}		// Konstruktor


	/**
	 * Die Methode stellt ein leeres Histogrammfenster dar. Sie legt nur die
	 * Groesse und Position des Fensters fest.
	 */
	private void jbInit() throws Exception {
		this.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		if (parent != null) {
			this.setLocation(parent.getLocationOnScreen().x + (parent.getSize().width / 2) - (this.getSize().width / 2), parent.getLocationOnScreen().y + (parent.getSize().height / 2) - (this.getSize().width / 2));
		} else {
			this.setLocation(200, 200);
		} 
		this.setResizable(false);
	}		// Methode jbInit
 

	/**
	 * Die Methode setzt das Bild, dessen Histogramm berechnet werden soll.
	 * Sie berechnet das Histogramm allerdings noch nicht. Dazu muss die Methode
	 * computeHistogramm() aufgerufen werden.
	 * @param i Das Bild, dessen Histogramm dargestellt werden soll.
	 */
	public void setImage(short[][] i) {
		image = i;
	}		// Methode setImage
 

	/**
	 * Die Methode berechnet das Histogramm des in der Instanzvariablen image gespeicherten
	 * Bildes. Dabei werden zur Zeit nur Bilder mit 256 Grauwerten unterstuetzt.
	 */
	public void computeHistogramm() {
		int total = 0;

		// Initialisieren der Histogrammtabelle
		histogram = new double[256];
		for (int j = 0; j < histogram.length; j++) {
			histogram[j] = 0;
		}		// for j
 
		// Berechnung des Histogramms
		for (int x = 0; x < image.length; x++) {
			for (int y = 0; y < image[0].length; y++) {
				histogram[image[x][y]]++;
				total++;
			}		// for y
 		 }		// for x
 
		// Normalisierung des Histogramms
		maxValue = 0;
		int sum = 0;

		for (int i = 0; i < histogram.length; i++) {
			sum += histogram[i];
			histogram[i] = histogram[i] / total;

			if (histogram[i] < PERCENTAGE) {
				if (histogram[i] > maxValue) {
					maxValue = histogram[i];
				} 
			} 
		}		// for i
 
	}			// computeHistogramm
 

	/**
	 * Die Methode zeichnet das Histogramm einschliesslich des Koordinatensystems
	 * und der Beschriftung.
	 * @param g Der Grafikkontext.
	 */
	public void paint(Graphics g) {

		// Setzen der Farbe fuer die Histogrammbalken
		g.setColor(Color.black);

		// Zeichnen der Koordinatenachsen
		g.drawLine(24, 130, 24, 30);		// y-Achse
		g.drawLine(24, 130, 280, 130);	// x-Achse

		// Beschriften der x-Achse
		Font	myFont = new Font("Arial", Font.PLAIN, 10);

		g.setFont(myFont);
		g.drawString("1", 20, 145);
		g.drawString("128", 145, 145);
		g.drawString("255", 274, 145);

		// Beschriften der y-Achse
		// g.drawString(""+maxValue,5,35);

		// Berechnen des y-Skalierungsfaktors
		double	yscale = ((double) (FRAME_HEIGHT) - 40.0) / maxValue;

		// Zeichnen der Histogrammbalken
		for (int i = 0; i < histogram.length; i++) {
			if (histogram[i] > PERCENTAGE) {
				g.setColor(Color.red);
			} else {
				g.setColor(Color.black);
			} 
			g.drawLine(i + 25, 130, i + 25, 130 - (int) ((double) (histogram[i]) * yscale));
		}		// for i
 	 }		// Methode paint
 
}






/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

