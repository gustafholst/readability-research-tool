package gillberg.holst;

import com.github.javaparser.*;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithIdentifier;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.WhileStmt;

//import com.ipeirotis.readability.engine.Readability;
//import com.ipeirotis.readability.enums.MetricType;

import gillberg.holst.calculators.ComplexityCalculator;
import gillberg.holst.enums.Feature;
import gillberg.holst.enums.NodeType;
import gillberg.holst.enums.Paradigm;
import gillberg.holst.exceptions.*;
import gillberg.holst.features.BuseReadability;
import gillberg.holst.features.CyclomaticComplexity;
import gillberg.holst.features.ScalabrinoFeatures;
import gillberg.holst.features.ScalabrinoReadability;
import it.unimol.readability.metric.FeatureCalculator;
import it.unimol.readability.metric.output.CSVWriter;
import it.unimol.readability.metric.output.OutputException;
import it.unimol.readability.metric.output.OutputWriter;
import it.unimol.readability.metric.output.Record;
import it.unimol.readability.metric.runnable.SerializedReadability;


import it.unimol.readability.metric.API.MetricClassifier;
import selector.runnable.MethodsExtractor;

import static raykernel.apps.readability.eval.Main.getReadability;

import java.io.*;
import java.util.*;

public class ReadabilityFeaturesCalculator {



//	public static Optional<Method> findMethod(Method method) {
//		return methodList.stream()
//				.filter(m -> m.equals(method))
//				.findFirst();
//	}
//
//	public static Method findOrCreateMethod(String className, MethodDeclaration method) {
//		Method temp = new Method(className, method);
//		return findMethod(temp).orElse(temp);
//	}
//
//	public static Method findOrCreateMethod(String className, String name) {
//		Method temp = new Method(className, name);
//		return findMethod(temp).orElse(temp);
//	}

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



	public static int countOccurrences(String str, char searched) {
		return ((int) str.chars().filter(ch -> ch == searched).count());
	}

//	public static void calculateComplexities() throws IOException, ParseException {
//
//		try {
//
//			ComplexityCalculator origCalculator = new ComplexityCalculator("source_code_orig", methodList);
//			origCalculator.calculate();
//
//			ComplexityCalculator rxCalculator = new ComplexityCalculator("source_code_rx", methodList);
//			rxCalculator.calculate();
//
//		} catch (FeatureAlreadySetException e) {
//			e.printStackTrace();
//		} catch (MethodNotRefactoredException e) {
//			e.printStackTrace();
//		} catch (UnknownParadigmException e) {
//			e.printStackTrace();
//		}
//
//	}

//	public static void parseSourceCodeFileAndStoreResults(String filePath, Paradigm paradigm) {
//
//		try {
//			CompilationUnit cu = readFile(filePath);
//			cu.getTypes()
//					.forEach(t -> t.getMethods()   // for each class
//							.forEach(m -> {        // for each method
//								Method method = findOrCreateMethod(t.getNameAsString(), m);
//								Position begin = m.getBegin().get();
//								Position end = m.getEnd().get();
//
//								int numLines = end.line - begin.line + 1;  //add one to count closing bracket as one line
//
//								String methodString = m.getTokenRange().get().toString();
//
//								String[] lines = methodString.split(System.lineSeparator());
//
//								OptionalInt max_line_length = Arrays.stream(lines)
//										.map(String::trim)
//										.mapToInt(String::length)
//										.max();
//
//								OptionalDouble alt_avg_line_length = Arrays.stream(lines)
//										.map(String::trim)
//										.mapToInt(String::length)
//										.average();
//
//								final int[] nodeCounts = new int[NodeType.values().length];
//								m.walk(node -> {
//									if (node instanceof NodeWithIdentifier) {  //this
//										nodeCounts[NodeType.identifier.ordinal()]++;
//										//System.out.println(node.toString());
//									}
//									if (node instanceof IfStmt) {
//										nodeCounts[NodeType.ifstatement.ordinal()]++;
//									}
//									if (node instanceof ForStmt || node instanceof WhileStmt) {
//										nodeCounts[NodeType.loop.ordinal()]++;
//									}
//									if (node instanceof CatchClause) {
//										nodeCounts[NodeType.catchclause.ordinal()]++;
//									}
//									if (node instanceof BinaryExpr) {
//										BinaryExpr be = (BinaryExpr)node;
//										BinaryExpr.Operator op = be.getOperator();
//										if (op == BinaryExpr.Operator.AND
//												|| op == BinaryExpr.Operator.OR
//												|| op == BinaryExpr.Operator.BINARY_AND
//												|| op == BinaryExpr.Operator.BINARY_OR) {
//											nodeCounts[NodeType.and_or.ordinal()]++;
//										}
//									}
//								});
//
////								Readability ipReadability = new Readability(methodString);
////								double flesch_kincaid_reading_ease = ipReadability.getMetric(MetricType.FLESCH_KINCAID);
////								double flesch_kincaid_grade_level = ipReadability.getMetric(MetricType.FLESCH_READING);
//
//								try {
//									if (paradigm == Paradigm.reactive) {
//										method.setRp_readability(getReadability(m.toString()));
//
//										method.rp_num_stops = countOccurrences(m.toString(), '.');
//										method.rp_num_brackets = countOccurrences(m.toString(), '{');
//										method.rp_num_paren = countOccurrences(m.toString(), '(');
//
//										method.rp_num_lines = numLines;
//										method.rp_avg_line_length = alt_avg_line_length.getAsDouble();
//										method.rp_max_line_length = max_line_length.getAsInt();
//
//										method.rp_num_identifiers = nodeCounts[NodeType.identifier.ordinal()];
//										method.rp_num_if_statements = nodeCounts[NodeType.ifstatement.ordinal()];
//										method.rp_num_loops = nodeCounts[NodeType.loop.ordinal()];
//										method.rp_num_catch = nodeCounts[NodeType.catchclause.ordinal()];
//										method.rp_num_and_or = nodeCounts[NodeType.and_or.ordinal()];
//
//										method.rp_fk_reading_ease = flesch_kincaid_reading_ease;
//										method.rp_fk_grade_level = flesch_kincaid_grade_level;
//									}
//									else if (paradigm == Paradigm.imperative){
//										method.setOo_readability(getReadability(m.toString()));
//
//										method.oo_num_stops = countOccurrences(m.toString(), '.');
//										method.oo_num_brackets = countOccurrences(m.toString(), '{');
//										method.oo_num_paren = countOccurrences(m.toString(), '(');
//
//										method.oo_num_lines = numLines;
//										method.oo_avg_line_length = alt_avg_line_length.getAsDouble();
//										method.oo_max_line_length = max_line_length.getAsInt();
//
//										method.oo_num_identifiers = nodeCounts[NodeType.identifier.ordinal()];
//										method.oo_num_if_statements = nodeCounts[NodeType.ifstatement.ordinal()];
//										method.oo_num_loops = nodeCounts[NodeType.loop.ordinal()];
//										method.oo_num_catch = nodeCounts[NodeType.catchclause.ordinal()];
//										method.oo_num_and_or = nodeCounts[NodeType.and_or.ordinal()];
//
//										method.oo_fk_reading_ease = flesch_kincaid_reading_ease;
//										method.oo_fk_grade_level = flesch_kincaid_grade_level;
//									}
//									else {
//										throw new Exception("Unknown paradigm");
//									}
//								} catch (Exception e) {
//									e.printStackTrace();
//								}
//
//								if (!methodList.contains(method))
//									methodList.add(method);
//							}));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}


	private static Optional<File> getOrigFiles() {
		File[] dirs = getDirectoriesByCategory("source_code");
		return Arrays.stream(dirs).filter(f -> f.isDirectory() && f.getName().endsWith("orig")).findFirst();
	}

	private static Optional<File> getRxFiles() {
		File[] dirs = getDirectoriesByCategory("source_code");
		return Arrays.stream(dirs).filter((f -> f.isDirectory() && f.getName().endsWith("rx"))).findFirst();
	}

//	private static void calculateFeaturesAndDumpResultsToJSON() throws IOException {
//		MySerializedReadability scalabrinoReadability = new MySerializedReadability();
//
//		Optional<File> java_orig = getOrigFiles();
//		Optional<File> java_rx = getRxFiles();
//
//		for (File orig_file : java_orig.get().listFiles()) {
//			System.out.println(orig_file.getName());
//			//find rx file
//			Optional<File> rx_file = Arrays.stream(java_rx.get().listFiles()).filter(f -> f.getName().equals(orig_file.getName())).findFirst();
//
//			if (rx_file.isPresent()) {
//				CompilationUnit origCu = readFile(orig_file.getPath());
//				CompilationUnit rxCu = readFile(rx_file.get().getPath());
//
//				origCu.getTypes().forEach(origType -> {
//					origType.getMethods().forEach(origMethod -> {
//						rxCu.getTypes().forEach(rxType -> {
//							rxType.getMethods().forEach(rxMethod -> {
//								Method orig = new Method(origType.getNameAsString(), origMethod);
//								Method rx = new Method(rxType.getNameAsString(), rxMethod);
//
//								if (orig.equals(rx)) {
//									String origBody = origMethod.getBody().toString();
//									String rxBody = rxMethod.getBody().toString();
//
//									if (!origBody.equals(rxBody)) {
//										refactored.add(orig);
//									}
//								}
//							});
//						});
//					});
//				});
//			}
//		}
//
//		//System.out.println(refactored);
//
//		File[] sourceCodeDirs = getSourceCodeDirectories();
//		for (File f : sourceCodeDirs) {
//			String fileName = f.getName();
//			String path = f.getPath();
//			Paradigm p = fileName.endsWith("rx") ? Paradigm.reactive : Paradigm.imperative;
//			for (File javaFile : getJavaFilesFromDir(path)) {
//				parseSourceCodeFileAndStoreResults(javaFile.getPath(), p);
//			}
//		}
//
//		// USING PMD OUTPUT JSONFILES
////		File[] complexityDirs = getComplexityDirectories();
////		for (File f : complexityDirs) {
////			String fileName = f.getName();
////			String path = f.getPath();
////			Paradigm p = fileName.endsWith("rx") ? Paradigm.reactive : Paradigm.imperative;
////			System.out.println(path);
////			for (File javaFile : getTextFilesFromDir(path)) {
////				parseJSONFileAndStoreResults(javaFile.getPath(), p);
////			}
////		}
//
//		// USING PMD jar to calculate complexity
//		try {
//			calculateComplexities();
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//
//		// SCALABRINO
//		for (File orig_file : java_orig.get().listFiles()) {
//
//			//find rx file
//			Optional<File> rx_file = Arrays.stream(java_rx.get().listFiles()).filter(f -> f.getName().equals(orig_file.getName())).findFirst();
//
//			//scalabrino tool
//
//			Map<String, Double> scalabrino_orig = scalabrinoReadability.getReadabilityMap(orig_file);
//			Map<String, Double> scalabrino_rx = scalabrinoReadability.getReadabilityMap(rx_file.get());
//
//
//			for (String methodName : scalabrino_orig.keySet()) {
//
//				//System.out.println(orig_file.getName().split("[\\.]")[0] + "\t" + methodName);
//
//				Method method = null;
//
//				for (Method m : methodList) {
//					if (m.className.equals(orig_file.getName().split("[\\.]")[0]) &&
//							methodName.equals(m.signature.split("\\(")[0])) {
//						method = m;
//						break;
//					}
//				}
//
//				if (method != null) {
//					method.oo_scalabrino = scalabrino_orig.get(methodName);
//					method.rp_scalabrino = scalabrino_rx.get(methodName);
//				}
//			}
//		}
//
//		ResultsWriter writer = new JSONResultsWriter();
//		writer.setFileName("measurement");
//
//		for (Method m : methodList) {
//			if (RefactoredMethods.shouldCalculate(m)) {
//				writer.addRow(m);
//			}
//			//if (!m.RPreadability().equals(Method.NOT_SET) && !m.OOreadability().equals(Method.NOT_SET))
//		}
//	}

//	public static void calculateFeaturesUsingScalabrinoImpl() {
//		System.out.println("creating methods extractor...");
//
//		MethodsExtractor methodsExtractor = new MethodsExtractor();
//
//		//Optional<File> java_orig = getOrigFiles();
//		//Optional<File> java_rx = getRxFiles();
//
//		System.out.println("getting source code directories...");
//		File[] source_dir = getSourceCodeDirectories();
//
//		System.out.println("getting feature calculators...");
//		List<FeatureCalculator> featureCalculators = FeatureCalculator.getFeatureCalculators();
//
//		System.out.println("getting output writer...");
//		OutputWriter.setImplementor(OutputWriter.CSV);
//		OutputWriter orig_outputWriter = OutputWriter.getWriter();
//		OutputWriter rx_outputWriter = OutputWriter.getWriter();
//
//		orig_outputWriter.setName("DATASET_HOLST_ORIGINAL");
//		rx_outputWriter.setName("DATASET_HOLST_REACTIVE");
//
//		System.out.println("adding attributes...");
//		orig_outputWriter.addAttribute("class");
//		rx_outputWriter.addAttribute("class");
//		orig_outputWriter.addAttribute("method");
//		rx_outputWriter.addAttribute("method");
//		for (FeatureCalculator fc : featureCalculators) {
//			orig_outputWriter.addAttribute(fc.getName());
//			rx_outputWriter.addAttribute(fc.getName());
//		}
//		orig_outputWriter.setClass("Readable", List.of("0", "1"));
//		rx_outputWriter.setClass("Readable", List.of("0", "1"));
//
//		System.out.println("\nlooping trough all methods!...");
//
//		for (File dir : getSourceCodeDirectories()) {
//			System.out.println("###### Entering folder: " + dir.getName() + " ######\n");
//			File[] source_files = dir.listFiles();
//
//			for (File file : source_files) {
//				System.out.println("### Reading file: " + file.getName() + " ###\n");
//				//String className = file.getName().split(".")[0];
//				try {
//					CompilationUnit cu = readFile(file.getPath());
//
//					Paradigm paradigm = dir.getName().endsWith("rx") ? Paradigm.reactive : Paradigm.imperative;
//
//					cu.getTypes()
//							.forEach(t -> t.getMethods()   // for each class
//									.forEach(m -> {        // for each method
//
//										Method method = new Method(t.getNameAsString(), m);
//
//										if (RefactoredMethods.shouldCalculate(method)) {
//											System.out.println("# Calculating features for\t" + method.signature);
//											Record record = new Record();
//											record.setClassValue("?");  // readable or not?
//
//											String extension = paradigm == Paradigm.imperative ? "orig" : "rx";
//											record.addValue(method.className);
//											record.addValue(method.signature + "_" + extension);
//
//											for (FeatureCalculator fc : featureCalculators) {
//
//												try {
//													String methodString = m.getTokenRange().get().toString();
//
//													fc.setSource(methodString);
//													record.addValue(fc.calculate());
//												}catch (Exception e) {
//													record.addValue("?");
//
//													System.out.println("Couldn't calculate feature \"" + fc.getName() + "\"\t reason: " + e.getMessage() );
//												}
//											}
//
//											if (paradigm == Paradigm.imperative) {
//												orig_outputWriter.addRecord(record);
//											}
//											else {
//												rx_outputWriter.addRecord(record);
//											}
//										}
//									})
//							);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//
//		System.out.println("\n\nsaving files!");
//		try {
//			orig_outputWriter.save();
//			rx_outputWriter.save();
//		} catch (OutputException | IOException e) {
//			e.printStackTrace();
//		}
//
//		System.out.println("DONE!");
//	}


	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {

		Context context = new BasicContext();

		context.setDirectoryForOriginalCode("D:\\OpenSourceProjects\\ReadabilityFeaturesCalculator\\src\\main\\data\\source_code_orig");
		context.setDirectoryForRefactoredCode("D:\\OpenSourceProjects\\ReadabilityFeaturesCalculator\\src\\main\\data\\source_code_rx");

		String methodsPath = "D:\\OpenSourceProjects\\ReadabilityFeaturesCalculator\\src\\main\\data\\refactored_methods.txt";
		RefactoredMethods refactoredMethods = new RefactoredMethods(methodsPath);
		context.setRefactoredMethods(refactoredMethods);

		//CalculatedFeatures.getInstance().addFeature(new BuseReadability());
		//CalculatedFeatures.getInstance().addFeature(new ScalabrinoReadability());
		//CalculatedFeatures.getInstance().addFeature(new CyclomaticComplexity());
		CalculatedFeatures.getInstance().addFeature(new ScalabrinoFeatures());

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
		ResultsWriter writer = new CSVResultsWriter();

		writer.setFileName("results");
		for (Method m : context.getMethods()) {
			try {
				writer.addRow(m);
			} catch (FeatureNotSetException e) {
				System.err.println(e.getMessage());
				//e.printStackTrace();
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


