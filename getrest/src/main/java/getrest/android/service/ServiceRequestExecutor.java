/**
 * Copyright
 */
package getrest.android.service;

/**
 * @author aha
 * @since 2012-01-17
 */
public interface ServiceRequestExecutor {
    void execute(final ServiceRequest request, final ServiceResponse response);
}
