package com.omnetpp.omnetpp_plugin;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;

public class NedRenameTest extends BasePlatformTestCase {

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

    public void testRenameModule() {
        myFixture.configureByFile("RenameTestData.ned");
        myFixture.renameElementAtCaret("RenamedModule");
        myFixture.checkResultByFile("RenameTestData_after.ned");
    }
}