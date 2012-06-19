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

import getrest.android.client.RequestCallback;
import getrest.android.client.RequestFuture;
import getrest.android.exception.GetrestRuntimeException;
import getrest.android.core.Request;
import getrest.android.core.Response;

class RequestFutureImpl implements RequestFuture {

    private String requestId;
    private Request request;
    private Response response;

    private boolean isPendingFired;
    private boolean isExecutingFired;
    private boolean isFinishedFired;

    private boolean isFinished;

    private RequestCallback callback;

    private final Object lock = new Object();

    public void setRequestId(final String requestId) {
        this.requestId = requestId;
    }

    public void setRequest(final Request request) {
        this.request = request;
    }

    public boolean isFinished() {
        synchronized (this) {
            return isFinished;
        }
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestCallback(final RequestCallback requestCallback) {
        this.callback = requestCallback;
    }

    public void finish(Response response) {
        synchronized (this) {
            this.response = response;
            this.isFinished = true;
            synchronized (lock) {
                lock.notifyAll();
            }
        }
    }

    public Response get() {
        synchronized (this) {
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException ex) {
                    throw new GetrestRuntimeException("Exception while trying to wait for response", ex);
                }
            }
        }
        return response;
    }

    public void firePending() {
        synchronized (this) {
            if (callback != null && !isPendingFired) {
                doFirePending();
            }
        }
    }

    private void doFirePending() {
        try {
            callback.onPending(request);
        } finally {
            isPendingFired = true;
        }
    }

    public void fireExecuting() {
        synchronized (this) {
            if (callback != null && !isExecutingFired) {
                doFireExecuting();
            }
        }
    }

    private void doFireExecuting() {
        try {
            if (!isPendingFired) {
                doFirePending();
            }
        } finally {
            try {
                callback.onExecuting(request);
            } finally {
                isExecutingFired = true;
            }
        }
    }

    public void fireFinished(final Response response) {
        synchronized (this) {
            if (callback != null && !isFinishedFired) {
                doFireFinished(response);
            }
        }
    }

    private void doFireFinished(final Response response) {
        try {
            if (!isExecutingFired) {
                doFireExecuting();
            }
        } finally {
            try {
                callback.onFinished(response);
            } finally {
                isFinishedFired = true;
            }
        }
    }

}
