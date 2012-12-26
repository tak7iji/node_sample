package org.sample.client;

import java.io.FileWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class WSTest {
    private CountDownLatch latch;
    private CyclicBarrier barrier;
    private List<String> logList;

    public static void main(String[] args) throws Exception {
        System.setOut(new PrintStream(new java.io.OutputStream() {
            public void write(int b) {
            }
        }));
        System.setErr(new PrintStream(new java.io.OutputStream() {
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
        new WSTest().startTest(Integer.parseInt(Options.max.getValue()),
                Options.host.getValue());
    }

    public void startTest(int max, String host) throws Exception {
        IOSocketClient client = null;
        logList = Collections.synchronizedList(new ArrayList<String>(max));
        latch = new CountDownLatch(max);
        barrier = new CyclicBarrier(max + 1);
        for (int i = 0; i < max; i++) {
            client = open(i, host);
            Thread.sleep(10);
        }

        Thread.sleep(5000);
        client.getIOSocket().send("Hello World!");
        latch.await();
        Thread.sleep(1000);
        barrier.await();

        FileWriter writer = new FileWriter("./log.csv", false);
        for (String line : logList) {
            writer.write(line);
        }
        writer.flush();
        writer.close();
    }

    public IOSocketClient open(final int id, String host) throws Exception {
        IOSocketClient client = new IOSocketClient(this);
        client.connect(id, host);
        return client;
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

    public void countDown() {
        latch.countDown();
    }

    public void addLog(String msg) {
        logList.add(msg);
    }

    public void await() throws Exception {
        barrier.await();
    }
}
