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

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.image.*;

import virtual.tools.*;


/**
 * Die Klasse implementiert die Zeichenflaeche des virtuellen MRT.
 * Insbesondere wird die paint-Methode der JScrollPane-Klasse ueberschrieben, um
 * die Bilder und Beschriftungen einzuzeichnen. Darueber hinaus verwaltet diese
 * Klasse den gesamten Bildstapel und ist für das Einfuegen von neuen Bildern
 * in diesen Stapel verantwortlich.
 * <DL><DT><B>Modifications: </B><DD>
 * Thomas Hacklaender 2000.8.18:
 * Um die Vorgabe der abstrakten Oberklasse zu erfuellen createNewImage(ImagePlus)
 * implementiert.<br>
 * Thomas Hacklaender 2000.8.18:
 * Die Klasse von ImagePanel statt von JPanel abgeleitet.<br>
 * Thomas Hacklaender 2002.11.1:
 * Bildbeschriftung modifiziert.<br>
 * </DD></DL>
 * @author   Christian Schalla
 * @author   Andreas Truemper
 * @version  1.2, 2002.11.1
 */
public class MRTPanel extends ImagePanel {


	// th 2005.11.11, added
	ResourceBundle			frameRsrc = PropertyResourceBundle.getBundle("virtual/mrt/resources/vmrt_frame");


	/**
	 * Anzahl der Bilder, die gleichzeitig dargestellt werden (1 oder 4).
	 */
	private int					iNumImages = 1;


	/**
	 * Nummer des selektierten Bildes. Ist -1, wenn kein Bild selektiert ist.
	 */
	private int					iActiveImage = -1;


	/**
	 * Verweis auf den Stapel aller Bilder, die zur Zeit dargestellt werden.
	 */
	private ImageStack	is;


	/**
	 * Rueckreferenz zum Hauptfenster. Diese wird benoetigt, um Einstellungen
	 * von einigen Bedienelementen auszulesen.
	 */
	VMRTFrame						myVMRTFrame;


	/**
	 * Standard-Konstruktor.
	 */
	public MRTPanel() {}


	/**
	 * Konstruktor mit einer Rueckreferenz zum Hauptfenster als Parameter.
	 * @param backReference Rueckreferenz zum Haupfenster. Sie wird benoetigt, um
	 * Einstellungen einiger Bedienelemente auszulesen.
	 */
	public MRTPanel(VMRTFrame backReference) {
		myVMRTFrame = backReference;
		is = new ImageStack();
	}		// Konstruktor


	/**
	 * Die Methode setzt den internen Parameter, welcher angibt, wie viele Bilder
	 * gleichzeitig dargestellt werden, auf 1. Ausserdem wird der Scrollbereich
	 * aktualisiert.
	 */
	public void setOneImage() {
		iNumImages = 1;
		updateScrollbar();
	}		// Methode setOneImage
 

	/**
	 * Die Methode setzt den internen Parameter, welcher angibt, wie viele Bilder
	 * gleichzeitig dargestellt werden, auf 4. Ausserdem wird der Scrollbereich
	 * aktualisiert.
	 */
	public void setFourImages() {
		iNumImages = 4;
		updateScrollbar();
	}		// Methode setFourImages
 

	/**
	 * Die Methode aktualisiert die Scrollbarlkeneinstellungen in Abhaengigkeit davon,
	 * ob ein oder vier Bilder gleichzeitig dargestellt werden sollen.
	 */
	public void updateScrollbar() {
		int num = is.getStackSize();

		// Ein Bild gleichzeitig darstellen
		// 20.4.00 Umgestellt auf vertikales Scrollen
		if (myVMRTFrame.rb1Image.isSelected()) {
			myVMRTFrame.sbVerScrollbar.setMinimum(0);
			myVMRTFrame.sbVerScrollbar.setMaximum(num * 512 + 512);
			myVMRTFrame.sbVerScrollbar.setBlockIncrement(512);
			myVMRTFrame.sbVerScrollbar.setUnitIncrement(512);
			myVMRTFrame.sbVerScrollbar.setVisibleAmount(511);
		} else {	// 4 Bilder gleichzeitig darstellen
			int max = 512;

			if (num > 0) {
				max = num / 2 * 256 + 256;
			} 
			myVMRTFrame.sbVerScrollbar.setMinimum(0);
			myVMRTFrame.sbVerScrollbar.setMaximum(max);
			myVMRTFrame.sbVerScrollbar.setBlockIncrement(512);
			myVMRTFrame.sbVerScrollbar.setUnitIncrement(256);
			myVMRTFrame.sbVerScrollbar.setVisibleAmount(511);
		}					// if-else
 	 }					// Methode updateScrollbar
 

	/**
	 * Die Methode liefert den Offset des Scrollbalkens zurueck.
	 * @return Der Offset des Scrollbalkens.
	 */
	private int getSBOffset() {
		int off = 0;

		if (iNumImages == 4) {
			off = myVMRTFrame.sbVerScrollbar.getValue() / 256;
		} else {
			off = myVMRTFrame.sbVerScrollbar.getValue() / 512;
		}		// if-else
		 return off;
	}		// Methode getSBOffset
 

	/**
	 * Die Methode liefert die Anzahl der gleichzeitig dargestellten Bilder zurueck.
	 * @return Die Anzahl gleichzeitig dargestellter Bilder.
	 */
	public int getNumImages() {
		return iNumImages;
	}		// Methode hetNumImages
 

	/**
	 * Die Methode legt die Nummer des zur Zeit selektierten Bildes fest.
	 * @param activeImage Die Nummer des selektierten Bildes.
	 */
	public void setActiveImage(int activeImage) {
		iActiveImage = activeImage;

		// tha:2000.07.17 // System.out.println("Aktives Bild: " + iActiveImage);
	}		// Methode setActiveImage
 

	/**
	 * Die Methode liefert die Nummer des zur Zeit selektierten Bildes zurueck.
	 * @return Die Nummer des selktierten Bildes.
	 */
	public int getActiveImage() {
		return iActiveImage;
	}		// Methode getActiveImage
 

	/**
	 * Die Methode liefert den ImageStack, also die Menge aller Bilder, die sich
	 * zur Zeit im Scrollbereich befinden (auch wenn sie gerade nicht sichtbar
	 * sind).
	 * @return Der aktuelle Bildstapel.
	 */
	public ImageStack getImageStack() {
		return is;
	} 


	/**
	 * Die Methode erzeugt aus einen DcmImage (12-Bit-Bild)
	 * ein ImagePlus-Objekt (ein Bild inklusive Fensterungseinstellungen
	 * (Window und Center) und weiterer Informationen). Dieses neue Objekt wird dem
	 * Bildstapel hinzugefuegt und an den Benutzer zurueckgeliefert.
	 * @param intensityImage Das ungefensterte 12-Bit Bild.
	 * @return Das neu erzeugte ImagePlus-Objekt.
	 */
	public ImagePlus createNewImage(DcmImage dcmimg) {

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

		if (myVMRTFrame.rbNextImageAtSelection.isSelected()) {
			pos = iActiveImage;
		} else {
			pos = is.getNextFreePosition();
			if (pos == 0) {
				pos++;
			} 
		} 

		// Erzeugen eines imagePlus-Objektes aus der 12Bit-Matrix
		ImagePlus ip = is.createPictureAtPos(pos, dcmimg);

		// Aktualisieren des Scrollbereichs
		updateScrollbar();

		// Ggf. neues Bild aktiveren und dorthin scrollen
		if (myVMRTFrame.chbActivateNewImg.isSelected()) {
			setActiveImage(pos);

			if (iNumImages == 4) {
				myVMRTFrame.sbVerScrollbar.setValue(((pos - 1) / 2) * 256);
			} else {
				myVMRTFrame.sbVerScrollbar.setValue((pos - 1) * 512);
			} 
		} 

		// Neuzeichnen des Canvas kann erst spaeter gemacht werden, da von
		// VMRTFrame noch einige Parmaeter im imagePlus-Objekt gesetzt werden

		// Das neue Image-Objekt zurueckliefern
		return ip;
	} 


	/**
	 * Die Methode fuegt das uebergebene ImagePlus-Objekt in den Bildstapel ein.
	 * Zuerst wird dafuer die Position bestimmt, an der das Bild eingefuegt wird.
	 * Dann wird das Bild in den stack eingefuegt und der Scrollbereich
	 * aktualisiert.
	 * @param myip Das ImagePlus-Objekt, das in den Stack eingefuegt werden soll.
	 * @return Die Position, an der das ImagePlus-Objekt eingefuegt wurde.
	 */
	public int createNewImage(ImagePlus myip) {

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

		if (myVMRTFrame.rbNextImageAtSelection.isSelected()) {
			pos = iActiveImage;
		} else {
			pos = is.getNextFreePosition();
			if (pos == 0) {
				pos++;
			} 
		} 

		// Erzeugen eines imagePlus-Objektes aus der 12Bit-Matrix
		is.setPictureAtPos(pos, myip);

		// Aktualisieren des Scrollbereichs
		updateScrollbar();

		// Ggf. neues Bild aktiveren und dorthin scrollen
		if (myVMRTFrame.chbActivateNewImg.isSelected()) {
			setActiveImage(pos);

			if (iNumImages == 4) {
				myVMRTFrame.sbVerScrollbar.setValue(((pos - 1) / 2) * 256);
			} else {
				myVMRTFrame.sbVerScrollbar.setValue((pos - 1) * 512);
			} 
		} 

		// Neuzeichnen des Canvas kann erst spaeter gemacht werden, da von
		// VMRTFrame noch einige Parmaeter im imagePlus-Objekt gesetzt werden

		return pos;
	} 


	/**
	 * Die paint-Methode sorgt dafuer, dass alle gewuenschten Elemente auf der
	 * Zeichenflaeche dargestellt werden. Dazu gehoeren die Bilder selbst,
	 * sowie deren Beschriftungen. Die Methode wird immer dann aufgerufen,
	 * wenn sich etwas auf der Zeichenflaeche veraendern soll.
	 * @param g Der Graphikkontext.
	 */
	public void paint(Graphics g) {
		FontMetrics myFontMetrics;
		int					lHeight;
		int					lSpacing;
		int					margin;
		int					stringWidth;

		// Zunaechst mal den Graphikontext komplett loeschen
		g.clearRect(0, 0, 511, 511);

		// Zeichnen des Bildes bzw. der Bilder
		ImagePlus ip = null;

		if (iNumImages == 4) {
			int off = getSBOffset();

			for (int i = 0; i < 4; i++) {
				ip = is.getPictureAtPos(2 * off + i + 1);
				if (ip != null) {

					// Umstellung auf vertikales Scrollen
					g.drawImage(ip.getAWTImage(), (i % 2) * 256, (i / 2) * 256, 256, 256, this);

					// g.drawImage(ip.getAWTImage(),(i / 2) * 256,(i % 2) * 256,256,256,this);
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

					// Umstellung auf vertikales Scrollen
					// case 1: {startx=0; starty=0; break; }
					// case 3: {startx=255; starty=0; break; }
					// case 2: {startx=0; starty=255; break; }
					// case 4: {startx=255; starty=255; break; }
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
		String[]	comments;
		String[]	commentsNW;
		String[]	commentsNE;
		String[]	commentsSW;
		String[]	commentsSE;

		if (myVMRTFrame.tbImageText.isSelected()) {

			if (iNumImages == 4) {

				// ///////////////////////////////////////////////
				// 
				// Falls 4 Bilder gleichzeitig dargestellt werden
				// 
				// ///////////////////////////////////////////////

				g.setFont(new Font("Arial", Font.PLAIN, 10));

				// th 2002.11.1
				myFontMetrics = g.getFontMetrics();
				lHeight = myFontMetrics.getHeight();
				lSpacing = 0;
				margin = 5;

				for (int i = 0; i < 4; i++) {
					int off = getSBOffset();

					ip = is.getPictureAtPos(2 * off + 1 + i);

					if (ip != null) {

						// Einzeichnen der Kommentare
						commentsNW = ip.getTextNW();

						// Einzeichnen der Bildnummer
						g.drawString(frameRsrc.getString("mrtpanel.text.nw.image") + " " + (off + 1), margin, lHeight + lSpacing);
						if (commentsNW != null) {
							for (int j = 0; j < commentsNW.length; j++) {
								g.drawString(commentsNW[j], (i % 2) * 256 + margin, (i / 2) * 256 + (lHeight + lSpacing) * (j + 2));

								// g.drawString(commentsNW[j], (i/2)*256 + 5, (i%2)*256 + 22 +12*j);
							} 
						} 

						commentsSW = ip.getTextSW();
						if (commentsSW != null) {
							for (int j = commentsSW.length - 1; j >= 0; j--) {
								g.drawString(commentsSW[j], (i % 2) * 256 + margin, (i / 2) * 256 + 256 - margin - (lHeight + lSpacing) * j);

								// g.drawString(commentsSW[j], (i/2)*256 + 5, (i%2)*256 + 256 - 8 - 10*j);
							} 
						} 

						commentsNE = ip.getTextNE();
						if (commentsNE != null) {
							for (int j = 0; j < commentsNE.length; j++) {
								stringWidth = (int) myFontMetrics.getStringBounds(commentsNE[j], g).getWidth();
								g.drawString(commentsNE[j], ((i % 2) * 256) + 256 - margin - stringWidth, (i / 2) * 256 + (lHeight + lSpacing) * (j + 1));

								// g.drawString(commentsNE[j], ((i/2)*256)+152, (i%2)*256 + +10 + 10*j);
							} 
						} 

						commentsSE = ip.getTextSE();
						if (commentsSE != null) {
							for (int j = commentsSE.length - 1; j >= 0; j--) {
								stringWidth = (int) myFontMetrics.getStringBounds(commentsSE[j], g).getWidth();
								g.drawString(commentsSE[j], ((i % 2) * 256) + 256 - margin - stringWidth, (i / 2) * 256 + 256 - margin - (lHeight + lSpacing) * j);

								// g.drawString(commentsSE[j], ((i/2)*256)+152, (i%2)*256 + 256 - 8 - 10*j);
							} 
						} 


					} 
				} 

			} else {

				// ///////////////////////////////////////////////
				// 
				// Falls 1 Bild dargestellt wird
				// 
				// ///////////////////////////////////////////////

				g.setFont(new Font("Arial", Font.PLAIN, 12));

				// th 2002.11.1
				myFontMetrics = g.getFontMetrics();
				lHeight = myFontMetrics.getHeight();
				lSpacing = 1;
				margin = 5;

				int off = getSBOffset();

				ip = is.getPictureAtPos(off + 1);
				if (ip != null) {

					// Einzeichnen der Kommentare
					commentsNW = ip.getTextNW();

					// Einzeichnen der Bildnummer
					g.drawString(frameRsrc.getString("mrtpanel.text.nw.image") + " " + (off + 1), margin, lHeight + lSpacing);
					if (commentsNW != null) {
						for (int j = 0; j < commentsNW.length; j++) {

							// Erste Zeile ist die Bildnummer
							g.drawString(commentsNW[j], margin, (lHeight + lSpacing) * (j + 2));
						} 
					} 

					commentsSW = ip.getTextSW();
					if (commentsSW != null) {
						for (int j = commentsSW.length - 1; j >= 0; j--) {
							g.drawString(commentsSW[j], margin, 512 - margin - (lHeight + lSpacing) * j);
						} 
					} 

					commentsNE = ip.getTextNE();
					if (commentsNE != null) {
						for (int j = 0; j < commentsNE.length; j++) {
							stringWidth = (int) myFontMetrics.getStringBounds(commentsNE[j], g).getWidth();
							g.drawString(commentsNE[j], 512 - margin - stringWidth, (lHeight + lSpacing) * (j + 1));
						} 
					} 

					commentsSE = ip.getTextSE();
					if (commentsSE != null) {
						for (int j = commentsSE.length - 1; j >= 0; j--) {
							stringWidth = (int) myFontMetrics.getStringBounds(commentsSE[j], g).getWidth();
							g.drawString(commentsSE[j], 512 - margin - stringWidth, 512 - margin - (lHeight + lSpacing) * j);
						} 
					} 

				}		// Ende if (ip != null)
 			 }		// Ende if (numImages)
 		 }			// Ende if tb_ImageText gedrueckt
 
	}					// Methode paint
 
}






/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

