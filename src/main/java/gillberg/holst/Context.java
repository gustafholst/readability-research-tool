package gillberg.holst;

import com.github.javaparser.ast.body.MethodDeclaration;
import gillberg.holst.exceptions.MethodNotRefactoredException;

import java.io.IOException;
import java.util.List;

public interface Context {

    void setDirectoryForOriginalCode(String dir);
    void setDirectoryForRefactoredCode(String dir);
    void setRefactoredMethods(RefactoredMethods methods);
    String getDirectoryForOriginalCode();
    String getDirectoryForRefactoredCode();
    Method getMethod(String className, MethodDeclaration methodDeclaration) throws MethodNotRefactoredException, IOException;
    Method getMethod(String className, String signature) throws MethodNotRefactoredException, IOException;
    List<Method> getMethods() throws IOException;
    boolean shouldCalculate(String className, String signature) throws IOException;
}
