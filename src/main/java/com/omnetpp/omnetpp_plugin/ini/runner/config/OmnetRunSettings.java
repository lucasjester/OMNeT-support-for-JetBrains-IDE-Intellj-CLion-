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
        public String oppRunPath = "opp_run";
        public String commonArgs = "";
        public String simLibPath = "";  // global: path to compiled binary/library
    }

    private State state = new State();

    public static @NotNull OmnetRunSettings getInstance() {
        return com.intellij.openapi.application.ApplicationManager.getApplication().getService(OmnetRunSettings.class);
    }

    @Override
    public @Nullable State getState() { return state; }

    @Override
    public void loadState(@NotNull State state) { this.state = state; }

    public @NotNull String getOppRunPath() {
        return state.oppRunPath == null || state.oppRunPath.isBlank() ? "opp_run" : state.oppRunPath.trim();
    }

    public @NotNull String getCommonArgs() {
        return state.commonArgs == null ? "" : state.commonArgs;
    }

    public @NotNull String getSimLibPath() {
        return state.simLibPath == null ? "" : state.simLibPath.trim();
    }

    public void setOppRunPath(@NotNull String v) { state.oppRunPath = v; }
    public void setCommonArgs(@NotNull String v) { state.commonArgs = v; }
    public void setSimLibPath(@NotNull String v) { state.simLibPath = v; }
}