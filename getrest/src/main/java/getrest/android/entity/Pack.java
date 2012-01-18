/**
 * Copyright
 */
package getrest.android.entity;

import android.os.Parcelable;

/**
 * {@link Parcelable} interface for sending and receiving objects.
 *
 * @author aha
 * @since 2012-01-17
 */
public interface Pack<T> extends Parcelable {

    T unpack();

}
