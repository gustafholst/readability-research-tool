package gillberg.holst.calculators;

import com.github.javaparser.ast.CompilationUnit;
import gillberg.holst.*;
import gillberg.holst.enums.Paradigm;
import gillberg.holst.exceptions.FeatureAlreadySetException;
import gillberg.holst.exceptions.MethodNotRefactoredException;
import gillberg.holst.exceptions.UnknownParadigmException;
import gillberg.holst.features.DynamicFeature;
import it.unimol.readability.metric.FeatureCalculator;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ScalabrinoFeaturesCalculator extends AbstractCalculator implements Calculator {

    public ScalabrinoFeaturesCalculator(Context context, Paradigm paradigm) {
        super(context, paradigm);
    }

    @Override
    public String getName() {
        return "Scalabrino features";
    }

    @Override
    public void calculate() throws IOException {

        File[] files = getJavaFilesFromDir(getDirectory());

        for (File file : files) {
            CompilationUnit cu = readFile(file);
            parseCompilationUnitAndStoreResults(cu);
        }
    }

    private void parseCompilationUnitAndStoreResults(CompilationUnit compilationUnit) {
        //get features from SC impl (it.unimol.readability.metric.FeatureCalculator)
        List<FeatureCalculator> featureCalculators = FeatureCalculator.getFeatureCalculators();

        CalculatedFeatures instance = CalculatedFeatures.getInstance();

        //add every feature to CalculatedFeatures singleton
        for (FeatureCalculator fc : featureCalculators) {
            instance.addFeature(new DynamicFeature(fc.getName()));
        }

        compilationUnit.getTypes()
                .forEach(t -> t.getMethods()   // for each class
                        .forEach(m -> {        // for each method
                            try {
                                Method method = getMethod(t.getNameAsString(), m);

                                if (context.shouldCalculate(method.className, method.signature)) {
                                    for (FeatureCalculator fc : featureCalculators) {

                                        CalculatedFeature scFeature = new DynamicFeature(fc.getName());

                                            m.getTokenRange().ifPresent(tr -> {
                                                String methodString = tr.toString();
                                                fc.setSource(methodString);
                                                double val = fc.calculate();

                                                try {
                                                    method.addCalculatedFeature(scFeature, val, getParadigm());
                                                } catch (UnknownParadigmException e) {
                                                    e.printStackTrace();
                                                } catch (FeatureAlreadySetException e) {
                                                    System.err.println("Feature \"" + fc.getName() + "\"\t already set for method " + method.signature );
                                                }
                                            });
                                        }
                                    }
                            } catch (MethodNotRefactoredException ignored) {
                                //ignore
                            } catch (IOException ioe) {
                                ioe.printStackTrace();
                            }
                        })
                );
    }
}
