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
package getrest.android.client;

import getrest.android.core.Error;
import getrest.android.core.ErrorState;
import getrest.android.core.Loggers;
import getrest.android.core.Request;
import getrest.android.core.RequestManager;
import getrest.android.core.RequestStatus;

import getrest.android.util.Logger;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryRequestManager implements RequestManager {

    private static final Logger LOGGER = Loggers.getRequestManagerLogger();
    private final Map<String, RequestStatus> stateMap = new ConcurrentHashMap<String, RequestStatus>();
    private final Map<String, WeakValue<Request>> requestMap = new HashMap<String, WeakValue<Request>>();
    private final Map<String, WeakValue<Object>> responseMap = new HashMap<String, WeakValue<Object>>();
    private final ReferenceQueue<Object> gcQueue = new ReferenceQueue<Object>();
    private final Map<String, CandidateEntry> undeadQueue = new HashMap<String, CandidateEntry>();
    private static final long MAX_UNDEAD_AGE = 30000L;

    private static class WeakValue<T> extends SoftReference<T> {

        private String requestId;

        public WeakValue(final String requestId, final T referent, final ReferenceQueue<?super T> q) {
            super(referent, q);
            this.requestId = requestId;
        }

        public String getRequestId() {

            return requestId;
        }
    }

    private class CleanupWorker implements Runnable {
        public void run() {

            while (true) {
                processQueue();
                processUndeadQueue();
                Thread.yield();
            }
        }
    }

    private void processQueue() {

        try {

            WeakValue<Object> ref;

            while ((ref = (WeakValue<Object>) gcQueue.remove()) != null) {

                final long now = System.currentTimeMillis();

                synchronized (undeadQueue) {

                    final Object obj = ref.get();

                    if (obj instanceof Request) {

                        final String requestId = ref.getRequestId();
                        final CandidateEntry candidateEntry = requireCandidateEntry(requestId);
                        candidateEntry.setRequest((Request) obj);
                        candidateEntry.setLastAccessedTime(now);
                    } else {

                        final String requestId = ref.getRequestId();
                        final CandidateEntry candidateEntry = requireCandidateEntry(requestId);
                        candidateEntry.setResponse(obj);
                        candidateEntry.setLastAccessedTime(now);
                    }
                }
            }
        } catch (final InterruptedException e) {
            LOGGER.error("Interrupted exception", e);
        }
    }

    private void processUndeadQueue() {

        final long maxUndeadAge = System.currentTimeMillis() - MAX_UNDEAD_AGE;

        synchronized (undeadQueue) {

            final Set<String> enqueuedIds = new HashSet<String>();

            for (final Map.Entry<String, CandidateEntry> entry : undeadQueue.entrySet()) {

                final CandidateEntry candidateEntry = entry.getValue();

                if (candidateEntry.getLastAccessedTime() < maxUndeadAge) {
                    enqueuedIds.add(entry.getKey());
                }
            }

            if (!enqueuedIds.isEmpty()) {
                undeadQueue.entrySet().removeAll(enqueuedIds);

                synchronized (requestMap) {
                    requestMap.keySet().removeAll(enqueuedIds);
                }

                synchronized (responseMap) {
                    responseMap.keySet().removeAll(enqueuedIds);
                }
            }
        }
    }

    private CandidateEntry requireCandidateEntry(final String requestId) {

        final CandidateEntry candidateEntry;

        synchronized (undeadQueue) {

            if (!undeadQueue.containsKey(requestId)) {
                candidateEntry = new CandidateEntry();
                undeadQueue.put(requestId, candidateEntry);
            } else {
                candidateEntry = undeadQueue.get(requestId);
            }
        }

        return candidateEntry;
    }

    public InMemoryRequestManager() {
        new Thread(new CleanupWorker()).start();
    }

    public void persistRequest(final Request request) {

        final String requestId = request.getRequestId();

        synchronized (requestMap) {

            if (stateMap.containsKey(requestId)) {
                throw new IllegalStateException(
                    "Request with id '" + requestId + "' is already registered");
            }

            requestMap.put(requestId, new WeakValue<Request>(requestId, request, gcQueue));
        }
    }

    public Request getRequest(final String requestId) {

        final WeakValue<Request> entry = requestMap.get(requestId);

        return (entry != null) ? entry.get() : null;
    }

    public void persistResponse(final String requestId, final Object response) {

        synchronized (requestMap) {

            final Request request = getRequest(requestId);

            if (request == null) {
                throw new IllegalStateException("Request must be acknowledged prior to response");
            }

            responseMap.put(requestId, new WeakValue<Object>(requestId, response, gcQueue));
        }
    }

    public Object getResponse(final String requestId) {

        final WeakValue<Object> entry = responseMap.get(requestId);

        return (entry == null) ? null : entry.get();
    }

    public void updateRequestStatus(final String requestId, final RequestStatus status) {

        synchronized (stateMap) {

            final Request request = getRequest(requestId);

            if (request == null) {
                throw new IllegalStateException("Request must be acknowledged prior to response");
            }

            stateMap.put(requestId, status);
        }
    }

    public RequestStatus getRequestStatus(final String requestId) {

        return stateMap.get(requestId);
    }

    public void updateRequestStatus(final String requestId, final ErrorState errorState,
                                    final String message) {

        final Request request = getRequest(requestId);

        if (request == null) {
            throw new IllegalStateException("Request is not acknowledged: " + requestId);
        }

        updateRequestStatus(requestId, RequestStatus.ERROR);

        final Error error = new Error();
        error.setErrorState(errorState);
        error.setMessage(message);
    }

    private class CandidateEntry {

        private Request request;
        private Object response;
        private long lastAccessedTime;

        public Request getRequest() {

            return request;
        }

        public void setRequest(final Request request) {
            this.request = request;
        }

        public Object getResponse() {

            return response;
        }

        public void setResponse(final Object response) {
            this.response = response;
        }

        public long getLastAccessedTime() {

            return lastAccessedTime;
        }

        public void setLastAccessedTime(final long lastAccessedTime) {
            this.lastAccessedTime = lastAccessedTime;
        }
    }
}
