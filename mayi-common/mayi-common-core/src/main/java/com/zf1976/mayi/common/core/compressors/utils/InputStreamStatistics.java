package com.zf1976.mayi.common.core.compressors.utils;

/**
 * This interface provides statistics on the current decompression stream.
 * The stream consumer can use that statistics to handle abnormal
 * compression ratios, i.e. to prevent zip bombs.
 *
 * @author ant
 * Create by Ant on 2021/7/31 9:03 PM
 */

public interface InputStreamStatistics {
    /**
     * @return the amount of raw or compressed bytes read by the stream
     */
    long getCompressedCount();

    /**
     * @return the amount of decompressed bytes returned by the stream
     */
    long getUncompressedCount();
}
