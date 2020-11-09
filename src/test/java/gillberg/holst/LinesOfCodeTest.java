package gillberg.holst;

import gillberg.holst.calculators.LinesOfCodeCalculator;
import gillberg.holst.enums.Paradigm;
import gillberg.holst.exceptions.FeatureAlreadySetException;
import gillberg.holst.exceptions.FeatureNotSetException;
import gillberg.holst.exceptions.MethodNotRefactoredException;
import gillberg.holst.exceptions.UnknownParadigmException;
import gillberg.holst.features.LinesOfCode;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class LinesOfCodeTest extends BaseTest {

    @Test()
    public void CalculatingComplexityOfClassWithOneOriginalMethod() throws IOException, FeatureNotSetException, FeatureAlreadySetException, MethodNotRefactoredException, UnknownParadigmException {

        whenRefactoredMethodsHasBeenCreatedWithOneMethod();

        andLinesOfCodeCalculatorPointingToOriginalSourceFileHasBeenCreated();

        calculator.calculate();

        CalculatedFeature locFeature = sendToTcpMethod.findCalculatedFeature(new LinesOfCode());
        Number actual = locFeature.getValueForOriginal();

        Integer expectedNumberOfLines = 10;

        assertEquals(expectedNumberOfLines, actual);
    }

    private void andLinesOfCodeCalculatorPointingToOriginalSourceFileHasBeenCreated() {
        this.calculator = new LinesOfCodeCalculator(this.currentContext, Paradigm.imperative);
    }

    private void andLinesOfCodeCalculatorPointingToRefactoredSourceFileHasBeenCreated() {
        this.calculator = new LinesOfCodeCalculator(this.currentContext, Paradigm.reactive);
    }
}
