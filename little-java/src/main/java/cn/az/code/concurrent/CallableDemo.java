package cn.az.code.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Liz
 */
public class CallableDemo<T> {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        TicketCallable<String> ticketCallable = new TicketCallable<>(10);
        // FutureTask<String> futureTask = new FutureTask<>(ticketCallable);
        ExecutorService service = Executors.newCachedThreadPool();
        Future<String> future = service.submit(ticketCallable);
        System.out.println(future.get());
    }

    public static class TicketCallable<T> implements Callable<T> {

        private int tickets;

        private T t;

        public TicketCallable(int tickets) {
            this.tickets = tickets;
        }

        /**
         * Computes a result, or throws an exception if unable to do so.
         *
         * @return computed result
         */
        @Override
        public T call() {
            while (tickets > 0) {
                System.out.println("left : " + tickets);
                tickets--;
            }
            // String res = "all sold";
            return t;
        }
    }
}
