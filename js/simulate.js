/* Virtual MRI — pulse-sequence simulation
 * Derivative of virtualmri (Thomas Hacklaender, Christian Schalla, Andreas Truemper), GPL-2.
 *
 * Signal equations follow the Java sequence classes. Three corrections are applied
 * because the original expressions do not match the comments or the named variables:
 *   - cosine of the flip angle (the Java code stored sin in the variable cosangel)
 *   - inversion recovery uses exp(-TR/T1), not exp(-TR/TI)
 *   - T2* adds gamma*deltaB in 1/ms (the Java product was in Hz and did not affect T2)
 * Refocused gradient echo uses PD * sin(a) / (1 + T1/T2* - (T1/T2* - 1)*cos(a)) * exp(-TE/T2*),
 * which is the written formula with the missing multiplication by cos(a) restored.
 * k-space is a real 2D FFT. The Java tool called the 1D FFT of the jigl library.
 */
(function (root) {
  const FFT = root.VMRI_FFT;
  const N = 256;
  const GYRO = 42.6;
  const GRAD = 10;
  const SNR_FACTOR = 3.44;
  const DELTAB = 19.5e-9;
  const T2_STAR_RATE = GYRO * DELTAB * 1e-3;

  function t2star(t2) {
    return 1 / (1 / t2 + T2_STAR_RATE);
  }

  function phaseEncodes(p) {
    let ny = N;
    let nex = p.nex;
    if (p.phaseOS) {
      ny *= 2;
      if (nex > 1) nex /= 2;
    }
    ny = Math.max(1, Math.round(ny * (p.rect / 8)));
    return { ny: ny, nex: nex };
  }

  function snr(p) {
    const pe = phaseEncodes(p);
    const pix = p.fov / N;
    const vol = pix * pix * p.thickness;
    let fov = p.fov;
    if (p.freqOS) fov *= 2;
    const bw = GYRO * GRAD * fov;
    return SNR_FACTOR * vol * Math.sqrt((pe.ny * pe.nex) / bw);
  }

  function scanTimeMs(p) {
    const pe = phaseEncodes(p);
    const shots = p.sequence === "tse" ? Math.ceil(pe.ny / Math.max(1, p.etl)) : pe.ny;
    return p.tr * shots * pe.nex;
  }

  function formatDuration(ms) {
    const total = Math.max(0, Math.round(ms / 1000));
    const m = Math.floor(total / 60);
    const s = total % 60;
    return m + " min " + s + " s";
  }

  function weighting(p) {
    if (p.sequence === "ir") {
      if (p.ti >= 1400) return "FLAIR-ähnlich, Liquor wird dunkel wenn TI nahe T1·ln 2 liegt";
      if (p.ti <= 250) return "STIR-ähnlich, Fett wird dunkel wenn TI nahe 0,69·T1 liegt";
      return "T1-Kontrast nach Inversion";
    }
    if (p.sequence === "sr") return "Sättigungserholung, vor allem T1-Kontrast";
    if (p.sequence === "spoiled" || p.sequence === "gre" || p.sequence === "refocused") {
      return "Gradienten-Echo, T2*-Einfluss über die Echozeit";
    }
    const trLong = p.tr >= 1500;
    const teLong = p.te >= 40;
    if (trLong && teLong) return "T2-Gewichtung";
    if (trLong && !teLong) return "Protonendichte-Gewichtung";
    if (!trLong && !teLong) return "T1-Gewichtung";
    return "Gemischter Kontrast";
  }

  function formula(sequence) {
    switch (sequence) {
      case "se":
        return "S = PD · exp(−TE/T2) · (1 − exp(−TR/T1))";
      case "tse":
        return "Wie Spin-Echo, je Echo mit TE = n·ESP. Das Echo nahe TEeff füllt die k-Raum-Mitte.";
      case "sr":
        return "S = PD · (1 − exp(−TR/T1))";
      case "ir":
        return "S = PD · |1 − 2·exp(−TI/T1) + exp(−TR/T1)|";
      case "spoiled":
        return "S = PD · exp(−TE/T2*) · sin α · (1−E1) / (1 − E1·cos α),  E1 = exp(−TR/T1)";
      case "gre":
        return "S = PD · exp(−TE/T2*) · sin α · (1−E1) / (1 − E1·cos α + E2·(cos α − E1))";
      case "refocused":
        return "S = PD · exp(−TE/T2*) · sin α / (1 + T1/T2* − (T1/T2* − 1)·cos α)";
      default:
        return "";
    }
  }

  function pixelSignal(sequence, pd, t1, t2, p, echo) {
    if (pd <= 0) return 0;
    const tr = p.tr;
    const te = p.te;
    const t1s = t1 > 0 ? t1 : 1;
    const t2s = t2 > 0 ? t2 : 1;
    if (sequence === "se") {
      return pd * Math.exp(-te / t2s) * (1 - Math.exp(-tr / t1s));
    }
    if (sequence === "tse") {
      const echoTe = (echo + 1) * p.esp;
      return pd * Math.exp(-echoTe / t2s) * (1 - Math.exp(-tr / t1s));
    }
    if (sequence === "sr") {
      return pd * (1 - Math.exp(-tr / t1s));
    }
    if (sequence === "ir") {
      return Math.abs(pd * (1 - 2 * Math.exp(-p.ti / t1s) + Math.exp(-tr / t1s)));
    }
    const t2x = t2star(t2s);
    const e1 = Math.exp(-tr / t1s);
    const rad = (p.fa * Math.PI) / 180;
    const sn = Math.sin(rad);
    const cs = Math.cos(rad);
    const decay = Math.exp(-te / t2x);
    if (sequence === "spoiled") {
      const denom = 1 - e1 * cs;
      if (Math.abs(denom) < 1e-8) return 0;
      return Math.abs((pd * decay * (1 - e1) * sn) / denom);
    }
    if (sequence === "gre") {
      const e2 = Math.exp(-tr / t2x);
      const denom = 1 - e1 * cs + e2 * (cs - e1);
      if (Math.abs(denom) < 1e-8) return 0;
      return Math.abs((pd * decay * (1 - e1) * sn) / denom);
    }
    const ratio = t1s / t2x;
    const denom = 1 + ratio - (ratio - 1) * cs;
    if (Math.abs(denom) < 1e-8) return 0;
    return Math.abs((pd * decay * sn) / denom);
  }

  function renderSequence(phantom, p, echo) {
    const n = phantom.n;
    const img = new Float64Array(n * n);
    const t1 = phantom.t1;
    const t2 = phantom.t2;
    const pd = phantom.pd;
    const sequence = p.sequence;
    for (let i = 0; i < img.length; i++) {
      img[i] = pixelSignal(sequence, pd[i], t1[i], t2[i], p, echo);
    }
    return img;
  }

  function despike(img, n) {
    const out = new Float64Array(img);
    for (let y = 1; y < n - 1; y++) {
      for (let x = 1; x < n - 1; x++) {
        const i = y * n + x;
        const mean =
          (img[i - n - 1] +
            img[i - n] +
            img[i - n + 1] +
            img[i - 1] +
            img[i + 1] +
            img[i + n - 1] +
            img[i + n] +
            img[i + n + 1]) /
          8;
        if (Math.abs(mean - img[i]) > 2 * img[i]) out[i] = mean;
      }
    }
    return out;
  }

  function tseLines(n, etl, esp, te) {
    let best = 0;
    let diff = Infinity;
    for (let i = 0; i < etl; i++) {
      const gap = Math.abs(esp * (i + 1) - te);
      if (gap < diff) {
        diff = gap;
        best = i;
      }
    }
    const ranked = [best];
    for (let d = 1; d < etl; d++) {
      if (best + d < etl) ranked.push(best + d);
      if (best - d >= 0) ranked.push(best - d);
    }
    const lines = new Int16Array(n);
    lines.fill(best);
    const width = Math.floor(n / etl);
    let extra = n - width * etl;
    let up = (n >> 1) - 1;
    let down = n >> 1;
    for (let k = 0; k < ranked.length; k++) {
      let count = width + (extra > 0 ? 1 : 0);
      if (extra > 0) extra--;
      const upCount = count >> 1;
      const downCount = count - upCount;
      for (let i = 0; i < upCount && up >= 0; i++) lines[up--] = ranked[k];
      for (let i = 0; i < downCount && down < n; i++) lines[down++] = ranked[k];
    }
    return { lines: lines, best: best, bestTe: (best + 1) * esp };
  }

  function centeredBin(row, n) {
    return (row + (n >> 1)) % n;
  }

  function reconstructTse(phantom, p) {
    const n = phantom.n;
    const plan = tseLines(n, p.etl, p.esp, p.te);
    const kre = new Float64Array(n * n);
    const kim = new Float64Array(n * n);
    for (let echo = 0; echo < p.etl; echo++) {
      const spatial = renderSequence(phantom, p, echo);
      const spec = FFT.forwardReal(spatial, n);
      for (let row = 0; row < n; row++) {
        if (plan.lines[row] !== echo) continue;
        const bin = centeredBin(row, n);
        const src = bin * n;
        kre.set(spec.re.subarray(src, src + n), src);
        kim.set(spec.im.subarray(src, src + n), src);
      }
    }
    const back = FFT.inverse(kre, kim, n);
    return { image: FFT.magnitude(back.re, back.im), bestTe: plan.bestTe, best: plan.best };
  }

  function translate(img, n, h, v) {
    const out = new Float64Array(n * n);
    const ih = Math.round(h);
    const iv = Math.round(v);
    for (let y = 0; y < n; y++) {
      const sy = y - iv;
      if (sy < 0 || sy >= n) continue;
      for (let x = 0; x < n; x++) {
        const sx = x - ih;
        if (sx < 0 || sx >= n) continue;
        out[y * n + x] = img[sy * n + sx];
      }
    }
    return out;
  }

  function displacement(p, tSec) {
    if (p.artefact === "motion") {
      const m = p.motion;
      let t = tSec;
      if (t >= m.stop) t = m.stop;
      if (t < m.start) return { x: 0, y: 0 };
      const minutes = (t - m.start) / 60;
      return { x: minutes * m.xSpeed, y: minutes * m.ySpeed };
    }
    const m = p.periodic;
    let t = tSec;
    if (t >= m.stop) t = m.stop;
    if (t < m.start || m.freq <= 0) return { x: 0, y: 0 };
    const period = 60 / m.freq;
    const phase = (t - m.start) % period;
    const half = period / 2;
    const frac = phase < half ? phase / half : 1 - (phase - half) / half;
    return { x: frac * m.xAmp, y: frac * m.yAmp };
  }

  function applyMotion(img, n, p) {
    const steps = 16;
    const block = n / steps;
    const kre = new Float64Array(n * n);
    const kim = new Float64Array(n * n);
    for (let s = 0; s < steps; s++) {
      const tSec = ((s * block * p.tr) / 1000);
      const shift = displacement(p, tSec);
      const moved = translate(img, n, shift.x, shift.y);
      const spec = FFT.forwardReal(moved, n);
      for (let row = s * block; row < (s + 1) * block; row++) {
        const bin = centeredBin(row, n);
        const src = bin * n;
        kre.set(spec.re.subarray(src, src + n), src);
        kim.set(spec.im.subarray(src, src + n), src);
      }
    }
    const back = FFT.inverse(kre, kim, n);
    return FFT.magnitude(back.re, back.im);
  }

  function gaussian() {
    let u = 0;
    let v = 0;
    while (u === 0) u = Math.random();
    while (v === 0) v = Math.random();
    return Math.sqrt(-2 * Math.log(u)) * Math.cos(2 * Math.PI * v);
  }

  function addNoise(img, n, p, snrValue) {
    let sum = 0;
    for (let i = 0; i < img.length; i++) sum += img[i];
    const mean = sum / img.length;
    const sigmaRel = 0.08 / Math.max(snrValue, 0.02);
    const coil = p.coil === "body" ? mean * 0.05 : 0;
    for (let i = 0; i < img.length; i++) {
      let value = img[i] + img[i] * sigmaRel * gaussian() + coil * gaussian();
      if (value < 0) value = 0;
      img[i] = value;
    }
  }

  function aliasAlong(img, n, fov, horizontal) {
    if (fov >= n) return;
    const margin = ((n - fov) / 2) | 0;
    const limit = n - margin;
    if (horizontal) {
      for (let y = 0; y < n; y++) {
        const row = y * n;
        for (let x = margin; x < limit; x++) {
          let extra = 0;
          const right = x + fov;
          const left = x - fov;
          if (right < n) extra += img[row + right];
          if (left >= 0) extra += img[row + left];
          img[row + x] += extra;
        }
      }
      return;
    }
    for (let x = 0; x < n; x++) {
      for (let y = margin; y < limit; y++) {
        let extra = 0;
        const below = y + fov;
        const above = y - fov;
        if (below < n) extra += img[below * n + x];
        if (above >= 0) extra += img[above * n + x];
        img[y * n + x] += extra;
      }
    }
  }

  function sample(img, n, x, y) {
    if (x < 0 || y < 0 || x >= n || y >= n) return 0;
    return img[y * n + x];
  }

  function bilinear(img, n, x, y) {
    const x0 = Math.floor(x);
    const y0 = Math.floor(y);
    const fx = x - x0;
    const fy = y - y0;
    const a = sample(img, n, x0, y0);
    const b = sample(img, n, x0 + 1, y0);
    const c = sample(img, n, x0, y0 + 1);
    const d = sample(img, n, x0 + 1, y0 + 1);
    const low = a * (1 - fx) + b * fx;
    const high = c * (1 - fx) + d * fx;
    return low * (1 - fy) + high * fy;
  }

  function scaleFov(img, n, fov) {
    if (fov >= n) return img;
    const out = new Float64Array(n * n);
    const origin = (n - fov) / 2;
    const scale = n / fov;
    for (let y = 0; y < n; y++) {
      const ys = y / scale + origin;
      for (let x = 0; x < n; x++) {
        out[y * n + x] = bilinear(img, n, x / scale + origin, ys);
      }
    }
    return out;
  }

  function rectFov(img, n, p) {
    if (p.rect >= 8) return;
    const ph = Math.max(1, ((p.fov * p.rect) / 8) | 0);
    if (!p.phaseOS) aliasAlong(img, n, ph, false);
    const margin = ((n - ph) / 2) | 0;
    for (let y = 0; y < n; y++) {
      if (y >= margin && y < ph + margin) continue;
      img.fill(0, y * n, y * n + n);
    }
  }

  function validate(p) {
    if (p.te >= p.tr && p.sequence !== "sr" && p.sequence !== "ir") {
      return "TE muss kleiner als TR sein.";
    }
    if (p.sequence === "ir" && p.ti >= p.tr) return "TI muss kleiner als TR sein.";
    if (p.sequence === "tse" && p.etl * p.esp >= p.tr) return "ETL · Echoabstand muss kleiner als TR sein.";
    if (p.fov < 32 || p.fov > N) return "FoV liegt zwischen 32 und 256 mm.";
    return "";
  }

  function simulate(phantom, p) {
    const problem = validate(p);
    if (problem) return { ok: false, message: problem };
    const n = phantom.n;
    let info = "";
    let img;
    if (p.sequence === "tse") {
      const tse = reconstructTse(phantom, p);
      img = tse.image;
      info = "k-Raum-Mitte aus Echo " + (tse.best + 1) + " bei " + tse.bestTe + " ms.";
    } else {
      img = renderSequence(phantom, p, 0);
    }
    for (let i = 0; i < img.length; i++) if (img[i] < 0) img[i] = 0;
    img = despike(img, n);
    if (p.artefact === "motion" || p.artefact === "periodic") {
      img = applyMotion(img, n, p);
    }
    const snrValue = snr(p);
    addNoise(img, n, p, snrValue);
    if (!p.freqOS) aliasAlong(img, n, p.fov, true);
    if (!p.phaseOS) aliasAlong(img, n, p.fov, false);
    img = scaleFov(img, n, p.fov);
    rectFov(img, n, p);
    return {
      ok: true,
      image: img,
      n: n,
      snr: snrValue,
      scanMs: scanTimeMs(p),
      weighting: weighting(p),
      info: info
    };
  }

  function kSpaceLog(image, n) {
    const spec = FFT.forwardReal(image, n);
    const mag = FFT.magnitude(spec.re, spec.im);
    const shifted = new Float64Array(n * n);
    const half = n >> 1;
    for (let y = 0; y < n; y++) {
      for (let x = 0; x < n; x++) {
        const sy = (y + half) % n;
        const sx = (x + half) % n;
        shifted[y * n + x] = Math.log1p(mag[sy * n + sx]);
      }
    }
    return shifted;
  }

  // Even-n fftshift. Matches FFTTools.shift(..., n/2, n/2): DC moves to the centre.
  function fftshiftComplex(re, im, n) {
    const half = n >> 1;
    const ore = new Float64Array(n * n);
    const oim = new Float64Array(n * n);
    for (let y = 0; y < n; y++) {
      const sy = (y + half) % n;
      for (let x = 0; x < n; x++) {
        const sx = (x + half) % n;
        const i = y * n + x;
        const j = sy * n + sx;
        ore[i] = re[j];
        oim[i] = im[j];
      }
    }
    return { re: ore, im: oim };
  }

  function centeredSpectrum(image, n) {
    const spec = FFT.forwardReal(image, n);
    return fftshiftComplex(spec.re, spec.im, n);
  }

  function logCentered(spec) {
    const out = new Float64Array(spec.re.length);
    for (let i = 0; i < out.length; i++) out[i] = Math.log1p(Math.hypot(spec.re[i], spec.im[i]));
    return out;
  }

  function zeroBin(re, im, n, x, y) {
    if (x < 0 || y < 0 || x >= n || y >= n) return;
    const i = y * n + x;
    re[i] = 0;
    im[i] = 0;
  }

  // Same order and bounds as KSpaceManipulator: margins, then lines, then the
  // centre rectangle. Left and top are inclusive (x <= left), as in the Java loops.
  // Coordinates are the centred display grid, DC at n/2.
  function editKSpace(spec, n, opt) {
    const re = new Float64Array(spec.re);
    const im = new Float64Array(spec.im);
    if (opt.margins) {
      const left = opt.left | 0;
      const right = opt.right | 0;
      const top = opt.top | 0;
      const bottom = opt.bottom | 0;
      if (left > 0) {
        for (let x = 0; x <= left && x < n; x++) {
          for (let y = 0; y < n; y++) zeroBin(re, im, n, x, y);
        }
      }
      if (right > 0) {
        for (let x = n - right; x < n; x++) {
          for (let y = 0; y < n; y++) zeroBin(re, im, n, x, y);
        }
      }
      if (top > 0) {
        for (let y = 0; y <= top && y < n; y++) {
          for (let x = 0; x < n; x++) zeroBin(re, im, n, x, y);
        }
      }
      if (bottom > 0) {
        for (let y = n - bottom; y < n; y++) {
          for (let x = 0; x < n; x++) zeroBin(re, im, n, x, y);
        }
      }
    }
    if (opt.lines) {
      const rows = opt.rows | 0;
      const columns = opt.columns | 0;
      if (rows > 0) {
        for (let y = 0; y < n; y++) {
          if (y % rows === 0) {
            for (let x = 0; x < n; x++) zeroBin(re, im, n, x, y);
          }
        }
      }
      if (columns > 0) {
        for (let x = 0; x < n; x++) {
          if (x % columns === 0) {
            for (let y = 0; y < n; y++) zeroBin(re, im, n, x, y);
          }
        }
      }
    }
    if (opt.rect) {
      const width = opt.width | 0;
      const height = opt.height | 0;
      if (width > 0 && height > 0) {
        const c = n >> 1;
        const x0 = c - (width >> 1);
        const x1 = c + (width >> 1);
        const y0 = c - (height >> 1);
        const y1 = c + (height >> 1);
        for (let x = x0; x < x1; x++) {
          for (let y = y0; y < y1; y++) zeroBin(re, im, n, x, y);
        }
      }
    }
    return { re: re, im: im };
  }

  // Unshift, inverse FFT, magnitude. Same result as FFTTools.getReshiftedIFFT.
  function reconstructCentered(spec, n) {
    const raw = fftshiftComplex(spec.re, spec.im, n);
    const back = FFT.inverse(raw.re, raw.im, n);
    return FFT.magnitude(back.re, back.im);
  }

  root.VMRI = {
    N: N,
    snr: snr,
    scanTimeMs: scanTimeMs,
    formatDuration: formatDuration,
    weighting: weighting,
    formula: formula,
    simulate: simulate,
    kSpaceLog: kSpaceLog,
    centeredSpectrum: centeredSpectrum,
    logCentered: logCentered,
    editKSpace: editKSpace,
    reconstructCentered: reconstructCentered
  };
})(globalThis);
