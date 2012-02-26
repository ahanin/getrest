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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ResourcePath implements Iterable<ResourceNode> {

    private List<ResourceNode> backedList = new LinkedList<ResourceNode>();

    public boolean add(final ResourceNode resourceNode) {
        return backedList.add(resourceNode);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final ResourcePath that = (ResourcePath) o;

        if (backedList != null ? !backedList.equals(that.backedList) : that.backedList != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return backedList != null ? backedList.hashCode() : 0;
    }

    public Iterator<ResourceNode> iterator() {
        return backedList.iterator();
    }
}
