/*
 * Copyright 2013 Alexey Hanin
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

import getrest.android.client.RequestCallback;

import getrest.android.core.Request;

import getrest.android.util.Preconditions;

public class RequestFutureSupport<R extends Request<V>, V> {

    private RequestTuple<R> requestTuple;
    private RequestCallback<R> requestCallback;
    private boolean hasGottenResult;
    private boolean isFinished;
    private byte notificationState = 0;
    private byte pendingNotificationState = 0;
    private V result;
    private Throwable exception;

    private static final byte NOTIFICATION_STATE_PENDING = 1;
    private static final byte NOTIFICATION_STATE_EXECUTING = 2;
    private static final byte NOTIFICATION_STATE_FINISHED = 3;

    private final Object syncLock = new Object();

    public void fireOnPending() {
        runNotificationCycle(NOTIFICATION_STATE_PENDING);
    }

    private void runNotificationCycle(final byte targetNotificationState) {
        runNotificationCycle(targetNotificationState, this.result, this.exception);
    }

    private void runNotificationCycle(final byte targetNotificationState, final V result,
                                      final Throwable exception) {

        synchronized (syncLock) {
            Preconditions.checkState(this.notificationState < targetNotificationState,
                                     "Inconsistent notification chain state");

            if (NOTIFICATION_STATE_PENDING <= targetNotificationState) {

                if (requestCallback != null) {
                    notificationState = NOTIFICATION_STATE_PENDING;

                    requestTuple.getCallerContext().getHandler().post(new Runnable() {
                            public void run() {
                                requestCallback.onPending(requestTuple.getRequest());
                            }
                        });
                } else {
                    pendingNotificationState = NOTIFICATION_STATE_PENDING;
                }
            }

            if (NOTIFICATION_STATE_EXECUTING <= targetNotificationState) {

                if (requestCallback != null) {
                    notificationState = NOTIFICATION_STATE_EXECUTING;
                    requestTuple.getCallerContext().getHandler().post(new Runnable() {
                            public void run() {
                                requestCallback.onExecuting(requestTuple.getRequest());
                            }
                        });
                } else {
                    pendingNotificationState = NOTIFICATION_STATE_EXECUTING;
                }
            }

            if (NOTIFICATION_STATE_FINISHED <= targetNotificationState) {
                this.result = result;
                this.exception = exception;

                if (requestCallback != null) {
                    Preconditions.checkState(!((this.result != null) && (this.exception != null)),
                                             "result and exception cannot present at the same time");

                    this.notificationState = NOTIFICATION_STATE_FINISHED;

                    if (this.result != null) {
                        requestTuple.getCallerContext().getHandler().post(new Runnable() {
                                public void run() {
                                    requestCallback.onCompleted(requestTuple.getRequest());
                                }
                            });
                    } else if (this.exception != null) {
                        requestTuple.getCallerContext().getHandler().post(new Runnable() {
                                public void run() {
                                    requestCallback.onError(requestTuple.getRequest());
                                }
                            });
                    }
                } else {
                    pendingNotificationState = NOTIFICATION_STATE_FINISHED;
                }
            }

            syncLock.notifyAll();
        }
    }

    public void fireOnExecuting() {
        runNotificationCycle(NOTIFICATION_STATE_EXECUTING);
    }

    public void fireOnError(final Throwable ex) {
        runNotificationCycle(NOTIFICATION_STATE_FINISHED, null, ex);
    }

    public void fireOnCompleted(final V result) {
        saveResult(result);

        runNotificationCycle(NOTIFICATION_STATE_FINISHED, result, null);
    }

    public void setRequestCallback(final RequestCallback<R> requestCallback) {
        this.requestCallback = requestCallback;

        if (notificationState < pendingNotificationState) {
            runNotificationCycle(pendingNotificationState);
        }
    }

    private void saveResult(final V result) {

        synchronized (syncLock) {
            Preconditions.checkState(!hasGottenResult && !isFinished,
                                     "result has already been set or the request has ended prematurely");

            this.result = result;

            isFinished = true;
            hasGottenResult = true;

            syncLock.notifyAll();
        }
    }

    public V getResult() {

        if (!hasGottenResult && !isFinished) {

            synchronized (syncLock) {

                while (!hasGottenResult && !isFinished) {

                    try {
                        syncLock.wait();
                    } catch (final InterruptedException ex) {
                        throw new IllegalStateException("Awaiting for a result has been interrupted",
                                                        ex);
                    }
                }
            }
        }

        return result;
    }

    public void setRequestTuple(final RequestTuple requestTuple) {
        this.requestTuple = requestTuple;
    }

    public RequestTuple getRequestTuple() {

        return requestTuple;
    }
}
