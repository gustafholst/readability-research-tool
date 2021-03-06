package gillberg.holst;

import com.github.javaparser.ast.body.MethodDeclaration;
import gillberg.holst.enums.Paradigm;
import gillberg.holst.exceptions.ValueNotSetException;
import gillberg.holst.exceptions.FeatureAlreadySetException;
import gillberg.holst.exceptions.FeatureNotSetException;
import gillberg.holst.exceptions.UnknownParadigmException;
import gillberg.holst.features.BuseReadability;
import raykernel.ml.feature.Feature;

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

    public void addCalculatedFeature(CalculatedFeature cf, Number value, Paradigm paradigm) throws UnknownParadigmException, FeatureAlreadySetException {
        CalculatedFeature feature = findCalculatedFeature(cf);

        try {
            if (paradigm == Paradigm.imperative) {
                feature.setValueForOriginal(value);
            }
            else if (paradigm == Paradigm.reactive) {
                feature.setValueForRefactored(value);
            }
            else {
                throw new UnknownParadigmException(paradigm.toString());
            }
        } catch (FeatureAlreadySetException fase) {
            throw new FeatureAlreadySetException(fase.getMessage() + " for method '" + signature + "' in class '" + className + "'");
        }

        calculatedFeatures.put(feature.getName(), feature);
    }

    public CalculatedFeature findCalculatedFeature(CalculatedFeature searched) {
        if (calculatedFeatures.containsKey(searched.getName())) {
            return calculatedFeatures.get(searched.getName());
        }
        return searched;
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
