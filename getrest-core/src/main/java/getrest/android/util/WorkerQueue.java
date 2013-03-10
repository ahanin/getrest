/*
 * Copyright 2012 Alexey Hanin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package getrest.android.util;

import getrest.android.core.Loggers;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

/**
 * Implements worker queue.
 *
 * @param <T>
 */
public class WorkerQueue<T> {

    private final Queue<T> queue;
    private final Worker<T> worker;
    private final Set<Thread> threads;
    private int maxThreads;
    private int busyWorkers;
    private Runnable workerRunnable;
    private boolean isRunning;
    private boolean isStopping;

    public interface Worker<T> {
        void execute(final T item);
    }

    public WorkerQueue(final Queue<T> queue, final Worker<T> worker, final int maxThreads) {
        this.queue = queue;
        this.worker = worker;
        this.maxThreads = maxThreads;
        this.threads = new HashSet<Thread>(maxThreads);
    }

    public Queue<T> getQueue() {

        return queue;
    }

    public void add(final T item) {

        synchronized (queue) {
            queue.add(item);
            queue.notifyAll();

            reviseWorkerThreads();
        }

        Loggers.getServiceLogger()
        .debug("Item enqueued: item={0}, queueSize={1}", item, queue.size());
    }

    private void reviseWorkerThreads() {

        synchronized (this) {

            if (isRunning && (queue.size() > (threads.size() - busyWorkers))
                  && (threads.size() < maxThreads)) {

                final Thread thread = new Thread(getWorkerRunnable());
                threads.add(thread);
                thread.start();
            }
        }
    }

    private Runnable getWorkerRunnable() {

        if (workerRunnable == null) {

            synchronized (this) {
                workerRunnable = new WorkerRunnable();
            }
        }

        return workerRunnable;
    }

    public void start() {

        synchronized (this) {
            Preconditions.checkState(!isRunning && !isStopping,
                                     "Worker is either already running or is stopping");

            isRunning = true;

            reviseWorkerThreads();

            synchronized (queue) {
                queue.notifyAll();
            }
        }
    }

    public void stop() {

        synchronized (this) {

            if (!isRunning || isStopping) {
                throw new IllegalStateException("Worker is either not running or is stopping");
            }

            isStopping = true;
            queue.notifyAll();
        }
    }

    private class WorkerRunnable implements Runnable {
        public void run() {
            Loggers.getServiceLogger().debug("Starting worker: [{0}] workerQueue={1}",
                                             Thread.currentThread().getName(),
                                             WorkerQueue.this);

            while (isRunning && !isStopping) {

                synchronized (queue) {

                    while (queue.isEmpty() && isRunning && !isStopping) {

                        try {
                            queue.wait();
                        } catch (final InterruptedException ex) {
                            Loggers.getServiceLogger().error("[{0}] Interrupted exception: {1}",
                                                             ex,
                                                             Thread.currentThread().getName(),
                                                             ex.getMessage());
                        }
                    }

                    if (isRunning && !isStopping) {
                        beginWork();

                        final T item = queue.poll();

                        try {
                            worker.execute(item);
                        } catch (final RuntimeException ex) {
                            Loggers.getServiceLogger().error("[{0}] Unexpected exception: {1}",
                                                             ex,
                                                             Thread.currentThread().getName(),
                                                             ex.getMessage());
                        }

                        Loggers.getServiceLogger().debug("[{0}] Item processed: queueSize={1}",
                                                         Thread.currentThread().getName(),
                                                         queue.size());

                        endWork();

                    }
                }
            }

            Loggers.getServiceLogger().debug("[0] Worker stopped: workerQueue={1}",
                                             Thread.currentThread().getName(),
                                             WorkerQueue.this);
        }
    }

    private void endWork() {

        synchronized (this) {
            busyWorkers--;

            if ((busyWorkers == 0) && isStopping) {
                isRunning = false;
                isStopping = false;
            }
        }
    }

    private void beginWork() {

        synchronized (this) {
            busyWorkers++;
        }
    }
}
