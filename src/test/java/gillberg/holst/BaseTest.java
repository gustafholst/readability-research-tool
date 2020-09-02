package gillberg.holst;

import gillberg.holst.calculators.ComplexityCalculator;

import java.io.IOException;
import java.util.List;

public class BaseTest {
    protected String oneMethodPath = "D:\\OpenSourceProjects\\ReadabilityFeaturesCalculator\\src\\test\\test_data\\one_method.txt";
    private RefactoredMethods refactoredMethods;
    protected List<Method> methods;

    protected Method sendToTcpMethod;

    protected String pathToTestDataDirectory = "D:\\OpenSourceProjects\\ReadabilityFeaturesCalculator\\src\\test\\test_data\\";

    protected Calculator calculator;

    protected void whenRefactoredMethodsHasBeenCreatedWithOneMethod() throws IOException {
        this.refactoredMethods = new RefactoredMethods(oneMethodPath);
        this.methods = this.refactoredMethods.getRefactoredMethods();

        String className = "TestClass";
        String signature = "sendToTCP(int,Object)";

        Method searchedMethod = new Method(className, signature);
        sendToTcpMethod = methods.get(methods.indexOf(searchedMethod));
    }

}
