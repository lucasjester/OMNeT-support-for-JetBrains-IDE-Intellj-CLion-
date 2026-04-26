package com.omnetpp.omnetpp_plugin;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.ConfigurationFromContext;
import com.intellij.psi.PsiElement;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.omnetpp.omnetpp_plugin.ini.IniFileType;
import com.omnetpp.omnetpp_plugin.ini.runner.rc.OmnetIniRunConfiguration;
import com.omnetpp.omnetpp_plugin.ini.runner.rc.OmnetIniRunConfigurationProducer;

/**
 * FR-4-A2 — context-menu entry point.
 *
 * <p>Verifies that {@link OmnetIniRunConfigurationProducer} produces a
 * correctly pre-filled {@link OmnetIniRunConfiguration} when invoked from a
 * caret position inside a {@code [Config \ldots]} section, and that it
 * declines to produce a configuration for the {@code [General]} section.</p>
 */
public class OmnetIniRunConfigurationProducerTest extends BasePlatformTestCase {

    public void testProducerPreFillsConfigFromCaretInsideConfigSection() {
        myFixture.configureByText(IniFileType.INSTANCE,
                "[General]\n" +
                        "network = TestNet\n" +
                        "\n" +
                        "[Tic<caret>toc1]\n" +
                        "sim-time-limit = 10s\n");

        PsiElement elementAtCaret = myFixture.getFile()
                .findElementAt(myFixture.getCaretOffset());
        assertNotNull("Must find element at caret", elementAtCaret);

        ConfigurationContext context = new ConfigurationContext(elementAtCaret);
        OmnetIniRunConfigurationProducer producer = new OmnetIniRunConfigurationProducer();

        ConfigurationFromContext fromContext = producer.createConfigurationFromContext(context);
        assertNotNull("producer should accept a caret inside [Config ...]",
                fromContext);

        OmnetIniRunConfiguration cfg = (OmnetIniRunConfiguration)
                fromContext.getConfigurationSettings().getConfiguration();

        assertEquals("configName must be extracted from the section header",
                "Tictoc1", cfg.getConfigName());
        assertTrue("iniPath must point at the fixture INI file",
                cfg.getIniPath().endsWith(".ini"));
        assertTrue("isConfigurationFromContext should recognise its own output",
                producer.isConfigurationFromContext(cfg, context));
    }

    public void testProducerIgnoresGeneralSection() {
        myFixture.configureByText(IniFileType.INSTANCE,
                "[Gen<caret>eral]\n" +
                        "network = TestNet\n");

        PsiElement elementAtCaret = myFixture.getFile()
                .findElementAt(myFixture.getCaretOffset());
        assertNotNull(elementAtCaret);

        ConfigurationContext context = new ConfigurationContext(elementAtCaret);
        OmnetIniRunConfigurationProducer producer = new OmnetIniRunConfigurationProducer();

        ConfigurationFromContext fromContext = producer.createConfigurationFromContext(context);
        assertNull("producer must not produce a configuration for [General]",
                fromContext);
    }

    public void testProducerIgnoresCaretOutsideSectionHeader() {
        myFixture.configureByText(IniFileType.INSTANCE,
                "[Config Tictoc1]\n" +
                        "sim-time<caret>-limit = 10s\n");

        PsiElement elementAtCaret = myFixture.getFile()
                .findElementAt(myFixture.getCaretOffset());
        assertNotNull(elementAtCaret);

        ConfigurationContext context = new ConfigurationContext(elementAtCaret);
        OmnetIniRunConfigurationProducer producer = new OmnetIniRunConfigurationProducer();

        ConfigurationFromContext fromContext = producer.createConfigurationFromContext(context);
        assertNull("producer must decline when caret is not on a section header",
                fromContext);
    }
}
