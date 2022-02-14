package com.stonedt.intelligence.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * description: HttpContextUtils <br>
 * date: 2020/5/9 17:39 <br>
 * author: huajiancheng <br>
 * version: 1.0 <br>
 */
public class HttpContextUtils {
    public static HttpServletRequest getHttpServletRequest() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        return request;
    }
}
