package gillberg.holst;

import com.github.javaparser.ast.body.MethodDeclaration;
import gillberg.holst.enums.Feature;
import gillberg.holst.enums.Paradigm;
import gillberg.holst.exceptions.ValueNotSetException;
import gillberg.holst.exceptions.FeatureAlreadySetException;
import gillberg.holst.exceptions.FeatureNotSetException;
import gillberg.holst.exceptions.UnknownParadigmException;
import gillberg.holst.features.BuseReadability;

import java.util.*;
import java.util.stream.Collectors;

public class Method extends MethodDeclaration{

//    public ReadabilityComplexity originalReadabilityComplexity = new ReadabilityComplexity();
//    public ReadabilityComplexity refactoredReadabilityComplexity = new ReadabilityComplexity();
//
//    public CalculatedFeatures originalCalculatedFeatures = new CalculatedFeatures();
//    public CalculatedFeatures refactoredCalculatedFeatures = new CalculatedFeatures();

    private Map<String, CalculatedFeature> calculatedFeatures;

    public final static String NOT_SET = "not set";

    public String className;
    public String returnType;
    public String signature;

    public Method(String className, MethodDeclaration declaration) {
        String tempClass = className;

        String n = declaration.getDeclarationAsString(false,false,false);
        String[] tokens = n.split(" ");

        this.returnType = tokens[0];
        String tempSignature = Arrays.stream(tokens).skip(1).collect(Collectors.joining()).replace(" ", "");

        initialize(tempClass, tempSignature);
    }

    public Method(String className, String signature) {
        initialize(className, signature);
    }

    private void initialize(String className, String signature) {
        this.className = className;
        this.signature = signature.replace(" ", "");
        this.calculatedFeatures = new HashMap<>();
    }

    public void addCalculatedFeature(CalculatedFeature cf, Number value, Paradigm paradigm) throws FeatureAlreadySetException, UnknownParadigmException {
        CalculatedFeature feature = findCalculatedFeature(cf);

        if (paradigm == Paradigm.imperative) {
            feature.setValueForOriginal(value);
        }
        else if (paradigm == Paradigm.reactive) {
            feature.setValueForRefactored(value);
        }
        else {
            throw new UnknownParadigmException(paradigm.toString());
        }

        calculatedFeatures.put(feature.getName(), feature);
    }

    public CalculatedFeature findCalculatedFeature(CalculatedFeature searched) {
        if (calculatedFeatures.containsKey(searched.getName())) {
            return calculatedFeatures.get(searched.getName());
        }
        return searched;
    }

//    private boolean isOriginalValueMissing(Number[] values) {
//        return values[Paradigm.imperative.ordinal()] == null;
//    }
//
//    private boolean isRefactoredValueMissing(Number[] values) {
//        return values[Paradigm.reactive.ordinal()] == null;
//    }
//
//    private boolean isAnyValueMissing(Number[] values) {
//        return  isOriginalValueMissing(values) || isRefactoredValueMissing(values);
//    }

//    private Number[] tryToCreateNumberArrayWithValues(Number original, Number refactored) throws ValueNotSetException {
//        Number[] values = new Number[2];
//        values[Paradigm.imperative.ordinal()] = original;
//        values[Paradigm.reactive.ordinal()] = refactored;
//
//        if (isAnyValueMissing(values)) {
//            throw new ValueNotSetException();
//        }
//
//        return values;
//    }

//    public Number[] getBuseReadability() throws ValueNotSetException {
//        try {
//            Number[] readabilities = tryToCreateNumberArrayWithValues(
//                    originalReadabilityComplexity.buseReadability,
//                    refactoredReadabilityComplexity.buseReadability);
//
//            return readabilities;
//        } catch (ValueNotSetException vnse) {
//            throw new ValueNotSetException("Buse readability missing");
//        }
//    }
//
//    public Number[] getScalabrinoReadability() throws ValueNotSetException {
//        try {
//            Number[] readabilities = tryToCreateNumberArrayWithValues(
//                    originalReadabilityComplexity.scalabrinoReadability,
//                    refactoredReadabilityComplexity.scalabrinoReadability);
//
//            return readabilities;
//        } catch (ValueNotSetException vnse) {
//            throw new ValueNotSetException("Scalabrino readability missing");
//        }
//    }

//    public Number[] getCyclomaticComplexity() throws ValueNotSetException {
//        try {
//            Number[] complexities = tryToCreateNumberArrayWithValues(
//                    originalReadabilityComplexity.cyclomaticComplexity,
//                    refactoredReadabilityComplexity.cyclomaticComplexity);
//
//            return complexities;
//        } catch (ValueNotSetException vnse) {
//            throw new ValueNotSetException("Cyclomatic complexity missing");
//        }
//    }
//
//    public Number[] getValuesForFeature(Feature feature) throws FeatureNotSetException, ValueNotSetException {
//        Number[] complexities = tryToCreateNumberArrayWithValues(
//                originalCalculatedFeatures.getCalculatedFeature(feature),
//                refactoredCalculatedFeatures.getCalculatedFeature(feature));
//
//        return complexities;
//    }
//
//    public void setValueForFeature(Feature feature, Paradigm p, Number value) throws FeatureAlreadySetException, UnknownParadigmException {
//        if (p == Paradigm.imperative) {
//            originalCalculatedFeatures.setCalculatedFeature(feature, value);
//        }
//        else if (p == Paradigm.reactive) {
//            refactoredCalculatedFeatures.setCalculatedFeature(feature, value);
//        }
//        else {
//            throw new UnknownParadigmException(feature.toString());
//        }
//    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Method) {
            Method other = (Method)obj;
            return other.className.equals(this.className) && other.signature.equals(this.signature);
        }
        return false;
    }
}
