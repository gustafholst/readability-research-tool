package gillberg.holst;

import gillberg.holst.exceptions.FeatureNotSetException;
import gillberg.holst.exceptions.FilenameNotSetException;
import gillberg.holst.features.ScalabrinoFeatures;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVResultsWriter implements ResultsWriter{

    private String fileName = "";
    private String headerLine = null;
    private List<String> rows = new ArrayList<>();

    private char columnSeparator = ',';

    private static final String suffix = ".csv";

    private static final String origPrefix = "oo_";
    private static final String refactoredPrefix = "rx_";

    public CSVResultsWriter() {

    }

    public CSVResultsWriter(char separator) {
        columnSeparator = separator;
    }

    @Override
    public void addRow(Method method) throws FeatureNotSetException {

        if (headerLine == null) {
            addColumns();
        }

        StringBuilder newRow = new StringBuilder();

        newRow.append(method.className);
        newRow.append(columnSeparator);
        newRow.append(method.signature);
        newRow.append(columnSeparator);

        for (CalculatedFeature f: CalculatedFeatures.getInstance().getFeatures()) {
            if (f instanceof ScalabrinoFeatures) continue;

            CalculatedFeature temp = method.findCalculatedFeature(f);

            newRow.append(temp.getValueForOriginal());
            newRow.append(columnSeparator);
            newRow.append(temp.getValueForRefactored());
            newRow.append(columnSeparator);
        }

        newRow.deleteCharAt(newRow.length() - 1);

        rows.add(newRow.toString());
    }

    private void addColumns() {
        StringBuilder header = new StringBuilder();

        header.append("class_name");
        header.append(columnSeparator);
        header.append("signature");
        header.append(columnSeparator);

        for (CalculatedFeature f: CalculatedFeatures.getInstance().getFeatures()) {
            String featureName = f.getName();

            header.append(origPrefix);
            header.append(featureName);
            header.append(columnSeparator);
            header.append(refactoredPrefix);
            header.append(featureName);
            header.append(columnSeparator);
        }

        header.deleteCharAt(header.length() - 1);

        headerLine = header.toString();
    }

    @Override
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void writeToFile() throws FilenameNotSetException {
        if (fileName == null) {
            throw new FilenameNotSetException();
        }

        if (!fileName.endsWith(suffix)) {
            fileName = fileName + suffix;
        }

        File csvFile = new File(fileName);

        try {
            FileUtils.write(csvFile, headerLine + System.lineSeparator(), "utf-8", false);

            for (String row : rows) {
                FileUtils.write(csvFile, row + System.lineSeparator(), "utf-8", true);
            }

        } catch (IOException e) {
            System.out.println("Could not write CSV file. Reason: " + e.getMessage());
        }
    }
}
