package com.omnetpp.omnetpp_plugin.ini.runner.rc;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class ArgsSplitter {
    private ArgsSplitter() {}

    public static @NotNull List<String> split(@NotNull String s) {
        List<String> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;
        char quote = 0;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (inQuotes) {
                if (c == quote) {
                    inQuotes = false;
                } else {
                    cur.append(c);
                }
                continue;
            }

            if (c == '"' || c == '\'') {
                inQuotes = true;
                quote = c;
                continue;
            }

            if (Character.isWhitespace(c)) {
                if (cur.length() > 0) {
                    out.add(cur.toString());
                    cur.setLength(0);
                }
                continue;
            }

            cur.append(c);
        }

        if (cur.length() > 0) out.add(cur.toString());
        return out;
    }
}
