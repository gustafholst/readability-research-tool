package gillberg.holst;

//import com.ipeirotis.readability.engine.Readability;
//import com.ipeirotis.readability.enums.MetricType;

import gillberg.holst.exceptions.*;
import gillberg.holst.features.*;

import java.io.*;
import java.util.*;

public class ReadabilityFeaturesCalculator {

	public static String getDataPath() {
		String sep = File.separator;
		String root = System.getProperty("user.dir");
		return root + sep + "src" + sep + "main" + sep + "data" + sep;
	}

	public static File[] getDirectoriesByCategory(String category) {
		return new File(getDataPath()).listFiles((f -> f.isDirectory() && f.getName().startsWith(category)));
	}

	public static File[] getSourceCodeDirectories() {
		return getDirectoriesByCategory("source_code");
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {

		Context context = new BasicContext();

		String resultsFileName = "DATASET_HOLST";
		context.setDirectoryForOriginalCode("D:\\OpenSourceProjects\\ReadabilityFeaturesCalculator\\src\\main\\data\\source_code_orig");
		context.setDirectoryForRefactoredCode("D:\\OpenSourceProjects\\ReadabilityFeaturesCalculator\\src\\main\\data\\source_code_rx");
		String methodsPath = "D:\\OpenSourceProjects\\ReadabilityFeaturesCalculator\\src\\main\\data\\refactored_methods.txt";

//		String resultsFileName = "EXAMPLES_RESULT";
//		context.setDirectoryForOriginalCode("D:\\OpenSourceProjects\\Reactive refactor excerpts\\src\\main\\java\\imperative");
//		context.setDirectoryForRefactoredCode("D:\\OpenSourceProjects\\Reactive refactor excerpts\\src\\main\\java\\reactive");
//		String methodsPath = "D:\\OpenSourceProjects\\ReadabilityFeaturesCalculator\\src\\main\\data\\example_methods.txt";

		RefactoredMethods refactoredMethods = new RefactoredMethods(methodsPath);
		context.setRefactoredMethods(refactoredMethods);

		CalculatedFeatures.getInstance().addFeature(new LinesOfCode());
		CalculatedFeatures.getInstance().addFeature(new BuseReadability());
		CalculatedFeatures.getInstance().addFeature(new ScalabrinoReadability());
		CalculatedFeatures.getInstance().addFeature(new CyclomaticComplexity());
		//CalculatedFeatures.getInstance().addFeature(new ScalabrinoFeatures());
		CalculatedFeatures.getInstance().addFeature(new CyclomaticComplexityFeatures());


		List<Calculator> calculators = new ArrayList<>();

		for (CalculatedFeature cf : CalculatedFeatures.getInstance().getFeatures()) {
			calculators.addAll(List.of(cf.getCalculators(context)));
		}

		for (Calculator c : calculators) {
			try {
				System.out.println("Calculating " + c.getName() + "...");
				c.calculate();
			} catch (FeatureAlreadySetException | MethodNotRefactoredException | UnknownParadigmException e) {
				System.out.println(e.getMessage());
				//e.printStackTrace();
			}
		}

		System.out.println("Preparing writer...");

		//ResultsWriter writer = new JSONResultsWriter();
		ResultsWriter writer = new CSVResultsWriter(';');
		writer.setSeparateFiles();

		writer.setFileName(resultsFileName);
		for (Method m : context.getMethods()) {
			try {
				writer.addRow(m);
			} catch (FeatureNotSetException e) {
				System.err.println(e.getMessage());
			}
		}

		System.out.println("Writing to file...");

		try {
			writer.writeToFile();
		} catch (FilenameNotSetException e) {
			e.printStackTrace();
		}
	}
}


