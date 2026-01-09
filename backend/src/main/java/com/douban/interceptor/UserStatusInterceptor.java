package com.douban.interceptor;

import com.douban.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class UserStatusInterceptor implements HandlerInterceptor {

    private final UserMapper userMapper;

    public UserStatusInterceptor(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        Object userIdAttr = request.getAttribute("userId");
        if (!(userIdAttr instanceof Long userId)) {
            return true;
        }

        String status = userMapper.findStatusById(userId);
        if (status != null && "ACTIVE".equalsIgnoreCase(status)) {
            return true;
        }

        // 允许被封禁用户访问公开的电影浏览（GET），避免因为本地 token 而无法浏览公开内容
        String uri = request.getRequestURI();
        if (uri != null && uri.startsWith("/api/movies") && "GET".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"error\":\"账号已被停用\"}");
        return false;
    }
}

