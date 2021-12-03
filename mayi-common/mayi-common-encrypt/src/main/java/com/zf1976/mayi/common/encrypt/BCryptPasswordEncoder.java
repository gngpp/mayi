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

package com.zf1976.mayi.common.encrypt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.security.SecureRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ant
 * Create by Ant on 2021/3/23 3:40 PM
 */
@SuppressWarnings("all")
public class BCryptPasswordEncoder {
    private final Pattern pattern;
    private final Log logger;
    private final int strength;
    private final BCryptPasswordEncoder.BCryptVersion version;
    private final SecureRandom random;

    public BCryptPasswordEncoder() {
        this(-1);
    }

    public BCryptPasswordEncoder(int strength) {
        this(strength, (SecureRandom)null);
    }

    public BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion version) {
        this(version, (SecureRandom)null);
    }

    public BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion version, SecureRandom random) {
        this(version, -1, random);
    }

    public BCryptPasswordEncoder(int strength, SecureRandom random) {
        this(BCryptPasswordEncoder.BCryptVersion.$2A, strength, random);
    }

    public BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion version, int strength) {
        this(version, strength, (SecureRandom)null);
    }

    public BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion version, int strength, SecureRandom random) {
        this.pattern = Pattern.compile("\\A\\$2(a|y|b)?\\$(\\d\\d)\\$[./0-9A-Za-z]{53}");
        this.logger = LogFactory.getLog(this.getClass());
        if (strength == -1 || strength >= 4 && strength <= 31) {
            this.version = version;
            this.strength = strength == -1 ? 10 : strength;
            this.random = random;
        } else {
            throw new IllegalArgumentException("Bad strength");
        }
    }

    public String encode(CharSequence rawPassword) {
        if (rawPassword == null) {
            throw new IllegalArgumentException("rawPassword cannot be null");
        } else {
            String salt;
            if (this.random != null) {
                salt = BCrypt.gensalt(this.version.getVersion(), this.strength, this.random);
            } else {
                salt = BCrypt.gensalt(this.version.getVersion(), this.strength);
            }

            return BCrypt.hashpw(rawPassword.toString(), salt);
        }
    }

    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (rawPassword == null) {
            throw new IllegalArgumentException("rawPassword cannot be null");
        } else if (encodedPassword != null && encodedPassword.length() != 0) {
            if (!this.pattern.matcher(encodedPassword).matches()) {
                this.logger.warn("Encoded password does not look like BCrypt");
                return false;
            } else {
                return BCrypt.checkpw(rawPassword.toString(), encodedPassword);
            }
        } else {
            this.logger.warn("Empty encoded password");
            return false;
        }
    }

    public boolean upgradeEncoding(String encodedPassword) {
        if (encodedPassword != null && encodedPassword.length() != 0) {
            Matcher matcher = this.pattern.matcher(encodedPassword);
            if (!matcher.matches()) {
                throw new IllegalArgumentException("Encoded password does not look like BCrypt: " + encodedPassword);
            } else {
                int strength = Integer.parseInt(matcher.group(2));
                return strength < this.strength;
            }
        } else {
            this.logger.warn("Empty encoded password");
            return false;
        }
    }

    public static enum BCryptVersion {
        $2A("$2a"),
        $2Y("$2y"),
        $2B("$2b");

        private final String version;

        private BCryptVersion(String version) {
            this.version = version;
        }

        public String getVersion() {
            return this.version;
        }
    }
}
