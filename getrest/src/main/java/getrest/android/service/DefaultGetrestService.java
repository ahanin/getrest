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

import android.os.Binder;
import android.os.IBinder;

import getrest.android.core.GetrestRuntime;
import getrest.android.core.Loggers;
import getrest.android.core.Request;
import getrest.android.core.RequestFuture;
import getrest.android.core.RequestManager;

import getrest.android.util.Provider;
import getrest.android.util.WorkerQueue;

import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 
 * @author aha
 *
 * @since 2012-01-13
 */
public class DefaultGetrestService extends Service implements GetrestService {

    private final WorkerQueue<RequestTuple> requestWorkerQueue = new WorkerQueue<RequestTuple>(
        new LinkedBlockingQueue<RequestTuple>(),
        new RequestProcessor(new RequestManagerProvider()),
        10);
    private boolean isStarted;

    public <R extends Request<V>, V> RequestFuture<V> execute(final R request,
                                                              final CallerContext callerContext) {

        final String requestId = UUID.randomUUID().toString();

        return execute(request, callerContext, requestId);
    }

    private <R extends Request<V>, V> RequestFuture<V> execute(final R request,
                                                               final CallerContext callerContext,
                                                               final String requestId) {
        Loggers.getClientLogger().trace("Executing request {1}",
                                        DefaultGetrestService.class.getSimpleName(),
                                        requestId);

        final RequestTuple<R> requestTuple = new RequestTuple<R>(requestId, request, callerContext);

        final RequestFutureSupport requestFutureSupport = getRequestManager()
                                                          .getRequestFutureSupport(requestTuple);
        getRequestManager().persistRequest(requestId, request);

        requestWorkerQueue.add(requestTuple);

        requestFutureSupport.fireOnPending();

        return new RequestFutureImpl<R, V>(requestFutureSupport);
    }

    public <R extends Request<V>, V> RequestFuture<V> obtainRequestFuture(final R request,
                                                                          final CallerContext callerContextAdapter) {

        // TODO implement "reconnection" to an already being executed request, probably cache
        throw new UnsupportedOperationException("not yet implemented");
    }

    private final LocalBinder localBinder = new LocalBinder(this);

    private static class LocalBinder extends Binder implements GetrestServiceBinder {

        private GetrestService getrestService;

        public LocalBinder(final GetrestService getrestService) {
            this.getrestService = getrestService;
        }

        public GetrestService getGetrestService() {

            return getrestService;
        }
    }

    public DefaultGetrestService() {}

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {

        if (!isStarted) {
            requestWorkerQueue.start();
        }

        isStarted = true;

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(final Intent intent) {

        return localBinder;
    }

    private class RequestManagerProvider implements Provider<RequestManager> {
        public RequestManager get() {

            return getRequestManager();
        }
    }

    private RequestManager getRequestManager() {

        return GetrestRuntime.getInstance(getApplicationContext()).getRequestManager();
    }
}
