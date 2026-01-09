package com.douban.config;

import com.douban.interceptor.AuthInterceptor;
import com.douban.interceptor.AdminInterceptor;
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

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 强制认证的拦截器 - 用于需要登录的接口
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/auth/**",
                        "/api/movies/**",
                        "/api/home/**",
                        "/api/ws/**"
                );

        // 可选认证的拦截器 - 用于电影相关接口（解析token但不强制登录）
        AuthInterceptor optionalAuthInterceptor = new AuthInterceptor(false);
        optionalAuthInterceptor.setJwtUtil(jwtUtil);
        registry.addInterceptor(optionalAuthInterceptor)
                .addPathPatterns("/api/movies/**");

        // 管理员权限拦截
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/api/admin/**");
    }
}
