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

import getrest.android.Getrest;

import getrest.android.config.Config;
import getrest.android.config.HasConfig;

import getrest.android.core.impl.RequestManagerImpl;
import getrest.android.persistence.Storage;

import getrest.android.persistence.impl.InMemoryRequestStorage;
import getrest.android.util.InstanceProvider;
import getrest.android.util.Lists;

import java.util.HashMap;
import java.util.List;

public class GetrestRuntime {
    private static final HashMap<Context, GetrestRuntime> cache = new HashMap<Context, GetrestRuntime>(
        1);
    private RequestManager requestManager = new RequestManagerImpl(
        new InstanceProvider<List<Storage>>(Lists.<Storage>immutableList(new InMemoryRequestStorage())));
    private Config config;

    public static GetrestRuntime getInstance(final Context context) {
        final Context applicationContext = context.getApplicationContext();

        if (!cache.containsKey(applicationContext)) {
            synchronized (cache) {
                if (!cache.containsKey(applicationContext)) {
                    final Config config;

                    if (applicationContext instanceof HasConfig) {
                        config = ((HasConfig) applicationContext).getGetrestConfig();
                    } else {
                        config = Getrest.newConfigBuilder().build();
                    }

                    cache.put(applicationContext, new GetrestRuntime(config));
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
