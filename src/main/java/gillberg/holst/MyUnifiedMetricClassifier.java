package gillberg.holst;

import it.unimol.readability.metric.API.UnifiedMetricClassifier;
import it.unimol.readability.metric.API.WekaException;
import it.unimol.readability.metric.FeatureCalculator;
import org.apache.commons.io.FileUtils;

import org.eclipse.jdt.core.dom.*;
import selector.CandidateMethodTrigger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MyUnifiedMetricClassifier extends UnifiedMetricClassifier {

    public static MyUnifiedMetricClassifier loadClassifier(File inFile) {
        MyUnifiedMetricClassifier metricClassifier = null;
        try {
            FileInputStream fileIn = new FileInputStream(inFile);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            metricClassifier = (MyUnifiedMetricClassifier)in.readObject();
            in.close();
            fileIn.close();

            metricClassifier.reloadFeatureCalculators();
        } catch (IOException i) {
            throw new RuntimeException(i);
        } catch (ClassNotFoundException c) {
            throw new RuntimeException(c);
        }
        return metricClassifier;
    }

    private void reloadFeatureCalculators() {
        this.calculators = FeatureCalculator.getFeatureCalculators();
        updateFeatureCalculators();
    }

    public Map<String, Double> getReadabilityMap(File fileName) throws IOException {
        String content = FileUtils.readFileToString(fileName, "UTF-8");

        CandidateMethodTrigger trigger = new CandidateMethodTrigger();
        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setKind(8);
        parser.setSource(content.toCharArray());
        ASTNode node = parser.createAST(null);
        ReadabilityVisitor visitor = new ReadabilityVisitor(content, this);
        node.accept(visitor);
        return visitor.getReadabilityPerMethod();
    }

    class ReadabilityVisitor extends ASTVisitor {
        private String source;

        private UnifiedMetricClassifier classifier;

        private ReadabilityClassAggregator aggregator;

        public ReadabilityVisitor(String source, UnifiedMetricClassifier classifier) {
            this.source = source;
            this.classifier = classifier;
            this.aggregator = new ReadabilityClassPerMethodAggregator();
        }

        public boolean visit(MethodDeclaration node) {
            String methodContent = this.source.substring(node.getStartPosition(), node.getStartPosition() + node.getLength());
            try {
                Double readability = this.classifier.classify(methodContent);
                this.aggregator.addValue(readability, node.getName().toString());
            } catch (WekaException e) {
                Logger.getLogger(UnifiedMetricClassifier.class.getName()).log(Level.SEVERE, "Classifying exception", e);
            }
            return super.visit(node);
        }

        public double getReadability() {
            return this.aggregator.getAggregated();
        }

        public Map<String, Double> getReadabilityPerMethod() {
            return this.aggregator.getAggregatedPerMethod();
        }
    }

    static interface ReadabilityClassAggregator {
        void addValue(Double param1Double, String methodName);

        double getAggregated();
        Map<String, Double> getAggregatedPerMethod();
    }

    class ReadabilityClassPerMethodAggregator implements ReadabilityClassAggregator {
        private List<Double> values = new ArrayList<>();
        private Map<String, Double> valuesMap = new HashMap<>();

        public void addValue(Double readability, String methodName) {
            this.values.add(readability);
            this.valuesMap.put(methodName, readability);
        }

        public double getAggregated() {
            double sum = 0.0D;
            for (Double value : this.values)
                sum += value.doubleValue();
            return sum / this.values.size();
        }

        @Override
        public Map<String, Double> getAggregatedPerMethod() {
            return valuesMap;
        }
    }
}
