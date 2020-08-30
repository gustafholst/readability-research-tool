package gillberg.holst.calculators;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import gillberg.holst.Calculator;
import gillberg.holst.Method;
import gillberg.holst.enums.Paradigm;
import gillberg.holst.exceptions.MethodNotRefactoredException;
import gillberg.holst.exceptions.UnknownParadigmException;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;

public abstract class AbstractCalculator {

    protected final String directory;
    protected final List<Method> methodList;

    protected AbstractCalculator(String dir, List<Method> methods) {
        this.directory = dir;
        this.methodList = methods;
    }

    protected static CompilationUnit readFile(File file) throws IOException {

        JavaParser parser = new JavaParser();

        parser.getParserConfiguration().setAttributeComments(true);
        parser.getParserConfiguration().setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_13);
        parser.getParserConfiguration().setCharacterEncoding(Charset.defaultCharset());
        parser.getParserConfiguration().setDoNotAssignCommentsPrecedingEmptyLines(false);

        InputStream in = null;

        try
        {
            in = new FileInputStream(file);
            ParseResult<CompilationUnit> result = parser.parse(in);
            Optional<CompilationUnit> cu = result.getResult();

            if (cu.isPresent()) {
                return cu.get();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally
        {
            assert in != null;
            in.close();
        }

        throw new IOException("No parse result");
    }

    protected static File[] getJavaFilesFromDir(String pathName) {
        return new File(pathName).listFiles(f -> f.getName().endsWith(".java"));
    }

    protected Paradigm getParadigm() throws UnknownParadigmException {
        if (directory.endsWith("_orig")) {
            return Paradigm.imperative;
        }
        else if (directory.endsWith("_rx")) {
            return Paradigm.reactive;
        }
        throw new UnknownParadigmException("Cannot determine paradigm from directory name \"" + directory + "\"");
    }

    protected Method getMethod(String className, MethodDeclaration methodDeclaration) throws MethodNotRefactoredException {
        Method temp = new Method(className, methodDeclaration);
        return getMethod(className, temp.signature);
    }

    protected Method getMethod(String className, String signature) throws MethodNotRefactoredException {
        Method temp = new Method(className, signature);

        Optional<Method> foundMethod = this.methodList.stream().filter(m -> m.equals(temp)).findFirst();
        if (foundMethod.isPresent()) {
            return foundMethod.get();
        }

        throw new MethodNotRefactoredException("No method [" + className + " " + signature + "] in memory");
    }
}
