/*
 * Copyright (c) 2021 zf1976
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.zf1976.mayi.common.security.support;

import com.google.common.base.Splitter;

import java.util.List;

/**
 * 资源查找树（基于字典树）
 *
 * @author mac
 * @date 2021/5/5
 */
public class ResourceTrie implements SearchTrie {

    public static final char SEPARATOR = '/';
    private final TrieTree resourceTrie = new TrieTree();


    public static void main(String[] args) {
        final ResourceTrie resourceTrie = new ResourceTrie();
        resourceTrie.putTrie("/api/test/a");
        resourceTrie.putTrie("/api/test/b");
        System.out.println(resourceTrie.searchTrie("/api/test/a"));
        System.out.println(resourceTrie.searchTrie("/api/test/c"));
    }

    @Override
    public void putTrie(CharSequence sequence) {
        final Iterable<String> iterable = Splitter.on(SEPARATOR)
                                               .trimResults()
                                               .omitEmptyStrings()
                                               .split(sequence);
        for (String path : iterable) {
            this.resourceTrie.insert(path);
        }
    }

    @Override
    public void putTrie(CharSequence[] charSequences) {
        for (CharSequence charSequence : charSequences) {
            this.putTrie(charSequence);
        }
    }

    @Override
    public void putTrie(String[] charArray) {
        for (String str : charArray) {
            this.putTrie(str);
        }
    }

    @Override
    public void putTrie(List<CharSequence> charSequenceList) {
        for (CharSequence charSequence : charSequenceList) {
            this.putTrie(charSequence);
        }
    }

    @Override
    public boolean searchTrie(CharSequence charSequence) {
        final Iterable<String> iterable = Splitter.on(SEPARATOR)
                                               .trimResults()
                                               .omitEmptyStrings()
                                               .split(charSequence);
        for (String path : iterable) {
            if (!this.resourceTrie.search(path)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean searchTrie(CharSequence[] charSequences) {
        for (CharSequence charSequence : charSequences) {
            if (!this.searchTrie(charSequence)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean searchTrie(String[] charArray) {
        for (String str : charArray) {
            if (!this.searchTrie(str)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean searchTrie(List<CharSequence> charSequenceList) {
        for (CharSequence charSequence : charSequenceList) {
            if (!this.searchTrie(charSequence)) {
                return false;
            }
        }
        return true;
    }

}
