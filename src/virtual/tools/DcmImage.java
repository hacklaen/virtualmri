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


import java.util.*;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

import rad.dicom.dcm.*;
import rad.dicom.ima.*;


/**
 *
 * <DL><DT><B>Modifications: </B><DD>
 * 2000.4.3:   Die Klasse dient jetzt nur noch als "Glue" zwischen dem
 * aktuellen dcm-Package Ver. 2.1 und der alten Struktu des
 * dcm-Package.
 * Die Methode extractPixels kehrt  o h n e  Fehlermeldung
 * zurueck, wenn sie keine Pixel Daten findet. Dieses
 * eigentlich widersinnige Verhalten ist notwendig, da der
 * Viewer z.B. in ViewerFrame.calcMIPImage ein neues Bild
 * (DcmImage) erzeugt und dazu auf das DcmDataObject eines
 * der Rohdatenbilder zugreift. Darin sind aber die Pixeldaten
 * aus Platzgruenden geloescht (in dem Moment, wo das
 * DcmDataObject mit ImagePlus.setDDO im ImagePlus gespeichert
 * wird.
 * Man sollte anstreben, innerhalb des VMRT/DicomViewer
 * diese Klasse vollstaendig zugunsten des dcm-Packages
 * zu ersetzen.
 * </DD></DL>
 * @author   Thomas Hacklaender
 * @version  2000.4.3
 */
public class DcmImage {
    
    // Supported standard SOP Classes PS 3.4-B.5 p.24
    public static final String	SOP_CT_IMAGE_STORAGE = "1.2.840.10008.5.1.4.1.1.2";
    public static final String	SOP_MR_IMAGE_STORAGE = "1.2.840.10008.5.1.4.1.1.4";
    
    // Supported values for 'Photometric Interpretation'  PS 3.3-C.7.6.3 p.80
    public static final int			PI_UNSUPPORTED = 0;
    public static final int			PI_MONOCHROME1 = 1;			// "MONOCHROME1"
    public static final int			PI_MONOCHROME2 = 2;			// "MONOCHROME2"
    public static final int			PI_PALETTE_COLOR = 3;		// "PALETTE COLOR"
    
    public static final int			IOD_UNKNOWN_IMAGE = 0;
    public static final int			IOD_CT_IMAGE = 1;
    public static final int			IOD_MR_IMAGE = 2;
    
    // Constants for Center/Width setup
    public static final int			CW_FILE = 0;
    public static final int			CW_AUTO = 1;
    
    
    public MemoryImageSource		imgSource = null;
    public short[]							pixel16 = null;
    public ColorModel						cModel;
    
    public int									imageIOD = IOD_UNKNOWN_IMAGE;
    public int									samplesPPixel;
    public int									photometricInterpretation = PI_UNSUPPORTED;
    public int									rows;
    public int									columns;
    public int									bitsAllocated;
    public int									bitsStored;
    public int									highBit;
    public boolean							unsignedPixel;
    public boolean							bytePixel;
    
    public boolean							isCenterWidth = false;
    public int									windowCenter;						// in SV units or HU (CT-images)
    public int									windowWidth;						// in SV units or HU (CT-images)
    
    public String								classUID;
    public String								instanceUID;
    
    public int									rescaleIntercept;
    public int									rescaleSlope;
    
    public String								scanningSequence;
    public String								scanningVariant;
    
    public double								pixelSpacingRow;				// [mm]
    public double								pixelSpacingColumn;			// [mm]
    public double								sliceThickness;					// [mm]
    public double								sliceLocation;					// [mm]
    
    
    // Pixel values range from 0 ... maxPixelValue16 (= 2^bitsStored -1)
    private int									maxPixelValue16;
    
    // Pixel data representation range from minPixelValue ... maxPixelValue
    private int									minPixelValue;
    private int									maxPixelValue;
    
    private int									autoCenter;							// in SV units or HU (CT-images)
    private int									autoWidth;							// in SV units or HU (CT-images)
    private int									theCenter;							// in SV units or HU (CT-images)
    private int									theWidth;								// in SV units or HU (CT-images)
    
    
    /**
     * @param ddo  Das DICOMDataObject, das das Bild beschreibt
     * @exception dcm.Exception
     */
    public DcmImage(DcmDataObject ddo) throws Exception {
        
        if (ddo == null) {
            throw new Exception("No DcmDataObject specified");
        }
        
        extractImageValues(ddo);
        if (samplesPPixel != 1) {
            throw new Exception("Only one image plane supported");
        }
        if ((bitsAllocated != 8) & (bitsAllocated != 16)) {
            throw new Exception("Only 8 or 16 bit/pixel supported");
        }
        if (photometricInterpretation == PI_UNSUPPORTED) {
            throw new Exception("Photometric Interpretation not supported");
        }
        
        maxPixelValue16 = (int) Math.pow(2, bitsStored) - 1;
        if (unsignedPixel) {
            minPixelValue = 0;
            maxPixelValue = (int) Math.pow(2, bitsStored) - 1;
        } else {
            minPixelValue = -((int) Math.pow(2, bitsStored)) / 2;
            maxPixelValue = ((int) Math.pow(2, bitsStored)) / 2 - 1;
        }
        
        extractPixels(ddo);
        
        switch (photometricInterpretation) {
            case PI_MONOCHROME1:
                setMonochrome1ColorModel();
                break;
                
            case PI_MONOCHROME2:
                setMonochrome2ColorModel();
                break;
                
            case PI_PALETTE_COLOR:
                extractLUT(ddo);
                break;
        }
        
        updateAWTImage();
    }
    
    
    /**
     * @param gi  Die GeneralImageIOD des Bildes
     * @exception dcm.Exception
     */
    public DcmImage(GeneralImageIOD gi) throws Exception {
        
        if (gi == null) {
            throw new Exception("No DcmDataObject specified");
        }
        
        extractImageValues(gi.headerDDO);
        if (samplesPPixel != 1) {
            throw new Exception("Only one image plane supported");
        }
        if ((bitsAllocated != 8) & (bitsAllocated != 16)) {
            throw new Exception("Only 8 or 16 bit/pixel supported");
        }
        if (photometricInterpretation == PI_UNSUPPORTED) {
            throw new Exception("Photometric Interpretation not supported");
        }
        
        maxPixelValue16 = (int) Math.pow(2, bitsStored) - 1;
        if (unsignedPixel) {
            minPixelValue = 0;
            maxPixelValue = (int) Math.pow(2, bitsStored) - 1;
        } else {
            minPixelValue = -((int) Math.pow(2, bitsStored)) / 2;
            maxPixelValue = ((int) Math.pow(2, bitsStored)) / 2 - 1;
        }
        
        pixel16 = new short[gi.pixel16.length];
        System.arraycopy(gi.pixel16, 0, pixel16, 0, gi.pixel16.length);
        
        switch (photometricInterpretation) {
            case PI_MONOCHROME1:
                setMonochrome1ColorModel();
                break;
                
            case PI_MONOCHROME2:
                setMonochrome2ColorModel();
                break;
                
            case PI_PALETTE_COLOR:
                extractLUT(gi.headerDDO);
                break;
        }
        
        updateAWTImage();
    }
    
    
    // Aktualisiert alle Einstellungen zur Erzeugung einer MemoryImageSource
    // aus dem 12Bit-Bild
    
    
    /**
     * Method declaration
     *
     *
     * @see
     */
    public void updateAWTImage() {
        if (pixel16 != null) {
            calcAutomaticCW();
            if (!isCenterWidth) {
                windowCenter = autoCenter;
                windowWidth = autoWidth;
            }
            theCenter = windowCenter;
            theWidth = windowWidth;
            
            updateImageSource();
        }
    }
    
    
    /**
     * Setzt die Center und Window Werte des Bildes entsprechend dem
     * Parameter type.
     * @param type  DcmImage.CW_FILE setzt die Center und Window Werte
     * auf die Werte der DICOM Datei zurueck.
     * DcmImage.CW_AUTO berechnet die Werte nach einem
     * internen Algorithmus.
     */
    public void setCW(int type) {
        switch (type) {
            case CW_FILE:
                theCenter = windowCenter;
                theWidth = windowWidth;
                break;
                
            case CW_AUTO:
                theCenter = autoCenter;
                theWidth = autoWidth;
                break;
        }
        updateImageSource();
    }
    
    
    /**
     * Setzt die Center und Window Werte des Bildes.
     * @param wCenter  neuer Center Wert
     * @param wWidth   neuer Window Wert
     */
    public void setCW(int wCenter, int wWidth) {
        theCenter = wCenter;
        theWidth = wWidth;
        updateImageSource();
    }
    
    
    /**
     * Method declaration
     *
     *
     * @param c
     *
     * @see
     */
    public void setCenter(int c) {
        theCenter = c;
        updateImageSource();
    }
    
    
    /**
     * Method declaration
     *
     *
     * @param w
     *
     * @see
     */
    public void setWindow(int w) {
        theWidth = w;
        updateImageSource();
    }
    
    
    
    /**
     * Liefert den aktuellen Center Wert des Bildes
     * @return  Der aktuelle Center Wert des Bildes
     */
    public int getCenter() {
        return theCenter;
    }
    
    
    /**
     * Liefert den aktuellen Window Wert des Bildes
     * @return  Der aktuelle Window Wert des Bildes
     */
    public int getWidth() {
        return theWidth;
    }
    
    
    /**
     * Setzt die lokalen Variablen der class auf die Werte des
     * DcmDataObject.
     * @param ddo  Das DcmDataObject, das das Bild beschreibt.
     * @exception dcm.Exception.
     */
    private void extractImageValues(DcmDataObject ddo) throws Exception {
        StringTokenizer st;
        String					s;
        
        try {
            
            // General Image PS 3.3-C.7.6.1 p.75
            // - no required Data Elements (Type 1)
            // see. PS 3.5-7.4 p.27
            
            // Image Plane PS 3.3-C.7.6.2 p.79
            // - no Data Elements for visualization
            
            // Image Pixel PS 3.3-C.7.6.3 p.80
            // & PS 3.5-8. p.34
            // & PS 3.5-Annex D p.50
            samplesPPixel = ddo.getUS(DcmDDE.DD_SamplesPerPixel);
            String	pi = ddo.getString(DcmDDE.DD_PhotometricInterpretation);
            
            rows = ddo.getUS(DcmDDE.DD_Rows);
            columns = ddo.getUS(DcmDDE.DD_Columns);
            bitsAllocated = ddo.getUS(DcmDDE.DD_BitsAllocated);
            bytePixel = bitsAllocated == 8;
            bitsStored = ddo.getUS(DcmDDE.DD_BitsStored);
            highBit = ddo.getUS(DcmDDE.DD_HighBit);
            unsignedPixel = ddo.getUS(DcmDDE.DD_PixelRepresentation) == 0;
            
            String[]	sa = DcmValue.str2StringArray(ddo.getString(DcmDDE.DD_PixelSpacing), "\\");
            
            pixelSpacingRow = 0.0;
            pixelSpacingColumn = 0.0;
            if (sa.length > 0) {
                pixelSpacingRow = DcmValue.str2Double(sa[0], 0.0);
            }
            if (sa.length > 1) {
                pixelSpacingColumn = DcmValue.str2Double(sa[1], 0.0);
            }
            
            sliceThickness = DcmValue.str2Double(ddo.getString(DcmDDE.DD_SliceThickness), 1);
            sliceLocation = DcmValue.str2Double(ddo.getString(DcmDDE.DD_SliceLocation), 0);
            
            
            // Setup PhotometricInterpretation
            if (pi.compareTo("MONOCHROME1") == 0) {
                photometricInterpretation = PI_MONOCHROME1;
            }
            if (pi.compareTo("MONOCHROME2") == 0) {
                photometricInterpretation = PI_MONOCHROME2;
            }
            if (pi.compareTo("PALETTE COLOR") == 0) {
                photometricInterpretation = PI_PALETTE_COLOR;
            }
            
            // SOP Common PS 3.3-C.12.1 p.139
            classUID = ddo.getString(DcmDDE.DD_SOPClassUID);
            instanceUID = ddo.getString(DcmDDE.DD_SOPInstanceUID);
            
            if (classUID.compareTo(SOP_CT_IMAGE_STORAGE) == 0) {
                imageIOD = IOD_CT_IMAGE;
                
                // CT Image PS 3.3-C.8.2.1 p.91
                // - Samples per Pixel = 1 (PS 3.3-C.8.2.1.1.2)
                // - Photometric Iterpretation =
                // 'MONOCHROME1' or 'MONOCHROME1' (PS 3.3-C.8.2.1.1.3)
                // - Bits allocated = 16 (PS 3.3-C.8.2.1.1.4)
                // - Bits stored = 12-16 (PS 3.3-C.8.2.1.1.5)
                rescaleIntercept = (int) DcmValue.str2Long(ddo.getString(DcmDDE.DD_RescaleIntercept), 0);
                rescaleSlope = (int) DcmValue.str2Long(ddo.getString(DcmDDE.DD_RescaleSlope), 1);
            }
            
            if (classUID.compareTo(SOP_MR_IMAGE_STORAGE) == 0) {
                imageIOD = IOD_MR_IMAGE;
                
                // MR Image PS 3.3-C.8.3.1 p.94
                // - Samples per Pixel = 1 (PS 3.3-C.8.3.1.1.2)
                // - Photometric Iterpretation =
                // 'MONOCHROME1' or 'MONOCHROME1' (PS 3.3-C.8.3.1.1.3)
                // - Bits allocated = 16 (PS 3.3-C.8.3.1.1.4)
                scanningSequence = ddo.getString(DcmDDE.DD_ScanningSequence);
                scanningVariant = ddo.getString(DcmDDE.DD_SequenceVariant);
            }
            
        } catch (Exception e) {
            throw new Exception("Can't find/extract requiered data element");
        }
        
        // (User Option) VOI LUT PS 3.3-C.11.2 p.137
        // - VM 1-n (PS 3.5-6.4 p.20): process value 1
        try {
            String	w = ddo.getString(DcmDDE.DD_WindowCenter);
            
            if (w.length() != 0) {
                try {
                    st = new StringTokenizer(w, "\\");
                    s = st.nextToken().trim();
                    windowCenter = Integer.parseInt(s);
                    w = ddo.getString(DcmDDE.DD_WindowWidth);
                    st = new StringTokenizer(w, "\\");
                    s = st.nextToken().trim();
                    windowWidth = Integer.parseInt(s);
                    isCenterWidth = true;
                } catch (Exception e) {
                    throw new Exception("Can't find/extract Center/Width data elements");
                }
            }
        } catch (Exception e) {}
        
    }
    
    
    /**
     * Generiert das ColorModel fuer das Bild. Die Farbkomponenten der Indices
     * werden aus dem DcmDataObject extrahiert.
     * Dies entspricht einer "Photometric Interpretation" = PALETTE_COLOR. Siehe:
     * Image Pixel Module, Photometric Interpretation PS 3.3-C.7.6.3.1.2.
     * Jedes ColorModel des Bildes ist ein IndexColorModel mit 256 moeglichen
     * Werten. Die Pixelwerte des Bildes werden auf das Intervall 0..255
     * skaliert (= gefenstert).
     * @exception Exception.
     */
    private void extractLUT(DcmDataObject ddo) throws Exception {
        DcmValue	dcvDescRed, dcvDescGreen, dcvDescBlue;
        DcmValue	dcvDataRed, dcvDataGreen, dcvDataBlue;
        byte[]		rTab, gTab, bTab;
        
        dcvDescRed = ddo.getDcmValue(DcmDDE.DD_RedPaletteColorLookupTableDescriptor);
        dcvDescGreen = ddo.getDcmValue(DcmDDE.DD_GreenPaletteColorLookupTableDescriptor);
        dcvDescBlue = ddo.getDcmValue(DcmDDE.DD_BluePaletteColorLookupTableDescriptor);
        if ((dcvDescRed == null) | (dcvDescGreen == null) | (dcvDescBlue == null)) {
            throw new Exception("GeneralImageIOD.extractLUT: Can't find LUT descriptors");
        }
        
        dcvDataRed = ddo.getDcmValue(DcmDDE.DD_RedPaletteColorLookupTableData);
        dcvDataGreen = ddo.getDcmValue(DcmDDE.DD_GreenPaletteColorLookupTableData);
        dcvDataBlue = ddo.getDcmValue(DcmDDE.DD_BluePaletteColorLookupTableData);
        if ((dcvDataRed == null) | (dcvDataGreen == null) | (dcvDataBlue == null)) {
            throw new Exception("GeneralImageIOD.extractLUT: Can't find LUT data");
        }
        
        rTab = new byte[maxPixelValue + 1];
        gTab = new byte[maxPixelValue + 1];
        bTab = new byte[maxPixelValue + 1];
        
        setColorComponent(dcvDescRed, dcvDataRed, rTab);
        setColorComponent(dcvDescGreen, dcvDataGreen, gTab);
        setColorComponent(dcvDescBlue, dcvDataBlue, bTab);
        
        cModel = new IndexColorModel(bitsStored, rTab.length, rTab, gTab, bTab);
    }
    
    
    /**
     * Extrahiert eine Farbkomponente aus dem DcmDataObject.
     * Image Pixel Module, Palette Color Lookup PS 3.3-C.7.6.3.1.5., C.7.6.3.1.6
     * @param dcvDesc	 Color Lookup Table Descriptor
     * @param dcvDesc	 Color Lookup Table Data
     * @param cTab		 Byte-Array als Farmkomponente des IndexColorModels des Bildes
     * @exception dcm.Exception.
     */
    private void setColorComponent(DcmValue dcvDesc, DcmValue dcvData, byte[] cTab) throws Exception {
        int		numEntries;
        int		numBits;
        int		firstMappedPixel, lastMappedPixel;
        int		lutMask;
        int		idxLast;
        byte	firstLUTvalue;
        byte	lastLUTvalue;
        
        // Der Descriptor besteht aus drei aufeinanderfolgenden SS oder US
        // Da die Zahlenwerte nie ausserhalb des Wertebereiches von SS liegen,
        // werden sie als SS eingelesen.
        numEntries = DcmValue.bufToSS(dcvDesc.getData(), 0);
        firstMappedPixel = DcmValue.bufToSS(dcvDesc.getData(), 2);
        numBits = DcmValue.bufToSS(dcvDesc.getData(), 4);
        
        if (firstMappedPixel < 0) {
            throw new Exception("GeneralImageIOD.setColorComponent: Only unsigned integer pixel values supported");
        }
        if (numBits > 8) {
            throw new Exception("GeneralImageIOD.setColorComponent: Max. 8 bit/color component supported");
        }
        
        lutMask = (int) Math.pow(2, numBits) - 1;
        lastMappedPixel = firstMappedPixel + numEntries - 1;
        idxLast = cTab.length - 1;
        if (lastMappedPixel > idxLast) {
            lastMappedPixel = idxLast;
            numEntries = lastMappedPixel - firstMappedPixel + 1;
        }
        
        // Die Daten bestehen aus aufeinanderfolgenden SS oder US.
        // Diese Methode unterstuetzt nur US.
        for (int i = 0; i <= idxLast; i++) {
            cTab[i] = (byte) (DcmValue.bufToUS(dcvData.getData(), i * 2) & lutMask);
        }
        
        // Bei Farbpaletten muss immer der gesamte Wertebereich sichtar sein
        isCenterWidth = true;
        windowWidth = numEntries;
        windowCenter = (lastMappedPixel - firstMappedPixel) / 2;
    }
    
    
    /**
     * Extrahiert die Pixeldaten aus dem DcmDataObject.
     * @exception dcm.Exception.
     */
    private void extractPixels(DcmDataObject ddo) throws Exception {
        DcmValue	dcv;
        int				nPixel;
        int				i, p;
        short			v;
        int				numShift;
        byte[]		data;
        int				bitMask;
        
        if (!ddo.hasValue(DcmDDE.DD_PixelData)) {
            return;
        }
        
        nPixel = rows * columns;
        dcv = ddo.getDcmValue(DcmDDE.DD_PixelData);
        data = dcv.getData();
        
        if (bytePixel) {
            if (data.length < nPixel) {
                throw new Exception("GeneralImageIOD.extractPixels: Not enough pixel data");
            }
        } else {
            if (data.length < 2 * nPixel) {
                throw new Exception("GeneralImageIOD.extractPixels: Not enough pixel data");
            }
        }
        
        pixel16 = new short[nPixel];
        numShift = bitsAllocated - bitsStored;
        bitMask = (int) Math.pow(2, bitsStored) - 1;
        i = 0;
        p = 0;
        
        if (bytePixel) {
            
            while (p < nPixel) {
                if (unsignedPixel) {
                    pixel16[p++] = (short) (data[i++] & bitMask);
                } else {
                    v = data[i++];
                    pixel16[p++] = (short) ((v << numShift) >> numShift);
                }
            }
            
        } else {
            
            while (p < nPixel) {
                
                // Daten werden als little Endian gespeichert
                v = (short) (((((int) data[i++]) & 0x00ff) | (((int) data[i++]) & 0x00ff) << 8));
                
                if (unsignedPixel) {
                    pixel16[p++] = (short) (v & bitMask);
                } else {
                    pixel16[p++] = (short) ((v << numShift) >> numShift);
                }
            }
            
        }
    }
    
    
    /**
     * Method declaration
     *
     *
     * @see
     */
    public void calcAutomaticCW() {
        int		nPixel;
        int[]           histogram;
        int		sum = 0;
        int		s;
        int		min;
        int		max;
        int             pixelValue;
        
        nPixel = rows * columns;
        histogram = new int[maxPixelValue16 + 1];
        int l = pixel16.length;
        for (int i = 0; i < nPixel; i++) {
            pixelValue = pixel16[i];
            if (pixelValue < 0) {
                pixelValue = 0;
            }
            if (pixelValue > maxPixelValue16) {
                pixelValue = maxPixelValue16;
            }
            histogram[pixelValue]++;
        }
        sum = 0;
        for (int i = 0; i <= maxPixelValue16; i++) {
            sum += histogram[i];
        }
        
        s = 0;
        min = 0;
        for (int i = 0; i <= maxPixelValue16; i++) {	// 10% Perzentiele
            s += histogram[i];
            if (s > sum / 10) {
                min = i;
                break;
            }
        }
        s = 0;
        max = 0;
        for (int i = maxPixelValue16; i >= 0; i--) {	// 90% Perzentiele
            s += histogram[i];
            if (s > sum / 10) {
                max = i;
                break;
            }
        }
        
        if (!unsignedPixel) {
            if (bytePixel) {
                min -= 128;
                max -= 128;
            } else {
                min -= 32768;
                max -= 32768;
            }
        }
        
        if (imageIOD == IOD_CT_IMAGE) {
            min = rescaleSlope * min + rescaleIntercept;
            max = rescaleSlope * max + rescaleIntercept;
        }
        autoCenter = (min + max) / 2;
        autoWidth = max - min;
    }
    
    
    // 0 is white, maxValue is black
    
    
    /**
     * Method declaration
     *
     *
     * @see
     */
    private void setMonochrome1ColorModel() {
        byte[]	cTab = new byte[256];
        
        for (int i = 0; i < 256; i++) {
            cTab[i] = (byte) (256 - i);
        }
        cModel = new IndexColorModel(8, 256, cTab, cTab, cTab);
    }
    
    
    // 0 is black, maxValue is white
    
    
    /**
     * Method declaration
     *
     *
     * @see
     */
    private void setMonochrome2ColorModel() {
        byte[]	cTab = new byte[256];
        
        for (int i = 0; i < 256; i++) {
            cTab[i] = (byte) i;
        }
        cModel = new IndexColorModel(8, 256, cTab, cTab, cTab);
    }
    
    int numupdate;
    
    
    /**
     * Method declaration
     *
     *
     * @see
     */
    public void updateImageSource() {
        int			ce, wi;
        int			minImageValue, maxImageValue;
        double	step;
        byte[]	pixel8;
        int			value;
        
        if (imageIOD == IOD_CT_IMAGE) {		// HU -> SV
            ce = (theCenter - rescaleIntercept) / rescaleSlope;
            wi = theWidth / rescaleSlope;
            if (ce > maxPixelValue) {
                ce = maxPixelValue;
            }
            if (ce < minPixelValue) {
                ce = minPixelValue;
            }
            if (wi > maxPixelValue) {
                wi = maxPixelValue;
            }
            if (wi < 1) {
                wi = 1;
            }
            theCenter = rescaleSlope * ce + rescaleIntercept;
            theWidth = rescaleSlope * wi;
            minImageValue = ce - wi / 2;
            maxImageValue = ce + wi / 2;
        } else {
            if (theCenter > maxPixelValue) {
                theCenter = maxPixelValue;
            }
            if (theCenter < minPixelValue) {
                theCenter = minPixelValue;
            }
            if (theWidth > maxPixelValue) {
                theWidth = maxPixelValue;
            }
            if (theWidth < 1) {
                theWidth = 1;
            }
            minImageValue = theCenter - theWidth / 2;
            maxImageValue = theCenter + theWidth / 2;
        }
        
        if (minImageValue < minPixelValue) {
            minImageValue = minPixelValue;
        }
        if (maxImageValue > maxPixelValue) {
            maxImageValue = maxPixelValue;
            
        }
        step = (double) ((IndexColorModel) cModel).getMapSize() / (maxImageValue - minImageValue + 1);
        pixel8 = new byte[pixel16.length];
        
        for (int i = 0; i < pixel16.length; i++) {
            if (unsignedPixel) {
                
                // mask "sign"-bits
                if (bytePixel) {
                    value = pixel16[i] & 0x000000ff;
                } else {
                    value = pixel16[i] & 0x0000ffff;
                }
            } else {
                value = pixel16[i];
            }
            value = pixel16[i] - minImageValue;
            if (value < 0) {
                value = 0;
            }
            value = (int) (value * step);
            if (value > 255) {
                value = 255;
            }
            pixel8[i] = (byte) (value & 0xff);
        }
        
        if (imgSource == null) {
            imgSource = new MemoryImageSource(columns, rows, cModel, pixel8, 0, columns);
        } else {
            imgSource.newPixels(pixel8, cModel, 0, columns);
        }
    }
    
}






/*--- formatting done in "My Own Convention" style on 11-12-2005 ---*/

