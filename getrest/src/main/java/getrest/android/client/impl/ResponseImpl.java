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
import getrest.android.core.Response;
import getrest.android.core.ResponseParcelable;
import getrest.android.exception.GetrestRuntimeException;
import getrest.android.core.Request;

class ResponseImpl<T> extends Response<T> {

    private String requestId;
    private Request request;
    private ResponseParcelable responseParcelable;

    private boolean isPendingFired;
    private boolean isExecutingFired;
    private boolean isFinishedFired;
    private boolean isErrorFired;

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

    public void finish(ResponseParcelable responseParcelable) {
        synchronized (this) {
            this.responseParcelable = responseParcelable;
            this.isFinished = true;
            synchronized (lock) {
                lock.notifyAll();
            }
        }
    }

    public T getEntity() {
        return (T) getResponseParcelable().getEntity().unpack();
    }

    private ResponseParcelable getResponseParcelable() {
        waitFinished();
        return responseParcelable;
    }

    private void waitFinished() {
        if (!isFinished) {
            synchronized (this) {
                if (!isFinished) {
                    synchronized (lock) {
                        try {
                            lock.wait();
                        } catch (InterruptedException ex) {
                            throw new GetrestRuntimeException("Exception while trying to wait for response", ex);
                        }
                    }
                }
            }
        }
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

    public void fireFinished(final ResponseParcelable responseParcelable) {
        synchronized (this) {
            if (callback != null && !isFinishedFired) {
                doFireFinished(responseParcelable);
            }
        }
    }

    private void doFireFinished(final ResponseParcelable responseParcelable) {
        try {
            if (!isExecutingFired) {
                doFireExecuting();
            }
        } finally {
            try {
                callback.onFinished(responseParcelable);
            } finally {
                isFinishedFired = true;
            }
        }
    }

    public void fireError() {
        synchronized (this) {
            if (callback != null && !isErrorFired) {
                doFireError();
            }
        }
    }

    private void doFireError() {
        try {
            callback.onError(request);
        } finally {
            isErrorFired = true;
        }
    }

}
