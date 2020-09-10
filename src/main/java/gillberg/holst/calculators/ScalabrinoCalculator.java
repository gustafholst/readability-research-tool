package gillberg.holst.calculators;

import com.github.javaparser.ast.CompilationUnit;
import gillberg.holst.Calculator;
import gillberg.holst.Context;
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
import java.util.Optional;

public class ScalabrinoCalculator extends AbstractCalculator implements Calculator {

    private final MySerializedReadability scalabrinoReadability;

    public ScalabrinoCalculator(Context context, Paradigm paradigm) {
        super(context, paradigm);
        this.scalabrinoReadability = new MySerializedReadability();
    }

    @Override
    public void calculate() {
        File[] files = getJavaFilesFromDir(getDirectory());

        for (File file : files) {
            parseFileAndStoreResults(file);
        }
    }

    private void parseFileAndStoreResults(File file) {

        try {
            Map<String, Double> readabilityMap = scalabrinoReadability.getReadabilityMap(file);

            for (String method : readabilityMap.keySet()) {
                try {
                    Method m = getMethod(getClassName(file.getName()), method);
                    Double value = readabilityMap.get(method);
                    m.addCalculatedFeature(new ScalabrinoReadability(), value, getParadigm());
                } catch (MethodNotRefactoredException | UnknownParadigmException | FeatureAlreadySetException e) {
                    System.out.println(e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    @Override
    public Method getMethod(String className, String signature) throws MethodNotRefactoredException, IOException {
        //Scalabrino tool doesnt not add parameter list (NOT ALLOWING OVERLOADED METHODS!!!)

        Method temp = new Method(className, signature);

        Optional<Method> foundMethod = context.getMethods().stream().
                filter(m -> {
                            String sign = m.signature.substring(0, m.signature.indexOf("("));
                            return sign.equals(signature) && m.className.equals(className);
                        }
                ).findFirst();

        if (foundMethod.isPresent()) {
            return foundMethod.get();
        }

        throw new MethodNotRefactoredException("No method [" + className + " " + signature + "] in memory");
    }

    private String getClassName(String fileName) {
        return fileName.substring(0, fileName.indexOf('.'));
    }

}
