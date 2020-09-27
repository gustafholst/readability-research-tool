package gillberg.holst.features;

import gillberg.holst.CalculatedFeature;
import gillberg.holst.Calculator;
import gillberg.holst.Context;

public class DynamicFeature extends CalculatedFeature {

    private String name;

    public DynamicFeature(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Calculator[] getCalculators(Context context) {
        return new Calculator[0];
    }
}
