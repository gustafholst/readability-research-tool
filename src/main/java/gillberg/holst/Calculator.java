package gillberg.holst;

import gillberg.holst.exceptions.FeatureAlreadySetException;
import gillberg.holst.exceptions.MethodNotRefactoredException;
import gillberg.holst.exceptions.UnknownParadigmException;

import java.io.IOException;

public interface Calculator {

    void calculate() throws IOException, FeatureAlreadySetException, MethodNotRefactoredException, UnknownParadigmException;
    String getName();
}
