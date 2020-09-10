package gillberg.holst;

import gillberg.holst.calculators.BuseCalculator;
import gillberg.holst.calculators.ComplexityCalculator;
import gillberg.holst.enums.Paradigm;
import gillberg.holst.exceptions.FeatureAlreadySetException;
import gillberg.holst.exceptions.FeatureNotSetException;
import gillberg.holst.exceptions.MethodNotRefactoredException;
import gillberg.holst.exceptions.UnknownParadigmException;
import gillberg.holst.features.BuseReadability;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class BuseCalculatorTest extends BaseTest{

    @Test()
    public void CalculatingComplexityOfClassWithOneOriginalMethod() throws IOException, FeatureNotSetException, FeatureAlreadySetException, MethodNotRefactoredException, UnknownParadigmException {

        whenRefactoredMethodsHasBeenCreatedWithOneMethod();

        andBuseCalculatorPointingToOriginalSourceFileHasBeenCreated();

        calculator.calculate();

        CalculatedFeature cycFeature = sendToTcpMethod.findCalculatedFeature(new BuseReadability());
        Number actual = cycFeature.getValueForOriginal();

        Double expectedReadability = 0.0288684144616127;

        assertEquals(expectedReadability, actual);
    }

    @Test()
    public void CalculatingComplexityOfClassWithOneRefactoredMethod() throws IOException, FeatureNotSetException, FeatureAlreadySetException, MethodNotRefactoredException, UnknownParadigmException {

        whenRefactoredMethodsHasBeenCreatedWithOneMethod();

        andBuseCalculatorPointingToRefactoredSourceFileHasBeenCreated();

        calculator.calculate();

        CalculatedFeature cycFeature = sendToTcpMethod.findCalculatedFeature(new BuseReadability());
        Number actual = cycFeature.getValueForRefactored();

        Double expectedReadability = 5.414261249825358E-4;

        assertEquals(expectedReadability, actual);
    }

    private void andBuseCalculatorPointingToOriginalSourceFileHasBeenCreated() {
        this.calculator = new BuseCalculator(this.currentContext, Paradigm.imperative);
    }

    private void andBuseCalculatorPointingToRefactoredSourceFileHasBeenCreated() {
        this.calculator = new BuseCalculator(this.currentContext, Paradigm.reactive);
    }

}
