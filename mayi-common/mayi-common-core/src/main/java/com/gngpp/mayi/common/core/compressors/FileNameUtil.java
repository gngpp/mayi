/*
 *
 *  * Copyright (c) 2021 gngpp
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

package com.gngpp.mayi.common.core.compressors;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author ant
 * Create by Ant on 2021/7/31 9:23 PM
 */
public class FileNameUtil {

    /**
     * Map from common file name suffixes to the suffixes that identify compressed
     * versions of those file types. For example: from ".tar" to ".tgz".
     */
    private final Map<String, String> compressSuffix =
            new HashMap<>();

    /**
     * Map from common file name suffixes of compressed files to the
     * corresponding suffixes of uncompressed files. For example: from
     * ".tgz" to ".tar".
     * <p>
     * This map also contains format-specific suffixes like ".gz" and "-z".
     * These suffixes are mapped to the empty string, as they should simply
     * be removed from the file name when the file is uncompressed.
     */
    private final Map<String, String> uncompressSuffix;

    /**
     * Length of the longest compressed suffix.
     */
    private final int longestCompressedSuffix;

    /**
     * Length of the shortest compressed suffix.
     */
    private final int shortestCompressedSuffix;

    /**
     * Length of the longest uncompressed suffix.
     */
    private final int longestUncompressedSuffix;

    /**
     * Length of the shortest uncompressed suffix longer than the
     * empty string.
     */
    private final int shortestUncompressedSuffix;

    /**
     * The format's default extension.
     */
    private final String defaultExtension;

    /**
     * sets up the utility with a map of known compressed to
     * uncompressed suffix mappings and the default extension of the
     * format.
     *
     * @param uncompressSuffix Map from common file name suffixes of
     *                         compressed files to the corresponding suffixes of uncompressed
     *                         files. For example: from ".tgz" to ".tar".  This map also
     *                         contains format-specific suffixes like ".gz" and "-z".  These
     *                         suffixes are mapped to the empty string, as they should simply
     *                         be removed from the file name when the file is uncompressed.
     * @param defaultExtension the format's default extension like ".gz"
     */
    public FileNameUtil(final Map<String, String> uncompressSuffix,
                        final String defaultExtension) {
        this.uncompressSuffix = Collections.unmodifiableMap(uncompressSuffix);
        int lc = Integer.MIN_VALUE, sc = Integer.MAX_VALUE;
        int lu = Integer.MIN_VALUE, su = Integer.MAX_VALUE;
        for (final Map.Entry<String, String> ent : uncompressSuffix.entrySet()) {
            final int cl = ent.getKey()
                              .length();
            if (cl > lc) {
                lc = cl;
            }
            if (cl < sc) {
                sc = cl;
            }

            final String u = ent.getValue();
            final int ul = u.length();
            if (ul > 0) {
                if (!compressSuffix.containsKey(u)) {
                    compressSuffix.put(u, ent.getKey());
                }
                if (ul > lu) {
                    lu = ul;
                }
                if (ul < su) {
                    su = ul;
                }
            }
        }
        longestCompressedSuffix = lc;
        longestUncompressedSuffix = lu;
        shortestCompressedSuffix = sc;
        shortestUncompressedSuffix = su;
        this.defaultExtension = defaultExtension;
    }

    /**
     * Detects common format suffixes in the given file name.
     *
     * @param fileName name of a file
     * @return {@code true} if the file name has a common format suffix,
     * {@code false} otherwise
     */
    public boolean isCompressedFilename(final String fileName) {
        final String lower = fileName.toLowerCase(Locale.ENGLISH);
        final int n = lower.length();
        for (int i = shortestCompressedSuffix;
             i <= longestCompressedSuffix && i < n; i++) {
            if (uncompressSuffix.containsKey(lower.substring(n - i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Maps the given name of a compressed file to the name that the
     * file should have after uncompression. Commonly used file type specific
     * suffixes like ".tgz" or ".svgz" are automatically detected and
     * correctly mapped. For example the name "package.tgz" is mapped to
     * "package.tar". And any file names with the generic ".gz" suffix
     * (or any other generic gzip suffix) is mapped to a name without that
     * suffix. If no format suffix is detected, then the file name is returned
     * unmapped.
     *
     * @param fileName name of a file
     * @return name of the corresponding uncompressed file
     */
    public String getUncompressedFilename(final String fileName) {
        final String lower = fileName.toLowerCase(Locale.ENGLISH);
        final int n = lower.length();
        for (int i = shortestCompressedSuffix;
             i <= longestCompressedSuffix && i < n; i++) {
            final String suffix = uncompressSuffix.get(lower.substring(n - i));
            if (suffix != null) {
                return fileName.substring(0, n - i) + suffix;
            }
        }
        return fileName;
    }

    /**
     * Maps the given file name to the name that the file should have after
     * compression. Common file types with custom suffixes for
     * compressed versions are automatically detected and correctly mapped.
     * For example the name "package.tar" is mapped to "package.tgz". If no
     * custom mapping is applicable, then the default ".gz" suffix is appended
     * to the file name.
     *
     * @param fileName name of a file
     * @return name of the corresponding compressed file
     */
    public String getCompressedFilename(final String fileName) {
        final String lower = fileName.toLowerCase(Locale.ENGLISH);
        final int n = lower.length();
        for (int i = shortestUncompressedSuffix;
             i <= longestUncompressedSuffix && i < n; i++) {
            final String suffix = compressSuffix.get(lower.substring(n - i));
            if (suffix != null) {
                return fileName.substring(0, n - i) + suffix;
            }
        }
        // No custom suffix found, just append the default
        return fileName + defaultExtension;
    }
}
