package test;

import com.zf1976.mayi.auth.enums.AuthenticationType;

/**
 * @author mac
 * 2021/11/17 星期三 1:17 下午
 */
public class Test {

    public static void main(String[] args) {
        System.out.println(AuthenticationType.ALL.matchType(11111));
        System.out.println(AuthenticationType.ALL);
    }
}
