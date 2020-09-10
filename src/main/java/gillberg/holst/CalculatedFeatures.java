package gillberg.holst;


import gillberg.holst.enums.Feature;
import gillberg.holst.exceptions.FeatureAlreadySetException;
import gillberg.holst.exceptions.FeatureNotSetException;

import java.util.ArrayList;
import java.util.List;

public class CalculatedFeatures {

    private static CalculatedFeatures instance;

    private List<CalculatedFeature> features;

    private CalculatedFeatures() {
        this.features = new ArrayList<>();
    }

    public static CalculatedFeatures getInstance() {
        if (instance == null) {
            instance = new CalculatedFeatures();
        }

        return instance;
    }

    public void addFeature(CalculatedFeature newFeature) {
        this.features.add(newFeature);
    }

    public List<CalculatedFeature> getFeatures() {
        return this.features;
    }

    //    private final Number[] values;
//
//    public CalculatedFeatures() {
//        values = new Number[Feature.values().length];
//    }
//
//    public void setCalculatedFeature(Feature feature, Number value) throws FeatureAlreadySetException {
//        if (values[feature.ordinal()] != null) {
//            throw new FeatureAlreadySetException("Value for " + feature.toString() + " is already set");
//        }
//        values[feature.ordinal()] = value;
//    }
//
//    public Number getCalculatedFeature(Feature feature) throws FeatureNotSetException {
//        if (values[feature.ordinal()] == null) {
//            throw new FeatureNotSetException("Value for " + feature.toString() + " is missing");
//        }
//        return values[feature.ordinal()];
//    }
}
