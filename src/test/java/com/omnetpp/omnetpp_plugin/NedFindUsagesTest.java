package com.omnetpp.omnetpp_plugin;

import com.intellij.usageView.UsageInfo;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import java.util.Collection;

public class NedFindUsagesTest extends BasePlatformTestCase {

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

    public void testFindUsages() {
        myFixture.configureByFile("FindUsagesTestData.ned");
        Collection<UsageInfo> usages = myFixture.findUsages(
                myFixture.getElementAtCaret()
        );
        assertEquals(1, usages.size());
    }
}