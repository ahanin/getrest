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

/**
 * Non thread safe.
 */
public class Error {

    private ErrorState errorState;
    private String message;

    public void setErrorState(final ErrorState errorState) {
        this.errorState = errorState;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public ErrorState getErrorState() {
        return errorState;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Error{" +
                "errorState=" + errorState +
                ", message='" + message + '\'' +
                '}';
    }
}
