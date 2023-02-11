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
import java.awt.image.*;
import java.util.Random;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.imageio.*;
import javax.imageio.stream.*;

import virtual.tools.*;

import rad.dicom.dcm.*;
import rad.dicom.ima.*;


/**
 * Die Klasse laedt einen Rohdatensatz aus Dateien.
 * <DL><DT><B>Modifications: </B><DD>
 * Thomas Hacklaender 2006.4.11:
 * Parameterbilder koennen wahlweise DICOM oder 16-Bit TIFF Bilder sein.<br>
 * Thomas Hacklaender 2000.8.14:
 * Die Methode File getCaseInfoFile zugunsten von URL getCaseInfoURL()
 * ausgetauscht.<br>
 * Thomas Hacklaender 2000.8.14:
 * In loadReferenceData() wird idxParent = null gesetzt, da es sich um keine
 * reale Datei handelt.<br>
 * Thomas Hacklaender 2000.7.14:
 * .idx-Datei als Java Property-Datei umdefiniert:<br>
 * <ul>
 * <li> name.t1 = Filename der T1 Rohdatenmatrix </li>
 * <li> name.t2 = Filename der T2 Rohdatenmatrix </li>
 * <li> name.pd = Filename der Protonendichte Rohdatenmatrix </li>
 * <li> name.sz = Filename der Suszeptibilitäts Rohdatenmatrix </li>
 * <li> name.fl = Filename der Fluss Rohdatenmatrix </li>
 * <li> name.info = Filename der Fall-Info Datei</li>
 * <li> title = Titel des Falls </li></ul>
 * Thomas Hacklaender 2000.4.3:
 * Umstellung auf die aktuelle Version 2.1 des dcm-Package.<br>
 * Thomas Hacklaender 2000.4.3:
 * Neues Testbild in loadReferenceData definiert.<br>
 * Thomas Hacklaender 2002.10.13:
 * Strings internationalisiert.<br>
 * Thomas Hacklaender 2002.10.27:
 * Keys der Properties in der Index Datei geaendert:<br>
 * name.info -> name.filename.<br>
 * title -> name.title.<br>
 * </DD></DL>
 * @author   Christian Schalla
 * @author   Andreas Truemper
 * @version  1.3, 2002.10.27
 */
public class FileLoader {
    
    
    // th 2005.11.11, added
    ResourceBundle				frameRsrc = PropertyResourceBundle.getBundle("virtual/mrt/resources/vmrt_frame");
    
    /*
     * Private Konstanten zur Bezeichnung der Rodatenmatrizen
     */
    private final int			MATRIX_T1 = 0;
    private final int			MATRIX_T2 = 1;
    private final int			MATRIX_PD = 2;
    private final int			MATRIX_SZ = 3;
    private final int			MATRIX_FL = 4;
    
    
    /**
     * Matrix mit den T1-Werten. Defaultwert ist 0.
     */
    private int[][]				t1Matrix = new int[256][256];
    
    
    /**
     * Matrix mit den T2-Werten. Defaultwert ist 0.
     */
    private int[][]				t2Matrix = new int[256][256];
    
    
    /**
     * Matrix mit den Protonendichtewerten. Defaultwert ist 0.
     */
    private int[][]				pdMatrix = new int[256][256];
    
    
    /**
     * Matrix mit den Suszeptibilitaetswerten. Defaultwert ist 0.
     */
    private int[][]				szMatrix = new int[256][256];
    
    
    /**
     * Matrix mit den Flusswerten. Defaultwert ist 0.
     */
    private int[][]				flMatrix = new int[256][256];
    
    
    /**
     * Das DcmDataObject (enthaelt Bildparameter, jedoch nicht mehr die Pixeldaten)
     */
    private DcmDataObject       ddo = null;
    
    
    /**
     * Der Titel des Rohdatensatzes
     */
    private String				caseInfoTitle = "";
    
    
    /**
     * URL der Case-Info Datei
     */
    private URL                 caseInfoURL = null;
    
    
    /**
     * True, wenn der Referenzdatensatz geladen wurde
     */
    private boolean				isReferenceData = false;
    
    
    /**
     * Standardkonstruktor.
     */
    public FileLoader() {}
    
    
    /**
     * Die Methode liefert die Matrix mit den T1-Werten zurueck.
     * @return Die Matrix mit den T1-Werten.
     */
    public int[][] getT1Matrix() {
        return t1Matrix;
    }		// Methode getT1Matrix
    
    
    /**
     * Die Methode liefert die Matrix mit den T2-Werten zurueck.
     * @return Die Matrix mit den T2-Werten.
     */
    public int[][] getT2Matrix() {
        return t2Matrix;
    }		// Methode getT2Matrix
    
    
    /**
     * Die Methode liefert die Matrix mit den PD-Werten zurueck.
     * @return Die Matrix mit den PD-Werten.
     */
    public int[][] getPDMatrix() {
        return pdMatrix;
    }		// Methode getPDMatrix
    
    
    /**
     * Die Methode liefert die Matrix mit den Suszeptibilitaetswerten zurueck.
     * @return Die Matrix mit den Suszeptibilitaetswerten.
     */
    public int[][] getSZMatrix() {
        return szMatrix;
    }		// Methode getSZMatrix
    
    
    /**
     * Die Methode liefert die Matrix mit den Flusswerten zurueck.
     * @return Die Matrix mit den Flusswerten.
     */
    public int[][] getFLMatrix() {
        return flMatrix;
    }		// Methode getFLMatrix
    
    
    /**
     * Die Methode liefert das DcmDataObject zurueck. Dieses enthaelt allerdings
     * keine Pixel-Informationen mehr. Es dient lediglich dazu, die zusaetzlichen
     * Bildinformationen zu speichern. Somit koennen neu erzeugte Bilder mit den
     * gleichen Informationen versehen und im DICOM-Format abgespeichert werden.
     * @return Das DicomDataObject
     */
    public DcmDataObject getDcmDataObject() {
        return ddo;
    }		// Methode getDcmDataObject
    
    
    /**
     * Die Methode liefert den Titel des Rohdatensatzes zurueck.
     * @return Der Titel des Rohdatensatzes.
     * @author Thomas Hacklaender
     */
    public String getCaseInfoTitle() {
        return caseInfoTitle;
    }
    
    
    /**
     * Die Methode liefert die URL der optionalen Case-Info Datei zurueck.
     * @return Die URL der optionalen Case-Info Datei.
     * @author Thomas Hacklaender
     */
    public URL getCaseInfoURL() {
        return caseInfoURL;
    }
    
    
    /**
     * Die Methode laedt die Rohdaten aus einer Datei. Zunaechst werden aus der
     * uebergebenen Indexdatei die 5 anderen Dateinamen ausgelesen. Danach werden
     * aus diesen Dateien die Rohdatenmatrizen gelesen.
     * @param dir Pfad der Indexdatei.
     * @param file Dateiname der Indexdatei.
     */
    public void loadData(String dir, String file) {
        FileInputStream fis = null;
        Properties	prop = new Properties();
        String		s;
        
        try {
            fis = new FileInputStream(dir + File.separator + file);
            prop.load(fis);
        } catch (Exception e) {
            System.out.println(frameRsrc.getString("fileloader.exception.load"));
            return;
        }
        
        s = prop.getProperty("name.t1");
        if (s != null) {
            loadImage(MATRIX_T1, s, dir);
        }
        
        s = prop.getProperty("name.t2");
        if (s != null) {
            loadImage(MATRIX_T2, s, dir);
        }
        
        s = prop.getProperty("name.pd");
        if (s != null) {
            loadImage(MATRIX_PD, s, dir);
        }
        
        s = prop.getProperty("name.fl");
        if (s != null) {
            loadImage(MATRIX_FL, s, dir);
        }
        
        s = prop.getProperty("name.sz");
        if (s != null) {
            loadImage(MATRIX_SZ, s, dir);
        }
        
        // th 2002.10.27
        // s = prop.getProperty("title");
        s = prop.getProperty("name.title");
        if (s != null) {
            caseInfoTitle = s;
        }
        
        // th 2002.10.27
        // s = prop.getProperty("name.info");
        s = prop.getProperty("name.filename");
        if (s != null) {
            // Datensatz ueber eine idx Datei beschrieben
            File f = new File(dir, s);
            if (f != null) {
                caseInfoURL = Tools.file2URL(f);
            }
        }
    }
    
    
    /**
     * Die Methode laedt eines der Rohdatenbilder. Welches Bild geladen wird,
     * wird durch einen Parameter bestimmt.
     * @param type Gibt an, welches Rohdatenbild geladen werden soll.
     * @param filename Der Dateiname des zu ladenden Bildes.
     * @param path Das Verzeichnis, in dem sich das zu ladende Rohdatenbild befindet.
     */
    private void loadImage(int type, String filename, String path) {
        File f = new File(path + File.separator + filename);
        loadImage(type, f);
    }
    
    
    /**
     * Die Methode laedt eines der Rohdatenbilder. Welches Bild geladen wird,
     * wird durch einen Parameter bestimmt.
     * @param type Gibt an, welches Rohdatenbild geladen werden soll.
     * @param f Der File des zu ladenden Bildes.
     */
    private void loadImage(int type, File f) {
        FileInputStream fis;
        byte[]          buf;
        
        try {
            fis = new FileInputStream(f);
        } catch (IOException e) {
            System.out.println(frameRsrc.getString("fileloader.exception.load"));
            return;
        }
        buf = readFirst256ByteAndClose(fis);
        if (buf == null) {
            System.out.println(frameRsrc.getString("fileloader.exception.load"));
            return;
        }
        try {
            fis = new FileInputStream(f);
        } catch (IOException e) {
            System.out.println(frameRsrc.getString("fileloader.exception.load"));
            return;
        }
        loadImage(buf, type, fis);
    }
    
    
    /**
     * Die Methode laedt eines der Rohdatenbilder. Welches Bild geladen wird,
     * wird durch einen Parameter bestimmt.
     * @param type Gibt an, welches Rohdatenbild geladen werden soll.
     * @param url Die URL des zu ladenden Bildes.
     */
    private void loadImage(int type, URL url) {
        InputStream is;
        byte[]      buf;
        
        try {
            is = url.openStream();
        } catch (IOException e) {
            System.out.println(frameRsrc.getString("fileloader.exception.load"));
            return;
        }
        buf = readFirst256ByteAndClose(is);
        if (buf == null) {
            System.out.println(frameRsrc.getString("fileloader.exception.load"));
            return;
        }
        try {
            is = url.openStream();
        } catch (IOException e) {
            System.out.println(frameRsrc.getString("fileloader.exception.load"));
            return;
        }
        loadImage(buf, type, is);
    }
    
    
    /**
     *
     * @param is
     */
    private byte[] readFirst256ByteAndClose(InputStream is) {
        byte[] buf = new byte[256];
        
        try {
            is.read(buf);
        } catch (Exception e) {
            return null;
        } finally {
            try {
                is.close();
            } catch (IOException e) {}
        }
        return buf;
    }
    
    
    /**
     * Die Methode laedt eines der Rohdatenbilder. Welches Bild geladen wird,
     * wird durch einen Parameter bestimmt.
     * @param type Gibt an, welches Rohdatenbild geladen werden soll.
     * @param url Die URL des zu ladenden Bildes.
     */
    private void loadImage(byte[] buf, int type, InputStream is) {
        
        // Testen auf Kennung fuer TIFF Images
        if (((buf[0] == 'M') && (buf[1] == 'M')) || ((buf[0] == 'I') && (buf[1] == 'I'))) {
            loadTiffImage(type, is);
        } else {
            loadDcmImage(type, is);
        }
        
        // Hochskalieren der PD-Werte
        for (int x = 0; x < 256; x++) {
            for (int y = 0; y < 256; y++) {
                pdMatrix[x][y] *= 25;
            }
        }
    }
    
    
    /**
     * Die Methode laedt eines der Rohdatenbilder im DICOM Format.
     * Welches Bild geladen wird, wird durch einen Parameter bestimmt.
     * @param type Gibt an, welches Rohdatenbild geladen werden soll.
     * @param is Der InputStream des zu ladenden Bildes.
     */
    private void loadTiffImage(int type, InputStream is) {
        DataBuffer      buf;
        BufferedImage   bi = null;
        ImageReader     reader = null;
        Iterator        iter = null;
        
        iter = ImageIO.getImageReadersByFormatName("TIFF");
        if (iter == null) {
            System.out.println(frameRsrc.getString("fileloader.exception.load"));
            return;
        }
        
        reader = (javax.imageio.ImageReader) iter.next();
        if (reader == null) {
            System.out.println(frameRsrc.getString("fileloader.exception.load"));
            return;
        }
        
        reader.setInput(new MemoryCacheImageInputStream(is));
        
        // TIFF Codec in JAI_ImageIO-1_0_01 erzeugt eine interne Exception beim
        // Einlesen von TIFF Bildern, die mit ImageJ erzeugt wurden.
        // In JAI_ImageIO-1_1 ist der Fehler behoben
        try {
            try {
                bi = reader.read(0);
            } catch (IOException e) {
                System.out.println(frameRsrc.getString("fileloader.exception.load"));
                return;
            }
        } catch (Exception e) {
            System.out.println(frameRsrc.getString("fileloader.exeption.tiffcodec"));
            return;
        }
        
        if (bi.getType() != BufferedImage.TYPE_USHORT_GRAY) {
            System.out.println(frameRsrc.getString("fileloader.exception.load"));
            return;
        }
        
        // Es muss ein unsigned short Buffer mit einer Bank sein
        buf = bi.getData().getDataBuffer();
        if (! (buf instanceof DataBufferUShort)) {
            System.out.println(frameRsrc.getString("fileloader.exception.load"));
            return;
        }
        if (buf.getNumBanks() != 1) {
            System.out.println(frameRsrc.getString("fileloader.exception.load"));
            return;
        }
        
        // Es koennen nur 256*256 Bilder verarbeitet werden
        if ((bi.getWidth() != 256) || (bi.getHeight() != 256)) {
            System.out.println(frameRsrc.getString("fileloader.exception.load"));
            return;
        }
        
        // Fuellen der Rohdatenmatrize(n)
        switch (type) {
            
            case MATRIX_T1:
                t1Matrix = new int[256][256];
                for (int x = 0; x < 256; x++) {
                    for (int y = 0; y < 256; y++) {
                        t1Matrix[x][y] = buf.getElem(y * 256 + x);
                    }
                }
                break;
                
            case MATRIX_T2:
                t2Matrix = new int[256][256];
                for (int x = 0; x < 256; x++) {
                    for (int y = 0; y < 256; y++) {
                        t2Matrix[x][y] = buf.getElem(y * 256 + x);
                    }
                }
                break;
                
            case MATRIX_SZ:
                szMatrix = new int[256][256];
                
                for (int x = 0; x < 256; x++) {
                    for (int y = 0; y < 256; y++) {
                        szMatrix[x][y] = buf.getElem(y * 256 + x);
                    }
                }
                break;
                
            case MATRIX_FL:
                flMatrix = new int[256][256];
                
                for (int x = 0; x < 256; x++) {
                    for (int y = 0; y < 256; y++) {
                        flMatrix[x][y] = buf.getElem(y * 256 + x);
                    }
                }
                break;
                
            case MATRIX_PD:
                pdMatrix = new int[256][256];
                
                for (int x = 0; x < 256; x++) {
                    for (int y = 0; y < 256; y++) {
                        pdMatrix[x][y] = (int) buf.getElem(y * 256 + x);
                    }
                }
        }
        
        // Erzeugen eines DicomDataObjects
        SecondaryCaptureIOD scIOD = new SecondaryCaptureIOD();
        
        // Erzeugen eines temporaeren Pixel-Arrays zur Ertzeugung eines korrekten DicomDataObjects
        short[] tmpPixels = new short[256 * 256];
        
        scIOD.set16UBitGrayImage(tmpPixels, 256, 256, 15);
        DcmDataObject tmpddo = scIOD.getDDO(DcmDataObject.LITTLE_ENDIAN, DcmDataObject.EXPLICITE_VR, DcmDataObject.META_STORAGE);
        
        // tmpddo.clearPixelData();
        ddo = tmpddo;
        
    }
    
    
    /**
     * Die Methode laedt eines der Rohdatenbilder im DICOM Format.
     * Welches Bild geladen wird, wird durch einen Parameter bestimmt.
     * @param type Gibt an, welches Rohdatenbild geladen werden soll.
     * @param is Der InputStream des zu ladenden Bildes.
     */
    private void loadDcmImage(int type, InputStream is) {
        DcmImage        di = null;
        DcmDataObject   sddo = null;
        
        try {
            sddo = DcmInputStream.loadDDO(is);
            // sddo = DcmInputStream.loadDDO(theURL);
            di = new DcmImage(sddo);
        } catch (Exception err) {
            System.out.println(frameRsrc.getString("fileloader.exception.load"));
        }
        
        try {
            is.close();
        } catch (Exception e) {}
        
        // Fuellen der Rohdatenmatrize(n)
        switch (type) {
            
            case MATRIX_T1:
                t1Matrix = new int[256][256];
                if (di != null) {
                    for (int x = 0; x < 256; x++) {
                        for (int y = 0; y < 256; y++) {
                            t1Matrix[x][y] = (int) di.pixel16[y * 256 + x];
                        }
                    }
                }
                break;
                
            case MATRIX_T2:
                t2Matrix = new int[256][256];
                if (di != null) {
                    for (int x = 0; x < 256; x++) {
                        for (int y = 0; y < 256; y++) {
                            t2Matrix[x][y] = (int) di.pixel16[y * 256 + x];
                        }
                    }
                }
                break;
                
            case MATRIX_SZ:
                szMatrix = new int[256][256];
                if (di != null) {
                    for (int x = 0; x < 256; x++) {
                        for (int y = 0; y < 256; y++) {
                            szMatrix[x][y] = (int) di.pixel16[y * 256 + x];
                        }
                    }
                }
                break;
                
            case MATRIX_FL:
                flMatrix = new int[256][256];
                if (di != null) {
                    for (int x = 0; x < 256; x++) {
                        for (int y = 0; y < 256; y++) {
                            flMatrix[x][y] = (int) di.pixel16[y * 256 + x];
                        }
                    }
                }
                break;
                
            case MATRIX_PD:
                pdMatrix = new int[256][256];
                if (di != null) {
                    for (int x = 0; x < 256; x++) {
                        for (int y = 0; y < 256; y++) {
                            pdMatrix[x][y] = (int) di.pixel16[y * 256 + x];
                        }
                    }
                    
                    // Gleichzeitig mit der PD-Matrix wird auch das DicomDataObject ausgelesen
                    // Zuvor werden die Pixeldaten daraus geloescht
                    ddo = sddo;
                }
                
        }
    }
    
    
    /**
     * Die Methode belegt die Matrizen fuer die T1-, T2- und PD-Werte mit
     * dem Beispielbild.
     */
    public void loadSampleData() {
        
        // Defaultwert fuer den Title
        caseInfoTitle = frameRsrc.getString("fileloader.sampledata.title");
        
        // Defaultwert fuer die Case-Info's
        caseInfoURL = this.getClass().getResource(frameRsrc.getString("fileloader.sampledata.filename"));
        
        loadImage(MATRIX_T1, this.getClass().getResource(frameRsrc.getString("fileloader.sampledata.t1")));
        loadImage(MATRIX_T2, this.getClass().getResource(frameRsrc.getString("fileloader.sampledata.t2")));
        loadImage(MATRIX_PD, this.getClass().getResource(frameRsrc.getString("fileloader.sampledata.pd")));
    }
    
    
    /**
     * Die Methode belegt die Matrizen fuer die T1-, T2- und PD-Werte mit
     * einigen Referenz-Werten. Anhand dieses Referenzbildes, wird die Funktionsweise
     * des virtuellen Tomographen in der schriftlichen Ausarbeitung der Diplomarbeit
     * erlaeutert.
     */
    public void loadReferenceData() {
        
        // Es ist der Referenzdatensatz
        isReferenceData = true;
        
        // Defaultwert fuer den Title
        caseInfoTitle = frameRsrc.getString("fileloader.refdata.title");
        
        // Defaultwert fuer die Case-Info's fuer den Referenzdatensatz
        caseInfoURL = this.getClass().getResource(frameRsrc.getString("fileloader.refdata.filename"));
        
        // Erstellen eines zufallsgenerators
        // Random	myRandomizer = new Random();
        
        // Erstellen und initialisieren der 3 Rohdatenmatrizen
        t1Matrix = new int[256][256];
        pdMatrix = new int[256][256];
        t2Matrix = new int[256][256];
        
        // Erzeugen eines temporaeren Pixel-Arrays zur Ertzeugung eines korrekten
        // DicomDataObjects
        short[]							tmpPixels = new short[256 * 256];
        
        // Erzeugen eines DicomDataObjects
        SecondaryCaptureIOD scIOD = new SecondaryCaptureIOD();
        
        scIOD.set16UBitGrayImage(tmpPixels, 256, 256, 15);
        DcmDataObject tmpddo = scIOD.getDDO(DcmDataObject.LITTLE_ENDIAN, DcmDataObject.EXPLICITE_VR, DcmDataObject.META_STORAGE);
        
        // tmpddo.clearPixelData();
        ddo = tmpddo;
        tmpPixels = null;
        
        // Testbild
        
        // Rahmen mit eiweisreicher Fluessigkeit
        // th 2002.10.27 azskommentoert
        // drawRelaxRect(2, 2, 252, 2, 500, 250, 100);
        // drawRelaxRect(2, 2, 2, 252, 500, 250, 100);
        // drawRelaxRect(2, 252, 252, 2, 500, 250, 100);
        // drawRelaxRect(252, 2, 2, 252, 500, 250, 100);
        
        // Fett:
        // Szumowski J, Simon J: Proton Chemical Shift Imaging.
        // In: Stark D, Bradley W: Magnetic Resonance Imaging.
        // S. 482. Second Edition, Mosby, St. Loise 1992
        drawRelaxRect(16, 16, 32, 32, 280, 50, 80);
        
        // Methaemoglobin:
        // Hendrick R, Raff U: Image Contrast and Noise.
        // In: Stark D, Bradley W: Magnetic Resonance Imaging.
        // S. 113. Second Edition, Mosby, St. Loise 1992
        drawRelaxRect(80, 16, 32, 32, 460, 106, 86);
        
        // Weisse Hirnsubstanz:
        drawRelaxRect(144, 16, 32, 32, 510, 67, 54);
        
        // Graue Hirnsubstanz:
        drawRelaxRect(208, 16, 32, 32, 760, 77, 62);
        
        // Hirnoedem:
        drawRelaxRect(16, 80, 32, 32, 900, 126, 77);
        
        // Zyste:
        drawRelaxRect(80, 80, 32, 32, 1080, 215, 100);
        
        // Liquor:
        drawRelaxRect(144, 80, 32, 32, 2650, 280, 89);
        
        // Reines Wsser:
        // Fullerton G: Physioloic Basis of Magnetic Relaxation.
        // In: Stark D, Bradley W: Magnetic Resonance Imaging.
        // S. 93. Second Edition, Mosby, St. Loise 1992
        drawRelaxRect(208, 80, 32, 32, 4000, 4000, 100);
        
        // Quadrat mit Fett und 3 Pixel Kreuz
        drawRelaxRect(16, 144, 96, 96, 280, 50, 80);
        drawRelaxRect(63, 144, 3, 96, 0, 0, 0);
        drawRelaxRect(16, 191, 96, 3, 0, 0, 0);
        
        // Quadrat mit Wasser und 3 PixelPunkt
        drawRelaxRect(144, 144, 96, 96, 4000, 4000, 100);
        drawRelaxRect(191, 191, 3, 3, 0, 0, 0);
    }
    
    
    /**
     * Zeichnet ein Rechteck mit T1, T2 und PD Werten in die Matritzen
     * @param x  	x-Koordinate der linken oberen Ecke
     * @param y  	y-Koordinate der linken oberen Ecke
     * @param b  	Breite
     * @param h  	Höhe
     * @param t1	T1 Relaxationszeit in ms
     * @param t2	T2 Relaxationszeit in ms
     * @param pd	Protonendichte in % bezogen auf reines Wasser
     *
     * @author Thomas Hacklaender
     * @version 2000.4.3
     */
    private void drawRelaxRect(int x, int y, int b, int h, int t1, int t2, int pd) {
        if (pd > 100) {
            pd = 100;
        }
        for (int ix = x; ix < x + b; ix++) {
            for (int iy = y; iy < y + h; iy++) {
                t1Matrix[ix][iy] = t1;
                t2Matrix[ix][iy] = t2;
                pdMatrix[ix][iy] = (65530 * pd) / 100;
            }		// for
        }		// for
    }
    
}






/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

