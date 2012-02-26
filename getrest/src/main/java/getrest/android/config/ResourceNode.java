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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ResourceNode {

    private static final Pattern SPLIT_PATTERN = Pattern.compile("[\\*\\?]");

    private String patternNode;

    private Pattern pattern;

    private Map<String, ResourceNode> childMap = new HashMap<String, ResourceNode>();

    private ResourceContextContributor[] contributors = new ResourceContextContributor[0];

    /**
     * Constructs root node.
     */
    ResourceNode() {
    }

    ResourceNode(final String patternNode) {
        this.patternNode = patternNode;
        this.pattern = toPattern(this.patternNode);
    }

    private Pattern toPattern(final String pattern) {
        final StringBuilder sb = new StringBuilder();
        sb.append('^');

        final Matcher m = SPLIT_PATTERN.matcher(pattern);
        int lastEnd = 0;
        while (m.find()) {
            if (lastEnd < m.start()) {
                sb.append(Pattern.quote(pattern.substring(lastEnd, m.start())));
            }

            final String matched = pattern.substring(m.start(), m.end());
            if ("*".equals(matched)) {
                sb.append(".*");
            } else if ("?".equals(matched)) {
                sb.append(".");
            } else {
                throw new IllegalStateException("Wildcard character does not match * nor ?");
            }
            lastEnd = m.end();
        }

        if (lastEnd < pattern.length()) {
            sb.append(Pattern.compile(pattern.substring(lastEnd)));
        }

        sb.append('$');

        return Pattern.compile(sb.toString());
    }

    public void configure(final ResourceContextContributor... contributors) {
        this.contributors = new ResourceContextContributor[contributors.length];
        System.arraycopy(contributors, 0, this.contributors, 0, this.contributors.length);
    }

    public ResourceNode findChild(final String patternNode) {
        return childMap.get(patternNode);
    }

    public ResourceNode matchChild(final String node) {
        if (childMap.isEmpty()) {
            return null;
        }

        final Iterator<ResourceNode> it = childMap.values().iterator();
        ResourceNode matchedNode = null;
        while (matchedNode == null && it.hasNext()) {
            final ResourceNode childNode = it.next();
            if (childNode.matches(node)) {
                matchedNode = childNode;
            }
        }

        return matchedNode;
    }

    public boolean matches(final String node) {
        return pattern.matcher(node).matches();
    }

    public ResourceNode addChild(final String patternNode) {
        final ResourceNode child = new ResourceNode(patternNode);
        childMap.put(patternNode, child);
        return child;
    }

    @Override
    public String toString() {
        return "ResourceNode{" + patternNode + '}';
    }

    public void contribute(final ResourceContextContribution contribution) {
        for (ResourceContextContributor contributor : contributors) {
            contributor.contribute(contribution);
        }
    }

}
