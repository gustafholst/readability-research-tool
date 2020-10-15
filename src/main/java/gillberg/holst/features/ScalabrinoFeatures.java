package gillberg.holst.features;

import gillberg.holst.CalculatedFeature;
import gillberg.holst.Calculator;
import gillberg.holst.Context;
import gillberg.holst.calculators.BuseCalculator;
import gillberg.holst.calculators.ScalabrinoCalculator;
import gillberg.holst.calculators.ScalabrinoFeaturesCalculator;
import gillberg.holst.enums.Paradigm;

public class ScalabrinoFeatures extends CalculatedFeature {

    @Override
    public String getName() {
        return "Scalabrino features";
    }

    @Override
    public boolean shouldBePresentedAsAColumn() {
        return false;
    }

    @Override
    public Calculator[] getCalculators(Context context) {
        final Calculator[] calculators = new Calculator[Paradigm.values().length];

        for (Paradigm p : Paradigm.values()) {
            calculators[p.ordinal()] = new ScalabrinoFeaturesCalculator(context, p);
        }

        return calculators;
    }
}
