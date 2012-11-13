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
package getrest.android.runtime;

import android.app.Application;

import android.content.Context;

import android.net.Uri;

import getrest.android.client.InMemoryRequestManager;

import getrest.android.config.Config;
import getrest.android.config.HasConfig;
import getrest.android.config.Resource;
import getrest.android.config.ResourceMethod;

import getrest.android.core.Request;

import getrest.android.ext.MessageBodyWriter;

import getrest.android.request.RequestContext;
import getrest.android.request.RequestManager;

import getrest.android.resource.Packer;
import getrest.android.resource.ResourceContext;
import getrest.android.resource.ResourceContextImpl;

import getrest.android.service.Representation;

import getrest.android.util.Collections2;
import getrest.android.util.Lists;
import getrest.android.util.Preconditions;
import getrest.android.util.Predicate;
import getrest.android.util.UriUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetrestRuntime {
    private static final HashMap<Context,GetrestRuntime> cache = new HashMap<Context,GetrestRuntime>(1);
    private RequestManager requestManager = new InMemoryRequestManager();

    public static GetrestRuntime getInstance(final Context context) {
        final Context applicationContext = context.getApplicationContext();

        if (!cache.containsKey(applicationContext)) {
            synchronized (cache) {
                if (!cache.containsKey(applicationContext)) {
                    if (!(applicationContext instanceof HasConfig)) {
                        throw new IllegalStateException(
                            "Unable to find configuration. Does your Android " +
                            Application.class.getSimpleName() + " implement " +
                            HasConfig.class.getName());
                    }

                    synchronized (applicationContext) {
                        cache.put(applicationContext,
                            new GetrestRuntime(
                                ((HasConfig) applicationContext).getGetrestConfig()));
                    }
                }
            }
        }

        return cache.get(applicationContext);
    }

    private static final Pattern URI_PARAM_PATTERN = Pattern.compile(
            "\\{([^\\}]+?)\\}");
    private final List<ResourceMethodTuple> index = Lists.newLinkedList();

    private GetrestRuntime(final Config config) {
        buildIndices(config);
    }

    private void buildIndices(final Config config) {
        for (final getrest.android.config.Application application : config.getApplications()) {
            for (Resource resource : application.getResources()) {
                for (ResourceMethod method : resource.getMethods()) {
                    index.add(buildResourceTuple(application, resource, method));
                }
            }
        }
    }

    private ResourceMethodTuple buildResourceTuple(
        final getrest.android.config.Application application,
        final Resource resource, final ResourceMethod method) {
        final StringBuffer patternRegexp = new StringBuffer();
        final String uri = Uri.encode(UriUtils.gluePath(resource.getPath(),
                    method.getPath())).replace(Uri.encode("{"), "{")
                              .replace(Uri.encode("}"), "}");

        final Matcher m = URI_PARAM_PATTERN.matcher(uri);
        final List<String> params = Lists.newArrayList();

        while (m.find()) {
            m.appendReplacement(patternRegexp, "([^/]+?)");
            params.add(m.group(1));
        }

        m.appendTail(patternRegexp);

        return new ResourceMethodTuple(uri,
            Pattern.compile(patternRegexp.toString()), params, resource, method);
    }

    /**
     * TODO implement {@link #getRequestContext(getrest.android.core.Request)}
     *
     * @param request
     *
     * @return
     */
    public RequestContext getRequestContext(final Request request) {
        final Collection<ResourceMethodTuple> tuples = Collections2.filter(index,
                new ResourceMethodTuplePredicate(request));

        Preconditions.checkState(!tuples.isEmpty(), "No matching methods found");

        final DefaultRequestContext requestContext = new DefaultRequestContext();

        requestContext.setRuntime(this);
        requestContext.setResourceMethod(tuples.iterator().next().getMethod());

        return requestContext;
    }

    public RequestManager getRequestManager() {
        return requestManager;
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

    public static class DefaultRequestContext implements RequestContext {
        private GetrestRuntime runtime;
        private Packer packer;
        private MessageBodyWriter messageBodyWriter;
        private ResourceContextImpl resourceContext;
        private ResourceMethod resourceMethod;

        public GetrestRuntime getRuntime() {
            return runtime;
        }

        public void setRuntime(final GetrestRuntime runtime) {
            this.runtime = runtime;
        }

        public ResourceContext getResourceContext() {
            return resourceContext;
        }

        public void setPacker(final Packer packer) {
            this.packer = packer;
        }

        public Packer getPacker() {
            return this.packer;
        }

        public void setMessageBodyWriter(
            final MessageBodyWriter<Representation> messageBodyWriter) {
            this.messageBodyWriter = messageBodyWriter;
        }

        public <T> MessageBodyWriter<Representation>getMessageBodyWriter() {
            return this.messageBodyWriter;
        }

        public RequestManager getRequestManager() {
            return resourceContext.getRequestManager();
        }

        public ResourceMethod getResourceMethod() {
            return resourceMethod;
        }

        public void setResourceMethod(final ResourceMethod resourceMethod) {
            this.resourceMethod = resourceMethod;
        }
    }

    private static class ResourceMethodTuplePredicate implements Predicate<ResourceMethodTuple> {
        private final Request request;

        public ResourceMethodTuplePredicate(final Request request) {
            this.request = request;
        }

        public boolean accept(final ResourceMethodTuple tuple) {
            return tuple.getMethod().getMethods().contains(request.getMethod()) &&
            tuple.getPattern().matcher(Uri.encode(request.getUri().toString()))
                 .matches();
        }
    }
}
