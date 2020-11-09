package gillberg.holst.calculators;

import com.github.javaparser.Position;
import com.github.javaparser.ast.CompilationUnit;
import gillberg.holst.Calculator;
import gillberg.holst.Context;
import gillberg.holst.Method;
import gillberg.holst.enums.Paradigm;
import gillberg.holst.exceptions.FeatureAlreadySetException;
import gillberg.holst.exceptions.MethodNotRefactoredException;
import gillberg.holst.exceptions.UnknownParadigmException;
import gillberg.holst.features.LinesOfCode;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class LinesOfCodeCalculator extends AbstractCalculator implements Calculator {

    public LinesOfCodeCalculator(Context context, Paradigm paradigm) {
        super(context, paradigm);
    }

    @Override
    public void calculate() throws IOException, FeatureAlreadySetException, MethodNotRefactoredException, UnknownParadigmException {
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

                                    Optional<Position> begin = m.getBegin();
                                    Optional<Position> end = m.getEnd();

                                    int loc = 0;
                                    if (begin.isPresent() && end.isPresent()) {
                                        loc = end.get().line - begin.get().line + 1;
                                    }

                                    method.addCalculatedFeature(new LinesOfCode(), loc, paradigm);
                                }
                            } catch (MethodNotRefactoredException | UnknownParadigmException | FeatureAlreadySetException | IOException e) {
                                System.out.println(e.getMessage());
                            }

                        }));
    }

    @Override
    public String getName() {
        return "LOC";
    }
}
