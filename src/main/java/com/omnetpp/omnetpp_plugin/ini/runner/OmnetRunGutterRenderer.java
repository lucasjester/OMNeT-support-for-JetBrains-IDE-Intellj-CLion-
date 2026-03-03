package com.omnetpp.omnetpp_plugin.ini.runner;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Arrays;
import java.util.Objects;

public class OmnetRunGutterRenderer extends GutterIconRenderer {

    private final Icon icon;
    private final String tooltip;
    private final AnAction[] actions;

    public OmnetRunGutterRenderer(@NotNull Icon icon,
                                  @NotNull String tooltip,
                                  @NotNull AnAction[] actions) {
        this.icon = icon;
        this.tooltip = tooltip;
        this.actions = actions;
    }

    @Override
    public @NotNull Icon getIcon() {
        return icon;
    }

    @Override
    public @Nullable String getTooltipText() {
        return tooltip;
    }

    /**
     * Left-click action (we run the first executor action, usually "Run").
     * Even if left-click just runs, the user can still right-click for Debug etc.
     */
    @Override
    public @Nullable AnAction getClickAction() {
        return actions.length > 0 ? actions[0] : null;
    }

    /**
     * Right-click popup menu actions (Run / Debug).
     */
    @Override
    public @Nullable ActionGroup getPopupMenuActions() {
        DefaultActionGroup group = new DefaultActionGroup();
        group.addAll(Arrays.asList(actions));
        return group;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OmnetRunGutterRenderer that)) return false;
        return Objects.equals(tooltip, that.tooltip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tooltip);
    }
}
