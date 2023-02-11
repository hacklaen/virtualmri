**Bekannte Fehler**

*   Die Applets laufen nicht über das Web. Befinden sie sich lokal auf dem Rechner starten sie unter Internet Explorer. Unter Netscape ist dies nicht nicht möglich.
    
*   Für Java Versionen 1.1.8 und 1.2:
    
    *   Sobald der Mauszeiger auf ein GUI-Element der Parametereinstellung der Meßsequenzen zeigt, wird das Panel deaktiviert.
        
    *   In der Info-Dialgbox für den ausgewählten Fall erscheinen einige Rechtecke mit der Bezeichnung "Header", "Titel" oder "Meta".
        

**To Do Liste: MRT/Viewer**
  
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
    

**To Do Liste: Raw Editor**

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