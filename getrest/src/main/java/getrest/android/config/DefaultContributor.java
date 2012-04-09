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
package getrest.android.config;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;
import getrest.android.entity.Marshaller;
import getrest.android.entity.Pack;
import getrest.android.entity.Packer;
import getrest.android.executor.RequestHandler;
import getrest.android.executor.RequestHandlerFactory;
import getrest.android.executor.RequestHandlerImpl;
import getrest.android.http.HttpServiceRequestExecutor;
import getrest.android.request.Request;
import getrest.android.request.RequestContext;
import getrest.android.request.RequestLifecycle;
import getrest.android.request.RequestManager;
import getrest.android.request.RequestState;
import getrest.android.request.Response;
import getrest.android.resource.ResourceContext;
import getrest.android.service.Representation;
import getrest.android.util.Logger;
import getrest.android.util.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class DefaultContributor implements ResourceContextContributor {

    private final Config config;

    public DefaultContributor(Config config) {
        this.config = config;
    }

    public void contribute(final ResourceContextContribution contribution) {
        contribution.setPacker(new ParcelablePacker());
        contribution.setMarshaller(new TempMarshallerImpl());
        contribution.setRequestHandlerFactory(new HttpRequestHandlerFactory(config));
        contribution.setRequestManager(new TempRequestManager());
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

    private static class TempMarshallerImpl implements Marshaller<ContentValues, Representation> {
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

    private static class HttpRequestHandlerFactory implements RequestHandlerFactory {

        private static final HttpServiceRequestExecutor HTTP_EXECUTOR = new HttpServiceRequestExecutor();

        private final Config config;

        public HttpRequestHandlerFactory(final Config config) {
            this.config = config;
        }

        public RequestHandler getRequestHandler(final Request request) {
            final RequestContext requestContext = getRequestContext(request);

            final DefaultRequestLifecycle requestLifecycle = new DefaultRequestLifecycle();
            requestLifecycle.setRequestContext(requestContext);

            final RequestHandlerImpl requestHandler = new RequestHandlerImpl();
            requestHandler.setRequestContext(requestContext);
            requestHandler.setRequestLifecycle(requestLifecycle);
            requestHandler.setServiceRequestExecutor(HTTP_EXECUTOR);

            return requestHandler;
        }

        private RequestContext getRequestContext(final Request request) {
            final ResourceContext resourceContext = config.getResourceContext(request.getUri());
            return resourceContext.getRequestContext(request);
        }
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

    private class TempRequestManager implements RequestManager {

        public void saveRequest(final Request request) {
//            // TODO implement persisting of Request
//            throw new UnsupportedOperationException();
        }

        public Request getRequest(final String requestId) {
            // TODO implement loading of Request
            throw new UnsupportedOperationException();
        }

        public void saveResponse(final Response response) {
            // TODO implement saving of Request
            throw new UnsupportedOperationException();
        }

        public Response getResponse(final String requestId) {
            // TODO implement loading of Response
            throw new UnsupportedOperationException();
        }

        public void setRequestState(final String requestId, final RequestState state) {
//            // TODO implement change of RequestState
//            throw new UnsupportedOperationException();
        }
    }
}
