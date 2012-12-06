package jp.co.ntts.test.sample;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class WSTest {
    private CyclicBarrier barrier;
    private CountDownLatch latch;
    
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
        barrier = new CyclicBarrier(max+1);
        latch = new CountDownLatch(max);
        long start = System.currentTimeMillis();

        for (int i = 0; i < max; i++) {
            new Thread(new SendTask(open(i, host))).start();
            Thread.sleep(10);
        }
        System.out.println("Elapsed time: "
                + (System.currentTimeMillis() - start));

        Thread.sleep(5000);

        System.out.println(System.currentTimeMillis() + ", Start all tasks.");
        barrier.await();
        barrier.reset();

        // wait until all tasks finish.
        latch.await();
        System.out.println(System.currentTimeMillis() + ", All tasks finished.");

        Thread.sleep(5000);
        System.out.println(System.currentTimeMillis() + ", Push logs to file.");
        open(max, host).pushLog();
        barrier.await();
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

    class SendTask implements Runnable {
        private IOSocketClient client;

        public SendTask(IOSocketClient client) {
            this.client = client;
        }

        public void run() {
            try {
                // wait until all threads are ready to go.
                barrier.await();
                client.getIOSocket().send(
                        client.getId() + "," + System.currentTimeMillis());
                latch.countDown();
                
                Thread.sleep(1000);
                barrier.await();
                client.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
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
