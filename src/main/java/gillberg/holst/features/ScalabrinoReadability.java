package gillberg.holst.features;

import gillberg.holst.CalculatedFeature;
import gillberg.holst.Calculator;
import gillberg.holst.Context;
import gillberg.holst.calculators.ScalabrinoCalculator;
import gillberg.holst.enums.Paradigm;

public class ScalabrinoReadability extends CalculatedFeature {

    @Override
    public String getName() {
        return "scalabrino_readability";
    }

    @Override
    public Calculator[] getCalculators(Context context) {
        final Calculator[] calculators = new Calculator[Paradigm.values().length];

        for (Paradigm p : Paradigm.values()) {
            calculators[p.ordinal()] = new ScalabrinoCalculator(context, p);
        }

        return calculators;
    }
}
