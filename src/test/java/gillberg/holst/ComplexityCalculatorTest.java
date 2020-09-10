package gillberg.holst;

import gillberg.holst.calculators.ComplexityCalculator;
import gillberg.holst.enums.Paradigm;
import gillberg.holst.exceptions.FeatureAlreadySetException;
import gillberg.holst.exceptions.FeatureNotSetException;
import gillberg.holst.exceptions.MethodNotRefactoredException;
import gillberg.holst.exceptions.UnknownParadigmException;
import gillberg.holst.features.CyclomaticComplexity;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class ComplexityCalculatorTest extends BaseTest {

    @Test
    public void TryingToGetFeatureThatIsNotYetSetShouldThrow() throws IOException {

        whenRefactoredMethodsHasBeenCreatedWithOneMethod();

        andComplexityCalculatorPointingToOriginalSourceFileHasBeenCreated();

        // No calculation is done

        CalculatedFeature cycFeature = sendToTcpMethod.findCalculatedFeature(new CyclomaticComplexity());

        assertThrows(FeatureNotSetException.class, cycFeature::getValueForOriginal);
    }

    @Test()
    public void CalculatingComplexityOfClassWithOneOriginalMethod() throws IOException, FeatureNotSetException, FeatureAlreadySetException, MethodNotRefactoredException, UnknownParadigmException {

        whenRefactoredMethodsHasBeenCreatedWithOneMethod();

        andComplexityCalculatorPointingToOriginalSourceFileHasBeenCreated();

        calculator.calculate();

        CalculatedFeature cycFeature = sendToTcpMethod.findCalculatedFeature(new CyclomaticComplexity());
        Number actual = cycFeature.getValueForOriginal();

        Number expectedOriginalCyc = 3;

        assertEquals(expectedOriginalCyc, actual);
    }

    @Test()
    public void CalculatingComplexityOfClassWithOneRefactoredMethod() throws IOException, FeatureNotSetException, FeatureAlreadySetException, MethodNotRefactoredException, UnknownParadigmException {

        whenRefactoredMethodsHasBeenCreatedWithOneMethod();

        andComplexityCalculatorPointingToRefactoredSourceFileHasBeenCreated();

        calculator.calculate();

        CalculatedFeature cycFeature = sendToTcpMethod.findCalculatedFeature(new CyclomaticComplexity());
        Number actual = cycFeature.getValueForRefactored();

        Number expectedRefactoredCyc = 1;

        assertEquals(expectedRefactoredCyc, actual);
    }

    @Test
    public void TryingToSetFeatureThatIsAlreadySetShouldThrow() throws IOException, FeatureAlreadySetException, MethodNotRefactoredException, UnknownParadigmException {

        whenRefactoredMethodsHasBeenCreatedWithOneMethod();

        andComplexityCalculatorPointingToOriginalSourceFileHasBeenCreated();

        //calculating once
        calculator.calculate();

        //...and once again
        assertThrows(FeatureAlreadySetException.class, calculator::calculate);
    }

    private void andComplexityCalculatorPointingToOriginalSourceFileHasBeenCreated() {
        this.calculator = new ComplexityCalculator(this.currentContext, Paradigm.imperative);
    }

    private void andComplexityCalculatorPointingToRefactoredSourceFileHasBeenCreated() {
        this.calculator = new ComplexityCalculator(this.currentContext, Paradigm.reactive);
    }
}
