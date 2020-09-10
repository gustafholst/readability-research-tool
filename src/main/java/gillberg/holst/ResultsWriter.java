package gillberg.holst;

import gillberg.holst.exceptions.FeatureNotSetException;
import gillberg.holst.exceptions.FilenameNotSetException;

public interface ResultsWriter {

    public void addRow(Method method) throws FeatureNotSetException;
    public void setFileName(String fileName);
    public void writeToFile() throws FilenameNotSetException;
}
