**Release Notes**  
(2006.01.10)

*   **Version 3.2.14**
    
*   Parameterbilder können wahlweise als DICOM oder 16 Bit TIFF Bilder kodiert sein. (tha 2006.04.11)
    

*   **Version 3.2.12**
    
*   Optimierung der Applet Variante (tha 2006.01.10)
    

*   Beispieluntersuchung des Gehirns ist als Resource im Programm verfügbar (tha 2006.01.10)
    

*   Das berechnete Bild wird mit einem milden Median-Filter von "Ausreissern" befreit (tha 2006.01.10)
    

*   **Version 3.1**
    
*   Internationalisierung der Version 2.0 (tha 2002.11.26)
    
*   Korrektur einer Vielzahl von kleineren Fehlern (tha 2002.11.26)
    
*   **Version 2.0ß2**
    
*   Im Viewer öffnen und speichern durch die Panels der dcm-Bibliothek ersetzt: (tha 2000.8.20)
    
    *   In `ViewerFrame` die Methode `pbSave_actionPerformed` neu implementiert. Sie verwendet jetzt das `ExportPanel` aus der dcm-Bibliothek.
        
    *   In `ViewerFrame` die Methode `pbLoad_actionPerformed` neu implementiert. Sie verwendet jetzt das `ImportPanel` aus der dcm-Bibliothek.
        
    *   In `ViewerFrame` dieMethode `pbLoadSeries_actionPerformed` gelöscht, da die Funktionalität in der neuen `pbLoad_actionPerformed` enthalten ist.
        
    *   Den Button "LoadSeries" und den Menüeintrag "Serie Oeffnen" entfernt.
        
    *   Die Klasse `SeriesLoader` komplett neu implementiert.
        
*   Programmfehler  korrigiert: Nachdem man einen Artefakt ausgewählt hatte, war die Auswahl 'Kein Artefakt' wirkungslos. Lösung: Fuer 'Kein Artefakt' wurde die neue Klasse `NoArtefact` erstellt. Sie wurde auch als gültiger Artefakt in die Property- Datei `vmrt_artefacts.properties` aufgenommen. Damit fallen die Korrekturen der Indizes in `getArtefacts`, `getSelectedArtefactUI` weg. Der neue "Artefakt" wurde auch in die Property-Datei der Pulssequenzen `vmrt_sequences.properties` aufgenommen. (tha 2000.8.19)
    
*   Programmfehler korrigiert, der erst ab Java 1.3 auftritt: `pCanvas` und `pSequence` waren gleichberechtigte Komponenten. Mouse-Events durften aber bei sichtbarer `pSequence` nur an diese gelangen. Lösung: Es muss eine z-Ordnung definiert werden. Auf die gesamte `ContentPane` des Frams wird eine `JLayeredPane` gelegt. Alle Komponenten werden auf die `JLayeredPane` gezeichnet. `pSequence`  wird in die "sichtbarste" Ebene gezeichnet. (tha 2000.8.19)
    
*   Die Auswahl des `KSpaceManipulator`  wird auch im MRT ermöglicht. Dazu waren folgende spezielle Änderungen notwendig: (tha 2000.8.18)
    
    *   Der `KSpaceManipulator`  benötigt im Konstruktor eine Referenz auf die Darstellungsebene der Bilder im übergeordneten Frame. Hierzu wird eine abstrakte Klasse `ImagePanel`  definiert, die dann von den Darstellungsebenen `DVPanel` des Viewers und `MRTPanel` des MRT konkret implementiert wird.
        
    *   In der Klasse `MRTPanel` wird die abstrakt definierte Methode `createNewImage(ImagePlus)` nicht benötigt. Lediglich wegen der abstrakten Oberklasse war eine Implementation notwendig.
        
*   Im Konstruktor von `KSpaceManipulator`  Code zum zentrieren des Frames auf dem Bildschirm eingefügt. (tha 2000.8.17)
    
*   Den Quellcode auf eine neue Packagestruktur geändert. Neben den nicht im Einzelnen dokumentierten Änderung der Namen der `package` Statements ergaben sich folgende spezielle Änderungen: (tha 2000.8.17)
    
    *   In `getArtefacts` in der Methode `getArtefacts` den String `strArtefactUI` auf die neue Packagestruktur geändert.
        
    *   In `SequenceCombo` in der Methode `getSequences` den String `strSequenceUI` auf die neue Packagestruktur geändert.
        
*   jigl wird weiterhin in der Version 1.3 verwendet, aber als externe Bibliothek (als JAR-File). Die Version 1.4 bringt gegenüber der bisher  verwendeten (modifizierten) Version 1.3 einen Geschwindigkeitsvorteil von 10% in der Berechnung von Turbo- Spin- Echo Sequenzen. Allerdings scheint es ein Skalierungsproblem mit der Signalintensität den rücktransformierten Bildern zu geben. (tha 2000.8.15)
    
*   In den Klassen `FFTTools`, `ImagePlus`, `Motion`, `PeriodicMotion` und `TurboSpinEcho` die statischen Methodenaufrufe `doFFT(Image, boolean)` auf die neu in `FFTTools` aufgenommenen statischen Methoden `forwardFFT` und `reverseFFT` geändert:
    
    *   Ab der jigl-Bibliothek Version 1.4  ist die statische  Methode `doFFT` nicht mehr implementiert.
        
    *   In Version 1.3 gibt es zu `doFFT` die alternativen Methoden `**static**` `forward(Image)` und `**static**` `inverse(Image)`.
        
    *   In Version 1.4 gibt es nur noch die Methoden `forward(Image)` und `reverse(Image)`, beide nicht statisch.
        
    *   Um eine Kompatibilität mit beiden Versionen von jigl zu ermöglichen, werden die Methodenaufruf über das Reflection-Interface von Java implementiert`.` (tha 2000.8.15)
        
*   Ein Bug  beim Laden des Referenzdatensatzes aus einem JAR-File beseitigt: `CaseInfoFrame.setInfo(File, String)` gelöscht. In `FileLoader` die Methode `getCaseInfoFile` zugunsten von `getCaseInfoURL` ausgetauscht. In `FileLoader.setReferenceData()` wird `idxParent = null` gesetzt, da es sich um keine reale Datei handelt. (tha 2000.8.14)
    
*   Debug-Output in `PeriodicMotion.motion_Std` auskommentiert. (tha 2000.8.14)
    
*   Debug-Output in `Motion.motion_Std` auskommentiert. (tha 2000.8.14)
    
*   Debug-Output in `LabelTFLabelPanel.pbUp_actionPerformed` auskommentiert. (tha 2000.8.14)
    
*   Debug-Output in `Pulsesequence.convertIntensityMatrixTo12Bit` auskommentiert. (tha 2000.8.14)
    
*   Im Quellcode die absoluten Methodenreferenzen `javax.swing...` durch `**import** javax.swing.*` ersetzt. (tha 2000.8.13)
    
*   In der Methode `KSpaceManipulator.jbInit` die Höhe des Frames von 539 auf 547 Pixel gesetzt.
    
*   In `KSpaceManipulatorCanvas.paint` die Debug-Meldungen gelöscht. (tha 2000.8.13)
    
*   In `LabTFLabTFPanel.jbInit` und `LabelTFLabelPane.jbInit`  alle Dateinamen der GIF-Bilder in Kleinbuchstaben umgewandelt. (tha 2000.8.13)
    
*   In `VMRTFrame.jbInit` alle Dateinamen der GIF-Bilder in Kleinbuchstaben umgewandelt. (tha 2000.8.13)
    
*   **Achtung:** Die jigl-Bibliothek speichert die Bildmatrizen in der Form `data[y][x]`, dieses Projekt aber in der Form `data[x][y]`. Deshalb dürfen die jigl-Konstruktoren, die im Aufruf eine Bildmatrix vorsehen **nicht** **verwendet** werden. (tha 2000.8.13)
    
*   In `KSpaceManipulatorCanvas.recalcTargetImage` die x- und y-Koordinate getauscht. Dies war ein Bug, der durch die ebenfalls falsche Koordinatenrichtung in `ImagePlus.calcFFT()` bislang kompensiert wurde. (tha 2000.8.13)
    
*   Methode `FFTTools.getFFT(float[][])` gelöscht, da sie im Projekt nicht mehr benötigt wird. (tha 2000.8.13)
    
*   In der jigl-Bibliothek Version 1.3 und 1.4 ist die Methode `RealGrayImage(float[][] dat)` fehlerhaft implementiert. Um die Bibliothek trotzdem als Ganzes übernehmen zu können, sind in `FFTTools` und in `ImagePlus` einige Anpassungen notwendig: (tha 2000.8.13)
    
    *   In `FFTTools` Methode `RealGrayImage` `makeRealGrayImage(int, int, short[])` eingefügt.
        
    *   In `FFTTools` Methode `RealGrayImage` `makeRealGrayImage(float[][])` eingefügt.
        
    *   In `FFTTools.getFFT` den Aufruf von `RealGrayImage(float[][])` durch `makeRealGrayImage(float[][])` ersetzt.
        
    *   In `ImagePlus. calcFFT` den Aufruf `RealGrayImage(float[][])` durch `FFTTools.makeRealGrayImage(int, int, short[][])` ersetzt.
        
*   **Erste im Web publizierte Version 2.0ß1**
    
*   Korrektur kleinere Fehler (tha)
    
*   Neuformatierung des Quellcodes mit JIndent nach den Sun-Konventionen (tha)
    
*   Benutzerinterface vereinheitlicht. Insbesondere identische Knöpfe in der Hauptebene und Menüeinträge.
    
*   Implementation einer "Info-Dialogbox" im MRT zur Darstellung von klinischen Informationen zu dem ausgewählten Rohdatensatz
    
*   Implementation eines "Testbildes/-serie" in den Viewer (tha)
    
*   Modifikation des Testdatensatzes im MRT (tha)
    
*   Möglichkeit, den Viewer und den MRT als Applet und Application zu starten (tha)
    
*   Umstellung auf Version 2.31 der Dcm-Lib (tha)
    
*   Umstellung auf vertikale Scrollbalken (CSC)
    
*   **Version 1.0 entspricht dem Stand der Diplomarbeit**
    

**Internationalisierung**

Die Defaultsprache ist Englisch. Die folgenden Dateien mussen zur Internationalisierung an die neue Sprache angepasst werden. Dazu muss an den Dateinamen der Ländercode der neuen Sprache angehängt werden. Beispiel: mrt\_loc.properties gilt für die englische (default) Version, mrt\_log\_de.properties für die deutsche Lokalisation.

*   **vmrt\_sequences.properties**
    
*   **vmrt\_artefacts.properties**
    
*   virtual.mrt.resources: **mrt\_loc.properties**
    
*   virtual.mrt.artefacts.resources: **artefacts\_loc.properties**
    
*   virtual.mrt.sequences.resources: **sequences\_loc.properties**
    
*   virtual.tools.resources: **tools\_loc.properties**
    

Für die folgenden HTML-Dateien muss die jeweilige Sprachversion (auch "\_en" für die Defaultsprache) hinterlegt sein. Diese Dateien werden allerdings nicht über den Java eigenen Rource-Mechanismus, sondern über Referencen in der Datei mrt\_loc.properties eigelesen:

*   virtual.mrt.resources: **refcaseinfo\_en.htm**
    
*   virtual.mrt.resources: **splash\_en.htm**
    

Zur Darstellung der Informationen über Patienten werden ebenfalls HTML-Dateien verwendet, die in der jeweilige Sprachversion (auch "\_en" für die Defaultsprache) hinterlegt sein sollten Für jede Sprache muss allerdings eine eigene ".idx" Datei vorhanden sein, in der dann unter dem Schlüssel "name.filename" eine Referenz auf die entsprechende HTML-Datei hinterlegt ist.

**Relaese Notes  
**(Zusammenfassung)

*   Im Viewer werden jetzt die Import- und Export-Panels der dcm-Bibliothek für öffnen und speichern von Bildern verwendet. (tha)
    
*   Der Inhalt der Property- Datei `vmrt_artefacts.properties` und `vmrt_sequences.properties` wurde gegenüber der Vorversion geändert. (tha)
    
*   Programmfehler  korrigiert: Auch nach einer Berechnung mit einem Artefakt ist die Auswahl 'Kein Artefakt' wirkungsam. (tha)
    
*   Die k-Space Funktionen des Viewers sind in gleicher Weise auch im MRT vorhanden.
    
*   Programmfehler ab Java 1.3 korrigiert: Im MRT verschwindet das Panel für die Sequenzeinstellungen nicht mehr, wenn sich die Maus über einem Bedienelement befindet (tha). 
    
*   **Erste im Web publizierte Version 2.0ß1**
    
*   Korrektur kleinere Fehler (tha)
    
*   Neuformatierung des Quellcodes mit JIndent nach den Sun-Konventionen (tha)
    
*   Benutzerinterface vereinheitlicht. Insbesondere identische Knöpfe in der Hauptebene und Menüeinträge.
    
*   Implementation einer "Info-Dialogbox" im MRT zur Darstellung von klinischen Informationen zu dem ausgewählten Rohdatensatz. (tha)
    
*   Implementation eines "Testbildes/-serie" in den Viewer (tha)
    
*   Modifikation des Testdatensatzes im MRT (tha)
    
*   Möglichkeit, den Viewer und den MRT als Applet und Application zu starten (tha)
    
*   Umstellung auf Version 2.31 der Dcm-Lib (tha)
    
*   Umstellung auf vertikale Scrollbalken (csc)
    
*   **Version 1.0 entspricht dem Stand der Diplomarbeit**
