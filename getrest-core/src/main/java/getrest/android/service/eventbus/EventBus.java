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
package getrest.android.service.eventbus;

import getrest.android.core.Loggers;

import getrest.android.event.Event;

import getrest.android.util.Preconditions;
import getrest.android.util.WorkerQueue;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class EventBus {

    private final EventWorker eventWorker = new EventWorker();
    private final Map<Class<?extends Event>, EventHandler> eventHandlerMap = new HashMap<Class<?extends Event>, EventHandler>();
    private WorkerQueue<Event> workerQueue = new WorkerQueue<Event>(
        new ConcurrentLinkedQueue<Event>(),
        eventWorker,
        10);

    {
        workerQueue.start();
    }

    public <E extends Event> void registerEventHandler(final Class<E> eventType,
                                                       final EventHandler<E> eventHandler) {
        eventHandlerMap.put(eventType, eventHandler);
    }

    private class EventWorker implements WorkerQueue.Worker<Event> {
        public void execute(final Event event) {
            Preconditions.checkState(event != null, "Event cannot be null");
            Preconditions.checkState(eventHandlerMap.containsKey(event.getClass()),
                                     "No handler assigned to the eventType {0}",
                                     event.getClass());
            Loggers.getServiceLogger().debug("broadcasting event: event={0}", event);
            eventHandlerMap.get(event.getClass()).handle(event);
        }
    }

    public void publishEvent(final Event event) {
        Preconditions.checkArgNotNull(event, "event");
        Loggers.getServiceLogger().debug("enqueued event: event={0}", event);
        workerQueue.add(event);
    }
}
