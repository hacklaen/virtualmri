/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

/*
 * Copyright (C) 1999, 2000 Thomas Hacklaender, Christian Schalla,
 * Andreas Truemper
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GFNU General Public License 2
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
import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import virtual.tools.*;

// import jigl.*;
import jigl.image.RealGrayImage;

import rad.dicom.dcm.*;
import rad.dicom.ima.*;
import rad.ijplugin.dcm.*;
import rad.ijplugin.util.*;


/**
 * Die Klasses implementiert das Hauptfenster des DICOM-Viewers mit all seinen
 * Bedienelementen und deren Ereignisbehandlung.
 * Ein Grossteil der Funktionalitaet des DICOM-Viewers steckt ebenfalls in
 * dieser Klasse.
 * <DL><DT><B>Modifications: </B><DD>
 * Thomas Hacklaender 2000.8.20:
 * Die Methode pbSave_actionPerformed neu implementiert. Sie verwendet jetzt
 * das ExportPanel aus der dcm-Bibliothek.<br>
 * Thomas Hacklaender 2000.8.20:
 * Die Methode pbLoad_actionPerformed neu implementiert. Sie verwendet jetzt
 * das ImportPanel aus der dcm-Bibliothek. Damit ist auch die Methode
 * pbLoadSeries_actionPerformed ueberflueesig geworden. Der Button LoadSeries und
 * der Menueeintrag "Serie Oeffnen" wurde entfernt. Die Klasse SeriesLoader
 * wurde komplett neu implementiert.<br>
 * Thomas Hacklaender 2000.4.3:
 * Umstellung auf die aktuelle Version 2.1 des dcm-Package.<br>
 * Thomas Hacklaender 2000.4.3:
 * Die Methoden calcProjection2 und calcProjection2 komplett neu
 * programmiert. Sie stellen die Rekonstruktionen jetz ohne
 * Verzerrung dar.<br>
 * Thomas Hacklaender 2000.7.9:
 * In loadDcmImage Window-Werte korrekt einlesen.<br>
 * Thomas Hacklaender 2000.7.9:
 * Knopf zur Erzeugung eines Referenzdatensatzes und zugehoerige Methoden.<br>
 * Thomas Hacklaender 2000.7.14:
 * Menues erweitert.<br>
 * Thomas Hacklaender 2002.10.12:
 * About-Menues gestrichen. Info in Tools-Menue.<br>
 * </DD></DL>
 * @author   Christian Schalla
 * @author   Andreas Truemper
 * @version  2002.10.12
 */
public class ViewerFrame extends JFrame {

	// Einige Konstanten


	/**
	 * Standard-Mauszeiger.
	 */
	private final Cursor	DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);


	/**
	 * Mauszeiger fuer die Zoom-Funktion, die Distanzmessung und die Winlemessung.
	 */
	private final Cursor	ZOOM_CURSOR = new Cursor(Cursor.CROSSHAIR_CURSOR);


	/**
	 * Mauszeiger fuer die Fensterung.
	 */
	private final Cursor	WINDOW_CURSOR = new Cursor(Cursor.MOVE_CURSOR);

	// Menueleiste inlusive ihrer Menues und deren Menueeintraege erzeugen


	/**
	 * Die Menueleiste.
	 */
	private JMenuBar			menuBar1 = new JMenuBar();


	/**
	 * Das Datei-Menue.
	 */
	private JMenu					menuFile = new JMenu();


	/**
	 * Dateimenueeintrag 'Bild Oeffnen'.
	 */
	private JMenuItem			menuFileImageOpen = new JMenuItem();


	/**
	 * Dateimenueeintrag 'Serie Oeffnen'.
	 */

	// Nicht mehr notwendig: tha 2000.8.20
	// private JMenuItem			menuFileSeriesOpen = new JMenuItem();


	/**
	 * Dateimenueeintrag 'Speichern'.
	 */
	private JMenuItem			menuFileSave = new JMenuItem();


	/**
	 * Dateimenueeintrag 'Drucken'.
	 */
	private JMenuItem			menuFilePrint = new JMenuItem();


	/**
	 * Dateimenueeintrag 'Beenden'.
	 */
	private JMenuItem			menuFileExit = new JMenuItem();


	/**
	 * Bearbeiten-Menue.
	 */
	private JMenu					menuEdit = new JMenu();


	/**
	 * Bearbeiten-Menueeintrag 'Selektiertes Bild loeschen'.
	 */
	private JMenuItem			menuEditClear = new JMenuItem();


	/**
	 * Bearbeiten-Menueeintrag 'Alle Bilder loeschen'.
	 */
	private JMenuItem			menuEditClearAll = new JMenuItem();


	/**
	 * Tools-Menue.
	 */
	private JMenu					menuTools = new JMenu();


	/**
	 * Hilfemenueeintrag 'Info...'.
	 */
	private JMenuItem			menuToolsAbout = new JMenuItem();


	/**
	 * Hilfe-Menue.
	 */

	// th 2002.10.12
	// private JMenu					menuHelp = new JMenu();


	// Hier werden einige Icons fuer einige Buttons deklariert


	/**
	 * Icon fuer den Knopf zum Laden eines Einzelbides.
	 */
	private ImageIcon			iOpen;


	/**
	 * Icon fuer den Knopf zum Laden einer Bildserie.
	 */

	// Nicht mehr notwendig: tha 2000.8.20
	// private ImageIcon			iOpenSeries;


	/**
	 * Icon fuer den Knopf zum Speichern eines Bildes.
	 */
	private ImageIcon			iSave;


	/**
	 * Icon fuer den Knopf zum Drucken des Zeichenflaecheninhalts.
	 */
	private ImageIcon			iPrint;


	/**
	 * Icon fuer den Knopf zur Erzeugung eines Referenzbildes.
	 */
	private ImageIcon			iRefIma;


	/**
	 * Icon fuer den Knopf zur Darstellung eines Einzelbildes.
	 */
	private ImageIcon			i1Image;


	/**
	 * Icon fuer den Knopf zur gleichzeitigen Darstellung von 4 Bildern.
	 */
	private ImageIcon			i4Images;


	/**
	 * Icon fuer den Knopf zum Ein- bzw. Ausschalten der Bildbeschriftung.
	 */
	private ImageIcon			iInfoText;


	/**
	 * Icon fuer den Knopf zur Anzeige des Histogramm-Fensters.
	 */
	private ImageIcon			iHistogram;


	/**
	 * Icon fuer den Knopf zur Anzeiges des k-Raum-Fensters.
	 */
	private ImageIcon			iKSpace;


	/**
	 * Icon fuer den Knopf zur Anzeige des k-Raum-Manipulator-Fensters.
	 */
	private ImageIcon			iKSpaceMan;


	/**
	 * Icon fuer den Knopf zur Winkelmessung.
	 */
	private ImageIcon			iAngel;


	/**
	 * Icon fuer den Knopf zur Auswahl des Standardmauszeigers.
	 */
	private ImageIcon			iPointer;


	/**
	 * Icon fuer den Knopf der Lupe-Funktion.
	 */
	private ImageIcon			iZoom;


	/**
	 * Icon fuer den Knopf der Rotations-Funktion.
	 */
	private ImageIcon			iRotate;


	/**
	 * Icon fuer den Knopf zum Spiegeln des selektierten Bildes.
	 */
	private ImageIcon			iMirror;


	/**
	 * Icon fuer den Knopf zum Invertieren des selektierten Bildes.
	 */
	private ImageIcon			iInvert;


	/**
	 * Icon fuer den Knopf fuer die Grauwertmessungs-Funktion.
	 */
	private ImageIcon			iGrayValue;


	/**
	 * Icon fuer den Knopf zur Abstandsmessung.
	 */
	private ImageIcon			iDistance;


	/**
	 * Icon fuer den Knopf zur Projektionsberechnung (1. Variante).
	 */
	private ImageIcon			iProjection1;


	/**
	 * Icon fuer den Knopf zur Projektionsberechnung (2. Variante).
	 */
	private ImageIcon			iProjection2;


	/**
	 * Icon fuer den Knopf zum Loeschen des selektierten Bilder.
	 */
	private ImageIcon			iDelete;


	/**
	 * Icon fuer den Knopf zur Anzeige des Animationsfensters.
	 */
	private ImageIcon			iAnimation;


	/**
	 * Statusleiste.
	 */
	protected JLabel			statusBar = new JLabel();


	/**
	 * Fortschrittsanzeige.
	 */
	private JProgressBar	prbarProgress = new JProgressBar();


	/**
	 * Beschriftung fuer die Positionsangabe des Mauszeigers.
	 */
	private JLabel				lPosition = new JLabel();


	/**
	 * Ausgabefeld fuer die Positionsangabe des Mauszeigers.
	 */
	private JLabel				lPositionText = new JLabel();


	/**
	 * Zeichenflaeche (Bildflaeche / Canvas).
	 */

	// Es handelt sich dabei um eine von JScrollPane abgeleitete Klasse, da die
	// paint-Methode ueberschrieben werden muss
	DVPanel								pCanvas = new DVPanel(this);


	/**
	 * Horizontale Bildlaufleiste fuer die Zeichenflaeche.
	 */
	JScrollBar						sbVerScrollbar = new JScrollBar();

	// Erzeugen der Funktionsleiste am rechten Rnd des Fensters (Toolbar)
	// inklusive aller darin enthaltenen Elemente


	/**
	 * Rahmen fuer den Werkzeugbereich am rechten Rand des Fensters.
	 */
	private JPanel				pToolbar = new JPanel();

	// tpMain ist das Hauptpanel des Karteireiters
	// Dort hinein werden weitere Panels gepackt, die die einzelnen
	// Karteikarten repraesentieren


	/**
	 * Hauptrahmen fuer die Karteikarten.
	 */
	private JTabbedPane		tpMain = new JTabbedPane();


	/**
	 * Knopf zum Laden eines neuen DICOM-Bildes.
	 */
	private JButton				pbLoad = new JButton();


	/**
	 * Knopf zum Laden einer neuen DICOM-Bildserie.
	 */

	// Nicht mehr notwendig: tha 2000.8.20
	// private JButton				pbLoadSeries = new JButton();


	/**
	 * Knopf zum Speichern eines Bildes.
	 */
	private JButton				pbSave = new JButton();


	/**
	 * Knopf zum Drucken des Zeichenflaecheninhalts.
	 */
	private JButton				pbPrint = new JButton();


	/**
	 * Knopf zum Laden des Referenzdatensatzes.
	 */
	private JButton				pbReferenceData = new JButton();


	/**
	 * Karteikarte fuer alle Layout-Funkionen.
	 */
	private JPanel				tpLayout = new JPanel();


	/**
	 * Karteikarte fuer alle Werkzeuge (Lupe, Distanzmessung, Winkelmessung,
	 * Grauwertmessung, Invertieren, Spiegeln, Drehen).
	 */
	private JPanel				tpTools = new JPanel();


	/**
	 * Karteikarte fuer die Fensterungs-Funktionen (Helligkeit und Kontrast).
	 */
	private JPanel				tpWindow = new JPanel();


	/**
	 * Karteikarte fuer die 3D-Werkzeuge.
	 */
	private JPanel				tp3D = new JPanel();


	/**
	 * Karteikarte fuer die Zusatzfunktionen.
	 */
	private JPanel				tpExtras = new JPanel();


	// Nun folgt die Erzeugung der einzelnen Elmente der Layout-Karte


	/**
	 * Knopf zur Auswahl der Darstellung von nur einem Bild.
	 */
	JToggleButton					rb1Image = new JToggleButton();


	/**
	 * Knopf zur Auswahl der Darstellung von 4 Bildern gleichzeitig.
	 */
	JToggleButton					rb4Images = new JToggleButton();


	/**
	 * Knopfgruppe zur Gruppierung der Knoepfe rb1Image und rb4Images.
	 */
	private ButtonGroup		bgNumOfImages = new ButtonGroup();


	/**
	 * Knopf zur Darstellung des k-Raums des selektierten Bildes.
	 */
	private JButton				pbKSpace = new JButton();


	/**
	 * Knopf zur Darstellung des Histogramms des selektierten Bildes.
	 */
	private JButton				pbHistogram = new JButton();


	/**
	 * Knopf zur Darstellung der Bildbeschriftungen (Nummern, TR, TI, u.a.).
	 */
	JToggleButton					tbImageText = new JToggleButton();


	/**
	 * Knopf zum Oeffnen des k-Raum-Manipulators.
	 */
	private JButton				pbKSpaceManip = new JButton();


	// Nun folgt die Erzeugung der einzelnen Elemente der Werkzeug-Karte


	/**
	 * Knopf zur Auswahl des Standard-Mauszeigers.
	 */
	private JToggleButton rbPointer = new JToggleButton();


	/**
	 * Knopf zur Auswahl der Lupe.
	 */
	JToggleButton					rbZoom = new JToggleButton();


	/**
	 * Knopf zur Auswahl des Messwerkzeugs (Distanz).
	 */
	JToggleButton					rbDistance = new JToggleButton();


	/**
	 * Knopf zur Auswahl des Winkelmesswerkzeugs.
	 */
	JToggleButton					rbAngel = new JToggleButton();


	/**
	 * Knopf zur Auswahl des Grauwert-Messwerkzeugs.
	 */
	JToggleButton					rbGrayValue = new JToggleButton();


	/**
	 * Knopf zur Auswahl des Spiegel-Werkzeugs.
	 */
	private JButton				pbMirror = new JButton();


	/**
	 * Knopf zur Auswahl des Rotations-Werkzeugs.
	 */
	private JButton				pbRotate = new JButton();


	/**
	 * Knopf zum Invertieren des selektierten Bildes.
	 */
	private JButton				pbInvert = new JButton();


	/**
	 * Knopf zum Loeschen des selektierten Bildes.
	 */
	private JButton				pbDelete = new JButton();

	// Nun folgt die Erstellung der einzelnen Elemente der Fensterung-Karteikarte
	// Panel zur Gruppierung der Funktionselemente fuer die Helligkeitsveraenderung


	/**
	 * Schieberegler fuer die Helligkeit (Zentrum des Fensters).
	 */
	protected SliderPanel pCenter;


	/**
	 * Schieberegler fuer den Kontrast (Breite des Fensters).
	 */
	protected SliderPanel pWindow;


	/**
	 * Knopf zum Zuruecksetzen der Fensterung auf Center=2048 und Window=4096.
	 */
	private JButton				pbResetWindowing = new JButton();


	/**
	 * Knopf zur Durchfuehrung einer optimalen Fensterung.
	 */
	private JButton				pbOptWindowing = new JButton();

	// Nun folgt die Erstellung der einzelnen Elemente der 3D-Karteikarte


	/**
	 * Knopf zum Oeffnen des Animations-Fensters.
	 */
	private JButton				pbAnimation = new JButton();


	/**
	 * Knopf zur Berechnung eines MIP-Bildes.
	 */
	private JButton				pbCalcMIP = new JButton();


	/**
	 * Knopf zur Berechnung einer 90-Grad-Projektion (1. Variante).
	 */
	private JButton				pbProjection1 = new JButton();


	/**
	 * Knopf zur Berechnung einer 90-Grad-Projektion (2. Variante).
	 */
	private JButton				pbProjection2 = new JButton();

	// Nun folgt die Erzeugung der einzelnen Elemente der Extras-Karteikarte


	/**
	 * Schieberegler zur Auswahl des Simulationszeitfaktors (0-100%).
	 */
	private SliderPanel		pTimeFactor;


	/**
	 * Panel zur Gruppierung der Elemente, die die Auswahl der Bildplazierung
	 * fuer neu erzeugte Bilder ermoeglicht.
	 */
	private JPanel				pNextImageAt = new JPanel();


	/**
	 * Beschriftung fuer die Auswahlelemente fuer die Bildplatzierung.
	 */
	private JLabel				lNextImageAt = new JLabel();


	/**
	 * Knopf zur Erzeugung des naechsten Bildes an der selektierten Position.
	 */
	public JRadioButton		rbNextImageAtSelection = new JRadioButton();


	/**
	 * Knopf zur Erzeugung des naechsten Bildes an der naechsten freien Position.
	 */
	private JRadioButton	rbNextImageAtFreePlace = new JRadioButton();


	/**
	 * Gruppe fuer die Knoepfe zur Auswahl der Bildplazierung.
	 */
	private ButtonGroup		bgPlaceNextImage = new ButtonGroup();


	/**
	 * CheckBox zum Aktivieren eines neu erzeugten Bildes
	 */
	public JCheckBox			chbActivateNewImg = new JCheckBox();

	// ***************************************************************************
	// ******** Ab hier beginnt die Deklaration von NICHT-GUI-ELEMENTEN **********
	// ***************************************************************************


	/**
	 * Aktuelle x-Mausposition auf der Zeichenflaeche.
	 */
	private int						curr_x;


	/**
	 * Aktuelle y-Mausposition auf der Zeichenflaeche.
	 */
	private int						curr_y;


	/**
	 * Letze Mausklick-x-Koordinate.
	 */
	private int						lastMouseClickX;


	/**
	 * Letze Mausklick-y-Koordinate.
	 */
	private int						lastMouseClickY;


	/**
	 * Anzahl der Mausklicks (benoetigt zur Abstands- und Winkelmessung).
	 */
	private int						mouseClicks = 0;


	/**
	 * Zeitpunkt des letzten Mausklicks im Hauptfenster.
	 */
	private long					lastTime;


	/**
	 * Fensterungszentrum-Einstellungen beim letzte Mausklick.
	 */
	private int						lastCenter;


	/**
	 * Fensterungsbreite-Einstellungen beim letzte Mausklick.
	 */
	private int						lastWindow;


	/**
	 * Merker fuer nichtgespeicherte Aenderungen.
	 */
	private boolean				unsavedChanges = false;


	/**
	 * Hashtabelle für die Standardeinstellungen
	 */
	private Properties		preferences = new Properties();


	/**
	 * Gibt an, ob von einer Applickation (true) oder einem Applet gestartet wurde.
	 */
	private boolean				isApplication = true;


	// *********************************************************************
	// *************** Konstruktoren ***************************************
	// *********************************************************************


	/**
	 * Standardkonstruktor. Er initialisiert die Bedienelemente des DICOM-Viewers.
	 */
	public ViewerFrame() {
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		try {

			// Initialisieren der GUI-Elemente
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		loadPreferences();
	}		// Konstruktor ViewerFrame()

	// *********************************************************************
	// *************** Methoden ********************************************
	// *********************************************************************


	/**
	 * Die Methode setzt das aktuelle Verzeichnis auf den zuletzt gewählten Pfad.
	 * @param ia true, wenn von einer Applikation gestartet wurde, false bei Applet.
	 */
	public void setAppType(boolean ia) {
		isApplication = ia;
	} 


	/**
	 * Die Methode richtet die Bedienelemente ein und stellt diese dar.
	 */
	private void jbInit() throws Exception {

		// Laden des 'Loeschen'-Iocns
		iAnimation = new ImageIcon(this.getClass().getResource("resources/animation.gif"));

		// Laden des 'Delete'-Iocns
		iDelete = new ImageIcon(this.getClass().getResource("resources/delete.gif"));

		// Laden des 'oeffnen'-Icons
		iOpen = new ImageIcon(this.getClass().getResource("resources/openfile.gif"));

		// Laden des 'oeffnen'-Icons
		// Nicht mehr notwendig: tha 2000.8.20
		// iOpenSeries = new ImageIcon(this.getClass().getResource("resources/openseries.gif"));

		// Laden des 'Speichern'-Icons
		iSave = new ImageIcon(this.getClass().getResource("resources/save.gif"));

		// Laden des 'Drucken'-Icons
		iPrint = new ImageIcon(this.getClass().getResource("resources/print.gif"));

		// Laden des "Referenzbild"-Icons
		iRefIma = new ImageIcon(this.getClass().getResource("resources/refima.gif"));

		// Laden des "1 Bild"-Icons
		i1Image = new ImageIcon(this.getClass().getResource("resources/1image.gif"));

		// Laden des "4 Bilder"-Icons
		i4Images = new ImageIcon(this.getClass().getResource("resources/4images.gif"));

		// Laden des "4 Bilder"-Icons
		iInfoText = new ImageIcon(this.getClass().getResource("resources/imageinfo.gif"));

		// Laden des "4 Bilder"-Icons
		iHistogram = new ImageIcon(this.getClass().getResource("resources/histogramm.gif"));

		// Laden des "4 Bilder"-Icons
		iKSpace = new ImageIcon(this.getClass().getResource("resources/k_raum.gif"));

		// Laden des k-Raum Manipulator Icons
		iKSpaceMan = new ImageIcon(this.getClass().getResource("resources/k_raum_man.gif"));

		// Laden des Winkel-icons
		iAngel = new ImageIcon(this.getClass().getResource("resources/winkel.gif"));

		// Laden des Mauszeiger-Icons
		iPointer = new ImageIcon(this.getClass().getResource("resources/picktool.gif"));

		// Laden des Lupen-Icons
		iZoom = new ImageIcon(this.getClass().getResource("resources/zoomtool.gif"));

		// Laden des Rotieren-Icons
		iRotate = new ImageIcon(this.getClass().getResource("resources/drehen.gif"));

		// Laden des Spiegel-icons
		iMirror = new ImageIcon(this.getClass().getResource("resources/spiegeln.gif"));

		// Laden des Invertierungs-Icons
		iInvert = new ImageIcon(this.getClass().getResource("resources/invertieren.gif"));

		// Laden des Grauwert-Icons
		iGrayValue = new ImageIcon(this.getClass().getResource("resources/grauwert.gif"));

		// Laden des Lineal-Icons
		iDistance = new ImageIcon(this.getClass().getResource("resources/abstand.gif"));

		// Laden des 1. projektionsicons
		iProjection1 = new ImageIcon(this.getClass().getResource("resources/projektion1.gif"));

		// Laden des 2. projektionsicons
		iProjection2 = new ImageIcon(this.getClass().getResource("resources/projektion2.gif"));


		// Layoutmanager des Hauptfensters auf null setzen. Damit sind alle
		// Elemente frei platzierbar und unterliegn nicht den Eigenwilligkeiten
		// eines Layoutmanagers
		this.getContentPane().setLayout(null);

		// Groesse des Hauptfensters auf 600*800 Punkte festlegen. Damit ist es
		// auf jedem SuperVGA-Monitor darstellbar
		this.setSize(new Dimension(800, 585));

		// Die Groesse des Fensters soll nicht veraendert werden koennen
		this.setResizable(false);

		// Hintergrundfarbe setzen
		this.setBackground(new Color(204, 204, 204));

		// Titelzeile setzen
		this.setTitle("DICOM-Viewer");

		// Menueleiste mit seinen Menues einrichten
		menuBar1.add(menuFile);
		menuBar1.add(menuEdit);
		menuBar1.add(menuTools);

		// th 2002.10.12
		// menuBar1.add(menuHelp);
		this.setJMenuBar(menuBar1);

		// Dateimenue mit seinen Menuepunkten einrichten
		menuFile.setText("Datei");

		// Menueeintrag 'Bild Oeffnen' einrichten
		menuFileImageOpen.setText("Bild Oeffnen...");
		menuFileImageOpen.addActionListener(new ActionListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void actionPerformed(ActionEvent e) {
				fileImageOpen_actionPerformed();
			} 

		});

		// Menueeintrag 'Serie Oeffnen' einrichten
		// Nicht mehr notwendig: tha 2000.8.20

		/*
		 * menuFileSeriesOpen.setText("Serie Oeffnen...");
		 * menuFileSeriesOpen.addActionListener(new ActionListener() {
		 * public void actionPerformed(ActionEvent e) {
		 * fileSeriesOpen_actionPerformed();
		 * }
		 * });
		 */

		// Menueeintrag 'Speichern einrichten'
		menuFileSave.setText("Speichern unter...");
		menuFileSave.addActionListener(new ActionListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void actionPerformed(ActionEvent e) {
				fileSave_actionPerformed();
			} 

		});

		// Menueeintrag 'Drucken' einrichten
		menuFilePrint.setText("Drucken...");
		menuFilePrint.addActionListener(new ActionListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void actionPerformed(ActionEvent e) {
				filePrint_actionPerformed();
			} 

		});

		// Menueeintrag 'Beenden' einrichten
		menuFileExit.setText("Beenden");
		menuFileExit.addActionListener(new ActionListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void actionPerformed(ActionEvent e) {
				fileExit_actionPerformed();
			} 

		});

		// Die einzelnen Menueeintraege des Dateimenue hinzufuegen
		menuFile.add(menuFileImageOpen);

		// Nicht mehr notwendig: tha 2000.8.20
		// menuFile.add(menuFileSeriesOpen);
		menuFile.add(menuFileSave);
		menuFile.add(menuFilePrint);
		menuFile.add(menuFileExit);

		// Bearbeiten-Menue mit seinen Menuepunkten einrichten
		menuEdit.setText("Bearbeiten");

		menuEditClear.setText("Selektiertes Bild loeschen");
		menuEditClear.addActionListener(new ActionListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void actionPerformed(ActionEvent e) {
				editClear_actionPerformed();
			} 

		});

		menuEditClearAll.setText("Alle Bilder loeschen");
		menuEditClearAll.addActionListener(new ActionListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void actionPerformed(ActionEvent e) {
				editClearAll_actionPerformed();
			} 

		});

		// Die einzelnen Menueeintraege hinzufuegen
		menuEdit.add(menuEditClear);
		menuEdit.add(menuEditClearAll);

		// Toolsmenue mit seinen Menuepunkten einrichten
		menuTools.setText("Tools");
		menuToolsAbout.setText("About...");
		menuToolsAbout.addActionListener(new ActionListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void actionPerformed(ActionEvent e) {
				helpAbout_actionPerformed();
			} 

		});

		// Die einzelnen Menueeintraege hinzufuegen
		menuTools.add(menuToolsAbout);

		// Statusleiste formatieren und positionieren
		statusBar.setText("Bereit.");
		statusBar.setForeground(Color.black);
		statusBar.setFont(new Font("Dialog", 0, 12));
		statusBar.setBounds(new Rectangle(1, 516, 512, 15));
		this.getContentPane().add(statusBar, null);

		// Fortschrittsanzeige enrichten
		prbarProgress.setToolTipText("Fortschrittsanzeige");
		prbarProgress.setBounds(new Rectangle(675, 516, 112, 15));
		prbarProgress.setMinimum(0);
		prbarProgress.setMaximum(100);
		prbarProgress.setValue(0);
		this.getContentPane().add(prbarProgress, null);

		// Canvas formatieren und positionieren
		pCanvas.setMaximumSize(new Dimension(512, 512));
		pCanvas.setPreferredSize(new Dimension(512, 512));
		pCanvas.setBackground(Color.black);
		pCanvas.setMinimumSize(new Dimension(512, 512));
		pCanvas.setBounds(new Rectangle(1, 0, 512, 512));
		pCanvas.setLayout(null);
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
			public void mouseExited(MouseEvent e) {
				pCanvas_mouseExited();
			} 


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void mouseClicked(MouseEvent e) {
				pCanvas_mouseClicked(e);
			} 


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
			public void mouseMoved(MouseEvent e) {
				pCanvas_mouseMoved(e);
			} 


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

		// Bildlaufleiste zum Canvas erzeugen, Positionieren und formatieren
		sbVerScrollbar = pCanvas.createVerticalScrollBar();
		sbVerScrollbar.setBounds(new Rectangle(513, 1, 20, 512));
		sbVerScrollbar.setBorder(BorderFactory.createEtchedBorder());
		sbVerScrollbar.setMinimum(0);
		sbVerScrollbar.setMaximum(1024);
		sbVerScrollbar.setVisibleAmount(511);
		sbVerScrollbar.setBlockIncrement(512);
		sbVerScrollbar.setUnitIncrement(512);
		this.getContentPane().add(sbVerScrollbar, null);
		sbVerScrollbar.addAdjustmentListener(new java.awt.event.AdjustmentListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void adjustmentValueChanged(AdjustmentEvent e) {
				sbVerScrollbar_adjustmentValueChanged();
			} 

		});

		// Toolbar-Panel positionieren und die Groesse einrichten
		pToolbar.setBounds(new Rectangle(535, 0, 255, 514));
		pToolbar.setLayout(null);
		pToolbar.add(tpMain, null);
		this.getContentPane().add(pToolbar, null);

		// 'Referenzserie'-Knopf formatieren und positionieren
		pbReferenceData.setMargin(new Insets(2, 2, 2, 2));
		pbReferenceData.setIcon(iRefIma);
		pbReferenceData.setBounds(new Rectangle(3, 12, 27, 26));
		pbReferenceData.setToolTipText("Referenzserie laden");
		pbReferenceData.setEnabled(true);
		pToolbar.add(pbReferenceData, null);
		pbReferenceData.addActionListener(new java.awt.event.ActionListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void actionPerformed(ActionEvent e) {
				pbReferenceData_actionPerformed();
			} 

		});

		// 'oeffnen'-Knopf formatieren und positionieren
		pbLoad.setMargin(new Insets(2, 2, 2, 2));
		pbLoad.setIcon(iOpen);
		pbLoad.setBounds(new Rectangle(40, 12, 27, 27));
		pbLoad.setToolTipText("DICOM-Bild laden");
		pToolbar.add(pbLoad, null);
		pbLoad.addActionListener(new java.awt.event.ActionListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void actionPerformed(ActionEvent e) {
				pbLoad_actionPerformed();
			} 

		});

		// 'oeffne Serie'-Knopf formatieren und positionieren
		// Nicht mehr notwendig: tha 2000.8.20

		/*
		 * pbLoadSeries.setMargin(new Insets(2, 2, 2, 2));
		 * pbLoadSeries.setIcon(iOpenSeries);
		 * pbLoadSeries.setBounds(new Rectangle(77, 12, 27, 27));
		 * pbLoadSeries.setToolTipText("DICOM-Bildserie laden");
		 * pToolbar.add(pbLoadSeries, null);
		 * pbLoadSeries.addActionListener(new java.awt.event.ActionListener() {
		 * public void actionPerformed(ActionEvent e) {
		 * pbLoadSeries_actionPerformed();
		 * }
		 * });
		 */

		// 'Speichern'-Knopf formatieren und positinieren
		pbSave.setMargin(new Insets(2, 2, 2, 2));
		pbSave.setBounds(new Rectangle(77, 12, 27, 27));
		pbSave.setToolTipText("Selektiertes Bild speichern");
		pbSave.setIcon(iSave);
		pToolbar.add(pbSave, null);
		pbSave.addActionListener(new java.awt.event.ActionListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void actionPerformed(ActionEvent e) {
				pbSave_actionPerformed();
			} 

		});

		// 'Drucken'-Knopf formatieren und poitionieren
		pbPrint.setMargin(new Insets(2, 2, 2, 2));
		pbPrint.setBounds(new Rectangle(114, 12, 27, 27));
		pbPrint.setIcon(iPrint);
		pbPrint.setToolTipText("Selektiertes Bild drucken");
		pToolbar.add(pbPrint, null);
		pbPrint.addActionListener(new java.awt.event.ActionListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void actionPerformed(ActionEvent e) {
				pbPrint_actionPerformed();
			} 

		});


		// Karteireiter-HauptPanel positionieren
		tpMain.setBounds(new Rectangle(3, 45, 250, 185));

		// Ansicht-Karteikarte einrichten (Layout)
		tpLayout.setLayout(null);
		tpMain.add(tpLayout, "Ansicht");

		// Knopf zur Anzeige des k-Raum Manipulationstools einrichten
		pbKSpaceManip.setIcon(iKSpaceMan);
		pbKSpaceManip.setToolTipText("k-Raum Manipulationswerkzeug");
		pbKSpaceManip.setBounds(new Rectangle(190, 10, 27, 27));
		pbKSpaceManip.addActionListener(new java.awt.event.ActionListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void actionPerformed(ActionEvent e) {
				pbKSpaceManip_actionPerformed();
			} 

		});
		tpLayout.add(pbKSpaceManip, null);

		// Knopf zur k-Raum-Anzeige einrichten
		// pbKSpace.setText("k-Raum");
		pbKSpace.setIcon(iKSpace);
		pbKSpace.setToolTipText("Transformiertes Bild im k-Raum anzeigen");
		pbKSpace.setBounds(new Rectangle(153, 10, 27, 27));
		pbKSpace.addActionListener(new java.awt.event.ActionListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void actionPerformed(ActionEvent e) {
				pbKSpace_actionPerformed();
			} 

		});
		tpLayout.add(pbKSpace, null);

		// Knopf zur Histogrammanzeige einrichten
		// pbHistogram.setText("Histogramm");
		pbHistogram.setIcon(iHistogram);
		pbHistogram.setToolTipText("Histogramm des selektierten Bildes anzeigen");
		pbHistogram.setBounds(new Rectangle(116, 10, 27, 27));
		pbHistogram.addActionListener(new java.awt.event.ActionListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void actionPerformed(ActionEvent e) {
				pbHistogram_actionPerformed();
			} 

		});
		tpLayout.add(pbHistogram, null);

		// Knopf zur Anzeige der Bildbeschriftungen einrichten
		// tbImageText.setText("Beschriftung");
		tbImageText.setIcon(iInfoText);
		tbImageText.setToolTipText("Bildbeschriftung ein-/ausblenden");
		tbImageText.setBounds(new Rectangle(79, 10, 27, 27));
		tbImageText.addMouseListener(new java.awt.event.MouseAdapter() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void mouseClicked(MouseEvent e) {
				tbImageText_mouseClicked();
			} 

		});
		tpLayout.add(tbImageText, null);

		// Button zur Darstellung nur eines Bildes einrichten
		rb1Image.setMargin(new Insets(2, 2, 2, 2));
		rb1Image.setIcon(i1Image);
		rb1Image.setSelected(true);
		rb1Image.setToolTipText("1 Bild anzeigen");
		rb1Image.setBounds(new Rectangle(5, 10, 27, 27));
		tpLayout.add(rb1Image, null);
		rb1Image.addActionListener(new java.awt.event.ActionListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void actionPerformed(ActionEvent e) {
				rb1Image_actionPerformed();
			} 

		});

		// Button zur gleichzeitigen Darstellung von 4 Bildern einrichten
		rb4Images.setMargin(new Insets(2, 2, 2, 2));
		rb4Images.setIcon(i4Images);
		rb4Images.setToolTipText("4 Bilder anzeigen");
		rb4Images.setBounds(new Rectangle(42, 10, 27, 27));
		tpLayout.add(rb4Images, null);
		rb4Images.addActionListener(new java.awt.event.ActionListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void actionPerformed(ActionEvent e) {
				rb4Images_actionPerformed();
			} 

		});

		// Die beiden Knoepfe zur Darstellung von 1 bzw. 4 Bildern so
		// gruppieren, dass immer nur einer gedrueclkt sein kann
		bgNumOfImages.add(rb1Image);
		bgNumOfImages.add(rb4Images);

		// Werkzeug-Karteikarte einrichten
		tpTools.setLayout(null);
		tpMain.add(tpTools, "Tools");

		// Knopf zur Auswahl des Mauszeigerwerkezugs einrichten
		rbPointer.setIcon(iPointer);
		rbPointer.setBounds(5, 10, 27, 27);
		rbPointer.setSelected(true);
		rbPointer.setToolTipText("Einfacher Mauszeiger und Fensterungswerkzeug.");
		tpTools.add(rbPointer, null);

		// Knopf zur Auswahl des Vergroesserungswerkzeugs einrichten
		rbZoom.setIcon(iZoom);
		rbZoom.setBounds(42, 10, 27, 27);
		rbZoom.setToolTipText("Lupe");
		tpTools.add(rbZoom, null);

		// Knopf zur Auswahl des Abstandsmessungswerkzeugs einrichten
		rbDistance.setIcon(iDistance);
		rbDistance.setBounds(79, 10, 27, 27);
		rbDistance.setToolTipText("Distanzmessung");
		tpTools.add(rbDistance, null);

		// Knopf zur Auswahl des Winkelmessungswerkzeugs einrichten
		rbAngel.setIcon(iAngel);
		rbAngel.setBounds(116, 10, 27, 27);
		rbAngel.setToolTipText("Winkelmessung");
		tpTools.add(rbAngel, null);

		// Knopf zur Auswahl des Grauwertmessungswerkzeugs einrichten
		rbGrayValue.setIcon(iGrayValue);
		rbGrayValue.setBounds(153, 10, 27, 27);
		rbGrayValue.setToolTipText("Grauwerterfassung");
		tpTools.add(rbGrayValue, null);

		// Knopf zur Auswahl des Spiegel-Werkzeugs einrichten
		pbMirror.setIcon(iMirror);
		pbMirror.setBounds(5, 47, 27, 27);
		pbMirror.setToolTipText("Horizontales Spiegeln");
		tpTools.add(pbMirror, null);
		pbMirror.addActionListener(new java.awt.event.ActionListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void actionPerformed(ActionEvent e) {
				pbMirror_actionPerformed();
			} 

		});

		// Knopf zur Auswahl des Rotier-Werkzeugs einrichten
		pbRotate.setIcon(iRotate);
		pbRotate.setBounds(42, 47, 27, 27);
		pbRotate.setToolTipText("90° im Urzeigersinn drehen");
		tpTools.add(pbRotate, null);
		pbRotate.addActionListener(new java.awt.event.ActionListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void actionPerformed(ActionEvent e) {
				pbRotate_actionPerformed();
			} 

		});

		// Knopf zum Invertieren des Bildes einrichten
		pbInvert.setIcon(iInvert);
		pbInvert.setBounds(79, 47, 27, 27);
		pbInvert.setToolTipText("Invertieren");
		tpTools.add(pbInvert, null);
		pbInvert.addActionListener(new java.awt.event.ActionListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void actionPerformed(ActionEvent e) {
				pbInvert_actionPerformed();
			} 

		});

		// Knopf zum Loeschen des selektierten Bildes einrichten
		pbDelete.setIcon(iDelete);
		pbDelete.setBounds(5, 84, 27, 27);
		pbDelete.setToolTipText("Selektiertes Bild loeschen");
		tpTools.add(pbDelete, null);
		pbDelete.addActionListener(new java.awt.event.ActionListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void actionPerformed(ActionEvent e) {
				pbDelete_actionPerformed();
			} 

		});


		// Die Werkzeugknoepfe so gruppieren, dass immer nur einer
		// ausgewaehlt sein kann
		ButtonGroup bgTools = new ButtonGroup();

		bgTools.add(rbPointer);
		bgTools.add(rbZoom);
		bgTools.add(rbDistance);
		bgTools.add(rbAngel);
		bgTools.add(rbGrayValue);

		// Fenstern-Karteikarte einrichten
		tpWindow.setLayout(null);
		tpMain.add(tpWindow, "Fenstern");

		// Helligkeitseingabefeld mit Slider hinzufuegen
		pCenter = new SliderPanel("Zentrum:", 0, 4096, "");
		pCenter.setBounds(new Rectangle(2, 5, 185, 70));
		pCenter.setToolTip("Einstellung der Bildhelligkeit");
		pCenter.setToolTipText("Mit diesem Wert wird die Helligkeit des Bildes beeinflusst.");
		pCenter.setTextRange(0, 4095);
		pCenter.setTickSpacing(1024, 0);
		pCenter.fillPanel();
		pCenter.setSliderMaximum(4095);
		tpWindow.add(pCenter);
		pCenter.setValue(2048);
		pCenter.getTextFieldReference().addCaretListener(new CaretListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void caretUpdate(CaretEvent e) {
				updateCenter();
				pCanvas.repaint();
			} 

		});

		// Helligkeitseingabefeld mit Slider hinzufuegen
		pWindow = new SliderPanel("Breite:", 0, 4096, "");
		pWindow.setBounds(new Rectangle(2, 80, 185, 70));
		pWindow.setToolTip("Einstellung der Bildhelligkeit");
		pWindow.setTextRange(1, 4096);
		pWindow.setTickSpacing(1024, 0);
		pWindow.setToolTipText("Mit diesem Wert wird der Kontrast des Bildes beeinflusst.");
		pWindow.fillPanel();
		pWindow.setSliderMinimum(1);
		tpWindow.add(pWindow);
		pWindow.setValue(4096);
		pWindow.getTextFieldReference().addCaretListener(new CaretListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void caretUpdate(CaretEvent e) {
				updateWindow();
				pCanvas.repaint();
			} 

		});


		// Button fuer das Zuruecksetzen der Fensterung einrichten
		pbResetWindowing.setBounds(193, 5, 45, 70);
		pbResetWindowing.setText("R");
		pbResetWindowing.setToolTipText("Standardfensterung (2048/4096)");
		tpWindow.add(pbResetWindowing);
		pbResetWindowing.addActionListener(new java.awt.event.ActionListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void actionPerformed(ActionEvent e) {
				pWindow.setValue(4096);
				pCenter.setValue(2048);
				updateCW();
				pCanvas.repaint();
			} 

		});

		// Button fuer optimale Fensterung einrichten
		pbOptWindowing.setBounds(193, 80, 45, 70);
		pbOptWindowing.setText("O");
		pbOptWindowing.setToolTipText("Optimale Fensterung");
		tpWindow.add(pbOptWindowing);
		pbOptWindowing.addActionListener(new java.awt.event.ActionListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void actionPerformed(ActionEvent e) {
				pbOptWindowing_actionPerformed();
			} 

		});

		// 3D-Karteikarte einrichten
		tp3D.setLayout(null);
		tpMain.add(tp3D, "3D");

		// Bedienelemente der 3D-Karteikarte einrichten
		// einrichten des Slider-Panels fuer die Geschwindigkeit
		pbAnimation.setMargin(new Insets(2, 2, 2, 2));
		pbAnimation.setIcon(iAnimation);
		pbAnimation.setBounds(new Rectangle(5, 10, 27, 27));
		pbAnimation.setToolTipText("oeffnet ein Animationsfenster");
		tp3D.add(pbAnimation, null);
		pbAnimation.addActionListener(new java.awt.event.ActionListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void actionPerformed(ActionEvent e) {
				pbAnimation_actionPerformed();
			} 

		});

		// / Knopf zur Berechnung eines MIP-Bildes einrichten
		pbCalcMIP.setMargin(new Insets(2, 2, 2, 2));
		pbCalcMIP.setText("M");
		pbCalcMIP.setBounds(new Rectangle(42, 10, 27, 27));
		pbCalcMIP.setToolTipText("MIP-Bild berechnen");
		tp3D.add(pbCalcMIP, null);
		pbCalcMIP.addActionListener(new java.awt.event.ActionListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void actionPerformed(ActionEvent e) {
				pbCalcMIP_actionPerformed();
			} 

		});

		// Knopf zur Berechnung einer 90-Grad-Projektion einrichten
		pbProjection1.setMargin(new Insets(2, 2, 2, 2));
		pbProjection1.setIcon(iProjection1);
		pbProjection1.setBounds(new Rectangle(79, 10, 27, 27));
		pbProjection1.setToolTipText("90°-Projektion");
		tp3D.add(pbProjection1, null);
		pbProjection1.addActionListener(new java.awt.event.ActionListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void actionPerformed(ActionEvent e) {
				pbProjection1_actionPerformed();
			} 

		});

		// Knopf zur Berechnung der zweiten 90-Grad-Projektion einrichten
		pbProjection2.setMargin(new Insets(2, 2, 2, 2));
		pbProjection2.setIcon(iProjection2);
		pbProjection2.setBounds(new Rectangle(116, 10, 27, 27));
		pbProjection2.setToolTipText("90°-Projektion");
		tp3D.add(pbProjection2, null);
		pbProjection2.addActionListener(new java.awt.event.ActionListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void actionPerformed(ActionEvent e) {
				pbProjection2_actionPerformed();
			} 

		});

		// Extras-Karteikarte einrichten
		tpExtras.setLayout(null);
		tpMain.add(tpExtras, "Extras");

		// Elemente zur Auswahl der Position des naechsten erzeugten Bildes anlegen
		pNextImageAt.setBounds(2, 2, 240, 44);
		pNextImageAt.setLayout(null);
		pNextImageAt.setBorder(BorderFactory.createEtchedBorder());
		tpExtras.add(pNextImageAt, null);
		lNextImageAt.setText("Naechstes Bild positionieren:");
		lNextImageAt.setBounds(2, 0, 240, 20);
		pNextImageAt.add(lNextImageAt);
		rbNextImageAtSelection.setText("Selektion");
		rbNextImageAtSelection.setBounds(2, 22, 125, 20);
		pNextImageAt.add(rbNextImageAtSelection, null);
		rbNextImageAtFreePlace.setText("Freie Stelle");
		rbNextImageAtFreePlace.setBounds(130, 22, 105, 20);
		pNextImageAt.add(rbNextImageAtFreePlace, null);
		rbNextImageAtFreePlace.setSelected(true);
		bgPlaceNextImage.add(rbNextImageAtSelection);
		bgPlaceNextImage.add(rbNextImageAtFreePlace);

		// Checkbox zur Aktivierung eines neuen Bildes einrichten
		chbActivateNewImg.setText("Neues Bild aktivieren");
		chbActivateNewImg.setBounds(new Rectangle(2, 47, 145, 25));
		chbActivateNewImg.setSelected(true);
		tpExtras.add(chbActivateNewImg, null);

		// Positionsanzeige einrichten
		lPosition.setText("");
		lPosition.setForeground(Color.black);
		lPosition.setFont(new Font("Dialog", 0, 12));
		lPosition.setBounds(new Rectangle(590, 516, 95, 15));
		this.getContentPane().add(lPosition, null);
		lPositionText.setText("Position:");
		lPositionText.setForeground(Color.black);
		lPositionText.setFont(new Font("Dialog", 0, 12));
		lPositionText.setBounds(new Rectangle(538, 516, 50, 15));
		this.getContentPane().add(lPositionText, null);
	}		// Methode jbInit
 

	// *************************************************************************
	// *******************  Event-Methoden *************************************
	// *************************************************************************


	/**
	 * Die Methode wird aufgerufen, wenn der Knopf zum Loeschen des selktierten
	 * Bildes gedrueckt wurde. Es wird zunaecht ein Dialog aufgeblendet, der eine
	 * Sicherheitsabfrage darstellt und nach dessen Bestaetigung wird das
	 * selektierte Bild geloescht.
	 */
	private void editClear_actionPerformed() {

		// Auflenden eines Bestaetigungsdialogs und Auslesen des gedrueckten Knopfes
		int response;

		response = JOptionPane.showConfirmDialog(null, "Moechten Sie wirklich das\n" + "selektierte Bild loeschen?");

		// Wenn der Dialog bestaetigt wurde, wird das Bild aus dem Stack geloescht
		if (response == 0)	// Ok gedrueckt
		 {
			pCanvas.getImageStack().deletePictureAtPos(pCanvas.getActiveImage());

			// Scrollbereich aktualisieren und Canvas neu zeichnen
			pCanvas.updateScrollbar();
			pCanvas.repaint();
			this.setVisible(true);
		} 
	} 


	/**
	 * Die Methode wird aufgerufen, wenn der Knopf zum Loeschen aller
	 * Bilder gedrueckt wurde. Es wird zunaecht ein Dialog aufgeblendet, der eine
	 * Sicherheitsabfrage darstellt und nach dessen Bestaetigung werden alle
	 * Bilder geloescht.
	 */
	private void editClearAll_actionPerformed() {

		// Auflenden eines Bestaetigungsdialogs und Auslesen des gedrueckten Knopfes
		int response;

		response = JOptionPane.showConfirmDialog(null, "Moechten Sie wirklich alle\n" + "Bilder loeschen?");

		// Wenn der Dialog bestaetigt wurde, werden alle Bilder geloescht
		if (response == 0)	// Ok gedrueckt
		 {
			pCanvas.getImageStack().clearImageStack();
			pCanvas.updateScrollbar();
			pCanvas.repaint();
			this.setVisible(true);
		} 
	} 


	/**
	 * Die Methode implementiert die Fensterung des selektierten Bildes mittels
	 * Mausbewegung. Eine Bewegung der Maus in x-Richhtung veraendert das Zentrum,
	 * eine Bewegung in y-Richtung die Breite. Ausserdem wird fuer die Distanz-
	 * und Winkelmessung die aktuelle Mausposition in der Canvas-Klasse gesetzt.
	 * @param e Das Ereignis, das beim Bewegen der Maus mit gedrueckter Maustaste
	 * ausgeloest wird.
	 */
	private void pCanvas_mouseDragged(MouseEvent e) {

		if (rbPointer.isSelected()) {
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
					pCenter.setValue(lastCenter + difY);
					lastCenter += difY;
				} 
			} 

			if ((lastWindow + difX) <= 4096) {
				if ((lastWindow + difX) >= 1) {
					pWindow.setValue(lastWindow + difX);
					lastWindow += difX;
				} 
			} 

			lastMouseClickX = curX;
			lastMouseClickY = curY;
			lastTime = curWhen;

			// Aktualisieren der Fensterung des selektierten Bildes
			updateCW();

			// Neuzeichnen des Canvasinhalts
			pCanvas.repaint();
		}		// if (Mauszeiger als Tool selektiert)
 
		if (rbDistance.isSelected()) {
			pCanvas.setCursor(ZOOM_CURSOR);

			// Im folgenden wird die aktuelle Mausposition in der Canvas-Klasse gesetzt
			if (rb1Image.isSelected()) {
				pCanvas.setCurrentPos(e.getX(), e.getY());
			} else {
				int startImg = getImageMouseIsOn(lastMouseClickX, lastMouseClickY);
				int actImg = getImageMouseIsOn(e.getX(), e.getY());

				// Die Position wird allerdings nur gesetzt, wenn man innerhalb eines Bildes bleibt
				if (startImg == actImg) {
					pCanvas.setCurrentPos(e.getX(), e.getY());
				} 
			} 

			// Neuzeichnen
			pCanvas.repaint();
		}		// if (Distanzmessung als Tool selektiert)
 
		if (rbAngel.isSelected()) {
			pCanvas.setCursor(ZOOM_CURSOR);

			if (rb1Image.isSelected()) {
				pCanvas.setCurrentPos(e.getX(), e.getY());
			} else {
				int startImg = getImageMouseIsOn(lastMouseClickX, lastMouseClickY);
				int actImg = getImageMouseIsOn(e.getX(), e.getY());

				if (startImg == actImg) {
					pCanvas.setCurrentPos(e.getX(), e.getY());
				} 
			}		// if-else
 
			// Neuzeichnen
			pCanvas.repaint();
		}			// if (Winkelmessung als Tool selektiert)
 
	}				// pCanvas_mouseDragged(MouseEvent e)
 

	/**
	 * Die Methode wird aufgerufen, wenn die Aktion Hilfe | Info aus dem Menue
	 * aufgerufen wurde. Das Informationsfenster wird aufgeblendet.
	 */
	private void helpAbout_actionPerformed() {
		AboutBox	abox = new AboutBox();

		// th 2002.10.29
		// abox.setImage(this.getClass().getResource("resources/dv.jpg"));
		abox.setFileURL(this.getClass().getResource("resources/splash_de.htm"));
		Dimension ts = this.getSize();
		Dimension as = abox.getSize();

		abox.setLocation((ts.width - as.width) / 2 + getLocation().x, (ts.height - as.height) / 2 + getLocation().y);
		abox.show();
	} 


	/**
	 * Die Method wird aufgerufen, wenn der Schliessen-Knopf oben rechts am Fenster
	 * betaetigt wird. Die Methode muss ueberschrieben werden, damit das Programm bei
	 * einem System-Close beendet werden kann.
	 * @param e Das Ereignis beim Betaetigen des Schliessen-Knopfs.
	 */
	protected void processWindowEvent(WindowEvent e) {

		// super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			this.savePreferences();

			// Aufruf der gleichen Methode wie bei Aktion Datei | Beenden
			fileExit_actionPerformed();
		}		// if
 	 }		// processWindowEvent(WindowEvent e)
 

	/**
	 * Die Methode aktualisiert die Center- und Window-Einstellung in der
	 * selektierten ImagePlus-Klasse.
	 */
	private void updateCW() {
		ImagePlus ip = null;

		// Nachschauen, welches Bild selektiert ist
		ip = getSelectedImage();

		// Fenstereinstellung im Bild Speichern; dabei wird das neugefensterte
		// Intensitaetsbild berechnet und dargestellt
		if (ip != null) {
			try {
				int c = pCenter.getValue();
				int w = pWindow.getValue();

				ip.setCW(c, w);
			} catch (Exception err) {}
		} 
	}		// Methode updateCW()
 

	/**
	 * Die Methode aktualisiert die Center-Einstellung in der
	 * selektierten ImagePlus-Klasse.
	 */
	private void updateCenter() {
		ImagePlus ip = null;

		// Nachschauen, welches Bild selektiert ist
		ip = getSelectedImage();

		// Fenstereinstellung im Bild Speichern; dabei wird das neugefensterte
		// Intensitaetsbild berechnet und dargestellt
		if (ip != null) {
			try {
				int c = pCenter.getValue();

				ip.setCenter(c);
			} catch (Exception err) {}
		} 
	}		// Methode updateCenter()
 

	/**
	 * Die Methode aktualisiert die Window-Einstellung in der
	 * selektierten ImagePlus-Klasse.
	 */
	private void updateWindow() {
		ImagePlus ip = null;

		// Nachschauen, welches Bild selektiert ist
		ip = getSelectedImage();

		// Fenstereinstellung im Bild Speichern; dabei wird das neugefensterte
		// Intensitaetsbild berechnet und dargestellt
		if (ip != null) {
			try {
				int w = pWindow.getValue();

				ip.setWindow(w);
			} catch (Exception err) {}
		} 
	}		// Methode updateWindow()
 

	/**
	 * Die Methode blendet einen Dateiauswahldiaolg auf. Nach Auswahl einer Datei
	 * aus der Liste, wird die Bildserie, die zu dem selektierten Bild gehoert
	 * geladen und auf der Zeichenflaeche dargestellt.
	 * @author Thomas Hacklaender
	 * @version 2000.8.20
	 */
	private void pbLoad_actionPerformed() {
		int							diaRes;
		GeneralImageIOD gi = null;
		ImportData			imda;
		ImportPanel			imPan;
		InputStream			pfs;
		SeriesLoader		sLoader;

		// Datenblock zur Kommunikation zwischen Plugin und Dialogbox generieren
		imda = new ImportData();

		// Versucht einen Property File zu finden
		pfs = Util.getPropertyFileStream("");
		if (pfs != null) {
			imda.getProperties(pfs);
		} 

		// ImportPanel mit zugehoeriger Datenstruktur generieren
		imPan = new ImportPanel();
		imPan.initWithDataBlock(imda);
		imPan.copyrightL.setText("(C) 2000 T. Hacklaender unter der GPL Lizenz. Dcm Ver.: " + DcmUID.DCM_VERSION);

		// Dialogbox zeigen
		diaRes = JOptionPane.showConfirmDialog(null, imPan, "DICOM Dateien einlesen", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null);
		if (diaRes != JOptionPane.OK_OPTION) {

			// "Cancel" gedrueckt
			return;
		} 

		imPan.updateDataBlock();

		gi = imda.theGI;
		if (gi == null) {

			// Kein DICOM Bild ausgwaehlt
			return;
		} 

		// Bilder in eigenem Thread laden
		sLoader = new SeriesLoader(this, gi, imda);
		sLoader.start();
	} 


	/**
	 * Die Methode wird aufgerufen, wenn der 'Speichern'-Knopf gedrueckt wurde oder
	 * die Aktion Datei | Speichern aufgerufen wurde.
	 * @author Thomas Hacklaender
	 * @version 2000.8.20
	 */
	private void pbSave_actionPerformed() {
		ImagePlus						currentImage;
		short[]							dcmPixels;
		DcmDataObject				ddo;
		int									diaRes;
		ExportData					exda;
		ExportPanel					exPan;
		File								f;
		InputStream					pfs;
		String							pn;
		SecondaryCaptureIOD scIOD;
		int									size;

		scIOD = new SecondaryCaptureIOD();

		currentImage = getSelectedImage();

		// Setzen des DcmDataObject mit den Zusatzinformationen
		scIOD.setPublicFields(currentImage.getDDO());

		// Besorgen der Pixeldaten und setzen in einem DcmSecondCaptFile-Object
		dcmPixels = currentImage.getImage12BitShort();
		size = (int) (Math.sqrt(dcmPixels.length) + 0.5);
		scIOD.set16UBitGrayImage(dcmPixels, size, size, 15);
		scIOD.setVOILUTModule(currentImage.getCenter(), currentImage.getWindow());

		// Datenblockzur Kommunikation zwischen Plugin und Dialogbox generieren
		exda = new ExportData();

		// SecondaryCaptureIOD eintragen
		exda.theSC = scIOD;

		// Versucht einen Property File zu finden
		pfs = Util.getPropertyFileStream("");
		if (pfs != null) {
			exda.getProperties(pfs);
		} 

		// Export Panel darstellen
		exPan = new ExportPanel();
		exPan.initWithDataBlock(exda);
		exPan.copyrightL.setText("(C) 2000 T. Hacklaender unter der GPL Lizenz. Dcm Ver.: " + DcmUID.DCM_VERSION);

		// Dialogbox zeigen
		diaRes = JOptionPane.showConfirmDialog(null, exPan, "Als DICOM Secondary Capture Datei speichern", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null);
		if (diaRes != JOptionPane.OK_OPTION) {

			// "Cancel" gedrueckt
			return;
		} 
		exPan.updateDataBlock();

		// erzeugt ein neues DcmDataObject mit der gewuenschten Transfersyntax
		ddo = exda.theSC.getDDO(exda.encoding, exda.structure, exda.storage);

		// Speichert die Datei
		try {

			// Generiert den vollstaendigen Pfad
			pn = exda.outputDir.getPath() + File.separator + exda.fileNameToSave + exda.fileNameSuffix;
			f = new File(pn);
			DcmOutputStream.saveDDO(ddo, f);
		} catch (Exception ex) {
			ex.printStackTrace();
		} 

	} 


	/**
	 * Die Methode wird aufgerufen, wenn der 'Drucken'-Knopf gedrueckt wurde oder
	 * die Aktion Datei | Drucken aufgerufen wurde. Der Systemabhaengige
	 * Druckdialog wird aufgeblendet. Nach dessen Bestaetigung wird der
	 * Inhalt der Zeichenflaeche auf dem ausgewaehlten Drucker ausgegeben.
	 */
	private void pbPrint_actionPerformed() {

		// Erzeugen eines Printjobs
		PrintJob	pj = this.getToolkit().getPrintJob(this, "Print MRT", null);

		if (pj != null) {

			// Den Graphikkontext des Printjobs besorgen
			Graphics	gc = pj.getGraphics();

			// Der Drucker-Grafikkontext wird an die paint-Methode des Canvas
			// uebergeben. Damit druckt die Paint-Methode auf dem Drucker statt
			// auf dem Bildschirn
			pCanvas.paint(gc);

			// Nach dem Drucken den Grafikkontext freigeben und den Printjob beenden
			gc.dispose();
			pj.end();
		}		// if
 	 }		// Methode pbPrint_actionPerformed()
 

	/**
	 * Die Methode wird aufgerufen, wenn der Knopf zum Loeschen des selktierten
	 * Bildes gedrueckt wurde. Es wird zunaecht ein Dialog aufgeblendet, der eine
	 * Sicherheitsabfrage darstellt und nach dessen Bestaetigung wird das
	 * selektierte Bild geloescht.
	 */
	private void pbDelete_actionPerformed() {
		editClear_actionPerformed();
	} 


	/**
	 * Die Methode wird aufgerufen, wenn der 'Referenzserie'-Knopf gedrueckt wurde.
	 */
	private void pbReferenceData_actionPerformed() {
		createReferenceSeries();
	} 


	/**
	 * Die Methode fuehrt verschiedene Funktionen aus, je nachdem welches Werkzeug
	 * ausgewaehlt wurde. In jedem Fall wird die Position des Mauszeiger auf
	 * der Zeichenflaeche in der Positionsanzeige ausgegeben.
	 * Ist ein anderes Werkzeug ausser dem Standard-Mauszeiger aktiviert (z.B. Lupe),
	 * so wird der Inhalt der Zeichenflaeche staendig neu gezeichnet. Was genau
	 * gezeichnet wird, wird in der paint-Methode der Zeichenflaeche festgelegt.
	 * @param e Das Ereignis beim Bewegen der Maus ueber die Zeichenflaeche.
	 */
	private void pCanvas_mouseMoved(MouseEvent e) {

		// Bestimmen der Position auf dem Canvas
		int			x, y, img_x, img_y;
		int			small = 2;	// kleines Bild ist 1, grosses 2 (weil um Faktor 2 Groesser);
		String	bild = new String();

		curr_x = x = e.getX();
		curr_y = y = e.getY();
		img_x = x / 2;
		img_y = y / 2;
		bild = "(Bild1)";
		if (rb4Images.isSelected()) {
			small = 1;
			int bildnr = 1 + 2 * (y / 256) + (x / 256);

			bild = "(Bild " + bildnr + ")";
			switch (bildnr) {
			case 1: {
				break;
			} 
			case 2: {
				x = x - 256;
				break;
			} 
			case 3: {
				y = y - 256;
				break;
			} 
			case 4: {
				x = x - 256;
				y = y - 256;
				break;
			} 
			}
			img_x = x;
			img_y = y;
		} 

		// Ausgeben der Mausposition
		lPosition.setText("" + x + " ; " + y + " ");

		// Wenn die Zommfunktion aktiviert ist, muss das Canvas staendig neu
		// gezeichnet werden
		if (rbZoom.isSelected()) {

			// Ausmasse des zu vergroessernden Ausschnitts
			final int part_x = 32;
			final int part_y = 32;

			// Kontextsensitiven Cursor verwenden
			pCanvas.setCursor(ZOOM_CURSOR);

			// Besorgen des Bildes, ueber dem sich der Mauszeiger befindet
			ImageStack	is = pCanvas.getImageStack();
			ImagePlus		ip = null;
			int					mouseImage = getImageMouseIsOn(curr_x, curr_y);

			ip = is.getPictureAtPos(mouseImage);

			if (ip != null) {

				// Auslesen des Teilbildes, das vergroessert werden soll
				Image myImagePart = ip.getImagePart(img_x - ((part_x / 2) / small), img_y - ((part_y / 2) / small), part_x / small, part_y / small);

				// Vergroessern des zu zoomenden Teilbildes
				Image myZoomImage = myImagePart.getScaledInstance(part_x * 2, part_y * 2, Image.SCALE_DEFAULT);

				// Vergroesserten Ausschnitt und dessen Position in der Canvas-klasse setzen
				pCanvas.setZoomImage(myZoomImage);
				pCanvas.setZoomPos(curr_x - part_x, curr_y - part_x);
			} else {
				pCanvas.setZoomImage(null);
			} 

			// Neuzeichnen
			pCanvas.repaint();

		}		// if (Zoom)
 
		// Grauwerterfassung selektiert
		if (rbGrayValue.isSelected()) {

			// Besorgen des Bildes, ueber dem sich die Maus befindet
			ImageStack	is = pCanvas.getImageStack();
			ImagePlus		ip = null;
			int					mouseImage = getImageMouseIsOn(curr_x, curr_y);

			ip = is.getPictureAtPos(mouseImage);

			if (ip != null) {

				// Kontextsensitiven Cursor verwende
				pCanvas.setCursor(ZOOM_CURSOR);

				// Mausposition und Grauwert in der canvas-Klasse setzen
				pCanvas.setCurrentPos(curr_x, curr_y);
				pCanvas.setGrayValue(ip.getGrayValue(img_x, img_y));
			} 

			// Neuzeichnen
			pCanvas.repaint();
		}		// if (Grauwertbestimmung)
 
		// Winkelmessung selektiert
		if (rbAngel.isSelected()) {

			// Mauspositon in Canvas-Klasse setzen
			if (rb1Image.isSelected()) {
				pCanvas.setCurrentPos(e.getX(), e.getY());
			} else {

				// Im Fall von 4 angezeigten Bildern, wird die Positionn nur gesetzt, wenn
				// man sich noch ueber dem Bild befindet, ueber dem man die Bewegung
				// angefangen hat. Dadurch wird die Winkelmessung ueber den Bildrand
				// hinaus vermieden.
				int startImg = getImageMouseIsOn(lastMouseClickX, lastMouseClickY);
				int actImg = getImageMouseIsOn(e.getX(), e.getY());

				if (startImg == actImg) {
					pCanvas.setCurrentPos(e.getX(), e.getY());
				} 
			}		// if-else
			 pCanvas.repaint();
		}			// if (Winkelmessung als Tool selektiert)
 
	}				// Methode
 

	/**
	 * Die Methode wird aufgerufen, wenn der Mauszeiger die Zeichenflaeche
	 * verlaesst. Die Positionsanzeige wird dann geloescht und ein Standardmauszeiger
	 * eingestellt. Ausserdem werden einige Ausnahmewerte fuer die Grauwert-,
	 * Distanz- und Winkelmessung gesetzt.
	 */
	private void pCanvas_mouseExited() {
		lPosition.setText("");
		pCanvas.setZoomImage(null);
		pCanvas.setGrayValue(-1);
		pCanvas.setCurrentPos(-1, -1);
		pCanvas.setLastClickPos(-1, -1);
		pCanvas.setReleasePos(-1, -1);
		pCanvas.setCursor(DEFAULT_CURSOR);
		pCanvas.repaint();
	}		// Methode pCanvas_mouseExited()
 

	/**
	 * Die Methode wird aufgerufen, wenn der Knopf zur gleichzeitigen Anzeige von
	 * 4 Bildern gedrueckt wurde. Der Inhalt der Zeichenflaeche wird dann neu
	 * aufgebaut.
	 */
	private void rb4Images_actionPerformed() {
		pCanvas.setFourImages();
		pCanvas.repaint();
	}		// Methode rb4Images_actionPerformed()
 

	/**
	 * Die Methode wird aufgerufen, wenn der Knopf zur Anzeige von nur einem
	 * Bild gedrueckt wurde. Der Inhalt der Zeichenflaeche wird dann neu aufgebaut.
	 */
	private void rb1Image_actionPerformed() {
		pCanvas.setOneImage();
		pCanvas.repaint();
	}		// Methode rb1Image_actionPerformed()
 

	/**
	 * Die Methode wird aufgerufen, wenn eine Maustaste gedrueckt wird, waehrend
	 * sich der Mauszeiger ueber der Zeichenflaeche befindet. In diesem Fall werden
	 * die Mausklickkoordinaten abgespeichert und die Fensterungswerte des zuletzt
	 * selektierten Bildes gesichert.
	 */
	private void pCanvas_mousePressed(MouseEvent e) {

		// Setzen der Mausklickkoordinaten in Klassenvariablen
		lastMouseClickX = e.getX();
		lastMouseClickY = e.getY();

		if (rbPointer.isSelected()) {

			// Setzen der Center- und Window-Einstellungen (benoetigt fuer die Fensterung)
			lastCenter = pCenter.getValue();
			lastWindow = pWindow.getValue();
			lastTime = e.getWhen();
		}		// if (Mauszeiger als Tool selektiert)
 
		if (rbDistance.isSelected()) {

			// Setzen der Mausklickkoordinaten in Canvas-Klasse
			pCanvas.setLastClickPos(lastMouseClickX, lastMouseClickY);
		}		// if (Distanzmessung als Tool selektiert)
 
		if (rbAngel.isSelected()) {

			// Setzen der Mausklickkoordinaten in Canvas-Klasse
			pCanvas.setLastClickPos(lastMouseClickX, lastMouseClickY);
		}		// if (Winkelmessung als Tool selektiert)
 
	}			// Methode pCanvas_mousePressed(MouseEvent e)
 

	/**
	 * Die Methode wird aufgerufen, wenn eine Maustaste losgelassen wird, waehrend
	 * sich der Mauszeiger ueber der Zeichenflaeche befindet. Die Mauskoordinaten
	 * werden dann gesichert. Dies wird fuer die Winkelmessung benoetigt.
	 */
	private void pCanvas_mouseReleased(MouseEvent e) {

		// Standardcursor
		pCanvas.setCursor(DEFAULT_CURSOR);

		if (rbAngel.isSelected()) {

			// Setzen der Mausrleasekoordinaten in Canvas-Klasse
			pCanvas.setReleasePos(e.getX(), e.getY());
		}		// if (Winkelmessung als Tool selektiert)
 
	}			// Methode pCanvas_mouseReleased(MouseEvent e)
 

	/**
	 * Die Methode wird aufgerufen, wenn in der Zeichenflaeche ein Mausknopf
	 * gedrueckt wird. Es werden dann je nach ausgewaehltem Werkzeug verschiedene
	 * Funktionen durchgefuehrt. In jedem Fall werden die Fensterungseinstellungen des
	 * bisher selektierten Bildes gesichert. Beim neu selektierten Bild werden
	 * die alten Fenstereinstellungen wiederhergestellt.
	 * Anschliessend wird der Inhalt der Zeichenflaeche aktualisiert. Was genau
	 * dabei passiert, ist in der paint-Methode der Zeichenflaeche festgelegt.
	 * @param e Das Ereignis beim Druecken einer Maustaste ueber der Zeichenflaeche.
	 */
	private void pCanvas_mouseClicked(MouseEvent e) {

		// Nachschauen, welches Bild selektiert war
		ImagePlus ip = null;

		// Besorgen des Bildes, ueber dem sich gerade der Mauszeiger befindet
		int				actImage = getImageMouseIsOn(e.getX(), e.getY());

		// ausgewaehltes Bild als selektiert markieren
		pCanvas.setActiveImage(actImage);

		// Wiederherstellen der Fenstereinstellungen des neu selektierten Bildes
		ip = pCanvas.getImageStack().getPictureAtPos(actImage);
		if (ip != null) {
			int newc = ip.getCenter();
			int neww = ip.getWindow();

			pCenter.setValue(newc);
			pWindow.setValue(neww);
		} 

		// Aktualisierung der Zeichenflaeche
		pCanvas.repaint();
	}		// Methode pCanvas_mouseClicked(MouseEvent e)
 

	/**
	 * Die Methode wird aufgerufen, wenn der Knopf zum Ein- bzw. Ausschalten  der
	 * Bildbeschriftung gedrueckt wird. Der Inhalt der Zeichenflaeche wird dann
	 * neu aufgebaut, entweder mit oder ohne Beschriftungen.
	 */
	private void tbImageText_mouseClicked() {
		pCanvas.repaint();
	}		// Methode tbImageText_mouseClicked()
 

	/**
	 * Die Methode wird aufgerufen, wenn die Aktion Datei | Bild Oeffnen aus dem Menue
	 * aufgerufen wird. Diese Methode wird nur auf die entsprechende Methode des
	 * Bild-Oeffnen-Knopfes in der Werkzeugleiste umgeleitet.
	 */
	private void fileImageOpen_actionPerformed() {

		// Umleiten der Funktion auf den entsprechenden Button in der Toolleiste
		pbLoad_actionPerformed();
	}		// Methode fileOpen_actionPerformed()
 

	/**
	 * Die Methode wird aufgerufen, wenn die Aktion Datei | Speichern aus dem Menue
	 * aufgerufen wird. Diese Methode wird nur auf die entsprechende Methode des
	 * Speichern-Knopfes in der Werkzeugleiste umgeleitet.
	 */
	private void fileSave_actionPerformed() {

		// Umleiten der Funktion auf den entsprechenden Button in der Toolleiste
		pbSave_actionPerformed();
	}		// Methode fileSave_actionPerformed()
 

	/**
	 * Die Methode wird aufgerufen, wenn die Aktion Datei | Drucken aus dem Menue
	 * aufgerufen wird. Diese Methode wird nur auf die entsprechende Methode des
	 * Drucken-Knopfes in der Werkzeugleiste umgeleitet.
	 */
	private void filePrint_actionPerformed() {

		// Umleiten der Funktion auf den entsprechenden Button in der Toolleiste
		pbPrint_actionPerformed();
	}		// Methode filePrint_actionPerformed()
 

	/**
	 * Die Methode wird aufgerufen, wenn die Aktion Datei | Beenden aus dem Menue
	 * aufgerufen wird. Ggf. wird eine Abfrage aufgeblendet, ob die neuen Bilder
	 * verworfen werden sollen. Nach deren Bestaetigung wird das Programm beendet.
	 */
	private void fileExit_actionPerformed() {
		if (unsavedChanges) {
			JButton[] buttons = new JButton[3];

			buttons[0] = new JButton("Ja");
			buttons[1] = new JButton("Nein");
			buttons[2] = new JButton("Abbrechen");

			int response;

			response = JOptionPane.showConfirmDialog(null, "Sie haben neue nichtgespeicherte\n" + "Bilder. Moechten Sie wirklich Beenden?");
			if (response != 0) {
				return;
			} 
		} 

		if (isApplication) {
			System.exit(0);
		} else {
			this.dispose();
		} 
	} 


	/**
	 * Die Methode wird aufgerufen, wenn der Knopf zum Spiegeln des selektierten
	 * Bildes gedrueckt wurde. Es wird dann die entsprechende Methode fuer das
	 * selektierte Bild aufgerufen und der Zeichenflaecheninhalt neu dargestellt.
	 */
	private void pbMirror_actionPerformed() {
		ImagePlus ip = getSelectedImage();

		if (ip != null) {
			ip.mirrorHor();
			pCanvas.repaint();
		} 
	}		// Methode pbMirror_actionPerformed()
 

	/**
	 * Die Methode wird aufgerufen, wenn der Knopf zum Rotieren des selektierten
	 * Bildes gedrueckt wurde. Es wird dann die entsprechende Methode fuer das
	 * selektierte Bild aufgerufen und der Zeichenflaecheninhalt neu dargestellt.
	 */
	private void pbRotate_actionPerformed() {
		ImagePlus ip = getSelectedImage();

		if (ip != null) {
			ip.rotate90CW();
			pCanvas.repaint();
		}		// if
 	 }		// Methode pbRotate_actionPerformed()
 

	/**
	 * Die Methode wird aufgerufen, wenn der Knopf zum Invertieren des selektierten
	 * Bildes gedrueckt wurde. Es wird dann die entsprechende Methode fuer das
	 * selektierte Bild aufgerufen und der Zeichenflaecheninhalt neu dargestellt.
	 */
	private void pbInvert_actionPerformed() {
		ImagePlus ip = getSelectedImage();

		if (ip != null) {
			ip.invert();
			pCanvas.repaint();
		} 
	}		// Methode pbInvert_actionPerformed()
 

	/**
	 * Die Methode wird aufgerufen, wenn der Knopf zum optimalen Fenstern des selektierten
	 * Bildes gedrueckt wurde. Es wird dann die entsprechende Methode fuer das
	 * selektierte Bild aufgerufen und der Zeichenflaecheninhalt neu dargestellt.
	 * Ausserdem werden die Einstellungen der Schieberegler fuer die Fensterung
	 * angepasst.
	 */
	public void pbOptWindowing_actionPerformed() {

		// Selektiertes Bild besorgen
		ImagePlus ip = getSelectedImage();

		// Selektiertes Bild optimal Fenstern
		ip.optimalWindowing();

		// Fensterungseinstellungen der Slider aktualisieren
		pWindow.setValue(ip.getWindow());
		pCenter.setValue(ip.getCenter());

		// Neuzeichnen des Canvas
		pCanvas.repaint();
	}		// Methode pbOptWindowing_actionPerformed()
 

	/**
	 * Die Methode wird aufgerufen, wenn der Knopf zur Darstellung des Histogramms
	 * zum selektierten Bild gedrueckt wurde. Es wird dann ein Fenster aufgeblendet,
	 * in dem das Histogramm des selektierten Bildes zu sehen ist.
	 */
	private void pbHistogram_actionPerformed() {

		ImagePlus ip;
		short[][] myShortImage = null;
		int				actImage = pCanvas.getActiveImage();

		ip = getSelectedImage();

		if (ip != null) {

			// Besorgen des AWT-Bildes des selektierten Bildes
			myShortImage = ip.get8BitImageShort();

			// Neues Histogrammfenster erzeugen und darstellen
			Histogramm	myHist = new Histogramm(this, myShortImage, "Histogramm zu Bild " + actImage);

			myHist.setVisible(true);

		}		// if ip!=null
 
	}			// Methode pbHistogram_actionPerformed()
 

	/**
	 * Die Methode wird aufgerufen, wenn der Knopf zur Darstellung des
	 * k-Raums-Manipulators fuer das selektierte Bild gedrueckt wurde.
	 * Es wird dann ein Fenster aufgeblendet, in dem das Originalbild, der original
	 * k-Raum, der manipulierte k-Raum und die Ruecktransformation des
	 * manipulierten k-Raums dargestellt werden.
	 * Der Benutzer hat die Moeglichkeit, verschiedene Manipulationen am k-Raum
	 * vorzunehmen und dann die Ruecktransformationen berechnen zu lassen.
	 */
	private void pbKSpaceManip_actionPerformed() {

		// Selektiertes Bild suchen
		ImagePlus ip;

		ip = getSelectedImage();

		// K-Raum-manipulator aufblenden
		KSpaceManipulator manipulator = new KSpaceManipulator(ip, pCanvas);

		manipulator.show();

	}		// Methode pbKSpaceManip_actionPerformed()
 

	/**
	 * Die Methode wird aufgerufen, wenn der Knopf zur Darstellung des k-Raums
	 * des selektierten Bild gedrueckt wurde. Es wird dann ein Fenster aufgeblendet,
	 * in dem der k-Raum (Magnitudenbild) des selektierten Bildes zu sehen ist.
	 * Ausserdem kann man sich das Phasenbild sowie den Real- und Imaginaerteil
	 * anzeigen lassen.
	 */
	private void pbKSpace_actionPerformed() {

		// Selektiertes Bild suchen
		ImagePlus ip;

		ip = getSelectedImage();

		// K-Raum Anzeigefenster oeff´nen
		if (ip != null) {
			KSpaceFrame myOrig = new KSpaceFrame(ip);

			myOrig.setVisible(true);
		}		// if ip!=null
 
	}			// Methode pbKSpace_actionPerformed ()
 

	// ***************************************************************************
	// *********************  Sonstige Methoden  *********************************
	// ***************************************************************************


	/**
	 * Die Methode bestimmt aufgrund der x- und y-Koordinate des Mauszeigers
	 * auf der Zeichenflaeche, ueber welchem Bild sich der Mauszeiger gerade
	 * befindet. Dabei geht die Einstellung 1 Bild / 4 Bilder und die
	 * Bildlaufleistenposition mit in die Berechnung ein.
	 * @param x Die aktuelle x-Koordinate des Mauszeigers.
	 * @param y Die aktuelle y-Koordinate des Mauszeigers.
	 * @return Nummer des Bildes, ueber dem sich der Mauszeiger befindet.
	 */
	private int getImageMouseIsOn(int x, int y) {
		int bildnr;

		if (rb4Images.isSelected()) {
			int off = sbVerScrollbar.getValue() / 256;

			bildnr = 1 + 2 * (y / 256) + (x / 256) + 2 * off;
		} else {
			int off = sbVerScrollbar.getValue() / 512;

			bildnr = 1 + off;
		} 

		if ((x < 0) || (x > 512) || (y < 0) || (y > 512)) {
			bildnr = -1;
		} 

		return bildnr;
	}		// Methode getImageMouseIsOn(int x, int y)
 

	/**
	 * Die Methode liefert eine Referenz auf die Fortschrittsanzeige zurueck.
	 * @return Eine Referenz auf die Fortschrittsanzeige.
	 */
	public JProgressBar getProgressBar() {
		return prbarProgress;
	}		// Methode getProgressBar()
 

	/**
	 * Die Method setzt den Merker fuer nicht gespeicherte Bilder.
	 */
	public void setChanges() {
		unsavedChanges = true;
	}		// Methode setChanges()
 

	/**
	 * Die Methode setzt den Merker fuer nicht gespeicherte Bilder zurueck.
	 */
	private void resetChanges() {
		unsavedChanges = false;
	}		// Methode resetChanges()
 

	/**
	 * Die Methode liefert das ImagePlus-Objekt des zur Zeit selektierten Bildes.
	 * @return Das ImagePlus-Objekt des selektierten Bildes.
	 */
	private ImagePlus getSelectedImage() {

		int				actImage = pCanvas.getActiveImage();
		ImagePlus ip = null;

		if (actImage >= 0) {
			ip = pCanvas.getImageStack().getPictureAtPos(actImage);
		} 
		return ip;
	}		// Methode getSelectedImage()
 

	/**
	 * Die Methode wird aufgerufen, wenn sich der Zustand des Scrollbalkens
	 * veraendert hat. Der Scrollbalken wird dann aktualisiert.
	 */
	private void sbVerScrollbar_adjustmentValueChanged() {
		pCanvas.repaint();
		pCanvas.updateScrollbar();
	}		// Methode  sbVerScrollbar_adjustmentValueChanged()
 

	/**
	 * Die Method wird aufgerufen, wenn der Knopf zur Darstellung des
	 * Animationsfenster gedrueckt wurde. Es wird eine neue Instanz des
	 * Animationsfensters erzeugt und dargestellt.
	 */
	private void pbAnimation_actionPerformed() {
		AnimationFrame	anim = new AnimationFrame(this);

		anim.show();
	}		// Methode pbAnimation_actionPerformed()
 

	/**
	 * Die Methode wird aufgerufen, wenn der Knopf zur Berechnung eines MIP-Bildes
	 * gedrueckt wurde. Es wird dann zunaechst ein Dialog aufgeblendet, der abfragt,
	 * welche Bilder in die MIP-Berechnung einbezogen werden sollen.
	 * Nach dessen Bestaetigung wird ein neues MIP-Bild berechnet, in den Bildstapel
	 * eingefuegt und dargestellt.
	 */
	private void pbCalcMIP_actionPerformed() {

		// Besorgen der Position des ersten und letzten Bildes im Stapel
		ImageStack	myIS = pCanvas.getImageStack();
		int					start = 0;
		int					end = 0;
		int					numimages = myIS.getStackSize();

		if (numimages != 0) {
			start = 1;
			end = numimages;
		} 

		// Aufblenden des parameterdialoges mit voreingestelltem Start- und Endbild
		MIPParamDlg myDlg = new MIPParamDlg(this, "MIP-Parameter", true);

		myDlg.setImages(start, end);
		myDlg.show();

		// Vom Dialog aus wird die Methode zur Berechnung des MIP-Bildes angestossen

	}		// Methode pbCalcMIP_actionPerformed
 

	/**
	 * Die Methode berechnet ein MIP-Bild aus den angegebenen Bildern. Es wird ein
	 * ImagePlus-Objekt erzeugt, dass die gleichen DICOM-Informationen enthaelt,
	 * wie die Bilder, aus denen das MIP-Bild erzeugt wurde.
	 * @param start Das erste Bild, das in die Berechnung einfliessen soll.
	 * @param end Das letzte Bild, das in die Berechnung einfliessen soll.
	 */
	public void calcMIPImage(int start, int end) {
		ImageStack	myIS = pCanvas.getImageStack();

		if (end != 0) {
			int			size = myIS.getPictureAtPos(start).getSize();
			short[] MIPImage = new short[size * size];

			// Berechnen des MIP-Bildes
			for (int i = start; i <= end; i++) {
				ImagePlus ip = myIS.getPictureAtPos(i);

				short[]		actImage = new short[size * size];

				actImage = ip.getImage12BitShort();

				for (int x = 0; x < size * size; x++) {
					short val = actImage[x];

					if (val > MIPImage[x]) {
						MIPImage[x] = val;
					}		// if
 				 }		// for x
 
			}				// for i
 
			// Hier steht nun das MIP-Bild als Matrix von Integer-Werten zur Verfuegung
			// Es wird nun ein neues Bild im Image Stack an der aktuell selektierten
			// Position angelegt

			// Auslesen eines DcmdataObjects aus einem der Ursprungsbilder
			DcmDataObject myddo = myIS.getPictureAtPos(start).getDDO();

			// DcmImage aus der Bildmatrix und dem DicomDataObject eines der
			// Ursprungsbilder erzeugen
			DcmImage			dcmimg = null;

			try {
				dcmimg = new DcmImage(myddo);
				dcmimg.pixel16 = MIPImage;
			} catch (Exception err) {
				err.printStackTrace();
			} 

			// ImagePlus-Objekt erzeugen. Dies geschieht in der Klasse MyPanel.
			// ImagePlus ip = pCanvas.createNewImage(intensityImage);
			ImagePlus ip = pCanvas.createNewImage(dcmimg);

			// DDo im ImagePlus abspeichern
			ip.setDDO(myddo);

			// Erzeugungsdatum und Uhrzeit im Bild speichern
			ip.setCurrentDate();

			// Fensterung im Bild speichern
			ip.setCW(2048, 4096);

		}					// if (end!=0)
 
		// Neuzeichnen des Canvas
		pCanvas.repaint();

	}		// Methode calcMIPImage
 

	/**
	 * Die Methode wird aufgerufen, wenn der Knopf zur Berechnung der ersten 90-Grad-Projektion
	 * gedrueckt wurde. Es wird dann zunaechst ein Dialog aufgeblendet, der abfragt,
	 * welche Bilder in die Berechnung einbezogen werden sollen, welche neue Schicht
	 * berechnet werden soll und ob die Schicht interpoliert werden soll.
	 * Nach dessen Bestaetigung wird ein neues Bild der gewaehlten Projektion
	 * berechnet, in den Bildstapel eingefuegt und dargestellt.
	 */
	private void pbProjection1_actionPerformed() {

		// Besorgen der Position des ersten und letzten Bildes im stapel
		ImageStack	myIS = pCanvas.getImageStack();
		int					start = 0;
		int					end = 0;
		int					numimages = myIS.getStackSize();

		if (numimages != 0) {
			start = 1;
			end = numimages;
		} 

		// Auflebenden des Dialoges
		ProjParamDlg	myDlg = new ProjParamDlg(this, "Projektions-Parameter", true);

		myDlg.setImages(start, end);
		myDlg.setProjectionNr(1);
		myDlg.show();

		// Aus dem Dialog heraus wird ggf. createNewProj1Image aufgerufen

	}		// Methode pbProjection1_actionPerformed()
 

	/**
	 * Die Methode berechnet ein neues Bild der ersten 90-Grad-Projektion, fuegt
	 * es in den Bildstapel ein, fenstert es optimal und stellt es dar.
	 * @param start Das erste Bild, das in die Berechnung einfliessen soll.
	 * @param end Das letzte Bild, das in die Berechnung einfliessen soll.
	 * @param slice Die neue Schicht, die berechnet werden soll.
	 * @param interpolate Schalter fuer Interpolation ein/aus.
	 */
	public void createNewProj1Image(int start, int end, int slice, boolean interpolate) {

		// Projektion berechnen und neues Bild in Stack einfuegen
		int pos = pCanvas.createNewImage(calcProjection1(slice, start, end, interpolate));

		// Neues Bild als aktiviert markieren
		pCanvas.setActiveImage(pos);

		// aktives (neues) Bild optimal Fenstern
		pbOptWindowing_actionPerformed();
	}		// Methode createNewProj1Image(int start, int end, int slice, boolean interpolate)
 

	/**
	 * Die Methode berechnet ein neues Bild der ersten 90-Grad-Projektion. Das
	 * Bild wird allerdings nicht in den Bildstapel eingefuegt. Die Methode wird
	 * zur Berechnung der Bilder fuer die Animation benoetigt.
	 * @param start Das erste Bild, das in die Berechnung einfliessen soll.
	 * @param end Das letzte Bild, das in die Berechnung einfliessen soll.
	 * @param position Die neue Schicht, die berechnet werden soll.
	 * @param interpolate Schalter fuer Interpolation ein/aus.
	 */
	public ImagePlus calcProjection1(int position, int fromSlice, int toSlice, boolean interpolate) {

		ImageStack	imaStack = pCanvas.getImageStack();
		int					numSlices = toSlice - fromSlice + 1;
		int					size = imaStack.getPictureAtPos(fromSlice).getSize();
		short[]			projImg = new short[size * size];
		int					slicenum = fromSlice;
		ImagePlus		ip = imaStack.getPictureAtPos(slicenum);
		short[]			origImg = ip.getImage12BitShort();
		double			loc1 = 0.0;
		double			loc2 = 0.0;
		double			relThick = 1.0;

		loc1 = DcmValue.str2Double(ip.getSliceLocation(), 9999.9);
		if ((loc1 == 9999.9) | (numSlices == 1)) {
			relThick = 1.0;
		} else {
			loc2 = DcmValue.str2Double(imaStack.getPictureAtPos(slicenum + 1).getSliceLocation(), 9999.9);
			relThick = Math.abs(loc2 - loc1) / ip.getPixelSpacingRow();
		} 

		double	thickness = 0.0;

		for (int y = 0; y < size; y++) {
			if (thickness > relThick) {
				thickness -= relThick;
				slicenum++;
				if (slicenum > toSlice) {
					break;
				} 
				ip = imaStack.getPictureAtPos(slicenum);
				origImg = ip.getImage12BitShort();
			} 
			for (int x = 0; x < size; x++) {
				projImg[(y * size) + x] = origImg[(position * size) + x];
			} 
			thickness += 1.0;
		} 

		// Nun noch optional eine Interpolation
		if (interpolate) {
			int nThick = (int) Math.round(relThick);

			if ((nThick % 2) == 0) {
				nThick++;
			} 
			int		nThickHalf = nThick / 2;

			long	summe;

			for (int x = 0; x < size; x++) {
				for (int y = nThickHalf; y < size - nThickHalf; y++) {
					summe = 0;
					for (int i = y - nThickHalf; i <= y + nThickHalf; i++) {
						summe += projImg[(i * size) + x];
					} 
					projImg[(y * size) + x] = (short) (summe / nThick);
				} 
			} 

		}		// if (interpolate)
 
		// Auslesen eines DcmdataObjects aus einem der Ursprungsbilder
		DcmDataObject myddo = imaStack.getPictureAtPos(fromSlice).getDDO();

		// DcmImage aus der Bildmatrix und dem DicomDataObject eines der
		// Ursprungsbilder erzeugen
		DcmImage			dcmimg = null;

		try {
			dcmimg = new DcmImage(myddo);
			dcmimg.pixel16 = projImg;
		} catch (Exception err) {
			err.printStackTrace();
		} 

		// ImagePlus-Objekt erzeugen. Dies geschieht in der Klasse MyPanel.
		ImagePlus newip = new ImagePlus(dcmimg);

		// DDo im ImagePlus abspeichern
		newip.setDDO(myddo);

		// Erzeugungsdatum und Uhrzeit im Bild speichern
		newip.setCurrentDate();

		// Fensterung im Bild speichern
		newip.setCW(2048, 4096);

		return newip;
	}		// Methode calcProjection1(int position, int fromSlice, int toSlice, boolean interpolate)
 

	/**
	 * Die Methode wird aufgerufen, wenn der Knopf zur Berechnung der zweiten 90-Grad-Projektion
	 * gedrueckt wurde. Es wird dann zunaechst ein Dialog aufgeblendet, der abfragt,
	 * welche Bilder in die Berechnung einbezogen werden sollen, welche neue Schicht
	 * berechnet werden soll und ob die Schicht interpoliert werden soll.
	 * Nach dessen Bestaetigung wird ein neues Bild der gewaehlten Projektion
	 * berechnet, in den Bildstapel eingefuegt und dargestellt.
	 */
	private void pbProjection2_actionPerformed() {

		// Auslesen der Position des ersten und letzten Bildes des Stapels
		ImageStack	myIS = pCanvas.getImageStack();
		int					start = 0;
		int					end = 0;
		int					numimages = myIS.getStackSize();

		if (numimages != 0) {
			start = 1;
			end = numimages;
		} 

		// Oeffen des Dialoges mit voreingestellten Werten
		ProjParamDlg	myDlg = new ProjParamDlg(this, "Projektions-Parameter", true);

		myDlg.setImages(start, end);
		myDlg.setProjectionNr(2);
		myDlg.show();

		// Aus dem Dialog heraus wird ggf. createNewProj1Image aufgerufen

	}		// Methode pbProjection2_actionPerformed()
 

	/**
	 * Die Methode berechnet ein neues Bild der zweiten 90-Grad-Projektion, fuegt
	 * es in den Bildstapel ein, fenstert es optimal und stellt es dar.
	 * @param start Das erste Bild, das in die Berechnung einfliessen soll.
	 * @param end Das letzte Bild, das in die Berechnung einfliessen soll.
	 * @param slice Die neue Schicht, die berechnet werden soll.
	 * @param interpolate Schalter fuer Interpolation ein/aus.
	 */
	public void createNewProj2Image(int start, int end, int slice, boolean interpolate) {

		// Projektion berechnen und neues Bild in Stack einfuegen
		int pos = pCanvas.createNewImage(calcProjection2(slice, start, end, interpolate));

		// Neues Bild als aktiviert markieren
		pCanvas.setActiveImage(pos);

		// aktives (neues) Bild optimal Fenstern
		pbOptWindowing_actionPerformed();
	}		// Methode createNewProj2Image(int start, int end, int slice, boolean interpolate)
 

	/**
	 * Die Methode berechnet ein neues Bild der zweiten 90-Grad-Projektion. Das
	 * Bild wird allerdings nicht in den Bildstapel eingefuegt. Die Methode wird
	 * zur Berechnung der Bilder fuer die Animation benoetigt.
	 * @param start Das erste Bild, das in die Berechnung einfliessen soll.
	 * @param end Das letzte Bild, das in die Berechnung einfliessen soll.
	 * @param position Die neue Schicht, die berechnet werden soll.
	 * @param interpolate Schalter fuer Interpolation ein/aus.
	 */
	public ImagePlus calcProjection2(int position, int fromSlice, int toSlice, boolean interpolate) {
		ImageStack	imaStack = pCanvas.getImageStack();
		int					numSlices = toSlice - fromSlice + 1;
		int					size = imaStack.getPictureAtPos(fromSlice).getSize();
		short[]			projImg = new short[size * size];
		int					slicenum = fromSlice;
		ImagePlus		ip = imaStack.getPictureAtPos(slicenum);
		short[]			origImg = ip.getImage12BitShort();
		double			loc1 = 0.0;
		double			loc2 = 0.0;
		double			relThick = 1.0;

		loc1 = DcmValue.str2Double(ip.getSliceLocation(), 9999.9);
		if ((loc1 == 9999.9) | (numSlices == 1)) {
			relThick = 1.0;
		} else {
			loc2 = DcmValue.str2Double(imaStack.getPictureAtPos(slicenum + 1).getSliceLocation(), 9999.9);
			relThick = Math.abs(loc2 - loc1) / ip.getPixelSpacingRow();
		} 

		double	thickness = 0.0;

		for (int x = 0; x < size; x++) {
			if (thickness > relThick) {
				thickness -= relThick;
				slicenum++;
				if (slicenum > toSlice) {
					break;
				} 
				ip = imaStack.getPictureAtPos(slicenum);
				origImg = ip.getImage12BitShort();
			} 
			for (int y = 0; y < size; y++) {
				projImg[(y * size) + x] = origImg[(y * size) + position];
			} 
			thickness += 1.0;
		} 

		// Nun noch optional eine Interpolation
		if (interpolate) {
			int nThick = (int) Math.round(relThick);

			if ((nThick % 2) == 0) {
				nThick++;
			} 
			int		nThickHalf = nThick / 2;

			long	summe;

			for (int y = 0; y < size; y++) {
				for (int x = nThickHalf; x < size - nThickHalf; x++) {
					summe = 0;
					for (int i = x - nThickHalf; i <= x + nThickHalf; i++) {
						summe += projImg[(y * size) + i];
					} 
					projImg[(y * size) + x] = (short) (summe / nThick);
				} 
			} 

		}		// if (interpolate)
 
		// Auslesen eines DcmdataObjects aus einem der Ursprungsbilder
		DcmDataObject myddo = imaStack.getPictureAtPos(fromSlice).getDDO();

		// DcmImage aus der Bildmatrix und dem DicomDataObject eines der
		// Ursprungsbilder erzeugen
		DcmImage			dcmimg = null;

		try {
			dcmimg = new DcmImage(myddo);
			dcmimg.pixel16 = projImg;
		} catch (Exception err) {
			err.printStackTrace();
		} 

		// ImagePlus-Objekt erzeugen. Dies geschieht in der Klasse MyPanel.
		ImagePlus newip = new ImagePlus(dcmimg);

		// DDo im ImagePlus abspeichern
		newip.setDDO(myddo);

		// Erzeugungsdatum und Uhrzeit im Bild speichern
		newip.setCurrentDate();

		// Fensterung im Bild speichern
		newip.setCW(2048, 4096);

		return newip;
	}		// Methode calcProjection2(int slice, int fromSlice, int toSlice, boolean interpolate)
 

	/**
	 * Die Methode setzt das aktuelle Verzeichnis auf den zuletzt gewählten Pfad.
	 * @param fd Dialog, für den der Pfad gesetzt wird.
	 */
	private void setStdDirectory(JFileChooser fd) {

		// Voreingestellten Pfad auslesen und setzen
		String	Path = (String) this.preferences.get("Path");

		if (Path != null) {
			File	myPath = new File(Path);

			if (!myPath.exists()) {
				Exception e = new Exception("Ungültiger Pfad in \nVoreinstellungsdatei" + "\ndv_preferences.properties !");

				ErrorMessage.showMessage(e);
				myPath = new File("C:\\");
			} 
			fd.setCurrentDirectory(myPath);
			myPath = null;
		} 
	} 


	/**
	 * Die Methode liest einie Voreinstellungen aus der Datei
	 * preferences.properties ein.
	 */
	private void loadPreferences() {
		LineNumberReader	numRead = null;
		boolean						ok = true;

		// Versuche, den neuen Eingabestream zu oeffnen
		try {
			FileReader	streamIn = new FileReader("dv_preferences.properties");

			numRead = new LineNumberReader(streamIn);
		} catch (Exception e) {
			System.out.println("Preferencesdatei nicht gefunden");
			return;
		}		// try-catch
		 String e = null;

		do {
			try {
				e = numRead.readLine();
			} catch (Exception err) {
				return;
			} 
			if (e == null) {
				break;
			} 
			StringTokenizer token = new StringTokenizer(e, "=");
			String					key = token.nextToken();
			String					value = token.nextToken();

			value = value.replace('"', ' ').trim();
			try {
				preferences.put(key, value);
			} catch (Exception err) {
				System.out.println("Fehler beim Lesen der Preferencesdatei");
			} 
		} while (e != null);
		try {
			numRead.close();
		} catch (Exception err) {
			System.out.println("Fehler beim Schließen der Preferencesdatei");
		} 
	} 


	/**
	 * Die Methode wird beim Beenden der Anwendung aufgerufen und speichert dann
	 * einige Voreinstellungen wie z.B. den Dateipfad, aus dem zuletzt ein
	 * Rohdatensatz geladen wurde.
	 */
	private void savePreferences() {
		BufferedWriter	bufWriter = null;
		FileWriter			streamOut = null;
		boolean					ok = true;

		// Ausgabestream zu oeffnen
		try {
			streamOut = new FileWriter("dv_preferences.properties");
			bufWriter = new BufferedWriter(streamOut);
		} catch (Exception e) {
			System.out.println("Fehler beim Öffnen der Preferencesdatei");
		} 
		Enumeration iterator = preferences.propertyNames();

		while (iterator.hasMoreElements()) {
			String	key = (String) iterator.nextElement();
			String	value = (String) preferences.get(key);

			try {
				streamOut.write(key);
				streamOut.write("=\"");
				streamOut.write(value + "\"\n");
			} catch (Exception err) {
				System.out.println("Fehler beim Schreiben der Preferencesdatei");
			} 
			try {
				streamOut.flush();
			} catch (Exception err) {
				System.out.println("Fehler beim Schreiben der Preferencesdatei");
			} 
			try {
				streamOut.close();
			} catch (Exception err) {
				System.out.println("Fehler beim Schließen der Preferencesdatei");
			} 
			streamOut = null;
		} 
	}		// savePreferences()
 

	/**
	 * Method declaration
	 * 
	 * 
	 * @return
	 * 
	 * @see
	 */
	public DVPanel getCanvasReference() {
		return pCanvas;
	} 


	/**
	 * Die Methode erzeugt die Bilder der Referenzserie.
	 */
	private void createReferenceSeries() {

		// Anzahl Bildzeilen
		final int							nRows = 256;

		// Anzahl Bildspalten
		final int							nColumns = 256;

		// Signifikante Bits
		final int							bits = 12;

		// Bildnummer
		int										imaNumber;

		// z-Position des Bildes
		int										zPosition;

		DcmDataObject					ddo;
		DcmImage							di = null;
		String[]							sa;
		boolean								selection = false;
		ViewerReferenceSeries vrs;
		int										windowCenter;
		int										windowWidth;

		// Wenn die Option 'An Selektion positionieren' eingeschaltet ist,
		// wird dies zunächst ungeschaltet und gemerkt
		if (rbNextImageAtSelection.isSelected()) {
			selection = true;
			rbNextImageAtFreePlace.setSelected(true);
		} 

		// Eine neue Referenzserie erzeugen
		vrs = new ViewerReferenceSeries(nRows, nColumns, bits);

		// Serie mit 16 Referenzbildern erzeugen
		for (imaNumber = 1; imaNumber <= 16; imaNumber++) {

			zPosition = (imaNumber - 1) * 4;

			// Das DcmDataObject des Refernzbildes erzeugen
			ddo = vrs.getDDO(zPosition, imaNumber);

			// Das Refernzbildes darstellen
			try {
				di = new DcmImage(ddo);
			} catch (Exception e) {}

			// Erzeugen eines ImagePlus-Objektes aus dem DcmImage
			ImagePlus ip = pCanvas.createNewImage(di);

			// Zusaetlich das DcmDataObject zum ImagePlus speichern. damit gehen keine
			// Informationen verloren
			ip.setDDO(ddo);

			// VOI LUT Module PS 3.3-C.11.2:
			// - Center und Window sind optionale Attribute.
			// - sind mehere Paare definiert, wird das erste Paar genommen.
			if (ddo.isAvailable(DcmDDE.DD_WindowCenter) & ddo.hasValue(DcmDDE.DD_WindowCenter)) {
				sa = DcmValue.str2StringArray(ddo.getString(DcmDDE.DD_WindowCenter), "\\");
				windowCenter = (int) DcmValue.str2Long(sa[0], 2048);
				sa = DcmValue.str2StringArray(ddo.getString(DcmDDE.DD_WindowWidth), "\\");
				windowWidth = (int) DcmValue.str2Long(sa[0], 4096);
			} else {
				windowCenter = 2048;
				windowWidth = 4096;
			} 
			pCenter.setValue(windowCenter);
			pWindow.setValue(windowWidth);
		} 

		// Option 'An Selektion positionieren' auf Ursprungswert zuruecksetzen
		rbNextImageAtSelection.setSelected(selection);
	} 

}












































































/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

