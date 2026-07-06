package com.whyfi.engine.dto;

import java.math.BigDecimal;

/**
 * balanceNominal is what the "what" view of the chart plots.
 * balanceReal (inflation-adjusted) is what a careful "what" view should
 * default to, or at minimum offer as a toggle — a nominal-only chart
 * overstates real purchasing power the further out it projects.
 *
 * cumulativeContributions vs cumulativeGrowth is the split the "why" toggle
 * needs: it lets the chart show, at any year, how much of the balance is
 * money the person put in versus money the market/interest generated.
 */
public record YearPoint(
        int year,
        BigDecimal balanceNominal,
        BigDecimal balanceReal,
        BigDecimal cumulativeContributions,
        BigDecimal cumulativeGrowth
) {}