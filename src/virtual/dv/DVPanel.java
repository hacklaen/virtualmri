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

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;

import virtual.tools.*;


/**
 * Die Klasse implementiert die Zeichenflaeche des DICOMViewers.
 * Insbesondere wird die paint-Methode der JScrollPane-Klasse ueberschrieben, um
 * die Bilder, die Bildbeschriftungen und die Werkzeuge darzustellen.
 * Darueber hinaus verwaltet die Klasse eine Referenz auf den gesamten Bildstapel
 * und ist fuer das Hinzufuegen von Bildern zu diesem Stapel verantwortlich.
 * <DL><DT><B>Modifications: </B><DD>
 * Thomas Hacklaender 2000.8.18:
 * Die Klasse von ImagePanel statt von JPanel abgeleitet.<br>
 * Thomas Hacklaender 2000.4.3:
 * In der Methode paint den Methodenaufruf getPixelSpacing fuer die x
 * und die y-Richtung durch getPixelSpacingColumn und getPixelSpacingRow
 * ersetzt.<br>
 * </DD></DL>
 * @author   Christian Schalla
 * @author   Andreas Truemper
 * @version  1.1, 2000.8.18
 */
public class DVPanel extends ImagePanel {


	/**
	 * Anzahl der Bilder, die gleichzeitig dargestellt werden (1 oder 4).
	 */
	private int					iNumImages = 1;


	/**
	 * Nummer des selektierten Bildes. Ist -1, wenn kein Bild selektiert ist.
	 */
	private int					iActiveImage = -1;


	/**
	 * Verweis auf den Stapel aller Bilder.
	 */
	private ImageStack	is;


	/**
	 * Der Bildausschnitt, der zusaetzlich gezeichnet werden muss, wenn die
	 * Lupen-Funktion eingeschaltet ist.
	 */
	private Image				zoomImage = null;


	/**
	 * x-Position, an der das Zoombild gezeichnet werden soll.
	 */
	private int					zoomX = 0;


	/**
	 * y-Position, an der das Zoombild gezeichnet werden soll.
	 */
	private int					zoomY = 0;


	/**
	 * x-Position des letzten Mausklicks.
	 */
	private int					lastClickX = -1;


	/**
	 * y-Position des letzten Mausklicks.
	 */
	private int					lastClickY = -1;


	/**
	 * x-Position des letzten MausRelease-Ereignisses.
	 */
	private int					lastReleaseX = -1;


	/**
	 * y-Position des letzten MausRelease-Ereignisses.
	 */
	private int					lastReleaseY = -1;


	/**
	 * Aktuelle x-Position des Mauszeigers.
	 */
	private int					currentX = -1;


	/**
	 * Aktuelle y-Position des Mauszeigers.
	 */
	private int					currentY = -1;


	/**
	 * aktueller Grauwert unter dem Mauszeiger.
	 */
	private int					currentGray = -1;


	/**
	 * Rueckreferenz zum Hauptfenster. Diese benoetigt, um Einstellungen
	 * einiger Bedienelemente auszulesen.
	 */
	private ViewerFrame myViewerFrame;


	/**
	 * Standard-Konstruktor.
	 */
	public DVPanel() {}


	/**
	 * Konstruktor mit Rueckreferenz zum Hauptfenster als Parameter.
	 * @param backReference Rueckreferenz zum Haupfenster. Sie wird benoetigt, um
	 * Einstellungen einiger Bedienelemente auszulesen.
	 */
	public DVPanel(ViewerFrame backReference) {
		myViewerFrame = backReference;
		is = new ImageStack();
	}		// Konstruktor MyPanel(ViewerFrame backReference)


	/**
	 * Die Methode aktualisiert die Scrollbalkeneinstellungen in Abhaengigkeit davon,
	 * ob ein oder vier Bilder gleichzeitig dargestellt werden sollen.
	 */
	public void updateScrollbar() {

		// Besorgen der Bildes mit der groessten Nummer
		int num = is.getStackSize();

		if (myViewerFrame.rb1Image.isSelected()) {
			myViewerFrame.sbVerScrollbar.setMinimum(0);
			myViewerFrame.sbVerScrollbar.setMaximum(num * 512 + 512);
			myViewerFrame.sbVerScrollbar.setBlockIncrement(512);
			myViewerFrame.sbVerScrollbar.setUnitIncrement(512);
			myViewerFrame.sbVerScrollbar.setVisibleAmount(511);
		} else {
			int max = 512;

			if (num > 0) {
				max = num / 2 * 256 + 256;
			}		// if
			 myViewerFrame.sbVerScrollbar.setMinimum(0);
			myViewerFrame.sbVerScrollbar.setMaximum(max);
			myViewerFrame.sbVerScrollbar.setBlockIncrement(512);
			myViewerFrame.sbVerScrollbar.setUnitIncrement(256);
			myViewerFrame.sbVerScrollbar.setVisibleAmount(511);
		}			// if-else
 	 }			// Methode updateScrollbar()
 

	/**
	 * Die Methode setzt den internen Parameter, welcher angibt, wie viele Bilder
	 * gleichzeitig dargestellt werden, auf 1. Ausserdem wird der Scrollbereich
	 * aktualisiert.
	 */
	public void setOneImage() {
		iNumImages = 1;
		updateScrollbar();
		myViewerFrame.sbVerScrollbar.setValue((iActiveImage * 512) - 512);
	}		// Methode setOneImage()
 

	/**
	 * Die Methode setzt den internen Parameter, welcher angibt, wie viele Bilder
	 * gleichzeitig dargestellt werden, auf 4. Ausserdem wird der Scrollbereich
	 * aktualisiert
	 */
	public void setFourImages() {
		iNumImages = 4;
		updateScrollbar();
		myViewerFrame.sbVerScrollbar.setValue(((iActiveImage / 2) * 256) - 256);
	}		// Methode setFourImages()
 

	/**
	 * Die Methode legt die Nummer des zur Zeit selektierten Bildes fest.
	 * @param activeImage Die Nummer des selektierten Bildes.
	 */
	public void setActiveImage(int activeImage) {
		iActiveImage = activeImage;
	}		// Methode setActiveImage(int activeImage)
 

	/**
	 * Die Methode legt das Bild fest, das zusaetzlich eingezeichnet werden muss,
	 * wenn die Lupen-Funktion eingeschaltet ist.
	 * @param zoomI Der Bildauschnitt, der zusaetzlich gezeichnet werden muss.
	 */
	public void setZoomImage(Image zoomI) {
		zoomImage = zoomI;
	}		// Methode setZoomImage(Image zoomI)
 

	/**
	 * Die Methode legt die x- und y-Position des Zoomfensters fest.
	 * @param x Die x-Position des Zoomfensters.
	 * @param y Die y-Position des Zoomfensters.
	 */
	public void setZoomPos(int x, int y) {
		zoomX = x;
		zoomY = y;
	}		// Methode setZoomPos(int x, int y)
 

	/**
	 * Die Methode legt die x- und y-Position des letzten Mausklicks fest.
	 * @param x Die x-Position des letzten Mausklicks.
	 * @param y Die y-Position des letzten Mausklicks.
	 */
	public void setLastClickPos(int x, int y) {
		lastClickX = x;
		lastClickY = y;
	}		// Methode setLastClickPos(int x, int y)
 

	/**
	 * Die Methode legt die x- und y-Position des letzten Mouse-Release Ereignisses
	 * fest.
	 * @param x Die x-Position des letzten Mouse-Release-Ereignisses.
	 * @param y Die y-Position des letzten Mouse-Release-Ereignisses.
	 */
	public void setReleasePos(int x, int y) {
		lastReleaseX = x;
		lastReleaseY = y;
	}		// Methode setReleasePos(int x, int y)
 

	/**
	 * Die Methode legt die aktuelle x- und y-Position des Mauszeigers fest.
	 * @param x Die aktuelle x-Position des Mauszeigers.
	 * @param y Die aktuelle y-Position des Mauszeigers.
	 */
	public void setCurrentPos(int x, int y) {
		currentX = x;
		currentY = y;
	}		// Methode setCurrentPos(int x, int y)
 

	/**
	 * Die Methode legt den Grauwert an der aktuellen Mausposition fest. Dieser wird
	 * zur Anzeige des Grauwerts bei Einstellung des entsprechenden Werkzeugs
	 * benoetigt.
	 * @param g Der Grauwert an der aktuellen Mausposition.
	 */
	public void setGrayValue(int g) {
		currentGray = g;
	}		// Methode setGrayValue(int g)
 

	/**
	 * Die Methode liefert die Nummer des zur Zeit selektierten Bildes.
	 * @return Die Nummer des selktierten Bildes.
	 */
	public int getActiveImage() {
		return iActiveImage;
	}		// Methode getActiveImage()
 

	/**
	 * Die Methode liefert den Bildtstapel, also die Menge aller Bilder, die sich
	 * zur Zeit im Scrollbereich befinden (auch wenn sie gerade nicht sichtbar
	 * sind).
	 * @return Der Stapel aller zur Zeit verfuegbarer Bilder.
	 */
	public ImageStack getImageStack() {
		return is;
	}		// Methode getImageStack()
 

	/**
	 * Die Methode erzeugt aus dem uebergebenen DcmImage-Objekt ein neues ImagePlus-Objekt
	 * und fuegt dieses in den Bildstapel ein. Zuerst wird die Position bestimmt, an der
	 * das Bild angelegt wird. Dann wird das Bild erzeugt und dargestellt und der Scrollbereich
	 * aktualisiert.
	 * @param di Das DcmImag-Objekt, aus dem ein ImagePlus-Objekt erzeugt werden soll.
	 * @return Das neu erzeugte ImagePlus-Objekt.
	 */
	public ImagePlus createNewImage(DcmImage di) {

		// Auslesen der Position fuer das neue Bild
		int				pos = getPosForNewImage();

		// Erzeugen eines imagePlus-Objektes aus der 12Bit-Matrix
		ImagePlus ip = is.createPictureAtPos(pos, di);

		// Tag setzen, dass ein neues Bild hinzugekommen ist
		myViewerFrame.setChanges();

		// Aktualisieren des Scrollbereichs
		updateScrollbar();

		// Ggf. neues Bild aktiveren und dorthin scrollen
		if (myViewerFrame.chbActivateNewImg.isSelected()) {
			setActiveImage(pos);

			if (iNumImages == 4) {
				myViewerFrame.sbVerScrollbar.setValue(((pos - 1) / 2) * 256);
			} else {
				myViewerFrame.sbVerScrollbar.setValue((pos - 1) * 512);
			} 
		} 

		// Neuzeichnen des Canvas kann erst spaeter gemacht werden, da von
		// VMRTFrame noch einige Parameter im imagePlus-Objekt gesetzt werden

		// Das neue Image-Objekt zurueckliefern
		return ip;
	}		// Methode createNewImage(DcmImage di)
 

	/**
	 * Die Methode fuegt das uebergebene ImagePlus-Objekt in den Bildstapel ein.
	 * Zuerst wird dafuer die Position bestimmt, an der das Bild eingefuegt wird.
	 * Dann wird das Bild in den stack eingefuegt und der Scrollbereich
	 * aktualisiert.
	 * @param myip Das ImagePlus-Objekt, das in den Stack eingefuegt werden soll.
	 * @return Die Position, an der das ImagePlus-Objekt eingefuegt wurde.
	 */
	public int createNewImage(ImagePlus myip) {

		// Bestimmen der Position fuer das Bild
		int pos = getPosForNewImage();

		// Einfuegen des ImagePlus-Objektes in den Stack
		is.setPictureAtPos(pos, myip);
		myViewerFrame.setChanges();

		// Aktualisieren des Scrollbereichs
		updateScrollbar();

		// Ggf. neues Bild aktiveren und dorthin scrollen
		if (myViewerFrame.chbActivateNewImg.isSelected()) {
			setActiveImage(pos);

			if (iNumImages == 4) {
				myViewerFrame.sbVerScrollbar.setValue(((pos - 1) / 2) * 256);
			} else {
				myViewerFrame.sbVerScrollbar.setValue((pos - 1) * 512);
			} 
		} 

		// Das neue Image-Objekt zurueckliefern
		return pos;
	}		// Methode createNewImage(ImagePlus myip)
 

	/**
	 * Die Methode bestimmt die Position fuer ein neu einzufuegendes Bild. Die
	 * Position ist von den Benutzervorgaben abhaengig. Das Bild kann entweder
	 * an der selektierten Position eingefuegt werden oder aber an der naechsten
	 * freien Position.
	 * @return Die Position, an der das naechste Bild eingefuegt werden soll.
	 */
	private int getPosForNewImage() {

		// Wenn der ImageStack noch leer (nicht vorhanden) ist, muss ein neuer
		// erzeugt werden
		if (is == null) {
			is = new ImageStack();
		} 

		// Wenn noch kein Bild selektiert war (noch keins vorhanden), so wird nun
		// das erste Bild das aktive, ansonsten bleibt das alte selektierte Bild
		// weiterhin selektiert
		iActiveImage = iActiveImage == -1 ? 1 : iActiveImage;

		// Nun wird noch entschieden, ob das neue Bild an der selektierten Position
		// oder an der naechsten freien Position positioniert wird
		int pos = 0;

		if (myViewerFrame.rbNextImageAtSelection.isSelected()) {
			pos = iActiveImage;
		} else {
			pos = is.getNextFreePosition();
			if (pos == 0) {
				pos++;
			} 
		} 

		return pos;
	}		// Methode getPosForNewImage()
 

	/**
	 * Die Methode liefert den aktuellen Offset des Scrollbalkens in Bildern zurueck.
	 * @return Der aktuelle Offset des Scrollbalkens.
	 */
	private int getSBOffset() {
		int off = 0;

		if (iNumImages == 4) {
			off = myViewerFrame.sbVerScrollbar.getValue() / 256;
		} else {
			off = myViewerFrame.sbVerScrollbar.getValue() / 512;
		} 
		return off;
	}		// Methode getSBOffset()
 

	/**
	 * Die Paint Methode sorgt dafuer, dass alle gewuenschten Elemente auf der
	 * Zeichenflaeche dargestellt werden. Die Methode wird immer dann aufgerufen,
	 * wenn sich etwas auf der Zeichenflaeche veraendern soll. Dazu zaehlen Aenderungen
	 * an den Bilder, an den Bildbeschriftungen oder Darstellungselemente, die durch
	 * die Werkzeuge bedingt sind (Lupe, Distanzmessung, Winkelmessung).
	 * @param g Der Graphikkontext.
	 */
	public void paint(Graphics g) {

		// Zunaechst mal den Graphikontext komplett loeschen
		g.clearRect(0, 0, 511, 511);

		// Zeichnen des Bildes bzw. der Bilder
		ImagePlus ip = null;

		if (iNumImages == 4) {
			int off = getSBOffset();

			for (int i = 0; i < 4; i++) {
				ip = is.getPictureAtPos(2 * off + i + 1);
				if (ip != null) {
					g.drawImage(ip.getAWTImage(), (i % 2) * 256, (i / 2) * 256, 256, 256, this);
				} 
			} 
		} else {

			int off = getSBOffset();

			ip = is.getPictureAtPos(off + 1);
			if (ip != null) {
				g.drawImage(ip.getAWTImage(), 0, 0, 512, 512, this);
			} 
		} 

		// Zeichnen der Umrandung der Bilder, dazu zunaechst Malfarbe auf
		// weiss setzen. Fallunterscheidung 1/4 Bilder

		g.setColor(Color.white);
		g.drawRect(0, 0, 511, 511);

		switch (iNumImages) {
		case 4: {
			g.drawLine(255, 0, 255, 511);
			g.drawLine(0, 255, 511, 255);
			break;
		} 
		case 1: {
			break;
		} 
		}		// switch

		// Zeichnen der Umrandung des aktiven Bildes
		g.setColor(Color.red);

		if (iActiveImage != -1) {
			if (iNumImages == 1) {
				int off = getSBOffset();

				if (iActiveImage == off + 1) {
					g.drawRect(0, 0, 511, 511);
				} 
			} else {
				int off = getSBOffset();

				// Liegt aktives Bild im sichtbaren Scrollbereich ???
				if ((iActiveImage > off * 2) && (iActiveImage <= (off * 2 + 4))) {
					int num = iActiveImage - (off * 2);

					int startx = 0, starty = 0;

					switch (num) {
					case 1: {
						startx = 0;
						starty = 0;
						break;
					} 
					case 3: {
						startx = 0;
						starty = 255;
						break;
					} 
					case 2: {
						startx = 255;
						starty = 0;
						break;
					} 
					case 4: {
						startx = 255;
						starty = 255;
						break;
					} 
					}		// switch
					g.drawRect(startx, starty, 255, 255);
				}			// if
 			 }			// if-else
 		 }				// if
 

		// Zeichnen der Bildbeschriftungen in gruen
		g.setColor(Color.green);

		// Auslesen der Bildkommentare fuer alle 4 Ecken
		String[]	commentsNW;
		String[]	commentsNE;
		String[]	commentsSW;
		String[]	commentsSE;

		if (myViewerFrame.tbImageText.isSelected()) {
			if (iNumImages == 4) {
				Font	myFont = new Font("Arial", Font.PLAIN, 10);

				g.setFont(myFont);
				for (int i = 0; i < 4; i++) {
					int off = getSBOffset();

					ip = is.getPictureAtPos(2 * off + 1 + i);
					if (ip != null) {

						// Einzeichnen der Kommentare
						commentsNW = ip.getTextNW();
						if (commentsNW != null) {
							for (int j = 0; j < commentsNW.length; j++) {
								g.drawString(commentsNW[j], (i % 2) * 256 + 5, (i / 2) * 256 + 22 + 12 * j);
							}		// Ende for j
 						 } 
						commentsNE = ip.getTextNE();
						if (commentsNE != null) {
							for (int j = 0; j < commentsNE.length; j++) {
								g.drawString(commentsNE[j], ((i % 2) * 256) + 152, (i / 2) * 256 + +10 + 10 * j);
							}		// Ende for j
 						 } 
						commentsSW = ip.getTextSW();
						if (commentsSW != null) {
							for (int j = 0; j < commentsSW.length; j++) {
								g.drawString(commentsSW[j], (i % 2) * 256 + 5, (i / 2) * 256 + 256 - 8 - 10 * j);
							}		// Ende for j
 						 } 
						commentsSE = ip.getTextSE();
						if (commentsSE != null) {
							for (int j = 0; j < commentsSE.length; j++) {
								g.drawString(commentsSE[j], ((i % 2) * 256) + 152, (i / 2) * 256 + 256 - 8 - 10 * j);
							}		// Ende for j
 						 } 

						// Einzeichnen der Bildnummer
						g.drawString("Bild " + (2 * off + i + 1), (i % 2) * 256 + 5, (i / 2) * 256 + 10);
					}				// Ende if  (ip != null)
 				 }				// Ende for i
 			 } else {
				Font	myFont = new Font("Arial", Font.PLAIN, 12);

				g.setFont(myFont);
				int off = getSBOffset();

				ip = is.getPictureAtPos(off + 1);
				if (ip != null) {

					// Einzeichnen der Kommentare
					commentsNW = ip.getTextNW();
					if (commentsNW != null) {
						for (int i = 0; i < commentsNW.length; i++) {
							g.drawString(commentsNW[i], 5, 30 + 15 * i);
						}			// Ende for i
 					 } 
					commentsNE = ip.getTextNE();
					if (commentsNE != null) {
						for (int i = 0; i < commentsNE.length; i++) {
							g.drawString(commentsNE[i], 393, 15 + 15 * i);
						}			// Ende for i
 					 } 
					commentsSW = ip.getTextSW();
					if (commentsSW != null) {
						for (int i = 0; i < commentsSW.length; i++) {
							g.drawString(commentsSW[i], 5, 500 - 15 * i);
						}			// Ende for i
 					 } 
					commentsSE = ip.getTextSE();
					if (commentsSE != null) {
						for (int i = 0; i < commentsSE.length; i++) {
							g.drawString(commentsSE[i], 390, 500 - 15 * i);
						}			// Ende for i
 					 } 

					// Einzeichnen der Bildnummer
					g.drawString("Bild " + (off + 1), 5, 15);
				}					// Ende if (ip != null)
 			 }					// Ende if (numImages)
 		 }						// Ende if tb_ImageText gedrueckt
 

		// Einzeichnen der Lupe (falls selektiert)
		if (myViewerFrame.rbZoom.isSelected()) {
			if (zoomImage != null) {
				g.drawImage(zoomImage, zoomX, zoomY, this);
				g.setColor(Color.yellow);
				g.drawRect(zoomX - 1, zoomY - 1, 65, 65);
			} 
		}		// if (Lupe)
 
		// Einzeichnen der Grauwertmessung (falls selektiert)
		if (myViewerFrame.rbGrayValue.isSelected()) {
			if (currentGray != (-1)) {
				int offX = 5;
				int offY = 5;

				if (currentX > (this.getWidth() - 40)) {
					offX = -30;
				} 
				if (currentY > (this.getHeight() - 40)) {
					offY = -20;
				} 
				this.getWidth();
				g.setColor(Color.yellow);
				g.fillRect(currentX + offX, currentY + offY, 27, 12);
				g.setFont(new Font("Arial", Font.PLAIN, 10));
				g.setColor(Color.black);
				g.drawString("" + currentGray, currentX + offX + 2, currentY + offY + 9);
			} 
		}		// if (Grauwert-Messung)
 
		// Einzeichnen der Hilfslinie fuer die Distanzmessung (falls selektiert)
		if (myViewerFrame.rbDistance.isSelected()) {
			if ((lastClickX != -1) && (currentX != -1)) {

				// Einzeichnen der Distanzlinie
				g.setColor(Color.yellow);
				g.drawLine(lastClickX, lastClickY, currentX, currentY);

				// Berechenen der Distanz in Pixeln
				int			distanceX = Math.abs(lastClickX - currentX);
				int			distanceY = Math.abs(lastClickY - currentY);

				// Besorgen des Pixel-Spacings
				double	spacingX = ip.getPixelSpacingColumn();
				double	spacingY = ip.getPixelSpacingRow();
				String	unit = "";

				if ((spacingX > 0) && (spacingY > 0)) {
					distanceX *= spacingX;
					distanceY *= spacingY;
					unit = " mm";
				} else {
					unit = " Pixel";
				} 
				int distance = (int) Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));

				if (myViewerFrame.rb1Image.isSelected()) {
					distance /= 2;

					// Einzeichnen der Distanz (gelb hinterlegt);
				} 
				int offX = 10;
				int offY = 10;

				if (currentX > (this.getWidth() - 60)) {
					offX = -50;
				} 
				if (currentY > (this.getHeight() - 30)) {
					offY = -20;
				} 
				g.fillRect(currentX + offX, currentY + offY, 50, 12);
				g.setFont(new Font("Arial", Font.PLAIN, 10));
				g.setColor(Color.black);
				g.drawString("" + distance + unit, currentX + offX + 2, currentY + offY + 9);

			} 
		}		// if (Abstandsmessung)
 
		// Einzeichnen der Hilfslinie fuer die Winkelmessung (falls selektiert)
		if (myViewerFrame.rbAngel.isSelected()) {
			if ((lastClickX != -1) && (currentX != -1)) {
				if (lastReleaseX == -1) {

					// Einzeichnen der Rferenzlinie
					g.setColor(Color.yellow);
					g.drawLine(lastClickX, lastClickY, currentX, currentY);
				} else {

					// Einzeichnen der Referenzlinie und der zweiten Linie
					g.setColor(Color.yellow);
					g.drawLine(lastClickX, lastClickY, lastReleaseX, lastReleaseY);
					g.drawLine(lastClickX, lastClickY, currentX, currentY);

					// Berechnen des Winkels
					double	a1 = (double) lastReleaseX - lastClickX;
					double	a2 = (double) lastReleaseY - lastClickY;
					double	b1 = (double) currentX - lastClickX;
					double	b2 = (double) currentY - lastClickY;
					double	cosPhi = ((a1 * b1) + (a2 * b2)) / (Math.sqrt(a1 * a1 + a2 * a2) * Math.sqrt(b1 * b1 + b2 * b2));
					int			phi = (int) (Math.acos(cosPhi) * 360 / Math.PI / 2);

					// Einzeichnen des Winkels
					int			offX = 10;
					int			offY = 10;

					if (currentX > (this.getWidth() - 40)) {
						offX = -35;
					} 
					if (currentY > (this.getHeight() - 30)) {
						offY = -20;
					} 
					g.fillRect(currentX + offX, currentY + offY, 25, 12);
					g.setFont(new Font("Arial", Font.PLAIN, 10));
					g.setColor(Color.black);
					g.drawString("" + phi + "°", currentX + offX + 2, currentY + offY + 9);
				}		// if-else
 			 }		// if
 		 }			// if (Winkelmessung)
 
	}					// Methode paint()
 
}






/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

