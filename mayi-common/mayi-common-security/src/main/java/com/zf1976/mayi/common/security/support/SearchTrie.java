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

package com.zf1976.mayi.common.security.support;

import java.io.Serializable;
import java.util.List;

/**
 * @author mac
 * @date 2021/5/5
 */
public interface SearchTrie extends Serializable {

    /**
     * 新增字符序列
     *
     * @param sequence 字符序列
     */
    void putTrie(CharSequence sequence);

    /**
     * 新增字符序列数组
     *
     * @param charSequences 序列数组
     */
    void putTrie(CharSequence[] charSequences);

    /**
     * 新增字典数组
     *
     * @param charArray 字符串数组
     */
    void putTrie(String[] charArray);

    /**
     * 新增字符序列列表
     *
     * @param charSequenceList 序列列表
     */
    void putTrie(List<CharSequence> charSequenceList);

    /**
     * 查找字符序列
     *
     * @param charSequence 字符序列
     */
    boolean searchTrie(CharSequence charSequence);

    /**
     * 查找字符序列数组
     *
     * @param charSequences 字符序列数组
     * @return {@link boolean}
     */
    boolean searchTrie(CharSequence[] charSequences);

    /**
     * 查找字符串数组
     *
     * @param charArray 字符串数组
     * @return {@link boolean}
     */
    boolean searchTrie(String[] charArray);

    /**
     * 查找字符序列列表
     *
     * @param charSequenceList 字符序列列表
     * @return {@link boolean}
     */
    boolean searchTrie(List<CharSequence> charSequenceList);

}
