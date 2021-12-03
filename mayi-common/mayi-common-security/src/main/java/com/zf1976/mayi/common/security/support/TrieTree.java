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

/**
 * 字典树(前缀字典树)
 *
 * @author mac
 * @date 2021/5/5
 */
public class TrieTree {

    private static final char TRIE_A = 'a';

    /**
     * 子节点数组
     */
    private final TrieTree[] children;

    /**
     * 是否为某字符串结尾字符
     */
    private boolean isEndingChar;

    public TrieTree() {
        this.children = new TrieTree[26];
        this.isEndingChar = false;
    }

    /**
     * 插入路径
     *
     * @param path 路径
     */
    public void insert(String path) {
        TrieTree node = this;
        for (int i = 0; i < path.length(); i++) {
            char ch = path.charAt(i);
            int index = ch - TRIE_A;
            if (node.children[index] == null) {
                node.children[index] = new TrieTree();
            }
            node = node.children[index];
        }
        node.isEndingChar = true;
    }

    /**
     * 查找路径
     *
     * @date 2021-05-05 23:12:36
     * @param path path
     * @return {@link boolean}
     */
    public boolean search(String path) {
        TrieTree node = searchPrefix(path);
        return node != null && node.isEndingChar;
    }

    /**
     * 查找以某前缀开头
     *
     * @param prefix 前缀
     * @return {@link boolean}
     */
    public boolean startsWith(String prefix) {
        return searchPrefix(prefix) != null;
    }

    /**
     * 查找前缀
     *
     * @param prefix 前缀
     * @return {@link TrieTree}
     */
    private TrieTree searchPrefix(String prefix) {
        TrieTree node = this;
        for (int i = 0; i < prefix.length(); i++) {
            char ch = prefix.charAt(i);
            int index = ch - TRIE_A;
            // 有一个字符找不到返回
            if (node.children[index] == null) {
                return null;
            }
            node = node.children[index];
        }
        return node;
    }
}
