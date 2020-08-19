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

import com.ipeirotis.readability.engine.Readability;
import com.ipeirotis.readability.enums.MetricType;

import it.unimol.readability.metric.FeatureCalculator;
import it.unimol.readability.metric.output.CSVWriter;
import it.unimol.readability.metric.output.OutputException;
import it.unimol.readability.metric.output.OutputWriter;
import it.unimol.readability.metric.output.Record;
import it.unimol.readability.metric.runnable.SerializedReadability;

import net.sourceforge.pmd.*;
import net.sourceforge.pmd.cpd.LanguageFactory;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.metrics.impl.CycloMetric;
import net.sourceforge.pmd.lang.java.metrics.impl.internal.CycloVisitor;
import net.sourceforge.pmd.lang.java.rule.design.CyclomaticComplexityRule;
import net.sourceforge.pmd.renderers.JsonRenderer;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.renderers.XMLRenderer;
import net.sourceforge.pmd.stat.Metric;
import net.sourceforge.pmd.util.ClasspathClassLoader;
import net.sourceforge.pmd.util.datasource.DataSource;
import net.sourceforge.pmd.util.datasource.FileDataSource;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import it.unimol.readability.metric.API.MetricClassifier;
import org.w3c.dom.Document;
import selector.runnable.MethodsExtractor;

import static raykernel.apps.readability.eval.Main.getReadability;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class JavaFileParser {

	private static final List<Method> methodList = new ArrayList<>();
	private static final List<Method> refactored = new ArrayList<>();

	private static final List<Method> methodSignatures = List.of(new Method("Client", "connect(int,InetAddress,int,int)"),
			new Method("Client", "run()"),
			new Method("Client", "start()"),
			new Method("Client", "close()"),
			new Method("Client", "broadcast(int,DatagramSocket)"),
			new Method("Client", "discoverHost(int,int)"),
			new Method("Client", "discoverHosts(int,int)"),
			new Method("Connection", "sendTCP(Object)"),
			new Method("Connection", "sendUDP(Object)"),
			new Method("Connection", "close()"),
			new Method("Connection", "addListener(Listener)"),
			new Method("Connection", "removeListener(Listener)"),
			new Method("Connection", "notifyConnected()"),
			new Method("Connection", "notifyDisconnected()"),
			new Method("Connection", "notifyIdle()"),
			new Method("Connection", "notifyReceived(Object)"),
			new Method("Connection", "getRemoteAddressTCP()"),
			new Method("Connection", "getRemoteAddressUDP()"),
			new Method("Server", "bind(InetSocketAddress,InetSocketAddress)"),
			new Method("Server", "keepAlive()"),
			new Method("Server", "run()"),
			new Method("Server", "acceptOperation(SocketChannel)"),
			new Method("Server", "addConnection(Connection)"),
			new Method("Server", "removeConnection(Connection)"),
			new Method("Server", "sendToAllTCP(Object)"),
			new Method("Server", "sendToAllExceptTCP(int,Object)"),
			new Method("Server", "sendToTCP(int,Object)"),
			new Method("Server", "sendToAllUDP(Object)"),
			new Method("Server", "sendToAllExceptUDP(int,Object)"),
			new Method("Server", "sendToUDP(int,Object)"),
			new Method("Server", "addListener(Listener)"),
			new Method("Server", "removeListener(Listener)"),
			new Method("Server", "close()"),
			new Method("TcpConnection", "accept(Selector,SocketChannel)"),
			new Method("TcpConnection", "connect(Selector,SocketAddress,int)"),
			new Method("TcpConnection", "close()"),
			new Method("UdpConnection", "bind(Selector,InetSocketAddress)"),
			new Method("UdpConnection", "connect(Selector,InetSocketAddress)"),
			new Method("UdpConnection", "readObject(Connection)"),
			new Method("UdpConnection", "send(Connection,Object,SocketAddress)"),
			new Method("UdpConnection", "close()"));

	public static boolean shouldCalculate(Method method) {
		return methodSignatures.contains(method);
	}

	public enum Paradigm {
		imperative, reactive;

		public String toString() {
			return name();
		}
	}

	public enum NodeType {
		identifier, ifstatement, loop, catchclause, and_or
	}

	private static CompilationUnit readFile(String filename) throws IOException {

		JavaParser parser = new JavaParser();

		parser.getParserConfiguration().setAttributeComments(true);
		parser.getParserConfiguration().setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_13);
		parser.getParserConfiguration().setCharacterEncoding(Charset.defaultCharset());
		parser.getParserConfiguration().setDoNotAssignCommentsPrecedingEmptyLines(false);

		InputStream in = null;
		CompilationUnit cu = null;
		try
		{
			in = new FileInputStream(filename);
			ParseResult<CompilationUnit> result = parser.parse(in);
			cu = result.getResult().get();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally
		{
			assert in != null;
			in.close();
		}
		return cu;
	}

	public static Optional<Method> findMethod(Method method) {
		return methodList.stream()
				.filter(m -> m.equals(method))
				.findFirst();
	}

	public static Method findOrCreateMethod(String className, MethodDeclaration method) {
		Method temp = new Method(className, method);
		return findMethod(temp).orElse(temp);
	}

	public static Method findOrCreateMethod(String className, String name) {
		Method temp = new Method(className, name);
		return findMethod(temp).orElse(temp);
	}

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

	public static File[] getComplexityDirectories() {
		return getDirectoriesByCategory("complexity");
	}

	public static File[] getJavaFilesFromDir(String pathName) {
		return new File(pathName).listFiles(f -> f.getName().endsWith(".java"));
	}

	public static File[] getTextFilesFromDir(String pathName) {
		return new File(pathName).listFiles(f -> f.getName().endsWith(".txt"));
	}

	public static int countOccurrences(String str, char searched) {
		return ((int) str.chars().filter(ch -> ch == searched).count());
	}

	public static List<DataSource> getDataSourcesFromDirectory(String directory) throws IOException {
		return determineFiles("D:\\OpenSourceProjects\\ReadabilityFeaturesCalculator\\src\\main\\data\\" + directory);
	}

	public static void calculateComplexities(String directory) throws IOException, ParseException {
		PMDConfiguration configuration = new PMDConfiguration();
		configuration.setMinimumPriority(RulePriority.LOW);
		configuration.setRuleSets("D:\\OpenSourceProjects\\ReadabilityFeaturesCalculator\\src\\main\\resources\\cyc_ruleset.xml");
		configuration.prependClasspath("/home/workspace/target/classes");
		configuration.setThreads(0);  // in order to not mess upp storing the results

		int numThread = configuration.getThreads();

		RuleSetFactory ruleSetFactory = RulesetsFactoryUtils.createFactory(configuration);

		List<DataSource> files = getDataSourcesFromDirectory(directory);

		Writer rendererOutput = new StringWriter();
		Renderer renderer = createRenderer(rendererOutput);

		renderer.start();

		RuleContext ctx = new RuleContext();
		ctx.getReport().addListener(createReportListener()); // alternative way to collect violations

		try {
			PMD.processFiles(configuration, ruleSetFactory, files, ctx,
					Collections.singletonList(renderer));
		} finally {
			ClassLoader auxiliaryClassLoader = configuration.getClassLoader();
			if (auxiliaryClassLoader instanceof ClasspathClassLoader) {
				((ClasspathClassLoader) auxiliaryClassLoader).close();
			}
		}

		renderer.end();
		renderer.flush();
//		System.out.println("Rendered Report:");
//		System.out.println(rendererOutput.toString());
		Paradigm p = directory.endsWith("orig") ? Paradigm.imperative : Paradigm.reactive;

		FileUtils.write(new File(p + ".json"), rendererOutput.toString());

		System.out.println("Calculated complexities from directory " + directory);
		System.out.println("Using " + numThread + " threads");

		JSONParser parser = new JSONParser();
		JSONObject rootObject = (JSONObject)parser.parse(rendererOutput.toString());

		parseJSONObjectAndStoreResults(rootObject, p);
	}

	private static ThreadSafeReportListener createReportListener() {
		return new ThreadSafeReportListener() {
			@Override
			public void ruleViolationAdded(RuleViolation ruleViolation) {
				System.out.printf("%-20s:%d %s%n", ruleViolation.getFilename(),
						ruleViolation.getBeginLine(), ruleViolation.getDescription());
			}

			@Override
			public void metricAdded(Metric metric) {
				// ignored
			}
		};
	}

	private static Renderer createRenderer(Writer writer) {
		//XMLRenderer renderer = new XMLRenderer("UTF-8");
		JsonRenderer renderer = new JsonRenderer();
		renderer.setWriter(writer);
		return renderer;
	}

	private static List<DataSource> determineFiles(String basePath) throws IOException {
		Path dirPath = FileSystems.getDefault().getPath(basePath);
		PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:*.java");

		List<DataSource> files = new ArrayList<>();

		Files.walkFileTree(dirPath, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
				if (matcher.matches(path.getFileName())) {
					System.out.printf("Using %s%n", path);
					files.add(new FileDataSource(path.toFile()));
				} else {
					System.out.printf("Ignoring %s%n", path);
				}
				return super.visitFile(path, attrs);
			}
		});
		System.out.printf("Analyzing %d files in %s%n", files.size(), basePath);
		return files;
	}

	public static void parseSourceCodeFileAndStoreResults(String filePath, Paradigm paradigm) {

		try {
			CompilationUnit cu = readFile(filePath);
			cu.getTypes()
					.forEach(t -> t.getMethods()   // for each class
							.forEach(m -> {        // for each method

//								System.out.println("==========================================================");
//								System.out.println("==========    " + t.getNameAsString() + "::" + m.getDeclarationAsString());

								//WRITE METHOD TO FILE
//								try {
//									String fileName = getDataPath() + File.separator + t.getNameAsString() + File.separator + "method_files" + File.separator + m.getNameAsString();
//
//									FileUtils.writeStringToFile(new File(fileName), m.getTokenRange().get().toString());
//
//								} catch (IOException e) {
//									e.printStackTrace();
//								}

								Method method = findOrCreateMethod(t.getNameAsString(), m);
								Position begin = m.getBegin().get();
								Position end = m.getEnd().get();

								int numLines = end.line - begin.line + 1;  //add one to count closing bracket as one line

								String methodString = m.getTokenRange().get().toString();

								String[] lines = methodString.split(System.lineSeparator());

								OptionalInt max_line_length = Arrays.stream(lines)
										.map(String::trim)
//										.peek(line -> {
//											//System.out.println(m.getNameAsString());
//											if (m.getNameAsString().equals("broadcast")) {
//												System.out.println(line);
//											}
//										})
										.mapToInt(String::length)
										.max();

								OptionalDouble alt_avg_line_length = Arrays.stream(lines)
										.map(String::trim)
										.mapToInt(String::length)
										.average();

								final int[] nodeCounts = new int[NodeType.values().length];
								m.walk(node -> {
									if (node instanceof NodeWithIdentifier) {  //this
										nodeCounts[NodeType.identifier.ordinal()]++;
										//System.out.println(node.toString());
									}
									if (node instanceof IfStmt) {
										nodeCounts[NodeType.ifstatement.ordinal()]++;
									}
									if (node instanceof ForStmt || node instanceof WhileStmt) {
										nodeCounts[NodeType.loop.ordinal()]++;
									}
									if (node instanceof CatchClause) {
										nodeCounts[NodeType.catchclause.ordinal()]++;
									}
									if (node instanceof BinaryExpr) {
										BinaryExpr be = (BinaryExpr)node;
										BinaryExpr.Operator op= be.getOperator();
										if (op == BinaryExpr.Operator.AND
												|| op == BinaryExpr.Operator.OR
												|| op == BinaryExpr.Operator.BINARY_AND
												|| op == BinaryExpr.Operator.BINARY_OR) {
											nodeCounts[NodeType.and_or.ordinal()]++;
										}
									}
								});

								Readability ipReadability = new Readability(methodString);
								double flesch_kincaid_reading_ease = ipReadability.getMetric(MetricType.FLESCH_KINCAID);
								double flesch_kincaid_grade_level = ipReadability.getMetric(MetricType.FLESCH_READING);

								try {
									if (paradigm == Paradigm.reactive) {
										method.setRp_readability(getReadability(m.toString()));

										method.rp_num_stops = countOccurrences(m.toString(), '.');
										method.rp_num_brackets = countOccurrences(m.toString(), '{');
										method.rp_num_paren = countOccurrences(m.toString(), '(');

										method.rp_num_lines = numLines;
										method.rp_avg_line_length = alt_avg_line_length.getAsDouble();
										method.rp_max_line_length = max_line_length.getAsInt();

										method.rp_num_identifiers = nodeCounts[NodeType.identifier.ordinal()];
										method.rp_num_if_statements = nodeCounts[NodeType.ifstatement.ordinal()];
										method.rp_num_loops = nodeCounts[NodeType.loop.ordinal()];
										method.rp_num_catch = nodeCounts[NodeType.catchclause.ordinal()];
										method.rp_num_and_or = nodeCounts[NodeType.and_or.ordinal()];

										method.rp_fk_reading_ease = flesch_kincaid_reading_ease;
										method.rp_fk_grade_level = flesch_kincaid_grade_level;
									}
									else if (paradigm == Paradigm.imperative){
										method.setOo_readability(getReadability(m.toString()));

										method.oo_num_stops = countOccurrences(m.toString(), '.');
										method.oo_num_brackets = countOccurrences(m.toString(), '{');
										method.oo_num_paren = countOccurrences(m.toString(), '(');

										method.oo_num_lines = numLines;
										method.oo_avg_line_length = alt_avg_line_length.getAsDouble();
										method.oo_max_line_length = max_line_length.getAsInt();

										method.oo_num_identifiers = nodeCounts[NodeType.identifier.ordinal()];
										method.oo_num_if_statements = nodeCounts[NodeType.ifstatement.ordinal()];
										method.oo_num_loops = nodeCounts[NodeType.loop.ordinal()];
										method.oo_num_catch = nodeCounts[NodeType.catchclause.ordinal()];
										method.oo_num_and_or = nodeCounts[NodeType.and_or.ordinal()];

										method.oo_fk_reading_ease = flesch_kincaid_reading_ease;
										method.oo_fk_grade_level = flesch_kincaid_grade_level;
									}
									else {
										throw new Exception("Unknown paradigm");
									}
								} catch (Exception e) {
									e.printStackTrace();
								}

								if (!methodList.contains(method))
									methodList.add(method);
							}));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void parseJSONObjectAndStoreResults(JSONObject rootObject, Paradigm paradigm) {
		try {
			JSONArray filesArray = (JSONArray) rootObject.get("files");

			String currentClass = null;  //keep track of class in case of nested classes
			for (Object file : filesArray) {
				JSONObject fileObject = (JSONObject)file;
				JSONArray violationsArray = (JSONArray) fileObject.get("violations");

				for (Object o : violationsArray) {
					String description = ((JSONObject)o).get("description").toString();
					String[] tokens = description.split("'");
					String type = tokens[0].split(" ")[1];

					if (type.equals("class")) {
						currentClass = tokens[1];
					}
					else if (type.equals("method") ) { // only store methods
						Method method = findOrCreateMethod(currentClass, tokens[1]);
//						if (!shouldCalculate(method))
//							continue;

						String[] t = tokens[2].split("[ .]");
						if (paradigm == Paradigm.reactive) {
							method.setRp_complexity(Integer.parseInt(t[t.length - 1]));
						}
						else if (paradigm == Paradigm.imperative) {
							method.setOo_complexity(Integer.parseInt(t[t.length - 1]));
						}

						if (!methodList.contains(method))
							methodList.add(method);
					}
				}
			}
		} catch (FileNotFoundException | ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void parseJSONFileAndStoreResults(String filePath, Paradigm paradigm) {
		JSONParser parser = new JSONParser();

		try {
			FileReader fileReader = new FileReader(filePath);
			JSONObject rootObject = (JSONObject) parser.parse(fileReader);
			parseJSONObjectAndStoreResults(rootObject, paradigm);
		}catch (ParseException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Optional<File> getOrigFiles() {
		File[] dirs = getDirectoriesByCategory("source_code");
		return Arrays.stream(dirs).filter(f -> f.isDirectory() && f.getName().endsWith("orig")).findFirst();
	}

	private static Optional<File> getRxFiles() {
		File[] dirs = getDirectoriesByCategory("source_code");
		return Arrays.stream(dirs).filter((f -> f.isDirectory() && f.getName().endsWith("rx"))).findFirst();
	}

	private static void calculateFeaturesAndDumpResultsToJSON() throws IOException {
		MySerializedReadability scalabrinoReadability = new MySerializedReadability();

		Optional<File> java_orig = getOrigFiles();
		Optional<File> java_rx = getRxFiles();

		for (File orig_file : java_orig.get().listFiles()) {
			System.out.println(orig_file.getName());
			//find rx file
			Optional<File> rx_file = Arrays.stream(java_rx.get().listFiles()).filter(f -> f.getName().equals(orig_file.getName())).findFirst();

			if (rx_file.isPresent()) {
				CompilationUnit origCu = readFile(orig_file.getPath());
				CompilationUnit rxCu = readFile(rx_file.get().getPath());

				origCu.getTypes().forEach(origType -> {
					origType.getMethods().forEach(origMethod -> {
						rxCu.getTypes().forEach(rxType -> {
							rxType.getMethods().forEach(rxMethod -> {
								Method orig = new Method(origType.getNameAsString(), origMethod);
								Method rx = new Method(rxType.getNameAsString(), rxMethod);

								if (orig.equals(rx)) {
									String origBody = origMethod.getBody().toString();
									String rxBody = rxMethod.getBody().toString();

									if (!origBody.equals(rxBody)) {
										refactored.add(orig);
									}
								}
							});
						});
					});
				});
			}
		}

		//System.out.println(refactored);

		File[] sourceCodeDirs = getSourceCodeDirectories();
		for (File f : sourceCodeDirs) {
			String fileName = f.getName();
			String path = f.getPath();
			Paradigm p = fileName.endsWith("rx") ? Paradigm.reactive : Paradigm.imperative;
			for (File javaFile : getJavaFilesFromDir(path)) {
				parseSourceCodeFileAndStoreResults(javaFile.getPath(), p);
			}
		}

		// USING PMD OUTPUT JSONFILES
//		File[] complexityDirs = getComplexityDirectories();
//		for (File f : complexityDirs) {
//			String fileName = f.getName();
//			String path = f.getPath();
//			Paradigm p = fileName.endsWith("rx") ? Paradigm.reactive : Paradigm.imperative;
//			System.out.println(path);
//			for (File javaFile : getTextFilesFromDir(path)) {
//				parseJSONFileAndStoreResults(javaFile.getPath(), p);
//			}
//		}

		// USING PMD jar to calculate complexity
		try {
			calculateComplexities("source_code_orig");
			calculateComplexities("source_code_rx");
		} catch (ParseException e) {
			e.printStackTrace();
		}

		// SCALABRINO
		for (File orig_file : java_orig.get().listFiles()) {

			//find rx file
			Optional<File> rx_file = Arrays.stream(java_rx.get().listFiles()).filter(f -> f.getName().equals(orig_file.getName())).findFirst();

			//scalabrino tool

			Map<String, Double> scalabrino_orig = scalabrinoReadability.getReadabilityMap(orig_file);
			Map<String, Double> scalabrino_rx = scalabrinoReadability.getReadabilityMap(rx_file.get());


			for (String methodName : scalabrino_orig.keySet()) {

				//System.out.println(orig_file.getName().split("[\\.]")[0] + "\t" + methodName);

				Method method = null;

				for (Method m : methodList) {
					if (m.className.equals(orig_file.getName().split("[\\.]")[0]) &&
							methodName.equals(m.signature.split("\\(")[0])) {
						method = m;
						break;
					}
				}

				if (method != null) {
					method.oo_scalabrino = scalabrino_orig.get(methodName);
					method.rp_scalabrino = scalabrino_rx.get(methodName);
				}
			}
		}

		//System.out.println(methodList);

		JSONObject outRoot = new JSONObject();

		JSONArray array = new JSONArray();

		for (Method m : methodList) {
			JSONObject jo = new JSONObject();
			jo.put("className", m.className);
			jo.put("signature", m.signature);
			jo.put("rp_readability", m.getRp_readability()); //m.RPreadability());
			jo.put("rp_complexity", m.getRp_complexity()); //m.RPcomplexity());
			jo.put("oo.readability", m.getOo_readability()); //m.OOreadability());
			jo.put("oo_complexity", m.getOo_complexity()); //m.OOcomplexity());
			jo.put("oo_num_stops", m.oo_num_stops);
			jo.put("oo_num_brackets", m.oo_num_brackets);
			jo.put("oo_num_paren", m.oo_num_paren);
			jo.put("rp_num_stops", m.rp_num_stops);
			jo.put("rp_num_brackets", m.rp_num_brackets);
			jo.put("rp_num_paren", m.rp_num_paren);
			jo.put("rp_num_lines", m.rp_num_lines);
			jo.put("oo_num_lines", m.oo_num_lines);
			jo.put("oo_avg_line_length", m.oo_avg_line_length);
			jo.put("rp_avg_line_length", m.rp_avg_line_length);
			jo.put("oo_num_identifiers", m.oo_num_identifiers);
			jo.put("rp_num_identifiers", m.rp_num_identifiers);
			jo.put("oo_num_if_statements", m.oo_num_if_statements);
			jo.put("rp_num_if_statements", m.rp_num_if_statements);
			jo.put("oo_num_loops", m.oo_num_loops);
			jo.put("rp_num_loops", m.rp_num_loops);
			jo.put("oo_num_catch", m.oo_num_catch);
			jo.put("rp_num_catch", m.rp_num_catch);
			jo.put("oo_num_and_or", m.oo_num_and_or);
			jo.put("rp_num_and_or", m.rp_num_and_or);

			jo.put("oo_max_line_length", m.oo_max_line_length);
			jo.put("rp_max_line_length", m.rp_max_line_length);

			jo.put("rp_fk_reading_ease", m.rp_fk_reading_ease);
			jo.put("rp_fk_grade_level", m.rp_fk_grade_level);
			jo.put("oo_fk_reading_ease", m.oo_fk_reading_ease);
			jo.put("oo_fk_grade_level", m.oo_fk_grade_level);

			jo.put("oo_scalabrino", m.oo_scalabrino);
			jo.put("rp_scalabrino", m.rp_scalabrino);

			if (refactored.contains(m))
				array.add(jo);

			//if (!m.RPreadability().equals(Method.NOT_SET) && !m.OOreadability().equals(Method.NOT_SET))
		}

		outRoot.put("methods", array);

		FileWriter writer = new FileWriter("measurements.json");

		outRoot.writeJSONString(writer);

		writer.flush();
		writer.close();
	}

	public static void calculateFeaturesUsingScalabrinoImpl() {
		System.out.println("creating methods extractor...");

		MethodsExtractor methodsExtractor = new MethodsExtractor();

		//Optional<File> java_orig = getOrigFiles();
		//Optional<File> java_rx = getRxFiles();

		System.out.println("getting source code directories...");
		File[] source_dir = getSourceCodeDirectories();

		System.out.println("getting feature calculators...");
		List<FeatureCalculator> featureCalculators = FeatureCalculator.getFeatureCalculators();

		System.out.println("getting output writer...");
		OutputWriter.setImplementor(OutputWriter.CSV);
		OutputWriter orig_outputWriter = OutputWriter.getWriter();
		OutputWriter rx_outputWriter = OutputWriter.getWriter();

		orig_outputWriter.setName("DATASET_HOLST_ORIGINAL");
		rx_outputWriter.setName("DATASET_HOLST_REACTIVE");

		System.out.println("adding attributes...");
		orig_outputWriter.addAttribute("class");
		rx_outputWriter.addAttribute("class");
		orig_outputWriter.addAttribute("method");
		rx_outputWriter.addAttribute("method");
		for (FeatureCalculator fc : featureCalculators) {
			orig_outputWriter.addAttribute(fc.getName());
			rx_outputWriter.addAttribute(fc.getName());
		}
		orig_outputWriter.setClass("Readable", List.of("0", "1"));
		rx_outputWriter.setClass("Readable", List.of("0", "1"));

		System.out.println("\nlooping trough all methods!...");

		for (File dir : getSourceCodeDirectories()) {
			System.out.println("###### Entering folder: " + dir.getName() + " ######\n");
			File[] source_files = dir.listFiles();

			for (File file : source_files) {
				System.out.println("### Reading file: " + file.getName() + " ###\n");
				//String className = file.getName().split(".")[0];
				try {
					CompilationUnit cu = readFile(file.getPath());

					Paradigm paradigm = dir.getName().endsWith("rx") ? Paradigm.reactive : Paradigm.imperative;

					cu.getTypes()
							.forEach(t -> t.getMethods()   // for each class
									.forEach(m -> {        // for each method

										Method method = new Method(t.getNameAsString(), m);

										if (shouldCalculate(method)) {
											System.out.println("# Calculating features for\t" + method.signature);
											Record record = new Record();
											record.setClassValue("?");  // readable or not?

											String extension = paradigm == Paradigm.imperative ? "orig" : "rx";
											record.addValue(method.className);
											record.addValue(method.signature + "_" + extension);

											for (FeatureCalculator fc : featureCalculators) {

												try {
													String methodString = m.getTokenRange().get().toString();

													fc.setSource(methodString);
													record.addValue(fc.calculate());
												}catch (Exception e) {
													record.addValue("?");

													System.out.println("Couldn't calculate feature \"" + fc.getName() + "\"\t reason: " + e.getMessage() );
												}
											}

											if (paradigm == Paradigm.imperative) {
												orig_outputWriter.addRecord(record);
											}
											else {
												rx_outputWriter.addRecord(record);
											}
										}
									})
							);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		System.out.println("\n\nsaving files!");
		try {
			orig_outputWriter.save();
			rx_outputWriter.save();
		} catch (OutputException | IOException e) {
			e.printStackTrace();
		}

		System.out.println("DONE!");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException, ParseException {

		// initial calculations for thesis
		calculateFeaturesAndDumpResultsToJSON();

		//calculateFeaturesUsingScalabrinoImpl();
	}
}


