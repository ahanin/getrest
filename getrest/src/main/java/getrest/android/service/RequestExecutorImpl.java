/**
 * Copyright
 */
package getrest.android.service;

import getrest.android.request.Request;
import getrest.android.request.Response;

/**
 * @author aha
 * @since 2012-01-18
 */
public class RequestExecutorImpl implements RequestExecutor {

    private Request request;

    public RequestExecutorImpl(final Request request) {
        this.request = request;
    }

    public Response execute() {
        // TODO finish implementation
        throw new UnsupportedOperationException("request execution is yet to implement");
    }
}
