package gillberg.holst.calculators;

import com.github.javaparser.ast.CompilationUnit;
import gillberg.holst.Calculator;
import gillberg.holst.Method;
import gillberg.holst.MySerializedReadability;
import gillberg.holst.enums.Paradigm;
import gillberg.holst.exceptions.FeatureAlreadySetException;
import gillberg.holst.exceptions.MethodNotRefactoredException;
import gillberg.holst.exceptions.UnknownParadigmException;
import gillberg.holst.features.BuseReadability;
import gillberg.holst.features.ScalabrinoReadability;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static raykernel.apps.readability.eval.Main.getReadability;

public class ScalabrinoCalculator extends AbstractCalculator implements Calculator {

    private final MySerializedReadability scalabrinoReadability;

    public ScalabrinoCalculator(String dir, List<Method> methods) {
        super(dir, methods);
        this.scalabrinoReadability = new MySerializedReadability();
    }

    @Override
    public void calculate() throws IOException {
        File[] files = getJavaFilesFromDir(this.directory);

        for (File file : files) {
            parseFileAndStoreResults(file);
        }
    }

    private void parseFileAndStoreResults(File file) {

        try {
            Map<String, Double> readabilityMap = scalabrinoReadability.getReadabilityMap(file);

            for (String method : readabilityMap.keySet()) {
                Method m = getMethod(getClassName(), method);

                Double value = readabilityMap.get(method);
                m.addCalculatedFeature(new ScalabrinoReadability(), value, getParadigm());
            }

        } catch (IOException | MethodNotRefactoredException | UnknownParadigmException | FeatureAlreadySetException e) {
            e.printStackTrace();
        }
    }

    private String getClassName() {
        return this.directory.substring(0, this.directory.indexOf('.'));
    }

}
