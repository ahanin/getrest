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

import getrest.android.util.Logger;
import getrest.android.util.LoggerFactory;

public class Loggers {

    private static Logger SERVICE_LOGGER = LoggerFactory.getLogger("getrest.service");
    private static Logger CLIENT_LOGGER = LoggerFactory.getLogger("getrest.client");
    private static Logger REQUEST_MANAGER_LOGGER = LoggerFactory.getLogger("getrest.reqman");
    private static Logger CONFIG_LOGGER = LoggerFactory.getLogger("getrest.config");

    public static Logger getServiceLogger() {
        return SERVICE_LOGGER;
    }

    public static Logger getClientLogger() {
        return CLIENT_LOGGER;
    }

    public static Logger getRequestManagerLogger() {
        return REQUEST_MANAGER_LOGGER;
    }

    public static Logger getConfigLogger() {
        return CONFIG_LOGGER;
    }
}
