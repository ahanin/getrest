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
package getrest.android.resource.impl;

import android.content.ContentValues;
import getrest.android.resource.Marshaller;
import getrest.android.service.Representation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MarshallerImpl implements Marshaller<Object, Representation> {

    public Representation marshal(final Object source) {
        return new Representation() {
            public InputStream getContent() throws IOException {
                return new ByteArrayInputStream(new byte[0]);
            }
        };
    }

    public Object unmarshal(final Representation entity) {
        return new ContentValues();
    }
}