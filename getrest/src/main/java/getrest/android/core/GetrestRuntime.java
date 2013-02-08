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
package getrest.android.core;

import android.content.Context;

import getrest.android.client.InMemoryRequestManager;

import getrest.android.config.Config;
import getrest.android.config.HasConfig;

import java.util.HashMap;

public class GetrestRuntime {

    private static final HashMap<Context, GetrestRuntime> cache = new HashMap<Context, GetrestRuntime>(
        1);
    private RequestManager requestManager = new InMemoryRequestManager();
    private Config config;

    public static GetrestRuntime getInstance(final Context context) {

        final Context applicationContext = context.getApplicationContext();

        if (!cache.containsKey(applicationContext)) {

            synchronized (cache) {

                if (!cache.containsKey(applicationContext)) {

                    if (!(applicationContext instanceof HasConfig)) {
                        throw new IllegalStateException(
                            "Unable to find configuration. Does your Android "
                            + Application.class.getSimpleName() + " implement "
                            + HasConfig.class.getName());
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

    private GetrestRuntime(final Config config) {
        this.config = config;
    }

    public RequestManager getRequestManager() {

        return requestManager;
    }

}
