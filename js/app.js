/* Virtual MRI — page controller
 * Derivative of virtualmri (Thomas Hacklaender, Christian Schalla, Andreas Truemper), GPL-2.
 */
(function () {
  const VMRI = globalThis.VMRI;
  const PH = globalThis.VMRI_PHANTOM;

  const $ = (id) => document.getElementById(id);

  const state = {
    phantom: null,
    image: null,
    center: 1,
    width: 1,
    drag: null
  };

  const PRESET_BASE = {
    artefact: "none",
    fov: 256,
    rect: 8,
    freqOS: false,
    phaseOS: false,
    thickness: 6,
    nex: 1,
    coil: "organ"
  };

  const PRESETS = {
    t1: { sequence: "se", tr: 500, te: 15 },
    t2: { sequence: "se", tr: 3000, te: 120 },
    pd: { sequence: "se", tr: 3000, te: 15 },
    flair: { sequence: "ir", tr: 8000, ti: 1700 },
    stir: { sequence: "ir", tr: 2000, ti: 190 },
    tse: { sequence: "tse", tr: 4000, te: 96, esp: 12, etl: 8 },
    gre: { sequence: "spoiled", tr: 50, te: 5, fa: 30 },
    alias: { sequence: "se", tr: 500, te: 15, fov: 160 },
    motion: { sequence: "se", tr: 800, te: 15, artefact: "periodic", px: 0, py: 12, pfreq: 12 }
  };

  function num(id) {
    return Number($(id).value);
  }

  function checked(id) {
    return $(id).checked;
  }

  function params() {
    return {
      sequence: $("sequence").value,
      tr: num("tr"),
      te: num("te"),
      ti: num("ti"),
      fa: num("fa"),
      esp: num("esp"),
      etl: num("etl"),
      fov: num("fov"),
      rect: num("rect"),
      thickness: num("thickness"),
      nex: num("nex"),
      freqOS: checked("freqOS"),
      phaseOS: checked("phaseOS"),
      coil: $("coil").value,
      artefact: $("artefact").value,
      motion: {
        xSpeed: num("mx"),
        ySpeed: num("my"),
        start: num("mstart"),
        stop: num("mstop")
      },
      periodic: {
        xAmp: num("px"),
        yAmp: num("py"),
        freq: num("pfreq"),
        start: num("pstart"),
        stop: num("pstop")
      }
    };
  }

  function showSequenceFields() {
    const seq = $("sequence").value;
    document.querySelectorAll("[data-seq]").forEach((node) => {
      const list = node.getAttribute("data-seq").split(" ");
      node.hidden = list.indexOf(seq) === -1;
    });
    $("formula").textContent = VMRI.formula(seq);
    const teLabel = $("te-label");
    if (teLabel) teLabel.textContent = seq === "tse" ? "TEeff (ms)" : "TE (ms)";
    const art = $("artefact").value;
    $("motion-fields").hidden = art !== "motion";
    $("periodic-fields").hidden = art !== "periodic";
  }

  function updateReadouts() {
    const p = params();
    $("snr").textContent = VMRI.snr(p).toFixed(3);
    $("scantime").textContent = VMRI.formatDuration(VMRI.scanTimeMs(p));
    const pix = p.fov / VMRI.N;
    $("pixelsize").textContent = pix.toFixed(2) + " mm";
    $("weighting").textContent = VMRI.weighting(p);
  }

  function autoWindow(data) {
    const values = [];
    for (let i = 0; i < data.length; i++) if (data[i] > 0) values.push(data[i]);
    if (values.length === 0) return { center: 1, width: 1 };
    values.sort((a, b) => a - b);
    const pick = (q) => values[Math.min(values.length - 1, Math.max(0, Math.round(q * (values.length - 1))))];
    const lo = pick(0.01);
    const hi = pick(0.995);
    const width = Math.max(1, hi - lo);
    return { center: (lo + hi) / 2, width: width };
  }

  function paint(canvas, data, n, center, width, logScale) {
    const ctx = canvas.getContext("2d");
    const image = ctx.createImageData(n, n);
    const low = center - width / 2;
    const scale = width > 0 ? 255 / width : 0;
    const px = image.data;
    for (let i = 0; i < data.length; i++) {
      let g = (data[i] - low) * scale;
      if (g < 0) g = 0;
      else if (g > 255) g = 255;
      const o = i * 4;
      px[o] = px[o + 1] = px[o + 2] = g;
      px[o + 3] = 255;
    }
    ctx.putImageData(image, 0, 0);
    if (logScale) return;
  }

  function paintMap(canvas, data) {
    const win = autoWindow(data);
    paint(canvas, data, state.phantom.n, win.center, win.width);
  }

  function paintResult() {
    if (!state.image) return;
    paint($("result"), state.image, state.phantom.n, state.center, state.width);
    $("window-center").textContent = Math.round(state.center).toString();
    $("window-width").textContent = Math.round(state.width).toString();
    $("center").value = String(Math.round(state.center));
    $("width").value = String(Math.round(Math.max(1, state.width)));
  }

  function paintKSpace(data, n) {
    const win = autoWindow(data);
    paint($("kspace"), data, n, win.center, win.width);
  }

  const kEdit = { source: null, edited: null, image: null, n: 256 };

  function intField(id, min, max) {
    const n = Math.round(Number($(id).value));
    if (!Number.isFinite(n)) return min;
    return Math.max(min, Math.min(max, n));
  }

  function lineStep(id) {
    const n = intField(id, 0, 256);
    return n >= 2 ? n : 0;
  }

  function kOptions() {
    return {
      margins: $("ks-margins").checked,
      left: intField("ks-left", 0, 255),
      right: intField("ks-right", 0, 255),
      top: intField("ks-top", 0, 255),
      bottom: intField("ks-bottom", 0, 255),
      rect: $("ks-rect").checked,
      width: intField("ks-rect-w", 0, 255),
      height: intField("ks-rect-h", 0, 255),
      lines: $("ks-lines").checked,
      rows: lineStep("ks-rows"),
      columns: lineStep("ks-cols")
    };
  }

  function syncKGroups() {
    [
      ["ks-margins", "ks-margin-fields"],
      ["ks-rect", "ks-rect-fields"],
      ["ks-lines", "ks-line-fields"]
    ].forEach((pair) => {
      const on = $(pair[0]).checked;
      $(pair[1]).querySelectorAll("input").forEach((node) => {
        node.disabled = !on;
      });
    });
  }

  function paintDialogK(canvas, spec) {
    const log = VMRI.logCentered(spec);
    const win = autoWindow(log);
    paint(canvas, log, kEdit.n, win.center, win.width);
  }

  function clearInverse() {
    const canvas = $("ks-inverse");
    canvas.getContext("2d").clearRect(0, 0, canvas.width, canvas.height);
    $("ks-inverse-note").textContent = "Noch keine Rücktransformation.";
  }

  function refreshEdited() {
    if (!kEdit.source) return;
    kEdit.edited = VMRI.editKSpace(kEdit.source, kEdit.n, kOptions());
    kEdit.image = null;
    $("ks-apply").disabled = true;
    paintDialogK($("ks-edited-k"), kEdit.edited);
    clearInverse();
  }

  function openKSpace() {
    if (!state.image) {
      setStatus("Zuerst ein Bild berechnen.", true);
      return;
    }
    kEdit.n = state.phantom.n;
    kEdit.source = VMRI.centeredSpectrum(state.image, kEdit.n);
    paint($("ks-original"), state.image, kEdit.n, state.center, state.width);
    paintDialogK($("ks-original-k"), kEdit.source);
    syncKGroups();
    refreshEdited();
    $("kspace-dialog").showModal();
  }

  function inverseKSpace() {
    if (!kEdit.edited) return;
    kEdit.image = VMRI.reconstructCentered(kEdit.edited, kEdit.n);
    const win = autoWindow(kEdit.image);
    paint($("ks-inverse"), kEdit.image, kEdit.n, win.center, win.width);
    $("ks-inverse-note").textContent = "Betrag der Rücktransformation.";
    $("ks-apply").disabled = false;
  }

  function applyKSpace() {
    if (!kEdit.image || !kEdit.edited) return;
    state.image = kEdit.image;
    const win = autoWindow(kEdit.image);
    state.center = win.center;
    state.width = win.width;
    paintResult();
    paintKSpace(VMRI.logCentered(kEdit.edited), kEdit.n);
    setStatus("Manipulierter k-Raum in die Ansicht übernommen.");
  }

  function bindKSpace() {
    syncKGroups();
    $("open-kspace").addEventListener("click", openKSpace);
    const dialog = $("kspace-dialog");
    const onEdit = () => {
      syncKGroups();
      if (dialog.open) refreshEdited();
    };
    dialog.addEventListener("input", onEdit);
    dialog.addEventListener("change", onEdit);
    $("ks-inverse-btn").addEventListener("click", inverseKSpace);
    $("ks-apply").addEventListener("click", applyKSpace);
    $("ks-close").addEventListener("click", () => dialog.close());
  }

  function drawPhantom() {
    paintMap($("map-t1"), state.phantom.t1);
    paintMap($("map-t2"), state.phantom.t2);
    paintMap($("map-pd"), state.phantom.pd);
    $("phantom-note").textContent = state.phantom.note;
    $("tissue-table").hidden = state.phantom.id !== "reference";
  }

  function setStatus(text, bad) {
    const node = $("status");
    node.textContent = text;
    node.classList.toggle("bad", !!bad);
  }

  function run() {
    showSequenceFields();
    updateReadouts();
    const button = $("run");
    button.disabled = true;
    setStatus("Berechnung läuft …");
    window.setTimeout(() => {
      const started = performance.now();
      let result;
      try {
        result = VMRI.simulate(state.phantom, params());
      } catch (err) {
        button.disabled = false;
        setStatus(err && err.message ? err.message : "Berechnung fehlgeschlagen.", true);
        return;
      }
      if (!result.ok) {
        button.disabled = false;
        setStatus(result.message, true);
        return;
      }
      state.image = result.image;
      const win = autoWindow(result.image);
      state.center = win.center;
      state.width = win.width;
      paintResult();
      paintKSpace(VMRI.kSpaceLog(result.image, result.n), result.n);
      $("open-kspace").disabled = false;
      const ms = Math.round(performance.now() - started);
      const extra = result.info ? " " + result.info : "";
      setStatus(
        "Fertig in " + ms + " ms. " + result.weighting + ". Messzeit " + VMRI.formatDuration(result.scanMs) + "." + extra
      );
      button.disabled = false;
    }, 20);
  }

  function applyPreset(key) {
    const preset = Object.assign({}, PRESET_BASE, PRESETS[key]);
    Object.keys(preset).forEach((name) => {
      if (name === "sequence") {
        $("sequence").value = preset.sequence;
        return;
      }
      if (name === "artefact") {
        $("artefact").value = preset.artefact;
        return;
      }
      if (name === "freqOS" || name === "phaseOS") {
        $(name).checked = preset[name];
        return;
      }
      const field = $(name);
      if (field) field.value = String(preset[name]);
    });
    showSequenceFields();
    updateReadouts();
    if (key === "alias") showTab("geometry");
    else if (key === "motion") showTab("artefact");
    else showTab("sequence");
    run();
  }

  function showTab(name) {
    ["sequence", "geometry", "artefact"].forEach((id) => {
      const on = id === name;
      $("panel-" + id).hidden = !on;
      $("tab-" + id).setAttribute("aria-selected", on ? "true" : "false");
    });
  }

  function loadPhantom(id) {
    state.phantom = id === "brain" ? PH.brainPhantom() : PH.referencePhantom();
    drawPhantom();
    run();
  }

  function canvasPoint(canvas, event) {
    const rect = canvas.getBoundingClientRect();
    const x = Math.floor(((event.clientX - rect.left) / rect.width) * canvas.width);
    const y = Math.floor(((event.clientY - rect.top) / rect.height) * canvas.height);
    return {
      x: Math.max(0, Math.min(canvas.width - 1, x)),
      y: Math.max(0, Math.min(canvas.height - 1, y))
    };
  }

  function onPointerMove(event) {
    if (!state.image) return;
    const canvas = $("result");
    const pt = canvasPoint(canvas, event);
    const i = pt.y * state.phantom.n + pt.x;
    const signal = state.image[i];
    const t1 = state.phantom.t1[i];
    const t2 = state.phantom.t2[i];
    const pd = state.phantom.pd[i];
    $("readout").textContent =
      "x " + pt.x + "  y " + pt.y +
      "   Signal " + Math.round(signal) +
      "   T1 " + Math.round(t1) + " ms" +
      "   T2 " + Math.round(t2) + " ms" +
      "   PD " + Math.round(pd);

    if (!state.drag) return;
    const dx = event.clientX - state.drag.x;
    const dy = event.clientY - state.drag.y;
    state.drag.x = event.clientX;
    state.drag.y = event.clientY;
    const gain = Math.max(1, state.drag.width) / 180;
    state.width = Math.max(1, state.drag.width0 + (event.clientX - state.drag.x0) * gain);
    state.center = state.drag.center0 + (event.clientY - state.drag.y0) * gain;
    state.drag.width = state.width;
    paintResult();
  }

  function bind() {
    ["sequence", "artefact", "coil", "freqOS", "phaseOS"].forEach((id) => {
      $(id).addEventListener("change", () => {
        showSequenceFields();
        updateReadouts();
      });
    });
    document.querySelectorAll("input[type=number]").forEach((node) => {
      node.addEventListener("input", updateReadouts);
    });
    $("run").addEventListener("click", run);
    $("phantom").addEventListener("change", () => loadPhantom($("phantom").value));
    document.querySelectorAll("[data-preset]").forEach((node) => {
      node.addEventListener("click", () => applyPreset(node.getAttribute("data-preset")));
    });
    $("center").addEventListener("input", () => {
      state.center = num("center");
      paintResult();
    });
    $("width").addEventListener("input", () => {
      state.width = Math.max(1, num("width"));
      paintResult();
    });
    const canvas = $("result");
    canvas.addEventListener("pointerdown", (event) => {
      canvas.setPointerCapture(event.pointerId);
      state.drag = {
        x: event.clientX,
        y: event.clientY,
        x0: event.clientX,
        y0: event.clientY,
        center0: state.center,
        width0: state.width,
        width: state.width
      };
    });
    canvas.addEventListener("pointerup", () => {
      state.drag = null;
    });
    canvas.addEventListener("pointermove", onPointerMove);
    canvas.addEventListener("dblclick", () => {
      if (!state.image) return;
      const win = autoWindow(state.image);
      state.center = win.center;
      state.width = win.width;
      paintResult();
    });
    document.querySelectorAll("button.tab").forEach((node) => {
      node.addEventListener("click", () => showTab(node.id.replace("tab-", "")));
    });
    $("save").addEventListener("click", () => {
      if (!state.image) return;
      const link = document.createElement("a");
      link.href = $("result").toDataURL("image/png");
      link.download = "virtualmri.png";
      link.click();
    });
    bindKSpace();
  }

  function fillTissueTable() {
    const body = $("tissues");
    PH.TISSUES.forEach((tissue) => {
      const row = document.createElement("tr");
      [tissue.name, tissue.t1, tissue.t2, tissue.pd].forEach((value) => {
        const cell = document.createElement("td");
        cell.textContent = String(value);
        row.appendChild(cell);
      });
      body.appendChild(row);
    });
  }

  bind();
  fillTissueTable();
  showSequenceFields();
  updateReadouts();
  loadPhantom("brain");
})();
