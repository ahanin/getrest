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
package com.github.ahanin.getrest.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.github.ahanin.getrest.Request;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author aha
 * @since 2012-01-13
 */
public class RestService extends Service {

    private final PriorityBlockingQueue<Runnable> jobQueue;
    private final ExecutorService jobExecutorService;

    public RestService(final ExecutorService jobExecutorService) {
        jobQueue = new PriorityBlockingQueue<Runnable>();
        this.jobExecutorService = new ThreadPoolExecutor(1, 5, 10, TimeUnit.SECONDS, jobQueue);
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        final Request request = new RequestWrapper(intent).getRequest();
        try {
            final RequestJob job = new RequestJob(request);
            jobExecutorService.submit(job);
        } catch (RejectedExecutionException ex) {
            // TODO implement rejected execution fallback
            throw new UnsupportedOperationException("Handling of rejected execution exception is yet to implement");
        }
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

}
