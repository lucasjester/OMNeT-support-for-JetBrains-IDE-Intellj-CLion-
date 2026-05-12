package com.omnetpp.omnetpp_plugin;

import com.intellij.codeInsight.generation.actions.CommentByLineCommentAction;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.omnetpp.omnetpp_plugin.ini.IniFileType;

/**
 * Tests for the INI Commenter.
 *
 * INI has no block-comment syntax, so only the line-comment toggle is tested.
 * The line-comment prefix is "#", the dominant convention in OMNeT++ INI files.
 */
public class IniCommenterTest extends BasePlatformTestCase {

    /**
     * Test toggling a line comment on a key-value line with the caret at the
     * start of the line.
     */
    public void testLineComment() {
        myFixture.configureByText(IniFileType.INSTANCE,
                "<caret>network = MyTestNetwork");

        CommentByLineCommentAction commentAction = new CommentByLineCommentAction();
        commentAction.actionPerformedImpl(getProject(), myFixture.getEditor());
        myFixture.checkResult("#network = MyTestNetwork");

        // Toggle back: uncomment
        commentAction.actionPerformedImpl(getProject(), myFixture.getEditor());
        myFixture.checkResult("network = MyTestNetwork");
    }

    /**
     * Test line comment with the caret in the middle of a key-value line.
     */
    public void testLineCommentCaretMiddle() {
        myFixture.configureByText(IniFileType.INSTANCE,
                "network <caret>= MyTestNetwork");

        CommentByLineCommentAction commentAction = new CommentByLineCommentAction();
        commentAction.actionPerformedImpl(getProject(), myFixture.getEditor());
        myFixture.checkResult("#network = MyTestNetwork");

        // Toggle back
        commentAction.actionPerformedImpl(getProject(), myFixture.getEditor());
        myFixture.checkResult("network = MyTestNetwork");
    }

    /**
     * Test toggling a line comment on a section header line.
     */
    public void testLineCommentOnSectionHeader() {
        myFixture.configureByText(IniFileType.INSTANCE,
                "<caret>[Config Tictoc1]");

        CommentByLineCommentAction commentAction = new CommentByLineCommentAction();
        commentAction.actionPerformedImpl(getProject(), myFixture.getEditor());
        myFixture.checkResult("#[Config Tictoc1]");

        // Toggle back
        commentAction.actionPerformedImpl(getProject(), myFixture.getEditor());
        myFixture.checkResult("[Config Tictoc1]");
    }
}