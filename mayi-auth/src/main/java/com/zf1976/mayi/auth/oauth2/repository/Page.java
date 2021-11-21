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

package com.zf1976.mayi.auth.oauth2.repository;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties
public final class Page<T> implements Serializable {

    /**
     * 每页最大数
     */
    public static final int MAX_SIZE = 1000;

    /**
     * 每页最小数
     */
    public static final int MIN_SIZE = 5;

    /**
     * 最小页
     */
    public static final int MIN_PAGE = 1;

    private int totalPage;

    private int totalRecord;

    private int page;

    private int size;

    List<T> records;

    public Page() {
        this(MIN_PAGE, MIN_SIZE);
    }


    public Page(int page, int size) {
        this.page = page;
        this.size = size;
    }

    public Page(PageBuilder<T> recordBuilder) {
        this.totalPage = recordBuilder.totalPage;
        this.totalRecord = recordBuilder.totalRecord;
        this.page = recordBuilder.page;
        this.size = recordBuilder.size;
        this.records = recordBuilder.records;
    }

    public int getTotalRecord() {
        return totalRecord;
    }

    public int getPage() {
        return Math.max(this.page, MIN_PAGE);
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return this.size <= 0? MIN_SIZE : Math.min(this.size, MAX_SIZE);
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<T> getRecords() {
        return records;
    }

    public Page<T> setRecords(List<T> records) {
        this.records.addAll(records);
        return this;
    }

    public Page<T> setTotalRecord(int totalRecord) {
        this.totalRecord = totalRecord;
        return this;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public Page<T> setTotalPage(int totalPage) {
        this.totalPage = totalPage;
        return this;
    }

    public static <T> PageBuilder<T> newBuilder() {
        return new PageBuilder<>();
    }

    public static <T> Page<T> from(Page<?> page) {
        return Page.<T>newBuilder()
                .page(page.getPage())
                .size(page.getSize())
                .totalRecord(page.getTotalRecord())
                .totalPage(page.getTotalPage())
                .records(new ArrayList<>())
                .build();
    }

    @Override
    public String toString() {
        return "Page{" +
                "totalPage=" + totalPage +
                ", totalRecord=" + totalRecord +
                ", page=" + page +
                ", size=" + size +
                ", records=" + records +
                '}';
    }

    public static class PageBuilder<T> {

        private int totalPage;

        private int totalRecord;

        private int page;

        private int size;

        private List<T> records;

        public PageBuilder<T> totalPage(int totalPage) {
            this.totalPage = totalPage;
            return this;
        }

        public PageBuilder<T> totalRecord(int totalRecord){
            this.totalRecord = totalRecord;
            return this;
        }

        public PageBuilder<T> page(int page) {
            this.page = page;
            return this;
        }

        public PageBuilder<T> size(int size) {
            this.size = size;
            return this;
        }

        public PageBuilder<T> records(List<T> t) {
            this.records = t;
            return this;
        }

        public Page<T> build() {
            return new Page<>(this);
        }

    }
}

