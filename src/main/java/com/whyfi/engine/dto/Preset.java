package com.whyfi.engine.dto;

public record Preset(
        String id,
        String title,
        String hook,
        ScenarioInput input
) {}