package com.whyfi.engine;

import com.whyfi.engine.dto.CompoundingFrequency;
import com.whyfi.engine.dto.Preset;
import com.whyfi.engine.dto.ScenarioInput;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Each preset pairs a relatable hook with pre-filled slider values. This is
 * the primary edtech surface: most first-time users should land on one of
 * these, not a blank form with unfamiliar fields.
 */
@Component
public class PresetLibrary {

    public List<Preset> all() {
        return List.of(
                new Preset(
                        "latte-factor",
                        "The latte factor",
                        "Skip a $6 daily coffee and invest it instead — what does that actually add up to?",
                        new ScenarioInput(
                                BigDecimal.ZERO,
                                BigDecimal.valueOf(180),
                                BigDecimal.valueOf(8),
                                30,
                                BigDecimal.valueOf(3),
                                CompoundingFrequency.MONTHLY
                        )
                ),
                new Preset(
                        "start-25-vs-35",
                        "Starting at 25 vs. 35",
                        "Same monthly contribution, same rate — the only difference is a 10-year head start.",
                        new ScenarioInput(
                                BigDecimal.ZERO,
                                BigDecimal.valueOf(300),
                                BigDecimal.valueOf(8),
                                35,
                                BigDecimal.valueOf(3),
                                CompoundingFrequency.MONTHLY
                        )
                ),
                new Preset(
                        "401k-match",
                        "The employer match left on the table",
                        "Not contributing enough to get the full 401k match is leaving free money — and its compounding — on the table.",
                        new ScenarioInput(
                                BigDecimal.ZERO,
                                BigDecimal.valueOf(500),
                                BigDecimal.valueOf(7),
                                30,
                                BigDecimal.valueOf(3),
                                CompoundingFrequency.MONTHLY
                        )
                )
        );
    }
}