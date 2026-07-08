package com.capsule.interceptor;

import com.capsule.common.BaseContext;
import com.capsule.exception.BusinessException;
import com.capsule.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 放行 OPTIONS 预检请求（CORS跨域）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 从请求头获取 Authorization Token
        String token = request.getHeader("Authorization");
        if (!StringUtils.hasText(token)) {
            throw new BusinessException(401, "未检测到身份令牌，请先登录");
        }

        // 兼容 Bearer 前缀格式
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            Claims claims = JwtUtils.parseToken(token);
            Long userId = Long.parseLong(claims.getSubject());
            // 将解析出的UserId存入当前线程上下文
            BaseContext.setCurrentId(userId);
            return true;
        } catch (Exception e) {
            throw new BusinessException(401, "身份令牌无效或已过期，请重新登录");
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 请求结束时必须移除，防止内存泄漏
        BaseContext.removeCurrentId();
    }
}
