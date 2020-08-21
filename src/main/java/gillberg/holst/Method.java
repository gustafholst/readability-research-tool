package gillberg.holst;

import com.github.javaparser.ast.body.MethodDeclaration;
import gillberg.holst.enums.Feature;
import gillberg.holst.enums.Paradigm;
import gillberg.holst.enums.ValueNotSetException;
import gillberg.holst.exceptions.FeatureAlreadySetException;
import gillberg.holst.exceptions.FeatureNotSetException;
import gillberg.holst.exceptions.UnknownParadigmException;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Method extends MethodDeclaration{

    public ReadabilityComplexity originalReadabilityComplexity = new ReadabilityComplexity();
    public ReadabilityComplexity refactoredReadabilityComplexity = new ReadabilityComplexity();

    public CalculatedFeatures originalCalculatedFeatures = new CalculatedFeatures();
    public CalculatedFeatures refactoredCalculatedFeatures = new CalculatedFeatures();

    public final static String NOT_SET = "not set";

    public final String className;
    public final String returnType;
    public final String signature;

    public int rp_num_stops = 0;
    public int oo_num_stops = 0;

    public int rp_num_brackets = 0;
    public int oo_num_brackets = 0;

    public int rp_num_paren = 0;
    public int oo_num_paren = 0;

    public int rp_num_lines = 0;
    public int oo_num_lines = 0;

    public double rp_avg_line_length = 0.0;
    public double oo_avg_line_length = 0.0;

    public int rp_max_line_length = 0;
    public int oo_max_line_length = 0;

    public int rp_num_identifiers = 0;
    public int oo_num_identifiers = 0;

    public int rp_num_if_statements = 0;
    public int oo_num_if_statements = 0;

    public int rp_num_loops = 0;
    public int oo_num_loops = 0;

    public int rp_num_catch = 0;
    public int oo_num_catch = 0;

    public int rp_num_and_or = 0;
    public int oo_num_and_or = 0;

    public Method(String className, MethodDeclaration declaration) {
        this.className = className;

        String n = declaration.getDeclarationAsString(false,false,false);
        String[] tokens = n.split(" ");

        this.returnType = tokens[0];
        this.signature = Arrays.stream(tokens).skip(1).collect(Collectors.joining()).replace(" ", "");
    }

    public Method(String className, String signature) {
        this.className = className;
        this.signature = signature.replace(" ", "");
        this.returnType = null;
    }

    private boolean isOriginalValueMissing(Number[] values) {
        return values[Paradigm.imperative.ordinal()] == null;
    }

    private boolean isRefactoredValueMissing(Number[] values) {
        return values[Paradigm.reactive.ordinal()] == null;
    }

    private boolean isAnyValueMissing(Number[] values) {
        return  isOriginalValueMissing(values) || isRefactoredValueMissing(values);
    }

    private Number[] tryToCreateNumberArrayWithValues(Number original, Number refactored) throws ValueNotSetException {
        Number[] values = new Number[2];
        values[Paradigm.imperative.ordinal()] = original;
        values[Paradigm.reactive.ordinal()] = refactored;

        if (isAnyValueMissing(values)) {
            throw new ValueNotSetException();
        }

        return values;
    }

    public Number[] getBuseReadability() throws ValueNotSetException {
        try {
            Number[] readabilities = tryToCreateNumberArrayWithValues(
                    originalReadabilityComplexity.buseReadability,
                    refactoredReadabilityComplexity.buseReadability);

            return readabilities;
        } catch (ValueNotSetException vnse) {
            throw new ValueNotSetException("Buse readability missing");
        }
    }

    public Number[] getScalabrinoReadability() throws ValueNotSetException {
        try {
            Number[] readabilities = tryToCreateNumberArrayWithValues(
                    originalReadabilityComplexity.scalabrinoReadability,
                    refactoredReadabilityComplexity.scalabrinoReadability);

            return readabilities;
        } catch (ValueNotSetException vnse) {
            throw new ValueNotSetException("Scalabrino readability missing");
        }
    }

    public Number[] getCyclomaticComplexity() throws ValueNotSetException {
        try {
            Number[] complexities = tryToCreateNumberArrayWithValues(
                    originalReadabilityComplexity.cyclomaticComplexity,
                    refactoredReadabilityComplexity.cyclomaticComplexity);

            return complexities;
        } catch (ValueNotSetException vnse) {
            throw new ValueNotSetException("Cyclomatic complexity missing");
        }
    }

    public Number[] getValuesForFeature(Feature feature) throws FeatureNotSetException, ValueNotSetException {
        Number[] complexities = tryToCreateNumberArrayWithValues(
                originalCalculatedFeatures.getCalculatedFeature(feature),
                refactoredCalculatedFeatures.getCalculatedFeature(feature));

        return complexities;
    }

    public void setValueForFeature(Feature feature, Paradigm p, Number value) throws FeatureAlreadySetException, UnknownParadigmException {
        if (p == Paradigm.imperative) {
            originalCalculatedFeatures.setCalculatedFeature(feature, value);
        }
        else if (p == Paradigm.reactive) {
            refactoredCalculatedFeatures.setCalculatedFeature(feature, value);
        }
        else {
            throw new UnknownParadigmException(feature.toString());
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Method) {
            Method other = (Method)obj;
            return other.className.equals(this.className) && other.signature.equals(this.signature);
        }
        return false;
    }
}
