# Deployment Guide

This guide explains how the Virtual MRI project is built and deployed to GitHub Pages using GitHub Actions.

## Overview

Both apps are deployed as static Flutter Web builds to GitHub Pages:

| App | URL | Base Href |
|-----|-----|-----------|
| Virtual MRI Scanner | https://hacklaen.github.io/virtualmri/ | `/virtualmri/` |
| Parameter Image Calculator | https://hacklaen.github.io/virtualmri/pic/ | `/virtualmri/pic/` |

Deployment is fully automatic: every push to the `flutter` branch triggers a GitHub Actions workflow that builds both apps and publishes them.

## How It Works

### Trigger

The workflow is triggered by any push to the `flutter` branch:

```yaml
on:
  push:
    branches: [flutter]
```

This means: commit and push to `flutter`, and the deployment starts automatically. No manual steps required.

### Build Process

Since both apps share a single `pubspec.yaml` but have different entry points, they are built sequentially with different `--target` and `--base-href` flags:

**Step 1 — Build vmri:**
```bash
flutter build web --release --base-href "/virtualmri/" --target lib/vmri/main.dart
```

The output lands in `build/web/`. This directory is temporarily renamed to `build/vmri_web/` to preserve it.

**Step 2 — Build paracalc:**
```bash
flutter build web --release --base-href "/virtualmri/pic/" --target lib/paracalc/main.dart
```

This overwrites `build/web/` with the paracalc build.

**Step 3 — Combine:**
```bash
mkdir -p build/vmri_web/pic
cp -r build/web/* build/vmri_web/pic/
mv build/vmri_web build/web
```

The paracalc build is copied into the `pic/` subdirectory of the vmri build. The final `build/web/` directory contains both apps in a single tree:

```
build/web/
├── index.html          ← vmri entry point
├── main.dart.js        ← vmri compiled code
├── flutter.js
├── assets/
│   └── web_assets/     ← brain dataset JSON
├── pic/
│   ├── index.html      ← paracalc entry point
│   ├── main.dart.js    ← paracalc compiled code
│   └── ...
└── ...
```

### Deployment

The combined `build/web/` directory is uploaded as a Pages artifact and deployed:

1. **`actions/upload-pages-artifact@v3`** — Packages `build/web/` into a deployable artifact
2. **`actions/deploy-pages@v4`** — Publishes the artifact to GitHub Pages

The deployment uses the modern GitHub Pages approach with dedicated permissions:

```yaml
permissions:
  contents: read       # Read repo contents for checkout
  pages: write         # Write to GitHub Pages
  id-token: write      # OIDC token for Pages authentication
```

### Concurrency Control

```yaml
concurrency:
  group: pages
  cancel-in-progress: true
```

If a new push arrives while a deployment is still running, the in-progress deployment is cancelled and replaced by the new one. This prevents outdated builds from being deployed.

## The `--base-href` Flag

Flutter Web apps use the HTML `<base href="...">` tag to resolve relative URLs. Since both apps are served under subpaths (not at the domain root), the `--base-href` flag is critical:

- **vmri**: `--base-href "/virtualmri/"` → assets load from `hacklaen.github.io/virtualmri/...`
- **paracalc**: `--base-href "/virtualmri/pic/"` → assets load from `hacklaen.github.io/virtualmri/pic/...`

Without the correct base href, the apps would fail to load their JavaScript, fonts, and other assets.

## Workflow File

The complete workflow is defined in `.github/workflows/deploy.yml`. It consists of two jobs:

| Job | Purpose | Runs on |
|-----|---------|---------|
| `build` | Checkout, install Flutter, build both apps, upload artifact | `ubuntu-latest` |
| `deploy` | Download artifact, publish to GitHub Pages | `ubuntu-latest` |

The `deploy` job depends on `build` (`needs: build`), so it only runs after a successful build.

## Local Build

To test the build locally without deploying, use the deploy script:

```bash
./scripts/deploy.sh
```

This runs the same build steps as the GitHub Actions workflow. The output is in `build/web/` and can be served locally:

```bash
cd build/web
python3 -m http.server 8080
# Open http://localhost:8080 for vmri
# Open http://localhost:8080/pic/ for paracalc
```

## GitHub Pages Setup

For the deployment to work, the repository must be configured:

1. Go to **Settings → Pages** in the GitHub repository
2. Under **Source**, select **GitHub Actions**
3. The workflow handles everything else automatically

## Troubleshooting

| Problem | Cause | Solution |
|---------|-------|----------|
| Apps show blank page | Wrong `--base-href` | Check that base href matches the actual URL path |
| Deployment fails with permission error | Pages not configured | Enable GitHub Pages with "GitHub Actions" source |
| Old version still visible | Browser cache | Hard refresh (Cmd+Shift+R) or clear cache |
| Build fails | Flutter version mismatch | The workflow uses `channel: stable` — check SDK constraints in `pubspec.yaml` |
