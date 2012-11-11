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
package getrest.android.config;

import getrest.android.ext.MessageBodyReader;
import getrest.android.ext.MessageBodyWriter;

import java.util.Set;

class GenericApplication implements Application {
    private final Set<MessageBodyWriter> messageBodyWriters;
    private final Set<MessageBodyReader> messageBodyReaders;
    private final Set<Resource> resources;

    GenericApplication(final Set<MessageBodyWriter> messageBodyWriters,
        final Set<MessageBodyReader> messageBodyReaders,
        final Set<Resource> resources) {
        this.messageBodyWriters = messageBodyWriters;
        this.messageBodyReaders = messageBodyReaders;
        this.resources = resources;
    }

    public Set<MessageBodyWriter> getMessageBodyWriters() {
        return messageBodyWriters;
    }

    public Set<MessageBodyReader> getMessageBodyReaders() {
        return messageBodyReaders;
    }

    public Set<Resource> getResources() {
        return resources;
    }
}
