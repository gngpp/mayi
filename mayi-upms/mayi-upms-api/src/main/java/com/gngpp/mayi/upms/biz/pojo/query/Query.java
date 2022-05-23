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

package com.gngpp.mayi.upms.biz.pojo.query;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gngpp.mayi.common.core.util.StringUtil;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * @author mac
 * @date 2020/10/22 9:11 下午
 */
public class Query<T extends AbstractQueryParam> implements Serializable {

    private static final long serialVersionUID = -8899444130507801280L;
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

    /**
     * 页
     */
    private int page;

    /**
     * 每页数
     */
    private int size;

    /**
     * 排序
     */
    private List<String> sort;

    /**
     * 排序条目
     */
    private List<OrderItem> orders;

    /**
     * 查询参数
     */
    private T query;

    public Query() {
        this(MIN_PAGE, MIN_SIZE);
    }

    public Query(int page, int size) {
        this.page = page;
        this.size = size;
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

    public List<String> getSort() {
        return sort;
    }

    public void setSort(List<String> sort) {
        this.sort = sort;
        this.setOrders(sort);
    }

    public List<OrderItem> getOrders() {
        return this.orders;
    }

    public void setOrders(List<String> sort) {
        this.orders = parser(sort);
    }

    public T getQuery() {
        return query;
    }

    public void setQuery(T query) {
        this.query = query;
    }

    public boolean orderIsEmpty() {
        return ObjectUtils.isEmpty(this.getOrders());
    }

    public <S> Page<S> toPage() {
        return new Page<>(this.getPage(), this.getSize());
    }

    private List<OrderItem> parser(List<String> sort) {
        orders = new ArrayList<>();
        sort.forEach(str -> {
            String[] array = StringUtils.commaDelimitedListToStringArray(str);
            Direction direction = Direction.fromString(array[1]);
            if (direction.isDescending()) {
                this.orders.add(OrderItem.desc(StringUtil.camelToUnderline(array[0])));
            } else if (direction.isAscending()) {
                this.orders.add(OrderItem.asc(StringUtil.camelToUnderline(array[0])));
            }
        });
        return orders;
    }

    public enum Direction {

        /**
         * 升序
         */
        ASC,

        /**
         * 降序
         */
        DESC;


        Direction() {}

        public boolean isAscending() {
            return this.equals(ASC);
        }

        public boolean isDescending() {
            return this.equals(DESC);
        }

        public static Direction fromString(String value) {
            try {
                return valueOf(value.toUpperCase(Locale.US));
            } catch (Exception var) {
                throw new IllegalArgumentException(String.format("Invalid value '%s' for orders given! Has to be either 'desc' or 'asc' (case insensitive).", value), var);
            }
        }

        public static Optional<Direction> fromOptionalString(String value) {
            try {
                return Optional.of(fromString(value));
            } catch (IllegalArgumentException var2) {
                return Optional.empty();
            }
        }
    }

    @Override
    public String toString() {
        return "Query{" +
                "page=" + page +
                ", size=" + size +
                ", sort=" + sort +
                ", orders=" + orders +
                ", query=" + query +
                '}';
    }
}
