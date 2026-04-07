// Line comment
/* Block comment */

package org.example;

import org.example.other.*;

simple Source extends Base like IModule {
    parameters:
        volatile double sendInterval @unit(s) = default(1s);
        string destAddress = "10.0.0.1";
        int count = 42;
        bool enabled = true;
        xml config;
    gates:
        input in;
        output out;
        inout io;
}

module MyNetwork {
    submodules:
        src: Source {
            sendInterval = 2s;
        }
    connections allowunconnected:
        src.out --> src.in;
}

network TestNet extends MyNetwork {
}

channelinterface IChannel {
}

moduleinterface IModule {
}

channel DataChannel extends ned.DatarateChannel {
    delay = 100us;
}