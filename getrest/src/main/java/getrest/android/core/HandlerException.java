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

import getrest.android.exception.GetrestException;

public class HandlerException extends GetrestException {

    private static final long serialVersionUID = -7091817046265184580L;

    public HandlerException(final String message) {
        super(message);
    }

    public HandlerException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
