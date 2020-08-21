package gillberg.holst.enums;

public class ValueNotSetException extends Throwable {

    public ValueNotSetException() {

    }

    public ValueNotSetException(String valueName) {
        super(valueName);
    }
}
