package org.sample.client;

import org.json.JSONObject;

import com.clwillingham.socket.io.IOSocket;
import com.clwillingham.socket.io.MessageCallback;

public class IOSocketClient {

    private IOSocket socket;
    private long time;
    private WSTest tester;
    
    public void setTime(long time) {
        this.time = time;
    }
    
    public long getTime() {
        return time;
    }
    
    public IOSocketClient(WSTest tester) {
        this.tester = tester;
    }

    public IOSocket getIOSocket() {
        return this.socket;
    }
    
    public void connect(String host) throws Exception {
        socket = new IOSocket("http://" + host + ":3000", new MessageCallback() {
            public void on(String event, JSONObject... data) {
            }

            public void onMessage(String message) {
                setTime(System.currentTimeMillis());
                tester.received();
           }

            public void onMessage(JSONObject message) {
            }

            public void onConnect() {
                tester.connected();
            }

            public void onDisconnect() {
            }
        });
        socket.connect();
    }

    public void close() {
        socket.disconnect();
        
    }
}
