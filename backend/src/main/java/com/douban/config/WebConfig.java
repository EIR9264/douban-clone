package com.douban.config;

import com.douban.interceptor.AdminInterceptor;
import com.douban.interceptor.AuthInterceptor;
import com.douban.interceptor.UserStatusInterceptor;
import com.douban.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AdminInterceptor adminInterceptor;

    @Autowired
    private UserStatusInterceptor userStatusInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 强制认证：用于必须登录的接口
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/auth/**",
                        "/api/movies/**",
                        "/api/reviews/**",
                        "/api/home/**",
                        "/api/ws/**",
                        "/api/announcements/**"
                );

        // 可选认证：解析 token 但不强制登录（用于电影浏览页）
        AuthInterceptor optionalAuthInterceptor = new AuthInterceptor(false);
        optionalAuthInterceptor.setJwtUtil(jwtUtil);
        registry.addInterceptor(optionalAuthInterceptor)
                .addPathPatterns("/api/movies/**", "/api/reviews/**");

        // 账号状态：封禁/停用用户禁止执行需要登录的操作（仍允许 GET /api/movies/** 浏览公开内容）
        registry.addInterceptor(userStatusInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/**", "/api/ws/**", "/api/announcements/**");

        // 管理员权限
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/api/admin/**");
    }
}
