package jp.co.ntts.test.sample;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.clwillingham.socket.io.IOSocket;
import com.clwillingham.socket.io.MessageCallback;
import org.json.JSONObject;

public class WSTest {
    public static void main(String[] args) throws Exception {
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-") && args.length >= (i + 1)) {
                try {
                    Options.valueOf(args[i].substring(1)).setValue(args[++i]);
                } catch (IllegalArgumentException iae) {
                    // ignore
                }
            }
        }
        new WSTest().startTest(Integer.parseInt(Options.max.getValue()),
                Options.host.getValue());
    }

    public void startTest(int max, String host) throws Exception {

        long start = System.currentTimeMillis();

        for (int i = 0; i < max; i++) {
            new Timer().schedule(new SendTask(open(i, host)), new Date(start
                    + 5000 + (max * 50)));
            Thread.sleep(10);
        }
        System.out.println("Elapsed time: "
                + (System.currentTimeMillis() - start));

        synchronized (this) {
            wait();
        }
    }

    public IOSocketClient open(final int id, String host) {
        IOSocketClient client = null;
        try {
            client = new IOSocketClient();
            client.connect(id, host);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return client;
    }

    class SendTask extends TimerTask {
        private IOSocketClient client;

        public SendTask(IOSocketClient client) {
            this.client = client;
        }

        @Override
        public void run() {
            try {
                client.getIOSocket().send(client.getId() + "," + System.currentTimeMillis());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class IOSocketClient {

        private int id;
        private IOSocket socket;

        int getId() {
            return this.id;
        }
        
        IOSocket getIOSocket() {
            return this.socket;
        }
        
        void connect(final int id, String host) throws Exception {
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

    enum Options {
        host("localhost"), max("1");

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
