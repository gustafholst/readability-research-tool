package gillberg.holst;


import gillberg.holst.enums.Feature;
import gillberg.holst.exceptions.FeatureAlreadySetException;
import gillberg.holst.exceptions.FeatureNotSetException;

public class CalculatedFeatures {

    private final Number[] values;

    public CalculatedFeatures() {
        values = new Number[Feature.values().length];
    }

    public void setCalculatedFeature(Feature feature, Number value) throws FeatureAlreadySetException {
        if (values[feature.ordinal()] != null) {
            throw new FeatureAlreadySetException("Value for " + feature.toString() + " is already set");
        }
        values[feature.ordinal()] = value;
    }

    public Number getCalculatedFeature(Feature feature) throws FeatureNotSetException {
        if (values[feature.ordinal()] == null) {
            throw new FeatureNotSetException("Value for " + feature.toString() + " is missing");
        }
        return values[feature.ordinal()];
    }
}
