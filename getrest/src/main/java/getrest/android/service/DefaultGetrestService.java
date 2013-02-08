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

import getrest.android.core.Request;
import getrest.android.core.RequestFuture;

import getrest.android.util.GetrestSupport;
import getrest.android.util.WorkerQueue;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * 
 * @author aha
 *
 * @since 2012-01-13
 */
public class DefaultGetrestService extends Service implements Broadcaster, GetrestService {

    private WorkerQueue<RequestTuple> requestWorkerQueue = new WorkerQueue<RequestTuple>(
        new LinkedBlockingQueue<RequestTuple>(),
        RequestProcessor.getInstance(),
        10);
    private boolean isStarted;

    public <R extends Request<V>, V> RequestFuture<V> execute(final R request,
                                                              final CallerContext callerContext) {

        final RequestFutureSupport<R, V> requestFutureSupport = GetrestSupport
                                                   .createRequestFutureSupport(request,
                                                                               callerContext);

        requestWorkerQueue.add(requestFutureSupport.getRequestTuple());

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

    @Override
    public void sendBroadcast(final Intent intent) {
        super.sendBroadcast(intent);
    }
}
