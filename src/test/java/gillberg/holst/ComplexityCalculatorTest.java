package gillberg.holst;

import gillberg.holst.calculators.ComplexityCalculator;
import gillberg.holst.exceptions.FeatureNotSetException;
import gillberg.holst.features.CyclomaticComplexity;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class ComplexityCalculatorTest {

    @Test()
    public void CalculatingComplexityOfClassWithOneOriginalMethod() {

        String path = "D:\\OpenSourceProjects\\ReadabilityFeaturesCalculator\\src\\test\\test_data\\one_method.txt";

        RefactoredMethods refactoredMethods = new RefactoredMethods(path);
        List<Method> methods = refactoredMethods.getRefactoredMethods();

        String basePath = "D:\\OpenSourceProjects\\ReadabilityFeaturesCalculator\\src\\test\\test_data\\";

        Calculator calculator = new ComplexityCalculator(basePath + "source_code_orig", methods);

        String className = "TestClass";
        String signature = "sendToTCP(int,Object)";

        Method searchedMethod = new Method(className, signature);

        try {
            calculator.calculate();

            Method method = methods.get(methods.indexOf(searchedMethod));
            CalculatedFeature cycFeature = method.findCalculatedFeature(new CyclomaticComplexity());
            Number actual = cycFeature.getValueForOriginal();

            Number expectedOriginalCyc = 3;

            assertEquals(expectedOriginalCyc, actual);

        } catch (IOException | FeatureNotSetException e) {
            e.printStackTrace();
        }
    }
}
