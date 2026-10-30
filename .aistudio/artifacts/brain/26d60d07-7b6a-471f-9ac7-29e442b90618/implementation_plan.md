# Implementation Plan: 100% Offline APK Extraction & Direct Sharing

Implement a completely offline, zero-network mechanism that extracts the app's own installed APK (`context.applicationInfo.sourceDir`), saves it as `ChatBoxPro.apk` in the user's public Downloads directory, and immediately presents a direct Install & Share popup.

---

## 1. Architecture & Technical Strategy

### Offline Source File Extraction
- Access the app's installed APK directly via `File(context.applicationInfo.sourceDir)`. Every Android application has read permission to its own APK binary without requiring external network connectivity.
- Copy the file offline into the public `Downloads` directory as `ChatBoxPro.apk`:
  - **Modern Storage (Android 10+ / API 29+)**: Leverage `MediaStore.Downloads` or `Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)` via content resolver to safely write without intrusive storage permissions.
  - **Legacy Storage (Android 7–9 / API 24–28)**: Handle `WRITE_EXTERNAL_STORAGE` permission check if writing directly to shared storage.
- Streaming copy with live progress indicators (0% to 100%) computed locally.

### Security & Package Installation Flow
- Check `canRequestPackageInstalls()` (`Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES`) on Android 8.0+ before installation.
- Provide a clear, friendly prompt explaining how to allow package installation.
- Generate secure content URI using `FileProvider.getUriForFile(context, "${context.packageName}.provider", apkFile)`.
- Launch standard `Intent.ACTION_VIEW` with MIME type `application/vnd.android.package-archive` and `FLAG_GRANT_READ_URI_PERMISSION`.

### Direct Share Intent
- Implement direct `Intent.ACTION_SEND` targeting `application/vnd.android.package-archive` with the `FileProvider` URI and `Intent.EXTRA_STREAM`.
- This enables instant P2P offline sharing over Bluetooth, Nearby Share / Quick Share, WhatsApp, and messaging apps.

---

## 2. Proposed Implementation Steps

### Step 1: Create `OfflineApkExtractor`
- File: `app/src/main/java/com/example/data/offline/OfflineApkExtractor.kt`
- Responsibilities:
  - Locate `context.applicationInfo.sourceDir`.
  - Copy stream to `Environment.DIRECTORY_DOWNLOADS/ChatBoxPro.apk` (and `MediaStore.Downloads`).
  - Provide extraction progress callback (`0.0f` to `1.0f`).
  - Generate share intent and install intent safely with `FileProvider`.
  - Handle `ACTION_MANAGE_UNKNOWN_APP_SOURCES` permission check.

### Step 2: Create `OfflineApkShareDialog`
- File: `app/src/main/java/com/example/ui/screens/settings/OfflineApkShareDialog.kt`
- UI Design:
  - Chat Box Pro branded header and emerald accents.
  - Live local progress bar during offline extraction.
  - Extracted confirmation showing `Downloads/ChatBoxPro.apk` and file size.
  - Action buttons:
    - **"Install App Now"**: Checks unknown sources permission and triggers native installer.
    - **"Share via WhatsApp / Bluetooth"**: Launches native Android Share Sheet.
    - **"Open Downloads Folder"**: Opens file manager.

### Step 3: Wire into Settings Screen
- File: `app/src/main/java/com/example/ui/screens/settings/SettingsScreen.kt`
- Replace or add a unified Settings item:
  - **Title**: "Share App & Export APK"
  - **Subtitle**: "Extract ChatBoxPro.apk offline to Downloads • Share via Bluetooth or WhatsApp"
  - **Icon**: `Icons.Default.Share`
  - Clicking opens `OfflineApkShareDialog`.

### Step 4: Verification & Automated Tests
- Create unit test `OfflineApkExtractorTest.kt` verifying source APK detection, file naming (`ChatBoxPro.apk`), and MIME intent configurations.
- Verify compilation with `compile_applet` and run JVM unit tests.
