/**
 * Copyright
 */
package getrest.android.service;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author aha
 * @since 2012-01-17
 */
public interface Representation {

    InputStream getContent() throws IOException;

}
