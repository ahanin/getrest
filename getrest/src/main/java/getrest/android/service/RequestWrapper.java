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

import android.content.Intent;
import getrest.android.request.Request;

/**
 * @author aha
 * @since 2012-01-13
 */
public class RequestWrapper {

    private Intent intent;

    private static final String EXTRA_REQUEST = "getrest.android.service.RequestWrapper.REQUEST";

    public RequestWrapper(final Intent intent) {
        this.intent = new Intent(intent);
    }

    public void setRequest(Request request) {
        intent.putExtra(EXTRA_REQUEST, request);
    }

    public Request getRequest() {
        return intent.getParcelableExtra(EXTRA_REQUEST);
    }

    public Intent asIntent() {
        return intent;
    }
}
