package gillberg.holst;

import gillberg.holst.calculators.BuseCalculator;
import gillberg.holst.calculators.ScalabrinoCalculator;
import gillberg.holst.exceptions.FeatureAlreadySetException;
import gillberg.holst.exceptions.FeatureNotSetException;
import gillberg.holst.exceptions.MethodNotRefactoredException;
import gillberg.holst.exceptions.UnknownParadigmException;
import gillberg.holst.features.BuseReadability;
import gillberg.holst.features.ScalabrinoReadability;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class ScalabrinoCalculatorTest extends BaseTest {

    @Test()
    public void CalculatingComplexityOfClassWithOneOriginalMethod() throws IOException, FeatureNotSetException, FeatureAlreadySetException, MethodNotRefactoredException, UnknownParadigmException {

        whenRefactoredMethodsHasBeenCreatedWithOneMethod();

        andScalabrinoCalculatorPointingToOriginalSourceFileHasBeenCreated();

        calculator.calculate();

        CalculatedFeature cycFeature = sendToTcpMethod.findCalculatedFeature(new ScalabrinoReadability());
        Number actual = cycFeature.getValueForOriginal();

        Double expectedReadability = 0.7868043184280396;

        assertEquals(expectedReadability, actual);
    }

    @Test()
    public void CalculatingComplexityOfClassWithOneRefactoredMethod() throws IOException, FeatureNotSetException, FeatureAlreadySetException, MethodNotRefactoredException, UnknownParadigmException {

        whenRefactoredMethodsHasBeenCreatedWithOneMethod();

        andScalabrinoCalculatorPointingToRefactoredSourceFileHasBeenCreated();

        calculator.calculate();

        CalculatedFeature cycFeature = sendToTcpMethod.findCalculatedFeature(new ScalabrinoReadability());
        Number actual = cycFeature.getValueForRefactored();

        Double expectedReadability = 0.5713483691215515;

        assertEquals(expectedReadability, actual);
    }

    private void andScalabrinoCalculatorPointingToOriginalSourceFileHasBeenCreated() {
        this.calculator = new ScalabrinoCalculator(pathToTestDataDirectory + "source_code_orig", methods);
    }

    private void andScalabrinoCalculatorPointingToRefactoredSourceFileHasBeenCreated() {
        this.calculator = new ScalabrinoCalculator(pathToTestDataDirectory + "source_code_rx", methods);
    }
}
