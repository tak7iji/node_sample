package org.sample.client;

import java.io.PrintStream;
import java.util.concurrent.CountDownLatch;

public class WSTest {
    CountDownLatch latch_con = new CountDownLatch(1);
    CountDownLatch latch_end = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {
        System.setOut(new PrintStream(new java.io.OutputStream() {
            public void write(int b) {
            }
        }));

        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-") && args.length >= (i + 1)) {
                try {
                    Options.valueOf(args[i].substring(1)).setValue(args[++i]);
                } catch (IllegalArgumentException iae) {
                    // ignore
                }
            }
        }

        int num = Integer.parseInt(Options.n.getValue());
        for(int size = 1000000; size <= 10000000; size+=1000000) {
            System.err.println("Size: "+size);
            for(int i = 0; i < num; i++) {
                new WSTest().startTest(Options.host.getValue(), Integer.toString(size));
                Thread.sleep(5000);
            }
        }
    }

    public void startTest(String host, String size) throws Exception {
        IOSocketClient client = new IOSocketClient(this);
        client.connect(host);
        latch_con.await();
        client.getIOSocket().send(size);
        long send = System.currentTimeMillis();
        latch_end.await();
        System.err.println("Errapsed time: " + (client.getTime() - send));
        client.close();
    }

    enum Options {
        host("localhost"), n("1");

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
    
    public void connected() {
        latch_con.countDown();
    }

    public void received() {
        latch_end.countDown();
    }
}
