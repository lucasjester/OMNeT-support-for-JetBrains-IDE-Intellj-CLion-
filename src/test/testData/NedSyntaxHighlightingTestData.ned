// ============================================================
//  NedSyntaxHighlightingTestData.ned
//  Comprehensive NED lexer coverage. Each block exercises a
//  specific token category from Ned.flex.
// ============================================================

// --- Comment styles ---
// Line comment with //
/* Block comment, single line */
/*
 * Multi-line block comment
 * spanning several lines
 */

// --- File-level: package, import, property ---
package org.example.test;

import org.example.other.*;
import org.example.deeper.SomeType;

property @namespace(name = "test");

// --- Module interfaces and channel interfaces ---
moduleinterface IModule {
    parameters:
        string name;
    gates:
        input in;
        output out;
}

channelinterface IChannel {
    parameters:
        double delay @unit(s);
}

// --- Simple module: type keywords, modifiers, all literals, reflection ---
simple Producer extends Base like IModule {
    parameters:
        // Properties on parameters
        @display("i=block/source");
        @class(ProducerImpl);
        @signal[packetSent](type=long);
        @statistic[throughput](title="Throughput"; source=packetSent; record=mean,max);

        // All type keywords
        int intParam       = 42;
        double doubleParam = 3.14;
        bool boolParam     = true;
        string strParam    = "hello";
        xml xmlParam;
        object objParam;
        xmldoc xmldocParam;

        // All modifier keywords
        volatile double v @unit(s) = default(1s);
        ask string promptedValue;

        // All literal keywords
        bool   tval    = true;
        bool   fval    = false;
        object nval    = null;
        object npval   = nullptr;
        double uval    = undefined;
        double naval   = nan;
        double infval  = inf;

        // Numeric forms: int, negative, scientific, leading-dot, expression
        int    negInt     = -7;
        double sci        = 1.5e-3;
        double leadingDot = .5;
        int    expr       = 2 * intParam + 1;

        // Reflection / meta keywords
        string tn  = typename;
        int    idx = index;
        int    sz  = sizeof(this);
        bool   exP = exists(parent);
    gates:
        input in;
        output out;
        output multiOut[];
        inout io;
}

simple Consumer { gates: input in; }
simple Base {}

// --- Compound module: types section, submodules, all connection arrows, control flow ---
module Network1 {
    types:
        channel LocalLink extends ned.DelayChannel {
            delay = 1ms;
        }
    parameters:
        int numHosts = default(4);
    gates:
        input externalIn @loose;
    submodules:
        src: Producer { v = 2s; }
        snk: Consumer;
        host[numHosts]: Producer;
    connections allowunconnected:
        src.out --> snk.in;                            // -->
        snk.in  <-- src.multiOut[0];                   // <--
        host[0].io <--> host[1].io;                    // <-->
        src.out --> LocalLink --> snk.in;              // channel-spec
        for i=0..numHosts-1 {
            host[i].out --> snk.in if i % 2 == 0;      // for + if
        }
}

// --- Network ---
network TestNet extends Network1 {
    parameters:
        numHosts = 8;
}

// --- Channel ---
channel DataChannel extends ned.DatarateChannel {
    @class(DataChannelImpl);
    delay    = 1ms;
    datarate = 100Mbps;
}

// --- Operator coverage: arithmetic, comparison, logical, bitwise, ternary ---
simple Operators {
    parameters:
        // Arithmetic
        int  add  = 1 + 2;
        int  sub  = 1 - 2;
        int  mul  = 1 * 2;
        int  div  = 1 / 2;
        int  mod  = 1 % 2;
        int  pow  = 2 ^ 3;
        int  bnot = ~1;
        // Comparison
        bool eq    = 1 == 2;
        bool neq   = 1 != 2;
        bool lt    = 1 < 2;
        bool le    = 1 <= 2;
        bool gt    = 1 > 2;
        bool ge    = 1 >= 2;
        bool spcmp = 1 <=> 2;
        bool match = "abc" =~ "ab*";
        // Logical
        bool and = true && false;
        bool or  = true || false;
        bool not = !true;
        // Bitwise
        int  band  = 1 & 2;
        int  bor   = 1 | 2;
        int  dhash = 1 ## 2;
        // Range and ternary
        int  range = 1..10;
        int  cond  = (1 > 2) ? 100 : 200;
}

// --- Special punctuation: scope-resolution, $-prefixed name ---
simple Special {
    parameters:
        string scope = ned::Sym;       // ::
        double iter  = $rep + 1;       // $ + identifier
}
