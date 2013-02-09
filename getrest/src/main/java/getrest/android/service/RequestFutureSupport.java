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
import getrest.android.core.RequestStatus;

import getrest.android.util.Objects;
import getrest.android.util.Preconditions;

import java.util.HashMap;
import java.util.Map;

public class RequestFutureSupport<R extends Request<V>, V> {

    private static final int NOTIFICATION_STATE_PENDING = 1;
    private static final int NOTIFICATION_STATE_EXECUTING = 2;
    private static final int NOTIFICATION_STATE_FINISHED = 3;
    private static final Map<RequestStatus, Integer> requestStatusOrdinalMap = new HashMap<RequestStatus, Integer>();

    static {
        requestStatusOrdinalMap.put(RequestStatus.PENDING, NOTIFICATION_STATE_PENDING);
        requestStatusOrdinalMap.put(RequestStatus.EXECUTING, NOTIFICATION_STATE_EXECUTING);
        requestStatusOrdinalMap.put(RequestStatus.FINISHED, NOTIFICATION_STATE_FINISHED);
        requestStatusOrdinalMap.put(RequestStatus.ERROR, NOTIFICATION_STATE_FINISHED);
    }

    private RequestTuple<R> requestTuple;
    private RequestCallback<R> requestCallback;
    private boolean hasGottenResult;
    private boolean isFinished;
    private V result;
    private Throwable exception;
    private final Object syncLock = new Object();
    private RequestStatus pendingRequestStatus;
    private RequestStatus currentNotifiedRequestStatus;

    private void runNotificationCycle(final RequestStatus requestStatus) {
        runNotificationCycle(requestStatus, this.result, this.exception);
    }

    private void runNotificationCycle(final RequestStatus requestStatus, final V result,
                                      final Throwable exception) {

        synchronized (syncLock) {

            final int targetOrdinal = getRequestStatusOrdinal(requestStatus);
            final int currentNotifiedStatusOrdinal = getRequestStatusOrdinal(
                this.currentNotifiedRequestStatus);
            final int pendingNotifiedStatusOrdinal = getRequestStatusOrdinal(this.pendingRequestStatus);

            if (targetOrdinal < Math.max(currentNotifiedStatusOrdinal, pendingNotifiedStatusOrdinal)) {

                // don't bother recording the progress of the state more than once
                return;
            }

            // all notifications must be stored and replayed once the callback is set
            if (requestCallback == null && targetOrdinal > pendingNotifiedStatusOrdinal) {

                if (requestStatus == RequestStatus.ERROR || requestStatus == RequestStatus.FINISHED) {
                    this.result = result;
                    this.exception = exception;
                }

                this.pendingRequestStatus = requestStatus;
            } else if (requestCallback != null && targetOrdinal > currentNotifiedStatusOrdinal) {

                for (int i = Math.max(currentNotifiedStatusOrdinal + 1, NOTIFICATION_STATE_PENDING);
                       i <= targetOrdinal; i++) {

                    switch (i) {

                        case NOTIFICATION_STATE_PENDING:
                            requestTuple.getCallerContext().getHandler().post(new Runnable() {
                                    public void run() {
                                        requestCallback.onPending(requestTuple.getRequest());
                                    }
                                });


                            break;

                        case NOTIFICATION_STATE_EXECUTING:
                            requestTuple.getCallerContext().getHandler().post(new Runnable() {
                                    public void run() {
                                        requestCallback.onExecuting(requestTuple.getRequest());
                                    }
                                });


                            break;

                        case NOTIFICATION_STATE_FINISHED:
                            this.result = result;
                            this.exception = exception;

                            Preconditions.checkState(!(this.result != null
                                                     && this.exception != null),
                                                     "result and exception cannot present at the same time");

                            if (requestStatus == RequestStatus.FINISHED) {
                                requestTuple.getCallerContext().getHandler().post(new Runnable() {
                                        public void run() {
                                            requestCallback.onCompleted(requestTuple.getRequest());
                                        }
                                    });
                            } else if (requestStatus == RequestStatus.ERROR) {
                                requestTuple.getCallerContext().getHandler().post(new Runnable() {
                                        public void run() {
                                            requestCallback.onError(requestTuple.getRequest());
                                        }
                                    });
                            }

                            break;
                    }
                }

                this.currentNotifiedRequestStatus = requestStatus;
            }

            syncLock.notifyAll();
        }
    }

    private int getRequestStatusOrdinal(final RequestStatus requestStatus) {

        return requestStatus == null ? -1
               : Objects.firstNotNull(requestStatusOrdinalMap.get(requestStatus), -1);
    }

    public void fireOnPending() {
        runNotificationCycle(RequestStatus.PENDING);
    }

    public void fireOnExecuting() {
        runNotificationCycle(RequestStatus.EXECUTING);
    }

    public void fireOnError(final Throwable ex) {
        runNotificationCycle(RequestStatus.ERROR, null, ex);
    }

    public void fireOnCompleted(final V result) {
        saveResult(result);

        runNotificationCycle(RequestStatus.FINISHED, result, null);
    }

    public void setRequestCallback(final RequestCallback<R> requestCallback) {

        synchronized (syncLock) {
            this.requestCallback = requestCallback;

            if (mustReplayNotifications()) {
                runNotificationCycle(pendingRequestStatus);
            }
        }
    }

    private boolean mustReplayNotifications() {

        return getRequestStatusOrdinal(pendingRequestStatus) > getRequestStatusOrdinal(
            currentNotifiedRequestStatus);
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
