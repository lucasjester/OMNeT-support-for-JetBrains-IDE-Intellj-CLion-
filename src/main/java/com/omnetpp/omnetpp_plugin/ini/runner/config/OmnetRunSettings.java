package com.omnetpp.omnetpp_plugin.ini.runner.config;

import com.intellij.openapi.components.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "OmnetRunSettings",
        storages = @Storage("omnetpp_plugin_run.xml")
)
@Service(Service.Level.APP)
public final class OmnetRunSettings implements PersistentStateComponent<OmnetRunSettings.State> {

    public static final class State {
        public String oppRunPath  = "opp_run";
        public String commonArgs  = "";
        public String simLibPath  = "";   // global compiled binary/library (-l)
        /**
         * Semicolon-separated list of extra NED source paths added to EVERY run.
         * Example:  /opt/omnetpp/samples/inet4.5/src;/opt/omnetpp/src
         */
        public String nedPaths    = "";
        /**
         * Semicolon-separated list of shared libraries loaded with -l for EVERY run.
         * Example:  /opt/omnetpp/samples/inet4.5/src/INET
         */
        public String libraries   = "";
    }

    private State state = new State();

    public static @NotNull OmnetRunSettings getInstance() {
        return com.intellij.openapi.application.ApplicationManager
                .getApplication().getService(OmnetRunSettings.class);
    }

    @Override public @Nullable State getState()              { return state; }
    @Override public void loadState(@NotNull State state)    { this.state = state; }

    // ── opp_run path ────────────────────────────────────────────────────────
    public @NotNull String getOppRunPath() {
        return state.oppRunPath == null || state.oppRunPath.isBlank() ? "opp_run" : state.oppRunPath.trim();
    }
    public void setOppRunPath(@NotNull String v) { state.oppRunPath = v; }

    // ── common extra args ────────────────────────────────────────────────────
    public @NotNull String getCommonArgs() {
        return state.commonArgs == null ? "" : state.commonArgs;
    }
    public void setCommonArgs(@NotNull String v) { state.commonArgs = v; }

    // ── global sim binary / -l library ──────────────────────────────────────
    public @NotNull String getSimLibPath() {
        return state.simLibPath == null ? "" : state.simLibPath.trim();
    }
    public void setSimLibPath(@NotNull String v) { state.simLibPath = v; }

    // ── global NED paths (semicolon-separated) ───────────────────────────────
    public @NotNull String getNedPaths() {
        return state.nedPaths == null ? "" : state.nedPaths;
    }
    public void setNedPaths(@NotNull String v) { state.nedPaths = v; }

    // ── global -l libraries (semicolon-separated) ────────────────────────────
    public @NotNull String getLibraries() {
        return state.libraries == null ? "" : state.libraries;
    }
    public void setLibraries(@NotNull String v) { state.libraries = v; }
}