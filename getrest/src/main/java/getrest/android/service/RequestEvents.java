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
package getrest.android.service;

public final class RequestEvents {

    public static final int PENDING = 1;
    public static final int EXECUTING = 2;
    public static final int FINISHED = 3;

    private RequestEvents() {
    }

    public static String getEventName(final int eventType) {
        switch (eventType) {
            case PENDING:
                return "PENDING";
            case EXECUTING:
                return "EXECUTING";
            case FINISHED:
                return "FINISHED";
            default:
                return null;
        }
    }

}
