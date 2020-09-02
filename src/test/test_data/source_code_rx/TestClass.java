public class TestClass {

    public void sendToTCP (int connectionID, Object object) {
        Observable.fromArray(this.connections)
                .filter(c -> c.id == connectionID)
                .firstElement()
                .subscribe(c -> c.sendTCP(object));
    }

}