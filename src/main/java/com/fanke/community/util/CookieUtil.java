package com.fanke.community.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName CookiedUtil
 * @Author Fanke
 * @Created 2021/9/25 0:25
 */
public class CookieUtil {
    /**
     * 获取指定cookie的value
     * @param request HTTP request
     * @param name cookie 的 name
     * @return
     */
    public static String getValue(HttpServletRequest request, String name) {
        if (request == null || name == null) {
            throw new IllegalArgumentException("参数为空!");
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

}
