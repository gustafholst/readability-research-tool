package gillberg.holst.features;

import gillberg.holst.*;
import gillberg.holst.calculators.ComplexityCalculator;
import gillberg.holst.enums.Paradigm;

public class CyclomaticComplexity extends CalculatedFeature {
    @Override
    public String getName() {
        return "cyclomatic_complexity";
    }

    @Override
    public Calculator[] getCalculators(Context context) {
        final Calculator[] calculators = new Calculator[Paradigm.values().length];

        for (Paradigm p : Paradigm.values()) {
            calculators[p.ordinal()] = new ComplexityCalculator(context, p);
        }
        //calculators[Paradigm.imperative.ordinal()] = new ComplexityCalculator(context, Paradigm.imperative);
        //calculators[Paradigm.reactive.ordinal()] = new ComplexityCalculator(context, Paradigm.reactive);

        return calculators;
    }
}
