package gillberg.holst;

import com.ipeirotis.readability.engine.Readability;

public class ReadabilityComplexity {

    public double buseReadability = Double.MIN_VALUE;
    public double scalabrinoReadability = Double.MIN_VALUE;
    public int cyclomaticComplexity = Integer.MIN_VALUE;

    public ReadabilityComplexity() {

    }

    public ReadabilityComplexity(double bw, double sc, int complexity) {
        this.buseReadability = bw;
        this.scalabrinoReadability = sc;
        this.cyclomaticComplexity = complexity;
    }
}
