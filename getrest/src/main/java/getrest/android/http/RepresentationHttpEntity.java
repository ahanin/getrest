/**
 * Copyright
 */
package getrest.android.http;

import getrest.android.service.Representation;
import org.apache.http.HttpEntity;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author aha
 * @since 2012-01-17
 */
class RepresentationHttpEntity implements Representation {

    private HttpEntity httpEntity;

    public RepresentationHttpEntity(final HttpEntity httpEntity) {
        this.httpEntity = httpEntity;
    }

    public InputStream getContent() throws IOException {
        return httpEntity.getContent();
    }
}
