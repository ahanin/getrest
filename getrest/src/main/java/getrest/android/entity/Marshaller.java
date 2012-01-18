/**
 * Copyright
 */
package getrest.android.entity;

/**
 * @author aha
 * @since 2012-01-17
 */
public interface Marshaller<F, T> {

    T marshall(F source);

}
