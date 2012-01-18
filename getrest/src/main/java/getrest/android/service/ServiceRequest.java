/**
 * Copyright
 */
package getrest.android.service;

import android.net.Uri;
import getrest.android.request.Method;

/**
 * @author aha
 * @since 2012-01-17
 */
public class ServiceRequest {

    private String requestId;
    private Uri uri;
    private Method method;
    private Representation entity;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(final String requestId) {
        this.requestId = requestId;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(final Uri uri) {
        this.uri = uri;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(final Method method) {
        this.method = method;
    }

    public Representation getEntity() {
        return entity;
    }

    public void setEntity(final Representation entity) {
        this.entity = entity;
    }
}
