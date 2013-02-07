/*
 * Copyright 2013 Alexey Hanin
 *
 * Licensed under the Apache License, Version 2.0 (the "License     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package getrest.android.core;

import getrest.android.client.RequestExecutor;

import getrest.android.util.Preconditions;

public class RunnableExecutor implements RequestExecutor {
    public RunnableExecutor() {}

    public void execute(final Request request) {
        Preconditions.checkArgNotNull(request, "request");
        Preconditions.checkState(request instanceof Runnable, "Request must be Runnable");
        ((Runnable) request).run();
    }
}
