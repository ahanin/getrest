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
package getrest.android.client.impl;

import android.app.Activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import android.os.Handler;
import android.os.IBinder;

import getrest.android.GetrestClient;

import getrest.android.client.RequestCallback;
import getrest.android.client.RequestCallbackFactory;
import getrest.android.client.RequestRegistry;

import getrest.android.core.GetrestRuntime;
import getrest.android.core.Loggers;
import getrest.android.core.Request;
import getrest.android.core.RequestFuture;
import getrest.android.core.RequestManager;

import getrest.android.service.CallerContext;
import getrest.android.service.DefaultGetrestService;
import getrest.android.service.GetrestService;
import getrest.android.service.GetrestServiceBinder;

import getrest.android.util.Preconditions;
import getrest.android.util.TypeLiteral;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class GetrestClientImpl extends GetrestClient {

    private final AtomicReference<RequestRegistry> requestRegistry = new AtomicReference<RequestRegistry>();
    private final CallerContextAdapter callerContextAdapter = new CallerContextAdapter();
    private Class<?extends GetrestService> serviceClass = DefaultGetrestService.class;
    private GetrestServiceConnection serviceConnection;
    private GetrestRuntime runtime;
    private Context androidContext;

    public GetrestClientImpl() {}

    private RequestFuture obtainStoredRequestFuture(final String requestId) {

        final RequestRegistry requestRegistry = getRequestRegistry();
        final RequestRegistry.Entry entry = requestRegistry.getEntry(requestId);

        if (entry == null) {
            throw new IllegalStateException("Request is not registered: " + requestId);
        }

        final RequestManager requestManager = runtime.getRequestManager();
        final Request request = requestManager.getRequest(requestId);

        return obtainRequestFuture(request);
    }

    @Override
    public <V> V executeWithResult(final Request<V> request) {

        return execute(request).get();
    }

    @Override
    public <R extends Request<?extends V>, V> RequestFuture<V> execute(final Request<V> request) {
        Preconditions.checkArgNotNull(request, "request");

        return serviceConnection.getService().execute(request, callerContextAdapter);
    }

    @Override
    public <R> R execute(final Request request, final Class<R> responseType) {

        return execute(request, TypeLiteral.fromClass(responseType));
    }

    @Override
    public <V> V execute(final Request request, final TypeLiteral<V> responseTypeLiteral) {
        Loggers.getClientLogger().trace("Calling {0}: requestId={1}, request={2}",
                                        DefaultGetrestService.class.getSimpleName(),
                                        request.getRequestId(),
                                        request);

        return (V) serviceConnection.getService().execute(request, callerContextAdapter);
    }

    private RequestRegistry getRequestRegistry() {

        if (requestRegistry.get() == null) {

            synchronized (requestRegistry) {

                if (requestRegistry.get() == null) {

                    if (!(androidContext instanceof Activity)) {
                        throw new IllegalStateException("Context must be an Activity");
                    }

                    requestRegistry.set(new RequestRegistryPreferencesImpl((Activity) androidContext));
                }
            }
        }

        return requestRegistry.get();
    }

    private <R extends Request<V>, V> RequestFuture<V> obtainRequestFuture(final R request) {

        final RequestFuture requestFuture = serviceConnection.getService().obtainRequestFuture(request,
                                                                                               callerContextAdapter);

        final RequestCallbackFactory callbackFactory = getRequestCallbackFactory();

        if (callbackFactory != null) {

            final RequestCallback callback = callbackFactory.createCallback(request);

            if (callback != null) {
                requestFuture.setRequestCallback(callback);
            }
        }

        return requestFuture;
    }

    /**
     * Obtain request future for previously registered request.
     *
     * @param entry
     *
     * @return
     */
    private RequestFuture obtainRequestFuture(final RequestRegistry.Entry entry) {

        final RequestManager requestManager = runtime.getRequestManager();
        final Request request = requestManager.getRequest(entry.getRequestId());

        if (request == null) {
            Loggers.getClientLogger().warn("Request [{0}] is not acknowledged by request manager",
                                           entry.getRequestId());

            return null;
        } else {

            return obtainRequestFuture(request);
        }
    }

    protected final void init(final Context context) {
        this.runtime = GetrestRuntime.getInstance(context);

        this.androidContext = context;

        serviceClass = DefaultGetrestService.class;

        final Intent intent = new Intent(context, serviceClass);
        context.startService(intent);

        serviceConnection = new GetrestServiceConnection();

        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void detach() {
        androidContext.unbindService(serviceConnection);
    }

    @Override
    public void replay() {

        // TODO Rewrite replaying of requests
        //        final Set<RequestRegistry.Entry> entries = getRequestRegistry().getEntries();
        //
        //        throw new UnsupportedOperationException("");
        //
        //        for (final RequestRegistry.Entry entry : entries) {
        //            obtainRequestFuture(entry);
        //        }
    }

    private static class GetrestServiceConnection implements ServiceConnection {

        private GetrestServiceBinder serviceBinder;
        public final Object waitLock = new Object();

        public GetrestServiceConnection() {}

        public void onServiceConnected(final ComponentName componentName, final IBinder iBinder) {

            synchronized (waitLock) {
                serviceBinder = (GetrestServiceBinder) iBinder;

                Loggers.getClientLogger()
                .debug("Service bound: {0}", serviceBinder.getGetrestService());

                waitLock.notifyAll();
            }
        }

        public void onServiceDisconnected(final ComponentName componentName) {

            synchronized (waitLock) {
                serviceBinder = null;

                Loggers.getClientLogger()
                .debug("Service unbound: {0}", serviceBinder.getGetrestService());

                waitLock.notifyAll();
            }
        }

        public GetrestService getService() {

            while (serviceBinder == null) {

                synchronized (waitLock) {

                    while (serviceBinder == null) {

                        try {
                            waitLock.wait();
                        } catch (final InterruptedException ex) {
                            throw new IllegalStateException("Awaiting for service binding interrupted",
                                                            ex);
                        }
                    }
                }
            }

            return serviceBinder.getGetrestService();
        }
    }

    private class CallerContextAdapter implements CallerContext {
        public Handler getHandler() {

            return getCallbackHandler();
        }
    }
}
