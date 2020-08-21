package gillberg.holst;

import gillberg.holst.exceptions.FilenameNotSetException;

public interface ResultsWriter {

    public void addRow(Method method);
    public void setFileName(String fileName);
    public void writeToFile() throws FilenameNotSetException;
}
