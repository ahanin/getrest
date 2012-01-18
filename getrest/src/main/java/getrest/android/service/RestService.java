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
package getrest.android.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import getrest.android.request.Request;
import getrest.android.request.Response;
import getrest.android.util.Logger;
import getrest.android.util.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author aha
 * @since 2012-01-13
 */
public class RestService extends Service implements RequestCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger("GetRest:RestService");

    private final PriorityBlockingQueue<Runnable> jobQueue;
    private final ExecutorService jobExecutorService;

    public RestService() {
        this.jobQueue = new PriorityBlockingQueue<Runnable>();
        this.jobExecutorService = new ThreadPoolExecutor(1, 5, 10, TimeUnit.SECONDS, jobQueue);
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        final Request request = new RequestWrapper(intent).getRequest();

        LOGGER.debug("Received request: requestId={}, url={}, method={}", request.getRequestId(), request.getUri(),
                request.getMethod().getName());

        try {
            final RequestJob job = new RequestJob(request);
            job.setCallback(this);
            jobQueue.add(job);

            LOGGER.debug("Request job {} added to execution queue", request.getRequestId());
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

    public void onResponse(final Response response) {
        // TODO implement response broadcasting
        throw new UnsupportedOperationException("response broadcasting is yet to implement");
    }
}
