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

package com.zf1976.mayi.upms.biz.controller;

import com.zf1976.mayi.upms.biz.annotation.Log;
import com.zf1976.mayi.upms.biz.dao.SysUserDao;
import com.zf1976.mayi.upms.biz.security.service.DynamicDataSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

/**
 * @author mac
 * @date 2020/12/24
 **/
@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private DynamicDataSourceService dynamicDataSourceService;

    @Autowired
    private SysUserDao sysUserDao;

    @Log(description = "测试A接口")
    @RequestMapping(method = RequestMethod.GET, path = "/demo")
    public void testA(@RequestParam String description) {
        throw new RuntimeException(this.getClass().getName());
    }

    @Log(description = "测试B接口")
    @GetMapping("/{demo}")
    public String testB(@PathVariable String demo) {
        return demo;
    }

    @Log(description = "测试SQL")
    @PostMapping("/sql")
    public Object testSql() {
        return sysUserDao.selectOneByUsername("admin");
    }

    @GetMapping("/path")
    public String testPath(@RequestParam String uri) {
        final var pathMatcher = new AntPathMatcher();
        // URI-Method
        Map<String, String> methodMap = this.dynamicDataSourceService.loadResourceMethodMap();
        // 条件
        Set<Map.Entry<String, String>> entrySet = methodMap.entrySet();

        // 匹配资源方法
        for (Map.Entry<String, String> entry : entrySet) {
            // eq匹配请求方法
            if (ObjectUtils.nullSafeEquals(entry.getKey(), uri)) {
                return entry.getKey();
            } else {
                // 模式匹配
                if (pathMatcher.match(entry.getKey(), uri)) {
                    return entry.getKey();
                }
            }
        }
        return "not matcher";
    }
}
