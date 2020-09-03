package gillberg.holst;

import gillberg.holst.features.BuseReadability;
import gillberg.holst.features.CyclomaticComplexity;
import gillberg.holst.features.ScalabrinoReadability;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class CalculatedFeaturesTest {

    @Test
    public void featuresShouldBeStoredInTheOrderTheyAreAdded() {

        CalculatedFeatures calculatedFeatures = CalculatedFeatures.getInstance();

        calculatedFeatures.addFeature(new CyclomaticComplexity());
        calculatedFeatures.addFeature(new BuseReadability());
        calculatedFeatures.addFeature(new ScalabrinoReadability());

        List<CalculatedFeature> storedFeatures = calculatedFeatures.getFeatures();

        assertEquals(storedFeatures.get(0).getName(), "cyclomatic_complexity");
        assertEquals(storedFeatures.get(1).getName(), "buse_readability");
        assertEquals(storedFeatures.get(2).getName(), "scalabrino_readability");
    }
}
