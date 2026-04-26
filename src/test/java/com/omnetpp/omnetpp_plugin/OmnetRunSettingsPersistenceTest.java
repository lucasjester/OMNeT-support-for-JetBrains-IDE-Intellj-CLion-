package com.omnetpp.omnetpp_plugin;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.intellij.util.xmlb.XmlSerializer;
import com.omnetpp.omnetpp_plugin.ini.runner.config.OmnetRunSettings;
import org.jdom.Element;

/**
 * FR-4-A6 — application-level settings persistence.
 *
 * <p>Verifies that every field of {@link OmnetRunSettings.State} round-trips
 * through IntelliJ's {@link XmlSerializer}, which is the mechanism the
 * {@link com.intellij.openapi.components.PersistentStateComponent} infrastructure
 * uses when the IDE saves and reloads application-level services across sessions.</p>
 *
 * <p>Additionally verifies that {@link OmnetRunSettings#loadState} populates the
 * state so that getter methods return the loaded values.</p>
 */
public class OmnetRunSettingsPersistenceTest extends BasePlatformTestCase {

    public void testStateSurvivesXmlSerializationRoundTrip() {
        OmnetRunSettings.State original = new OmnetRunSettings.State();
        original.oppRunPath = "/usr/local/bin/opp_run";
        original.simLibPath = "/opt/mySim/build/libSim.so";
        original.nedPaths   = "/opt/inet/src;/home/user/ned";
        original.libraries  = "/opt/inet/src/INET";

        Element serialized = XmlSerializer.serialize(original);
        OmnetRunSettings.State restored =
                XmlSerializer.deserialize(serialized, OmnetRunSettings.State.class);

        assertEquals("/usr/local/bin/opp_run", restored.oppRunPath);
        assertEquals("/opt/mySim/build/libSim.so", restored.simLibPath);
        assertEquals("/opt/inet/src;/home/user/ned", restored.nedPaths);
        assertEquals("/opt/inet/src/INET", restored.libraries);
    }

    public void testLoadStatePopulatesGetters() {
        OmnetRunSettings settings = OmnetRunSettings.getInstance();
        OmnetRunSettings.State snapshot = settings.getState();

        try {
            OmnetRunSettings.State fresh = new OmnetRunSettings.State();
            fresh.oppRunPath = "/tmp/fake/opp_run";
            fresh.simLibPath = "/tmp/fake/lib";
            fresh.nedPaths   = "/tmp/fake/ned1;/tmp/fake/ned2";
            fresh.libraries  = "/tmp/fake/lib1;/tmp/fake/lib2";

            settings.loadState(fresh);

            assertEquals("/tmp/fake/opp_run", settings.getOppRunPath());
            assertEquals("/tmp/fake/lib", settings.getSimLibPath());
            assertEquals("/tmp/fake/ned1;/tmp/fake/ned2", settings.getNedPaths());
            assertEquals("/tmp/fake/lib1;/tmp/fake/lib2", settings.getLibraries());
        } finally {
            if (snapshot != null) settings.loadState(snapshot);
        }
    }
}
