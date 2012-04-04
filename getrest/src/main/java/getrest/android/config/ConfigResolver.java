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

import android.app.Application;
import android.content.Context;

import java.util.HashMap;

public class ConfigResolver {

    private static final ConfigResolver INSTANCE = new ConfigResolver();

    public static ConfigResolver getInstance() {
        return INSTANCE;
    }

    private final HashMap<Context, Config> cache = new HashMap<Context, Config>(1);

    public Config obtainConfig(Context context) {
        final Context applicationContext = context.getApplicationContext();
        if (!cache.containsKey(applicationContext)) {
            synchronized (cache) {
                if (!cache.containsKey(applicationContext)) {
                    if (!(applicationContext instanceof HasConfig)) {
                        throw new IllegalStateException("Unable to find configuration. Does your Android " +
                                Application.class.getSimpleName() + " implement " + HasConfig.class.getName());
                    }
                    synchronized (applicationContext) {
                        cache.put(applicationContext, ((HasConfig) applicationContext).getGetrestConfig());
                    }
                }
            }
        }
        return cache.get(applicationContext);
    }

}
