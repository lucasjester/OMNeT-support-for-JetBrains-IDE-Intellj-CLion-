package com.omnetpp.omnetpp_plugin;

import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.omnetpp.omnetpp_plugin.ini.runner.rc.OmnetIniRunConfiguration;
import com.omnetpp.omnetpp_plugin.ini.runner.rc.OmnetIniRunConfigurationType;
import org.jdom.Element;

/**
 * FR-4-A5 — per-configuration settings persistence.
 *
 * <p>Verifies that every field of {@link OmnetIniRunConfiguration} round-trips
 * through the {@code writeExternal}/{@code readExternal} contract the IntelliJ
 * Platform invokes when the IDE saves and reloads run configurations across
 * sessions.</p>
 */
public class OmnetIniRunConfigurationPersistenceTest extends BasePlatformTestCase {

    public void testXmlRoundTripPreservesAllFields() {
        OmnetIniRunConfiguration original = newBlankConfig();
        original.setIniPath("/home/user/simulations/tictoc.ini");
        original.setConfigName("Tictoc1");
        original.setSimLibPath("/opt/sim/myModel");
        original.setNedPaths("/opt/inet/src;/home/user/ned");
        original.setLibraries("libInet.so;libCustom.so");
        original.setShowGui(false);
        original.setExtraArgs("--record-eventlog=true --extra=\"quoted value\"");

        Element element = new Element("configuration");
        original.writeExternal(element);

        OmnetIniRunConfiguration loaded = newBlankConfig();
        loaded.readExternal(element);

        assertEquals("/home/user/simulations/tictoc.ini", loaded.getIniPath());
        assertEquals("Tictoc1", loaded.getConfigName());
        assertEquals("/opt/sim/myModel", loaded.getSimLibPath());
        assertEquals("/opt/inet/src;/home/user/ned", loaded.getNedPaths());
        assertEquals("libInet.so;libCustom.so", loaded.getLibraries());
        assertFalse("showGui should round-trip as false", loaded.isShowGui());
        assertEquals("--record-eventlog=true --extra=\"quoted value\"", loaded.getExtraArgs());
    }

    private OmnetIniRunConfiguration newBlankConfig() {
        OmnetIniRunConfigurationType type =
                ConfigurationTypeUtil.findConfigurationType(OmnetIniRunConfigurationType.class);
        assertNotNull("OmnetIniRunConfigurationType must be registered", type);
        return (OmnetIniRunConfiguration)
                type.getConfigurationFactories()[0].createTemplateConfiguration(getProject());
    }
}
