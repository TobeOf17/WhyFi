package com.whyfi.engine.dto;

public record Milestone(
        int year,
        MilestoneType type,
        String label
) {
    public enum MilestoneType {
        GROWTH_OVERTAKES_CONTRIBUTIONS,
        BALANCE_DOUBLES,
        RULE_OF_72_ESTIMATE
    }
}