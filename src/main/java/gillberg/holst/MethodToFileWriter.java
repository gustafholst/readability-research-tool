package gillberg.holst;

import gillberg.holst.exceptions.TokenRangeNotSetException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class MethodToFileWriter {

    public static void writeToFile(Method m) throws TokenRangeNotSetException {
        System.out.println("==========================================================");
        System.out.println("==========    " + m.className + "::" + m.getDeclarationAsString());

        try {
            String fileName = ReadabilityFeaturesCalculator.getDataPath()
                    + File.separator
                    + m.className
                    + File.separator
                    + "method_files"
                    + File.separator
                    + m.getNameAsString();

            if (m.getTokenRange().isPresent()) {
                FileUtils.writeStringToFile(new File(fileName), m.getTokenRange().get().toString());
            }else {
                throw new TokenRangeNotSetException();
            }

        } catch (IOException e) {
            System.out.println("Could not write method to file. Reason: " + e.getMessage());
        }
    }
}
