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

import getrest.android.core.Application;
import getrest.android.ext.MessageBodyReader;
import getrest.android.ext.MessageBodyWriter;
import getrest.android.util.Sets;

import java.util.Set;

public interface Config {

    Set<Application> getApplications();

    public static class ConfigBuilder {

        private Set<Application> applications = Sets.newHashSet();

        public void addApplication(final Application application) {
            this.applications.add(application);
        }

        public Config build() {
            return new GenericConfig(applications);
        }

    }

    class ApplicationBuilder {
        private Set<MessageBodyWriter> messageBodyWriters = Sets.newHashSet();
        private Set<MessageBodyReader> messageBodyReaders = Sets.newHashSet();
        private Set<Resource> resources = Sets.newHashSet();

        public ApplicationBuilder addMessageBodyWriter(
            final MessageBodyWriter messageBodyWriter) {
            this.messageBodyWriters.add(messageBodyWriter);

            return this;
        }

        public ApplicationBuilder addMessageBodyReader(
            final MessageBodyReader messageBodyReader) {
            this.messageBodyReaders.add(messageBodyReader);

            return this;
        }

        public ApplicationBuilder addResource(final Resource resource) {
            this.resources.add(resource);

            return this;
        }

        public Application build() {
            return new GenericApplication(messageBodyWriters, messageBodyReaders,
                resources);
        }
    }
}
