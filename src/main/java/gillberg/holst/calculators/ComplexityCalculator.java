package gillberg.holst.calculators;

import gillberg.holst.Calculator;
import gillberg.holst.Context;
import gillberg.holst.Method;
import gillberg.holst.enums.Paradigm;
import gillberg.holst.exceptions.FeatureAlreadySetException;
import gillberg.holst.exceptions.MethodNotRefactoredException;
import gillberg.holst.exceptions.UnknownParadigmException;
import gillberg.holst.features.CyclomaticComplexity;
import net.sourceforge.pmd.*;
import net.sourceforge.pmd.renderers.JsonRenderer;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.util.ClasspathClassLoader;
import net.sourceforge.pmd.util.datasource.DataSource;
import net.sourceforge.pmd.util.datasource.FileDataSource;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class ComplexityCalculator extends AbstractCalculator implements Calculator {

    private final PMDConfiguration configuration;

    public ComplexityCalculator(Context context, Paradigm paradigm) {
        super(context, paradigm);

        Properties properties = new Properties();
        properties.setProperty("showClassesComplexity", "true");
        properties.setProperty("showMethodsComplexity", "true");

        this.configuration = new PMDConfiguration();
        this.configuration.setMinimumPriority(RulePriority.LOW);
        this.configuration.setRuleSets("D:\\OpenSourceProjects\\ReadabilityFeaturesCalculator\\src\\main\\resources\\cyc_ruleset.xml");
        this.configuration.setReportProperties(properties);
        try {
            configuration.prependClasspath("/home/workspace/target/classes");
        } catch (IOException e) {
            System.out.println("Could not prepend classpath. Reason: " + e.getMessage());
        }
        int numThreads = 0;
        configuration.setThreads(numThreads);  // in order to not mess upp storing the results
    }

    @Override
    public String getName() {
        return "Cyclomatic complexity";
    }

    public void calculate() throws IOException, FeatureAlreadySetException, MethodNotRefactoredException, UnknownParadigmException {

        RuleSetFactory ruleSetFactory = RulesetsFactoryUtils.createFactory(configuration);

        List<DataSource> files = getDataSourcesFromDirectory(getDirectory());

        Writer rendererOutput = new StringWriter();
        Renderer renderer = createRenderer(rendererOutput);

        renderer.start();

        RuleContext ctx = new RuleContext();

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

        parseJSONStringAndStoreResults(rendererOutput.toString());
    }

    private void parseJSONStringAndStoreResults(String jsonString) throws MethodNotRefactoredException, UnknownParadigmException, FeatureAlreadySetException, IOException {

        JSONParser parser = new JSONParser();

        try {
            JSONObject rootObject = (JSONObject)parser.parse(jsonString);
            JSONArray filesArray = (JSONArray) rootObject.get("files");

             //keep track of class in case of nested classes
            for (Object file : filesArray) {
                String currentClass = null;
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

                        if (currentClass == null) {
                            String[] filenameTokens = fileObject.get("filename").toString().split("\\\\");
                            String filename = filenameTokens[filenameTokens.length - 1];
                            currentClass = filename.substring(0, filename.indexOf('.'));
                        }

                        if (context.shouldCalculate(currentClass, tokens[1])) {

                            Method method = getMethod(currentClass, tokens[1]);

                            String[] t = tokens[2].split("[ .]");

                            Number cycValue = Integer.parseInt(t[t.length - 1]);

                            method.addCalculatedFeature(new CyclomaticComplexity(), cycValue, getParadigm());
                        }
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
                    //System.out.printf("Using %s%n", path);
                    files.add(new FileDataSource(path.toFile()));
                }
                return super.visitFile(path, attrs);
            }
        });
        // System.out.printf("Analyzing %d files in %s%n", files.size(), basePath);
        return files;
    }

    public List<DataSource> getDataSourcesFromDirectory(String directory) throws IOException {
        return determineFiles(directory);
    }
}
