package com.omnetpp.omnetpp_plugin.ned.lexer;

import com.intellij.psi.tree.TokenSet;
import com.omnetpp.omnetpp_plugin.ned.psi.NedTypes;

public interface NedTokenSets {
    TokenSet COMMENTS = TokenSet.create(
            NedTypes.LINE_COMMENT,
            NedTypes.BLOCK_COMMENT
    );

    TokenSet STRINGS = TokenSet.create(NedTypes.STRINGCONSTANT);
}
