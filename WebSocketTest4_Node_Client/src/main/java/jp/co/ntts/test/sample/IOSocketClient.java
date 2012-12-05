package jp.co.ntts.test.sample;

import org.json.JSONObject;

import com.clwillingham.socket.io.IOSocket;
import com.clwillingham.socket.io.MessageCallback;

public class IOSocketClient {

    private int id;
    private IOSocket socket;

    public int getId() {
        return this.id;
    }
    
    public IOSocket getIOSocket() {
        return this.socket;
    }
    
    public void connect(final int id, String host) throws Exception {
        socket = new IOSocket("http://" + host + ":3000", new MessageCallback() {
            public void on(String event, JSONObject... data) {
                System.out.println(id + " >>> " + event + ":" + data[0]);
            }

            public void onMessage(String message) {
                System.out.println(id + " >>> " + message);
            }

            public void onMessage(JSONObject message) {
                System.out.println(id + " >>> " + message + " [JSON])");
            }

            public void onConnect() {
                System.out.println(id + " >>> Connection opened.");
            }

            public void onDisconnect() {
                System.out.println(id + " >>> Connection closed.");
            }
        });
        socket.connect();

        this.id = id;
    }

}
