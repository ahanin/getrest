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
package getrest.android.http;

import android.net.Uri;
import android.util.Pair;
import getrest.android.core.Application;
import getrest.android.core.PackerResolver;
import getrest.android.core.Request;
import getrest.android.core.RequestSupport;
import getrest.android.core.ResourceMethodResolver;
import getrest.android.http.ext.MessageBodyReader;
import getrest.android.http.ext.MessageBodyWriter;
import getrest.android.util.Collections2;
import getrest.android.util.Lists;
import getrest.android.util.Preconditions;
import getrest.android.util.Predicate;
import getrest.android.util.Sets;
import getrest.android.util.UriUtils;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class RestfulApplication implements Application, ResourceMethodResolver, PackerResolver {

    private final Set<MessageBodyWriter> messageBodyWriters = Sets.newHashSet();
    private final Set<MessageBodyReader> messageBodyReaders = Sets.newHashSet();
    private final Set<Pair<Class, Packer>> classToPackerPairs = Sets.newHashSet();
    private final Set<Resource> resources = Sets.newHashSet();

    private static final Pattern URI_PARAM_PATTERN = Pattern.compile(
            "(?:(\\{([^\\}]+?)\\})|(\\*))");
    private final List<ResourceMethodTuple> index = Lists.newLinkedList();

    RestfulApplication(final Set<MessageBodyWriter> messageBodyWriters,
                       final Set<MessageBodyReader> messageBodyReaders,
                       final Set<Resource> resources) {
        this.messageBodyWriters.addAll(messageBodyWriters);
        this.messageBodyReaders.addAll(messageBodyReaders);
        this.resources.addAll(resources);

        buildIndices();
    }

    public static ApplicationBuilder newApplicationBuilder() {
        return new ApplicationBuilder();
    }

    public ResourceMethodResolver getResourceMethodResolver() {
        return this;
    }

    public PackerResolver getPackerResolver() {
        return this;
    }

    public ResourceMethod getResourceMethod(final HttpRequest httpRequest) {
        final Collection<ResourceMethodTuple> tuples = Collections2.filter(index,
                new ResourceMethodTuplePredicate(httpRequest));
        return tuples.isEmpty() ? null : tuples.iterator().next().getMethod();
    }

    public Packer getPacker(Class entityType) {

        Preconditions.checkArgNotNull(entityType, "Entity cannot be null");

        for (Pair<Class, Packer> classToPackerPair : classToPackerPairs) {
            if (classToPackerPair.first.isAssignableFrom(entityType)) {
                // TODO calculate best choice based on hierarchy proximity
                // TODO cache results
                return classToPackerPair.second;
            }
        }

        return null;
    }

    private void buildIndices() {
        for (Resource resource : this.resources) {
            for (ResourceMethod method : resource.getMethods()) {
                index.add(buildResourceTuple(resource, method));
            }
        }
    }

    private ResourceMethodTuple buildResourceTuple(final Resource resource, final ResourceMethod method) {
        final StringBuffer patternRegexp = new StringBuffer();
        final String uri = Uri.encode(UriUtils.gluePath(resource.getPath(),
                method.getPath())).replace(Uri.encode("{"), "{")
                .replace(Uri.encode("}"), "}");

        final Matcher m = URI_PARAM_PATTERN.matcher(uri);
        final List<String> params = Lists.newArrayList();

        while (m.find()) {
            if (m.group(1) != null) { // {param}
                m.appendReplacement(patternRegexp, "([^/]+?)");
                params.add(m.group(1));
            } else if (m.group(3) != null) { // *
                m.appendReplacement(patternRegexp, ".*");
            }
        }

        m.appendTail(patternRegexp);
        patternRegexp.append('$');

        return new ResourceMethodTuple(uri,
                Pattern.compile(patternRegexp.toString()), params, resource, method);
    }

    public boolean isRequestSupported(final Request request) {
        return request instanceof HttpRequest;
    }

    public <T extends Request> RequestSupport<T> getRequestSupport(final T request) {
        throw new UnsupportedOperationException();
    }

    public static class ResourceMethodTuple {
        private final String uriTemplate;
        private final Pattern pattern;
        private final List<String> params;
        private final Resource resource;
        private final ResourceMethod method;

        public ResourceMethodTuple(final String uriTemplate,
                                   final Pattern pattern, final List<String> params,
                                   final Resource resource, final ResourceMethod method) {
            this.uriTemplate = uriTemplate;
            this.pattern = pattern;
            this.params = Lists.immutableList(params);
            this.resource = resource;
            this.method = method;
        }

        public String getUriTemplate() {
            return uriTemplate;
        }

        public Pattern getPattern() {
            return pattern;
        }

        public List<String> getParams() {
            return params;
        }

        public Resource getResource() {
            return resource;
        }

        public ResourceMethod getMethod() {
            return method;
        }
    }

    private static class ResourceMethodTuplePredicate implements Predicate<ResourceMethodTuple> {
        private final HttpRequest request;

        public ResourceMethodTuplePredicate(final HttpRequest request) {
            this.request = request;
        }

        public boolean accept(final ResourceMethodTuple tuple) {
            return tuple.getMethod().getMethods().contains(request.getMethod()) &&
                    tuple.getPattern().matcher(Uri.encode(request.getUri().toString()))
                            .matches();
        }
    }

    public static class ApplicationBuilder {
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
            return new RestfulApplication(messageBodyWriters, messageBodyReaders,
                resources);
        }
    }
}
