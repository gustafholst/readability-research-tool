package gillberg.holst;

import com.github.javaparser.ast.body.MethodDeclaration;
import gillberg.holst.exceptions.MethodNotRefactoredException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BasicContext implements Context {

    private String origDir;
    private String refactoredDir;
    private RefactoredMethods refactoredMethods;

    @Override
    public void setDirectoryForOriginalCode(String dir) {
        this.origDir = dir;
    }

    @Override
    public void setDirectoryForRefactoredCode(String dir) {
        this.refactoredDir = dir;
    }

    @Override
    public void setRefactoredMethods(RefactoredMethods methods) {
        this.refactoredMethods = methods;
    }

    @Override
    public String getDirectoryForOriginalCode() {
        return this.origDir;
    }

    @Override
    public String getDirectoryForRefactoredCode() {
        return this.refactoredDir;
    }

    public Method getMethod(String className, MethodDeclaration methodDeclaration) throws MethodNotRefactoredException, IOException {
        Method temp = new Method(className, methodDeclaration);
        return getMethod(className, temp.signature);
    }

    public Method getMethod(String className, String signature) throws MethodNotRefactoredException, IOException {
        return refactoredMethods.getMethod(className, signature);
    }

    @Override
    public List<Method> getMethods() throws IOException {
        return refactoredMethods.getRefactoredMethods();
    }

    @Override
    public boolean shouldCalculate(String className, String signature) throws IOException {
        return refactoredMethods.shouldCalculate(new Method(className, signature));
    }

}
