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

import android.content.Context;

import android.os.Parcel;
import android.os.Parcelable;

import getrest.android.client.InMemoryRequestManager;

import getrest.android.config.Config;
import getrest.android.config.HasConfig;

import getrest.android.http.Packer;

import getrest.android.util.Preconditions;

import java.util.HashMap;

public class GetrestRuntime {

    private static final HashMap<Context, GetrestRuntime> cache = new HashMap<Context, GetrestRuntime>(
        1);
    private static final DefaultPacker DEFAULT_PACKER = new DefaultPacker();
    private RequestManager requestManager = new InMemoryRequestManager();
    private Config config;

    public static GetrestRuntime getInstance(final Context context) {

        final Context applicationContext = context.getApplicationContext();

        if (!cache.containsKey(applicationContext)) {

            synchronized (cache) {

                if (!cache.containsKey(applicationContext)) {

                    if (!(applicationContext instanceof HasConfig)) {
                        throw new IllegalStateException(
                            "Unable to find configuration. Does your Android "
                            + Application.class.getSimpleName() + " implement "
                            + HasConfig.class.getName());
                    }

                    synchronized (applicationContext) {
                        cache.put(applicationContext,
                                  new GetrestRuntime(
                            ((HasConfig) applicationContext).getGetrestConfig()));
                    }
                }
            }
        }

        return cache.get(applicationContext);
    }

    private GetrestRuntime(final Config config) {
        this.config = config;
    }

    public Packer getPacker() {

        // TODO implement resolving of context-specific packer
        return DEFAULT_PACKER;
    }

    public RequestManager getRequestManager() {

        return requestManager;
    }

    public <T extends Request> RequestSupport<T> getRequestSupport(final T request) {

        for (final Application application : config.getApplications()) {

            if (application.isRequestSupported(request)) {

                return new RequestSupportDecorator<T>(request,
                                                      application.getRequestSupport(request));
            }
        }

        throw new IllegalStateException("Request is not supported: " + request);
    }

    private static class DefaultPacker implements Packer {
        public <T> Pack<T> pack(final T object) {
            Preconditions.checkArgNotNull(object, "entity");
            // TODO support non-parcelable entities with different packers
            Preconditions.checkState(object instanceof Parcelable, "entity must be parcelable");

            return (Pack<T>) new ParcelablePack((Parcelable) object);
        }

        private static class ParcelablePack implements Pack<Parcelable> {

            private Parcelable entity;

            public ParcelablePack(final Parcelable entity) {
                this.entity = entity;
            }

            public Parcelable unpack() {

                return entity;
            }

            public int describeContents() {

                return 0;
            }

            public void writeToParcel(final Parcel parcel, final int flags) {
                parcel.writeParcelable(entity, 0);
            }

            public static final Creator<ParcelablePack> CREATOR = new Creator<ParcelablePack>() {
                public ParcelablePack createFromParcel(final Parcel parcel) {

                    return new ParcelablePack(
                        parcel.readParcelable(GetrestRuntime.class.getClassLoader()));
                }

                public ParcelablePack[] newArray(final int size) {

                    return new ParcelablePack[size];
                }
            };
        }
    }
}