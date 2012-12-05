package jp.co.ntts.test.sample;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

public class WSTest {
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
        latch = new CountDownLatch(max);
        long start = System.currentTimeMillis();
        Date delay = new Date(start + 5000 + (max * 50));

        for (int i = 0; i < max; i++) {
            new Timer().schedule(new SendTask(open(i, host)), delay);
            Thread.sleep(10);
        }
        System.out.println("Elapsed time: "
                + (System.currentTimeMillis() - start));

        latch.await();

        Thread.sleep(5000);
        open(max, host).getIOSocket().send("get");
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
                client.getIOSocket().send(
                        client.getId() + "," + System.currentTimeMillis());
                latch.countDown();
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
