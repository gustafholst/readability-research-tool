package gillberg.holst.exceptions;

public class FeatureAlreadySetException extends Throwable {

    public FeatureAlreadySetException(String reason) {
        super(reason);
    }
}
