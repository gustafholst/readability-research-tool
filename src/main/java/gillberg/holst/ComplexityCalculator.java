package gillberg.holst;

import gillberg.holst.enums.Paradigm;
import net.sourceforge.pmd.*;
import net.sourceforge.pmd.renderers.JsonRenderer;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.stat.Metric;
import net.sourceforge.pmd.util.ClasspathClassLoader;
import net.sourceforge.pmd.util.datasource.DataSource;
import net.sourceforge.pmd.util.datasource.FileDataSource;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ComplexityCalculator {

    private final int numThreads = 0;
    private String directory;
    private PMDConfiguration configuration;

    public ComplexityCalculator(String dir) {
        this.directory = dir;

        PMDConfiguration configuration = new PMDConfiguration();
        configuration.setMinimumPriority(RulePriority.LOW);
        configuration.setRuleSets("D:\\OpenSourceProjects\\ReadabilityFeaturesCalculator\\src\\main\\resources\\cyc_ruleset.xml");
        try {
            configuration.prependClasspath("/home/workspace/target/classes");
        } catch (IOException e) {
            System.out.println("Could not instantiate ComplexityCalculator. Reason: " + e.getMessage());
        }
        configuration.setThreads(numThreads);  // in order to not mess upp storing the results
    }

    public String calculate() throws IOException {


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

        return rendererOutput.toString();
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

    public static List<DataSource> getDataSourcesFromDirectory(String directory) throws IOException {
        return determineFiles("D:\\OpenSourceProjects\\ReadabilityFeaturesCalculator\\src\\main\\data\\" + directory);
    }
}
