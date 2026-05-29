package com.streats.ancientextensions.dex;

import net.minecraft.network.chat.Component;

/**
 * One checklist entry on the Regional Survey Journal.
 */
public record SurveyGoal(
        String id,
        Component label,
        boolean complete,
        Component progressHint
) {
    public Component statusLine() {
        if (complete) {
            return Component.translatable("ancient_extensions.journal.goal_done", label);
        }
        if (progressHint.getString().isEmpty()) {
            return Component.translatable("ancient_extensions.journal.goal_todo_short", label);
        }
        return Component.translatable("ancient_extensions.journal.goal_todo", label, progressHint);
    }
}
