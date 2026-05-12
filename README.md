# OMNeT++ Language Support Plugin

JetBrains IDE plugin that adds NED and INI language support and OMNeT++ run configurations.

## Requirements

- JDK 21
- OMNeT++ 6.x (for using the run configuration)

The Gradle wrapper (`./gradlew`) downloads everything else.

## Install the plugin (end users)

1. Download the latest plugin ZIP from this repository's **Releases** page.
2. In IntelliJ IDEA or CLion, open `Settings (or Preferences) → Plugins → ⚙ (gear icon) → Install Plugin from Disk...`, select the ZIP and restart the IDE.

After restart the plugin is active. Open any `.ned` or `.ini` file and you should see syntax highlighting, navigation, find usages, rename and run-configuration support.

## Build the plugin from source

If you want to rebuild the plugin yourself from this source tree:

```bash
./gradlew buildPlugin
```

The installable ZIP is written to `build/distributions/<plugin-name>-<version>.zip`. Install it via the same "Install Plugin from Disk" path described above.

## Develop the plugin (developer workflow)

```bash
./gradlew runIde
```

Launches a sandboxed IDE with the plugin pre-installed. Used during development only. End users do not need this.

```bash
./gradlew test
```

Runs the test suite.

## Target IDE: IntelliJ IDEA or CLion

The target IDE is selected in `build.gradle.kts`:

```kotlin
intellijPlatform {
    create("IC", "2025.1.4.1")   // IntelliJ IDEA Community
    //create("CL", "2025.1")     // CLion
}
```

To build/run against CLion instead, comment out the `IC` line and uncomment the `CL` line.

## macOS users: `opp_run` wrapper script

On macOS, IntelliJ does not inherit your shell environment when it spawns subprocesses, so the IDE-launched `opp_run` does not see `PATH`, library paths, `OMNETPP_IMAGE_PATH`, or the OMNeT++ Python venv. Pointing the run configuration directly at `omnetpp-6.3.0/bin/opp_run` will fail with missing-library errors.

**Workaround:** create a wrapper script (e.g. `~/omnetpp_run.sh`) that sources the OMNeT++ environment before exec'ing the real binary, and point the run configuration at the wrapper instead of `opp_run` directly.

```bash
#!/bin/bash
set -e
source /path/to/omnetpp-6.3.0/setenv
exec /path/to/omnetpp-6.3.0/bin/opp_run "$@"
```

Make it executable (`chmod +x omnetpp_run.sh`) and set it as the `opp_run` path in the plugin's run-configuration settings.

Linux users can point the run configuration straight at `opp_run` — the wrapper is only needed on macOS.
