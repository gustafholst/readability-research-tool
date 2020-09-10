package gillberg.holst;

import gillberg.holst.calculators.ComplexityCalculator;

import java.io.IOException;
import java.util.List;

public class BaseTest {
    protected String oneMethodPath = "D:\\OpenSourceProjects\\ReadabilityFeaturesCalculator\\src\\test\\test_data\\one_method.txt";
    protected List<Method> methods;

    protected Context currentContext;

    protected Method sendToTcpMethod;

    protected String pathToTestDataDirectory = "D:\\OpenSourceProjects\\ReadabilityFeaturesCalculator\\src\\test\\test_data\\";

    protected Calculator calculator;

    protected void whenRefactoredMethodsHasBeenCreatedWithOneMethod() throws IOException {
        Context context = new BasicContext();
        context.setDirectoryForOriginalCode(pathToTestDataDirectory + "source_code_orig");
        context.setDirectoryForRefactoredCode(pathToTestDataDirectory + "source_code_rx");
        context.setRefactoredMethods(new RefactoredMethods(oneMethodPath));

        this.currentContext = context;
        this.methods = context.getMethods();

        String className = "TestClass";
        String signature = "sendToTCP(int,Object)";

        Method searchedMethod = new Method(className, signature);
        sendToTcpMethod = methods.get(methods.indexOf(searchedMethod));
    }

}
