package com.zf1976.ant.auth.controller;

import com.zf1976.mayi.auth.service.DynamicDataSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Map;

/**
 * @author mac
 * @date 2021/4/10
 */
@RestController
@RequestMapping(value = "/oauth")
public class TestEndpoint {

    @Autowired
    private DynamicDataSourceService dynamicDataSourceService;

    @GetMapping("/test")
    public String test() {
        return "";
    }

    @GetMapping("/test2")
    public Map<String, Collection<String>> demo() {
        return this.dynamicDataSourceService.loadDynamicDataSource();
    }

}