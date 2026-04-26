package com.omnetpp.omnetpp_plugin;

import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.omnetpp.omnetpp_plugin.ini.runner.rc.OmnetIniRunConfiguration;
import com.omnetpp.omnetpp_plugin.ini.runner.rc.OmnetIniRunConfigurationType;
import com.omnetpp.omnetpp_plugin.ini.runner.rc.OmnetIniRunSettingsEditor;

/**
 * FR-4-A4 — dialog entry point (underlying settings editor).
 *
 * <p>Verifies that {@link OmnetIniRunSettingsEditor} round-trips every field
 * between the {@link OmnetIniRunConfiguration} model and its Swing form.
 * {@code resetFrom(cfg)} populates the UI widgets from the configuration;
 * {@code applyTo(cfg)} writes the widget values back into a configuration.
 * Together they are the contract the IntelliJ Platform invokes when the
 * user opens, edits and accepts a run configuration in the Run/Debug
 * dialog.</p>
 *
 * <p>This test covers the editor's model-to-UI-to-model path. The actual
 * Swing form rendering and user interaction are outside the scope of
 * automated testing and are verified manually in \cref{sec:eval-fr4-manual}.</p>
 */
public class OmnetIniRunSettingsEditorTest extends BasePlatformTestCase {

    public void testEditorRoundTripPreservesAllFields() throws Exception {
        OmnetIniRunConfiguration original = newBlankConfig();
        original.setIniPath("/home/user/simulations/tictoc.ini");
        original.setConfigName("Tictoc1");
        original.setSimLibPath("/opt/sim/myModel");
        original.setNedPaths("/opt/inet/src;/home/user/ned");
        original.setLibraries("libInet.so;libCustom.so");
        original.setShowGui(false);
        original.setExtraArgs("--record-eventlog=true --extra=\"quoted value\"");

        OmnetIniRunSettingsEditor editor = new OmnetIniRunSettingsEditor();
        try {
            // Materialise the Swing form before invoking the reset/apply contract.
            editor.getComponent();

            editor.resetFrom(original);

            OmnetIniRunConfiguration fresh = newBlankConfig();
            editor.applyTo(fresh);

            assertEquals(original.getIniPath(),    fresh.getIniPath());
            assertEquals(original.getConfigName(), fresh.getConfigName());
            assertEquals(original.getSimLibPath(), fresh.getSimLibPath());
            assertEquals(original.getNedPaths(),   fresh.getNedPaths());
            assertEquals(original.getLibraries(),  fresh.getLibraries());
            assertEquals(original.isShowGui(),     fresh.isShowGui());
            assertEquals(original.getExtraArgs(),  fresh.getExtraArgs());
        } finally {
            com.intellij.openapi.util.Disposer.dispose(editor);
        }
    }

    public void testEditorRoundTripPreservesShowGuiTrue() throws Exception {
        OmnetIniRunConfiguration original = newBlankConfig();
        original.setShowGui(true);

        OmnetIniRunSettingsEditor editor = new OmnetIniRunSettingsEditor();
        try {
            editor.getComponent();
            editor.resetFrom(original);

            OmnetIniRunConfiguration fresh = newBlankConfig();
            editor.applyTo(fresh);

            assertTrue("showGui = true must round-trip through the editor",
                    fresh.isShowGui());
        } finally {
            com.intellij.openapi.util.Disposer.dispose(editor);
        }
    }

    private OmnetIniRunConfiguration newBlankConfig() {
        OmnetIniRunConfigurationType type =
                ConfigurationTypeUtil.findConfigurationType(OmnetIniRunConfigurationType.class);
        return (OmnetIniRunConfiguration)
                type.getConfigurationFactories()[0].createTemplateConfiguration(getProject());
    }
}
