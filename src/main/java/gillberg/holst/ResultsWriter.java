package gillberg.holst;

import gillberg.holst.exceptions.FeatureNotSetException;
import gillberg.holst.exceptions.FilenameNotSetException;

public interface ResultsWriter {

    void addRow(Method method) throws FeatureNotSetException;
    void setFileName(String fileName);
    void writeToFile() throws FilenameNotSetException;
    void setSeparateFiles();
}
