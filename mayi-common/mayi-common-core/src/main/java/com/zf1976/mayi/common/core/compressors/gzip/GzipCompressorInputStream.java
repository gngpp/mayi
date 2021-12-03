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

import com.zf1976.mayi.common.core.compressors.CompressorInputStream;
import com.zf1976.mayi.common.core.compressors.utils.CountingInputStream;
import com.zf1976.mayi.common.core.compressors.utils.InputStreamStatistics;
import com.zf1976.mayi.common.core.util.ByteUtils;
import com.zf1976.mayi.common.core.util.IOUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * @author ant
 * Create by Ant on 2021/7/31 9:01 PM
 */
@SuppressWarnings("all")
public class GzipCompressorInputStream extends CompressorInputStream
        implements InputStreamStatistics {

    // Header flags
    // private static final int FTEXT = 0x01; // Uninteresting for us
    private static final int FHCRC = 0x02;
    private static final int FEXTRA = 0x04;
    private static final int FNAME = 0x08;
    private static final int FCOMMENT = 0x10;
    private static final int FRESERVED = 0xE0;

    private final CountingInputStream countingStream;

    // Compressed input stream, possibly wrapped in a
    // BufferedInputStream, always wrapped in countingStream above
    private final InputStream in;

    // True if decompressing multi member streams.
    private final boolean decompressConcatenated;

    // Buffer to hold the input data
    private final byte[] buf = new byte[8192];
    // CRC32 from uncompressed data
    private final CRC32 crc = new CRC32();
    // used in no-arg read method
    private final byte[] oneByte = new byte[1];
    private final GzipParameters parameters = new GzipParameters();
    // Amount of data in buf.
    private int bufUsed;
    // Decompressor
    private Inflater inf = new Inflater(true);
    // True once everything has been decompressed
    private boolean endReached;

    /**
     * Constructs a new input stream that decompresses gzip-compressed data
     * from the specified input stream.
     * <p>
     * This is equivalent to
     * <code>GzipCompressorInputStream(inputStream, false)</code> and thus
     * will not decompress concatenated .gz files.
     *
     * @param inputStream the InputStream from which this object should
     *                    be created of
     * @throws IOException if the stream could not be created
     */
    public GzipCompressorInputStream(final InputStream inputStream)
            throws IOException {
        this(inputStream, false);
    }

    /**
     * Constructs a new input stream that decompresses gzip-compressed data
     * from the specified input stream.
     * <p>
     * If <code>decompressConcatenated</code> is {@code false}:
     * This decompressor might read more input than it will actually use.
     * If <code>inputStream</code> supports <code>mark</code> and
     * <code>reset</code>, then the input position will be adjusted
     * so that it is right after the last byte of the compressed stream.
     * If <code>mark</code> isn't supported, the input position will be
     * undefined.
     *
     * @param inputStream            the InputStream from which this object should
     *                               be created of
     * @param decompressConcatenated if true, decompress until the end of the input;
     *                               if false, stop after the first .gz member
     * @throws IOException if the stream could not be created
     */
    public GzipCompressorInputStream(final InputStream inputStream,
                                     final boolean decompressConcatenated)
            throws IOException {
        countingStream = new CountingInputStream(inputStream);
        // Mark support is strictly needed for concatenated files only,
        // but it's simpler if it is always available.
        if (countingStream.markSupported()) {
            in = countingStream;
        } else {
            in = new BufferedInputStream(countingStream);
        }

        this.decompressConcatenated = decompressConcatenated;
        init(true);
    }

    private static byte[] readToNull(final DataInput inData) throws IOException {
        try (final ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            int b = 0;
            // NOPMD NOSONAR
            while ((b = inData.readUnsignedByte()) != 0x00) {
                bos.write(b);
            }
            return bos.toByteArray();
        }
    }

    /**
     * Checks if the signature matches what is expected for a .gz file.
     *
     * @param signature the bytes to check
     * @param length    the number of bytes to check
     * @return true if this is a .gz stream, false otherwise
     * @since 1.1
     */
    public static boolean matches(final byte[] signature, final int length) {
        return length >= 2 && signature[0] == 31 && signature[1] == -117;
    }

    /**
     * Provides the stream's meta data - may change with each stream
     * when decompressing concatenated streams.
     *
     * @return the stream's meta data
     * @since 1.8
     */
    public GzipParameters getMetaData() {
        return parameters;
    }

    private boolean init(final boolean isFirstMember) throws IOException {
        assert isFirstMember || decompressConcatenated;

        // Check the magic bytes without a possibility of EOFException.
        final int magic0 = in.read();

        // If end of input was reached after decompressing at least
        // one .gz member, we have reached the end of the file successfully.
        if (magic0 == -1 && !isFirstMember) {
            return false;
        }

        if (magic0 != 31 || in.read() != 139) {
            throw new IOException(isFirstMember
                    ? "Input is not in the .gz format"
                    : "Garbage after a valid .gz stream");
        }

        // Parsing the rest of the header may throw EOFException.
        final DataInput inData = new DataInputStream(in);
        final int method = inData.readUnsignedByte();
        if (method != Deflater.DEFLATED) {
            throw new IOException("Unsupported compression method "
                    + method + " in the .gz header");
        }

        final int flg = inData.readUnsignedByte();
        if ((flg & FRESERVED) != 0) {
            throw new IOException(
                    "Reserved flags are set in the .gz header");
        }

        parameters.setModificationTime(ByteUtils.fromLittleEndian(inData, 4) * 1000);
        switch (inData.readUnsignedByte()) { // extra flags
            case 2:
                parameters.setCompressionLevel(Deflater.BEST_COMPRESSION);
                break;
            case 4:
                parameters.setCompressionLevel(Deflater.BEST_SPEED);
                break;
            default:
                // ignored for now
                break;
        }
        parameters.setOperatingSystem(inData.readUnsignedByte());

        // Extra field, ignored
        if ((flg & FEXTRA) != 0) {
            int xlen = inData.readUnsignedByte();
            xlen |= inData.readUnsignedByte() << 8;

            // This isn't as efficient as calling in.skip would be,
            // but it's lazier to handle unexpected end of input this way.
            // Most files don't have an extra field anyway.
            while (xlen-- > 0) {
                inData.readUnsignedByte();
            }
        }

        // Original file name
        if ((flg & FNAME) != 0) {
            parameters.setFilename(new String(readToNull(inData),
                    StandardCharsets.ISO_8859_1));
        }

        // Comment
        if ((flg & FCOMMENT) != 0) {
            parameters.setComment(new String(readToNull(inData),
                    StandardCharsets.ISO_8859_1));
        }

        // Header "CRC16" which is actually a truncated CRC32 (which isn't
        // as good as real CRC16). I don't know if any encoder implementation
        // sets this, so it's not worth trying to verify it. GNU gzip 1.4
        // doesn't support this field, but zlib seems to be able to at least
        // skip over it.
        if ((flg & FHCRC) != 0) {
            inData.readShort();
        }

        // Reset
        inf.reset();
        crc.reset();

        return true;
    }

    @Override
    public int read() throws IOException {
        return read(oneByte, 0, 1) == -1 ? -1 : oneByte[0] & 0xFF;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.1
     */
    @Override
    public int read(final byte[] b, int off, int len) throws IOException {
        if (len == 0) {
            return 0;
        }
        if (endReached) {
            return -1;
        }

        int size = 0;

        while (len > 0) {
            if (inf.needsInput()) {
                // Remember the current position because we may need to
                // rewind after reading too much input.
                in.mark(buf.length);

                bufUsed = in.read(buf);
                if (bufUsed == -1) {
                    throw new EOFException();
                }

                inf.setInput(buf, 0, bufUsed);
            }

            final int ret;
            try {
                ret = inf.inflate(b, off, len);
            } catch (final DataFormatException e) {
                // NOSONAR
                throw new IOException("Gzip-compressed data is corrupt");
            }

            crc.update(b, off, ret);
            off += ret;
            len -= ret;
            size += ret;
            count(ret);

            if (inf.finished()) {
                // We may have read too many bytes. Rewind the read
                // position to match the actual amount used.
                in.reset();

                final int skipAmount = bufUsed - inf.getRemaining();
                if (IOUtil.skip(in, skipAmount) != skipAmount) {
                    throw new IOException();
                }

                bufUsed = 0;

                final DataInput inData = new DataInputStream(in);

                // CRC32
                final long crcStored = ByteUtils.fromLittleEndian(inData, 4);

                if (crcStored != crc.getValue()) {
                    throw new IOException("Gzip-compressed data is corrupt "
                            + "(CRC32 error)");
                }

                // Uncompressed size modulo 2^32 (ISIZE in the spec)
                final long isize = ByteUtils.fromLittleEndian(inData, 4);

                if (isize != (inf.getBytesWritten() & 0xffffffffL)) {
                    throw new IOException("Gzip-compressed data is corrupt"
                            + "(uncompressed size mismatch)");
                }

                // See if this is the end of the file.
                if (!decompressConcatenated || !init(false)) {
                    inf.end();
                    inf = null;
                    endReached = true;
                    return size == 0 ? -1 : size;
                }
            }
        }

        return size;
    }

    /**
     * Closes the input stream (unless it is System.in).
     *
     * @since 1.2
     */
    @Override
    public void close() throws IOException {
        if (inf != null) {
            inf.end();
            inf = null;
        }

        if (this.in != System.in) {
            this.in.close();
        }
    }

    /**
     * @since 1.17
     */
    @Override
    public long getCompressedCount() {
        return countingStream.getBytesRead();
    }
}
