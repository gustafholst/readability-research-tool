package gillberg.holst.features;

import gillberg.holst.CalculatedFeature;
import gillberg.holst.Calculator;
import gillberg.holst.Context;
import gillberg.holst.calculators.BuseCalculator;
import gillberg.holst.enums.Paradigm;

public class BuseReadability extends CalculatedFeature {

    @Override
    public String getName() {
        return "buse_readability";
    }

    @Override
    public Calculator[] getCalculators(Context context) {
        final Calculator[] calculators = new Calculator[Paradigm.values().length];

        for (Paradigm p : Paradigm.values()) {
            calculators[p.ordinal()] = new BuseCalculator(context, p);
        }

        return calculators;
    }

}
