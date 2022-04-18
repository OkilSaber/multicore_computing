import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class pc_static_cyclic {
    private static int NUM_END = 200000;
    private static int NUM_THREADS = 1;
    private static int NUM_CYCLES = NUM_END / NUM_THREADS;
    private static int counter = 0;
    private static Object lock = new Object();

    public static void main(String[] args) {
        if (args.length == 2) {
            NUM_THREADS = Integer.parseInt(args[0]);
            NUM_END = Integer.parseInt(args[1]);
            NUM_CYCLES = NUM_END / NUM_THREADS;
        }
        long startTime = System.currentTimeMillis();
        int threadId = 0;
        ExecutorService es = Executors.newCachedThreadPool();
        for (int j = 0; j < NUM_THREADS; j++) {
            es.execute(new MyThread(threadId));
            threadId++;
        }
        es.shutdown();
        while (!es.isTerminated()) {
        }
        long endTime = System.currentTimeMillis();
        long timeDiff = endTime - startTime;
        System.out.println("Program Execution Time: " + timeDiff + "ms");
        System.out.println("1..." + (NUM_END - 1) + " prime# counter=" + counter);
    }

    private static boolean isPrime(int x) {
        if (x <= 1)
            return false;
        for (int i = 2; i < x; i++) {
            if (x % i == 0)
                return false;
        }
        return true;
    }

    public static class MyThread implements Runnable {
        private int id;

        public MyThread(int id) {
            this.id = id;
        }

        public void run() {
            long startTime = System.currentTimeMillis();
            int i = this.id;
            System.out.println(
                    "Thread Running with id:" + this.id);
            for (; i < NUM_END;) {
                if (isPrime(i))
                    synchronized (lock) {
                        counter++;
                    }
                i += NUM_THREADS;
            }
            long endTime = System.currentTimeMillis();
            long timeDiff = endTime - startTime;
            System.out.println("Thread#" + this.id + " Execution Time: " + timeDiff + "ms");
        }
    }
}