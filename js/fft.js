/* Virtual MRI — 2D FFT
 * Derivative of virtualmri (Thomas Hacklaender, Christian Schalla, Andreas Truemper), GPL-2.
 */
(function (root) {
  function fft1d(re, im, inverse) {
    const n = re.length;
    for (let i = 1, j = 0; i < n; i++) {
      let bit = n >> 1;
      for (; j & bit; bit >>= 1) j ^= bit;
      j ^= bit;
      if (i < j) {
        const tr = re[i];
        re[i] = re[j];
        re[j] = tr;
        const ti = im[i];
        im[i] = im[j];
        im[j] = ti;
      }
    }

    const sign = inverse ? 1 : -1;
    for (let len = 2; len <= n; len <<= 1) {
      const ang = (sign * 2 * Math.PI) / len;
      const wlenRe = Math.cos(ang);
      const wlenIm = Math.sin(ang);
      const half = len >> 1;
      for (let i = 0; i < n; i += len) {
        let wRe = 1;
        let wIm = 0;
        for (let j = 0; j < half; j++) {
          const a = i + j;
          const b = a + half;
          const vRe = re[b] * wRe - im[b] * wIm;
          const vIm = re[b] * wIm + im[b] * wRe;
          const uRe = re[a];
          const uIm = im[a];
          re[a] = uRe + vRe;
          im[a] = uIm + vIm;
          re[b] = uRe - vRe;
          im[b] = uIm - vIm;
          const nwRe = wRe * wlenRe - wIm * wlenIm;
          wIm = wRe * wlenIm + wIm * wlenRe;
          wRe = nwRe;
        }
      }
    }

    if (inverse) {
      for (let i = 0; i < n; i++) {
        re[i] /= n;
        im[i] /= n;
      }
    }
  }

  function fft2(reIn, imIn, n, inverse) {
    const re = new Float64Array(reIn);
    const im = new Float64Array(imIn);
    for (let y = 0; y < n; y++) {
      fft1d(re.subarray(y * n, y * n + n), im.subarray(y * n, y * n + n), inverse);
    }
    const colRe = new Float64Array(n);
    const colIm = new Float64Array(n);
    for (let x = 0; x < n; x++) {
      for (let y = 0; y < n; y++) {
        colRe[y] = re[y * n + x];
        colIm[y] = im[y * n + x];
      }
      fft1d(colRe, colIm, inverse);
      for (let y = 0; y < n; y++) {
        re[y * n + x] = colRe[y];
        im[y * n + x] = colIm[y];
      }
    }
    return { re: re, im: im };
  }

  function forwardReal(image, n) {
    const im = new Float64Array(n * n);
    return fft2(image, im, n, false);
  }

  function inverse(re, im, n) {
    return fft2(re, im, n, true);
  }

  function magnitude(re, im) {
    const out = new Float64Array(re.length);
    for (let i = 0; i < re.length; i++) out[i] = Math.hypot(re[i], im[i]);
    return out;
  }

  root.VMRI_FFT = {
    forwardReal: forwardReal,
    inverse: inverse,
    magnitude: magnitude
  };
})(globalThis);
