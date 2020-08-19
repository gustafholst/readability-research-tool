package gillberg.holst;

import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Method {

    public final static String NOT_SET = "not set";

    public final String className;
    public final String returnType;
    public final String signature;
    private double rp_readability = Double.MIN_VALUE;
    private int rp_complexity = Integer.MIN_VALUE;
    private double oo_readability = Double.MIN_VALUE;
    private int oo_complexity = Integer.MIN_VALUE;

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

    public double oo_fk_reading_ease = 0.0;
    public double oo_fk_grade_level = 0.0;
    public double rp_fk_reading_ease = 0.0;
    public double rp_fk_grade_level = 0.0;

    public double oo_scalabrino = 0.0;
    public double rp_scalabrino = 0.0;

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

    private boolean OOreadabilitySet() {
        return Double.compare(this.oo_readability, Double.MIN_VALUE) != 0;
    }

    private boolean RPreadabilitySet() {
        return Double.compare(this.rp_readability, Double.MIN_VALUE) != 0;
    }

    private boolean OOcomplexitytySet() {
        return Integer.compare(this.oo_complexity, Integer.MIN_VALUE) != 0;
    }

    private boolean RPcomplexitySet() {
        return Integer.compare(this.rp_complexity, Integer.MIN_VALUE) != 0;
    }

    public String OOreadability() {
        return OOreadabilitySet() ? String.valueOf(oo_readability) : NOT_SET;
    }

    public String RPreadability() {
        return RPreadabilitySet() ? String.valueOf(rp_readability) : NOT_SET;
    }

    public String OOcomplexity() {
        return OOcomplexitytySet() ? String.valueOf(oo_complexity) : NOT_SET;
    }

    public String RPcomplexity() {
        return RPcomplexitySet() ? String.valueOf(rp_complexity) : NOT_SET;
    }

    public double getOo_readability() {
        return oo_readability;
    }

    public void setOo_readability(double oo_readability) throws Exception {
        if (OOreadabilitySet())
            throw new Exception("OO readability is already set");
        this.oo_readability = oo_readability;
    }

    public int getOo_complexity() {
        return oo_complexity;
    }

    public void setOo_complexity(int oo_complexity) throws Exception {
        if (this.oo_complexity != Integer.MIN_VALUE)
            throw new Exception("OO complexity is already set");
        this.oo_complexity = oo_complexity;
    }

    public double getRp_readability() {
        return rp_readability;
    }

    public void setRp_readability(double rp_readability) throws Exception {
        if (Double.compare(this.rp_readability, Double.MIN_VALUE) != 0)
            throw new Exception("RP readability is already set");
        this.rp_readability = rp_readability;
    }

    public int getRp_complexity() {
        return rp_complexity;
    }

    public void setRp_complexity(int rp_complexity) throws Exception {
        if (this.rp_complexity != Integer.MIN_VALUE)
            throw new Exception("RP complexity is already set");
        this.rp_complexity = rp_complexity;
    }

    public String toString() {
        return String.format("Class: %s\t " +
                "return type: %s\t " +
                "name: %s\t" +
                "RP_readability: %s\t" +
                "RP_complexity: %s\t" +
                "OO_readability %s\t" +
                "OO_complexity: %s" +
                "\n", className, returnType, signature, RPreadability(), RPcomplexity(), OOreadability(), OOcomplexity());
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
