# Firemaking Log Guard

A RuneLite plugin for OSRS. While your character is lighting a fire and stepping away from it, it outlines logs in your inventory in red and consumes inventory actions on those logs. This prevents an extra click from using or dropping another log during the attempt. Other inventory items remain clickable. Protection ends as your character reaches the next tile. If no step occurs, it ends after a short fallback delay.

The plugin recognizes items named `Logs` or ending in ` logs`. It does not cover bonfires or other firemaking animations.

## Build

Requires Java 11+. Run `./gradlew build` in this directory; the wrapper downloads Gradle automatically. To try it in a RuneLite development client, import the Gradle project and load `com.firemakinglogguard.FiremakingLogGuardPlugin` as an external plugin.

## Test locally

Copy this whole project to the machine that runs RuneLite. Install JDK 17 there, then open a terminal in the copied project and run:

Linux/macOS:

```bash
./gradlew run
```

Windows Command Prompt:

```bat
gradlew.bat run
```

The wrapper downloads Gradle automatically. The `run` task starts a separate development RuneLite client with this plugin loaded; copying the built JAR into the normal RuneLite installation is unnecessary. This follows the [official RuneLite example plugin](https://github.com/runelite/example-plugin).

Log in, open the plugin list, and enable **Firemaking Log Guard** if needed. Put a tinderbox and at least two logs in your inventory. Use the tinderbox on one log. During lighting and the step to the next tile, remaining logs should have red boxes; clicking or choosing **Drop** on one should do nothing. As the character reaches the next tile, the boxes disappear and the logs work normally.

If you use a Jagex Account and the development client cannot log in, follow [RuneLite's Jagex Account development guide](https://github.com/runelite/runelite/wiki/Using-Jagex-Accounts). Keep the credentials file described there private.
