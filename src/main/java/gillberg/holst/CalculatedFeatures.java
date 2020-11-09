package gillberg.holst;

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
        if (!this.features.contains(newFeature))
            this.features.add(newFeature);
    }

    public List<CalculatedFeature> getFeatures() {
        return this.features;
    }
}
