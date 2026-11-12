/* Virtual MRI — parameter phantoms
 * Derivative of virtualmri (Thomas Hacklaender, Christian Schalla, Andreas Truemper), GPL-2.
 * Reference squares follow FileLoader.loadReferenceData (T1/T2 in ms, PD scaled like the Java tool).
 */
(function (root) {
  const N = 256;

  const TISSUES = [
    { name: "Fett", t1: 280, t2: 50, pd: 80 },
    { name: "Methämoglobin", t1: 460, t2: 106, pd: 86 },
    { name: "Weiße Substanz", t1: 510, t2: 67, pd: 54 },
    { name: "Graue Substanz", t1: 760, t2: 77, pd: 62 },
    { name: "Hirnödem", t1: 900, t2: 126, pd: 77 },
    { name: "Zyste", t1: 1080, t2: 215, pd: 100 },
    { name: "Liquor", t1: 2650, t2: 280, pd: 89 },
    { name: "Wasser", t1: 4000, t2: 4000, pd: 100 }
  ];

  function draw(t1, t2, pd, x, y, w, h, t1v, t2v, pdv) {
    const scaled = (65530 * Math.min(pdv, 100)) / 100;
    for (let iy = y; iy < y + h; iy++) {
      for (let ix = x; ix < x + w; ix++) {
        const i = iy * N + ix;
        t1[i] = t1v;
        t2[i] = t2v;
        pd[i] = scaled;
      }
    }
  }

  function referencePhantom() {
    const t1 = new Float64Array(N * N);
    const t2 = new Float64Array(N * N);
    const pd = new Float64Array(N * N);
    const rects = [
      [16, 16, 32, 32, 280, 50, 80],
      [80, 16, 32, 32, 460, 106, 86],
      [144, 16, 32, 32, 510, 67, 54],
      [208, 16, 32, 32, 760, 77, 62],
      [16, 80, 32, 32, 900, 126, 77],
      [80, 80, 32, 32, 1080, 215, 100],
      [144, 80, 32, 32, 2650, 280, 89],
      [208, 80, 32, 32, 4000, 4000, 100],
      [16, 144, 96, 96, 280, 50, 80],
      [63, 144, 3, 96, 0, 0, 0],
      [16, 191, 96, 3, 0, 0, 0],
      [144, 144, 96, 96, 4000, 4000, 100],
      [191, 191, 3, 3, 0, 0, 0]
    ];
    for (let r = 0; r < rects.length; r++) {
      draw(t1, t2, pd, rects[r][0], rects[r][1], rects[r][2], rects[r][3], rects[r][4], rects[r][5], rects[r][6]);
    }
    return {
      id: "reference",
      name: "Referenzphantom",
      n: N,
      t1: t1,
      t2: t2,
      pd: pd,
      note: "Quadrate mit Literaturwerten bei etwa 1 Tesla. Unten ein Fettfeld mit Kreuz und ein Wasserfeld mit Punkt, zum Beurteilen der Schärfe."
    };
  }

  function fromU16(src) {
    const out = new Float64Array(src.length);
    for (let i = 0; i < src.length; i++) out[i] = src[i];
    return out;
  }

  function brainPhantom() {
    const sample = root.SAMPLE_BRAIN;
    if (!sample) throw new Error("Beispielgehirn fehlt");
    return {
      id: "brain",
      name: "Beispielgehirn",
      n: sample.size,
      t1: fromU16(sample.t1),
      t2: fromU16(sample.t2),
      pd: fromU16(sample.pd),
      note: "Parameterkarten aus der Beispieluntersuchung des Java-Programms (Gehirn, transversal, ohne Pathologie). T1 und T2 in Millisekunden."
    };
  }

  root.VMRI_PHANTOM = {
    N: N,
    TISSUES: TISSUES,
    referencePhantom: referencePhantom,
    brainPhantom: brainPhantom
  };
})(globalThis);
