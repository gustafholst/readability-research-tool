package gillberg.holst;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class RefactoredMethodsTest {

    @Test(timeout=5000)
    public void initializingRefactoredMethodsObjectWithExistingFileShouldReturnNonEmptyList() {

        RefactoredMethods refactoredMethods = new RefactoredMethods(
                "D:\\OpenSourceProjects\\ReadabilityFeaturesCalculator\\src\\test\\test_data\\refactored_methods.txt");

        List<Method> result = refactoredMethods.getRefactoredMethods();
        assertFalse(result.isEmpty());
    }

    @Test(timeout=5000)
    public void FileContainingFiveLinesShouldReturnListOfSizeFive() {

        RefactoredMethods refactoredMethods = new RefactoredMethods(
                "D:\\OpenSourceProjects\\ReadabilityFeaturesCalculator\\src\\test\\test_data\\five_methods.txt");

        List<Method> result = refactoredMethods.getRefactoredMethods();
        assertEquals(5, result.size());
    }

    @Test(timeout=5000)
    public void FileContainingFiveMethodNameShouldReturnListContainingThoseMethods() {

        RefactoredMethods refactoredMethods = new RefactoredMethods(
                "D:\\OpenSourceProjects\\ReadabilityFeaturesCalculator\\src\\test\\test_data\\five_methods.txt");

        List<Method> result = refactoredMethods.getRefactoredMethods();

        List<Method> expected = Arrays.asList(
                new Method("Client", "close()"),
                new Method("Connection", "notifyConnected()"),
                new Method("Server", "run()"),
                new Method("TcpConnection", "accept(Selector,SocketChannel)"),
                new Method("UdpConnection", "send(Connection,Object,SocketAddress)")
                );

        assertEquals(expected, refactoredMethods.getRefactoredMethods());
    }
}
