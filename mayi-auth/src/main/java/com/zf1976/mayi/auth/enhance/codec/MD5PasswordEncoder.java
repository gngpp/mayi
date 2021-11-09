package com.zf1976.mayi.auth.enhance.codec;

import com.zf1976.mayi.common.encrypt.MD5Encoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.ObjectUtils;

@SuppressWarnings("all")
public class MD5PasswordEncoder implements PasswordEncoder {

    private final MD5Encoder encoder = new MD5Encoder();

    @Override
    public String encode(CharSequence charSequence) {
        return encoder.encode(charSequence);
    }

    /**
     * 匹配
     *
     * @param rawPassword raw
     * @param encodedPassword encode
     * @return
     */
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        String encode = this.encode(rawPassword);
        return ObjectUtils.nullSafeEquals(encodedPassword, encode);
    }

}
