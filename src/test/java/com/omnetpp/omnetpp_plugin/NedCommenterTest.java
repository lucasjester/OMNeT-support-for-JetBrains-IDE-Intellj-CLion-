package com.omnetpp.omnetpp_plugin;

import com.intellij.codeInsight.generation.actions.CommentByBlockCommentAction;
import com.intellij.codeInsight.generation.actions.CommentByLineCommentAction;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.omnetpp.omnetpp_plugin.ned.NedFileType;

/**
 * Tests for the NED Commenter.
 *
 * Based on the JetBrains "Testing Custom Language Support" tutorial, section 9:
 * https://plugins.jetbrains.com/docs/intellij/commenter-test.html
 */
public class NedCommenterTest extends BasePlatformTestCase {

    /**
     * Test toggling a line comment on a simple NED statement.
     * Mirrors the tutorial's testCommenter() approach.
     */
    public void testLineComment() {
        myFixture.configureByText(NedFileType.INSTANCE,
                "<caret>simple Source {}");

        CommentByLineCommentAction commentAction = new CommentByLineCommentAction();
        commentAction.actionPerformedImpl(getProject(), myFixture.getEditor());
        myFixture.checkResult("//simple Source {}");

        // Toggle back: uncomment
        commentAction.actionPerformedImpl(getProject(), myFixture.getEditor());
        myFixture.checkResult("simple Source {}");
    }

    /**
     * Test line comment with the caret in the middle of a line.
     */
    public void testLineCommentCaretMiddle() {
        myFixture.configureByText(NedFileType.INSTANCE,
                "simple <caret>Source {}");

        CommentByLineCommentAction commentAction = new CommentByLineCommentAction();
        commentAction.actionPerformedImpl(getProject(), myFixture.getEditor());
        myFixture.checkResult("//simple Source {}");

        // Toggle back
        commentAction.actionPerformedImpl(getProject(), myFixture.getEditor());
        myFixture.checkResult("simple Source {}");
    }

    /**
     * Test wrapping a selected substring of a line in a /* ... *\/ block
     * comment, then toggling it back.
     */
    public void testBlockCommentSelection() {
        myFixture.configureByText(NedFileType.INSTANCE,
                "simple <selection>Source<caret></selection> {}");

        CommentByBlockCommentAction blockAction = new CommentByBlockCommentAction();
        blockAction.actionPerformedImpl(getProject(), myFixture.getEditor());
        myFixture.checkResult("simple /*Source*/ {}");

        // Toggle back: uncomment
        blockAction.actionPerformedImpl(getProject(), myFixture.getEditor());
        myFixture.checkResult("simple Source {}");
    }

}