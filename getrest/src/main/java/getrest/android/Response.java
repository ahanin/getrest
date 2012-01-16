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
package getrest.android;

/**
 * @author aha
 * @since 2012-01-13
 */
public class Response {

    private Representation entity;

    private boolean isFailed;

    /**
     * Tells if communication error occurred during the request execution.
     *
     * @return
     */
    public boolean isFailed() {
        return isFailed;
    }

    /**
     * Set communication failure flag.
     *
     * @param isFailed {@code true} in case communication error occurred during the request execution,
     *                 otherwise {@code false}
     */
    public void setFailed(final boolean isFailed) {
        this.isFailed = isFailed;
    }

}
