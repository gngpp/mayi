package com.zf1976.mayi.auth.service;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author mac
 * 2021/11/12 星期五 5:03 下午
 */
public class OAuth2ClientService {

    private static final Pattern ID_SECRET_PATTERN = Pattern.compile("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{10,20}$");
    private static final List<String> autoApproveScope = Arrays.asList("false", "true", "read", "write");
    private static final String SCOPE = "all";
    private final int tokenMinTime = 3600;
    private final int tokenRefreshMinTime = 7200;
    private final int tokenMaxTime = 2678400;
    private final int tokenRefreshMaxTime = 5356800;
}
