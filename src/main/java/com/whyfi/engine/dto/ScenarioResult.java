package com.whyfi.engine.dto;

import java.math.BigDecimal;
import java.util.List;

public record ScenarioResult(
        List<YearPoint> series,
        List<Milestone> milestones,
        BigDecimal finalBalanceNominal,
        BigDecimal finalBalanceReal
) {}