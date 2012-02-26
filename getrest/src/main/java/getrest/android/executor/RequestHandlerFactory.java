/**
 * Copyright
 */
package getrest.android.executor;

import getrest.android.request.Request;

public interface RequestHandlerFactory {
    RequestHandler getRequestHandler(Request request);
}
