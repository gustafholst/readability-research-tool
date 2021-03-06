package gillberg.holst;

import com.github.javaparser.ast.body.MethodDeclaration;
import gillberg.holst.exceptions.MethodNotRefactoredException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RefactoredMethods {

    private List<Method> refactoredMethods;
    private final String filePath;

    public RefactoredMethods(String filePath) {
        this.filePath = filePath;
    }

    public List<Method> getRefactoredMethods() throws IOException {
        if (refactoredMethods == null) {
            loadMethods();
        }

        return this.refactoredMethods;
    }

    private void loadMethods() throws IOException {
        List<String> lines = FileUtils.readLines(new File(filePath), "UTF-8");
        parseLinesAndCreateMethods(lines);
    }

    private void parseLinesAndCreateMethods(List<String> lines) {
        refactoredMethods = new ArrayList<>();

        for (String l : lines) {
            if (l.startsWith("%"))  // ignored commented out lines
                continue;

            int firstSpace = l.indexOf(" ");
            String className = l.substring(0, firstSpace);
            String signature = l.substring(firstSpace);

            refactoredMethods.add(new Method(className, signature));
        }
    }

    public Method getMethod(String className, MethodDeclaration methodDeclaration) throws MethodNotRefactoredException, IOException {
        Method temp = new Method(className, methodDeclaration);
        return getMethod(className, temp.signature);
    }

    public Method getMethod(String className, String signature) throws MethodNotRefactoredException, IOException {

        Method temp = new Method(className, signature);

        Optional<Method> foundMethod = getRefactoredMethods().stream().filter(m -> m.equals(temp)).findFirst();
        if (foundMethod.isPresent()) {
            return foundMethod.get();
        }

        throw new MethodNotRefactoredException("No method [" + className + " " + signature + "] in memory");
    }

    public boolean shouldCalculate(Method method) throws IOException {
        return getRefactoredMethods().contains(method);
    }


    //    private static final List<Method> methodSignatures = List.of(
//            new Method("Client", "connect(int,InetAddress,int,int)"),
//            new Method("Client", "run()"),
//            new Method("Client", "start()"),
//            new Method("Client", "close()"),
//            new Method("Client", "broadcast(int,DatagramSocket)"),
//            new Method("Client", "discoverHost(int,int)"),
//            new Method("Client", "discoverHosts(int,int)"),
//            new Method("Connection", "sendTCP(Object)"),
//            new Method("Connection", "sendUDP(Object)"),
//            new Method("Connection", "close()"),
//            new Method("Connection", "addListener(Listener)"),
//            new Method("Connection", "removeListener(Listener)"),
//            new Method("Connection", "notifyConnected()"),
//            new Method("Connection", "notifyDisconnected()"),
//            new Method("Connection", "notifyIdle()"),
//            new Method("Connection", "notifyReceived(Object)"),
//            new Method("Connection", "getRemoteAddressTCP()"),
//            new Method("Connection", "getRemoteAddressUDP()"),
//            new Method("Server", "bind(InetSocketAddress,InetSocketAddress)"),
//            new Method("Server", "keepAlive()"),
//            new Method("Server", "run()"),
//            new Method("Server", "acceptOperation(SocketChannel)"),
//            new Method("Server", "addConnection(Connection)"),
//            new Method("Server", "removeConnection(Connection)"),
//            new Method("Server", "sendToAllTCP(Object)"),
//            new Method("Server", "sendToAllExceptTCP(int,Object)"),
//            new Method("Server", "sendToTCP(int,Object)"),
//            new Method("Server", "sendToAllUDP(Object)"),
//            new Method("Server", "sendToAllExceptUDP(int,Object)"),
//            new Method("Server", "sendToUDP(int,Object)"),
//            new Method("Server", "addListener(Listener)"),
//            new Method("Server", "removeListener(Listener)"),
//            new Method("Server", "close()"),
//            new Method("TcpConnection", "accept(Selector,SocketChannel)"),
//            new Method("TcpConnection", "connect(Selector,SocketAddress,int)"),
//            new Method("TcpConnection", "close()"),
//            new Method("UdpConnection", "bind(Selector,InetSocketAddress)"),
//            new Method("UdpConnection", "connect(Selector,InetSocketAddress)"),
//            new Method("UdpConnection", "readObject(Connection)"),
//            new Method("UdpConnection", "send(Connection,Object,SocketAddress)"),
//            new Method("UdpConnection", "close()"));
}
