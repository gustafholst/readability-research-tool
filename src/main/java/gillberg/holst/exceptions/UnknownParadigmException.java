package gillberg.holst.exceptions;

public class UnknownParadigmException extends Throwable{

    public UnknownParadigmException(String paradigmName) {
        super("Paradigm with by name " + paradigmName + " is not known");
    }
}
