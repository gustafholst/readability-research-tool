package gillberg.holst;

import gillberg.holst.enums.Feature;
import gillberg.holst.enums.Paradigm;
import gillberg.holst.exceptions.ValueNotSetException;
import gillberg.holst.exceptions.FeatureNotSetException;
import gillberg.holst.exceptions.FilenameNotSetException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;

public class JSONResultsWriter implements ResultsWriter {
    private JSONObject outRoot = new JSONObject();
    private JSONArray array = new JSONArray();

    private String fileName;
    private static String suffix = ".json";

    public JSONResultsWriter() {

    }

    private void addValues(JSONObject jo, String featureName, Number[] values) {
        jo.put("oo_" + featureName, values[Paradigm.imperative.ordinal()].toString());
        jo.put("rx_" + featureName, values[Paradigm.reactive.ordinal()].toString());
    }

    @Override
    public void addRow(Method m) {
        JSONObject jo = new JSONObject();

        jo.put("className", m.className);
        jo.put("signature", m.signature);

        try {
            Number[] buse = m.getBuseReadability();
            Number[] scalabrino = m.getScalabrinoReadability();
            Number[] cyc = m.getCyclomaticComplexity();

            addValues(jo, "buse_readability", buse);
            addValues(jo, "scalabrino_readability", scalabrino);
            addValues(jo, "cyclomatic_complexity", cyc);
        } catch (ValueNotSetException vnse) {
            System.out.println(vnse.getMessage() + " for method " + m);
        }

        // TODO remove FeatureNotSetException
        for (Feature feature : Feature.values()) {
            try {
                Number[] values = m.getValuesForFeature(feature);
                addValues(jo, feature.toString(), values);
            } catch (FeatureNotSetException | ValueNotSetException e) {
                System.out.println("Could not add a value for feature " + feature.toString());
                // TODO add some character to json file for this feature (f.ex. '-' or '?')
            }
        }

        array.add(jo);
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

        outRoot.put("methods", array);

        if (!fileName.endsWith(suffix)) {
            fileName = fileName + suffix;
        }

        try {
            FileWriter fileWriter = new FileWriter(fileName);
            outRoot.writeJSONString(fileWriter);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("Could not write JSON. Reason: " + e.getMessage());
        }
    }
}
