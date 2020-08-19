package gillberg.holst;

import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {

        String method = "\tprivate void broadcast (int udpPort, DatagramSocket socket) throws IOException {\n" +
                "\t\tByteBuffer dataBuffer = ByteBuffer.allocate(64);\n" +
                "\t\tserialization.write(null, dataBuffer, new DiscoverHost());\n" +
                "\t\tdataBuffer.flip();\n" +
                "\t\tbyte[] data = new byte[dataBuffer.limit()];\n" +
                "\t\tdataBuffer.get(data);\n" +
                "\n" +
                "\t\tObservable.fromIterable(Collections.list(NetworkInterface.getNetworkInterfaces()))\n" +
                "\t\t\t\t.flatMap(iface -> Observable.fromIterable(Collections.list(iface.getInetAddresses()))\n" +
                "\t\t\t\t\t\t.map(InetAddress::getAddress)\n" +
                "\t\t\t\t\t\t// Java 1.5 doesn't support getting the subnet mask, so try the two most common.\n" +
                "\t\t\t\t\t\t.doOnNext(ip -> {\n" +
                "\t\t\t\t\t\t\tip[3] = -1; // 255.255.255.0\n" +
                "\t\t\t\t\t\t\ttry{\n" +
                "\t\t\t\t\t\t\t\tsocket.send(new DatagramPacket(data, data.length, InetAddress.getByAddress(ip), udpPort));\n" +
                "\t\t\t\t\t\t\t}catch (Exception ignored){}\n" +
                "\t\t\t\t\t\t})\n" +
                "\t\t\t\t\t\t.doOnNext(ip -> {\n" +
                "\t\t\t\t\t\t\tip[2] = -1; // 255.255.0.0\n" +
                "\t\t\t\t\t\t\ttry{\n" +
                "\t\t\t\t\t\t\t\tsocket.send(new DatagramPacket(data, data.length, InetAddress.getByAddress(ip), udpPort));\n" +
                "\t\t\t\t\t\t\t}catch (Exception ignored){}\n" +
                "\t\t\t\t\t\t})\n" +
                "\t\t\t\t\t\t.doFinally(() -> {\n" +
                "\t\t\t\t\t\t\tif (DEBUG) debug(\"kryonet\", \"Broadcasted host discovery on port: \" + udpPort);\n" +
                "\t\t\t\t\t\t})\n" +
                "\t\t\t\t).subscribe(item -> {}, ignored -> {});\n" +
                "\t}";


        String[] lines = method.split("\n");

//        List<String> list = Arrays.stream(lines).map(String::trim).collect(Collectors.toList());
//        System.out.println(list);


        Arrays.stream(lines)
                .map(String::trim)
                .forEach(System.out::println);
    }

}
