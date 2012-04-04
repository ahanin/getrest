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

import android.net.Uri;
import getrest.android.resource.ResourceContext;
import getrest.android.resource.ResourceContextImpl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Config {

    private ResourceNode root = new ResourceNode();

    {
        root.configure(new DefaultContributor(this));
    }

    private final Map<ResourcePath, ResourceContext> contextCache = new LinkedHashMap<ResourcePath, ResourceContext>() {
        @Override
        protected boolean removeEldestEntry(final Map.Entry<ResourcePath, ResourceContext> eldest) {
            return size() > getCacheSize();
        }
    };

    private final Map<String, ResourcePath> resourcePathCache = new LinkedHashMap<String, ResourcePath>() {
        @Override
        protected boolean removeEldestEntry(final Map.Entry<String, ResourcePath> eldest) {
            return size() > getCacheSize();
        }
    };

    private int getCacheSize() {
        // TODO work out cache size formula
        return 10;
    }

    /**
     * Configure resource settings. Resource settings takes precedence over global settings configured by
     * {@link #configure(ResourceContextContributor...)}
     *
     * @param uriPattern   resource url pattern
     * @param contributors configuration settings contributors
     * @return configuration object for further access
     */
    public Config configure(final String uriPattern, ResourceContextContributor... contributors) {
        final ResourceNode resourceNode = obtainConfigNode(uriPattern);
        resourceNode.configure(contributors);
        return this;
    }

    private ResourceNode obtainConfigNode(final String uriPattern) {
        final ArrayList<String> patternNodes = toUriNodes(uriPattern);

        ResourceNode currentNode = root;
        for (String patternNode : patternNodes) {
            final ResourceNode existingNode = root.findChild(patternNode);
            currentNode = existingNode != null ? existingNode : currentNode.addChild(patternNode);
        }

        return currentNode;
    }

    private ArrayList<String> toUriNodes(final String uriString) {
        final Uri uri = Uri.parse(uriString);
        return toUriNodes(uri);
    }

    private ArrayList<String> toUriNodes(final Uri uri) {
        final String scheme = uri.getScheme();
        final List<String> pathSegments = uri.getPathSegments();

        final ArrayList<String> patternNodes = new ArrayList<String>(pathSegments.size() + 2);
        patternNodes.add(scheme);
        patternNodes.add(uri.getAuthority());
        patternNodes.addAll(patternNodes);

        return patternNodes;
    }

    /**
     * Configure global settings.
     *
     * @param contributors configuration settings contributors
     * @return configuration object for further access
     */
    public Config configure(ResourceContextContributor... contributors) {
        root.configure(contributors);
        return this;
    }

    public ResourceContext getResourceContext(Uri uri) {
        final String key = toResourceKey(uri);

        ResourcePath resourcePath;
        if ((resourcePath = resourcePathCache.get(key)) == null) {
            synchronized (resourcePathCache) {
                if ((resourcePath = resourcePathCache.get(key)) == null) {
                    resourcePath = new ResourcePath();
                    final ArrayList<String> uriNodes = toUriNodes(uri);
                    final Iterator<String> iUriNodes = uriNodes.iterator();

                    ResourceNode currentNode = root;
                    do {
                        resourcePath.add(currentNode);
                    } while (iUriNodes.hasNext() && (currentNode = currentNode.matchChild(iUriNodes.next())) != null);

                    resourcePathCache.put(key, resourcePath);
                }
            }
        }

        return getResourceContext(resourcePath);
    }

    private ResourceContext getResourceContext(final ResourcePath resourcePath) {
        ResourceContext resourceContext;
        if ((resourceContext = contextCache.get(resourcePath)) == null) {
            synchronized (contextCache) {
                if ((resourceContext = contextCache.get(resourcePath)) == null) {
                    resourceContext = createResourceContext(resourcePath);
                    contextCache.put(resourcePath, resourceContext);
                }
            }
        }
        return resourceContext;
    }

    private ResourceContext createResourceContext(final ResourcePath resourcePath) {
        final ResourceContextImpl resourceContext = new ResourceContextImpl();
        for (ResourceNode resourceNode : resourcePath) {
            resourceNode.contribute(resourceContext);
        }
        return resourceContext;
    }

    private String toResourceKey(final Uri uri) {
        // TODO enhance key
        return new StringBuffer().append(uri.getScheme()).append("://").append(uri.getAuthority())
                .toString();
    }

}
