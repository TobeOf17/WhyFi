package com.whyfi.engine;

import com.whyfi.engine.dto.Milestone;
import com.whyfi.engine.dto.Milestone.MilestoneType;
import com.whyfi.engine.dto.ScenarioInput;
import com.whyfi.engine.dto.ScenarioResult;
import com.whyfi.engine.dto.YearPoint;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Real-world concepts this engine deliberately encodes:
 *
 * 1. BigDecimal, not double — money math must not carry binary floating
 *    point rounding error across dozens of compounding periods.
 * 2. Compounding frequency matters — 8% compounded monthly is not the same
 *    number as 8% compounded annually; the difference is small per period
 *    but the project's whole premise is that small per-period differences
 *    compound into large outcomes, so the engine has to model it correctly
 *    rather than approximate with a single annual step.
 * 3. Ordinary annuity timing — contributions are modeled as landing at the
 *    END of each period (the standard assumption for "I contribute monthly
 *    from my paycheck"), not the start. This is a real, if small, difference
 *    from an "annuity due" model and should stay an explicit, documented
 *    choice rather than an accident of implementation.
 * 4. Nominal vs. real returns — every result carries both. Inflation quietly
 *    erodes purchasing power at scale; a 30-year projection that only shows
 *    nominal dollars is technically correct and practically misleading.
 * 5. Growth vs. contribution decomposition — cumulativeGrowth is tracked
 *    separately from cumulativeContributions at every year, because the
 *    "why" mechanism view depends on being able to show that split, not
 *    just the final number.
 * 6. Milestones as first-class output — the crossover year where growth
 *    overtakes contributions, the doubling point, and the rule-of-72
 *    estimate are computed here, once, from the same numbers driving the
 *    chart — not re-derived or eyeballed on the frontend.
 *
 * Explicitly NOT modeled here (out of scope for this engine, by design):
 * taxes, fees/expense ratios, sequence-of-returns risk (this assumes a
 * constant rate, not a simulated path), and withdrawal/decumulation phases.
 * Each is called out separately below this class.
 */
@Service
public class CompoundInterestEngine {

    private static final MathContext MC = new MathContext(12, RoundingMode.HALF_UP);
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    public ScenarioResult calculate(ScenarioInput input) {
        int periodsPerYear = input.compoundingFrequency().periodsPerYear();
        BigDecimal periodRate = input.annualRatePercent()
                .divide(HUNDRED, MC)
                .divide(BigDecimal.valueOf(periodsPerYear), MC);
        BigDecimal periodContribution = toPeriodContribution(input.monthlyContribution(), periodsPerYear);
        BigDecimal annualInflation = input.annualInflationPercent().divide(HUNDRED, MC);

        List<YearPoint> series = new ArrayList<>();
        BigDecimal balance = input.startingPrincipal();
        BigDecimal cumulativeContributions = input.startingPrincipal();

        series.add(yearPoint(0, balance, cumulativeContributions, annualInflation));

        int totalPeriods = input.years() * periodsPerYear;
        for (int period = 1; period <= totalPeriods; period++) {
            balance = balance.multiply(BigDecimal.ONE.add(periodRate), MC).add(periodContribution);
            cumulativeContributions = cumulativeContributions.add(periodContribution);

            if (period % periodsPerYear == 0) {
                int year = period / periodsPerYear;
                series.add(yearPoint(year, balance, cumulativeContributions, annualInflation));
            }
        }

        List<Milestone> milestones = detectMilestones(series, input.annualRatePercent());
        YearPoint last = series.get(series.size() - 1);

        return new ScenarioResult(series, milestones, last.balanceNominal(), last.balanceReal());
    }

    private YearPoint yearPoint(int year, BigDecimal balance, BigDecimal cumulativeContributions, BigDecimal annualInflation) {
        BigDecimal cumulativeGrowth = balance.subtract(cumulativeContributions).max(BigDecimal.ZERO);
        BigDecimal deflator = BigDecimal.ONE.add(annualInflation).pow(Math.max(year, 0), MC);
        BigDecimal balanceReal = balance.divide(deflator, MC);
        return new YearPoint(
                year,
                round2(balance),
                round2(balanceReal),
                round2(cumulativeContributions),
                round2(cumulativeGrowth)
        );
    }

    /**
     * Detects the milestones that back the "why" and annotated-chart views.
     * These are computed once here so the frontend never has to re-derive
     * them from raw series data and risk disagreeing with the numbers shown.
     */
    private List<Milestone> detectMilestones(List<YearPoint> series, BigDecimal annualRatePercent) {
        List<Milestone> milestones = new ArrayList<>();

        for (YearPoint point : series) {
            if (point.year() > 0 && point.cumulativeGrowth().compareTo(point.cumulativeContributions()) > 0) {
                milestones.add(new Milestone(
                        point.year(),
                        MilestoneType.GROWTH_OVERTAKES_CONTRIBUTIONS,
                        "Year " + point.year() + ": growth now makes up more of the balance than what was put in"
                ));
                break;
            }
        }

        BigDecimal startingBalance = series.get(0).balanceNominal();
        if (startingBalance.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal doubleTarget = startingBalance.multiply(BigDecimal.valueOf(2));
            for (YearPoint point : series) {
                if (point.year() > 0 && point.balanceNominal().compareTo(doubleTarget) >= 0) {
                    milestones.add(new Milestone(
                            point.year(),
                            MilestoneType.BALANCE_DOUBLES,
                            "Year " + point.year() + ": the starting balance has doubled"
                    ));
                    break;
                }
            }
        }

        if (annualRatePercent.compareTo(BigDecimal.ZERO) > 0) {
            int ruleOf72Year = new BigDecimal(72).divide(annualRatePercent, MC).setScale(0, RoundingMode.HALF_UP).intValue();
            milestones.add(new Milestone(
                    ruleOf72Year,
                    MilestoneType.RULE_OF_72_ESTIMATE,
                    "Rule of 72 estimate: money doubles roughly every " + ruleOf72Year + " years at this rate"
            ));
        }

        return milestones;
    }

    private BigDecimal toPeriodContribution(BigDecimal monthlyContribution, int periodsPerYear) {
        BigDecimal annualContribution = monthlyContribution.multiply(BigDecimal.valueOf(12));
        return annualContribution.divide(BigDecimal.valueOf(periodsPerYear), MC);
    }

    private BigDecimal round2(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}