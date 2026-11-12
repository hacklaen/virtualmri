# Portierung von Java nach JavaScript

Dieses Dokument beschreibt, wie die Browser-Version aus der Java-Version des Virtual MR Scanner entstanden ist und was dabei bewusst anders gemacht wurde. Es ist eine Ausbildungs-Simulation, keine Diagnose.

## Ausgangslage und Branches

- Original: Java-Anwendung von Thomas Hackländer, Christian Schalla und Andreas Trümper (1999 bis 2006).
- Die unveränderte Java-Version liegt auf dem Branch `java-org`. Sie ist eine Kopie des früheren Hauptbranches (Commit `c7c18cc`), die Historie bleibt erhalten.
- `main` enthält jetzt die JavaScript-Version. Die Java-Quellen, Build-Dateien (`build.xml`, `nbproject/`), Startdateien (`vmrt_*.bat`, Mac-Apps), Bibliotheken (`lib/`, `dist/`), Javadoc und die alten Listen (`Changelog.md`, `Todolist.md`) sind dort entfernt und nur noch auf `java-org` zu finden.
- Behalten wurden `assets/` (Parameterbild-Rechner, DICOM-Werkzeuge, Beispieluntersuchung) und `manual/` (Handbuch der Java-Version) als Herkunftsmaterial.

## Randbedingungen der Browser-Version

- Läuft lokal im Browser: `index.html` per Doppelklick öffnen, ohne Build und ohne Server.
- Deshalb klassische `<script>`-Tags, keine ES-Module und kein `fetch`. Unter `file://` kann der Browser sonst nichts nachladen.
- `js/sample-brain.js` enthält die Beispielkarten als Base64, aus demselben Grund.
- Oberfläche auf Deutsch. Matrix 256 × 256 und Objektgitter 256 mm wie in der Java-Oberfläche.

## Was portiert wurde

Es ist kein Zeile-für-Zeile-Abbild der Swing-Oberfläche. Portiert ist die Rechenkette aus `src/virtual/mrt` und der k-Raum-Manipulator aus `src/virtual/tools/KSpaceManipulator.java`.

| Java (Branch `java-org`) | JavaScript (`main`) |
|---|---|
| `virtual.mrt.sequences.*` (SpinEcho, TurboSpinEcho, Flash, GESpoiled, GERefocused, InversionRecovery, SaturationRecovery) | `js/simulate.js` |
| `virtual.mrt.artefacts.*` (Motion, PeriodicMotion, NoArtefact) | `js/simulate.js` |
| `PulsesequenceUI.getSNRatio` | `js/simulate.js` (SNR, Messzeit) |
| `virtual.tools.FFTTools`, jigl-FFT | `js/fft.js` |
| Referenzdatensatz, Parameterkarten | `js/phantom.js`, `js/sample-brain.js` |
| `virtual.tools.KSpaceManipulator` | Dialog in `index.html` und `js/app.js` |
| Swing-Hauptfenster (`VMRTFrame`) | `index.html`, `css/app.css`, `js/app.js` |

### Dateien der Browser-Version

- `index.html`: deutsche Oberfläche
- `css/app.css`: Layout, Reiter, Dialog
- `js/fft.js`: Radix-2-FFT, 1D und 2D. Die Inverse teilt pro Achse durch n.
- `js/phantom.js`: Referenzphantom und Beispielgehirn, N = 256
- `js/sample-brain.js`: 16-Bit-Karten T1, T2, PD aus den Beispiel-TIFFs
- `js/simulate.js`: Sequenzen, Rauschen, Einfaltung, Bewegung, k-Raum
- `js/app.js`: Seite, Beispiele, Fensterung, Dialog
- `README.md`, `LICENSE`

## Datenübernahme

Die Beispiel-TIFFs der Java-Version sind 16-Bit, Big-Endian, 256 × 256, Streifen ab Offset 768. Im Browser liegen sie als Little-Endian-Uint16 in Base64.

- T1 und T2 sind Millisekunden.
- Die Protonendichte des Referenzphantoms ist Prozent, skaliert wie in Java mit `(65530 * Prozent) / 100`.
- Die Protonendichte des Gehirns ist in Scanner-Einheiten (Maximum etwa 2048), nicht Prozent.
- Der Hintergrund der T1-Karte ist oft 1. Die Protonendichte außerhalb des Kopfes ist 0, das Signal dort also 0.

## Sequenzen

Spin-Echo, Turbo-Spin-Echo, Gradienten-Echo (FLASH, kohärent mit E2), gespoiltes Gradienten-Echo, refokussiertes Gradienten-Echo, Inversion-Recovery und Saturation-Recovery. Standard beim Start: Spin-Echo, TR 500 ms, TE 15 ms, Beispielgehirn.

Turbo-Spin-Echo legt das Echo, das der effektiven Echozeit am nächsten liegt, in die k-Raum-Mitte.

## Signal-Rausch-Verhältnis und Geometrie

Das SNR stammt aus `PulsesequenceUI.getSNRatio`. Der Faktor 3,44 macht die Referenz (FoV 256 mm, Schichtdicke 6 mm, eine Mittelung, kein Oversampling, Matrix 256) zu 1,000.

- Phasen-Oversampling verdoppelt die Phasenkodierschritte und halbiert die Mittelungen, wenn mehr als eine Mittelung eingestellt ist.
- Frequenz-Oversampling verdoppelt das FoV in der Bandbreite.
- Reihenfolge: Einfaltung, dann FoV-Skalierung, dann rechteckiges FoV. Die Phasenrichtung ist vertikal.
- Das Rauschen ist stetig: `sigmaRel = 0.08 / max(snr, 0.02)`. Bei der Körperspule kommen 5 Prozent des Mittelwerts dazu.

## Artefakte

Bewegung und periodische Bewegung übernehmen k-Raum-Zeilen in 16 Schritten, um die Mitte zentriert. Die periodische Bewegung ist ein Dreieck. Der Java-Kommentar beschrieb das so, der Java-Code war ein Sägezahn.

## Bewusste Abweichungen von Java

Vier Formelstellen folgen der beabsichtigten Physik und nicht dem fehlerhaften Ausdruck im Java-Code:

1. Der Kosinus des Flipwinkels wird als Kosinus berechnet. In Java stand `Math.sin` in der Variablen `cosangel`.
2. Inversion-Recovery nutzt `exp(−TR/T1)`, nicht `exp(−TR/TI)`.
3. T2* addiert γ·ΔB in 1/ms. Konstanten: `GYRO = 42.6`, `DELTAB = 19.5e-9`, `T2_STAR_RATE = GYRO * DELTAB * 1e-3`. Das Java-Produkt lag in Hz und veränderte T2 praktisch nicht.
4. Beim refokussierten Gradienten-Echo ist die fehlende Multiplikation mit `cos α` wieder eingesetzt, und die Echozeit dämpft mit T2*.

Weitere Unterschiede:

- Der k-Raum ist eine echte zweidimensionale FFT. Java wendete die eindimensionale FFT von jigl auf das Raster an. Die angezeigte Mitte sind dadurch die tiefen Ortsfrequenzen, wie es der Kommentar im Turbo-Spin-Echo beschreibt.
- Bewegung ist auch beim Turbo-Spin-Echo möglich. Java hatte dafür nur eine Warnung.
- Das Rauschen skaliert durchgehend mit 1/SNR.

### Validierung in der Oberfläche

- TE kleiner als TR, außer bei Saturation- und Inversion-Recovery
- TI kleiner als TR bei Inversion-Recovery
- Echozuglänge mal Echospacing kleiner als TR
- FoV 32 bis 256 mm

## k-Raum-Manipulator

Der Button unter dem k-Raum-Bild öffnet einen nativen `<dialog>`. Drei Operationen wirken auf eine Kopie des zentrierten Spektrums, in der Java-Reihenfolge: Ränder, dann Zeilen und Spalten, dann Rechteck. Koordinaten sind das verschobene Anzeigegitter, der Gleichanteil (DC) liegt bei n/2. Die Anzeige ist `log1p` des Betrags nach fftshift. Auf null gesetzt werden Real- und Imaginärteil, nicht der Logarithmus.

- Ränder: links `x <= left`, rechts `x >= n - right`, oben `y <= top`, unten `y >= n - bottom`. Links und oben sind inklusiv, wie in den Java-Schleifen. Eine eingegebene Spalte links löscht dadurch eine Spalte mehr.
- Jede n-te Zeile oder Spalte: `y % rows == 0` bzw. `x % columns == 0`. In der Oberfläche gilt 0 als aus, Werte unter 2 werden ignoriert. Der Java-Regler begann bei 2.
- Inneres Rechteck: halboffen um die Mitte, `c - (breite >> 1)` inklusive bis `c + (breite >> 1)` exklusive, ebenso für die Höhe.

Rücktransformation: erneut verschieben, inverse FFT, Betrag. Das entspricht `FFTTools.getReshiftedIFFT`, das den Betrag zurückgibt. „In die Ansicht übernehmen" ersetzt das Hauptbild und zeigt dort den bearbeiteten k-Raum, nicht eine neue FFT des Betragsbilds. „Bild berechnen" verwirft das und rechnet den Scan neu.

## Oberfläche

- Sequenz, Geometrie und Artefakt sind Reiter in einem gemeinsamen Bereich. Die Reiter sind normale Buttons, keine `role="tab"`.
- Berechnete Bilder links, Einstellungen rechts.
- Die Beispielbuttons (T1, T2, PD, TSE, FLAIR, STIR, GRE, Einfaltung, Bewegung) liegen in einem eigenen Bereich unter „Bild berechnen". Eine spätere Detailanpassung ist zurückgestellt.

## Lizenz

Die Java-Dateiköpfe nennen die GNU GPL Version 2. Die frühere Datei `License` im Java-Repository enthielt den Text der GPL Version 3. Die Browser-Version führt deshalb den vollständigen GPL-2-Text in `LICENSE` und kennzeichnet sich in den Dateiköpfen als GPL-2-Ableitung. Der frühere GPL-3-Text bleibt auf dem Branch `java-org` erhalten.

## Herkunft und Literatur

Das Projekt wurde auf der RSNA 2002 (88th Scientific Assembly and Annual Meeting, Chicago) vorgestellt und mit dem infoRAD award cum laude ausgezeichnet.

- Schalla C, Trümper A: Entwicklung eines virtuellen klinischen Kernspintomographen. Diplomarbeit 1999. Interner Bericht der Universität Dortmund, Fachbereich Informatik.
- Hackländer T, Schalla C, Trümper A, Mertens H, Hiltner J: Virtueller Kernspintomograph. 81. Deutscher Röntgenkongreß, Wiesbaden. RoFo Fortschr Geb Röntgenstr Neuen Bildgeb Verfahr 172 (2000) S135.
- Hacklaender T, Schalla C, Truemper A, Mertens H, Hiltner J, Cramer BM: A Virtual MR Scanner for Education. RSNA 2002. Radiology 225 (P) (2002) 757.
- Hackländer T, Mertens H, Cramer BM: Computersimulation eines klinischen Magnetresonanztomographen für Ausbildungszwecke. RoFo 176 (2004) 1151-1156.
- Hackländer T, Mertens H: Virtual MRI: A PC based simulation of a clinical MR scanner. Acad Radiol 12,1 (2005) 85-96.

## Vorgehen beim Übernehmen in dieses Repository

1. Java-Stand sichern: Branch `java-org` aus dem damaligen `main` angelegt, ohne Force-Push.
2. Auf `main` die Java-Quellen und die davon abhängigen Dateien entfernen.
3. Die Dateien der Browser-Version (`index.html`, `css/`, `js/`, `README.md`, `LICENSE`) aus dem lokalen Ordner `virtualmri-2` kopieren. Die Kopie wurde mit `diff` auf Identität geprüft, die JS-Dateien mit `node --check` auf Syntax.
4. Diese Dokumentation anlegen.
