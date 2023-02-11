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
 * Diese Klasse stellt das Fenster zur animierten Darstellung des geladenen
 * Datensatzes dar. Es ermoeglicht, die Einzelbilder manuell oder automatisch
 * nacheinander darzustellen. Dabei ist es auch moeglich, andere Schichtfuehrungen
 * zu waehlen, als die durch den Originaldatensatz vorgegebene. Ausserdem koennen die
 * Animationsgeschwindigkeit und die Bilder, die in die Animation mit einbezogen
 * werden sollen, eingestellt werden.
 * Die Klasse ist in einem Thread implementiert, damit sie die Anwendung nicht
 * blockiert.
 * 
 * @author   Christian Schalla
 * @author   Andreas Truemper
 * @version  1.0, 15. Oktober 1999
 */
public class AnimationFrame extends JFrame implements Runnable {


	/**
	 * Der Thread, in dem die Animation ablaeuft.
	 */
	private Thread						myThread;


	/**
	 * Eine Rueckreferenz zum Hauptfenster, von dem aus die Animation aufgerufen
	 * wurde.
	 */
	private ViewerFrame				mainFrame;


	/**
	 * Das aktuelle Schichtbild, daß dargestellt werden soll.
	 */
	private Image							myImage = null;


	/**
	 * Der Stapel mit allen Schichtbildern der Originalschichtfuehrung.
	 */
	private ImageStack				is = null;


	/**
	 * Ein Flag, dass anzeigt, ob auch die Bedienelemente des Fensters neu
	 * gezeichnet werden muessen. Das ist z.B. dann der Fall, wenn das Fenster
	 * bewegt wurde. Ansonsten wird nur das Bild neu gezeichnet.
	 */
	private boolean						paintAll = true;


	/**
	 * Modus-Flag fuer das einmalige Abspielen des Bildstapels.
	 */
	final private static int	ONE_TIME = 1;


	/**
	 * Modus-Flag fuer wiederholtes Abspielen des Bildstapels. Dabei wird immer
	 * bei Bild 1 begonnen.
	 */
	final private static int	LOOP = 2;


	/**
	 * Modus-Flag fuer wiederholtes Abspielen des Bildstapels. Wenn des Ende des
	 * Stapels erreicht ist, wird rueckwaerts abgespielt.
	 */
	final private static int	LOOPBACK = 3;


	/**
	 * Modus-Flag fuer die manuelle Steuerung der Animation.
	 */
	final private static int	MANUAL = 4;


	/**
	 * Zeitdauer, die ein Bild eingeblendet bleibt, bis das naechste folgt.
	 */
	private int								sleep = 0;


	/**
	 * Das Bild, mit dem die Animation begonnen wird .
	 */
	private int								startImage = 1;


	/**
	 * Das Bild, bei dem die Animation endet.
	 */
	private int								stopImage = 1;


	/**
	 * Der aktuell eingestellte Animationsmodus.
	 */
	private int								mode = LOOP;


	/**
	 * Icon fuer den 'Play'-Knopf.
	 */
	private ImageIcon					iPlay;


	/**
	 * Icon fuer den 'Pause'-Knopf.
	 */
	private ImageIcon					iPause;


	/**
	 * Icon fuer den 'Stop'-Knopf.
	 */
	private ImageIcon					iStop;


	/**
	 * Icon fuer den Modusknopf 'Animation einfach abspielen'.
	 */
	private ImageIcon					iOneWay;


	/**
	 * Icon fuer den Modusknopf 'Animation wiederholt abspielen'.
	 */
	private ImageIcon					iLoop;


	/**
	 * Icon fuer den Modusknopf 'Animation wiederholt abspielen (vor-zurueck)'.
	 */
	private ImageIcon					iLoopBack;


	/**
	 * Icon fuer den Projektionsknopf 'Originalschichten'.
	 */
	private ImageIcon					iOrigProj;


	/**
	 * Icon fuer den Projektionsknopf '90°-Projektion 1'.
	 */
	private ImageIcon					iProj1;


	/**
	 * Icon fuer den Projektionsknopf '90°-Projektion 2'.
	 */
	private ImageIcon					iProj2;

	// Erzeugen der Bedienelemente


	/**
	 * Schieberegler zur Einstellung der Animationsgeschwindigkeit.
	 */
	private SliderPanel				pFps;


	/**
	 * Rahmen fuer den Schieberegler zur manuellen Steuerung der Animation.
	 */
	private JPanel						pSliderPanel = new JPanel();


	/**
	 * Beschriftung fuer den Schieberegler zur manuellen Animationssteuerung.
	 */
	private JLabel						lSliderLabel = new JLabel();


	/**
	 * Schieberegler zur manuellen Steuerung der Animation.
	 */
	private JSlider						slImage = new JSlider();


	/**
	 * Beschriftung fuer den Animationsmodus.
	 */
	private JLabel						lAnimModus = new JLabel();


	/**
	 * Knopf fuer den Animationsmodus 'Animation einfach abspielen'.
	 */
	private JToggleButton			tbAnimMode1 = new JToggleButton();


	/**
	 * Knopf fuer den Animationsmodus 'Animation wiederholt abspielen'.
	 */
	private JToggleButton			tbAnimMode2 = new JToggleButton();


	/**
	 * Knopf fuer den Animationsmodus 'Animation wiederholt abspielen (vor-zurueck)'.
	 */
	private JToggleButton			tbAnimMode3 = new JToggleButton();


	/**
	 * Knopf fuer den Animationsmodus 'manuelle Animationssteuerung'.
	 */
	private JToggleButton			tbAnimMode4 = new JToggleButton();


	/**
	 * Textfelder zur Einstellung, welche Bilder in die Animation einbezogen werden sollen.
	 */
	private LabTFLabTFPanel		pImages;


	/**
	 * Beschriftung fuer den Projektionsmodus.
	 */
	private JLabel						lProjection = new JLabel();


	/**
	 * Knopf fuer den Projektionsmodus 'Originalschichten'.
	 */
	private JToggleButton			tbProj1 = new JToggleButton();


	/**
	 * Knopf fuer den Projektionsmodus '90°-Projektion 1'.
	 */
	private JToggleButton			tbProj2 = new JToggleButton();


	/**
	 * Knopf fuer den Projektionsmodus '90°-Projektion 2'.
	 */
	private JToggleButton			tbProj3 = new JToggleButton();


	/**
	 * Knopf zum Starten der Animation.
	 */
	private JButton						pbAnimStart = new JButton();


	/**
	 * Knopf zum Stoppen der Animation.
	 */
	private JButton						pbAnimStop = new JButton();


	/**
	 * Knopf zum Anhalten der Animation.
	 */
	private JToggleButton			tbAnimPause = new JToggleButton();


	/**
	 * Standardkonstruktor. Er speichert eine Ruechreferenz auf das Hauptfenster
	 * des DICOM-Viewers. Außerdem wird die jbInit-Methode aufgerufen, die die
	 * Bedienelemente darstellt.
	 * @param fra Eine Rueckreferenz auf das aufrufende Fenster.
	 */
	public AnimationFrame(Frame fra) {
		mainFrame = (ViewerFrame) fra;
		is = mainFrame.pCanvas.getImageStack();
		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		is = null;
	}		// Konstruktor AnimationFrame(Frame fra)


	/**
	 * Die Method erzeugt einen neuen Thread, setzt dessen Prioritaet auf maximal
	 * und startet die Animation.
	 */
	public void start() {
		if (myThread == null) {
			myThread = new Thread(this);
			myThread.setPriority(Thread.MAX_PRIORITY - 1);
			myThread.start();
		}		// if
 	 }		// Methode start
 

	/**
	 * Die Methode entscheidet aufgrund der aktuellen Einstellungen der
	 * Bedienelemente, welches Bild als naechstes dargestellt werden muss. Die
	 * Methode besorgt dieses Bild, ruft die repaint-Methode auf und wartet eine
	 * gewisse Zeit, bis schliesslich das naechte Bild berechnet und dargestellt
	 * wird.
	 */
	public void run() {
		int			num = 0;
		boolean forward = true;

		if ((mode == ONE_TIME) || (mode == LOOP)) {
			num = startImage;
		} else {
			num = stopImage;
		} 

		for (; ; )	// Endlosschleife (wird durch Stop-Button abgebrochen)
		 {

			// Wenn 90°-Projektion ausgewaehlt, dann wird diese nun berechnet
			if (tbProj2.isSelected()) {

				// Berechnen des naechsten Projektionsbildes
				ImagePlus ip = mainFrame.calcProjection1(num, pImages.getValue1(), pImages.getValue2(), true);

				// Zur besseren darstellung wird das Bild optimal gefenstert
				ip.optimalWindowing();

				// Besorgen des AWT-Bildes
				myImage = ip.getAWTImage();
			} else if (tbProj3.isSelected()) {

				// Berechnen des naechsten Projektionsbildes
				ImagePlus ip = mainFrame.calcProjection2(num, pImages.getValue1(), pImages.getValue2(), true);

				// Zur besseren darstellung wird das Bild optimal gefenstert
				ip.optimalWindowing();

				// Besorgen des AWT-Bildes
				myImage = ip.getAWTImage();
			} else {	// ... wenn die Originalschichtfuehrung gewaehlt ist ...

				// Besorgen des naechsten AWT-Bildes
				ImagePlus ip = is.getPictureAtPos(num);

				myImage = ip.getAWTImage();
			}					// if
 
			// Neuzeichnen des Zeichenflaecheninhalts (neues Bild)
			repaint();

			if (forward) {
				num++;
			} else {
				num--;
			} 

			// Nun wird aufgrund des Animationsmodus entschieden, ob bzw. welches
			// Bild als naechste dargestellt wird.
			if ((num > stopImage) || (num < startImage)) {
				if (mode == ONE_TIME) {
					pbAnimStop_actionPerformed();
					break;
				} 
				if (mode == LOOP) {
					num = startImage;
					forward = true;
				} 
				if (mode == LOOPBACK) {
					if (forward) {
						forward = false;
						num = stopImage;
					} else {
						forward = true;
						num = startImage;
					}			// if-else
 				 }			// if
 			 }				// if
 
			// Warten (so dass die voreingestellte Bildrate erreicht wird)
			try {
				Thread.sleep((long) (1000 / ((double) pFps.getValue() + 0.5)));
			} catch (Exception err) {}
		}						// for (Endloasschleife)
 
	}							// Methode run
 

	/**
	 * Die Methode beendet den aktuellen Thread.
	 */
	public void stop() {
		is = null;
		if (!((mode == this.ONE_TIME) && (tbProj1.isSelected()))) {
			myThread.stop();
		} 
		myThread = null;
	} 


	/**
	 * Die Methode richtet die Bedienelemente ein und stellt diese dar.
	 */
	private void jbInit() throws Exception {

		// Laden der Icons
		iPlay = new ImageIcon(AnimationFrame.class.getResource("play.gif"));
		iPause = new ImageIcon(AnimationFrame.class.getResource("pause.gif"));
		iStop = new ImageIcon(AnimationFrame.class.getResource("stop.gif"));
		iOneWay = new ImageIcon(AnimationFrame.class.getResource("oneway.gif"));
		iLoop = new ImageIcon(AnimationFrame.class.getResource("loop.gif"));
		iLoopBack = new ImageIcon(AnimationFrame.class.getResource("loopback.gif"));
		iOrigProj = new ImageIcon(AnimationFrame.class.getResource("origproj.gif"));
		iProj1 = new ImageIcon(AnimationFrame.class.getResource("projektion1.gif"));
		iProj2 = new ImageIcon(AnimationFrame.class.getResource("projektion2.gif"));

		// einige Fenstereinstellungen vornehmen
		this.getContentPane().setLayout(null);
		this.setLocation(100, 100);
		this.setTitle("Animation");
		this.setSize(new Dimension(323, 465));
		this.setResizable(false);

		// slider fuer die manuelle Animationssteuerung einrichten
		pSliderPanel.setLayout(null);
		pSliderPanel.setBounds(260, 0, 55, 256);
		pSliderPanel.setBorder(BorderFactory.createEtchedBorder());
		this.getContentPane().add(pSliderPanel);
		lSliderLabel.setText("Bild:");
		lSliderLabel.setBounds(3, 0, 55, 20);
		pSliderPanel.add(lSliderLabel);
		slImage.setBounds(2, 20, 50, 233);
		slImage.setOrientation(JSlider.VERTICAL);
		slImage.setMinimum(1);
		slImage.setMaximum(1);
		slImage.setValue(1);
		slImage.setPaintLabels(true);
		slImage.setEnabled(false);
		pSliderPanel.add(slImage);
		slImage.addChangeListener(new ChangeListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void stateChanged(ChangeEvent e) {
				slImage_stateChanged();
			} 

		});

		// Slider fuer die Animationsgeschwindigkeit einrichten */
		pFps = new SliderPanel("Geschwindigkeit:", 0, 30, "fps");
		pFps.setTextRange(0, 100);
		pFps.setBounds(new Rectangle(2, 347, 313, 60));
		pFps.setToolTip("Einstellung fuer die Anzahl der Bilder pro Sekunde");
		pFps.setTextRange(1, 30);
		pFps.setTickSpacing(5, 0);
		this.getContentPane().add(pFps);
		pFps.fillPanel();
		pFps.setValue(10);

		// Einrichten des Labels fuer den Modus
		lAnimModus.setText("Modus:");
		lAnimModus.setBounds(2, 260, 70, 20);
		this.getContentPane().add(lAnimModus);

		// Einrichten der ToggleButtons fuer den Modus
		tbAnimMode1.setIcon(iOneWay);
		tbAnimMode1.setBounds(82, 260, 57, 25);
		tbAnimMode1.addActionListener(new ActionListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void actionPerformed(ActionEvent e) {
				mode = ONE_TIME;
				slImage.setEnabled(false);
			} 

		});
		this.getContentPane().add(tbAnimMode1);

		tbAnimMode2.setIcon(iLoop);
		tbAnimMode2.setBounds(142, 260, 57, 25);
		tbAnimMode2.setSelected(true);
		tbAnimMode2.addActionListener(new ActionListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void actionPerformed(ActionEvent e) {
				mode = LOOP;
				slImage.setEnabled(false);
			} 

		});
		this.getContentPane().add(tbAnimMode2);

		tbAnimMode3.setIcon(iLoopBack);
		tbAnimMode3.setBounds(202, 260, 55, 25);
		tbAnimMode3.addActionListener(new java.awt.event.ActionListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void actionPerformed(ActionEvent e) {
				mode = LOOPBACK;
				slImage.setEnabled(false);
			} 

		});
		this.getContentPane().add(tbAnimMode3);

		tbAnimMode4.setText("M");
		tbAnimMode4.setBounds(260, 260, 55, 25);
		tbAnimMode4.addActionListener(new ActionListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void actionPerformed(ActionEvent e) {
				mode = MANUAL;
				slImage.setEnabled(true);
			} 

		});
		this.getContentPane().add(tbAnimMode4);

		// Gruppieren der ToggleButtons
		ButtonGroup bgAnimMode = new ButtonGroup();

		bgAnimMode.add(tbAnimMode1);
		bgAnimMode.add(tbAnimMode2);
		bgAnimMode.add(tbAnimMode3);
		bgAnimMode.add(tbAnimMode4);

		// Einrichten einiger Window-Listener
		this.addWindowListener(new WindowAdapter() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void windowActivated(WindowEvent e) {
				this_windowActivated(e);
			} 

		});
		this.addComponentListener(new ComponentAdapter() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void componentMoved(ComponentEvent e) {
				this_componentMoved(e);
			} 

		});

		// Einrichten der Bedienelemente zur Angabe der Bilder, die in die Animation
		// einbezogen werden sollen
		pImages = new LabTFLabTFPanel("Bilder:", "1", "bis", "1");
		pImages.setBounds(2, 322, 270, 20);
		pImages.setRange1(1, is.getStackSize());
		pImages.setRange2(1, is.getStackSize());
		this.getContentPane().add(pImages);
		pImages.fillPanel();

		// Einrichten der Bedienelemente fuee den Projektionsmodus
		lProjection.setText("Projektion:");
		lProjection.setBounds(2, 293, 70, 20);
		this.getContentPane().add(lProjection);

		tbProj1.setIcon(iOrigProj);
		tbProj1.setBounds(82, 291, 57, 25);
		tbProj1.setSelected(true);
		this.getContentPane().add(tbProj1);
		tbProj1.addActionListener(new java.awt.event.ActionListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void actionPerformed(ActionEvent e) {
				tbProj1_actionPerformed();
			} 

		});

		tbProj2.setIcon(iProj1);
		tbProj2.setBounds(142, 291, 57, 25);
		this.getContentPane().add(tbProj2);
		tbProj2.addActionListener(new java.awt.event.ActionListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void actionPerformed(ActionEvent e) {
				tbProj2_actionPerformed();
			} 

		});

		tbProj3.setIcon(iProj2);
		tbProj3.setBounds(202, 291, 55, 25);
		this.getContentPane().add(tbProj3);
		tbProj3.addActionListener(new java.awt.event.ActionListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void actionPerformed(ActionEvent e) {
				tbProj3_actionPerformed();
			} 

		});

		// Gruppieren der Knoepfe fuer den projektionsmodus
		ButtonGroup bgProjections = new ButtonGroup();

		bgProjections.add(tbProj1);
		bgProjections.add(tbProj2);
		bgProjections.add(tbProj3);

		// Einrichten der Start- / Pause- / Stop-Buttons
		pbAnimStart.setIcon(iPlay);
		pbAnimStart.setBounds(2, 411, 90, 25);
		pbAnimStart.addActionListener(new java.awt.event.ActionListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void actionPerformed(ActionEvent e) {
				pbAnimStart_actionPerformed();
			} 

		});
		this.getContentPane().add(pbAnimStart);

		tbAnimPause.setIcon(iPause);
		tbAnimPause.setBounds(113, 411, 90, 25);
		tbAnimPause.addActionListener(new java.awt.event.ActionListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void actionPerformed(ActionEvent e) {
				tbAnimPause_actionPerformed();
			} 

		});
		this.getContentPane().add(tbAnimPause);

		pbAnimStop.setIcon(iStop);
		pbAnimStop.setBounds(222, 411, 90, 25);
		pbAnimStop.addActionListener(new java.awt.event.ActionListener() {


			/**
			 * Method declaration
			 * 
			 * 
			 * @param e
			 * 
			 * @see
			 */
			public void actionPerformed(ActionEvent e) {
				pbAnimStop_actionPerformed();
			} 

		});
		this.getContentPane().add(pbAnimStop);

		// Einrichten des Slider zur manuellen Animationssteuerung
		if (is != null) {
			int size = is.getStackSize();

			pImages.setValue2("" + size);
			slImage.setMaximum(size);
			int val = (slImage.getMaximum() - slImage.getMinimum()) / 10;

			slImage.setMajorTickSpacing(val);
			if (size == 0) {
				pImages.setValue1("" + size);
				slImage.setMaximum(0);
				slImage.setMinimum(0);
			}		// if
 		 }		// if
 	 }			// Methode jbInit
 

	/**
	 * Die Methode wird aufgerufen, wenn der Start-Knopf gedrueckt wurde. Sie liest
	 * die aktuellen Einstellungen der Bedienelemente aus, speichert sie in
	 * Instanzvariablen und sperrt anschliessend einige Bedienelemente. Dann wird
	 * die start-Methode aufgerufen und somit die Animation gestartet.
	 */
	private void pbAnimStart_actionPerformed() {

		// Sperren der Start-Knopfes
		pbAnimStop.setEnabled(true);

		// Aktivieren des Stop-Knopfes
		pbAnimStart.setEnabled(false);

		// Aktivieren der Projektionsknoepfe
		tbProj1.setEnabled(false);
		tbProj2.setEnabled(false);
		tbProj3.setEnabled(false);

		// Sperren der Eingabefelder fuer die Bildnummer, die in die Animation
		// einbezogen werden sollen
		pImages.getTextFieldReference1().setEnabled(false);
		pImages.getTextFieldReference2().setEnabled(false);

		// Besorgen des Bildstapels
		is = mainFrame.pCanvas.getImageStack();

		// Auslesen der Animationsgeschwindigkeit
		sleep = 1000 / pFps.getValue();

		// Auslesen des Animationsmodus
		if (tbAnimMode1.isSelected()) {
			mode = ONE_TIME;
		} 
		if (tbAnimMode2.isSelected()) {
			mode = LOOP;
		} 
		if (tbAnimMode3.isSelected()) {
			mode = LOOPBACK;
		} 
		if (tbAnimMode4.isSelected()) {
			mode = MANUAL;

			// Auslesen der Bilder, die in die Animation einbezogen werden sollen
		} 
		try {
			if (tbProj2.isSelected() || tbProj3.isSelected()) {
				startImage = 0;
				stopImage = 255;
			} else {
				startImage = pImages.getValue1();
				stopImage = pImages.getValue2();
			} 
		} catch (Exception err) {}

		// Starten der Animation
		this.start();
	}		// Methode pbAnimStart_actionPerformed()
 

	/**
	 * Die Methode wird aufgerufen, wenn der Pause-Knopf gedrueckt wurde. Die Methode
	 * entscheidet, ob die Pausefunktion nun aktiviert oder deaktiviert ist und
	 * versetzt dem entsprechend den Thread in den Schlaf oder erweckt ihn wieder
	 * zum Leben.
	 */
	private void tbAnimPause_actionPerformed() {
		if (tbAnimPause.isSelected()) {
			myThread.suspend();
		} else {
			myThread.resume();
		} 
	}		// Methode tbAnimPause_actionPerformed()
 

	/**
	 * Die Methode wird aufgerufen, wenn der Stop-Knopf gedrueckt wurde. Die
	 * Animation wird gestoppt und die Bedienelemente werden wieder in den
	 * Bereitschaftszustand versetzt.
	 */
	private void pbAnimStop_actionPerformed() {

		// Zuruecksetzen der Bedienelemente in den Bereitschaftzustand
		pbAnimStart.setEnabled(true);
		pbAnimStop.setEnabled(false);
		tbAnimPause.setSelected(false);
		pImages.getTextFieldReference1().setEnabled(true);
		pImages.getTextFieldReference2().setEnabled(true);

		tbProj1.setEnabled(true);
		tbProj2.setEnabled(true);
		tbProj3.setEnabled(true);

		// Stoppen der Animation
		this.stop();
	}		// Methode pbAnimStop_actionPerformed()
 

	/**
	 * Die Methode zeichnet das aktuell darzustellende Bild in die Zeichenflaeche
	 * ein und aktualisiert ggf. auch die Bedienelemente.
	 * @param g Der Graphikkontext.
	 */
	public void paint(Graphics g) {

		// Wenn auch die Bedienelemente neu gezeichnet werden muessen (z.B. nach
		// Fensterverschiebung, wird die paint-Methode der Superklasse
		// aufgerufen
		if (paintAll) {
			super.paint(g);
		}		// if
 
		paintAll = false;

		if (myImage != null) {

			// Einzeichnen des aktuellen Bildes
			g.drawImage(myImage, 4, 23, 256, 256, this);
		} else {

			// Einzeichnen eines durchgestrichenen Quadrats
			g.drawRect(4, 23, 256, 256);
			g.drawLine(4, 23, 260, 279);
			g.drawLine(260, 23, 4, 279);
		}		// if-else
 
	}			// Methode paint(Graphics g)
 

	/**
	 * Die Methode wird aufgerufen, wenn des Animationsfenster bewegt wurde. Sie
	 * setzt dann ein Flag, so dass auch die Bedienelemente neu gezeichnet werden.
	 * @param e Das Ereignis, das bei Fensterbewegung ausgeloest wird.
	 */
	private void this_componentMoved(ComponentEvent e) {
		paintAll = true;
		repaint();
	}		// this_componentMoved(ComponentEvent e)
 

	/**
	 * Die Methode wird aufgerufen, wenn das Animationsfenster aktiviert wurde. Sie
	 * setzt dann ein Flag, so dass auch die Bedienelemente neu gezeichnet werden.
	 * @param e Das Ereignis, das bei Aktivierung des Fensters ausgeloest wird.
	 */
	private void this_windowActivated(WindowEvent e) {
		paintAll = true;
		repaint();
	}		// Methode this_windowActivated(WindowEvent e
 

	/**
	 * Die Methode wird aufgerufen, wenn sich der Status des Schiebereglers zur
	 * manuellen Animationssteuerung aendert. In Abhaengigkeit von der Einstellung
	 * des Projektionsmodus wird dann das benoetigte Bild ausgelesen oder neu
	 * berechnet.
	 */
	private void slImage_stateChanged() {

		// Aktuelle Einstellung des Sliders auslesen
		int actimg = slImage.getValue();

		// Wenn Origianlschichtfuehrung ausgewaehlt, dann besorge das naechste Bild
		// aus dem Stack
		if (tbProj1.isSelected()) {
			myImage = mainFrame.pCanvas.getImageStack().getPictureAtPos(actimg).getAWTImage();
		} 

		// Wenn 90°-Projektion eingestellt, dann berechne das gewuenschte Bild der
		// Projektion und fenstere es
		if (tbProj2.isSelected()) {
			ImagePlus ip = mainFrame.calcProjection1(actimg - 1, pImages.getValue1(), pImages.getValue2(), true);

			ip.optimalWindowing();
			myImage = ip.getAWTImage();
		} 

		// Wenn 90°-Projektion eingestellt, dann berechne das gewuenschte Bild der
		// Projektion und fenstere es
		if (tbProj3.isSelected()) {
			ImagePlus ip = mainFrame.calcProjection2(actimg - 1, pImages.getValue1(), pImages.getValue2(), true);

			ip.optimalWindowing();
			myImage = ip.getAWTImage();
		} 

		// Neueichnen des Canvasinhalts
		repaint();
	}		// Methode slImage_stateChanged(ChangeEvent e)
 

	/**
	 * Die Methode wird aufgerufen, wenn der Projektionsmodus fuer die
	 * Originalschichtfuehrung ausgewaehlt wurde. Die Methode setzt nur die
	 * Beschriftung des Schiebereglers fuer die manuelle Animationssteuerung neu.
	 */
	private void tbProj1_actionPerformed() {
		slImage.setMinimum(pImages.getValue1());
		slImage.setMaximum(pImages.getValue2());
		int val = (slImage.getMaximum() - slImage.getMinimum()) / 10;

		slImage.setMajorTickSpacing(val);
	}		// Methode tbProj1_actionPerformed()
 

	/**
	 * Die Methode wird aufgerufen, wenn der Projektionsmodus fuer die erste
	 * 90°-Projektion ausgewaehlt wurde. Die Methode setzt nur die
	 * Beschriftung des Schiebereglers fuer die manuelle Animationssteuerung neu.
	 */
	private void tbProj2_actionPerformed() {
		slImage.setMinimum(1);
		slImage.setMaximum(256);
		int val = (slImage.getMaximum() - slImage.getMinimum()) / 10;

		slImage.setMajorTickSpacing(val);
	}		// Methode tbProj2_actionPerformed()
 

	/**
	 * Die Methode wird aufgerufen, wenn der Projektionsmodus fuer die zweite
	 * 90°-Projektion ausgewaehlt wurde. Die Methode setzt nur die
	 * Beschriftung des Schiebereglers fuer die manuelle Animationssteuerung neu.
	 */
	private void tbProj3_actionPerformed() {
		slImage.setMinimum(1);
		slImage.setMaximum(256);
		int val = (slImage.getMaximum() - slImage.getMinimum()) / 10;

		slImage.setMajorTickSpacing(val);
	}		// Methode tbProj3_actionPerformed()
 
}







/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

