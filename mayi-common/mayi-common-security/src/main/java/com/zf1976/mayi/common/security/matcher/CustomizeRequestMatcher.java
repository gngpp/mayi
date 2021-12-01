package com.zf1976.mayi.common.security.matcher;

import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;

/**
 * @author mac
 * 2021/12/1 星期三 8:12 PM
 */
public class CustomizeRequestMatcher implements RequestMatcher {

    private static final String MATCH_ALL = "/**";

    private final CustomizeRequestMatcher.Matcher matcher;

    private final String pattern;

    private final HttpMethod httpMethod;

    private final boolean caseSensitive;


    /**
     * Creates a matcher with the specific pattern which will match all HTTP methods in a
     * case sensitive manner.
     * @param pattern the ant pattern to use for matching
     */
    public CustomizeRequestMatcher(String pattern) {
        this(pattern, null);
    }

    /**
     * Creates a matcher with the supplied pattern and HTTP method in a case sensitive
     * manner.
     * @param pattern the ant pattern to use for matching
     * @param httpMethod the HTTP method. The {@code matches} method will return false if
     * the incoming request doesn't have the same method.
     */
    public CustomizeRequestMatcher(String pattern, String httpMethod) {
        this(pattern, httpMethod, true);
    }

    /**
     * Creates a matcher with the supplied pattern which will match the specified Http
     * method
     * @param pattern the ant pattern to use for matching
     * @param httpMethod the HTTP method. The {@code matches} method will return false if
     * the incoming request doesn't doesn't have the same method.
     * @param caseSensitive true if the matcher should consider case, else false
     */
    public CustomizeRequestMatcher(String pattern, String httpMethod, boolean caseSensitive) {
        Assert.hasText(pattern, "Pattern cannot be null or empty");
        this.caseSensitive = caseSensitive;
        if (pattern.equals(MATCH_ALL) || pattern.equals("**")) {
            pattern = MATCH_ALL;
            this.matcher = null;
        }
        else {
            // If the pattern ends with {@code /**} and has no other wildcards or path
            // variables, then optimize to a sub-path match
            if (pattern.endsWith(MATCH_ALL)
                    && (pattern.indexOf('?') == -1 && pattern.indexOf('{') == -1 && pattern.indexOf('}') == -1)
                    && pattern.indexOf("*") == pattern.length() - 2) {
                this.matcher = new CustomizeRequestMatcher.SubpathMatcher(pattern.substring(0, pattern.length() - 3), caseSensitive);
            }
            else {
                this.matcher = new CustomizeRequestMatcher.SpringAntMatcher(pattern, caseSensitive);
            }
        }
        this.pattern = pattern;
        this.httpMethod = StringUtils.hasText(httpMethod) ? HttpMethod.valueOf(httpMethod) : null;
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        if (this.httpMethod != null && StringUtils.hasText(request.getMethod())
                && this.httpMethod != HttpMethod.resolve(request.getMethod())) {
            return false;
        }
        if (this.pattern.equals(MATCH_ALL)) {
            return true;
        }
        String url = getRequestPath(request);
        return this.matcher.matches(url);
    }

    @Override
    public MatchResult matcher(HttpServletRequest request) {
        if (!matches(request)) {
            return MatchResult.notMatch();
        }
        if (this.matcher == null) {
            return MatchResult.match();
        }
        String url = getRequestPath(request);
        return MatchResult.match(this.matcher.extractUriTemplateVariables(url));
    }

    private String getRequestPath(HttpServletRequest request) {
        String url = request.getServletPath();
        String pathInfo = request.getPathInfo();
        if (pathInfo != null) {
            url = StringUtils.hasLength(url) ? url + pathInfo : pathInfo;
        }
        return url;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CustomizeRequestMatcher other)) {
            return false;
        }
        return this.pattern.equals(other.pattern) && this.httpMethod == other.httpMethod
                && this.caseSensitive == other.caseSensitive;
    }

    @Override
    public int hashCode() {
        int result = (this.pattern != null) ? this.pattern.hashCode() : 0;
        result = 31 * result + ((this.httpMethod != null) ? this.httpMethod.hashCode() : 0);
        result = 31 * result + (this.caseSensitive ? 1231 : 1237);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Ant [pattern='").append(this.pattern).append("'");
        if (this.httpMethod != null) {
            sb.append(", ").append(this.httpMethod);
        }
        sb.append("]");
        return sb.toString();
    }

    private static final class SpringAntMatcher implements CustomizeRequestMatcher.Matcher {

        private final AntPathMatcher antMatcher;

        private final String pattern;

        private SpringAntMatcher(String pattern, boolean caseSensitive) {
            this.pattern = pattern;
            this.antMatcher = createMatcher(caseSensitive);
        }

        @Override
        public boolean matches(String path) {
            return this.antMatcher.match(this.pattern, path);
        }

        @Override
        public Map<String, String> extractUriTemplateVariables(String path) {
            return this.antMatcher.extractUriTemplateVariables(this.pattern, path);
        }

        private static AntPathMatcher createMatcher(boolean caseSensitive) {
            AntPathMatcher matcher = new AntPathMatcher();
            matcher.setTrimTokens(false);
            matcher.setCaseSensitive(caseSensitive);
            return matcher;
        }

    }

    private interface Matcher {

        boolean matches(String path);

        Map<String, String> extractUriTemplateVariables(String path);

    }

    /**
     * Optimized matcher for trailing wildcards
     */
    @SuppressWarnings("SpellCheckingInspection")
    private static final class SubpathMatcher implements CustomizeRequestMatcher.Matcher {

        private final String subpath;

        private final int length;

        private final boolean caseSensitive;

        private SubpathMatcher(String subpath, boolean caseSensitive) {
            Assert.isTrue(!subpath.contains("*"), "subpath cannot contain \"*\"");
            this.subpath = caseSensitive ? subpath : subpath.toLowerCase();
            this.length = subpath.length();
            this.caseSensitive = caseSensitive;
        }

        @Override
        public boolean matches(String path) {
            if (!this.caseSensitive) {
                path = path.toLowerCase();
            }
            return path.startsWith(this.subpath) && (path.length() == this.length || path.charAt(this.length) == '/');
        }

        @Override
        public Map<String, String> extractUriTemplateVariables(String path) {
            return Collections.emptyMap();
        }

    }
}
