package jp.co.ntts.test.sample;

import org.json.JSONObject;

import com.clwillingham.socket.io.IOSocket;
import com.clwillingham.socket.io.MessageCallback;

public class IOSocketClient {

    private int id;
    private IOSocket socket;
    private WSTest tester;
    
    public IOSocketClient(WSTest tester) {
        this.tester = tester;
    }

    public int getId() {
        return this.id;
    }
    
    public IOSocket getIOSocket() {
        return this.socket;
    }
    
    public void connect(final int id, String host) throws Exception {
        socket = new IOSocket("http://" + host + ":3000", new MessageCallback() {
            public void on(String event, JSONObject... data) {
            }

            public void onMessage(String message) {
                long recv = System.currentTimeMillis();
                tester.countDown();
                tester.addLog(id + ", " + message + ", " + recv + "\n");
                try {
                    tester.await();
                } catch (Exception e) {
                    // Ignore
                }
                close();
            }

            public void onMessage(JSONObject message) {
            }

            public void onConnect() {
            }

            public void onDisconnect() {
            }
        });
        socket.connect();

        this.id = id;
    }

    public void close() {
        socket.disconnect();
        
    }
}
