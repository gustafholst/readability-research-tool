package gillberg.holst;

import gillberg.holst.exceptions.FeatureAlreadySetException;
import gillberg.holst.exceptions.FeatureNotSetException;

public abstract class CalculatedFeature {
    private Number original;
    private Number refactored;

    public Number getValueForOriginal() throws FeatureNotSetException {
        if (original == null) {
            throw new FeatureNotSetException("Value for [original] [" + getName() + "] is missing");
        }
        return original;
    }

    public Number getValueForRefactored() throws FeatureNotSetException {
        if (refactored == null) {
            throw new FeatureNotSetException("Value for [refactored] [" + getName() + "] is missing");
        }
        return refactored;
    }

    public void setValueForOriginal(Number value) throws FeatureAlreadySetException {
        if (original != null) {
            throw new FeatureAlreadySetException("Value for [original] [" + getName() + "] is already set");
        }
        this.original = value;
    }

    public void setValueForRefactored(Number value) throws FeatureAlreadySetException {
        if (refactored != null) {
            throw new FeatureAlreadySetException("Value for [refactored] [" + getName() + "] is already set");
        }
        this.refactored = value;
    }

    public abstract String getName();

    @Override
    public boolean equals(Object o) {
        if (o instanceof CalculatedFeature) {
            return this.getName().equals(((CalculatedFeature) o).getName());
        }
        return false;
    }
}
