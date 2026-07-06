package com.whyfi.engine.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * All money fields are BigDecimal, never double or float — binary floating
 * point cannot represent most decimal fractions exactly (0.1 + 0.2 != 0.3),
 * and that drift compounds literally over a 30-year projection.
 */
public record ScenarioInput(

        @NotNull
        @DecimalMin(value = "0.0")
        @DecimalMax(value = "100000000.0")
        BigDecimal startingPrincipal,

        @NotNull
        @DecimalMin(value = "0.0")
        @DecimalMax(value = "100000.0")
        BigDecimal monthlyContribution,

        @NotNull
        @DecimalMin(value = "-20.0")
        @DecimalMax(value = "30.0")
        BigDecimal annualRatePercent,

        @NotNull
        @Min(1)
        @Max(60)
        Integer years,

        @DecimalMin(value = "0.0")
        @DecimalMax(value = "20.0")
        BigDecimal annualInflationPercent,

        CompoundingFrequency compoundingFrequency
) {
    public ScenarioInput {
        if (compoundingFrequency == null) {
            compoundingFrequency = CompoundingFrequency.MONTHLY;
        }
        if (annualInflationPercent == null) {
            annualInflationPercent = BigDecimal.ZERO;
        }
    }
}