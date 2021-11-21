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

package com.zf1976.mayi.common.datasource.config;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author lengleng
 * @date 2019-06-24
 * <p>
 * 解决Mybatis Plus Order By SQL注入问题
 */
public class SqlFilterArgumentResolver implements HandlerMethodArgumentResolver {

	private final static String[] KEYWORDS = { "master", "truncate", "insert", "select", "delete", "update", "declare",
			"alter", "drop", "sleep" };

	/**
	 * 判断Controller是否包含page 参数
	 * @param parameter 参数
	 * @return 是否过滤
	 */
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.getParameterType().equals(Page.class);
	}

	/**
	 * @param parameter 入参集合
	 * @param mavContainer model 和 view
	 * @param webRequest web相关
	 * @param binderFactory 入参解析
	 * @return 检查后新的page对象
	 * <p>
	 * page 只支持查询 GET .如需解析POST获取请求报文体处理
	 */
	@Override
	public Object resolveArgument(@Nullable MethodParameter parameter, ModelAndViewContainer mavContainer,
								  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

		HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
		Assert.notNull(request, "request cannot been null");
		String[] ascArray = request.getParameterValues("ascs");
		String[] descArray = request.getParameterValues("descs");
		String current = request.getParameter("current");
		String size = request.getParameter("size");

		Page<?> page = new Page<>();
		if (StringUtils.isEmpty(current)) {
			page.setCurrent(Long.parseLong(current));
		}

		if (StringUtils.isEmpty(size)) {
			page.setSize(Long.parseLong(size));
		}

		List<OrderItem> orderItemList = new ArrayList<>();
		Optional.ofNullable(ascArray).ifPresent(s -> orderItemList.addAll(
				Arrays.stream(s).filter(sqlInjectPredicate()).map(OrderItem::asc).collect(Collectors.toList())));
		Optional.ofNullable(descArray).ifPresent(s -> orderItemList.addAll(
				Arrays.stream(s).filter(sqlInjectPredicate()).map(OrderItem::desc).collect(Collectors.toList())));
		page.addOrder(orderItemList);

		return page;
	}

	/**
	 * 判断用户输入里面有没有关键字
	 * @return Predicate
	 */
	private Predicate<String> sqlInjectPredicate() {
		return sql -> {
			for (String keyword : KEYWORDS) {
				if (containsIgnoreCase(sql, keyword)) {
					return false;
				}
			}
			return true;
		};
	}

	public static boolean containsIgnoreCase(CharSequence str, CharSequence testStr) {
		if (null == str) {
			return null == testStr;
		} else {
			return str.toString().toLowerCase().contains(testStr.toString().toLowerCase());
		}
	}

}
