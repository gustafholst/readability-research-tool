package gillberg.holst.features;

import gillberg.holst.CalculatedFeature;
import gillberg.holst.Calculator;
import gillberg.holst.Context;
import gillberg.holst.calculators.LinesOfCodeCalculator;
import gillberg.holst.enums.Paradigm;

public class LinesOfCode extends CalculatedFeature {
    @Override
    public String getName() {
        return "lines_of_code";
    }

    @Override
    public Calculator[] getCalculators(Context context) {
        final Calculator[] calculators = new Calculator[Paradigm.values().length];

        for (Paradigm p : Paradigm.values()) {
            calculators[p.ordinal()] = new LinesOfCodeCalculator(context, p);
        }

        return calculators;
    }
}
