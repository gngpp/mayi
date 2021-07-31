package com.zf1976.mayi.common.core.compressors;

import java.io.InputStream;

/**
 * @author ant
 * Create by Ant on 2021/7/31 8:58 PM
 */
public abstract class CompressorInputStream extends InputStream {
    private long bytesRead;

    /**
     * Increments the counter of already read bytes.
     * Doesn't increment if the EOF has been hit (read == -1)
     *
     * @param read the number of bytes read
     * @since 1.1
     */
    protected void count(final int read) {
        count((long) read);
    }

    /**
     * Increments the counter of already read bytes.
     * Doesn't increment if the EOF has been hit (read == -1)
     *
     * @param read the number of bytes read
     */
    protected void count(final long read) {
        if (read != -1) {
            bytesRead = bytesRead + read;
        }
    }

    /**
     * Decrements the counter of already read bytes.
     *
     * @param pushedBack the number of bytes pushed back.
     * @since 1.7
     */
    protected void pushedBackBytes(final long pushedBack) {
        bytesRead -= pushedBack;
    }

    /**
     * Returns the current number of bytes read from this stream.
     *
     * @return the number of read bytes
     * @deprecated this method may yield wrong results for large
     * archives, use #getBytesRead instead
     */
    @Deprecated
    public int getCount() {
        return (int) bytesRead;
    }

    /**
     * Returns the current number of bytes read from this stream.
     *
     * @return the number of read bytes
     * @since 1.1
     */
    public long getBytesRead() {
        return bytesRead;
    }

    public long getUncompressedCount() {
        return getBytesRead();
    }
}
