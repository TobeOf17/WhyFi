package com.whyfi.engine.dto;

public enum CompoundingFrequency {
    MONTHLY(12),
    DAILY(365),
    ANNUALLY(1);

    private final int periodsPerYear;

    CompoundingFrequency(int periodsPerYear) {
        this.periodsPerYear = periodsPerYear;
    }

    public int periodsPerYear() {
        return periodsPerYear;
    }
}