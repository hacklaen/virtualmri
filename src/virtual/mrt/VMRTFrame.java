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


import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.lang.reflect.*;
import java.beans.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import virtual.mrt.sequences.*;
import virtual.mrt.artefacts.*;
import virtual.tools.*;

import rad.dicom.dcm.*;
import rad.dicom.ima.*;

// import jigl.*;
import jigl.image.*;


/**
 * Diese Klasse stellt die Oberflaeche fuer das Messwerkzeug des virtuellen Tomographen dar.
 * Es werden alle graphischen Elemente erzeugt, eingerichtet und dargestellt.
 * Ausserdem findet hier die Ereignissteuerung der Bedienelmente statt.
 * <DL><DT><B>Modifications: </B><DD>
 * Thomas Hacklaender 2000.8.19:
 * Programmfehler beseitigt, der erst ab Java 1.3 auftritt: pCanvas und pSequence
 * waren gleichberechtigte Komponenten. Mouse-Events durften aber bei  sichtbarer
 * pSequence nur an diese gelangen. Loesung: Es muss eine z-Ordnung definiert werden.
 * Auf die gesamte ContentPane des Frames wird eine JLayeredPane gelegt. Alle Komponenten
 * werden auf die JLayeredPane gezeichnet. pSequence wird in die "sichtbarste" Ebene
 * gezeichnet.<br>
 * Thomas Hacklaender 2006.1.2:
 * Überdeckung durch Meldung des Security Managers am unteren Bildrand berücksichtigt.<br>
 * Thomas Hacklaender 2006.1.2:
 * Applet/Application als Startklassen getrennt.<br>
 * Thomas Hacklaender 2000.4.3:
 * Umstellung auf die aktuelle Version 2.1 des dcm-Package.<br>
 * Thomas Hacklaender 2000.4.3:
 * In der Methode drawCreatedIntensityImage Optimale Windoweinstellung
 * eingebaut.<br>
 * Thomas Hacklaender 2000.7.2:
 * Menuepunkt 'Hilfe/Ueber' zeigt die AboutBox.<br>
 * Thomas Hacklaender 2000.7.13:
 * Knopf 'Fall-Info' eingebaut.<br>
 * Thomas Hacklaender 2000.7.14:
 * Menues erweitert.<br>
 * Thomas Hacklaender 2002.10.12:
 * About-Menues gestrichen. Info in Tools-Menue.<br>
 * Thomas Hacklaender 2002.10.13:
 * Strings internationalisiert.<br>
 * Thomas Hacklaender 2002.10.29:
 * in jInit die Hintergrundfarbe auf Dunkelblau gesetzt.<br>
 * </DD></DL>
 * @author   Christian Schalla
 * @author   Andreas Truemper
 * @version  1.3, 2002.10.29
 */
public class VMRTFrame extends JFrame {


	// th 2005.11.11, added
	ResourceBundle					frameRsrc = PropertyResourceBundle.getBundle("virtual/mrt/resources/vmrt_frame");


	/**
	 * Standard-Mauszeiger.
	 */
	private final Cursor		defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);


	/**
	 * Mauszeiger fuer die Fensterung.
	 */
	private final Cursor		windowCursor = new Cursor(Cursor.MOVE_CURSOR);

	// Menueleiste inlusive ihrer Menues und deren Menueeintraege erzeugen


	/**
	 * Die Menueleiste.
	 */
	private JMenuBar				menuBar1 = new JMenuBar();


	/**
	 * Das Dateimenue.
	 */
	private JMenu						menuFile = new JMenu();


	/**
	 * Dateimenueeintrag 'Oeffnen'.
	 */
	private JMenuItem				menuFileOpen = new JMenuItem();


	/**
	 * Dateimenueeintrag 'Speichern'.
	 */
	private JMenuItem				menuFileSave = new JMenuItem();


	/**
	 * Dateimenueeintrag 'Drucken'.
	 */
	private JMenuItem				menuFilePrint = new JMenuItem();


	/**
	 * Dateimenueeintrag 'Beenden'.
	 */
	private JMenuItem				menuFileExit = new JMenuItem();


	/**
	 * Bearbeiten-Menue.
	 */
	private JMenu						menuEdit = new JMenu();


	/**
	 * Bearbeiten-Menueeintrag 'Selektiertes Bild loeschen'.
	 */
	private JMenuItem				menuEditClear = new JMenuItem();


	/**
	 * Bearbeiten-Menueeintrag 'Alle Bilder loeschen'.
	 */
	private JMenuItem				menuEditClearAll = new JMenuItem();


	/**
	 * Tools-Menue.
	 */
	private JMenu						menuTools = new JMenu();


	/**
	 * Tools-Menueeintrag 'About...'.
	 */
	private JMenuItem				menuToolsAbout = new JMenuItem();


	/**
	 * Hilfe-Menue.
	 */

	// th 2002.10.12
	// private JMenu						menuHelp = new JMenu();


	// Hier werden einige Icons fuer einige Buttons deklariert


	/**
	 * Icon fuer den Knopf zum Laden eines Rohdatensatzes.
	 */
	private ImageIcon				iOpen;


	/**
	 * Icon fuer den Knopf zum Speichern eines Bildes.
	 */
	private ImageIcon				iSave;


	/**
	 * Icon fuer den Knopf zum Drucken des Zeichenflaecheninhalts.
	 */
	private ImageIcon				iPrint;


	/**
	 * Icon fuer den Knopf, um 1 Bild darzustellen.
	 */
	private ImageIcon				i1Image;


	/**
	 * Icon fuer den Knopf, um 4 Bilder darzustellen.
	 */
	private ImageIcon				i4Images;


	/**
	 * Icon fuer den Knopf zur Anzeige der Bidbeschriftung.
	 */
	private ImageIcon				iInfoText;


	/**
	 * Icon fuer den Knopf zur Darstellung des Histogrammfensters.
	 */
	private ImageIcon				iHistogram;


	/**
	 * Icon fuer den Knopf zur Darstellung des k-Raum-Fensters.
	 */
	private ImageIcon				iKSpace;


	/**
	 * Icon fuer den Knopf zur Anzeige des k-Raum-Manipulator-Fensters.
	 */
	private ImageIcon				iKSpaceMan;


	/**
	 * Icon fuer den Knopf zur Darstellung der Fall-Infos.
	 */
	private ImageIcon				iCaseInfo;


	/**
	 * Icon fuer den Knopf zur Erzeugung eines Referenzbildes.
	 */
	private ImageIcon				iRefIma;


	/**
	 * Icon fuer den Knopf zur Erzeugung des Beispielbildes.
	 */
	private ImageIcon				iSampleIma;


	/**
	 * Icon fuer den Knopf zum Reset des Windows (width=max).
	 */
	private ImageIcon				winReset;


	/**
	 * Icon fuer den Knopf zur Optimalen Einstellung des Windows.
	 */
	private ImageIcon				winOpt;


	/**
	 * Statusleiste.
	 */
	private JLabel					statusBar = new JLabel();


	/**
	 * Beschriftungsfeld fuer die Mausposition.
	 */
	private JLabel					lPositionText = new JLabel();


	/**
	 * Textfeld fuer die Positionsangabe des Mauszeigers, wenn dieser sich
	 * ueber dem Canvas befindet.
	 */
	private JLabel					lPosition = new JLabel();


	/**
	 * Fortschrittsanzeige.
	 */
	private JProgressBar		prbarProgress = new JProgressBar();


	/**
	 * Darstellungsbereich fuer den gesamten Frame. Da pSequenceSettings ueber pCanvas
	 * gezeichnet werden muss, ist diese Zwischenebene notwendig
	 */
	private JLayeredPane		flPane = new JLayeredPane();


	/**
	 * Zeichenflaeche (Bildflaeche / Canvas).
	 */
	public MRTPanel					pCanvas = new MRTPanel(this);

	// /** Horizontale Bildlaufleiste fuer die Zeichenflaeche. */
	// public JScrollBar sbHorScrollbar = new JScrollBar();


	/**
	 * Vertikale Bildlaufleiste fuer die Zeichenflaeche.
	 */
	public JScrollBar				sbVerScrollbar = new JScrollBar();


	/**
	 * Rahmen fuer den Werkzeugbereich am rechten Rand des Fensters.
	 */
	private JPanel					pToolbar = new JPanel();

	// tpMain ist das Hauptpanel des Karteireiters
	// Dort hinein werden weitere Panels gepackt, die die einzelnen
	// Karteikarten repraesentieren


	/**
	 * Hauptrahmen fuer die Karteikarten .
	 */
	private JTabbedPane			tpMain = new JTabbedPane();


	/**
	 * Knopf zum Laden eines neuen Rohdatensatzes.
	 */
	private JButton					pbLoad = new JButton();


	/**
	 * Knopf zum Speichern eines Bildes.
	 */
	private JButton					pbSave = new JButton();


	/**
	 * Knop zum Drucken des Zeichenflaecheninhalts.
	 */
	private JButton					pbPrint = new JButton();


	/**
	 * Knopf zum Laden des Referenzdatensatzes.
	 */
	private JButton					pbReferenceData = new JButton();


	/**
	 * Knopf zum Laden des Beispielbildes.
	 */
	private JButton					pbSampleData = new JButton();


	/**
	 * Knopf zum Darstellen der Fall-Infos.
	 */
	private JButton					pbCaseInfo = new JButton();


	/**
	 * Karteikarte fuer alle Layout-Funkionen.
	 */
	private JPanel					tpLayout = new JPanel();


	/**
	 * Karteikarte fuer die Fensterungs-Funktionen (Helligkeit und Kontrast).
	 */
	private JPanel					tpWindow = new JPanel();


	/**
	 * Karteikarte fuer die Artefaktsimulation.
	 */
	private JPanel					tpArtefact = new JPanel();


	/**
	 * Karteikarte fuer die Zusatzfunktionen (Simulationszeit, u.a.).
	 */
	private JPanel					tpExtras = new JPanel();

	// Nun folgt die Erzeugung der einzelnen Elmente der Layout-Karte


	/**
	 * Knopf zur Auswahl der Darstellung von nur einem Bild.
	 */
	public JToggleButton		rb1Image = new JToggleButton();


	/**
	 * Knopf zur Auswahl der Darstellung von 4 Bildern gleichzeitig.
	 */
	private JToggleButton		rb4Images = new JToggleButton();


	/**
	 * Knopfgruppe zur Gruppierung der Knoepfe rb1Image und rb4Images.
	 */
	private ButtonGroup			bgNumOfImages = new ButtonGroup();


	/**
	 * Knopf zur Darstellung des k-Raums des selektierten Bildes.
	 */
	private JButton					pbKSpace = new JButton();


	/**
	 * Knopf zum Oeffnen des k-Raum-Manipulators.
	 */
	private JButton					pbKSpaceManip = new JButton();


	/**
	 * Knopf zur Darstellung des Histogramms des selektierten Bildes.
	 */
	private JButton					pbHistogram = new JButton();


	/**
	 * Knopf zur Darstellung der Bildbeschriftungen (Nummern, TR, TI, u.a.).
	 */
	public JToggleButton		tbImageText = new JToggleButton();

	// Nun folgt die Erstellung der einzelnen Elemente der Fensterung-Karteikarte
	// Panel zur Gruppierung der Funktionselemente fuer die Helligkeitsveraenderung


	/**
	 * Schieberegler fuer die Helligkeit (Center).
	 */
	private SliderPanel			pCenter;


	/**
	 * Schieberegler fuer den Kontrast (Window).
	 */
	private SliderPanel			pWindow;


	/**
	 * Knopf zum  Zuruecksetzen der Fensterung auf C=2048 und W=4096.
	 */
	private JButton					pbResetWindowing = new JButton();


	/**
	 * Knopf zum Berechnen der optimalen Fensterung.
	 */
	private JButton					pbOptWindowing = new JButton();

	// Nun folgt die Erzeugung der einzelnen Elemente der Artefakt-Karteikarte


	/**
	 * Bereich, in dem die Artefakt-Simulatoren ihre Oberflaechenelemente
	 * darstellen koennen.
	 */
	private JPanel					pArtefacts = new JPanel();


	/**
	 * Auswahlbox zur Auswahl eines Artefakt-Simulators.
	 */
	private ArtefactsCombo	cbArtefacts = new ArtefactsCombo(this, pArtefacts);

	// Nun folgt die Erzeugung der einzelnen Elemente der Extras-Karteikarte


	/**
	 * Schieberegler zur Auswahl des Simulationszeitfaktors (0-100%).
	 */
	private SliderPanel			pTimeFactor;


	/**
	 * Rahmen zur optischen Gruppierung der Elemente, die zur Einstellung der
	 * Position des naechsten Bildes dienen.
	 */
	private JPanel					pNextImageAt = new JPanel();


	/**
	 * Beschriftung fuer die Bedienelemente, die zur Einstellung der
	 * Position des naechsten Bildes dienen.
	 */
	private JLabel					lNextImageAt = new JLabel();


	/**
	 * Knopf zur Erzeugung des naechsten Bildes an der selektierten Position.
	 */
	public JRadioButton			rbNextImageAtSelection = new JRadioButton();


	/**
	 * Knopf zur Erzeugung des naechsten Bildes an der naechsten freien Position.
	 */
	private JRadioButton		rbNextImageAtFreePlace = new JRadioButton();


	/**
	 * Knopfgruppe fuer die beiden Knoepfe rbNextImageAtSelection und rbNextImageAtFreePlace.
	 */
	private ButtonGroup			bgPlaceNextImage = new ButtonGroup();


	/**
	 * CheckBox zum Aktivieren eines neu erzeugten Bildes
	 */
	public JCheckBox				chbActivateNewImg = new JCheckBox();


	/**
	 * Beschriftung ueber der Sequenzauswahlbox.
	 */
	private JLabel					lSequence = new JLabel();


	/**
	 * Darstellungsbereich, in dem die Pulssequenzen ihre eigenen Info-GUI-Elemente darstellen koennen.
	 */
	private JPanel					pSequence = new JPanel();


	/**
	 * Darstellungsbereich, in dem die Pulssequenzen ihre Einstellungsmoeglichkeiten darstellen koennen.
	 */
	private JPanel					pSequenceSettings = new JPanel();


	/**
	 * Auswahlbox, aus der der Benutzer die zu verwendende Pulssequenz auswaehlen kann.
	 */
	private SequenceCombo		cbSequence = new SequenceCombo(this, pSequenceSettings, pSequence);


	// Ab hier beginnt die Deklaration von NICHT-GUI-ELEMENTEN


	/**
	 * Aktuelle x-Mausposition auf der Zeichenflaeche.
	 */
	private int							curr_x;


	/**
	 * Aktuelle y-Mausposition auf der Zeichenflaeche.
	 */
	private int							curr_y;


	/**
	 * Letzte Mausklick-x-Koordinate.
	 */
	private int							lastMouseClickX;


	/**
	 * Letzte Mausklick-y-Koordinate.
	 */
	private int							lastMouseClickY;


	/**
	 * Zeitpunkt des letzten Mausklicks im Hauptfenster.
	 */
	private long						lastTime;


	/**
	 * Fensterungszentrum-Einstellungen beim letzten Mausklick.
	 */
	private int							lastCenter;


	/**
	 * Fensterungsbreite-Einstellungen beim letzten Mausklick.
	 */
	private int							lastWindow;


	/**
	 * Das FileLoader-Objekt laedt einen Rohdatensatz und stellt die
	 * Rohdatenmatrizen zur Verfuegung.
	 */
	private FileLoader			myFileLoader = null;


	/**
	 * Merker fuer nichtgespeicherte Aenderungen.
	 */
	private boolean					unsavedChanges = false;


	/**
	 * Hashtabelle für die Standardeinstellungen
	 */
	private Properties			preferences = new Properties();


	/**
	 * Gibt an, ob von einer Applickation (true) oder einem Applet gestartet wurde.
	 */
	private boolean					isApplication = true;


	/**
	 * Standardkonstruktor. Default is "Application".
	 */
	public VMRTFrame() {
            this(true);
        }


	/**
	 * Standardkonstruktor. Er initialisiert das Fenster, fuellt die Auswahlbox
	 * mit den zur Verfuegung stehenden Pulssequenzen und stellt die Bedienelemente
	 * der ersten Pulssequenz dar.
	 */
	public VMRTFrame(boolean isApplication) {
                AboutBox aBox;
            
		this.isApplication = isApplication;
                
                aBox = showAboutBox();
                
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
                
		try {

			// Initialisieren der GUI-Elemente
			jbInit();

			// Standardeinstellungen:

			// Referenzdatensatz laden
			// pbReferenceData_actionPerformed();
			chbActivateNewImg.setSelected(true);

			ErrorMessage.setMainFrame(this);
		} catch (Exception e) {
			e.printStackTrace();
		} 

                if (isApplication) {
                    // Application
                    loadPreferences();
                } else {
                    // Applet
                    pbLoad.setEnabled(false);
                    pbSave.setEnabled(false);
                    menuFileOpen.setEnabled(false);
                    menuFileSave.setEnabled(false);
                }             
                
                aBox.dispose();
	}

	/**
	 * Setzt die Umgebung des Programms
         * tha 2005.12.18.
	 */
	public static void setLookAndFeel() {
            // Look and Feel
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                // UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
                // UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
 

	/**
	 * Die Methode wird aufgerufen, wenn die Aktion Hilfe | Info aus dem Menue
	 * aufgerufen wurde. Das Informationsfenster wird dann aufgeblendet.
	 */
	private AboutBox showAboutBox() {
                Dimension aboxSize;
                Dimension scrSize;
                
		AboutBox	abox = new AboutBox();

		// th 2002.10.29
		// abox.setImage(this.getClass().getResource("resources/mrt.jpg"));
		abox.setFileURL(this.getClass().getResource(frameRsrc.getString("vmrt.splash")));
		aboxSize = abox.getSize();
                scrSize = Toolkit.getDefaultToolkit().getScreenSize();
		abox.setLocation((scrSize.width - aboxSize.width) / 2, (scrSize.height - aboxSize.height) / 2);
		abox.setVisible(true);
                
                return abox;
	} 
 

	/**
	 * Stellt den Frame zentriert auf dem Bildschirm dar.
	 */
	public void showCentered() {
            Dimension frameSize;
            Dimension scrSize;

            // Frame validieren, die er eine voreingestellte Groeße besitzen.
            this.validate();

            // Frame zentriert zeigen
            frameSize = this.getSize();
            scrSize = Toolkit.getDefaultToolkit().getScreenSize();
            this.setLocation((scrSize.width - frameSize.width) / 2, (scrSize.height - frameSize.height) / 2);
            this.setVisible(true);
            this.toFront();
        }
 
        
	/**
	 * Die Methode richtet die Bedienelemente ein und stellt diese dar.
	 */
	private void jbInit() throws Exception {

		// Layoutmanager des Hauptfensters auf null setzen.
		this.getContentPane().setLayout(null);

		// Groesse des Hauptfensters auf 800*600 Punkte festlegen. Damit ist es
		// auf jedem SuperVGA-Monitor darstellbar (Minus 15 Punkte Taskleiste).
                if (System.getSecurityManager() == null) {
                    this.setSize(new Dimension(800, 585));
                } else {
                    // Wenn das Programm innerhalb eines Security Managers läuft,
                    // wird die Warnung "" von 18 Pt am unteren Bildrand eingeblendet.
                    // Sie überdeckt den Bildinhalt.
                    this.setSize(new Dimension(800, 600));
                }

		// Die Groesse des Fensters soll nicht veraendert werden koennen
		this.setResizable(false);

		// Hintergrundfarbe setzen
		// th 2002.10.29
		// this.setBackground(new Color(204, 204, 204));
		// th 2005.11.12
		// this.setBackground(new Color(0, 0, 51));

		// Titelzeile setzen
		this.setTitle(frameRsrc.getString("vmrtframe.title"));

		// Ergaenzt: tha 2000.8.19
		// Um mehrere Lightweight Komponenten in einer geordneten z-Ordnung
		// uebereinander darstellen zu koennen
		flPane.setLayout(null);
		flPane.setSize(this.getSize());
		this.getContentPane().add(flPane, null);

		// Laden des 'oeffnen'-Icons
		iOpen = new ImageIcon(this.getClass().getResource("resources/openfile.gif"));

		// Laden des 'Speichern'-Icons
		iSave = new ImageIcon(this.getClass().getResource("resources/save.gif"));

		// Laden des 'Drucken'-Icons
		iPrint = new ImageIcon(this.getClass().getResource("resources/print.gif"));

		// Laden des "1 Bild"-Icons
		i1Image = new ImageIcon(this.getClass().getResource("resources/1image.gif"));

		// Laden des "4 Bilder"-Icons
		i4Images = new ImageIcon(this.getClass().getResource("resources/4images.gif"));

		// Laden des "Bildbeschriftung"-Icons
		iInfoText = new ImageIcon(this.getClass().getResource("resources/imageinfo.gif"));

		// Laden des "Histogramm"-Icons
		iHistogram = new ImageIcon(this.getClass().getResource("resources/histogramm.gif"));

		// Laden des "k-Raum"-Icons
		iKSpace = new ImageIcon(this.getClass().getResource("resources/k_raum.gif"));

		// Laden des k-Raum Manipulator Icons
		iKSpaceMan = new ImageIcon(this.getClass().getResource("resources/k_raum_man.gif"));

		// Laden des "Fall-Info"-Icons
		iCaseInfo = new ImageIcon(this.getClass().getResource("resources/caseinfo.gif"));

		// Laden des "Referenzbild"-Icons
		iRefIma = new ImageIcon(this.getClass().getResource("resources/refima.gif"));

		// Laden des "Beispielbild"-Icons
		iSampleIma = new ImageIcon(this.getClass().getResource("resources/sampleima.gif"));

		// Laden des "Window Reset"-Icons
		winReset = new ImageIcon(this.getClass().getResource("resources/winreset.gif"));

		// Laden des "Window Optimal"-Icons
		winOpt = new ImageIcon(this.getClass().getResource("resources/winopt.gif"));

		// Menueleiste mit seinen Menues einrichten
		menuBar1.add(menuFile);
		menuBar1.add(menuEdit);
		menuBar1.add(menuTools);

		// th 2002.10.12
		// menuBar1.add(menuHelp);
		this.setJMenuBar(menuBar1);

		// Dateimenue mit seinen Menuepunkten einrichten
		menuFile.setText(frameRsrc.getString("vmrtframe.menu.file"));

		// Menueeintrag 'oeffnen' einrichten
		menuFileOpen.setText(frameRsrc.getString("vmrtframe.menu.file.open"));
		menuFileOpen.addActionListener(new ActionListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void actionPerformed(ActionEvent e) {
				fileOpen_actionPerformed();
			} 

		});

		// Menueeintrag 'Speichern einrichten'
		menuFileSave.setText(frameRsrc.getString("vmrtframe.menu.file.saveas"));
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
		menuFilePrint.setText(frameRsrc.getString("vmrtframe.menu.file.print"));
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
		menuFileExit.setText(frameRsrc.getString("vmrtframe.menu.file.exit"));
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

		// Die einzelnen Menueeintraege desm Dateimenue hinzufuegen
		menuFile.add(menuFileOpen);
		menuFile.add(menuFileSave);
		menuFile.add(menuFilePrint);
		menuFile.add(menuFileExit);

		// Bearbeiten-Menue mit seinen Menuepunkten einrichten
		menuEdit.setText(frameRsrc.getString("vmrtframe.menu.edit"));

		menuEditClear.setText(frameRsrc.getString("vmrtframe.menu.edit.delete"));
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

		menuEditClearAll.setText(frameRsrc.getString("vmrtframe.menu.edit.deleteall"));
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

		menuTools.setText(frameRsrc.getString("vmrtframe.menu.tools"));
		menuToolsAbout.setText(frameRsrc.getString("vmrtframe.menu.tools.about"));
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

		// th 2002.10.12
		// Hilfemenue mit seinen Menuepunkten einrichten
		// menuHelp.setText("Hilfe");
		// menuHelpAbout.setText("Info...");

		// Panel fuer die Einstellungsmoeglichkeiten der Pulssequenz formatiern
		// und positionieren. Anschliessend allerdings wieder verstecken, da sonst
		// das halbe Canvas ueberdeckt wird.
		pSequenceSettings.setBounds(0, 256, 513, 256);
		pSequenceSettings.setBorder(BorderFactory.createEtchedBorder());
		pSequenceSettings.setVisible(false);

		// tha 2000.8.19: Panel ueber alle anderen Layer einfuegen
		flPane.add(pSequenceSettings, JLayeredPane.DRAG_LAYER);

		// Statusleiste formatieren und positionieren
		statusBar.setText(frameRsrc.getString("vmrtframe.statusbar.title"));
		statusBar.setForeground(Color.black);
		statusBar.setFont(new Font("Dialog", 0, 12));
		statusBar.setBounds(new Rectangle(1, 516, 512, 15));

		// tha 2000.8.19
		flPane.add(statusBar);

		// Canvas formatieren und positionieren
		pCanvas.setMaximumSize(new Dimension(512, 512));
		pCanvas.setPreferredSize(new Dimension(512, 512));
		pCanvas.setBackground(Color.black);
		pCanvas.setMinimumSize(new Dimension(512, 512));
		pCanvas.setBounds(new Rectangle(1, 0, 512, 512));
		pCanvas.setLayout(null);

		// tha 2000.8.19
		flPane.add(pCanvas, null);
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
				pCanvas_mouseExited(e);
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


		// Horizontale Bildlaufleiste zum Canvas erzeugen, Positionieren und formatieren

		/*
		 * sbHorScrollbar = pCanvas.createHorizontalScrollBar();
		 * sbHorScrollbar.setBounds(new Rectangle(1,513,512,20));
		 * sbHorScrollbar.setBorder(BorderFactory.createEtchedBorder());
		 * sbHorScrollbar.setMinimum(0);
		 * sbHorScrollbar.setMaximum(1024);
		 * sbHorScrollbar.setVisibleAmount(511);
		 * sbHorScrollbar.setBlockIncrement(512);
		 * sbHorScrollbar.setUnitIncrement(512);
		 * this.getContentPane().add(sbHorScrollbar, null);
		 * sbHorScrollbar.addAdjustmentListener(new java.awt.event.AdjustmentListener() {
		 * public void adjustmentValueChanged(AdjustmentEvent e) {
		 * sbHorScrollbar_adjustmentValueChanged();
		 * }
		 * });
		 */

		// Vertikale Bildlaufleiste zum Canvas erzeugen, Positionieren und formatieren
		sbVerScrollbar = pCanvas.createVerticalScrollBar();
		sbVerScrollbar.setBounds(new Rectangle(513, 1, 20, 512));
		sbVerScrollbar.setBorder(BorderFactory.createEtchedBorder());
		sbVerScrollbar.setMinimum(0);
		sbVerScrollbar.setMaximum(1024);
		sbVerScrollbar.setVisibleAmount(511);
		sbVerScrollbar.setBlockIncrement(512);
		sbVerScrollbar.setUnitIncrement(512);

		// tha 2000.8.19
		flPane.add(sbVerScrollbar, null);
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

		// tha 2000.8.19
		flPane.add(pToolbar, null);

		// 'Referenzbild'-Knopf formatieren und positionieren
		pbReferenceData.setMargin(new Insets(2, 2, 2, 2));
		pbReferenceData.setIcon(iRefIma);
		pbReferenceData.setBounds(new Rectangle(3, 12, 27, 26));
		pbReferenceData.setToolTipText(frameRsrc.getString("vmrtframe.btn.irefima.tooltip"));
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

		// 'Beispielbild'-Knopf formatieren und positionieren
		pbSampleData.setMargin(new Insets(2, 2, 2, 2));
		pbSampleData.setIcon(iSampleIma);
		pbSampleData.setBounds(new Rectangle(40, 12, 27, 26));
		pbSampleData.setToolTipText(frameRsrc.getString("vmrtframe.btn.isampleima.tooltip"));
		pbSampleData.setEnabled(true);
		pToolbar.add(pbSampleData, null);
		pbSampleData.addActionListener(new java.awt.event.ActionListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void actionPerformed(ActionEvent e) {
				pbSampleData_actionPerformed();
			} 

		});

		// 'oeffnen'-Knopf formatieren und positionieren
		pbLoad.setMargin(new Insets(2, 2, 2, 2));
		pbLoad.setIcon(iOpen);
		pbLoad.setBounds(new Rectangle(77, 12, 27, 26));
		pbLoad.setToolTipText(frameRsrc.getString("vmrtframe.btn.open.tooltip"));
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

		// 'Speichern'-Knopf formatieren und positinieren
		pbSave.setMargin(new Insets(2, 2, 2, 2));
		pbSave.setBounds(new Rectangle(114, 12, 27, 26));
		pbSave.setToolTipText(frameRsrc.getString("vmrtframe.btn.save.tooltip"));
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
		pbPrint.setBounds(new Rectangle(151, 12, 27, 26));
		pbPrint.setIcon(iPrint);
		pbPrint.setToolTipText(frameRsrc.getString("vmrtframe.btn.print.tooltip"));
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
		tpMain.setBounds(new Rectangle(3, 45, 249, 185));

		// Ansicht-Karteikarte einrichten (Layout)
		tpLayout.setLayout(null);
		tpMain.add(tpLayout, frameRsrc.getString("vmrtframe.tab.layout.title"));

		// Button zur Darstellung nur eines Bildes einrichten
		rb1Image.setMargin(new Insets(2, 2, 2, 2));
		rb1Image.setIcon(i1Image);
		rb1Image.setSelected(true);
		rb1Image.setToolTipText(frameRsrc.getString("vmrtframe.btn.1image.tooltip"));
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
		rb4Images.setToolTipText(frameRsrc.getString("vmrtframe.btn.4image.tooltip"));
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

		// Knopf zur Anzeige der Bildbeschriftungen einrichten
		tbImageText.setIcon(iInfoText);
		tbImageText.setToolTipText(frameRsrc.getString("vmrtframe.btn.infotext.tooltip"));
		tbImageText.setBounds(new Rectangle(79, 10, 27, 27));
		tpLayout.add(tbImageText, null);
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

		// Knopf zur Histogrammanzeige einrichten
		pbHistogram.setIcon(iHistogram);
		pbHistogram.setToolTipText(frameRsrc.getString("vmrtframe.btn.histogramm.tooltip"));
		pbHistogram.setBounds(new Rectangle(116, 10, 27, 27));
		tpLayout.add(pbHistogram, null);
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

		// Knopf zur Anzeige des k-Raum Manipulationstools einrichten
		pbKSpaceManip.setIcon(iKSpaceMan);
		pbKSpaceManip.setToolTipText(frameRsrc.getString("vmrtframe.btn.kspaceman.tooltip"));
		pbKSpaceManip.setBounds(new Rectangle(190, 10, 27, 27));
		tpLayout.add(pbKSpaceManip, null);
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

		// Knopf zur k-Raum-Anzeige einrichten
		pbKSpace.setIcon(iKSpace);
		pbKSpace.setToolTipText(frameRsrc.getString("vmrtframe.btn.kspace.tooltip"));
		pbKSpace.setBounds(new Rectangle(153, 10, 27, 27));
		tpLayout.add(pbKSpace, null);
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

		// 'Fall-Info'-Knopf formatieren und positionieren
		pbCaseInfo.setMargin(new Insets(2, 2, 2, 2));
		pbCaseInfo.setIcon(iCaseInfo);
		pbCaseInfo.setBounds(new Rectangle(5, 47, 27, 27));
		pbCaseInfo.setToolTipText(frameRsrc.getString("vmrtframe.btn.caseinfo.tooltip"));
		pbCaseInfo.setEnabled(true);
		tpLayout.add(pbCaseInfo, null);
		pbCaseInfo.addActionListener(new java.awt.event.ActionListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void actionPerformed(ActionEvent e) {
				pbCaseInfo_actionPerformed();
			} 

		});


		// Fenstern-Karteikarte einrichten
		tpWindow.setLayout(null);
		tpMain.add(tpWindow, frameRsrc.getString("vmrtframe.tab.window.title"));

		// Helligkeitseingabefeld mit Slider hinzufuegen
		pCenter = new SliderPanel(frameRsrc.getString("vmrtframe.window.slider.center"), 0, 4096, "");
		pCenter.setBounds(new Rectangle(2, 5, 187, 70));
		pCenter.setToolTipText(frameRsrc.getString("vmrtframe.window.slider.center.tooltip"));
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
		pWindow = new SliderPanel(frameRsrc.getString("vmrtframe.window.slider.width"), 0, 4096, "");
		pWindow.setBounds(new Rectangle(2, 80, 187, 70));
		pWindow.setToolTipText(frameRsrc.getString("vmrtframe.window.slider.width.tooltip"));
		pWindow.setTextRange(1, 4096);
		pWindow.setTickSpacing(1024, 0);
		pWindow.fillPanel();
		tpWindow.add(pWindow);
		pWindow.setValue(4096);
		pWindow.setSliderMinimum(1);
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

		// Button fuer optimale Fensterung einrichten
		// th 2002.10.27
		// pbOptWindowing.setBounds(194, 80, 45, 70);
		// pbOptWindowing.setText(frameRsrc.getString("vmrtframe.window.btn.opt.title"));
		pbOptWindowing.setBounds(203, 81, 27, 27);
		pbOptWindowing.setIcon(winOpt);
		pbOptWindowing.setToolTipText(frameRsrc.getString("vmrtframe.window.btn.opt.tooltip"));

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

		// Button fuer das Zuruecksetzen der Fensterung einrichten
		// th 2002.10.27
		// pbResetWindowing.setBounds(194, 5, 45, 70);
		// pbResetWindowing.setText(frameRsrc.getString("vmrtframe.window.btn.reset.title"));
		pbResetWindowing.setBounds(203, 123, 27, 27);
		pbResetWindowing.setIcon(winReset);
		pbResetWindowing.setToolTipText(frameRsrc.getString("vmrtframe.window.btn.reset.tooltip"));

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

				// pbResetWindowing_actionPerformed(e);
			} 

		});

		// Artefakt-Karteikarte einrichten
		tpArtefact.setLayout(null);
		tpMain.add(tpArtefact, frameRsrc.getString("vmrtframe.tab.artefact.title"));


		// Elemente der Artefakt-Karteikarte einrichten
		pArtefacts.setBounds(new Rectangle(1, 32, 258, 122));
		pArtefacts.setLayout(null);
		cbArtefacts.setBounds(new Rectangle(1, 2, 243, 27));

		// Geloescht: tha 2000.8.19
		// cbArtefacts.addItem("keine");
		cbArtefacts.getArtefacts();

		tpArtefact.add(cbArtefacts, null);
		tpArtefact.add(pArtefacts, null);


		// Extras-Karteikarte einrichten
		tpExtras.setLayout(null);
		tpMain.add(tpExtras, frameRsrc.getString("vmrtframe.tab.extras.title"));

		// Checkbox zur Aktivierung eines neuen Bildes einrichten
		chbActivateNewImg.setText(frameRsrc.getString("vmrtframe.extras.activate"));
		chbActivateNewImg.setBounds(5, 5, 240, 25);
		tpExtras.add(chbActivateNewImg, null);

		// Elemente zur Auswahl der Position des naechsten erzeugten Bildes anlegen
		pNextImageAt.setBounds(2, 30, 240, 45);
		pNextImageAt.setLayout(null);
		pNextImageAt.setBorder(BorderFactory.createEtchedBorder());
		lNextImageAt.setText(frameRsrc.getString("vmrtframe.extras.position"));
		lNextImageAt.setBounds(7, 0, 240, 20);
		pNextImageAt.add(lNextImageAt);
		rbNextImageAtSelection.setText(frameRsrc.getString("vmrtframe.extras.position.selection"));
		rbNextImageAtSelection.setBounds(7, 22, 120, 20);
		pNextImageAt.add(rbNextImageAtSelection, null);
		rbNextImageAtFreePlace.setText(frameRsrc.getString("vmrtframe.extras.position.freeplace"));
		rbNextImageAtFreePlace.setBounds(130, 22, 100, 20);
		pNextImageAt.add(rbNextImageAtFreePlace, null);
		rbNextImageAtFreePlace.setSelected(true);
		bgPlaceNextImage.add(rbNextImageAtSelection);
		bgPlaceNextImage.add(rbNextImageAtFreePlace);
		tpExtras.add(pNextImageAt, null);

		// Sliderpanel fuer den Zeitfaktor einrichten
		pTimeFactor = new SliderPanel(frameRsrc.getString("vmrtframe.extras.simulation"), 0, 100, "%");
		pTimeFactor.setBounds(2, 95, 240, 60);
		pTimeFactor.setTickSpacing(10, 5);
		pTimeFactor.setTextRange(0, 100);
		pTimeFactor.setToolTipText(frameRsrc.getString("vmrtframe.extras.simulation.tooltip"));
		pTimeFactor.fillPanel();
		tpExtras.add(pTimeFactor, null);

		// Sequenzauswahlbox einrichten und positionieren (inkl. Label)
		cbSequence.setToolTipText(frameRsrc.getString("vmrtframe.sequence.tooltip"));
		cbSequence.setBounds(new Rectangle(3, 256, 249, 27));

		pToolbar.add(cbSequence, null);
		lSequence.setText(frameRsrc.getString("vmrtframe.sequence"));
		lSequence.setForeground(Color.black);
		lSequence.setBounds(new Rectangle(3, 237, 250, 15));
		pToolbar.add(lSequence, null);

		// Sequenzen auslesen und Combobox einrichten
		cbSequence.getSequences();

		// Sequenz-Panel einrichten und positionieren
		pSequence.setBorder(BorderFactory.createEtchedBorder());
		pSequence.setToolTipText(frameRsrc.getString("vmrtframe.sequence.settings.tooltip"));
		pSequence.setLayout(null);
		pSequence.setBounds(new Rectangle(3, 292, 249, 221));
		pToolbar.add(pSequence, null);

		// Fortschrittsanzeige enrichten
		prbarProgress.setToolTipText(frameRsrc.getString("vmrtframe.statusbar.progress"));
		prbarProgress.setBounds(new Rectangle(675, 516, 112, 15));
		prbarProgress.setMinimum(0);
		prbarProgress.setMaximum(255);
		prbarProgress.setValue(0);

		// tha 2000.8.19
		flPane.add(prbarProgress, null);

		// Positionsanzeige einrichten
		lPosition.setText("");
		lPosition.setForeground(Color.black);
		lPosition.setFont(new Font("Dialog", 0, 12));
		lPosition.setBounds(new Rectangle(600, 516, 95, 15));

		// tha 2000.8.19
		flPane.add(lPosition, null);

		lPositionText.setText(frameRsrc.getString("vmrtframe.statusbar.position"));
		lPositionText.setForeground(Color.black);
		lPositionText.setFont(new Font("Dialog", 0, 12));
		lPositionText.setBounds(new Rectangle(510, 516, 100, 15));

		// tha 2000.8.19
		flPane.add(lPositionText, null);

	}		// Methode jbInit
 

	// ////////////////////////////
	// Event-Methoden
	// ////////////////////////////


	/**
	 * Die Methode wird aufgerufen, wenn der Knopf zum Loeschen des selktierten
	 * Bildes gedrueckt wurde. Es wird zunaecht ein Dialog aufgeblendet, der eine
	 * Sicherheitsabfrage darstellt und nach dessen Bestaetigung wird das
	 * selektierte Bild geloescht.
	 */
	private void editClear_actionPerformed() {

		// Auflenden eines Bestaetigungsdialogs und Auslesen des gedrueckten Knopfes
		int response;

		response = JOptionPane.showConfirmDialog(null, frameRsrc.getString("vmrtframe.dialog.delete1"));

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

		response = JOptionPane.showConfirmDialog(null, frameRsrc.getString("vmrtframe.dialog.deleteall"));

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
	 * Mausbewegung. Eine Bewegung der Maus in x-Richtung veraendert das Zentrum,
	 * eine Bewegung in y-Richtung die Breite des Fensters.
	 * @param e Das Ereignis, das beim Bewegen der Maus ausgeloest wird.
	 */
	private void pCanvas_mouseDragged(MouseEvent e) {

		int		curX, curY, difX, difY, difT;
		long	curWhen;

		// Cursor setzen
		pCanvas.setCursor(windowCursor);

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

		if ((lastCenter + difY) < 4096) {
			if ((lastCenter + difY) >= 0) {
				pCenter.setValue(lastCenter + difY);
				lastCenter += difY;
			} 
		} 

		if ((lastWindow + difX) <= 4096) {
			if ((lastWindow + difX) > 0) {
				pWindow.setValue(lastWindow + difX);
				lastWindow += difX;
			} 
		} 

		lastMouseClickX = curX;
		lastMouseClickY = curY;
		lastTime = curWhen;

		updateCW();
		pCanvas.repaint();
	}		// Methode pCanvas_mouseDragged
 

	/**
	 * Die Methode wird aufgerufen, wenn die Aktion Hilfe | Info aus dem Menue
	 * aufgerufen wurde. Das Informationsfenster wird dann aufgeblendet.
	 */
	private void helpAbout_actionPerformed() {
            showAboutBox();
	} 


	/**
	 * Die Method wird aufgerufen, wenn der Schliessen-Knopf oben rechts am Fenster
	 * betaetigt wird. Die Methode muss ueberschrieben werden, damit das Programm bei
	 * einem System-Close beendet werden kann.
	 * @param e Das Ereignis beim Betaetigen des Schliessen-Knopfes.
	 */
	protected void processWindowEvent(WindowEvent e) {
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
                        if (isApplication) {
                            // Application
                            this.savePreferences();
                        }

			// Aufruf der gleichen Methode wie bei Aktion Datei | Beenden
			fileExit_actionPerformed();
		} 
	} 


	/**
	 * Die Methode aendert die Fensterungseinstellungen des selektierten Bildes
	 * gemaess den Einstellungen der Schieberegler.
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
	 * selektierten imagePlus-Klasse.
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
	 * Die Methode setzt das aktuelle Verzeichnis auf den zuletzt gewählten Pfad.
	 * @param fd Dialog, für den der Pfad gesetzt wird.
	 */
	private void setStdDirectory(JFileChooser fd) {
		File		myPath;
		String	pathStr;

		// th 2002.10.13: Default path ist das user Directory
		myPath = new File(System.getProperty("user.dir"));
		fd.setCurrentDirectory(myPath);

		// Voreingestellten Pfad auslesen und setzen
		pathStr = (String) this.preferences.get("Path");

		if (pathStr != null) {
			myPath = new File(pathStr);
			if (myPath.exists()) {
				fd.setCurrentDirectory(myPath);
			} else {
				Exception e = new Exception(frameRsrc.getString("vmrtframe.exception.path"));

				ErrorMessage.showMessage(e);
			} 
		} 
	} 
 

	/**
	 * Die Methode wird aufgerufen, wenn der 'Referenzbild'-Knopf gedrueckt wurde.
	 * Es wird ein Refenezdatensatz geladen, der die Parameterdaten von 6
	 * verschiedenen Geweben enthaelt.
	 */
	private void pbReferenceData_actionPerformed() {
		myFileLoader = new FileLoader();
		myFileLoader.loadReferenceData();

		this.setTitle(myFileLoader.getCaseInfoTitle());

		CaseInfoFrame cif = new CaseInfoFrame();

		// Geaendert 2002.8.18 tha
		// cif.setInfo(VMRTFrame.class.getResource(myFileLoader.getCaseInfoName()), myFileLoader.getCaseInfoTitle());
		cif.setInfo(myFileLoader.getCaseInfoURL(), myFileLoader.getCaseInfoTitle());
		cif.show();
	} 
 

	/**
	 * Die Methode wird aufgerufen, wenn der 'Beispielbild'-Knopf gedrueckt wurde.
	 * Es wird ein Refenezdatensatz geladen, der die Parameterdaten von 6
	 * verschiedenen Geweben enthaelt.
	 */
	private void pbSampleData_actionPerformed() {
		myFileLoader = new FileLoader();
		myFileLoader.loadSampleData();

		this.setTitle(myFileLoader.getCaseInfoTitle());

		CaseInfoFrame cif = new CaseInfoFrame();
		cif.setInfo(myFileLoader.getCaseInfoURL(), myFileLoader.getCaseInfoTitle());
		cif.show();
	} 


	/**
	 * Die Methode wird aufgerufen, wenn der 'Oeffnen'-Knopf gedrueckt wurde oder
	 * die Aktion Datei | Oeffnen aufgerufen wurde.
	 */
	private void pbLoad_actionPerformed() {
		File				theFile;
		String				strFile;
		String				strDirectory;

		JFileChooser	fd = new JFileChooser();
		IDXFile				ff = new IDXFile();

		fd.addChoosableFileFilter(ff);
		fd.setFileFilter(ff);

		fd.setDialogTitle(frameRsrc.getString("vmrtframe.dialog.open.title"));
		this.setStdDirectory(fd);

		fd.showOpenDialog(this);

		// th 2002.10.13
		// String	strFile = fd.getSelectedFile().getName();
		theFile = fd.getSelectedFile();
		if (theFile == null) {

			// Abbruch, wenn keine Datei gewählt wurde
			return;
		} 

		strFile = fd.getSelectedFile().getName();
		strDirectory = fd.getSelectedFile().getParent();

		preferences.put("Path", strDirectory);

		myFileLoader = new FileLoader();
		myFileLoader.loadData(strDirectory, strFile);

		this.setTitle(myFileLoader.getCaseInfoTitle());

		if (myFileLoader.getCaseInfoTitle() != null) {
			CaseInfoFrame cif = new CaseInfoFrame();

			cif.setInfo(myFileLoader.getCaseInfoURL(), myFileLoader.getCaseInfoTitle());
			cif.show();
		} 
	} 


	/**
	 * Die Methode wird aufgerufen, wenn der 'Speichern'-Knopf gedrueckt wurde oder
	 * die Aktion Datei | Speichern aufgerufen wurde. Ein Dateidialogfenster wird
	 * aufgeblendet, wo der Benutzer den Pfad und den Namen der zu speichernden
	 * Datei angeben kann. Mit Hilfe des zurueckgelieferten Dateipfades und dem
	 * selektierten Bild wird ein DcmSecondCapt-Objekt erzeugt und dessen write-Methode
	 * aufgerufen. Dadurch wird das selektierte Bild im DICOM-Format gespeichert.
	 */
	private void pbSave_actionPerformed() {
		File					f;
		DcmDataObject ddo;

		JFileChooser	fd = new JFileChooser();

		fd.setDialogTitle(frameRsrc.getString("vmrtframe.dialog.save.title"));

		setStdDirectory(fd);
		fd.showSaveDialog(this);
		String							strFile = fd.getSelectedFile().getName();
		String							strDirectory = fd.getSelectedFile().getParent();

		ImagePlus						currentImage = getSelectedImage();
		short[]							dcmPixels = currentImage.getImage12BitShort();
		SecondaryCaptureIOD scIOD = new SecondaryCaptureIOD();

		scIOD.setPublicFields(currentImage.getDDO());
		scIOD.set16UBitGrayImage(dcmPixels, 256, 256, 15);
		int[] w = ImaUtil.calcAutomaticCW(dcmPixels, 15, true);

		scIOD.setVOILUTModule(w[0], w[1]);

		try {
			f = new File(strDirectory + File.separator + strFile);
			ddo = scIOD.getDDO(DcmDataObject.LITTLE_ENDIAN, DcmDataObject.EXPLICITE_VR, DcmDataObject.META_STORAGE);
			DcmOutputStream.saveDDO(ddo, f);
		} catch (Exception err) {
			err.printStackTrace();
		} 

	}		// Methode pbSave_actionPerformed
 

	/**
	 * Die Methode wird aufgerufen, wenn der 'Drucken'-Knopf gedrueckt wurde oder
	 * die Aktion Datei | Drucken aufgerufen wurde. Der systemabhaengige
	 * Druckdialog wird aufgeblendet. Nach dessen Bestaetigung wird der
	 * Inhalt der Zeichenflaeche auf dem ausgewaehlten Drucker ausgegeben.
	 */
	private void pbPrint_actionPerformed() {

		// Erzeugen eines Printjobs
		PrintJob	pj = this.getToolkit().getPrintJob(this, frameRsrc.getString("vmrtframe.print.job.title"), null);

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
 	 }		// Methode pbPrint_actionPerformed


	/**
	 * Die Methode wird aufgerufen, wenn der 'Fall-Info'-Knopf gedrueckt wurde.
	 * Es wird ein Refenezdatensatz geladen, der die Parameterdaten von 6
	 * verschiedenen Geweben enthaelt.
	 */
	private void pbCaseInfo_actionPerformed() {
		CaseInfoFrame cif = new CaseInfoFrame();

		// Ergaenzt 2002.8.18 tha
		// Falls noch kein Fall ausgewaehlt wurde nichts tun
		if (myFileLoader == null) {
			return;

		} 
		cif.setInfo(myFileLoader.getCaseInfoURL(), myFileLoader.getCaseInfoTitle());
		cif.show();
	} 


	/**
	 * Die Methode bestimmt waehrend der Bewegung des Mauszeigers ueber der
	 * Zeichenflaeche dessen Poition und gibt sie in einem Ausgabefeld aus.
	 * Dabei muss zwischen der Darstellung von 1 und 4 Bildern unterschieden werden.
	 * @param e Das Ereignis beim Bewegen der Maus ueber die Zeichenflaeche.
	 */
	private void pCanvas_mouseMoved(MouseEvent e) {
		int x, y, img_x, img_y;
		int small = 2;	// kleines Bild ist 1, grosses 2 (weil um Faktor 2 Groesser);

		// th 2002.10.13
		// String	bild = new String();

		curr_x = x = e.getX();
		curr_y = y = e.getY();
		img_x = x / 2;
		img_y = y / 2;

		// bild = "(Bild1)";
		if (rb4Images.isSelected()) {
			small = 1;
			int bildnr = 1 + 2 * (y / 256) + (x / 256);

			// bild = "(Bild " + bildnr + ")";
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

		// th 2002.10.12
		// lPosition.setText("" + x + " ; " + y + " ");
		lPosition.setText(x + " ; " + y);
	}		// Methode pCanvas_mouseMoved
 

	/**
	 * Die Methode wird aufgerufen, wenn der Mauszeiger die Zeichenflaeche
	 * verlaesst. Die Positionsanzeige wird dann geloescht.
	 * @param e Das Ereignis beim Verlassen der Zeichenflaeche mit der Maus.
	 */
	private void pCanvas_mouseExited(MouseEvent e) {
		lPosition.setText("");
		pCanvas.setCursor(defaultCursor);
		pCanvas.repaint();
	}		// Methode pCanvas_mouseExited
 

	/**
	 * Die Methode wird aufgerufen, wenn der Knopf zur gleichzeitigen Anzeige von
	 * 4 Bildern gedrueckt wurde. Der Inhalt der Zeichenflaeche wird dann neu
	 * aufgebaut.
	 */
	private void rb4Images_actionPerformed() {
		pCanvas.setFourImages();
		pCanvas.repaint();
	}		// Methode rb4Images_actionPerformed
 

	/**
	 * Die Methode wird aufgerufen, wenn der Knopf zur Anzeige von nur einem
	 * Bild gedrueckt wurde. Der Inhalt der Zeichenflaeche wird dann neu aufgebaut.
	 */
	private void rb1Image_actionPerformed() {
		pCanvas.setOneImage();
		pCanvas.repaint();
	}		// Methode  rb1Image_actionPerformed
 

	/**
	 * Die Methode wird aufgerufen, sobald ein Mausknopf auf der Zeichenflaeche
	 * gedrueckt wird. Es werden dann die Mauskoordinaten und die aktuellen
	 * Fensterungswerte gespeichert. Dies wird benoetigt, um die Fensterung per
	 * Mausbewegung durchfuehren zu koennen.
	 * @param e Das Ereignis, das beim Druecken eines Mausknopfes ausgeloest wird.
	 */
	private void pCanvas_mousePressed(MouseEvent e) {

		// Setzen der Mausklickkoordinaten in Klassenvariablen
		lastMouseClickX = e.getX();
		lastMouseClickY = e.getY();

		// Setzen der Center- und Window-Einstellungen (benoetigt fuer die Fensterung)
		lastCenter = pCenter.getValue();
		lastWindow = pWindow.getValue();
		lastTime = e.getWhen();
	}		// Methode pCanvas_mousePressed
 

	/**
	 * Die Methode wird aufgerufen, sobald eine Maustaste ueber der Zeichenflaeche
	 * wieder losgelassen wird. Der Mauszeiger wird dann auf den Standardwert
	 * zurueckgesetzt.
	 * @param e Das Ereignis, das beim Loslassen einer Maustaste ausgeloest wird.
	 */
	private void pCanvas_mouseReleased(MouseEvent e) {
		pCanvas.setCursor(defaultCursor);
	}		// Methode pCanvas_mouseReleased
 

	/**
	 * Die Methode wird aufgerufen, wenn auf der Zeichenflaeche ein Mausklick
	 * durchgefuehrt wird. Es werden dann die Fenstereinstellungen des
	 * bisher selektierten Bildes gesichert. Beim neu selektierten Bild werden
	 * die alten Fenstereinstellungen ausgelesen und wieder dargestellt.
	 * Anschliessend wird der Inhalt der Zeichenflaeche aktualisiert.
	 * @param e Das Ereignis beim Druecken einer Maustaste auf der Zeichenflaeche.
	 */
	private void pCanvas_mouseClicked(MouseEvent e) {

		// Nachschauen, welches Bild selektiert war
		ImagePlus ip = null;

		ip = getSelectedImage();

		// tha:2000.07.17 // System.out.println("FEnsterungswerte, die gespeichert werden: " + pCenter.getValue() + "  " + pWindow.getValue());

		// Fenstereinstellung aus zuletzt gewaehltem Bild abspeichern
		if (ip != null) {
			ip.setCW(pCenter.getValue(), pWindow.getValue());
		} 

		// Besorgen des Bildes, ueber dem sich gerade der Mauszeiger befindet
		int actImage = getImageMouseIsOn(e.getX(), e.getY());

		// ausgewaehltes Bild als selktiert markieren
		pCanvas.setActiveImage(actImage);

		// Wiederherstellen der Fenstereinstellungen des neu selektierten Bildes
		ip = pCanvas.getImageStack().getPictureAtPos(actImage);
		if (ip != null) {
			pWindow.setValue(ip.getWindow());
			pCenter.setValue(ip.getCenter());
		} 

		// Aktualisierung der Zeichenflaeche
		pCanvas.repaint();
	}		// Methode pCanvas_mouseClicked
 

	/**
	 * Die Methode wird aufgerufen, wenn der Knopf zum Einzeichnen der
	 * Bildbeschriftung gedrueckt wird. Die Beschriftung wird dann ein- oder
	 * ausgeschaltet und der Inhalt der Zeichenflaeche wird neu aufgebaut.
	 */
	private void tbImageText_mouseClicked() {
		pCanvas.repaint();
	}		// Methode tbImageText_mouseClicked
 

	/**
	 * Die Methode wird aufgerufen, wenn die Aktion Datei | Oeffnen aus dem Menue
	 * aufgerufen wird. Diese Methode wird nur auf die entsprechende Methode des
	 * Oeffnen-Knopfes in der Werkzeugleiste umgeleitet.
	 */
	private void fileOpen_actionPerformed() {

		// Umleiten der Funktion auf den entsprechenden Button in der Toolleiste
		pbLoad_actionPerformed();
	}		// Methode tbImageText_mouseClicked
 

	/**
	 * Die Methode wird aufgerufen, wenn die Aktion Datei | Speichern aus dem Menue
	 * aufgerufen wird. Diese Methode wird nur auf die entsprechende Methode des
	 * Speichern-Knopfes in der Werkzeugleiste umgeleitet.
	 */
	private void fileSave_actionPerformed() {

		// Umleiten der Funktion auf den entsprechenden Button in der Toolleiste
		pbSave_actionPerformed();
	}		// Methode fileSave_actionPerformed
 

	/**
	 * Die Methode wird aufgerufen, wenn die Aktion Datei | Drucken aus dem Menue
	 * aufgerufen wird. Diese Methode wird nur auf die entsprechende Methode des
	 * Drucken-Knopfes in der Werkzeugleiste umgeleitet.
	 */
	private void filePrint_actionPerformed() {

		// Umleiten der Funktion auf den entsprechenden Button in der Toolleiste
		pbPrint_actionPerformed();
	}		// Methode filePrint_actionPerformed
 

	/**
	 * Die Methode wird aufgerufen, wenn die Aktion Datei | Beenden aus dem Menue
	 * aufgerufen wird. Die Anwendung wird beendet.
	 */
	public void fileExit_actionPerformed() {
		if (isApplication) {
			System.exit(0);
		} else {
			this.dispose();
		} 
	} 


	/**
	 * Die Methode wird aufgerufen, wenn der Knopf zum optimalen Fenstern des selektierten
	 * Bildes gedrueckt wurde. Es wird dann die entsprechende Methode fuer das
	 * selektierte Bild aufgerufen und der Zeichenflaecheninhalt neu dargestellt.
	 * Ausserdem werden die Einstellungen der Schieberegler fuer die Fensterung
	 * angepasst.
	 */
	private void pbOptWindowing_actionPerformed() {

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
			Histogramm	myHist = new Histogramm(this, myShortImage, frameRsrc.getString("vmrtframe.dialog.histogram.title") + actImage);

			myHist.setVisible(true);

		}		// if ip!=null
 
	}			// Methode pbHistogram_actionPerformed()
 

	/**
	 * Die Methode wird aufgerufen, wenn der Knopf zur Darstellung des K-Raums
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
	 * Die Methode wird aufgerufen, wenn sich der Zustand des horzontalen
	 * Scrollbalkens veraendert hat. Der Scrollbalken wird dann aktualisiert.
	 */

	/*
	 * private void sbHorScrollbar_adjustmentValueChanged() {
	 * pCanvas.repaint();
	 * pCanvas.updateScrollbar();
	 * } // Methode  sbHorScrollbar_adjustmentValueChanged()
	 */


	/**
	 * Die Methode wird aufgerufen, wenn sich der Zustand des vertikalen
	 * Scrollbalkens veraendert hat. Der Scrollbalken wird dann aktualisiert.
	 */
	private void sbVerScrollbar_adjustmentValueChanged() {
		pCanvas.repaint();
		pCanvas.updateScrollbar();
	}		// Methode  sbVerScrollbar_adjustmentValueChanged()
 

	// ////////////////////////////
	// Sonstige Methoden
	// ////////////////////////////


	/**
	 * Die Methode bestimmt aufgrund der x- und y-Koordinate des Mauszeigers
	 * auf der Zeichenflaeche, ueber welchem Bild sich der Mauszeiger gerade
	 * befindet. Dabei geht die Einstellung 1 Bild / 4 Bilder und die
	 * Bildlaufleistenposition mit in die Berechnung ein.
	 * @param x x-Koordinate des Mauszeigers.
	 * @param y y-Koordinate des Mauszeigers.
	 * @return Nummer des Bildes, ueber dem sich der Mauszeiger befindet.
	 */
	private int getImageMouseIsOn(int x, int y) {
		int bildnr;

		// Umgestellt auf vertikales Scrollen
		if (rb4Images.isSelected()) {

			// int off = sbHorScrollbar.getValue() / 256;
			int off = sbVerScrollbar.getValue() / 256;

			// bildnr = 1 + (y/256) + 2*(x/256) + 2*off;
			bildnr = 1 + 2 * (y / 256) + (x / 256) + 2 * off;
		} else {

			// int off = sbHorScrollbar.getValue()/512;
			int off = sbVerScrollbar.getValue() / 512;

			bildnr = 1 + off;
		} 

		if ((x < 0) || (x > 512) || (y < 0) || (y > 512)) {
			bildnr = -1;
		} 

		// tha:2000.07.17 // System.out.println(bildnr);
		return bildnr;
	}		// Methode getImageMouseIsOn(int x, int y)
 

	/**
	 * Die Methode erzeugt ein Intensitaetsbild aus den Rohdaten mittels der
	 * ausgewaehlten Pulssequenz. Dazu werden die Rohdaten zunaechst aus dem
	 * FileLoader-Objekt ausgelesen. Die Rohdaten werden
	 * dann an die Sequenz uebergeben und diese wird gestartet. Die Sequenz meldet,
	 * wenn sie mit der Berechnung fertig ist. Sie ruft dann die Methode
	 * drawCreatedIntensityImage auf.
	 * Die Methode wird angestossen, wenn der 'Berechnen'-Knopf der Pulssequenz
	 * betaetigt wurde.
	 * @param sequence Die Pulssequenz, die zur Erzeugung des Intensitaetsbildes
	 * verwendet werden soll.
	 */
	public void createIntensityImage(Pulsesequence sequence) {

		// Physikalische Rohdaten an die Sequenz uebergeben
		// und Berechnung starten.
		try {
			sequence.setRawData(myFileLoader);
		} catch (Exception e) {
			ErrorMessage.showMessage(e);
			return;
		} 
		sequence.setMainFrame(this);

		// Starten der Berechnung mittels der Pulssequenz. Da es sich bei den
		// Pulssequenzen um Threads handelt, muss die start-Methode aufgerufen werden.
		sequence.start();
	}		// Methode createIntensityImage
 

	/**
	 * Die Methode stellt ein von einer Pulssequenz erzeugtes Intensitaetsbild auf
	 * der Zeichenflaeche dar. Dazu wird das Intensitaetsbild zunaechst aus der
	 * Pulssequnz ausgelesen und dann in ein DcmImage-Objekt umgewandet. Damit
	 * enthaelt das Bild die geleichen zusaetzlichen Bildinformationen wie die
	 * Rohdatenmatrizen. Einige Werte, wie z.B. die Fensterungswerte werden
	 * natuerlich entsprechend neu gesetzt. Spaeter ist es somit moeglich, das Bild im
	 * DICOM-Format abzuspeichern. Aber zunaecht wird das DcmImage-Objekt erstmal
	 * in einem ImagePlus-Objekt gespeichert.
	 * @param sequence Die Pulssequenz, aus der die Intensitaetsmatrix ausgelesen
	 * werden soll.
	 */
	public ImagePlus drawCreatedIntensityImage(Pulsesequence sequence) {

		// 12Bit-Matrix aus der Pulssequenz auslesen
		short[]		shortIntensityImage = sequence.getShort12BitMatrix();

		// DcmImage aus der Bildmatrix und dem DicomDataObject des FileLoaders erzeugen
		DcmImage	dcmimg = null;

		try {
			dcmimg = new DcmImage(myFileLoader.getDcmDataObject());
			dcmimg.pixel16 = shortIntensityImage;
			dcmimg.updateAWTImage();
		} catch (Exception err) {
			err.printStackTrace();
		} 

		// ImagePlus-Objekt erzeugen. Dies geschieht in der Klasse MyPanel.
		// ImagePlus ip = pCanvas.createNewImage(intensityImage);
		ImagePlus			ip = pCanvas.createNewImage(dcmimg);

		// DDo im ImagePlus abspeichern
		DcmDataObject mynewddo = new DcmDataObject();

		mynewddo = myFileLoader.getDcmDataObject().getCopyOfMe();
		ip.setDDO(mynewddo);

		// Nun werden einige Dicom-Parameter im neuen Bild abgespeichert

		// Fensterwerte in Image abspeichern. Die Werte werden beim naechsten repaint
		// mit beruecksichtigt

		// >>> tha 2000.4.3: Optimale Windoweinstellung
		// ip.setCW(pCenter.getValue(), pWindow.getValue());
		// Bild optimal Fenstern
		ip.optimalWindowing();

		// Fensterungseinstellungen der Slider aktualisieren
		// cschalla 00.4.20: so gehts nicht: Slider zeigen die Fensterung des
		// selektierten Bildes. Das neue ist aber nicht selektiert
		ImagePlus selip = getSelectedImage();

		pWindow.setValue(selip.getWindow());
		pCenter.setValue(selip.getCenter());

		// <<<

		// Erzeugungsdatum und Uhrzeit im Bild speichern
		ip.setCurrentDate();

		// ip.setComments(sequence.getComments());

		// Neuzeichnen des Canvas
		pCanvas.repaint();

		return ip;
	}		// Methode drawCreatedIntensityImage
 

	/**
	 * Die Methode liefert eine Referenz auf die Fortschrittsanzeige zurueck.
	 * @return Eine Referenz auf die Fortschrittsanzeige.
	 */
	public JProgressBar getProgressBar() {
		return prbarProgress;
	}		// Methode getProgressBar
 

	/**
	 * Die Methode liefert den eingestellten Simulationszeitfaktor zurueck.
	 * @return Der vom Benutzer eingestellte Simulationszeitfaktor.
	 */
	public int getTimeFactor() {
		return pTimeFactor.getValue();
	}		// Methode getTimeFactor
 

	/**
	 * Die Methode liefert das ImagePlus-Objekt des zur Zeit selektierten Bildes zurueck.
	 * @return Das ImagePlus-Objekt des selektierten Bildes.
	 */
	public ImagePlus getSelectedImage() {

		int				actImage = pCanvas.getActiveImage();

		// tha:2000.07.17 // System.out.println("Selektiertes Bild: " + actImage);
		ImagePlus ip = null;

		if (actImage >= 0) {
			ip = pCanvas.getImageStack().getPictureAtPos(actImage);
		} 
		return ip;
	}		// Methode getSelectedImage
 

	/**
	 * Die Methode liefert das FileLoader-Objekt zurueck, in dem die Rohdatenmatrizen
	 * und einige zusaetzliche Informationen stehen (wie z.B. Pixelabstand).
	 * @return Das aktuelle FileLoader-Objekt.
	 */
	public FileLoader getFileLoader() {
		return myFileLoader;
	}		// Methode getFileLoader
 

	/**
	 * Die Methode setzt den Text der Statusleiste auf den uebergebenen Text.
	 * @param txt Der Text, der in der Statusleiste angezeigt werden soll.
	 */
	public void setStatusBar(String txt) {
		statusBar.setText(txt);
	}		// Methode setStatusBar
 

	/**
	 * Die Methode liefert den Text der Statusleiste zurueck.
	 * @return Der Text der Statusleiste.
	 */
	public String getStatusText() {
		return statusBar.getText();
	}		// Methode getStatusBar
 

	/**
	 * Die Methode setzt den Wert der Fortschrittsanzeige auf den uebergebenen Wert.
	 * @param val Der Wert, auf den die Fortschrittsanzeige gesetzt werden soll.
	 */
	public void setProgressBar(int val) {
		prbarProgress.setValue(val);
	}		// Methode setStatusBar
 

	/**
	 * Die Methode liefert eine Referenz auf die Oberflaechenklasse des aktuell
	 * ausgewaehlten Artefakt-Simulators zurueck.
	 * @return Die Oberflaechenklasse des selektierten Artefakt-Simulators.
	 */
	public ArtefactUI getSelectedArtefactUI() {
		return cbArtefacts.getSelectedArtefactUI();
	}		// Methode getSelectedArtefactUI
 

	/**
	 * Die Methode liest einige Voreinstellungen aus der Datei
	 * preferences.properties ein.
	 */
	public void loadPreferences() {
		try {
			preferences.load(new FileInputStream("vmrt_preferences.properties"));
		} catch (Exception e) {
			System.out.println(frameRsrc.getString("vmrtframe.exception.preferences.open"));
			return;
		}		// try-catch
 
		// LineNumberReader	numRead = null;
		// boolean						ok = true;
		// 
		// // Versuche, den neuen Eingabestream zu oeffnen
		// try {
		// FileReader	streamIn = new FileReader("vmrt_preferences.properties");
		// 
		// numRead = new LineNumberReader(streamIn);
		// } catch (Exception e) {
		// System.out.println(frameRsrc.getString("vmrtframe.exception.preferences.open"));
		// return;
		// }		// try-catch
		// String e = null;
		// 
		// do {
		// try {
		// e = numRead.readLine();
		// } catch (Exception err) {
		// return;
		// }
		// if (e == null) {
		// break;
		// }
		// StringTokenizer token = new StringTokenizer(e, "=");
		// String					key = token.nextToken();
		// String					value = token.nextToken();
		// 
		// value = value.replace('"', ' ').trim();
		// try {
		// preferences.put(key, value);
		// } catch (Exception err) {
		// System.out.println(frameRsrc.getString("vmrtframe.exception.preferences.read"));
		// }
		// } while (e != null);
		// try {
		// numRead.close();
		// } catch (Exception err) {
		// System.out.println(frameRsrc.getString("vmrtframe.exception.preferences.close"));
		// }
	} 


	/**
	 * Die Methode wird beim Beenden der Anwendung aufgerufen und speichert dann
	 * einige Voreinstellungen wie z.B. den Dateipfad, aus dem zuletzt ein
	 * Rohdatensatz geladen wurde.
	 */
	public void savePreferences() {
		try {
			preferences.store(new FileOutputStream("vmrt_preferences.properties"), "");
		} catch (Exception e) {
			System.out.println(frameRsrc.getString("vmrtframe.exception.preferences.write"));
		} 

		// BufferedWriter	bufWriter = null;
		// FileWriter			streamOut = null;
		// boolean					ok = true;
		// 
		// // Ausgabestream zu oeffnen
		// try {
		// streamOut = new FileWriter("vmrt_preferences.properties");
		// bufWriter = new BufferedWriter(streamOut);
		// } catch (Exception e) {
		// System.out.println(frameRsrc.getString("vmrtframe.exception.preferences.open"));
		// }
		// Enumeration iterator = preferences.propertyNames();
		// 
		// while (iterator.hasMoreElements()) {
		// String	key = (String) iterator.nextElement();
		// String	value = (String) preferences.get(key);
		// 
		// try {
		// streamOut.write(key);
		// streamOut.write("=\"");
		// streamOut.write(value + "\"\n");
		// } catch (Exception err) {
		// System.out.println(frameRsrc.getString("vmrtframe.exception.preferences.write"));
		// }
		// try {
		// streamOut.flush();
		// } catch (Exception err) {
		// System.out.println(frameRsrc.getString("vmrtframe.exception.preferences.write"));
		// }
		// try {
		// streamOut.close();
		// } catch (Exception err) {
		// System.out.println(frameRsrc.getString("vmrtframe.exception.preferences.close"));
		// }
		// streamOut = null;
		// }
	}		// savePreferences()
 

}










/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

