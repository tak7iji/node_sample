package jp.co.ntts.test.sample;

import com.clwillingham.socket.io.IOSocket;
import com.clwillingham.socket.io.MessageCallback;
import org.json.JSONObject;

public class WSTest {
    public static void main(String[] args) throws Exception {
        for(int i = 0; i < args.length; i++){
            if(args[i].startsWith("-") && args.length >= (i+1)) {
                try {
                    Options.valueOf(args[i].substring(1)).setValue(args[++i]);
                } catch (IllegalArgumentException iae) {
                    //ignore
                }
            }
        }
        new WSTest().startTest(Integer.parseInt(Options.max.getValue()), Options.host.getValue());
    }

    public void startTest(int max, String host) throws Exception {

        for (int i = 0; i < max; i++) {
            open(i, host);
            Thread.sleep(10);
        }
        synchronized (this) {
            wait();
        }
    }

    public void open(final int id, String host) {
        try {
            IOSocket socket = new IOSocket("http://"+host+":3000",
                    new MessageCallback() {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    enum Options {
        host("localhost"),
        max("1");
        
        private String value;

        Options(String defaultValue) {
            this.value = defaultValue;
        }
        
        String getValue() {
            return this.value;
        }
        
        void setValue(String value) {
            this.value = value;
        }
    }
}
