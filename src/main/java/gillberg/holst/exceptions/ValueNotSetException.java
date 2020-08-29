package gillberg.holst.exceptions;

public class ValueNotSetException extends Throwable {

    public ValueNotSetException() {

    }

    public ValueNotSetException(String valueName) {
        super(valueName);
    }
}
