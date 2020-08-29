package gillberg.holst.features;

import gillberg.holst.CalculatedFeature;

public class CyclomaticComplexity extends CalculatedFeature {
    @Override
    public String getName() {
        return "cyclomatic_complexity";
    }
}
