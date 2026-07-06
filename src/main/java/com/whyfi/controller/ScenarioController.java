package com.whyfi.controller;

import com.whyfi.engine.CompoundInterestEngine;
import com.whyfi.engine.PresetLibrary;
import com.whyfi.engine.dto.Preset;
import com.whyfi.engine.dto.ScenarioInput;
import com.whyfi.engine.dto.ScenarioResult;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/scenarios")
public class ScenarioController {

    private final CompoundInterestEngine engine;
    private final PresetLibrary presetLibrary;

    public ScenarioController(CompoundInterestEngine engine, PresetLibrary presetLibrary) {
        this.engine = engine;
        this.presetLibrary = presetLibrary;
    }

    @GetMapping("/presets")
    public List<Preset> presets() {
        return presetLibrary.all();
    }

    @PostMapping("/calculate")
    public ScenarioResult calculate(@Valid @RequestBody ScenarioInput input) {
        return engine.calculate(input);
    }
}