package jp.co.ntts.test.sample;

import com.clwillingham.socket.io.IOSocket;
import com.clwillingham.socket.io.MessageCallback;
import org.json.JSONObject;

public class WSTest {
    public static void main(String[] args) throws Exception {
        new WSTest().startTest(args.length == 0 ? 1 : Integer.valueOf(args[0]));
    }

    public void startTest(int max) throws Exception {

        for (int i = 0; i < max; i++) {
            open(i);
            Thread.sleep(10);
        }
        synchronized (this) {
            wait();
        }
    }

    public void open(final int id) {
        try {
            IOSocket socket = new IOSocket("http://localhost:3000/",
                    new MessageCallback() {
                        public void on(String event, JSONObject... data) {
                            System.out.println(id + " >>> " + event + ":" + data[0]);
                        }

                        @Override
                        public void onMessage(String message) {
                            System.out.println(id + " >>> " + message);
                        }

                        @Override
                        public void onMessage(JSONObject message) {
                            System.out.println(id + " >>> " + message + " [JSON])");
                        }

                        @Override
                        public void onConnect() {
                            System.out.println(id + " >>> Connection opened.");
                        }

                        @Override
                        public void onDisconnect() {
                            System.out.println(id + " >>> Connection closed.");
                        }

                    });
            socket.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
