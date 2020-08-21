package gillberg.holst;

import java.util.List;

public class RefactoredMethods {

    private static final List<Method> methodSignatures = List.of(
            new Method("Client", "connect(int,InetAddress,int,int)"),
            new Method("Client", "run()"),
            new Method("Client", "start()"),
            new Method("Client", "close()"),
            new Method("Client", "broadcast(int,DatagramSocket)"),
            new Method("Client", "discoverHost(int,int)"),
            new Method("Client", "discoverHosts(int,int)"),
            new Method("Connection", "sendTCP(Object)"),
            new Method("Connection", "sendUDP(Object)"),
            new Method("Connection", "close()"),
            new Method("Connection", "addListener(Listener)"),
            new Method("Connection", "removeListener(Listener)"),
            new Method("Connection", "notifyConnected()"),
            new Method("Connection", "notifyDisconnected()"),
            new Method("Connection", "notifyIdle()"),
            new Method("Connection", "notifyReceived(Object)"),
            new Method("Connection", "getRemoteAddressTCP()"),
            new Method("Connection", "getRemoteAddressUDP()"),
            new Method("Server", "bind(InetSocketAddress,InetSocketAddress)"),
            new Method("Server", "keepAlive()"),
            new Method("Server", "run()"),
            new Method("Server", "acceptOperation(SocketChannel)"),
            new Method("Server", "addConnection(Connection)"),
            new Method("Server", "removeConnection(Connection)"),
            new Method("Server", "sendToAllTCP(Object)"),
            new Method("Server", "sendToAllExceptTCP(int,Object)"),
            new Method("Server", "sendToTCP(int,Object)"),
            new Method("Server", "sendToAllUDP(Object)"),
            new Method("Server", "sendToAllExceptUDP(int,Object)"),
            new Method("Server", "sendToUDP(int,Object)"),
            new Method("Server", "addListener(Listener)"),
            new Method("Server", "removeListener(Listener)"),
            new Method("Server", "close()"),
            new Method("TcpConnection", "accept(Selector,SocketChannel)"),
            new Method("TcpConnection", "connect(Selector,SocketAddress,int)"),
            new Method("TcpConnection", "close()"),
            new Method("UdpConnection", "bind(Selector,InetSocketAddress)"),
            new Method("UdpConnection", "connect(Selector,InetSocketAddress)"),
            new Method("UdpConnection", "readObject(Connection)"),
            new Method("UdpConnection", "send(Connection,Object,SocketAddress)"),
            new Method("UdpConnection", "close()"));

    public static boolean shouldCalculate(Method method) {
        return methodSignatures.contains(method);
    }

}
