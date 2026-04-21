// Single-line comment using //
/* Single-line block comment */
/*
 * Multi-line block comment
 * with a second line
 * and a third
 */

package org.example;

import org.example.other.*;
import org.example.deeper.SomeType;

channelinterface IChannel {
}

moduleinterface IModule {
}

simple Base {
}

simple Source extends Base like IModule {
    parameters:
        @display("i=block/source;is=s");
        @class(SourceImpl);
        @signal[packetSent](type=long);
        @statistic[throughput](title="Throughput"; source=packetSent; record=mean,max);
        volatile double sendInterval @unit(s) = default(1s);
        string destAddress = "10.0.0.1"; // inline comment after code
        int count = 42;
        int negativeCount = -7;
        double scientific = 1.5e-3;
        double computed = 2 * count + 1;
        bool enabled = true;
        xml config;
    gates:
        input in;
        output out;
        output multiOut[];
        inout io;
}

simple Sink {
    gates:
        input in[];
        inout io;
}

module MyNetwork {
    parameters:
        int numHosts = default(4);
    submodules:
        src: Source {
            sendInterval = 2s;
        }
        snk: Sink;
        host[numHosts]: Source;
    connections allowunconnected:
        src.out --> snk.in[0];
        snk.in[1] <-- src.multiOut[0];
        host[0].io <--> host[1].io;
        for i=0..numHosts-1 {
            host[i].out --> snk.in[i] if i % 2 == 0;
        }
}

network TestNet extends MyNetwork {
}

channel DataChannel extends ned.DatarateChannel {
    delay = 100us;
}