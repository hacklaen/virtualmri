# Virtual MRI

Lokale Browser-Version des [Virtual MR Scanner](https://github.com/hacklaen/virtualmri) (Thomas Hackländer, Christian Schalla, Andreas Trümper). Die Simulation läuft vollständig im Browser. Es ist kein Build-Schritt und kein Server nötig.

## Start

`index.html` im Browser öffnen (Doppelklick oder Datei → Öffnen).

Beim ersten Aufruf wird das mitgelieferte Beispielgehirn mit einer T1-gewichteten Spin-Echo-Sequenz gerechnet. Die Vorlagen T1, T2, PD, TSE, FLAIR, STIR, GRE, Einfaltung und Bewegung setzen typische Parameter und starten die Rechnung.

## Bedienung

- Datensatz: Beispielgehirn (Parameterkarten der Java-Anwendung) oder Referenzphantom mit Gewebequadraten.
- Sequenz: Spin-Echo, Turbo-Spin-Echo, drei Gradienten-Echos, Inversion-Recovery, Saturation-Recovery.
- Geometrie: FoV, rechteckiges FoV, Schichtdicke, Mittelungen, Spule, Frequenz- und Phasen-Oversampling. Daraus folgen Pixelgröße, relatives SNR und Messzeit.
- Artefakte: Translation und periodische Bewegung. Die Phasenrichtung ist vertikal.
- Bild: Ziehen fenstert (horizontal Breite, vertikal Zentrum). Doppelklick stellt die automatische Fensterung wieder her. PNG speichert das aktuelle Bild.
- k-Raum bearbeiten, unter dem k-Raum-Bild: Ränder, ein Rechteck um die Mitte oder jede n-te Zeile und Spalte auf null setzen, rücktransformieren und das Ergebnis in die Ansicht übernehmen. Die Koordinaten sind der zentrierte k-Raum, die Mitte sind die tiefen Frequenzen.

Der Mauszeiger über dem Bild zeigt Signal sowie T1, T2 und Protonendichte des Pixels.

## Was gerechnet wird

Aus den Karten für T1, T2 und Protonendichte entsteht pro Pixel die Signalintensität der gewählten Sequenz. Turbo-Spin-Echo setzt den k-Raum aus mehreren Echos zusammen; das Echo nahe der effektiven Echozeit belegt die Mitte. Bewegung verschiebt das Objekt zwischen Phasenkodierschritten und übernimmt jeweils die passende k-Raum-Zeile. Ein zu kleines FoV ohne Oversampling faltet Signal von außen in das Bild. Anschließend wird das FoV auf 256×256 skaliert. Rauschen folgt dem relativen SNR (Referenz 1,000 bei FoV 256 mm, Schichtdicke 6 mm, einer Mittelung, ohne Oversampling) und ist bei der Körperspule höher.

Die Matrix bleibt 256×256, wie in der Java-Oberfläche.

## Abweichungen von der Java-Version

Die Formeln sind dieselben Klassen (SE, TSE, FLASH, gespoiltes und refokussiertes Gradienten-Echo, IR, SR). An vier Stellen folgt die Browser-Version der beabsichtigten Physik statt eines Fehlers im Quelltext:

- Der Kosinus des Flipwinkels wird als Kosinus berechnet. Im Java-Code stand dort `Math.sin`.
- Inversion-Recovery nutzt `exp(−TR/T1)`. Im Java-Code stand `exp(−TR/TI)`.
- T2* addiert γ·ΔB in 1/ms. Das Java-Produkt lag in Hz und veränderte T2 praktisch nicht.
- Beim refokussierten Gradienten-Echo ist die Multiplikation mit `cos α` wieder eingesetzt, und die Echozeit dämpft mit T2*.

Der k-Raum ist eine zweidimensionale FFT. Die Java-Version hat die eindimensionale FFT der Bibliothek jigl aufgerufen. Die k-Raum-Mitte entspricht dadurch den tiefen Ortsfrequenzen, wie es der Kommentar im Turbo-Spin-Echo-Code beschreibt. Periodische Bewegung läuft als Dreieck, nicht als Sägezahn. Rauschen skaliert durchgehend mit 1/SNR. Bewegung ist auch beim Turbo-Spin-Echo möglich; die Java-Version hatte dafür nur eine Warnung.

## Lizenz

GNU General Public License, Version 2. Siehe `LICENSE`. Die Beispielkarten stammen aus `src/virtual/mrt/resources/sample` des Originals.
