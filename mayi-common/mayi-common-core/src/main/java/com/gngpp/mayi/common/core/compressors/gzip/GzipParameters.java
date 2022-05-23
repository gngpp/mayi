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

package com.gngpp.mayi.common.core.compressors.gzip;

import java.io.OutputStream;
import java.util.zip.Deflater;

/**
 * @author ant
 * Create by Ant on 2021/7/31 8:56 PM
 */
public class GzipParameters {

    private int compressionLevel = Deflater.DEFAULT_COMPRESSION;
    private long modificationTime;
    private String filename;
    private String comment;
    private int operatingSystem = 255; // Unknown OS by default
    private int bufferSize = 512;

    public int getCompressionLevel() {
        return compressionLevel;
    }

    /**
     * Sets the compression level.
     *
     * @param compressionLevel the compression level (between 0 and 9)
     * @see Deflater#NO_COMPRESSION
     * @see Deflater#BEST_SPEED
     * @see Deflater#DEFAULT_COMPRESSION
     * @see Deflater#BEST_COMPRESSION
     */
    public void setCompressionLevel(final int compressionLevel) {
        if (compressionLevel < -1 || compressionLevel > 9) {
            throw new IllegalArgumentException("Invalid gzip compression level: " + compressionLevel);
        }
        this.compressionLevel = compressionLevel;
    }

    public long getModificationTime() {
        return modificationTime;
    }

    /**
     * Sets the modification time of the compressed file.
     *
     * @param modificationTime the modification time, in milliseconds
     */
    public void setModificationTime(final long modificationTime) {
        this.modificationTime = modificationTime;
    }

    public String getFilename() {
        return filename;
    }

    /**
     * Sets the name of the compressed file.
     *
     * @param fileName the name of the file without the directory path
     */
    public void setFilename(final String fileName) {
        this.filename = fileName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }

    public int getOperatingSystem() {
        return operatingSystem;
    }

    /**
     * Sets the operating system on which the compression took place.
     * The defined values are:
     * <ul>
     *   <li>0: FAT file system (MS-DOS, OS/2, NT/Win32)</li>
     *   <li>1: Amiga</li>
     *   <li>2: VMS (or OpenVMS)</li>
     *   <li>3: Unix</li>
     *   <li>4: VM/CMS</li>
     *   <li>5: Atari TOS</li>
     *   <li>6: HPFS file system (OS/2, NT)</li>
     *   <li>7: Macintosh</li>
     *   <li>8: Z-System</li>
     *   <li>9: CP/M</li>
     *   <li>10: TOPS-20</li>
     *   <li>11: NTFS file system (NT)</li>
     *   <li>12: QDOS</li>
     *   <li>13: Acorn RISCOS</li>
     *   <li>255: Unknown</li>
     * </ul>
     *
     * @param operatingSystem the code of the operating system
     */
    public void setOperatingSystem(final int operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    /**
     * Gets size of the buffer used to retrieve compressed data.
     *
     * @return The size of the buffer used to retrieve compressed data.
     * @see #setBufferSize(int)
     * @since 1.21
     */
    public int getBufferSize() {
        return this.bufferSize;
    }

    /**
     * Sets size of the buffer used to retrieve compressed data from
     * {@link Deflater} and write to underlying {@link OutputStream}.
     *
     * @param bufferSize the bufferSize to set. Must be a positive value.
     * @since 1.21
     */
    public void setBufferSize(int bufferSize) {
        if (bufferSize <= 0) {
            throw new IllegalArgumentException("invalid buffer size: " + bufferSize);
        }
        this.bufferSize = bufferSize;
    }
}
