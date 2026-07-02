# Baseline audit - Mina dos Sons v1.0.0

Date: 2026-07-02
Branch created for work: `codex/senior-hardening-v1.1.0`
Repository: `brunogp32/Mina-dos-Sons`

## GitHub

- Default branch: `main`
- Latest release: `v1.0.0` (`Mina dos Sons 1.0.0`)
- Latest release URL: https://github.com/brunogp32/Mina-dos-Sons/releases/tag/v1.0.0
- Latest observed failing run: `28609596019`

## Current CI failure

- Workflow: `Android`
- Failing step: `Unit tests`
- Command: `./gradlew testDebugUnitTest`
- Exact failure: `/home/runner/work/_temp/...sh: line 1: ./gradlew: Permission denied`
- Exit code: `126`
- Root cause: `gradlew` is committed with mode `100644`, so Linux runners cannot execute it.
- Wrapper state: custom shell/batch scripts, not the official Gradle Wrapper.
- Node warning cause: workflow uses `actions/checkout@v4`, `actions/setup-java@v4`, `gradle/actions/setup-gradle@v4`, and `actions/upload-artifact@v4`, which GitHub reports as Node.js 20-based actions.
- Report upload issue: the workflow uploads only after failure and searches paths before Gradle creates reports, producing `No files were found`.

## Local toolchain

- Java: Temurin OpenJDK `17.0.19+10`
- Gradle: `8.10.2`
- Android Gradle Plugin: `8.7.3`
- Kotlin: `2.0.21`
- Compose plugin: `2.0.21`
- Compile SDK: `35`
- Target SDK: `35`
- Min SDK: `26`

## Local baseline commands

```powershell
$env:JAVA_HOME='C:\Users\bruno\.gradle\codex-jdks\jdk-17'
.\gradlew.bat clean testDebugUnitTest lintDebug assembleDebug copyDebugApkToDist
python tools\audit_pt_pt.py
```

## Local baseline results

- Gradle build: `BUILD SUCCESSFUL in 1m 36s`
- Unit tests: `:app:testDebugUnitTest` passed
- Lint: `:app:lintDebug` passed
- Debug APK: `app/build/outputs/apk/debug/Mina dos Sons.apk`
- Debug APK size: `11,246,283` bytes
- Dist APK: `dist/Mina dos Sons-v1.0.0.apk`
- PT-PT audit: exit code `0`

Raw logs:

- `build/reports/baseline/local-baseline-build.log`
- `build/reports/baseline/pt-pt-audit.log`

## Baseline risks found before changes

- `android:allowBackup="true"` while the app stores child voice recordings.
- New recordings are stored in `filesDir/recordings`, not `noBackupFilesDir`.
- TTS silently falls back to the device default language when `pt-PT` is missing.
- `AppViewModel` owns repositories, TTS, players, recorder, and game/session state directly.
- Audio stopping is spread across UI and ViewModel instead of a single lifecycle-aware coordinator.
- Collection/shop work with a large in-memory catalog and should use lazy/grid state more deliberately.
- Release `v1.0.0` published a debug APK; there is no production signing configuration in GitHub secrets.
