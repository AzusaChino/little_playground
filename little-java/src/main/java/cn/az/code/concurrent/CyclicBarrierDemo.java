package cn.az.code.concurrent;

import cn.hutool.core.thread.ThreadUtil;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author az
 */
public class CyclicBarrierDemo {

    public static void main(String[] args) throws InterruptedException {
        // 等待所有任务完成后执行 CyclicBarrier 的 action
        CyclicBarrier cyclicBarrier = new CyclicBarrier(5,
                () -> {
                    System.out.println("All previous tasks are completed");
                });
        ExecutorService service = ThreadUtil.newExecutor(5);
        // 10 = CyclicBarrier(5) + CyclicBarrier.reset()
        for (int i = 0; i < 20; i++) {
            service.submit(() -> {
                action();
                try {
                    // when count > 0, block
                    cyclicBarrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            });
        }
        service.awaitTermination(3, TimeUnit.SECONDS);

        // cyclicBarrier.reset();

        System.out.println("DONE");
        service.shutdown();
    }

    public static void action() {
        System.out.printf("Thread[%s] is running...\n", Thread.currentThread().getName());
    }

    static class Writer extends Thread {
        private final CyclicBarrier cyclicBarrier;

        public Writer(CyclicBarrier cyclicBarrier) {
            this.cyclicBarrier = cyclicBarrier;
        }

        /**
         * If this thread was constructed using a separate
         * <code>Runnable</code> run object, then that
         * <code>Runnable</code> object's <code>run</code> method is called;
         * otherwise, this method does nothing and returns.
         * <p>
         * Subclasses of <code>Thread</code> should override this method.
         *
         * @see #start()
         * @see #stop()
         */
        @Override
        public void run() {
            try {
                Thread.sleep(5000);
                System.out.println(Thread.currentThread().getName() + " finished writing, waiting for others");
                cyclicBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
            System.out.println("all threads finished");
        }
    }
}
