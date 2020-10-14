package gillberg.holst.features;

import gillberg.holst.CalculatedFeature;
import gillberg.holst.Calculator;
import gillberg.holst.Context;
import gillberg.holst.calculators.BuseCalculator;
import gillberg.holst.calculators.CycFeaturesCalculator;
import gillberg.holst.enums.Paradigm;

public class CyclomaticComplexityFeatures extends CalculatedFeature {
    @Override
    public String getName() {
        return "cyclomatic complexity features";
    }

    @Override
    public Calculator[] getCalculators(Context context) {
        final Calculator[] calculators = new Calculator[Paradigm.values().length];

        for (Paradigm p : Paradigm.values()) {
            calculators[p.ordinal()] = new CycFeaturesCalculator(context, p);
        }

        return calculators;
    }
}
