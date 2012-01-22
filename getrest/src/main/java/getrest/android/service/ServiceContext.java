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

import android.content.ContentValues;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import getrest.android.entity.Marshaller;
import getrest.android.entity.Pack;
import getrest.android.entity.Packer;
import getrest.android.executor.RequestExecutor;
import getrest.android.executor.RequestExecutorImpl;
import getrest.android.http.HttpServiceRequestExecutor;
import getrest.android.request.Method;
import getrest.android.request.Request;
import getrest.android.request.RequestContext;
import getrest.android.request.RequestLifecycle;
import getrest.android.resource.ResourceContext;
import getrest.android.resource.ResourceContextImpl;
import getrest.android.util.Logger;
import getrest.android.util.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author aha
 * @since 2012-01-16
 */
public abstract class ServiceContext {

    // temporary solution
    // TODO resolve ResourceContext based on Request attributes and service configuration
    private final ResourceContextImpl resourceContextSingleton = new ResourceContextImpl();

    {
        resourceContextSingleton.setPacker(new ParcelablePacker());
        resourceContextSingleton.setMarshaller(new SimpleMarshallerImpl());
        resourceContextSingleton.setServiceRequestExecutor(new HttpServiceRequestExecutor());
    }


    public ResourceContext getResourceContext(final Uri uri, final Method method) {
        // TODO resolve ResourceContext based on URI and request method
        return resourceContextSingleton;
    }

    /**
     * Return {@link RequestContext} associated with given {@link Request}. For each particular resource will return
     * the same instance of {@link RequestContext}.
     *
     * @param request {@link Request} object
     * @return associated {@link RequestContext}
     */
    public ResourceContext getResourceContext(Request request) {
        // TODO resolve ResourceContext based on request attributes
        return resourceContextSingleton;
    }

    public RequestExecutor getRequestExecutor(final Request request) {
        final ResourceContext resourceContext = getResourceContext(request);
        final RequestContext requestContext = resourceContext.getRequestContext(request);

        final DefaultRequestLifecycle requestLifecycle = new DefaultRequestLifecycle();
        requestLifecycle.setRequestContext(requestContext);

        final RequestExecutorImpl requestExecutor = new RequestExecutorImpl();
        requestExecutor.setRequest(request);
        requestExecutor.setRequestContext(requestContext);
        requestExecutor.setRequestLifecycle(requestLifecycle);
        requestExecutor.setServiceRequestExecutor(resourceContext.getServiceRequestExecutor());

        return requestExecutor;
    }

    /**
     * Return {@link ServiceContext} associated with given {@link Request}.
     *
     * @param request {@link Request} instance
     * @return associated {@link ServiceContext}
     */
    public static ServiceContext forRequest(final Request request) {
        // TODO return ServiceContext based on request attributes
        return new ServiceContextImpl();
    }

    public static ServiceContext forServiceId(final String serviceId) {
        // TODO return ServiceContext based on service id
        return new ServiceContextImpl();
    }

    /**
     * This is a temporary solution.
     * TODO remove when "real" class is implemented
     */
    private static class DefaultRequestLifecycle implements RequestLifecycle {

        private static final Logger LOGGER = LoggerFactory.getLogger("getrest.service");

        private RequestContext requestContext;

        public void setRequestContext(final RequestContext requestContext) {
            this.requestContext = requestContext;
        }

        public void beforeMarshal() {
            LOGGER.trace("before marshal");
        }

        public void afterMarshal() {
            LOGGER.trace("after marshal");
        }

        public void beforeUnmarshal() {
            LOGGER.trace("before unmarshal");
        }

        public void afterUnmarshal() {
            LOGGER.trace("after unmarshal");
        }

    }

    private static class ServiceContextImpl extends ServiceContext {
    }

    private static class ParcelablePack implements Pack<Parcelable> {

        private final Parcelable parcelable;

        public ParcelablePack(final Parcelable parcelable) {
            this.parcelable = parcelable;
        }

        public Parcelable unpack() {
            return parcelable;
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(final Parcel parcel, final int flags) {
            parcel.writeString(parcelable.getClass().getName());
            parcel.writeParcelable(parcelable, 0);
        }

        public static final Creator<ParcelablePack> CREATOR = new Creator<ParcelablePack>() {
            public ParcelablePack createFromParcel(final Parcel parcel) {
                final String className = parcel.readString();

                final Class<?> parcelableClass;
                try {
                    parcelableClass = Class.forName(className);
                } catch (ClassNotFoundException e) {
                    throw new IllegalStateException("Cannot unpack non-parcelable entity");
                }

                final Parcelable entity = parcel.readParcelable(parcelableClass.getClassLoader());

                return new ParcelablePack(entity);
            }

            public ParcelablePack[] newArray(final int size) {
                return new ParcelablePack[size];
            }
        };

    }

    private static class ParcelablePacker implements Packer {

        public ParcelablePack pack(final Object object) {
            if (object instanceof Parcelable) {
                return new ParcelablePack((Parcelable) object);
            } else {
                throw new IllegalArgumentException("Only Parcelable is supported for packing");
            }
        }

    }

    private static class SimpleMarshallerImpl implements Marshaller<ContentValues, Representation> {
        public Representation marshal(final ContentValues source) {
            return new Representation() {
                public InputStream getContent() throws IOException {
                    return new ByteArrayInputStream(new byte[0]);
                }
            };
        }

        public ContentValues unmarshal(final Representation entity) {
            return new ContentValues();
        }
    }
}
