package gillberg.holst;

import it.unimol.readability.metric.API.UnifiedMetricClassifier;
import it.unimol.readability.metric.API.WekaException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import it.unimol.readability.metric.runnable.SerializedReadability;

public class MySerializedReadability extends SerializedReadability {

    private final UnifiedMetricClassifier classifier;

    public MySerializedReadability() {
        File classifierFile = new File("src\\main\\resources\\readability.classifier");
        if (!classifierFile.exists()) {
            System.err.println("Classifier file not existing: " + classifierFile.getPath());
            System.exit(-1);
        }
        classifier = UnifiedMetricClassifier.loadClassifier(classifierFile);
    }


    public static void main(String[] args) {

        // Code for creating new classifier /////////////////////////////

        MyUnifiedMetricClassifier classifier = new MyUnifiedMetricClassifier();

        try {
            classifier.setTrainingSet("D:\\Miun\\AAExjobb\\readability\\DATASET_NEW_EXTENSION.arff");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WekaException e) {
            e.printStackTrace();
        }

        classifier.saveClassifier(new File("NewClassifier.classifier"));

        //////////////////////////////////



        //File file = new File(args[0]);
        File[] source_folders = ReadabilityFeaturesCalculator.getSourceCodeDirectories();
        File rx_folder = Arrays.stream(source_folders).filter(dir -> dir.getName().endsWith("rx")).findFirst().get();


        File file = new File(rx_folder.getPath() + File.separator + "Client.java");
        System.out.println(file.getPath());

        if (!file.exists()) {
            System.err.println("File not existing.");
            System.exit(-1);
        }
        try {
            //double result = classifier.classifyClass(file);
            Map<String, Double> resultMap = classifier.getReadabilityMap(file);
//            if ((Double.valueOf(result)).isNaN()) {
//                result = classifier.classify(FileUtils.readFileToString(file.getAbsoluteFile(), "UTF-8"));
//                System.out.println("Snippet readability:" + result);
//            } else {
//                System.out.println("Class mean readability:" + result);
//
//            }

            System.out.println("Class mean readability:" + resultMap );
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    public Map<String, Double> getReadabilityMap(File fileName) throws IOException {

        //org.eclipse.core.runtime.SubMonitor temp = org.eclipse.core.runtime.SubMonitor.split(int);
        return classifier.classifyClassMethods(fileName);
    }
}
