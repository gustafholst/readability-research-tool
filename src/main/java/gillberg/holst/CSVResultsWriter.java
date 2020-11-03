package gillberg.holst;

import gillberg.holst.exceptions.FeatureNotSetException;
import gillberg.holst.exceptions.FilenameNotSetException;
import gillberg.holst.features.CyclomaticComplexityFeatures;
import gillberg.holst.features.ScalabrinoFeatures;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVResultsWriter implements ResultsWriter{

    private String fileName = "";
    private String headerLine = null;

    private final List<String> rows = new ArrayList<>();

    private final List<String> origRows = new ArrayList<>();
    private final List<String> refactoredRows = new ArrayList<>();

    private char columnSeparator = ',';

    private boolean separateFiles = false;

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
        if (separateFiles) {
            buildRowsForSeparateFiles(method);
        }
        else {
            buildRow(method);
        }
    }

    private void buildColumns() {
        StringBuilder header = new StringBuilder();

        header.append("class_name");
        header.append(columnSeparator);
        header.append("signature");
        header.append(columnSeparator);

        for (CalculatedFeature f: CalculatedFeatures.getInstance().getFeatures()) {
            if (f.shouldBePresentedAsAColumn()) {
                String featureName = f.getName();

                header.append(origPrefix);
                header.append(featureName);
                header.append(columnSeparator);
                header.append(refactoredPrefix);
                header.append(featureName);
                header.append(columnSeparator);
            }
        }

        header.deleteCharAt(header.length() - 1);

        headerLine = header.toString();
    }

    private void buildColumnsForSeparateFiles() {
        StringBuilder header = new StringBuilder();

        header.append("class_name");
        header.append(columnSeparator);
        header.append("signature");
        header.append(columnSeparator);

        for (CalculatedFeature f: CalculatedFeatures.getInstance().getFeatures()) {
            if (f.shouldBePresentedAsAColumn()) {
                String featureName = f.getName();

                header.append(featureName);
                header.append(columnSeparator);
            }
        }

        header.deleteCharAt(header.length() - 1);

        headerLine = header.toString();
    }

    private void buildRow(Method method) throws FeatureNotSetException {
        StringBuilder newRow = new StringBuilder();

        newRow.append(method.className);
        newRow.append(columnSeparator);
        newRow.append(method.signature);
        newRow.append(columnSeparator);

        for (CalculatedFeature f: CalculatedFeatures.getInstance().getFeatures()) {
            if (f.shouldBePresentedAsAColumn()) {
                CalculatedFeature temp = method.findCalculatedFeature(f);

                newRow.append(temp.getValueForOriginal());
                newRow.append(columnSeparator);
                newRow.append(temp.getValueForRefactored());
                newRow.append(columnSeparator);
            }
        }

        newRow.deleteCharAt(newRow.length() - 1);

        rows.add(newRow.toString());
    }

    private void buildRowsForSeparateFiles(Method method) throws FeatureNotSetException {
        StringBuilder origRow = new StringBuilder();
        StringBuilder refactoredRow = new StringBuilder();

        origRow.append(method.className);
        origRow.append(columnSeparator);
        origRow.append(method.signature);
        origRow.append(columnSeparator);

        refactoredRow.append(method.className);
        refactoredRow.append(columnSeparator);
        refactoredRow.append(method.signature);
        refactoredRow.append(columnSeparator);

        for (CalculatedFeature f: CalculatedFeatures.getInstance().getFeatures()) {
            if (f.shouldBePresentedAsAColumn()) {
                CalculatedFeature temp = method.findCalculatedFeature(f);

                origRow.append(temp.getValueForOriginal());
                origRow.append(columnSeparator);

                refactoredRow.append(temp.getValueForRefactored());
                refactoredRow.append(columnSeparator);
            }
        }

        origRow.deleteCharAt(origRow.length() - 1);
        refactoredRow.deleteCharAt(refactoredRow.length() - 1);

        origRows.add(origRow.toString());
        refactoredRows.add(refactoredRow.toString());
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

        if (separateFiles) {
            writeSeparateFiles();
        }
        else {
            writeOneFile();
        }
    }

    private void writeOneFile() {
        if (!fileName.endsWith(suffix)) {
            fileName = fileName + suffix;
        }

        buildColumns();

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

    private void writeSeparateFiles() {
        if (fileName.endsWith(suffix)) {
            fileName = fileName.substring(0, fileName.indexOf(suffix));
        }

        buildColumnsForSeparateFiles();

        File csvFile_original = new File(fileName + "_ORIGINAL" + suffix);
        File csvFile_refactored = new File(fileName + "_REACTIVE" + suffix);

        try {
            //write original file
            FileUtils.write(csvFile_original, headerLine + System.lineSeparator(), "utf-8", false);

            for (String row : origRows) {
                FileUtils.write(csvFile_original, row + System.lineSeparator(), "utf-8", true);
            }

            //write refactored file
            FileUtils.write(csvFile_refactored, headerLine + System.lineSeparator(), "utf-8", false);

            for (String row : refactoredRows) {
                FileUtils.write(csvFile_refactored, row + System.lineSeparator(), "utf-8", true);
            }

        } catch (IOException e) {
            System.out.println("Could not write CSV file. Reason: " + e.getMessage());
        }
    }

    @Override
    public void setSeparateFiles() {
        separateFiles = true;
    }
}
