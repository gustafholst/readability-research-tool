package gillberg.holst.calculators;

import com.github.javaparser.ast.CompilationUnit;

import gillberg.holst.Calculator;
import gillberg.holst.Context;
import gillberg.holst.Method;
import gillberg.holst.enums.Paradigm;
import gillberg.holst.exceptions.FeatureAlreadySetException;
import gillberg.holst.exceptions.MethodNotRefactoredException;
import gillberg.holst.exceptions.UnknownParadigmException;
import gillberg.holst.features.BuseReadability;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static raykernel.apps.readability.eval.Main.getReadability;

public class BuseCalculator extends AbstractCalculator implements Calculator {

    public BuseCalculator(Context context, Paradigm paradigm) {
        super(context, paradigm);
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
        compilationUnit.getTypes()
                .forEach(t -> t.getMethods()   // for each class
                        .forEach(m -> {        // for each method
                            try {
                                if (context.shouldCalculate(t.getNameAsString(), m.getSignature().asString())) {
                                    Method method = getMethod(t.getNameAsString(), m);
                                    Paradigm paradigm = getParadigm();

                                    double readability = getReadability(m.toString());
                                    method.addCalculatedFeature(new BuseReadability(), readability, paradigm);
                                }
                            } catch (MethodNotRefactoredException | UnknownParadigmException | FeatureAlreadySetException | IOException e) {
                                System.out.println(e.getMessage());
                            }

                        }));
    }
}
