package com.zf1976.mayi.auth.deprecate;


import com.zf1976.mayi.auth.LoginUserDetails;
import com.zf1976.mayi.auth.exception.IllegalAccessException;
import com.zf1976.mayi.common.core.util.SpringContextHolder;
import com.zf1976.mayi.common.security.enums.AuthenticationState;
import com.zf1976.mayi.common.security.property.SecurityProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author mac
 * Create by Ant on 2020/9/3 下午9:51
 */
@Deprecated
public class JwtTokenProvider {

    private static final SecurityProperties CONFIG;
    private static final JwtBuilder JWT_BUILDER;
    private static final JwtParser JWT_PARSER;
    public static final String DELIMITER = ",";
    public static final String ONE_BLANK_SPACE = " ";


    static {
        CONFIG = SpringContextHolder.getBean(SecurityProperties.class);
        byte[] keyByte = Decoders.BASE64.decode(CONFIG.getTokenBase64Secret());
        Key secretKey = Keys.hmacShaKeyFor(keyByte);
        JWT_PARSER = Jwts.parserBuilder()
                         .setSigningKey(secretKey)
                         .build();
        JWT_BUILDER = Jwts.builder()
                          .signWith(secretKey, SignatureAlgorithm.HS512);
    }

    private JwtTokenProvider() {}

    /**
     * 根据认证信息创建token
     * authorities只是临时数据，会变动
     *
     * @param authentication 认证
     * @return token
     */
    public static String createToken(Authentication authentication) {
        final LoginUserDetails userDetails = (LoginUserDetails) authentication.getDetails();
        final String uuid = (String) authentication.getCredentials();
        final String authorities = authentication.getAuthorities()
                                           .stream()
                                           .map(GrantedAuthority::getAuthority)
                                           .collect(Collectors.joining(DELIMITER));
        // 验证码uuid
        return JWT_BUILDER.setId(uuid)
                          .setIssuer(CONFIG.getTokenIssuer())
                          .claim(CONFIG.getTokenAuthoritiesKey(), authorities)
                          .setSubject(authentication.getName())
                          .setAudience(String.valueOf(userDetails.getId()))
                          .setExpiration(new Date(System.currentTimeMillis() + CONFIG.getTokenExpiredTime()))
                          .setIssuedAt(new Date())
                          .compact();
    }

    /**
     * 刷新token 到期时间 增加一小时
     *
     * @param token token
     * @param restoreTime restore time
     * @return new token
     */
    public static String refreshToken(String token,long restoreTime) {
        final Claims claims = getClaims(token);
        final long expiredTime = claims.getExpiration().getTime();
        return JWT_BUILDER.setClaims(claims)
                          .setExpiration(new Date(expiredTime + restoreTime))
                          .compact();
    }


    /**
     * 解析token授权信息
     *
     * @param token token
     * @return authentication
     */
    public static Authentication resolveAuthentication(String token) {
        Claims claims = null;
        try {
            // 这里token expired 过期解析会异常
            claims = getClaims(token);
        } catch (Exception e) {
            // 转为自定义异常 抛出处理
            throw new com.zf1976.mayi.auth.exception.ExpiredJwtException(AuthenticationState.EXPIRED_JWT, e);
        }
        String authorities = (String) claims.get(CONFIG.getTokenAuthoritiesKey());
        Set<SimpleGrantedAuthority> grantedAuthorities;
        if (!StringUtils.isEmpty(authorities)) {
            grantedAuthorities = Arrays.stream(authorities.split(","))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());

        }else {
            grantedAuthorities = null;
        }

        return new UsernamePasswordAuthenticationToken(claims.getSubject(), token, grantedAuthorities);
    }

    /**
     * 获取载荷
     *
     * @param token token
     * @return body
     */
    public static Claims getClaims(String token) {
        return JWT_PARSER.parseClaimsJws(token)
                         .getBody();
    }

    public static Long getSessionId(String token) {
        return Long.valueOf(getClaims(token).getAudience());
    }

    public static Long getSessionId(Claims claims) {
        return Long.valueOf(claims.getAudience());
    }

    /**
     * 验证token 是否过期
     *
     * @param token token
     * @return boolean
     */
    public static boolean validateExpired(String token) {
        Claims claims;
        try {
            // 这里token expired 过期解析会异常 或者非法token
            claims = getClaims(token);
        } catch (Exception e) {
            // 转为自定义异常 抛出处理
            throw new IllegalAccessException(AuthenticationState.ILLEGAL_ACCESS);
        }
        long expiredTime = claims.getExpiration().getTime();
        return System.currentTimeMillis() > expiredTime;
    }

    /**
     * 获取token 过期时间
     *
     * @param token token
     * @return expired time
     */
    public static long getTokenExpired(String token) {
        return getClaims(token).getExpiration()
                               .getTime();
    }

    /**
     * 获取请求头 token
     *
     * @param request 请求
     * @return token
     */
    public static String getRequestToken(HttpServletRequest request) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.isEmpty(token)) {
            if (token.startsWith(CONFIG.getPrefixToken() + ONE_BLANK_SPACE)) {
                return token.substring(7);
            }
        }
        return null;
    }
}
