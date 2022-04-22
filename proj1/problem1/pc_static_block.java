import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class pc_static_block {
    private static int NUM_END = 200000;
    private static int NUM_THREADS = 1;
    private static int BLOCKS_RANGE = NUM_END / NUM_THREADS;
    private static int counter = 0;
    private static Object lock = new Object();

    public static void main(String[] args) {
        if (args.length == 2) {
            NUM_THREADS = Integer.parseInt(args[0]);
            NUM_END = Integer.parseInt(args[1]);
            BLOCKS_RANGE = NUM_END / NUM_THREADS;
        }
        long startTime = System.currentTimeMillis();
        ExecutorService es = Executors.newCachedThreadPool();
        for (int i = 0; i <= NUM_THREADS; i++) {
            es.execute(new MyThread(i));
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
        private int start;
        private int end;

        public MyThread(int id) {
            this.id = id;
            start = BLOCKS_RANGE * id;
            end = start + BLOCKS_RANGE;
        }

        public void run() {
            long startTime = System.currentTimeMillis();
            for (int i = start; i <= end; i++) {
                if (isPrime(i))
                    synchronized (lock) {
                        counter++;
                    }
            }
            long endTime = System.currentTimeMillis();
            long timeDiff = endTime - startTime;
            System.out.println("Thread#" + id + " Execution Time: " + timeDiff + "ms");
        }
    }
}
