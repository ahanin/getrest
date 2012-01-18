/**
 * Copyright
 */
package getrest.android.http;

import getrest.android.service.Representation;
import org.apache.http.entity.AbstractHttpEntity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
* @author aha
* @since 2012-01-17
*/
class HttpEntityRepresentation extends AbstractHttpEntity {

    private final Representation entity;

    public HttpEntityRepresentation(final Representation entity) {
        this.entity = entity;
    }

    public boolean isRepeatable() {
        return false;
    }

    public long getContentLength() {
        return -1;
    }

    public InputStream getContent() throws IOException, IllegalStateException {
        return entity.getContent();
    }

    public void writeTo(final OutputStream outputStream) throws IOException {
        final InputStream content = entity.getContent();
        final byte[] buff = new byte[8192];
        int bytesRead;
        try {
            while ((bytesRead = content.read(buff)) > 0) {
                outputStream.write(buff, 0, bytesRead);
            }
        } finally {
            content.close();
        }
    }

    public boolean isStreaming() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
