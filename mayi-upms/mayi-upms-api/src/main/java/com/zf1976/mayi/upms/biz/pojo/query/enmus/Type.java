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

package com.zf1976.mayi.upms.biz.pojo.query.enmus;

import java.io.Serializable;

/**
 * @author mac
 * @date 2020/10/26 11:47 下午
 */
public enum Type implements Serializable {

    /**
     * eq
     */
    EQ,

    /**
     * ne
     */
    NE,

    /**
     * like
     */
    LIKE,

    /**
     * not like
     */
    NOT_LIKE,

    /**
     * in
     */
    IN,

    /**
     * between
     */
    BETWEEN,

    /**
     * not null
     */
    NOT_NULL,

    /**
     * is null
     */
    IS_NULL,

    /**
     *  ASC
     */
    ASC,
    /**
     * DESC
     */
    DESC
}
