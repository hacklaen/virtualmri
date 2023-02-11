**Virtual MR Scanner**

A realistic simulation of a MRI was developed. For the user it should be possible to change all relevant setting of the virtual scanner and to adapt them to the expected pathology. Students and doctors in training are the target group.
At the moment the pulse sequence classes SR, IR, SE, TSE, FLASH and FISP are implemented. By a plugin mechanism this list can be easily extended. Parameters, like TR, TE, TI, flip-angle or echo train length, can be adjusted. The choice of matrix size, FOV, slice-thickness and number of acquisitions affect the signal-to-noise ratio of the images. In a first step, the simulation calculates the signal intensity in the k-space. Aliasing- and motion-artifacts are simulated by modifying the k-space data. In a last step, a 2D-fourier-transform of the k-space data is performed. Window and center of the resulting images can be changed. The simulation time can be optionally delayed till the real measuring time. The algorithms of the simulation are based on parameter-images of the three physical basic items T1, T2 and proton density. They are calculated once from a patient examination with suitable pulse sequences. To work out pathologies a small graphic tool is included. The graphical user interface is oriented on a real MR scanner. The software is implemented in pure Java under the GPL license.

On a 500 MHz PC the software calculates an image in 5 to 20 seconds, depending on the pulse sequence and the degree of the desired artifacts.

An interactive simulation of a real world examination is possible on a standard PC. The users can study the operation of a costly and not everywhere available equipment on their desktop.

**RSNA 2002**
The current version of the simulation was presented at the RSNA 2002, 88th Scientific Assembly and Annual Meeting, Chicago, IL, USA. During the meeting a lecture was given. The presentation was given the RSNA infoRAD award cum laude.


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
    

**Bekannte Fehler**

*   Die Applets laufen nicht über das Web. Befinden sie sich lokal auf dem Rechner starten sie unter Internet Explorer. Unter Netscape ist dies nicht nicht möglich.
    
*   Für Java Versionen 1.1.8 und 1.2:
    
    *   Sobald der Mauszeiger auf ein GUI-Element der Parametereinstellung der Meßsequenzen zeigt, wird das Panel deaktiviert.
        
    *   In der Info-Dialgbox für den ausgewählten Fall erscheinen einige Rechtecke mit der Bezeichnung "Header", "Titel" oder "Meta".
        

**To Do Liste MRT/Viewer  
**sortiert nach Prioritäten (2000.8.20)

1.  Property-Datei `vmrt_artefacts.properties`: Es sollte nur überprüft werden, ob \_Class\_x und \_Name\_x vorhanden sind. Es besteht in keine Notwendigkeit, dass der Index bei einer bestimmten Zahl beginnt und fortlaufend ist.
    
2.  Property- Datei `vmrt_sequences.properties`: Es sollte nur überprüft werden, ob \_Class\_x und \_Name\_x vorhanden sind. Es besteht in keine Notwendigkeit, dass der Index bei einer bestimmten Zahl beginnt und fortlaufend ist. Zur Zeit muss man für jede Sequenz jeden in `vmrt_artefacts.properties` definierten Artefakt aufführen. Macht man das nicht, werden die Artefaktmethoden nicht ausgeführt, obwohl der Artefakt ausgewählt ist. Mögliche Lösung: In `Artefact` wird eine abstrakte Methode definiert, die als Standardberechnungsmethode fungiert. Diese sollte dann nicht in `vmrt_sequences.properties` eingetragen werden müssen. Nur wenn man eine andere Berechnungsmethode haben möchte, sollte ein Eintrag  notwendig sein.
    
3.  In ImagePlus die Methoden getWindow und setWindow in getWidth und setWidth umbenennen (nach DICOM wird unter dem Window die Kombination von Center und Width verstanden).
    
4.  DcmImage ist in Relikt der alten dcm-Bibliothek. Darin ist die Klasse jetzt als GeneralImageIOD implementiert. Nach Möglichkeit sollte DcmImage durch GeneralImageIOD ersetzt werden, oder zumindest als abgeleitete Klasse davon implementiert werden. Möglicherweise werden in der aktuellen Konstellation die Pixeldaten durch eine versteckte Referenz nach dem Einlesen doppelt im Speicher gehalten.
    
5.  Das DcmDataObject mit den  Header-Informationen sollt von ImagePlus in die "neue" DcmImage-Klasse wechseln.
    
6.  Im Viewer, genau wie im MRT, eine Info-Fenster für Informationen über den aktuellen Fall ermöglichen. Hierzu könnte z.B. eine optionale Datei mit Namen "info.html" im Verzeichnis der einzulesenen Bilder  hinterlegt werden.
    
7.  Der Viewer sollte beim Aufruf als Applet optional einen Parameter verstehen, über den ihm der Name einer Indexdatei übergeben werden kann, in der die Namen von DICOM-Dateien auf dem Server stehen.
    
8.  Im Viewer und dem MRT sollte ein Menupunkt untergebracht werden, über den das Gerät, sofern es als Applet gestartet wird, Daten vom Server laden kann.
    
9.  **_In Arbeit CSC:_** Im Quellcode wurden an vielen Stellen expliziete Windows-Pfad-Separatoren ('\\') in Dateinamen angegeben. Hier muss überprüft werden, ob alle durch File.separator ersetzt wurden
    
10.  **_In Arbeit CSC:_** Alle Property-Dateien werden z.Zt. mit "eigenen" Routinen gelesen bzw. geschrieben. Dies sollt generell durch die Java-eigene Variante ersetzt werden. Dadurch entfallen dann auch die "" zur Kennzeichnung von String in der Property Datei. Beispiel: Auswertung der Indexdatei der Rohdaten des Tomografen.
    
11.  **_In Arbeit CSC:_** Können nicht alle Property-Dateien durch eine ersetzt werden? Man könnte z.B. alle Properties des Tomografen mit "vmrt." beginnen lassen und alle für den Viewer mit "dv.".
    
12.  **_In Arbeit CSC:_** Der Speicherort der Property-Dateien sollte klar definiert sein. 1. im user.dir, 2. im classpath. In der jetzigen Variante kann man nicht einfach alle class-Dateien in einen jar-File compilieren, da vmrt\_artefact und vmrt\_sequence im classpath liegen müssen.
    
13.  **_In Arbeit CSC:_** Property-Dateien sollten nicht generell vom Programm aus geschrieben werden. Man weiß ja nicht, ob der User die Programme nicht evl. von einer CD startet. Eine Möglichkeit wäre z.B. in der normalen Property Datei vorzugeben, wo standardmäßig das Default-Verzeichnis für die Suche nach Dateien ist. Als Ergänzung könnte man vom Programm aus versuchen in user.dir eine temporäre Datei mit dem zuletzt verwendeten Verzeichnis zu schreiben. Wenn das nicht gelingt, darf aber kein Fehler generiert werden. Nach dieser optionalen, temporären Datei könnte dann beim nächsten Programmstart gesucht werden.
    
14.  **_In Arbeit CSC:_** Der Viewer sollte mit DICOM-Bildern beliebiger Matrixgröße zurechtkommen.
    

**To Do Liste Raw Editor  
**sortiert nach Prioritäten (2000.8.9)

1.  Calculator: Die Mathematik des Plugin funktioniert nicht zuverlässig. Hier sollten andere/alternative Verfahren, wie der Marquard-Algorithmus implementiert werden.
    
2.  Umstellung der Projektdateien auf JBuilder Foundation 3.5.
    
3.  Calculator & Painter: Die Klasse ExportDialog wird von beiden Plugins benutzt. Sie gehört deshalb ins Package "tools".
    
4.  Im Quellcode wurden an vielen Stellen expliziete Windows-Pfad-Separatoren ('\\') in Dateinamen angegeben. Hier muss überprüft werden, ob alle durch File.separator ersetzt wurden
    
5.  Painter: Hier sollte die Möglichkeit eingebaut werden, die Bilder zu fenstern. In der aktuellen Version kann man manchmal "gar nichts" erkennen.
    
6.  Painter: In der Dialogbox sollte ein Knopf "Speichern" eingebaut werden, um auch Zwischenergebnisse sichern zu können.
    
7.  Painter: Das Umschalten zwischen Umriss/ausgefüllt zeichnen ist nicht intuitiv. Evl. dazu zwei Radioknöpfe zum Umschalten einbauen.
    
8.  Painter: "Undo" entweder ausschreiben, oder als Icon dastellen. Die Icons zur Auswahl des 4. Bildes mit grauem Hintergrund versehen.
    
9.  Calculator: Den Quellcode mit dem Ziel neu strukturieren, dass man verschieden Berechnungsverfahren benutzen kann. Das einzige jetzt implementierte wäre LeastSquare. Die Funktionalität aus dem eigentlichen Plugin RawData\_Calculator herausnehmen und in eine eigene Klasse kapseln. In dieser ist aber noch keine Mathematik implementiert. Lediglich der Rahmen und Schleifen über die Bildzeilen und Spalten. Für jeden Bildpunkt wird dann eine Klasse mit der eigentlichen Mathematik aufgerufen. Sie bekommt alle Rohdatenwerte eines Voxels und ein Array mit den DcmDataObjekts aller Bilder übergeben. Diese Berechnungsklasse wird zunächst abstract deklariert. Es muß Methoden geben, welche Parameter die Klasse berechnet etwa "boolean isCalculatingT1()" und zur Rückgabe der Parameterwerte, etwa "double getT1()". Für jedes mathematisches Verfahren muß dann eine konkrete Implementation vorgenommen werden.
    
10.  Calculator entprechend Punkt 9: Im ImportDialog gibt es nur noch eine Zeile mit den Eingane für dden Bereich der Bildnummern und die minimale Signalintensität, ab der die Berechnung startet. Man sollte vielleicht noch 3 weitere Eingabefelder für Zahlenwerte vorsehen über die die verschiedenen math. Modelle Parameter übergeben bekommen (schon in der abstrakten Klasse berücksichtigen).
    
11.  Calculator entprechend Punkt 9: Ein Auswahlmenü (Pop-up-menu) zur Auswahl des zu verwendenden Algorithmus einbauen.
    
12.  Calculator entprechend Punkt 9: Alle Rohdatenbilder enthalten zunächst den Wert 0. Durch ein oder mehrfaches Aufrufen vonBerechnungsalgorithemen werden sie dann mit Werten belegt.
    
13.  Calculator entprechend Punkt 9: Es muß einen Knopf "Save" geben, ducrh den die Werte abgespeichertwerden.
    
14.  Calculator: Dialogbox zentrieren und etwas "ansprechender" gestalten".
    
15.  Calculator: Umstellen des Datenformates der T1,T2,PD etc. Bilder von DICOM auf PNG: Damit wäre es wesentlich einfacher möglich , die Basisbilder in einem anderen Grafikprogramm als dem Stack-Painter zu bearbeiten. PNG erlaubt 16-Bit Graustufenbilder. T1, T2 und PD könnten dann in der Form der physikalischen Zahlenwerte gespeichert werden. SZ und Flow könnten mit (physikalische Größe\*100)+32768 als vorzeichenbehaftete Größen abgelegt werden. Die Standardnamen der Dateien sollten dann name\_t1.png usw. lauten.