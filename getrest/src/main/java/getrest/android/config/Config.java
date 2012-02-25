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

import java.util.ArrayList;
import java.util.List;

public class Config {

    private ResourceNode root = new ResourceNode();

    /**
     * Configure resource settings. Resource settings takes precedence over global settings configured by
     * {@link #configure(ConfigContributor...)}
     *
     * @param uriPattern   resource url pattern
     * @param contributors configuration settings contributors
     * @return configuration object for further access
     */
    public Config configure(final String uriPattern, ConfigContributor... contributors) {
        final ResourceNode resourceNode = obtainConfigNode(uriPattern);
        resourceNode.configure(contributors);
        return this;
    }

    private ResourceNode obtainConfigNode(final String uriPattern) {
        final Uri uri = Uri.parse(uriPattern);
        final String scheme = uri.getScheme();
        final List<String> pathSegments = uri.getPathSegments();

        final ArrayList<String> patternNodes = new ArrayList<String>(pathSegments.size() + 2);
        patternNodes.add(scheme);
        patternNodes.add(uri.getAuthority());
        patternNodes.addAll(patternNodes);

        ResourceNode currentNode = root;
        for (String patternNode : patternNodes) {
            final ResourceNode existingNode = root.findChild(patternNode);
            currentNode = existingNode != null ? existingNode : currentNode.addChild(patternNode);
        }

        return currentNode;
    }

    /**
     * Configure global settings.
     *
     * @param contributors configuration settings contributors
     * @return configuration object for further access
     */
    public Config configure(ConfigContributor... contributors) {
        root.configure(contributors);
        return this;
    }

    public ResourceContext getResourceContext(Uri uri) {
        // TODO implement search URI matching
        throw new UnsupportedOperationException();
    }

}
