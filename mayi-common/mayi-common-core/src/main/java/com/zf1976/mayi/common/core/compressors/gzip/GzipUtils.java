/*
 *
 *  * Copyright (c) 2021 zf1976
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in all
 *  * copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING COMMUNICATION_AUTHORIZATION,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  * SOFTWARE.
 *  *
 *
 */

package com.zf1976.mayi.common.core.compressors.gzip;

import com.zf1976.mayi.common.core.compressors.FileNameUtil;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * gzip 压缩格式的实用程序代码。
 *
 * @author ant
 * Create by Ant on 2021/7/31 9:24 PM
 */
@SuppressWarnings("all")
public class GzipUtils {

    private static final FileNameUtil fileNameUtil;

    static {
        // using LinkedHashMap so .tgz is preferred over .taz as
        // compressed extension of .tar as FileNameUtil will use the
        // first one found
        final Map<String, String> uncompressSuffix =
                new LinkedHashMap<>();
        uncompressSuffix.put(".tgz", ".tar");
        uncompressSuffix.put(".taz", ".tar");
        uncompressSuffix.put(".svgz", ".svg");
        uncompressSuffix.put(".cpgz", ".cpio");
        uncompressSuffix.put(".wmz", ".wmf");
        uncompressSuffix.put(".emz", ".emf");
        uncompressSuffix.put(".gz", "");
        uncompressSuffix.put(".z", "");
        uncompressSuffix.put("-gz", "");
        uncompressSuffix.put("-z", "");
        uncompressSuffix.put("_z", "");
        fileNameUtil = new FileNameUtil(uncompressSuffix, ".gz");
    }

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private GzipUtils() {
    }

    /**
     * Detects common gzip suffixes in the given file name.
     *
     * @param fileName name of a file
     * @return {@code true} if the file name has a common gzip suffix,
     * {@code false} otherwise
     */
    public static boolean isCompressedFilename(final String fileName) {
        return fileNameUtil.isCompressedFilename(fileName);
    }

    /**
     * Maps the given name of a gzip-compressed file to the name that the
     * file should have after uncompression. Commonly used file type specific
     * suffixes like ".tgz" or ".svgz" are automatically detected and
     * correctly mapped. For example the name "package.tgz" is mapped to
     * "package.tar". And any file names with the generic ".gz" suffix
     * (or any other generic gzip suffix) is mapped to a name without that
     * suffix. If no gzip suffix is detected, then the file name is returned
     * unmapped.
     *
     * @param fileName name of a file
     * @return name of the corresponding uncompressed file
     */
    public static String getUncompressedFilename(final String fileName) {
        return fileNameUtil.getUncompressedFilename(fileName);
    }

    /**
     * Maps the given file name to the name that the file should have after
     * compression with gzip. Common file types with custom suffixes for
     * compressed versions are automatically detected and correctly mapped.
     * For example the name "package.tar" is mapped to "package.tgz". If no
     * custom mapping is applicable, then the default ".gz" suffix is appended
     * to the file name.
     *
     * @param fileName name of a file
     * @return name of the corresponding compressed file
     */
    public static String getCompressedFilename(final String fileName) {
        return fileNameUtil.getCompressedFilename(fileName);
    }

}
