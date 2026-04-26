package com.omnetpp.omnetpp_plugin;

import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.omnetpp.omnetpp_plugin.ini.IniFileType;
import com.omnetpp.omnetpp_plugin.ini.runner.IniConfigLineMarkerProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * FR-4-A1 — detection of runnable configurations.
 *
 * <p>Verifies that {@link IniConfigLineMarkerProvider} emits a gutter marker
 * for every {@code [Config \ldots]} section header in an INI file and does
 * not emit a marker for other section headers (in particular, the special
 * {@code [General]} header).</p>
 */
public class IniConfigLineMarkerProviderTest extends BasePlatformTestCase {

    public void testMarkersAppearAtConfigSectionsOnly() {
        myFixture.configureByText(IniFileType.INSTANCE,
                "[General]\n" +
                        "network = TestNet\n" +
                        "\n" +
                        "[Tictoc1]\n" +
                        "sim-time-limit = 10s\n" +
                        "\n" +
                        "[Tictoc2]\n" +
                        "sim-time-limit = 20s\n");

        List<LineMarkerInfo<?>> markers = collectMarkers();

        assertEquals("Expected exactly two markers (one per runnable section header)",
                2, markers.size());

        List<String> markerTexts = new ArrayList<>();
        for (LineMarkerInfo<?> m : markers) {
            PsiElement el = m.getElement();
            assertNotNull("marker must be attached to a PSI element", el);
            markerTexts.add(el.getText());
        }

        assertTrue("marker set must contain [Tictoc1]",
                markerTexts.contains("[Tictoc1]"));
        assertTrue("marker set must contain [Tictoc2]",
                markerTexts.contains("[Tictoc2]"));

        for (String text : markerTexts) {
            assertFalse("marker must not be emitted for [General]",
                    text.equals("[General]"));
        }
    }

    private List<LineMarkerInfo<?>> collectMarkers() {
        IniConfigLineMarkerProvider provider = new IniConfigLineMarkerProvider();
        List<LineMarkerInfo<?>> result = new ArrayList<>();

        myFixture.getFile().accept(new PsiRecursiveElementVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                super.visitElement(element);
                LineMarkerInfo<?> info = provider.getLineMarkerInfo(element);
                if (info != null) result.add(info);
            }
        });

        return result;
    }
}
