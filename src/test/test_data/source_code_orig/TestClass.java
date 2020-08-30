public class TestClass {

    public void sendToTCP (int connectionID, Object object) {
        Connection[] connections = this.connections;
        for (int i = 0, n = connections.length; i < n; i++) {
            Connection connection = connections[i];
            if (connection.id == connectionID) {
                connection.sendTCP(object);
                break;
            }
        }
    }

}