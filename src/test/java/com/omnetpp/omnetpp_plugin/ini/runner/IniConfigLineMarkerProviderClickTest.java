package com.omnetpp.omnetpp_plugin.ini.runner;

import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.omnetpp.omnetpp_plugin.ini.runner.rc.OmnetIniRunConfiguration;

/**
 * FR-4-A3 — gutter entry point (create-or-reuse behaviour).
 *
 * <p>Verifies that clicking the gutter icon next to a {@code [Config \ldots]}
 * header creates a new run configuration on the first click and reuses the
 * existing one on subsequent clicks, rather than creating a duplicate each
 * time.</p>
 *
 * <p>The click handler itself is a Swing lambda that first shows a
 * Run/Debug popup and then invokes
 * {@link IniConfigLineMarkerProvider#findOrCreateSettings} with the selected
 * executor. This test drives {@code findOrCreateSettings} directly, which
 * is the part of the handler the plugin owns; the popup is platform UI and
 * is verified manually.</p>
 */
public class IniConfigLineMarkerProviderClickTest extends BasePlatformTestCase {

    public void testFirstClickCreatesRunConfiguration() {
        IniConfigLineMarkerProvider provider = new IniConfigLineMarkerProvider();

        RunnerAndConfigurationSettings settings = provider.findOrCreateSettings(
                getProject(),
                "/tmp/fixture/tictoc.ini",
                "Tictoc1",
                "tictoc.ini");

        assertNotNull("first call must create a RunnerAndConfigurationSettings", settings);
        RunConfiguration rc = settings.getConfiguration();
        OmnetIniRunConfiguration cfg = assertInstanceOf(rc, OmnetIniRunConfiguration.class);
        assertEquals("/tmp/fixture/tictoc.ini", cfg.getIniPath());
        assertEquals("Tictoc1", cfg.getConfigName());

        assertEquals("new configuration must be registered in RunManager",
                1, countMatching("/tmp/fixture/tictoc.ini", "Tictoc1"));
    }

    public void testSecondClickReusesExistingConfiguration() {
        IniConfigLineMarkerProvider provider = new IniConfigLineMarkerProvider();

        RunnerAndConfigurationSettings first = provider.findOrCreateSettings(
                getProject(),
                "/tmp/fixture/tictoc.ini",
                "Tictoc1",
                "tictoc.ini");

        RunnerAndConfigurationSettings second = provider.findOrCreateSettings(
                getProject(),
                "/tmp/fixture/tictoc.ini",
                "Tictoc1",
                "tictoc.ini");

        assertSame("second call with same parameters must reuse the existing settings",
                first, second);
        assertEquals("no duplicate must be registered in RunManager",
                1, countMatching("/tmp/fixture/tictoc.ini", "Tictoc1"));
    }

    public void testDifferentConfigNamesCreateSeparateConfigurations() {
        IniConfigLineMarkerProvider provider = new IniConfigLineMarkerProvider();

        RunnerAndConfigurationSettings a = provider.findOrCreateSettings(
                getProject(),
                "/tmp/fixture/tictoc.ini",
                "Tictoc1",
                "tictoc.ini");

        RunnerAndConfigurationSettings b = provider.findOrCreateSettings(
                getProject(),
                "/tmp/fixture/tictoc.ini",
                "Tictoc2",
                "tictoc.ini");

        assertNotSame("different config names must produce different settings objects", a, b);
        assertEquals(1, countMatching("/tmp/fixture/tictoc.ini", "Tictoc1"));
        assertEquals(1, countMatching("/tmp/fixture/tictoc.ini", "Tictoc2"));
    }

    private int countMatching(String iniPath, String configName) {
        int count = 0;
        for (RunnerAndConfigurationSettings s :
                RunManager.getInstance(getProject()).getAllSettings()) {
            if (s.getConfiguration() instanceof OmnetIniRunConfiguration omnet
                    && iniPath.equals(omnet.getIniPath())
                    && configName.equals(omnet.getConfigName())) {
                count++;
            }
        }
        return count;
    }
}
