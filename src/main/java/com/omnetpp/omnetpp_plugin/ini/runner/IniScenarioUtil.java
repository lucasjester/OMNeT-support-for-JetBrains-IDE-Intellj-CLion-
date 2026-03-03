package com.omnetpp.omnetpp_plugin.ini.runner;

import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class IniScenarioUtil {

    // Matches: [Config Name]   (allow spaces)
    // Matcht BEIDE Formate:
// [Config Tictoc1]  → configName = "Tictoc1"
// [Tictoc1]         → configName = "Tictoc1"
// aber NICHT [General]
    private static final Pattern CONFIG_SECTION =
            Pattern.compile("^\\[\\s*(?:Config\\s+)?(.+?)\\s*]$");

    public static @Nullable String extractConfigName(String sectionHeaderText) {
        if (sectionHeaderText == null) return null;
        String t = sectionHeaderText.trim();
        // [General] ausschliessen
        if (t.equalsIgnoreCase("[General]")) return null;
        Matcher m = CONFIG_SECTION.matcher(t);
        if (!m.matches()) return null;
        return m.group(1).trim();
    }
}
