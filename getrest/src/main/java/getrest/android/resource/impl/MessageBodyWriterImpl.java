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

import getrest.android.core.Headers;
import getrest.android.core.MediaType;

import getrest.android.ext.MessageBodyWriter;

import getrest.android.service.Representation;

import java.io.OutputStream;

public class MessageBodyWriterImpl implements MessageBodyWriter<Representation> {

    public void write(final Object source, final MediaType mediaType,
        final Headers headers, final OutputStream outputStream) {
        // noop
    }

    public Object unmarshal(final Representation entity) {
        return new ContentValues();
    }
}
