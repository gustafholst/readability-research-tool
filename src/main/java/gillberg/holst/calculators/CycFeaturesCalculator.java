package gillberg.holst.calculators;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithIdentifier;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import gillberg.holst.*;
import gillberg.holst.enums.Paradigm;
import gillberg.holst.exceptions.FeatureAlreadySetException;
import gillberg.holst.exceptions.MethodNotRefactoredException;
import gillberg.holst.exceptions.UnknownParadigmException;
import gillberg.holst.features.BuseReadability;
import gillberg.holst.features.DynamicFeature;
import it.unimol.readability.metric.FeatureCalculator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static raykernel.apps.readability.eval.Main.getReadability;

public class CycFeaturesCalculator extends AbstractCalculator implements Calculator {

    private enum Feature {
        num_if_statements,
        num_loops,
        num_catch,
        num_and_or;

        public static List<String> getFeatures() {
            List<String> featureNames = new ArrayList<>();
            for (Feature f : values()) {
                featureNames.add(f.toString());
            }
            return featureNames;
        }

        public String toString() {
            return name();
        }

    }

    public CycFeaturesCalculator(Context context, Paradigm paradigm) {
        super(context, paradigm);
    }

    @Override
    public void calculate() throws IOException, FeatureAlreadySetException, MethodNotRefactoredException, UnknownParadigmException {
        File[] files = getJavaFilesFromDir(getDirectory());

        for (File file : files) {
            CompilationUnit cu = readFile(file);
            parseCompilationUnitAndStoreResults(cu);
        }
    }

    private void parseCompilationUnitAndStoreResults(CompilationUnit compilationUnit) {
        compilationUnit.getTypes()
                .forEach(t -> t.getMethods()   // for each class
                        .forEach(m -> {        // for each method
                            try {
                                Method method = getMethod(t.getNameAsString(), m);

                                final int[] nodeCounts = new int[Feature.values().length];
								m.walk(node -> {
									if (node instanceof IfStmt) {
										nodeCounts[Feature.num_if_statements.ordinal()]++;
									}
									if (node instanceof ForStmt || node instanceof WhileStmt) {
										nodeCounts[Feature.num_loops.ordinal()]++;
									}
									if (node instanceof CatchClause) {
										nodeCounts[Feature.num_catch.ordinal()]++;
									}
									if (node instanceof BinaryExpr) {
										BinaryExpr be = (BinaryExpr)node;
										BinaryExpr.Operator op = be.getOperator();
										if (op == BinaryExpr.Operator.AND
												|| op == BinaryExpr.Operator.OR
												|| op == BinaryExpr.Operator.BINARY_AND
												|| op == BinaryExpr.Operator.BINARY_OR) {
											nodeCounts[Feature.num_and_or.ordinal()]++;
										}
									}
								});

                                CalculatedFeatures instance = CalculatedFeatures.getInstance();

                                //add every feature to CalculatedFeatures singleton
                                for (Feature feature : Feature.values()) {
                                    CalculatedFeature newFeature = new DynamicFeature(feature.toString());
                                    //features.add(newFeature);

                                    Integer value = nodeCounts[feature.ordinal()];
                                    try {
                                        method.addCalculatedFeature(newFeature, value, getParadigm());
                                    } catch (UnknownParadigmException e) {
                                        e.printStackTrace();
                                    } catch (FeatureAlreadySetException e) {
                                        System.err.println("Feature \"" + newFeature.getName() + "\"\t already set for method " + method.signature );
                                    }

                                    //register feature with calculated feature singleton
                                    instance.addFeature(newFeature);
                                }
                            } catch (MethodNotRefactoredException ignored) {
                                //ignore
                            } catch (IOException ioe) {
                                ioe.printStackTrace();
                            }
                        })
                );
    }

    @Override
    public String getName() {
        return "Cyclomatic complexity features";
    }
}
