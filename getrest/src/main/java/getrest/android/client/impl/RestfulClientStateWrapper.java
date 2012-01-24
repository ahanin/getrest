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
package getrest.android.client.impl;

import android.os.Bundle;

public class RestfulClientStateWrapper {

    private static final String UNFINISHED_REQUEST_IDS = "getrest.android.client.UNFINISHED_REQUEST_IDS";
    private Bundle stateBundle;

    public RestfulClientStateWrapper(final Bundle stateBundle) {
        this.stateBundle = stateBundle;
    }

    public String[] getUnfinishedRequestIds() {
        return stateBundle.getStringArray(UNFINISHED_REQUEST_IDS);
    }

    public void setUnfinishedRequestIds(String[] requestIds) {
        stateBundle.putStringArray(UNFINISHED_REQUEST_IDS, requestIds);
    }

}
